package com.chigix.resserver.endpoint.PutResource;

import com.chigix.resserver.config.ApplicationContext;
import com.chigix.resserver.domain.model.resource.AmassedResource;
import com.chigix.resserver.domain.model.bucket.Bucket;
import com.chigix.resserver.domain.model.chunk.Chunk;
import com.chigix.resserver.domain.model.resource.ChunkedResource;
import com.chigix.resserver.domain.model.resource.Resource;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.domain.error.NoSuchKey;
import com.chigix.resserver.application.Context;
import com.chigix.resserver.interfaces.handling.http.HttpHeaderNames;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import java.util.Iterator;
import java.util.UUID;
import org.joda.time.format.ISODateTimeFormat;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class CopyResourceRouting {

    private static enum MetaDataDirective {
        COPY, REPLACE
    }

    private static final CtxReceiver ctxReceiver = new CtxReceiver();

    private static LastHttpReceiver lastReceiver = null;

    private static final AttributeKey<Context> RESOURCE_CTX = AttributeKey.newInstance(UUID.randomUUID().toString());

    public static void makeRouting(ChannelPipeline pipeline, ApplicationContext app) {
        if (lastReceiver == null) {
            lastReceiver = new LastHttpReceiver(app);
        }
        pipeline.addLast(ctxReceiver, lastReceiver);
    }

    @ChannelHandler.Sharable
    private static class CtxReceiver extends SimpleChannelInboundHandler<Context> {

        @Override
        protected void messageReceived(ChannelHandlerContext ctx, Context msg) throws Exception {
            ctx.attr(RESOURCE_CTX).set(msg);
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
            final Context resource_ctx = ctx.attr(RESOURCE_CTX).getAndRemove();
            final HttpHeaders headers = resource_ctx.getRoutedInfo().getRequestMsg().headers();
            final MetaDataDirective metadata_directive;
            if ("REPLACE".equalsIgnoreCase(headers.getAndConvert(HttpHeaderNames.AMZ_METADATA_DIRECTIVE))) {
                metadata_directive = MetaDataDirective.REPLACE;
            } else {
                metadata_directive = MetaDataDirective.COPY;
            }
            String key_to_copy = QueryStringDecoder.decodeComponent(headers.getAndConvert(HttpHeaderNames.AMZ_COPY_RESOURCE));
            String[] segs;
            while (true) {
                segs = key_to_copy.split("/", 2);
                if (segs.length < 2) {
                    throw new NoSuchKey(key_to_copy);
                }
                if (segs[0].length() > 2) {
                    break;
                } else {
                    key_to_copy = segs[1];
                }
            }
            final Resource resource_to_copy = application.getEntityManager().getResourceRepository().findResource(segs[0], segs[1]);
            final Resource resource_to_save;
            if (resource_to_copy instanceof ChunkedResource) {
                resource_to_save = new ChunkedResource(resource_ctx.getResource().getKey(),
                        resource_to_copy.getVersionId()) {
                    @Override
                    public Iterator<Chunk> getChunks() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Bucket getBucket() throws NoSuchBucket {
                        return resource_ctx.getResource().getBucket();
                    }
                };
            } else if (resource_to_copy instanceof AmassedResource) {
                resource_to_save = new AmassedResource(resource_ctx.getResource().getKey(),
                        resource_to_copy.getVersionId()) {
                    @Override
                    public Iterator<ChunkedResource> getSubResources() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public Bucket getBucket() throws NoSuchBucket {
                        return resource_ctx.getResource().getBucket();
                    }
                };
            } else {
                throw new Exception("UnExcpected::: Unknown type of resource saved in db: "
                        + resource_to_copy.getClass().getSuperclass());
            }
            resource_to_save.setETag(resource_to_copy.getETag());
            resource_to_save.setSize(resource_to_copy.getSize());
            if (metadata_directive == MetaDataDirective.COPY) {
                resource_to_copy.snapshotMetaData().forEach((k, v) -> {
                    resource_to_save.setMetaData(k, v);
                });
            }
            resource_ctx.getResource().snapshotMetaData().forEach((k, v) -> {
                resource_to_save.setMetaData(k, v);
            });
            application.getEntityManager().getResourceRepository().saveResource(resource_to_save);
            StringBuilder return_sb = new StringBuilder("<CopyObjectResult><LastModified>");
            return_sb.append(resource_to_save.getLastModified().toString(ISODateTimeFormat.dateHourMinuteSecond()));
            return_sb.append("</LastModified><ETag>\"");
            return_sb.append(resource_to_save.getETag());
            return_sb.append("\"</ETag></CopyObjectResult>");
            application.finishRequest(resource_ctx.getRoutedInfo());
            ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.copiedBuffer(return_sb.toString().getBytes(CharsetUtil.UTF_8))));
        }

    }

}
