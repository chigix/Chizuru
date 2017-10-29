package com.chigix.resserver.domain.dao;

import com.chigix.resserver.domain.Lifecycle;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface DaoFactory {

    /**
     * @TODO rename to {@link DaoFactory#getBucketRepository() }
     *
     * @return
     */
    BucketDao getBucketDao();

    /**
     * @TODO rename to {@link DaoFactory#getChunkRepository() }
     *
     * @return
     */
    ChunkDao getChunkDao();

    /**
     * @TODO rename to {@link DaoFactory#getResourceRepository() }
     *
     * @return
     */
    ResourceDao getResourceDao();

    /**
     * @TODO rename to {@link DaoFactory#getUploadRepository() }
     *
     * @return
     */
    MultipartUploadDao getUploadDao();

    void close();

    Lifecycle getEntityState(Object entity) throws UnknownEntityTypeException;

    public static class UnknownEntityTypeException extends Exception {

        public UnknownEntityTypeException(String message) {
            super(message);
        }

    }

}
