package com.chigix.resserver.GetService;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.router.RoutingConfig;
import io.netty.handler.routing.DefaultExceptionForwarder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Routing extends RoutingConfig.GET {

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
        pipeline.addLast(new ChunkedWriteHandler())
                .addLast(new RoutedHandler())
                .addLast(new ResponseHandler())
                .addLast(new DefaultExceptionForwarder());
    }

}
