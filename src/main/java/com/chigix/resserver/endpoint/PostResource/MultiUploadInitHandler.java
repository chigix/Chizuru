package com.chigix.resserver.endpoint.PostResource;

import com.chigix.resserver.config.ApplicationContext;
import com.chigix.resserver.domain.model.resource.AmassedResource;
import com.chigix.resserver.domain.model.bucket.Bucket;
import com.chigix.resserver.domain.model.resource.ChunkedResource;
import com.chigix.resserver.domain.model.multiupload.MultipartUpload;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.application.ResourceInfoContext;
import com.chigix.resserver.interfaces.handling.http.HttpHeaderNames;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import java.util.Iterator;
import java.util.UUID;

/**
 * Initiate Multipart Upload
 * http://docs.aws.amazon.com/AmazonS3/latest/API/mpUploadInitiate.html
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
public class MultiUploadInitHandler extends SimpleChannelInboundHandler<ResourceInfoContext> {

    private final ApplicationContext application;

    private static MultiUploadInitHandler INSTANCE = null;
    private static Response RESPONSE_INSTANCE = null;

    private final static AttributeKey<MultipartUploadContext> ROUTING_CTX = AttributeKey.newInstance(UUID.randomUUID().toString());

    public static ChannelHandler getResponseHandler(ApplicationContext application) {
        if (RESPONSE_INSTANCE == null) {
            RESPONSE_INSTANCE = new Response(application);
        }
        return RESPONSE_INSTANCE;
    }

    public static MultiUploadInitHandler getInstance(ApplicationContext application) {
        if (INSTANCE == null) {
            INSTANCE = new MultiUploadInitHandler(application);
        }
        return INSTANCE;
    }

    public MultiUploadInitHandler(ApplicationContext application) {
        this.application = application;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, ResourceInfoContext msg) throws Exception {
        final AmassedResource r = new AmassedResource(msg.getResource().getKey()) {
            @Override
            public Iterator<ChunkedResource> getSubResources() {
                throw new UnsupportedOperationException("Not supported yet. Please refetch resource from dao.");
            }

            @Override
            public Bucket getBucket() throws NoSuchBucket {
                return msg.getResource().getBucket();
            }
        };
        msg.getResource().snapshotMetaData().entrySet().forEach((entry) -> {
            r.setMetaData(entry.getKey(), entry.getValue());
        });
        final MultipartUpload upload = application.getEntityManager().getUploadRepository().initiateUpload(r);
        final MultipartUploadContext routing_ctx = new MultipartUploadContext(msg.getRoutedInfo(), upload.getResource());
        routing_ctx.setUpload(upload);
        ctx.channel().attr(ROUTING_CTX).set(routing_ctx);
        msg.getRoutedInfo().allow();
    }

    @Sharable
    private static class Response extends SimpleChannelInboundHandler<LastHttpContent> {

        private final ApplicationContext application;

        public Response(ApplicationContext application) {
            this.application = application;
        }

        @Override
        protected void messageReceived(ChannelHandlerContext ctx, LastHttpContent msg) throws Exception {
            MultipartUploadContext routing_ctx = ctx.channel().attr(ROUTING_CTX).getAndRemove();
            StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            sb.append("<InitiateMultipartUploadResult xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\">");
            sb.append("<Bucket>").append(routing_ctx.getResource().getBucket().getName()).append("</Bucket>");
            sb.append("<Key>").append(routing_ctx.getUpload().getResource().getKey()).append("</Key>");
            sb.append("<UploadId>").append(routing_ctx.getUpload().getUploadId()).append("</UploadId>");
            sb.append("</InitiateMultipartUploadResult>");
            application.finishRequest(routing_ctx.getRoutedInfo());
            DefaultFullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(sb.toString().getBytes(CharsetUtil.UTF_8)));
            resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/xml");
            ctx.writeAndFlush(resp);
        }
    }

}
