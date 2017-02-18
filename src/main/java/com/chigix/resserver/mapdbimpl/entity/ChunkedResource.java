package com.chigix.resserver.mapdbimpl.entity;

import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.Chunk;
import com.chigix.resserver.mapdbimpl.BucketInStorage;
import com.chigix.resserver.mapdbimpl.dao.ChunkDaoImpl;
import com.chigix.resserver.mapdbimpl.dao.ResourceDaoImpl;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ChunkedResource extends com.chigix.resserver.entity.ChunkedResource implements ResourceExtension {

    private BucketInStorage bucket;

    private final String bucketUUID;
    private final String bucketName;

    private ResourceDaoImpl resourceDao;
    private ChunkDaoImpl chunkDao;

    private final String keyHash;

    public ChunkedResource(String key, String keyhash,
            String versionId, String bucket_uuid, String bucket_name) {
        super(key, versionId);
        bucketUUID = bucket_uuid;
        bucketName = bucket_name;
        keyHash = keyhash;
    }

    public ChunkedResource(String key, String keyhash,
            String bucket_uuid, String bucket_name) {
        super(key);
        bucketUUID = bucket_uuid;
        bucketName = bucket_name;
        keyHash = keyhash;
    }

    @Override
    public Iterator<Chunk> getChunks() {
        final ChunkedResource self = this;
        return new ChunksIterator() {
            @Override
            protected ChunkedResource getBelongedResource() {
                return self;
            }

        };
    }

    @Override
    public void appendChunk(Chunk chunk) {
        resourceDao.appendChunk(this, chunk);
    }

    @Override
    public void empty() {
        resourceDao.emptyResourceChunkNode(this);
    }

    @Override
    public void setBucket(BucketInStorage bucket) {
        this.bucket = bucket;
    }

    @Override
    public Bucket getBucket() {
        return bucket;
    }

    @Override
    public void setResourceDao(ResourceDaoImpl resourceDao) {
        this.resourceDao = resourceDao;
    }

    @Override
    public String getStoredBucketUUID() {
        return bucketUUID;
    }

    @Override
    public String getKeyHash() {
        return keyHash;
    }

    @Override
    public String getStoredBucketName() {
        return bucketName;
    }

    @Override
    public void setChunkDao(ChunkDaoImpl chunkdao) {
        this.chunkDao = chunkdao;
    }

    private class ChunksIterator implements Iterator<Chunk> {

        private BigInteger nextChunkCount = BigInteger.ZERO;

        protected ChunkedResource getBelongedResource() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean hasNext() {
            if (nextChunkCount == null) {
                return false;
            }
            return resourceDao.findChunkNode(getBelongedResource(), nextChunkCount.toString(32)) != null;
        }

        @Override
        public Chunk next() {
            String content_hash = resourceDao.findChunkNode(getBelongedResource(), nextChunkCount.toString(32));
            if (content_hash == null) {
                nextChunkCount = null;
                throw new NoSuchElementException();
            }
            nextChunkCount = nextChunkCount.add(BigInteger.ONE);
            return chunkDao.findChunk(content_hash);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Resource Chunk is not "
                    + "allowed to be deleted individually, please use Resource#empty method.");
        }

    }

}
