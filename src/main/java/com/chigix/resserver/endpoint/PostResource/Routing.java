package com.chigix.resserver.endpoint.PostResource;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.sharablehandlers.Context;
import com.chigix.resserver.sharablehandlers.ResourceInfoHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.router.RoutingConfig;
import io.netty.handler.codec.http.router.exceptions.NotFoundException;
import io.netty.handler.routing.DefaultExceptionForwarder;
import io.netty.handler.routing.Router;
import io.netty.handler.routing.SimpleCycleRouter;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Routing extends RoutingConfig.POST {

    private static final Logger LOG = LoggerFactory.getLogger(Routing.class.getName());

    private static RoutingConfig INSTANCE = null;

    /**
     * Not allowed for usage, because Netty Router component doesn't support
     * sharable.
     *
     * @param application
     * @return
     */
    public static RoutingConfig getInstance(ApplicationContext application) {
        if (INSTANCE == null) {
            INSTANCE = new Routing(application);
        }
        return INSTANCE;
    }

    private final ApplicationContext application;

    public Routing(ApplicationContext application) {
        this.application = application;
    }

    @Override
    public String configureRoutingName() {
        return "POST_RESOURCE";
    }

    @Override
    public String configurePath() {
        return ResourceInfoHandler.ROUTING_PATH;
    }

    @Override
    public void configurePipeline(ChannelPipeline pipeline) {
        final DefaultExceptionForwarder forwarder = new DefaultExceptionForwarder();
        pipeline.addLast(ResourceInfoHandler.getInstance(application),
                new SimpleCycleRouter<Context, LastHttpContent>(false, "PostResourceParamRouter") {
            @Override
            protected ChannelPipeline routeBegin(ChannelHandlerContext ctx, Context msg, Map<String, ChannelPipeline> routingPipelines) throws Exception {
                QueryStringDecoder decoder = new QueryStringDecoder(msg.getRoutedInfo().getRequestMsg().uri());
                if (decoder.parameters().get("uploads") != null) {
                    LOG.debug(configureRoutingName() + ":MultipartUploadInitiate");
                    return routingPipelines.get("MultipartUploadInitiate");
                } else if (decoder.parameters().get("uploadId") != null) {
                    LOG.debug(configureRoutingName() + ":MultipartUploadComplete");
                    return routingPipelines.get("MultipartUploadComplete");
                } else {
                    throw new NotFoundException(msg.getRoutedInfo().getRequestMsg().uri(), msg.getRoutedInfo());
                }
            }

            @Override
            protected boolean routeEnd(ChannelHandlerContext ctx, LastHttpContent msg) throws Exception {
                return true;
            }

            @Override
            protected void initRouter(ChannelHandlerContext ctx) throws Exception {
                this.newRouting(ctx, "MultipartUploadInitiate").addLast(
                        MultiUploadInitHandler.getInstance(application),
                        MultiUploadInitHandler.getResponseHandler(application),
                        new DefaultExceptionForwarder());
                this.newRouting(ctx, "MultipartUploadComplete").addLast(
                        MultiUploadCompleteHandler.beginHandler(application),
                        MultiUploadCompleteHandler.contentHandler(application),
                        MultiUploadCompleteHandler.responseHandler(application), new DefaultExceptionForwarder());
            }

            @Override
            protected void initExceptionRouting(ChannelPipeline pipeline) {
                final Router self = this;
                pipeline.addLast(new ChannelHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        self.exceptionCaught(ctx, (Throwable) msg);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        self.exceptionCaught(ctx, cause);
                    }

                });
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                forwarder.exceptionCaught(ctx, cause);
            }

        }).addLast(forwarder);
    }

}
