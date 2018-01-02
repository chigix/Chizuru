package com.chigix.resserver.endpoint.HeadResource;

import com.chigix.resserver.application.ResourceInfoContext;
import com.chigix.resserver.domain.error.NoSuchKey;
import com.chigix.resserver.interfaces.handling.http.HttpHeaderNames;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponse;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
class RespHeaderFixer extends SimpleChannelInboundHandler<ResourceInfoContext> {

    public static final ChannelHandler DEFAULT = new RespHeaderFixer();

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, ResourceInfoContext msg) throws Exception {
        HttpResponse resp = msg.getResourceResp();
        resp.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        ctx.writeAndFlush(msg).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof NoSuchKey) {
            super.exceptionCaught(ctx,
                    new NoSuchKey(((NoSuchKey) cause).getResourceKey()) {
                @Override
                public HttpResponse fixResponse(FullHttpResponse resp) {
                    FullHttpResponse new_resp = resp.copy(Unpooled.EMPTY_BUFFER);
                    new_resp.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
                    new_resp.headers().remove(HttpHeaderNames.CONTENT_LENGTH);
                    return new_resp;
                }

            });
        }
        super.exceptionCaught(ctx, cause);
    }

}
