package com.chigix.resserver.PutResource;

import com.chigix.resserver.entity.MultipartUpload;
import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.sharablehandlers.Context;
import io.netty.handler.codec.http.router.HttpRouted;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
class MultipartUploadContext extends Context {

    private MultipartUpload multipartUpload;

    private Integer partNumber;

    public MultipartUploadContext(HttpRouted routedInfo, Resource resource) {
        super(routedInfo, resource);
    }

    public void setMultipartUpload(MultipartUpload multipartUpload) {
        this.multipartUpload = multipartUpload;
    }

    public MultipartUpload getMultipartUpload() {
        return multipartUpload;
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    public Integer getPartNumber() {
        return partNumber;
    }

}
