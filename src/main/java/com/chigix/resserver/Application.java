package com.chigix.resserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.router.FullResponseLengthFixer;
import io.netty.handler.codec.http.router.HttpRouter;
import io.netty.util.CharsetUtil;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class.getName());

    public static void main(String[] args) throws InterruptedException {
        DB db = DBMaker.fileDB("./data/bankai.db").transactionEnable().closeOnJvmShutdown().make();
        ApplicationContext appctx = new ApplicationContext(new File("./data"), db);
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
                                // ByteBuf bytebuf = (ByteBuf) msg;
                                // System.out.println(bytebuf.toString(CharsetUtil.UTF_8));
                                super.channelRead(ctx, msg);
                            }

                        }).addLast(new HttpRequestDecoder())
                                .addLast(new HttpResponseEncoder())
                                .addLast(new FullResponseLengthFixer())
                                .addLast(headerFixer(), configRouter());
                    }
                });
        try {
            ChannelFuture future = sb.bind(9000).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            db.close();
        }
    }

    public static ChannelHandlerAdapter headerFixer() {
        return new ChannelHandlerAdapter() {

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                if (msg instanceof HttpResponse) {
                    HttpResponse resp = (HttpResponse) msg;
                    resp.headers().set(HttpHeaderNames.CONNECTION, ctx.channel().isOpen() ? "open" : "close");
                    resp.headers().set(HttpHeaderNames.DATE, new DateTime(DateTimeZone.forID("GMT")).toString("E, dd MMM yyyy HH:mm:ss z", Locale.US));
                    resp.headers().set(HttpHeaderNames.SERVER, "Chizuru");
                    // @TODO add request-id support, channelread might could be used in this case.
                }
                super.write(ctx, msg, promise);
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                super.channelRead(ctx, msg);
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                if (cause instanceof IOException) {
                    if (ctx.channel().isOpen()) {
                        LOG.warn(cause.getMessage() + ", Channel is not closed.");
                    }
                    ctx.close();
                    return;
                }
                super.exceptionCaught(ctx, cause);
            }

        };
    }

    public static HttpRouter configRouter() {
        return new HttpRouter() {

            @Override
            protected void initRoutings(ChannelHandlerContext ctx, HttpRouter router) {
                this.newRouting(ctx, new com.chigix.resserver.GetService.Routing());
            }

        };
    }

}
