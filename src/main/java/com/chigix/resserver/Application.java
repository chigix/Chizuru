package com.chigix.resserver;

import com.chigix.resserver.error.DaoExceptionHandler;
import com.chigix.resserver.error.ExceptionHandler;
import com.chigix.resserver.error.UnwrappedExceptionHandler;
import com.chigix.resserver.mybatis.ChizuruMapper;
import com.chigix.resserver.mybatis.dto.ApplicationContextDto;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
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
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.router.FullResponseLengthFixer;
import io.netty.handler.codec.http.router.HttpRouter;
import io.netty.util.CharsetUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class.getName());

    public static ExecutorService amassedResourceFileReadingPool;

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext app_ctx = initNode();
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

    public static SqlSessionFactory initMyBatis(String env) {
        InputStream in;
        try {
            in = Resources.getResourceAsStream("com/chigix/resserver/mybatis/mybatis-config.xml");
        } catch (IOException ex) {
            LOG.error("Unexpected.", ex);
            throw new RuntimeException("Unexpected.");
        }
        return new SqlSessionFactoryBuilder().build(in, env);
    }

    public static ApplicationContext initNode() {
        SqlSessionFactory session_factory = initMyBatis("chizuru");
        ApplicationContext result;
        try (SqlSession settings_session = session_factory.openSession(true)) {
            ChizuruMapper chizuru = settings_session.getMapper(ChizuruMapper.class);
            Map<String, String> settings = ChizuruMapper.SETTINGS_MAP.apply(chizuru.selectChizuruSettings());
            final Configuration config;
            if (settings.get("NODE_ID") == null) {
                config = new Configuration(UUID.randomUUID().toString());
            } else {
                config = new Configuration(settings.get("NODE_ID"));
            }
            config.setMainSession(session_factory);
            File uploadDbFile_1 = new File("./data/Uploading.mv.db");
            File uploadDbFile_2 = new File("./data/Uploading.trace.db");
            if (uploadDbFile_1.exists()) {
                uploadDbFile_1.delete();
            }
            if (uploadDbFile_2.exists()) {
                uploadDbFile_2.delete();
            }
            config.setUploadSession(initMyBatis("upload"));
            try (SqlSession upload_session = config.getUploadSession().openSession(true)) {
                upload_session.update("com.chigix.resserver.mybatis.MultipartUploadMapper.createUploadTable");
            }
            if (settings.get("CREATION_DATE") == null) {
                config.setCreationDate(new DateTime(DateTimeZone.forID("GMT")));
            } else {
                config.setCreationDate(DateTime.parse(settings.get("CREATION_DATE")));
            }
            if (settings.get("MAX_CHUNKSIZE") != null) {
                config.setMaxChunkSize(Integer.valueOf(settings.get("MAX_CHUNKSIZE")));
            }
            if (settings.get("TRANSFER_BUFFERSIZE") != null) {
                config.setTransferBufferSize(Integer.valueOf(settings.get("TRANSFER_BUFFERSIZE")));
            }
            File chunks_dir = new File("./data/chunks");
            if (!chunks_dir.exists()) {
                chunks_dir.mkdirs();
            }
            if (!chunks_dir.isDirectory()) {
                throw new RuntimeException("Unable to have chunks directory.");
            }
            config.setChunksDir(new File("./data/chunks"));
            result = new ApplicationContextBuilder().build(config);
            chizuru.updateChizuruSettings(new ApplicationContextDto(result));
        }
        return result;
    }

    public static ChannelHandlerAdapter headerFixer(ApplicationContext application) {
        return new ChannelHandlerAdapter() {

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                if (msg instanceof HttpResponse) {
                    HttpResponse resp = (HttpResponse) msg;
                    resp.headers().set(HttpHeaderNames.CONNECTION, ctx.channel().isOpen() ? "open" : "close");
                    resp.headers().set(HttpHeaderNames.DATE, new DateTime(DateTimeZone.forID("GMT")).toString("E, dd MMM yyyy HH:mm:ss z", Locale.US));
                    resp.headers().set(HttpHeaderNames.SERVER, "Chizuru");
                }
                super.write(ctx, msg, promise);
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof HttpRequest) {
                    ((HttpRequest) msg).headers().add(application.getRequestIdHeaderName(), UUID.randomUUID().toString());
                }
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

    public static HttpRouter configRouter(ApplicationContext application) {
        return new HttpRouter() {
            @Override
            protected void initExceptionRouting(ChannelPipeline pipeline) {
                pipeline.addLast(new UnwrappedExceptionHandler(),
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
