package com.chigix.resserver.endpoint.GetResource;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.domain.error.NoSuchKey;
import com.chigix.resserver.sharablehandlers.Context;
import com.chigix.resserver.sharablehandlers.ExtractGetResponseHandler;
import com.chigix.resserver.sharablehandlers.ResourceInfoHandler;
import com.chigix.resserver.sharablehandlers.ResourceRespEncoder;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.router.RoutingConfig;
import io.netty.handler.routing.DefaultExceptionForwarder;
import io.netty.handler.routing.SimpleIntervalRouter;
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
                ResourceRespEncoder.getInstance(),
                ExtractGetResponseHandler.getInstance(),
                ResourceInfoHandler.getInstance(application),
                new SimpleIntervalRouter<Context, LastHttpContent>(false, "GetResourceParamRouter") {
            @Override
            protected ChannelPipeline routeBegin(ChannelHandlerContext ctx, Context routing_ctx, Map<String, ChannelPipeline> routingPipelines) throws Exception {
                if (routing_ctx.getResource() instanceof Context.UnpersistedResource) {
                    routing_ctx.getRoutedInfo().deny();
                    throw new NoSuchKey(routing_ctx.getResource().getKey());
                }
                Map<String, List<String>> queries = routing_ctx.getQueryDecoder().parameters();
                if (queries.get("acl") != null) {
                    return routingPipelines.get("GET_RESOURCE_ACL");
                } else {
                    return routingPipelines.get("GET_RESOURCE_CONTENT");
                }
            }

            @Override
            protected boolean routeEnd(ChannelHandlerContext ctx, LastHttpContent msg) throws Exception {
                return true;
            }

            @Override
            protected void initRouter(ChannelHandlerContext ctx) throws Exception {
                this.newRouting(ctx, "GET_RESOURCE_CONTENT").addLast(
                        ContentRespHeaderBuildingHandler.getInstance(application),
                        ContentStreamingHandler.getInstance(application),
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
                        exception_fwd.exceptionCaught(ctx, (Throwable) msg);
                    }

                });
            }

        }, exception_fwd);
    }

}
