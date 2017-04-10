package com.chigix.resserver.endpoint.GetService;

import com.chigix.resserver.endpoint.GetBucket.LocationHandler;
import com.chigix.resserver.ApplicationContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.router.HttpRouted;
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
                .addLast(new SimpleChannelInboundHandler<HttpRouted>() {
                    @Override
                    protected void messageReceived(ChannelHandlerContext ctx, HttpRouted msg) throws Exception {
                        msg.allow();
                    }
                })
                .addLast(new ResponseHandler(this.ctx))
                .addLast(new DefaultExceptionForwarder());
    }

}
