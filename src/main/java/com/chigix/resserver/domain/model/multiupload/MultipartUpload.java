package com.chigix.resserver.domain.model.multiupload;

import com.chigix.resserver.domain.model.resource.AmassedResource;
import java.util.UUID;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * @TODO abstract this class
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class MultipartUpload {

    private final AmassedResource resource;

    private final String uploadId;

    private final DateTime initiated;

    public MultipartUpload(AmassedResource resource, String uploadId, DateTime initiated) {
        this.resource = resource;
        this.uploadId = uploadId;
        this.initiated = initiated;
    }

    public MultipartUpload(AmassedResource resource, String uploadId) {
        this.resource = resource;
        this.uploadId = uploadId;
        this.initiated = new DateTime(DateTimeZone.UTC);
    }

    public MultipartUpload(AmassedResource resource) {
        this(resource, UUID.randomUUID().toString().replace("-", ""));
    }

    public AmassedResource getResource() {
        return this.resource;
    }

    public String getUploadId() {
        return uploadId;
    }

    public DateTime getInitiated() {
        return initiated;
    }

}
