package com.chigix.resserver.endpoint.HeadResource;

import com.chigix.resserver.application.Context;
import com.chigix.resserver.interfaces.handling.http.HttpHeaderNames;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponse;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
class RespHeaderFixer extends SimpleChannelInboundHandler<Context> {

    public static final ChannelHandler DEFAULT = new RespHeaderFixer();

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Context msg) throws Exception {
        HttpResponse resp = msg.getResourceResp();
        resp.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        ctx.writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE);
    }

}
