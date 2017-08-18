package com.chigix.resserver.endpoint.GetBucket;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.sharablehandlers.ExtractGetResponseHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.router.RoutingConfig;
import io.netty.handler.codec.http.router.exceptions.NotFoundException;
import io.netty.handler.routing.DefaultExceptionForwarder;
import io.netty.handler.routing.SimpleMessageRouter;
import io.netty.handler.stream.ChunkedWriteHandler;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Routing extends RoutingConfig.GET {

    private static final Logger LOG = LoggerFactory.getLogger(Routing.class.getName());

    private static final String ROUTING_NAME = "GET_BUCKET";

    private final ApplicationContext application;

    public Routing(ApplicationContext ctx) {
        application = ctx;
    }

    @Override
    public String configureRoutingName() {
        return ROUTING_NAME;
    }

    @Override
    public String configurePath() {
        return "/:bucketName";
    }

    @Override
    public void configurePipeline(ChannelPipeline pipeline) {
        final DefaultExceptionForwarder exception_forwarder = new DefaultExceptionForwarder();
        pipeline.addLast(ExtractGetResponseHandler.getInstance(),
                Context.Wrapper.getInstance(application),
                new SimpleMessageRouter<Context>(true, "GetBucketRouter") {
            @Override
            protected ChannelPipeline dispatch(ChannelHandlerContext ctx, Context routing_ctx, Map<String, ChannelPipeline> routings) throws Exception {
                QueryStringDecoder decoder = new QueryStringDecoder(routing_ctx.getRoutedInfo().getRequestMsg().uri());
                if (decoder.parameters().get("location") != null) {
                    LOG.debug(ROUTING_NAME + ":BUCKET_LOCATION");
                    return routings.get(ROUTING_NAME + ":BUCKET_LOCATION");
                } else if (decoder.parameters().get("uploads") != null) {
                    LOG.debug(ROUTING_NAME + ":LIST_UPLOADS");
                    return routings.get(ROUTING_NAME + ":LIST_UPLOADS");
                } else if (decoder.parameters().get("acl") != null) {
                    LOG.debug(ROUTING_NAME + ":GET_BUCKET_ACL");
                    throw new NotFoundException("GET_BUCKET_ACL-->NOT supported", routing_ctx.getRoutedInfo());
                } else {
                    LOG.debug(ROUTING_NAME + ":RESOURCE_LIST");
                    return routings.get(ROUTING_NAME + ":RESOURCE_LIST");
                }
            }

            @Override
            protected void initRouter(ChannelHandlerContext ctx) throws Exception {
                this.newRouting(ctx, ROUTING_NAME + ":RESOURCE_LIST").addLast(
                        new ChunkedWriteHandler(),
                        ResourceListHandler.getInstance(application), new DefaultExceptionForwarder());
                this.newRouting(ctx, ROUTING_NAME + ":BUCKET_LOCATION").addLast(LocationHandler.getInstance(application), new DefaultExceptionForwarder());
                this.newRouting(ctx, ROUTING_NAME + ":LIST_UPLOADS").addLast(
                        new ChunkedWriteHandler(),
                        ListUploadsHandler.getInstance(application), new DefaultExceptionForwarder());
            }

            @Override
            protected void initExceptionRouting(ChannelPipeline pipeline) {
                pipeline.addLast(new ChannelHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        exception_forwarder.exceptionCaught(ctx, (Throwable) msg);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        exception_forwarder.exceptionCaught(ctx, cause);
                    }

                });
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                exception_forwarder.exceptionCaught(ctx, cause);
            }

        },
                exception_forwarder
        );
    }

}
