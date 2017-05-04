package com.chigix.resserver.endpoint.GetBucket;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.util.HttpHeaderNames;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
public class LocationHandler extends SimpleChannelInboundHandler<Context> {

    private final ApplicationContext application;

    private static LocationHandler instance = null;

    public static LocationHandler getInstance(ApplicationContext ctx) {
        if (instance == null) {
            instance = new LocationHandler(ctx);
        }
        return instance;
    }

    public LocationHandler(ApplicationContext ctx) {
        application = ctx;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Context msg) throws Exception {
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<LocationConstraint>");
        sb.append(application.getCurrentNodeId());
        sb.append("</LocationConstraint>");
        HttpResponse resp = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(sb.toString(), CharsetUtil.UTF_8)
        );
        resp.headers().add(HttpHeaderNames.CONTENT_TYPE, "application/xml");
        ctx.writeAndFlush(resp);
    }

}
