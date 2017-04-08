package com.chigix.resserver.PutResource;

import com.chigix.resserver.sharablehandlers.Context;
import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.Chunk;
import com.chigix.resserver.entity.ChunkedResource;
import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.entity.error.NoSuchBucket;
import com.chigix.resserver.sharablehandlers.ResourceInfoHandler;
import com.chigix.resserver.util.Authorization;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.router.HttpException;
import io.netty.handler.codec.http.router.HttpRouted;
import io.netty.handler.codec.http.router.RoutingConfig;
import io.netty.handler.routing.DefaultExceptionForwarder;
import io.netty.util.AttributeKey;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adds a resource to a bucket.
 * http://docs.aws.amazon.com/AmazonS3/latest/API/RESTObjectPUT.html
 *
 * Here, every resource received through PUT Resource will be equally preserved
 * as a {@link ChunkedResource}. AmassedResource would not be modified in this
 * handler, which should only be updated via Multipart Upload Init API and
 * Multipart Upload Complete API.
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Routing extends RoutingConfig.PUT {

    private final ApplicationContext application;

    private static final Logger LOG = LoggerFactory.getLogger(Routing.class.getName());

    private static final AttributeKey<Context> CONTEXT = AttributeKey.valueOf(UUID.randomUUID().toString());

    private static final ConcurrentMap<String, Chunk> CHUNK_IN_WRITING = new ConcurrentHashMap<>();

    public Routing(ApplicationContext application) {
        this.application = application;
    }

    @Override
    public String configureRoutingName() {
        return "PUT_RESOURCE";
    }

    @Override
    public String configurePath() {
        return "/:bucketName/:resource_key";
    }

    @Override
    public void configurePipeline(ChannelPipeline pipeline) {
        pipeline.addLast(ResourceInfoHandler.getInstance(application),
                new SimpleChannelInboundHandler<Context>() {
            @Override
            protected void messageReceived(ChannelHandlerContext ctx, Context msg) throws Exception {
                final Resource prebuilt = msg.getResource();
                Context routing_ctx = msg;
                final Bucket b = msg.getResource().getBucket();
                String key = msg.getResource().getKey();
                QueryStringDecoder decoder = new QueryStringDecoder(msg.getRoutedInfo().getRequestMsg().uri());
                List<String> parameter_upload_id = decoder.parameters().get("uploadId");
                List<String> parameter_upload_number = decoder.parameters().get("partNumber");
                if (parameter_upload_id != null && parameter_upload_id.size() > 0
                        && parameter_upload_number != null
                        && parameter_upload_number.size() > 0) {
                    MultipartUploadContext multi_ctx = new MultipartUploadContext(
                            msg.getRoutedInfo(),
                            null);
                    msg.copyTo(multi_ctx);
                    multi_ctx.setMultipartUpload(application.getDaoFactory().getUploadDao().findUpload(parameter_upload_id.get(0)));
                    multi_ctx.setPartNumber(Integer.valueOf(parameter_upload_number.get(0)));
                    routing_ctx = multi_ctx;
                }
                final ChunkedResource new_resource = new ChunkedResource(key) {
                    @Override
                    public Iterator<Chunk> getChunks() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Bucket getBucket() throws NoSuchBucket {
                        return b;
                    }

                };
                prebuilt.snapshotMetaData().forEach((k, v) -> {
                    new_resource.setMetaData(k, v);
                });
                routing_ctx.setResource(new_resource);
                ctx.channel().attr(CONTEXT).set(routing_ctx);
                msg.getRoutedInfo().allow();
            }
        }, new SimpleChannelInboundHandler<HttpContent>() {
            @Override
            protected void messageReceived(ChannelHandlerContext ctx, HttpContent msg) throws Exception {
                Context routing_ctx = ctx.channel().attr(CONTEXT).get();
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
        }, new SimpleChannelInboundHandler<ByteBuf>() {
            @Override
            protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                Context routing_ctx = ctx.channel().attr(CONTEXT).get();
                MessageDigest digest = Chunk.contentHashDigest();
                final byte[] chunk_content = new byte[msg.writerIndex()];
                msg.readBytes(chunk_content);
                digest.update(chunk_content);
                routing_ctx.getEtagDigest().update(chunk_content);
                routing_ctx.getSha256Digest().update(chunk_content);
                final String chunk_hash = Authorization.HexEncode(digest.digest());
                File chunk_file = new File(application.getChunksDir(), "./" + chunk_hash);
                final Chunk chunk = application.getDaoFactory().getChunkDao().newChunk(chunk_hash, chunk_content.length);
                if (application.getDaoFactory().getChunkDao().saveChunkIfAbsent(chunk) == null
                        && CHUNK_IN_WRITING.putIfAbsent(chunk_hash, chunk) == null) {
                    try (OutputStream out = new FileOutputStream(chunk_file)) {
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
                application.getDaoFactory().getResourceDao().appendChunk((ChunkedResource) routing_ctx.getResource(), chunk);
            }
        }, new SimpleChannelInboundHandler<LastHttpContent>() {
            @Override
            protected void messageReceived(ChannelHandlerContext ctx, LastHttpContent msg) throws Exception {
                Context routing_ctx = ctx.channel().attr(CONTEXT).getAndRemove();
                routing_ctx.getResource().setETag(Authorization.HexEncode(routing_ctx.getEtagDigest().digest()));
                if (routing_ctx instanceof MultipartUploadContext) {
                    application.getDaoFactory().getUploadDao().appendChunkedResource(
                            ((MultipartUploadContext) routing_ctx).getMultipartUpload(),
                            (ChunkedResource) routing_ctx.getResource(),
                            ((MultipartUploadContext) routing_ctx).getPartNumber() + "");
                } else {
                    application.getDaoFactory().getResourceDao().saveResource(routing_ctx.getResource()); // Individual ChunkedResource
                }
                DefaultFullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                resp.headers().add(HttpHeaderNames.ETAG, routing_ctx.getResource().getETag());
                ctx.writeAndFlush(resp);
            }
        }, new DefaultExceptionForwarder());
    }

}
