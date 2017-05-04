package com.chigix.resserver.endpoint.HeadResource;

import com.chigix.resserver.util.HttpHeaderNames;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponse;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
class RespHeaderFixer extends ChannelHandlerAdapter {

    public static final ChannelHandler DEFAULT = new RespHeaderFixer();

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof HttpResponse)) {
            super.write(ctx, msg, promise);
        }
        HttpResponse resp = (HttpResponse) msg;
        resp.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        ctx.writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE);
    }

}
