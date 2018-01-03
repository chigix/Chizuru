package com.chigix.resserver;

import com.chigix.resserver.config.ApplicationContext;
import com.chigix.resserver.domain.error.DaoException;
import com.chigix.resserver.error.DaoExceptionHandler;
import com.chigix.resserver.error.ExceptionHandler;
import com.chigix.resserver.error.UnwrappedExceptionHandler;
import com.chigix.resserver.mybatis.EntityManagerImpl;
import com.chigix.resserver.interfaces.handling.http.HttpHeaderNames;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.router.FullResponseLengthFixer;
import io.netty.handler.codec.http.router.HttpException;
import io.netty.handler.codec.http.router.HttpRouted;
import io.netty.handler.codec.http.router.HttpRouter;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericXmlApplicationContext;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class.getName());

    public static ExecutorService amassedResourceFileReadingPool;

    public static void main(String[] args) throws InterruptedException {
        org.springframework.context.ApplicationContext springContext = new GenericXmlApplicationContext("appContext.xml");
        ApplicationContext app_ctx = springContext.getBean(ApplicationContext.class);
        LOG.info(app_ctx.getChizuruVersion());
        LOG.info("NODE ID: " + app_ctx.getCurrentNodeId());
        LOG.info("Created At: " + app_ctx.getCreationDate());
        // @TODO: support Capacity for fixed Thread pool configure from command line.
        amassedResourceFileReadingPool = Executors.newFixedThreadPool(10);
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap sb = new ServerBootstrap();
        sb.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                ByteBuf bytebuf = (ByteBuf) msg;
//                                System.out.println(bytebuf.toString(CharsetUtil.UTF_8));
                                super.channelRead(ctx, msg);
                            }

                        }).addLast(new HttpRequestDecoder(4096, 8192, 8192))
                                .addLast(new HttpResponseEncoder())
                                .addLast(new FullResponseLengthFixer())
                                .addLast(headerFixer(app_ctx), configRouter(app_ctx));
                    }
                });
        try {
            ChannelFuture future = sb.bind(9000).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static ChannelHandlerAdapter headerFixer(ApplicationContext application) {
        return new ChannelHandlerAdapter() {

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                if (msg instanceof HttpResponse) {
                    HttpResponse resp = (HttpResponse) msg;
                    HttpHeaders headers = resp.headers();
                    if (!headers.contains(HttpHeaderNames.CONNECTION)) {
                        resp.headers().set(HttpHeaderNames.CONNECTION, ctx.channel().isOpen() ? "open" : "close");
                    }
                    resp.headers().set(HttpHeaderNames.DATE, new DateTime(DateTimeZone.forID("GMT")).toString("E, dd MMM yyyy HH:mm:ss z", Locale.US));
                    resp.headers().set(HttpHeaderNames.SERVER, "Chizuru");
                }
                super.write(ctx, msg, promise);
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof HttpRequest) {
                    //@TODO Here may be performance problem, if the previous request 
                    // in this thread have also invoked ApplicationContext#finishRequest()
                    // Because it seems that H2Database Sql Session's commit and flush could only 
                    // have effect in the later sessions. 
                    // -- This problem may be solved automatically with the
                    //    support from newly involved Spring Framework.
                    ((EntityManagerImpl) application.getEntityManager()).close();
                    ((HttpRequest) msg).headers().add(application.getRequestIdHeaderName(), UUID.randomUUID().toString());
                }
                super.channelRead(ctx, msg);
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                if (cause instanceof IOException) {
                    if (ctx.channel().isOpen()) {
                        LOG.warn(cause.getMessage()
                                + ", Channel[" + ctx.channel().id() + "] "
                                + "is not closed.");
                    }
                    ctx.close();
                    return;
                }
                super.exceptionCaught(ctx, cause);
            }

        };
    }

    public static HttpRouter configRouter(ApplicationContext application) {
        return new HttpRouter() {
            @Override
            protected void initExceptionRouting(ChannelPipeline pipeline) {
                pipeline.addLast(new UnwrappedExceptionHandler() {
                    @Override
                    protected void handleDecoderException(ChannelHandlerContext ctx, HttpException httpexc) throws Exception {
                        handleUnwrappedException(ctx, new HttpException(httpexc.getCause().getCause()) {
                            @Override
                            public HttpRequest getHttpRequest() {
                                return httpexc.getHttpRequest();
                            }

                            @Override
                            public HttpRouted getHttpRouted() {
                                return httpexc.getHttpRouted();
                            }
                        });
                    }

                    @Override
                    protected void handleUnwrappedException(ChannelHandlerContext ctx, HttpException httpexc) throws Exception {
                        final Throwable innerException = httpexc.getCause();
                        // httpexc.getCause() --> NullPointerException
                        if (innerException instanceof DaoException) {
                            super.channelRead(ctx, new HttpException(innerException) {
                                @Override
                                public HttpRequest getHttpRequest() {
                                    return httpexc.getHttpRequest();
                                }

                                @Override
                                public HttpRouted getHttpRouted() {
                                    return httpexc.getHttpRouted();
                                }
                            });
                        } else {
                            super.handleUnwrappedException(ctx, httpexc);
                        }
                    }

                },
                        DaoExceptionHandler.getInstance(application),
                        ExceptionHandler.getInstance(application)
                );
            }

            @Override
            protected void initRoutings(ChannelHandlerContext ctx, HttpRouter router) {
                this.newRouting(ctx, new com.chigix.resserver.endpoint.GetService.Routing(application));
                this.newRouting(ctx, new com.chigix.resserver.endpoint.GetBucket.Routing(application));
                this.newRouting(ctx, new com.chigix.resserver.endpoint.HeadBucket.Routing(application));
                this.newRouting(ctx, new com.chigix.resserver.endpoint.PutBucket.Routing(application));
                this.newRouting(ctx, new com.chigix.resserver.endpoint.PostBucket.Routing(application));
                this.newRouting(ctx, new com.chigix.resserver.endpoint.DeleteBucket.Routing(application));
                this.newRouting(ctx, new com.chigix.resserver.endpoint.GetResource.Routing(application));
                this.newRouting(ctx, new com.chigix.resserver.endpoint.HeadResource.Routing(application));
                this.newRouting(ctx, new com.chigix.resserver.endpoint.PutResource.Routing(application));
                this.newRouting(ctx, new com.chigix.resserver.endpoint.PostResource.Routing(application));
                this.newRouting(ctx, new com.chigix.resserver.endpoint.DeleteResource.Routing(application));
            }

        };
    }

}
