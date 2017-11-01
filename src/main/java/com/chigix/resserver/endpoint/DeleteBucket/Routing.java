package com.chigix.resserver.endpoint.DeleteBucket;

import com.chigix.resserver.config.ApplicationContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.router.HttpRouted;
import io.netty.handler.codec.http.router.RoutingConfig;
import io.netty.util.AttributeKey;
import java.util.UUID;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Routing extends RoutingConfig.DELETE {

    private final ApplicationContext application;

    private static final AttributeKey<HttpRouted> ROUTED_INFO = AttributeKey.valueOf(UUID.randomUUID().toString());

    public Routing(ApplicationContext application) {
        this.application = application;
    }

    @Override
    public String configureRoutingName() {
        return "DELETE_BUCKET";
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
                ctx.channel().attr(ROUTED_INFO).set(msg);
            }
        }).addLast(new SimpleChannelInboundHandler<LastHttpContent>() {
            @Override
            protected void messageReceived(ChannelHandlerContext ctx, LastHttpContent msg) throws Exception {
                HttpRouted routed_info = ctx.channel().attr(ROUTED_INFO).get();
                application.getEntityManager().getBucketRepository().deleteBucketByName((String) routed_info.decodedParams().get("bucketName"));
                ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT));
            }
        });
    }

}
