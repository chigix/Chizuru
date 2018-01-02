package com.chigix.resserver.domain.model.resource;

import com.chigix.resserver.domain.Specification;
import com.chigix.resserver.domain.model.bucket.Bucket;
import com.chigix.resserver.domain.model.chunk.Chunk;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.domain.error.NoSuchKey;
import java.util.Iterator;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface ResourceRepository {

    Resource findResource(String bucketName, String resourceKey) throws NoSuchKey, NoSuchBucket;

    Resource findResource(Bucket bucket, String resourceKey) throws NoSuchKey, NoSuchBucket;

    Resource saveResource(Resource resource) throws NoSuchBucket;

    Resource insertSubresource(ChunkedResource r, SubresourceSpecification spec) throws NoSuchBucket;

    /**
     * Fetch Resources through an iterator via specifications as criteria.
     *
     * @param specification
     * @param limit
     * @return
     */
    Iterator<Resource> fetchResources(Specification<Resource> specification, int limit);

    void removeResource(Resource resource) throws NoSuchKey, NoSuchBucket;

    /**
     *
     * @param r
     * @param c
     * @param chunkIndex From zero.
     */
    void putChunk(ChunkedResource r, Chunk c, int chunkIndex);

}
