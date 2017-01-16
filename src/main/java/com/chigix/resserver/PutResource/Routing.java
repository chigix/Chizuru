package com.chigix.resserver.PutResource;

import com.chigix.resserver.AmzHeaderNames;
import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.Chunk;
import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.entity.error.NoSuchKey;
import com.chigix.resserver.util.Authorization;
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
import java.text.MessageFormat;
import java.util.UUID;
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
        pipeline.addLast(new SimpleChannelInboundHandler<HttpRouted>() {
            @Override
            protected void messageReceived(ChannelHandlerContext ctx, HttpRouted msg) throws Exception {
                Bucket b = application.BucketDao.findBucketByName((String) msg.decodedParams().get("bucketName"));
                Resource r;
                try {
                    r = application.ResourceDao.findResource(
                            b.getName(),
                            (String) msg.decodedParams().get("resource_key")
                    );
                } catch (NoSuchKey noSuchKey) {
                    r = new Resource(b, (String) msg.decodedParams().get("resource_key"));
                }
                r.empty();
                Context routing_ctx = new Context(msg, r);
                ctx.channel().attr(CONTEXT).set(routing_ctx);
                LOG.info(MessageFormat.format("PUT FILE: [{0}/{1}]", routing_ctx.getResource().getBucket().getName(), routing_ctx.getResource().getKey()));
                LOG.info(MessageFormat.format("TYPE: [{0}]", routing_ctx.getResource().snapshotMetaData().get(HttpHeaderNames.CONTENT_TYPE.toString())));
                LOG.info(MessageFormat.format("SHA-256: [{0}]", routing_ctx.getRoutedInfo().getRequestMsg().headers().get(AmzHeaderNames.CONTENT_SHA256)));
                msg.allow();
            }
        }, new SimpleChannelInboundHandler<HttpContent>() {
            @Override
            protected void messageReceived(ChannelHandlerContext ctx, HttpContent msg) throws Exception {
                Context routing_ctx = ctx.channel().attr(CONTEXT).get();
                MessageDigest digest = Chunk.contentHashDigest();
                byte[] chunk_content = new byte[msg.content().writerIndex()];
                msg.content().readBytes(chunk_content);
                if (msg.content().isReadable()) {
                    throw new Exception("ByteBuf is still readable however bytes length has reached writerIndex.");
                }
                digest.update(chunk_content);
                routing_ctx.getEtagDigest().update(chunk_content);
                routing_ctx.getSha256Digest().update(chunk_content);
                final String chunk_hash = Authorization.HexEncode(digest.digest());
                File chunk_file = new File(application.getChunksDir(), "./" + chunk_hash);
                if (application.ChunkDao.increaseChunkRef(chunk_hash) > 1) {
                } else if (chunk_file.createNewFile()) {
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
                final Chunk chunk = application.ChunkDao.newChunk(chunk_hash);
                application.ResourceDao.appendChunk(routing_ctx.getResource(), chunk);
                LOG.info(msg.content().writerIndex() + "");
                if (msg instanceof LastHttpContent) {
                    msg.retain();
                    ctx.fireChannelRead(msg);
                }
            }
        }, new SimpleChannelInboundHandler<LastHttpContent>() {
            @Override
            protected void messageReceived(ChannelHandlerContext ctx, LastHttpContent msg) throws Exception {
                Context routing_ctx = ctx.channel().attr(CONTEXT).getAndRemove();
                routing_ctx.getResource().setETag(Authorization.HexEncode(routing_ctx.getEtagDigest().digest()));
                LOG.info("CALCULATED SHA256: " + Authorization.HexEncode(routing_ctx.getSha256Digest().digest()));
                DefaultFullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                resp.headers().add(HttpHeaderNames.ETAG, routing_ctx.getResource().getETag());
                ctx.writeAndFlush(resp);
            }
        }, new DefaultExceptionForwarder());
    }

}
