package com.chigix.resserver.endpoint.PostBucket;

import com.chigix.resserver.config.ApplicationContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.router.HttpRouted;
import io.netty.handler.codec.http.router.RoutingConfig;
import io.netty.handler.codec.http.router.exceptions.NotFoundException;
import io.netty.handler.routing.DefaultExceptionForwarder;
import io.netty.handler.routing.SimpleIntervalRouter;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Routing extends RoutingConfig.POST {

    private final ApplicationContext application;

    public Routing(ApplicationContext application) {
        this.application = application;
    }

    @Override
    public String configureRoutingName() {
        return "POST_BUCKET";
    }

    @Override
    public String configurePath() {
        return "/:bucketName";
    }

    @Override
    public void configurePipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new SimpleIntervalRouter<HttpRouted, LastHttpContent>(false, "PostBucketParamRouter") {

            @Override
            protected void initRouter(ChannelHandlerContext ctx) throws Exception {
                this.newRouting(ctx, "MULTIPLE_DELETE").addLast(new MultiDeleteHandler(application),
                        new DefaultExceptionForwarder()
                );
            }

            @Override
            protected ChannelPipeline routeBegin(ChannelHandlerContext ctx, HttpRouted msg, Map<String, ChannelPipeline> routingPipelines) throws Exception {
                QueryStringDecoder decoder = new QueryStringDecoder(msg.getRequestMsg().uri());
                Map<String, List<String>> parameters = decoder.parameters();
                if (parameters.get("delete") != null) {
                    return routingPipelines.get("MULTIPLE_DELETE");
                }
                throw new NotFoundException(msg.getRequestMsg().uri(), msg);
            }

            @Override
            protected boolean routeEnd(ChannelHandlerContext ctx, LastHttpContent msg) throws Exception {
                return true;
            }

        }, new DefaultExceptionForwarder());
    }

}
