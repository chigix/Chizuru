package com.chigix.resserver.domain;

import com.chigix.resserver.domain.model.bucket.BucketRepository;
import com.chigix.resserver.domain.model.chunk.ChunkRepository;
import com.chigix.resserver.domain.model.multiupload.MultipartUploadRepository;
import com.chigix.resserver.domain.model.resource.ResourceRepository;

/**
 * @TODO rename to EntityManager
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface DaoFactory {

    /**
     *
     * @return
     */
    BucketRepository getBucketRepository();

    /**
     *
     * @return
     */
    ChunkRepository getChunkRepository();

    /**
     *
     * @return
     */
    ResourceRepository getResourceRepository();

    /**
     *
     * @return
     */
    MultipartUploadRepository getUploadRepository();

    void close();

    Lifecycle getEntityState(Object entity) throws UnknownEntityTypeException;

    public static class UnknownEntityTypeException extends Exception {

        public UnknownEntityTypeException(String message) {
            super(message);
        }

    }

}
