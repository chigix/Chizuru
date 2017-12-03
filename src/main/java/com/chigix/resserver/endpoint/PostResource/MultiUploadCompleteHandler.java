package com.chigix.resserver.endpoint.PostResource;

import com.chigix.resserver.config.ApplicationContext;
import com.chigix.resserver.application.Context;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.util.AttributeKey;
import java.util.UUID;

/**
 * Complete Multipart Upload
 * http://docs.aws.amazon.com/AmazonS3/latest/API/mpUploadComplete.html
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class MultiUploadCompleteHandler {

    private final static AttributeKey<MultipartUploadContext> ROUTING_CTX = AttributeKey.newInstance(UUID.randomUUID().toString());

    private static Begin begin = null;

    private static Content content = null;

    private static MultiUploadCompleteResponseHandler response = null;

    /**
     * Beginning handler for Multipart Upload Complete API.
     *
     * @param application
     * @return
     */
    public static final ChannelHandler beginHandler(ApplicationContext application) {
        if (begin == null) {
            begin = new MultiUploadCompleteBeginHandler(application);
        }
        return begin;
    }

    public static final ChannelHandler contentHandler(ApplicationContext application) {
        if (content == null) {
            content = application
                    .getSharableHandler(MultiUploadCompleteContentHandler.class);
        }
        return content;
    }

    public static final ChannelHandler responseHandler(ApplicationContext application) {
        if (response == null) {
            response = new MultiUploadCompleteResponseHandler(application);
        }
        return response;
    }

    static abstract class Begin extends SimpleChannelInboundHandler<Context> {

        protected void setContext(ChannelHandlerContext ctx, MultipartUploadContext routing_ctx) {
            ctx.attr(ROUTING_CTX).set(routing_ctx);
        }
    }

    static class Content extends SimpleChannelInboundHandler<HttpContent> {

        @Override
        protected final void messageReceived(ChannelHandlerContext ctx, HttpContent msg) throws Exception {
            messageReceived(ctx, msg.content(), ctx.attr(ROUTING_CTX).get());
        }

        protected void messageReceived(ChannelHandlerContext ctx, ByteBuf bytebuf, MultipartUploadContext routing_ctx) throws Exception {
            throw new UnsupportedOperationException("Not Supported Yet!");
        }

    }

}
