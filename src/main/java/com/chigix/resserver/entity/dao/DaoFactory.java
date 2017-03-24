package com.chigix.resserver.entity.dao;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface DaoFactory {

    BucketDao getBucketDao();

    ChunkDao getChunkDao();

    ResourceDao getResourceDao();

}
