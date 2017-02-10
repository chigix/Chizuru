package com.chigix.resserver.entity.dao;

import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.Chunk;
import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.entity.error.DaoException;
import com.chigix.resserver.entity.error.NoSuchBucket;
import com.chigix.resserver.entity.error.NoSuchKey;
import java.util.Iterator;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface ResourceDao {

    Resource findResource(String bucketName, String resourceKey) throws NoSuchKey, NoSuchBucket;

    Resource findResource(Bucket bucket, String resourceKey) throws NoSuchKey, NoSuchBucket;

    Resource saveResource(Resource resource) throws NoSuchBucket, DaoException;

    /**
     *
     * @param resource
     * @param chunk
     */
    void appendChunk(Resource resource, Chunk chunk);

    Iterator<Resource> listResources(Bucket bucket) throws NoSuchBucket;

    Iterator<Resource> listResources(Bucket bucket, String continuation);

    void removeResource(Resource resource) throws NoSuchKey, NoSuchBucket;

}
