package com.chigix.resserver.application;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
public class FlushChannelHandler extends ChannelHandlerAdapter {

    public static ChannelHandler DEFAULT = new FlushChannelHandler();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.flush();
        super.channelRead(ctx, msg);
    }

}
