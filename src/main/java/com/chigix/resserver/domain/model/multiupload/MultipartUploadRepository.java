package com.chigix.resserver.domain.model.multiupload;

import com.chigix.resserver.domain.model.resource.AmassedResource;
import com.chigix.resserver.domain.model.bucket.Bucket;
import com.chigix.resserver.domain.model.resource.ChunkedResource;
import com.chigix.resserver.domain.error.InvalidPart;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.domain.error.NoSuchUpload;
import java.util.Iterator;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface MultipartUploadRepository {

    /**
     *
     * @param resource
     * @return
     * @throws NoSuchBucket
     */
    MultipartUpload initiateUpload(AmassedResource resource) throws NoSuchBucket;

    MultipartUpload findUpload(String uploadId) throws NoSuchUpload, NoSuchBucket;

    ChunkedResource findUploadPart(MultipartUpload upload, String partNumber, String etag) throws InvalidPart;

    void removeUpload(MultipartUpload upload) throws NoSuchUpload;

    Iterator<MultipartUpload> listUploadsByBucket(Bucket b) throws NoSuchBucket;

    /**
     * Saving ChunkedResource Only. **NOTE:** Parent AmassedResource would not
     * be update, because AmassedResource modification should be done in
     * complete api.
     *
     * @param upload
     * @param r
     * @param partNumber A part number uniquely identifies a part and also
     * defines its position within the object being created. If append a new
     * part using the same part number that was used with a previous part, the
     * previously uploaded part is overwritten.
     * @throws NoSuchBucket
     */
    void appendChunkedResource(MultipartUpload upload, ChunkedResource r, String partNumber) throws NoSuchBucket;

}
