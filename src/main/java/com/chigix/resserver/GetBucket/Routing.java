package com.chigix.resserver.GetBucket;

import com.chigix.resserver.ApplicationContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.router.RoutingConfig;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Routing extends RoutingConfig.GET {

    private final ApplicationContext application;

    public Routing(ApplicationContext ctx) {
        application = ctx;
    }

    @Override
    public String configureRoutingName() {
        return "GET_BUCKET";
    }

    @Override
    public String configurePath() {
        return "/:key";
    }

    @Override
    public void configurePipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new LocationHandler(application));
    }

}
