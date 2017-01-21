package com.chigix.resserver.PutResource;

import com.chigix.resserver.entity.Resource;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.router.HttpRouted;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Context {

    private final HttpRouted routedInfo;

    private final Resource resource;

    private final MessageDigest etagDigest;

    private final MessageDigest sha256Digest;

    private ByteBuf cachingChunkBuf;

    public Context(HttpRouted routedInfo, Resource resource) {
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
        String content_type = routedInfo.getRequestMsg().headers().getAndConvert(HttpHeaderNames.CONTENT_TYPE);
        if (content_type != null) {
            resource.setMetaData(HttpHeaderNames.CONTENT_TYPE.toString(), content_type);
        }
    }

    public HttpRouted getRoutedInfo() {
        return routedInfo;
    }

    public Resource getResource() {
        return resource;
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

    public void setCachingChunkBuf(ByteBuf cachingChunkBuf) {
        this.cachingChunkBuf = cachingChunkBuf;
    }

}
