package com.chigix.resserver.GetBucket;

import com.chigix.resserver.ApplicationContext;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.router.HttpRouted;
import io.netty.handler.codec.http.router.RoutingConfig;
import io.netty.handler.routing.DefaultExceptionForwarder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import java.util.UUID;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Routing extends RoutingConfig.GET {

    private final ApplicationContext application;

    private static final AttributeKey<HttpRouted> ROUTED_INFO = AttributeKey.newInstance(UUID.randomUUID().toString());

    public Routing(ApplicationContext ctx) {
        application = ctx;
    }

    @Override
    public String configureRoutingName() {
        return "GET_BUCKET";
    }

    @Override
    public String configurePath() {
        return "/:bucketName";
    }

    @Override
    public void configurePipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new LocationHandler(application))
                .addLast(new ChunkedWriteHandler())
                .addLast(new ChannelHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        if (msg instanceof HttpRouted) {
                            ((HttpRouted) msg).allow();
                            ctx.channel().attr(ROUTED_INFO).set((HttpRouted) msg);
                        } else if (msg instanceof LastHttpContent) {
                            HttpRouted routed_info = ctx.channel().attr(ROUTED_INFO).get();
                            if (routed_info == null) {
                                throw new Exception("NO http routed info recorded previously.");
                            }
                            super.channelRead(ctx, routed_info);
                            ctx.channel().attr(ROUTED_INFO).set(null);
                        }
                        ReferenceCountUtil.release(msg);
                    }

                })
                .addLast(ResourceListHandler.getInstance(application))
                .addLast(new DefaultExceptionForwarder());
    }

}
