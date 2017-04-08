package com.chigix.resserver.PostResource;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.util.Authorization;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
class MultiUploadCompleteResponseHandler extends SimpleChannelInboundHandler<MultipartUploadContext> {

    private static final Logger LOG = LoggerFactory.getLogger(MultiUploadCompleteResponseHandler.class.getName());

    private final ApplicationContext application;

    public MultiUploadCompleteResponseHandler(ApplicationContext application) {
        this.application = application;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, MultipartUploadContext msg) throws Exception {
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<CompleteMultipartUploadResult xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\">");
        sb.append("<Location>http://Example-Bucket.s3.amazonaws.com/Example-Object</Location>");
        sb.append("<Bucket>").append(msg.getUpload().getResource().getBucket().getName()).append("</Bucket>");
        sb.append("<Key>").append(msg.getResource().getKey()).append("</Key>");
        sb.append("<ETag>\"").append(Authorization.HexEncode(msg.getCurrentEtagCalculator().getEtagDigest().digest())).append("\"</ETag>");
        sb.append("</CompleteMultipartUploadResult>");
        ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(sb.toString().getBytes(CharsetUtil.UTF_8))))
                .addListener((future) -> {
                    if (future.isSuccess()) {
                        LOG.info("UPLOAD SUCCEEDED: [{}], RESOURCE: [{}:{}]",
                                msg.getUpload().getUploadId(),
                                msg.getUpload().getResource().getKey(),
                                msg.getResource().getVersionId());
                        application.getDaoFactory().getUploadDao().removeUpload(msg.getUpload());
                    } else {
                        LOG.warn("Response Writing Error for Upload Success. Upload session is still preserved. "
                                + "UPLOAD SUCCEEDED: [{}], RESOURCE: [{}:{}]",
                                msg.getUpload().getUploadId(),
                                msg.getUpload().getResource().getKey(),
                                msg.getResource().getVersionId()
                        );
                    }
                }
                );
    }

}
