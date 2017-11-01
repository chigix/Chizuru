package com.chigix.resserver.application;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
public class CloseChannelInReadHandler extends ChannelHandlerAdapter {

    public static ChannelHandler DEFAULT = new CloseChannelInReadHandler();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.flush();
        ctx.close().addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

}
