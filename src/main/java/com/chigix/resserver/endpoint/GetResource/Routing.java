package com.chigix.resserver.endpoint.GetResource;

import com.chigix.resserver.config.ApplicationContext;
import com.chigix.resserver.domain.Lifecycle;
import com.chigix.resserver.domain.error.NoSuchKey;
import com.chigix.resserver.application.ResourceInfoContext;
import com.chigix.resserver.application.ExtractGetResponseHandler;
import com.chigix.resserver.application.ResourceInfoHandler;
import com.chigix.resserver.application.ResourceRespEncoder;
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
                new SimpleMessageRouter<ResourceInfoContext>(false, "GetResourceParamRouter") {
            @Override
            protected ChannelPipeline dispatch(ChannelHandlerContext ctx, ResourceInfoContext msg, Map<String, ChannelPipeline> routings) throws Exception {
                if (application.getEntityManager().getEntityState(msg.getResource()) == Lifecycle.NEW) {
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
