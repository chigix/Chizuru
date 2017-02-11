package com.chigix.resserver.PutResource;

import com.chigix.resserver.sharablehandlers.Context;
import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.entity.Chunk;
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
import java.util.UUID;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adds a resource to a bucket.
 * http://docs.aws.amazon.com/AmazonS3/latest/API/RESTObjectPUT.html
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Routing extends RoutingConfig.PUT {

    private final ApplicationContext application;

    private static final Logger LOG = LoggerFactory.getLogger(Routing.class.getName());

    private static final AttributeKey<Context> CONTEXT = AttributeKey.valueOf(UUID.randomUUID().toString());

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
                msg.getResource().empty();
                ctx.channel().attr(CONTEXT).set(msg);
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
                while (content.isReadable()) {
                    int i = content.readInt();
                    try {
                        caching.writeInt(i);
                    } catch (IndexOutOfBoundsException e) {
                        ctx.fireChannelRead(caching);
                        caching = Unpooled.buffer(application.getMaxChunkSize(), application.getMaxChunkSize());
                        caching.writeInt(i);
                        routing_ctx.setCachingChunkBuf(caching);
                    }
                }
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
                byte[] chunk_content = new byte[msg.writerIndex()];
                msg.readBytes(chunk_content);
                digest.update(chunk_content);
                routing_ctx.getEtagDigest().update(chunk_content);
                routing_ctx.getSha256Digest().update(chunk_content);
                final String chunk_hash = Authorization.HexEncode(digest.digest());
                File chunk_file = new File(application.getChunksDir(), "./" + chunk_hash);
                final Chunk chunk = application.ChunkDao.newChunk(chunk_hash, chunk_content.length);
                application.ChunkDao.increaseChunkRef(chunk_hash);
                if (application.ChunkDao.saveChunkIfAbsent(chunk) == null) {
                    try (OutputStream out = new FileOutputStream(chunk_file)) {
                        out.write(chunk_content);
                    } catch (IOException iOException) {
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
                    }
                }
                application.ResourceDao.appendChunk(routing_ctx.getResource(), chunk);
            }
        }, new SimpleChannelInboundHandler<LastHttpContent>() {
            @Override
            protected void messageReceived(ChannelHandlerContext ctx, LastHttpContent msg) throws Exception {
                Context routing_ctx = ctx.channel().attr(CONTEXT).getAndRemove();
                routing_ctx.getResource().setETag(Authorization.HexEncode(routing_ctx.getEtagDigest().digest()));
                application.ResourceDao.saveResource(routing_ctx.getResource());
                DefaultFullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                resp.headers().add(HttpHeaderNames.ETAG, routing_ctx.getResource().getETag());
                ctx.writeAndFlush(resp);
            }
        }, new DefaultExceptionForwarder());
    }

}
