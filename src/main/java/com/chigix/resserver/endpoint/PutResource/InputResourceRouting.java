package com.chigix.resserver.endpoint.PutResource;

import com.chigix.resserver.config.ApplicationContext;
import com.chigix.resserver.domain.model.chunk.Chunk;
import com.chigix.resserver.domain.model.resource.ChunkedResource;
import com.chigix.resserver.application.ResourceInfoContext;
import com.chigix.resserver.application.util.Authorization;
import com.chigix.resserver.interfaces.handling.http.HttpHeaderNames;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.router.HttpException;
import io.netty.handler.codec.http.router.HttpRouted;
import io.netty.handler.routing.DefaultExceptionForwarder;
import io.netty.util.AttributeKey;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.zip.GZIPOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class InputResourceRouting {

    private static final AttributeKey<ResourceInfoContext> RESOURCE_CTX = AttributeKey.newInstance(UUID.randomUUID().toString());

    private static CtxReceiver routingCtxReceiver = null;
    private static ContentReceiver contentReceiver = null;
    private static ByteBufReceiver byteReceiver = null;
    private static LastHttpReceiver lastReceiver = null;

    private static final ConcurrentMap<String, Chunk> CHUNK_IN_WRITING = new ConcurrentHashMap<>();

    public static void makeRouting(ChannelPipeline pipeline, ApplicationContext app) {
        if (routingCtxReceiver == null) {
            routingCtxReceiver = new CtxReceiver();
        }
        if (contentReceiver == null) {
            contentReceiver = new ContentReceiver(app);
        }
        if (byteReceiver == null) {
            byteReceiver = new ByteBufReceiver(app);
        }
        if (lastReceiver == null) {
            lastReceiver = new LastHttpReceiver(app);
        }
        pipeline.addLast(routingCtxReceiver,
                contentReceiver,
                byteReceiver,
                lastReceiver,
                new DefaultExceptionForwarder());
    }

    @ChannelHandler.Sharable
    private static class CtxReceiver extends SimpleChannelInboundHandler<ResourceInfoContext> {

        @Override
        protected void messageReceived(ChannelHandlerContext ctx, ResourceInfoContext msg) throws Exception {
            ctx.attr(RESOURCE_CTX).set(msg);
        }
    }

    @ChannelHandler.Sharable
    private static class ContentReceiver extends SimpleChannelInboundHandler<HttpContent> {

        private final ApplicationContext application;

        public ContentReceiver(ApplicationContext application) {
            this.application = application;
        }

        @Override
        protected void messageReceived(ChannelHandlerContext ctx, HttpContent msg) throws Exception {
            ResourceInfoContext routing_ctx = ctx.channel().attr(RESOURCE_CTX).get();
            ByteBuf caching = routing_ctx.getCachingChunkBuf();
            if (caching == null) {
                caching = Unpooled.buffer(application.getMaxChunkSize(), application.getMaxChunkSize());
                routing_ctx.setCachingChunkBuf(caching);
            }
            ByteBuf content = msg.content();
            do {
                int readable_bytes = content.readableBytes();
                byte[] reading_buffer;
                if (readable_bytes > 8192) {
                    reading_buffer = new byte[8192];
                } else if (readable_bytes > 0) {
                    reading_buffer = new byte[readable_bytes];
                } else {
                    break;
                }
                if (caching.writableBytes() < reading_buffer.length) {
                    reading_buffer = new byte[caching.writableBytes()];
                }
                content.readBytes(reading_buffer);
                caching.writeBytes(reading_buffer);
                if (caching.writableBytes() < 1) {
                    ctx.fireChannelRead(caching);
                    caching = Unpooled.buffer(application.getMaxChunkSize(), application.getMaxChunkSize());
                    routing_ctx.setCachingChunkBuf(caching);
                }
            } while (true);
            if (msg instanceof LastHttpContent) {
                ctx.fireChannelRead(caching);
                msg.retain();
                ctx.fireChannelRead(msg);
            }

        }
    }

    @ChannelHandler.Sharable
    private static class ByteBufReceiver extends SimpleChannelInboundHandler<ByteBuf> {

        private static final Logger LOG = LoggerFactory.getLogger(ByteBufReceiver.class.getName());

        private final ApplicationContext application;

        public ByteBufReceiver(ApplicationContext application) {
            this.application = application;
        }

        @Override
        protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            ResourceInfoContext routing_ctx = ctx.channel().attr(RESOURCE_CTX).get();
            MessageDigest digest = Chunk.contentHashDigest();
            final byte[] chunk_content = new byte[msg.writerIndex()];
            msg.readBytes(chunk_content);
            digest.update(chunk_content);
            routing_ctx.getEtagDigest().update(chunk_content);
            routing_ctx.getSha256Digest().update(chunk_content);
            final String chunk_hash = Authorization.HexEncode(digest.digest());
            File chunk_file = new File(application.getChunksDir(), "./" + chunk_hash);
            final Chunk chunk = application.getEntityManager().getChunkRepository().newChunk(chunk_hash, chunk_content.length);
            if (application.getEntityManager().getChunkRepository().saveChunkIfAbsent(chunk) == null
                    && CHUNK_IN_WRITING.putIfAbsent(chunk_hash, chunk) == null) {
                try (final OutputStream out = new GZIPOutputStream(new FileOutputStream(chunk_file), true)) {
                    out.write(chunk_content);
                } catch (IOException iOException) {
                    //@TODO Unsafe if the first thread failed writing, 
                    // which is unknown for later threads 
                    // who directly goto database save skipped this step.
                    chunk_file.delete();
                    LOG.error(iOException.getMessage(), iOException);
                    throw new HttpException(iOException.getMessage()) {
                        @Override
                        public HttpRequest getHttpRequest() {
                            return routing_ctx.getRoutedInfo().getRequestMsg();
                        }

                        @Override
                        public HttpRouted getHttpRouted() {
                            return routing_ctx.getRoutedInfo();
                        }
                    };
                } finally {
                    CHUNK_IN_WRITING.remove(chunk_hash);
                }
            }
            application.getEntityManager().getResourceRepository().putChunk(
                    (ChunkedResource) routing_ctx.getResource(),
                    chunk,
                    routing_ctx.getChunkCounter().incrementAndGet() - 1);
        }
    }

    @ChannelHandler.Sharable
    private static class LastHttpReceiver extends SimpleChannelInboundHandler<LastHttpContent> {

        private final ApplicationContext application;

        public LastHttpReceiver(ApplicationContext application) {
            this.application = application;
        }

        @Override
        protected void messageReceived(ChannelHandlerContext ctx, LastHttpContent msg) throws Exception {
            ResourceInfoContext routing_ctx = ctx.channel().attr(RESOURCE_CTX).getAndRemove();
            routing_ctx.getResource().setETag(Authorization.HexEncode(routing_ctx.getEtagDigest().digest()));
            if (routing_ctx instanceof MultipartUploadContext) {
                application.getEntityManager().getUploadRepository().saveSubresource(
                        ((MultipartUploadContext) routing_ctx).getMultipartUpload(),
                        (ChunkedResource) routing_ctx.getResource(),
                        ((MultipartUploadContext) routing_ctx).getPartNumber() + "");
            } else {
                application.getEntityManager().getResourceRepository().saveResource(routing_ctx.getResource()); // Individual ChunkedResource
            }
            application.finishRequest(routing_ctx.getRoutedInfo());
            DefaultFullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            resp.headers().add(HttpHeaderNames.ETAG, routing_ctx.getResource().getETag());
            ctx.writeAndFlush(resp);
        }
    }

}
