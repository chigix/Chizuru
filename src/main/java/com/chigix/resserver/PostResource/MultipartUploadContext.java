package com.chigix.resserver.PostResource;

import com.chigix.resserver.entity.MultipartUpload;
import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.sharablehandlers.Context;
import io.netty.handler.codec.http.router.HttpRouted;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class MultipartUploadContext extends Context {

    private MultipartUpload upload;

    public MultipartUploadContext(HttpRouted routedInfo, Resource resource) {
        super(routedInfo, resource);
    }

    public void setUpload(MultipartUpload upload) {
        this.upload = upload;
    }

    public MultipartUpload getUpload() {
        return upload;
    }

}
