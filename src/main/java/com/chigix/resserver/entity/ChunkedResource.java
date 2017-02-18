package com.chigix.resserver.entity;

import java.util.Iterator;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public abstract class ChunkedResource extends Resource {

    public ChunkedResource(String key) {
        super(key);
    }

    public ChunkedResource(String key, String versionId) {
        super(key, versionId);
    }

    /**
     * It depends on Dao implementation. Defaultly return empty chunks list for
     * the manually created ChunkedResource object.
     *
     * @return
     */
    public abstract Iterator<Chunk> getChunks();

    public abstract void appendChunk(Chunk chunk);

}
