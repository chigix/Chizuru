package com.chigix.resserver.domain.model.resource;

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

    /**
     * @deprecated @TODO separate it into three methods. The two are still named
     * {@code saveResource} samely but with different arguments list
     * corresponding to {@link AmassedResource} and {@link ChunkedResource}. The
     * third one is designed as {@code appendSubResource}.
     *
     * @param resource
     * @return
     * @throws NoSuchBucket
     */
    Resource saveResource(Resource resource) throws NoSuchBucket;

    Iterator<Resource> listResources(Bucket bucket, int limit) throws NoSuchBucket;

    Iterator<Resource> listResources(Bucket bucket, String continuation, int limit);

    void removeResource(Resource resource) throws NoSuchKey, NoSuchBucket;

    /**
     *
     * @param r
     * @param c
     * @param chunkIndex From zero.
     */
    void putChunk(ChunkedResource r, Chunk c, int chunkIndex);

}
