package com.chigix.resserver.mapdbimpl;

import com.chigix.resserver.entity.Chunk;
import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.mapdbimpl.dao.ChunkDaoImpl;
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

    private BucketNameSearchProxy bucketProxy;

    public ResourceInStorage(BucketNameSearchProxy bucket, String key) {
        super(bucket, key);
        bucketProxy = bucket;
    }

    public Chunk getFirstChunk() {
        if (firstChunkHash == null) {
            return null;
        } else if (firstChunk == null) {
            firstChunk = chunkdao.newChunk(firstChunkHash);
        }
        return firstChunk;
    }

    public Chunk getLastChunk() {
        if (lastChunkHash == null) {
            return null;
        } else if (lastChunk == null) {
            lastChunk = chunkdao.newChunk(lastChunkHash);
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

}
