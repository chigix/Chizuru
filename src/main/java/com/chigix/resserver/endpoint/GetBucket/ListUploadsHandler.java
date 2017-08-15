package com.chigix.resserver.endpoint.GetBucket;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.domain.MultipartUpload;
import com.chigix.resserver.util.HttpHeaderNames;
import com.chigix.resserver.util.IteratorInputStream;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.stream.ChunkedStream;
import io.netty.util.CharsetUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.NoSuchElementException;

/**
 * List Multipart Uploads
 * http://docs.aws.amazon.com/AmazonS3/latest/API/mpUploadListMPUpload.html
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
public class ListUploadsHandler extends SimpleChannelInboundHandler<Context> {

    private final ApplicationContext applicationContext;

    private static ListUploadsHandler INSTANCE = null;

    public static ListUploadsHandler getInstance(ApplicationContext context) {
        if (INSTANCE == null) {
            INSTANCE = new ListUploadsHandler(context);
        }
        return INSTANCE;
    }

    public ListUploadsHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, final Context msg) throws Exception {
        DefaultHttpResponse resp = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/xml");
        resp.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
        ctx.write(resp);
        ctx.writeAndFlush(new HttpChunkedInput(new ChunkedStream(
                new SequenceInputStream(
                        new ByteArrayInputStream(generateDocumentStart(msg).getBytes(CharsetUtil.UTF_8)),
                        new SequenceInputStream(
                                new IteratorInputStream<MultipartUpload>(
                                        applicationContext.getDaoFactory().getUploadDao().listUploadsByBucket(msg.getTargetBucket())) {
                            @Override
                            protected InputStream inputStreamProvider(MultipartUpload upload) throws NoSuchElementException {
                                StringBuilder sb = new StringBuilder("<Upload>");
                                sb.append("<Key>").append(upload.getResource().getKey()).append("</Key>");
                                sb.append("<UploadId>").append(upload.getUploadId()).append("</UploadId>");
                                sb.append("<StorageClass>").append(upload.getResource().getStorageClass()).append("</StorageClass>");
                                sb.append("<Initiated>").append(upload.getInitiated()).append("</Initiated>");
                                sb.append("</Upload>");
                                return new ByteArrayInputStream(sb.toString().getBytes(CharsetUtil.UTF_8));
                            }
                        },
                                new ByteArrayInputStream("</ListMultipartUploadsResult>".getBytes(CharsetUtil.UTF_8))
                        )
                )
        ))
        );
    }

    private String generateDocumentStart(Context context) {
        int maxUploads = 1000;
        QueryStringDecoder decoder = new QueryStringDecoder(context.getRoutedInfo().getRequestMsg().uri());
        if (decoder.parameters().get("max-uploads") != null) {
            maxUploads = Integer.valueOf(decoder.parameters().get("max-uploads").get(0));
        }
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<ListMultipartUploadsResult xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\">");
        sb.append("<Bucket>").append(context.getTargetBucket().getName()).append("</Bucket>");
        sb.append("<KeyMarker></KeyMarker>");
        sb.append("<UploadIdMarker></UploadIdMarker>");
        sb.append("<NextKeyMarker></NextKeyMarker>");
        sb.append("<MaxUploads>").append(maxUploads).append("</MaxUploads>");
        sb.append("<IsTruncated>false</IsTruncated>");
        return sb.toString();
    }

}
