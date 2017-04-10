package com.chigix.resserver.domain.dao;

import com.chigix.resserver.domain.Bucket;
import com.chigix.resserver.domain.Chunk;
import com.chigix.resserver.domain.ChunkedResource;
import com.chigix.resserver.domain.Resource;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.domain.error.NoSuchKey;
import java.util.Iterator;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface ResourceDao {

    Resource findResource(String bucketName, String resourceKey) throws NoSuchKey, NoSuchBucket;

    Resource findResource(Bucket bucket, String resourceKey) throws NoSuchKey, NoSuchBucket;

    Resource saveResource(Resource resource) throws NoSuchBucket;

    Iterator<Resource> listResources(Bucket bucket) throws NoSuchBucket;

    Iterator<Resource> listResources(Bucket bucket, String continuation);

    void removeResource(Resource resource) throws NoSuchKey, NoSuchBucket;

    void appendChunk(ChunkedResource r, Chunk c);

}
