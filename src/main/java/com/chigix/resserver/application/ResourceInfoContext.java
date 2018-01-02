package com.chigix.resserver.application;

import com.chigix.resserver.domain.model.resource.Resource;
import com.chigix.resserver.interfaces.handling.http.HttpHeaderNames;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.router.HttpRouted;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Build a context relative to a given Resource involving relative metadata from
 * request headers.
 *
 * The list of modifiable metadata for resources are referenced from:
 * https://docs.aws.amazon.com/AmazonS3/latest/dev/UsingMetadata.html#SysMetadata
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ResourceInfoContext {

    private final HttpResponse resourceResp;

    private final HttpRouted routedInfo;

    private Resource resource;

    private final MessageDigest etagDigest;

    private final MessageDigest sha256Digest;

    private ByteBuf cachingChunkBuf;

    private final QueryStringDecoder queryDecoder;

    private final AtomicInteger chunkCounter;

    private ResourceInfoContext(HttpRouted routedInfo, Resource resource, HttpResponse httpresp) {
        try {
            this.etagDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
        try {
            this.sha256Digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
        this.routedInfo = routedInfo;
        this.resource = resource;
        this.queryDecoder = new QueryStringDecoder(routedInfo.getRequestMsg().uri());
        chunkCounter = new AtomicInteger();
        String content_type = routedInfo.getRequestMsg().headers().getAndConvert(HttpHeaderNames.CONTENT_TYPE);
        if (content_type != null) {
            resource.setMetaData("Content-Type", content_type);
        }
        String cache_control = routedInfo.getRequestMsg().headers().getAndConvert(HttpHeaderNames.CACHE_CONTROL);
        if (cache_control != null) {
            resource.setMetaData("Cache-Control", cache_control);
        }
        String content_disp = routedInfo.getRequestMsg().headers().getAndConvert(HttpHeaderNames.CONTENT_DISPOSITION);
        if (content_disp != null) {
            resource.setMetaData("Content-Disposition", content_disp);
        }
        String content_enc = routedInfo.getRequestMsg().headers().getAndConvert(HttpHeaderNames.CONTENT_ENCODING);
        if (content_enc != null) {
            resource.setMetaData("Content-Encoding", content_enc);
        }
        resourceResp = httpresp;
    }

    public ResourceInfoContext(HttpRouted routedInfo, Resource resource) {
        this(routedInfo, resource, new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK));
    }

    public ResourceInfoContext(ResourceInfoContext src) {
        this(src.routedInfo, src.resource, src.getResourceResp());
        this.cachingChunkBuf = src.cachingChunkBuf;
    }

    public final HttpRouted getRoutedInfo() {
        return routedInfo;
    }

    public QueryStringDecoder getQueryDecoder() {
        return queryDecoder;
    }

    public Resource getResource() {
        return resource;
    }

    /**
     * @todo remove all reference on this method and use the new decorating
     * constructor.<p>
     * ----- Why not set a clone method into this class instead of proxy-liked
     * constructor.
     * @deprecated
     * @param resource
     */
    public final void setResource(Resource resource) {
        if (resource == null) {
            throw new InvalidParameterException();
        }
        this.resource = resource;
    }

    public MessageDigest getEtagDigest() {
        return etagDigest;
    }

    public MessageDigest getSha256Digest() {
        return sha256Digest;
    }

    public ByteBuf getCachingChunkBuf() {
        return cachingChunkBuf;
    }

    public final void setCachingChunkBuf(ByteBuf cachingChunkBuf) {
        this.cachingChunkBuf = cachingChunkBuf;
    }

    public AtomicInteger getChunkCounter() {
        return chunkCounter;
    }

    public HttpResponse getResourceResp() {
        return resourceResp;
    }

    public void copyTo(ResourceInfoContext target) {
        target.cachingChunkBuf = this.cachingChunkBuf;
        target.resource = this.resource;
    }

}
