package com.chigix.resserver.endpoint.DeleteResource;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.sharablehandlers.Context;
import com.chigix.resserver.sharablehandlers.ResourceInfoHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.router.RoutingConfig;
import io.netty.util.AttributeKey;
import java.util.UUID;

/**
 * DELETE Resource
 * http://docs.aws.amazon.com/AmazonS3/latest/API/RESTObjectDELETE.html
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Routing extends RoutingConfig.DELETE {

    private final ApplicationContext application;

    private static final AttributeKey<Context> CONTEXT = AttributeKey.newInstance(UUID.randomUUID().toString());

    public Routing(ApplicationContext application) {
        this.application = application;
    }

    @Override
    public String configureRoutingName() {
        return "DELETE_RESOURCE";
    }

    @Override
    public String configurePath() {
        return ResourceInfoHandler.ROUTING_PATH;
    }

    @Override
    public void configurePipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new ResourceInfoHandler(application),
                new SimpleChannelInboundHandler<Context>() {
            @Override
            protected void messageReceived(ChannelHandlerContext ctx, Context msg) throws Exception {
                ctx.attr(CONTEXT).set(msg);
            }
        }, new SimpleChannelInboundHandler<LastHttpContent>() {
            @Override
            protected void messageReceived(ChannelHandlerContext ctx, LastHttpContent msg) throws Exception {
                Context routing_ctx = ctx.attr(CONTEXT).getAndRemove();
                application.getDaoFactory().getResourceDao()
                        .removeResource(routing_ctx.getResource());
                application.finishRequest(routing_ctx.getRoutedInfo());
                ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.NO_CONTENT,
                        Unpooled.EMPTY_BUFFER));
            }
        });
    }

}
