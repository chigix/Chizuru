package com.chigix.resserver.GetService;

import com.chigix.resserver.GetBucket.LocationHandler;
import com.chigix.resserver.ApplicationContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.router.RoutingConfig;
import io.netty.handler.routing.DefaultExceptionForwarder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Routing extends RoutingConfig.GET {

    private final ApplicationContext ctx;

    public Routing(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public String configureRoutingName() {
        return "GET_SERVICE";
    }

    @Override
    public String configurePath() {
        return "/";
    }

    @Override
    public void configurePipeline(ChannelPipeline pipeline) {
        pipeline.addLast(LocationHandler.getInstance(ctx)).addLast(new ChunkedWriteHandler())
                .addLast(new RoutedHandler())
                .addLast(new ResponseHandler(this.ctx))
                .addLast(new DefaultExceptionForwarder());
    }

}
