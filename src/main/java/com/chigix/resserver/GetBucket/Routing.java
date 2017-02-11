package com.chigix.resserver.GetBucket;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.entity.Bucket;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
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

    private static final AttributeKey<Context> ROUTING_CONTEXT = AttributeKey.newInstance(UUID.randomUUID().toString());

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
        pipeline.addLast(new SimpleChannelInboundHandler<HttpRouted>() {
            @Override
            protected void messageReceived(ChannelHandlerContext ctx, HttpRouted msg) throws Exception {
                Bucket target_bucket = application.BucketDao.findBucketByName((String) msg.decodedParams().get("bucketName"));
                Context routing_ctx = new Context(target_bucket, msg);
                ctx.attr(ROUTING_CONTEXT).set(routing_ctx);
                ctx.fireChannelRead(routing_ctx);
            }
        },
                new LocationHandler(application),
                new ChunkedWriteHandler(),
                new ChannelHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof LastHttpContent) {
                    Context routing_ctx = ctx.channel().attr(ROUTING_CONTEXT).get();
                    if (routing_ctx != null) {
                        super.channelRead(ctx, routing_ctx);
                        ctx.channel().attr(ROUTING_CONTEXT).set(null);
                    }
                }
                ReferenceCountUtil.release(msg);
            }

        }, ResourceListHandler.getInstance(application), new DefaultExceptionForwarder());
    }

}
