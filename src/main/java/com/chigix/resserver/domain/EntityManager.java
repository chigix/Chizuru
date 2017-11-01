package com.chigix.resserver.domain;

import com.chigix.resserver.domain.model.bucket.BucketRepository;
import com.chigix.resserver.domain.model.chunk.ChunkRepository;
import com.chigix.resserver.domain.model.multiupload.MultipartUploadRepository;
import com.chigix.resserver.domain.model.resource.ResourceRepository;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface EntityManager {

    BucketRepository getBucketRepository();

    ChunkRepository getChunkRepository();

    ResourceRepository getResourceRepository();

    MultipartUploadRepository getUploadRepository();

    void close();

    Lifecycle getEntityState(Object entity) throws UnknownEntityTypeException;

    public static class UnknownEntityTypeException extends Exception {

        public UnknownEntityTypeException(String message) {
            super(message);
        }

    }

}
