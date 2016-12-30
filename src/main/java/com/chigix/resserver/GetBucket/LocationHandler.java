package com.chigix.resserver.GetBucket;

import com.chigix.resserver.ApplicationContext;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.router.HttpRouted;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import java.util.UUID;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
public class LocationHandler extends ChannelHandlerAdapter {

    private final ApplicationContext application;

    private static LocationHandler instance = null;

    private static final AttributeKey<Status> LOCATION_CHECK_STATUS = AttributeKey.newInstance(UUID.randomUUID().toString());

    private enum Status {
        PASS, OCCUPIED
    }

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
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.channel().attr(LOCATION_CHECK_STATUS).compareAndSet(null, Status.PASS);
        if (msg instanceof HttpRouted) {
            HttpRouted routed = (HttpRouted) msg;
        }
        if (this.isLocationRequest(ctx, msg)) {
            ctx.channel().attr(LOCATION_CHECK_STATUS).set(Status.OCCUPIED);
            return;
        }
        if (ctx.channel().attr(LOCATION_CHECK_STATUS).get() == Status.PASS) {
            super.channelRead(ctx, msg);
            return;
        }
        // OCCUPIED
        if (msg instanceof LastHttpContent) {
            ctx.channel().attr(LOCATION_CHECK_STATUS).set(Status.PASS);
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
        ReferenceCountUtil.release(msg);
    }

    private boolean isLocationRequest(ChannelHandlerContext ctx, Object msg) {
        if (!(msg instanceof HttpRouted)) {
            return false;
        }
        HttpRouted routed = (HttpRouted) msg;
        QueryStringDecoder decoder = new QueryStringDecoder(routed.getRequestMsg().uri());
        return decoder.parameters().get("location") != null;
    }

}
