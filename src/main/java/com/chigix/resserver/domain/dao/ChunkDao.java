package com.chigix.resserver.domain.dao;

import com.chigix.resserver.domain.Chunk;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface ChunkDao {

    Chunk newChunk(String contentHash, int chunk_size);

    // INCREASE REFERENCE COUNT
    Chunk saveChunkIfAbsent(Chunk chunk);
}
