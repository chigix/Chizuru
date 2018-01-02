package com.chigix.resserver.endpoint.PutResource;

import com.chigix.resserver.domain.model.multiupload.MultipartUpload;
import com.chigix.resserver.domain.model.resource.Resource;
import com.chigix.resserver.application.ResourceInfoContext;
import io.netty.handler.codec.http.router.HttpRouted;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
class MultipartUploadContext extends ResourceInfoContext {

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
