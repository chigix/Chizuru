package com.chigix.resserver.entity.dao;

import com.chigix.resserver.entity.Chunk;
import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.entity.error.NoSuchKey;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface ResourceDao {

    Resource findResource(String bucketName, String resourceKey) throws NoSuchKey;

    void saveResource(Resource resource);

    /**
     *
     * @param resource
     * @param chunk
     */
    void appendChunk(Resource resource, Chunk chunk);

}
