package com.chigix.resserver.application;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
public class ResourceRespEncoder extends ChannelHandlerAdapter {

    private static ResourceRespEncoder instance = null;

    public static final ResourceRespEncoder getInstance() {
        if (instance == null) {
            instance = new ResourceRespEncoder();
        }
        return instance;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof Context) {
            encodeContext(ctx, (Context) msg, promise);
            return;
        }
        super.write(ctx, msg, promise);
    }

    private void encodeContext(ChannelHandlerContext ctx, Context resource_ctx, ChannelPromise p) throws Exception {
        ctx.write(resource_ctx.getResourceResp(), p);
    }

}
