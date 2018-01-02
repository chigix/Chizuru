package com.chigix.resserver.endpoint.DeleteResource;

import com.chigix.resserver.config.ApplicationContext;
import com.chigix.resserver.application.ResourceInfoContext;
import com.chigix.resserver.application.ResourceInfoHandler;
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

    private static final AttributeKey<ResourceInfoContext> CONTEXT = AttributeKey.newInstance(UUID.randomUUID().toString());

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
                new SimpleChannelInboundHandler<ResourceInfoContext>() {
            @Override
            protected void messageReceived(ChannelHandlerContext ctx, ResourceInfoContext msg) throws Exception {
                ctx.attr(CONTEXT).set(msg);
            }
        }, new SimpleChannelInboundHandler<LastHttpContent>() {
            @Override
            protected void messageReceived(ChannelHandlerContext ctx, LastHttpContent msg) throws Exception {
                ResourceInfoContext routing_ctx = ctx.attr(CONTEXT).getAndRemove();
                application.getEntityManager().getResourceRepository()
                        .removeResource(routing_ctx.getResource());
                application.finishRequest(routing_ctx.getRoutedInfo());
                ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.NO_CONTENT,
                        Unpooled.EMPTY_BUFFER));
            }
        });
    }

}
