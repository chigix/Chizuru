package com.chigix.resserver.mapdbimpl;

import com.chigix.resserver.entity.Chunk;
import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.mapdbimpl.dao.ChunkDaoImpl;
import com.chigix.resserver.mapdbimpl.dao.ResourceDaoImpl;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ResourceInStorage extends Resource {

    private ChunkDaoImpl chunkdao;

    private ResourceDaoImpl resourcedao;

    private final BucketNameSearchProxy bucketProxy;

    private final String keyHash;

    public ResourceInStorage(BucketNameSearchProxy bucket, String key, String keyHash) {
        super(bucket, key);
        this.keyHash = keyHash;
        bucketProxy = bucket;
        bucket.addCheckers(() -> {
            if (!(bucket.getProxied() instanceof BucketInStorage)) {
                return false;
            }
            return hashKey(((BucketInStorage) bucket.getProxied()).getUUID(), key).equals(keyHash);
        });
    }

    public boolean isEmptied() {
        return resourcedao.findChunkNode(this, "0") == null;
    }

    @Override
    public Iterator<Chunk> getChunks() {
        final ResourceInStorage self = this;
        return new ChunksIterator() {
            @Override
            protected ResourceInStorage getBelongedResource() {
                return self;
            }

        };
    }

    @Override
    public void empty() {
        resourcedao.emptyResourceChunkNode(this);
    }

    public void setChunkdao(ChunkDaoImpl chunkdao) {
        this.chunkdao = chunkdao;
    }

    public void setResourcedao(ResourceDaoImpl resourcedao) {
        this.resourcedao = resourcedao;
    }

    public BucketNameSearchProxy getBucketProxy() {
        return bucketProxy;
    }

    public String getKeyHash() {
        return keyHash;
    }

    public static String hashKey(String bucket_uuid, String resource_key) {
        String keytohash = MessageFormat.format("[bucket: {0}, key: {1}]", bucket_uuid, resource_key);
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
        digest.update(keytohash.getBytes());
        StringBuilder sb = new StringBuilder();
        byte[] hashed = digest.digest();
        for (byte cipher_byte : hashed) {
            sb.append(String.format("%02x", cipher_byte & 0xff));
        }
        return sb.toString();
    }

    private class ChunksIterator implements Iterator<Chunk> {

        private BigInteger nextChunkCount = BigInteger.ZERO;

        protected ResourceInStorage getBelongedResource() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean hasNext() {
            if (nextChunkCount == null) {
                return false;
            }
            return resourcedao.findChunkNode(getBelongedResource(), nextChunkCount.toString(32)) != null;
        }

        @Override
        public Chunk next() {
            String content_hash = resourcedao.findChunkNode(getBelongedResource(), nextChunkCount.toString(32));
            if (content_hash == null) {
                nextChunkCount = null;
                throw new NoSuchElementException();
            }
            nextChunkCount = nextChunkCount.add(BigInteger.ONE);
            return chunkdao.findChunk(content_hash);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Resource Chunk is not "
                    + "allowed to be deleted individually, please use Resource#empty method.");
        }

    }

}
