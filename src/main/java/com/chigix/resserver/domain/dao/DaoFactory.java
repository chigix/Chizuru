package com.chigix.resserver.domain.dao;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface DaoFactory {

    BucketDao getBucketDao();

    ChunkDao getChunkDao();

    ResourceDao getResourceDao();

    MultipartUploadDao getUploadDao();

}
