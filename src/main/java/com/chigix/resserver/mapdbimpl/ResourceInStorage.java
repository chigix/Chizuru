package com.chigix.resserver.mapdbimpl;

import com.chigix.resserver.entity.Chunk;
import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.mapdbimpl.dao.ChunkDaoImpl;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Iterator;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ResourceInStorage extends Resource {

    private Chunk firstChunk;
    private String firstChunkHash = null;

    private Chunk lastChunk;
    private String lastChunkHash = null;

    private ChunkDaoImpl chunkdao;

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

    public Chunk getFirstChunk() {
        if (firstChunkHash == null) {
            return null;
        } else if (firstChunk == null) {
            firstChunk = chunkdao.newChunkProxy(firstChunkHash);
        }
        return firstChunk;
    }

    public Chunk getLastChunk() {
        if (lastChunkHash == null) {
            return null;
        } else if (lastChunk == null) {
            lastChunk = chunkdao.newChunkProxy(lastChunkHash);
        }
        return lastChunk;
    }

    @Override
    public Iterator<Chunk> getChunks() {
        return super.getChunks();
    }

    public void setFirstChunk(Chunk firstChunk) {
        this.firstChunk = firstChunk;
        this.firstChunkHash = firstChunk.getContentHash();
    }

    public void setFirstChunk(String firstChunkHash) {
        this.firstChunkHash = firstChunkHash;
    }

    public void setLastChunk(Chunk lastChunk) {
        this.lastChunk = lastChunk;
        this.lastChunkHash = lastChunk.getContentHash();
    }

    public void setLastChunk(String lastChunkHash) {
        this.lastChunkHash = lastChunkHash;
    }

    @Override
    public void empty() {
        firstChunk = null;
        firstChunkHash = null;
        lastChunk = null;
        lastChunkHash = null;
    }

    public void setChunkdao(ChunkDaoImpl chunkdao) {
        this.chunkdao = chunkdao;
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

}
