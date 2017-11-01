package com.chigix.resserver.endpoint.PostResource;

import com.chigix.resserver.config.ApplicationContext;
import com.chigix.resserver.domain.model.multiupload.MultipartUpload;
import com.chigix.resserver.domain.error.NoSuchUpload;
import com.chigix.resserver.application.Context;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
public class MultiUploadCompleteBeginHandler extends MultiUploadCompleteHandler.Begin {

    private final ApplicationContext application;

    public MultiUploadCompleteBeginHandler(ApplicationContext application) {
        this.application = application;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Context msg) throws Exception {
        String uploadId;
        try {
            uploadId = msg.getQueryDecoder().parameters().get("uploadId").get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new NoSuchUpload();
        }
        final MultipartUpload upload;
        try {
            if (uploadId.length() > 5) {
                upload = application.getEntityManager().getUploadRepository().findUpload(msg.getQueryDecoder().parameters().get("uploadId").get(0));
            } else {
                throw new NoSuchUpload();
            }
        } catch (NullPointerException nullPointerException) {
            if (uploadId == null) {
                throw new NoSuchUpload();
            }
            throw nullPointerException;
        }
        MultipartUploadContext routing_ctx = new MultipartUploadContext(msg.getRoutedInfo(), upload.getResource());
        routing_ctx.setUpload(upload);
        msg.getRoutedInfo().allow();
        setContext(ctx, routing_ctx);
    }

}
