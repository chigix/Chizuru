package com.chigix.resserver.endpoint.PutResource;

import com.chigix.resserver.application.Context;
import com.chigix.resserver.config.ApplicationContext;
import com.chigix.resserver.domain.model.resource.ChunkedResource;
import com.chigix.resserver.application.ResourceInfoHandler;
import com.chigix.resserver.interfaces.handling.http.HttpHeaderNames;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.router.RoutingConfig;
import io.netty.handler.routing.DefaultExceptionForwarder;
import io.netty.handler.routing.SimpleIntervalRouter;
import java.util.Map;

/**
 * Adds a resource to a bucket.
 * http://docs.aws.amazon.com/AmazonS3/latest/API/RESTObjectPUT.html
 *
 * Here, every resource received through PUT Resource will be equally preserved
 * as a {@link ChunkedResource}. AmassedResource would not be modified in this
 * handler, which should only be updated via Multipart Upload Init API and
 * Multipart Upload Complete API.
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Routing extends RoutingConfig.PUT {

    private final ApplicationContext application;

    public Routing(ApplicationContext application) {
        this.application = application;
    }

    @Override
    public String configureRoutingName() {
        return "PUT_RESOURCE";
    }

    @Override
    public String configurePath() {
        return ResourceInfoHandler.ROUTING_PATH;
    }

    @Override
    public void configurePipeline(ChannelPipeline pipeline) {
        final DefaultExceptionForwarder forwarder = new DefaultExceptionForwarder();
        pipeline.addLast(ResourceInfoHandler.getInstance(application),
                PutResourceContextBuildHandler.getInstance(application),
                new SimpleIntervalRouter<Context, LastHttpContent>(false, "PutResourceParamRouter") {
            @Override
            protected ChannelPipeline routeBegin(ChannelHandlerContext ctx, Context msg, Map<String, ChannelPipeline> routingPipelines) throws Exception {
                HttpHeaders headers = msg.getRoutedInfo().getRequestMsg().headers();
                if (headers.contains(HttpHeaderNames.AMZ_COPY_RESOURCE)) {
                    return routingPipelines.get("PUT_RESOURCE_COPY");
                } else {
                    return routingPipelines.get("PUT_RESOURCE_COMMON");
                }
            }

            @Override
            protected boolean routeEnd(ChannelHandlerContext ctx, LastHttpContent msg) throws Exception {
                return true;
            }

            @Override
            protected void initRouter(ChannelHandlerContext ctx) throws Exception {
                InputResourceRouting.makeRouting(this.newRouting(ctx, "PUT_RESOURCE_COMMON"), application);
                CopyResourceRouting.makeRouting(this.newRouting(ctx, "PUT_RESOURCE_COPY"), application);
            }

            @Override
            protected void initExceptionRouting(ChannelPipeline pipeline) {
                pipeline.addLast(new ChannelHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        forwarder.exceptionCaught(ctx, (Throwable) msg);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        forwarder.exceptionCaught(ctx, cause);
                    }

                });
            }

        }, forwarder);
    }

}
