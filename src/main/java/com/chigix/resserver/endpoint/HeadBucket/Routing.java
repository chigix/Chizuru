package com.chigix.resserver.endpoint.HeadBucket;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.sharablehandlers.ExtractGetResponseHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.router.HttpRouted;
import io.netty.handler.codec.http.router.RoutingConfig;
import io.netty.handler.routing.DefaultExceptionForwarder;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Routing extends RoutingConfig.HEAD {

    private final ApplicationContext application;

    public Routing(ApplicationContext ctx) {
        application = ctx;
    }

    @Override
    public String configureRoutingName() {
        return "HEAD_BUCKET";
    }

    @Override
    public String configurePath() {
        return "/:bucketName";
    }

    @Override
    public void configurePipeline(ChannelPipeline pipeline) {
        pipeline.addLast(ExtractGetResponseHandler.getInstance(), new SimpleChannelInboundHandler<HttpRouted>() {
            @Override
            protected void messageReceived(ChannelHandlerContext ctx, HttpRouted routed_info) throws Exception {
                application.getDaoFactory().getBucketDao().findBucketByName((String) routed_info.decodedParams().get("bucketName"));
                ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK));
            }
        }, new DefaultExceptionForwarder());
    }

}
