package com.chigix.resserver.entity.dao;

import com.chigix.resserver.entity.Chunk;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface ChunkDao {

    Chunk newChunk(String contentHash, int chunk_size);

    // INCREASE REFERENCE COUNT
    Chunk saveChunkIfAbsent(Chunk chunk);
}
