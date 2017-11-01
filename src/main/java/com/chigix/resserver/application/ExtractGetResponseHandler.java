package com.chigix.resserver.application;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.router.HttpRouted;
import io.netty.util.AttributeKey;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
public class ExtractGetResponseHandler extends ChannelHandlerAdapter {

    private static final Map<String, Object> SUPPORTED_METHODS = new HashMap<>();

    static {
        SUPPORTED_METHODS.put("GET", "GET");
        SUPPORTED_METHODS.put("HEAD", "HEAD");
    }

    private static ExtractGetResponseHandler instance = null;

    public static final ExtractGetResponseHandler getInstance() {
        if (instance == null) {
            instance = new ExtractGetResponseHandler();
        }
        return instance;
    }

    private static final AttributeKey<HttpRouted> EXTRACTED_ROUTED_INFO = AttributeKey
            .newInstance(UUID.randomUUID().toString());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRouted && SUPPORTED_METHODS.get(
                ((HttpRouted) msg).getRequestMsg().method().toString()) != null) {
            ctx.attr(EXTRACTED_ROUTED_INFO).set((HttpRouted) msg);
            return;
        }
        HttpRouted routed = ctx.attr(EXTRACTED_ROUTED_INFO).getAndRemove();
        if (msg instanceof LastHttpContent && routed != null) {
            super.channelRead(ctx, routed);
            return;
        }
        super.channelRead(ctx, msg);
    }

}
