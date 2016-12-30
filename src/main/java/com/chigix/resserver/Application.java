package com.chigix.resserver;

import com.chigix.resserver.errorhandlers.ExceptionHandler;
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
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.router.FullResponseLengthFixer;
import io.netty.handler.codec.http.router.HttpRouter;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class.getName());

    public static void main(String[] args) throws InterruptedException {
        AtomicReference<ApplicationContext> ctxInited = new AtomicReference<>();
        initNode(ctxInited);
        LOG.info("NODE ID: " + ctxInited.get().getCurrentNodeId());
        LOG.info("Created At: " + ctxInited.get().getCreationDate());
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
                                .addLast(headerFixer(), configRouter(ctxInited.get()));
                    }
                });
        try {
            ChannelFuture future = sb.bind(9000).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            ctxInited.get().getDb().close();
        }
    }

    public static void initNode(AtomicReference<ApplicationContext> ctxInited) {
        File db_file = new File("./data/chizuru.db");
        String node_id = null;
        if (!db_file.exists()) {
            node_id = UUID.randomUUID().toString();
        }
        DB db = DBMaker.fileDB("./data/chizuru.db").transactionEnable().closeOnJvmShutdown().make();
        HTreeMap<String, String> CONFIG = (HTreeMap<String, String>) db.hashMap("CHIZURU", Serializer.STRING, Serializer.STRING).createOrOpen();
        if (CONFIG.get("NODE_ID") == null) {
            if (node_id == null) {
                throw new RuntimeException("Existing DB File without node inited.");
            }
            CONFIG.put("NODE_ID", node_id);
            db.commit();
        }
        if (CONFIG.get("CREATION_DATE") == null) {
            CONFIG.put("CREATION_DATE", new DateTime(DateTimeZone.forID("GMT")).toString());
            db.commit();
        }
        File chunks_dir = new File("./data/chunks");
        if (!chunks_dir.exists()) {
            chunks_dir.mkdirs();
        }
        if (!chunks_dir.isDirectory()) {
            throw new RuntimeException("Unable to have chunks directory.");
        }
        ctxInited.set(new ApplicationContext(CONFIG.get("NODE_ID"), "chizuru", DateTime.parse(CONFIG.get("CREATION_DATE")), new File("./data/chunks"), db));
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

    public static HttpRouter configRouter(ApplicationContext application) {
        return new HttpRouter() {
            @Override
            protected void initExceptionRouting(ChannelPipeline pipeline) {
                pipeline.addLast(ExceptionHandler.getInstance(application));
            }

            @Override
            protected void initRoutings(ChannelHandlerContext ctx, HttpRouter router) {
                this.newRouting(ctx, new com.chigix.resserver.GetService.Routing(application));
                this.newRouting(ctx, new com.chigix.resserver.GetBucket.Routing(application));
                this.newRouting(ctx, new com.chigix.resserver.HeadBucket.Routing(application));
            }

        };
    }

}
