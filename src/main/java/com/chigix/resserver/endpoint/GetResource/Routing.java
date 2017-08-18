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
import io.netty.handler.codec.http.router.RoutingConfig;
import io.netty.handler.routing.DefaultExceptionForwarder;
import io.netty.handler.routing.SimpleMessageRouter;
import io.netty.handler.stream.ChunkedWriteHandler;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Routing extends RoutingConfig.GET {

    private final ApplicationContext application;

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
                new SimpleMessageRouter<Context>(false, "GetResourceParamRouter") {
            @Override
            protected ChannelPipeline dispatch(ChannelHandlerContext ctx, Context msg, Map<String, ChannelPipeline> routings) throws Exception {
                if (msg.getResource() instanceof Context.UnpersistedResource) {
                    msg.getRoutedInfo().deny();
                    throw new NoSuchKey(msg.getResource().getKey());
                }
                Map<String, List<String>> queries = msg.getQueryDecoder().parameters();
                if (queries.get("acl") != null) {
                    return routings.get("GET_RESOURCE_ACL");
                } else {
                    return routings.get("GET_RESOURCE_CONTENT");
                }
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
