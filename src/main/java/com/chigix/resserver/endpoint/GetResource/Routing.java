package com.chigix.resserver.endpoint.GetResource;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.endpoint.HeadResource.HeadResponseHandler;
import com.chigix.resserver.sharablehandlers.Context;
import com.chigix.resserver.sharablehandlers.ResourceInfoHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.router.RoutingConfig;
import io.netty.handler.routing.DefaultExceptionForwarder;
import io.netty.handler.routing.Router;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.AttributeKey;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Routing extends RoutingConfig.GET {

    private final ApplicationContext application;

    private static final AttributeKey<Context> ROUTING_CTX = AttributeKey.newInstance(UUID.randomUUID().toString());

    public Routing(ApplicationContext application) {
        this.application = application;
    }

    @Override
    public String configureRoutingName() {
        return "GET_RESOURCE";
    }

    @Override
    public String configurePath() {
        return ResourceInfoHandler.ROUTING_PATH;
    }

    @Override
    public void configurePipeline(ChannelPipeline pipeline) {
        final DefaultExceptionForwarder exception_fwd = new DefaultExceptionForwarder();
        pipeline.addLast(new ChunkedWriteHandler(),
                ResourceInfoHandler.getInstance(application),
                new SimpleChannelInboundHandler<Context>() {
            @Override
            protected void messageReceived(ChannelHandlerContext ctx, Context msg) throws Exception {
                ctx.attr(ROUTING_CTX).set(msg);
            }
        }, new Router(true, "GET_RESOURCE_ROUTER") {
            @Override
            protected void route(ChannelHandlerContext ctx, Object msg, Map<String, ChannelPipeline> routingPipelines) throws Exception {
                if (!(msg instanceof LastHttpContent)) {
                    return;
                }
                Context routing_ctx = ctx.attr(ROUTING_CTX).getAndRemove();
                Map<String, List<String>> queries = routing_ctx.getQueryDecoder().parameters();
                if (queries.get("acl") != null) {
                    pipelineForward(routingPipelines.get("GET_RESOURCE_ACL"), routing_ctx);
                } else {
                    pipelineForward(routingPipelines.get("GET_RESOURCE_CONTENT"), routing_ctx);
                }
            }

            @Override
            protected void initRouter(ChannelHandlerContext ctx) throws Exception {
                this.newRouting(ctx, "GET_RESOURCE_CONTENT").addLast(
                        HeadResponseHandler.getInstance(application),
                        ContentResponseHandler.getInstance(application),
                        new DefaultExceptionForwarder());
                this.newRouting(ctx, "GET_RESOURCE_ACL").addLast(
                        AclHandler.getInstance(application),
                        new DefaultExceptionForwarder());
            }

            @Override
            protected void initExceptionRouting(ChannelPipeline pipeline) {
                pipeline.addLast(new ChannelHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        exception_fwd.channelRead(ctx, msg);
                    }

                });
            }

        }, exception_fwd);
    }

}
