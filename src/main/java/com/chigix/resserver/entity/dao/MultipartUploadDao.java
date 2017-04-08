package com.chigix.resserver.entity.dao;

import com.chigix.resserver.entity.AmassedResource;
import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.ChunkedResource;
import com.chigix.resserver.entity.MultipartUpload;
import com.chigix.resserver.entity.error.InvalidPart;
import com.chigix.resserver.entity.error.NoSuchBucket;
import com.chigix.resserver.entity.error.NoSuchUpload;
import java.util.Iterator;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface MultipartUploadDao {

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
     * @param partNumber
     * @throws NoSuchBucket
     */
    void appendChunkedResource(MultipartUpload upload, ChunkedResource r, String partNumber) throws NoSuchBucket;

}
