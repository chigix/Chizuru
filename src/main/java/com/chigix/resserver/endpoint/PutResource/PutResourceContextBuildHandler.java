package com.chigix.resserver.endpoint.PutResource;

import com.chigix.resserver.config.ApplicationContext;
import com.chigix.resserver.domain.model.bucket.Bucket;
import com.chigix.resserver.domain.model.chunk.Chunk;
import com.chigix.resserver.domain.model.resource.ChunkedResource;
import com.chigix.resserver.domain.model.resource.Resource;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.application.Context;
import com.chigix.resserver.application.ResourceInfoHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.QueryStringDecoder;
import java.util.Iterator;
import java.util.List;

/**
 * Replace the {@link Resource} in {@link Context} from
 * {@link ResourceInfoHandler} with a newly created {@link ChunkedResource}.
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
public class PutResourceContextBuildHandler extends SimpleChannelInboundHandler<Context> {

    private static PutResourceContextBuildHandler instance = null;

    public static final ChannelHandler getInstance(ApplicationContext application) {
        if (instance == null) {
            instance = new PutResourceContextBuildHandler(application);
        }
        return instance;
    }

    private final ApplicationContext application;

    public PutResourceContextBuildHandler(ApplicationContext application) {
        this.application = application;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Context msg) throws Exception {
        final Resource prebuilt = msg.getResource();
        Context routing_ctx = msg;
        final Bucket b = msg.getResource().getBucket();
        String key = msg.getResource().getKey();
        QueryStringDecoder decoder = msg.getQueryDecoder();
        List<String> parameter_upload_id = decoder.parameters().get("uploadId");
        List<String> parameter_upload_number = decoder.parameters().get("partNumber");
        if (parameter_upload_id != null && parameter_upload_id.size() > 0
                && parameter_upload_number != null
                && parameter_upload_number.size() > 0) {
            MultipartUploadContext multi_ctx = new MultipartUploadContext(
                    msg.getRoutedInfo(),
                    null);
            msg.copyTo(multi_ctx);
            multi_ctx.setMultipartUpload(application.getEntityManager().getUploadRepository().findUpload(parameter_upload_id.get(0)));
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
        msg.getRoutedInfo().allow();
        ctx.fireChannelRead(routing_ctx);

    }

}
