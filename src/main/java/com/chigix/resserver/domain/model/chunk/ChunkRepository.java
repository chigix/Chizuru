package com.chigix.resserver.domain.model.chunk;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface ChunkRepository {

    Chunk newChunk(String contentHash, int chunk_size);

    // INCREASE REFERENCE COUNT
    /**
     * This method might should be considered on the combination with
     * Filestorage persistence and db storage.
     *
     * @TODO: File storage should only be accessed once while writing the bytes.
     * While, existence check should be managed through database.
     *
     * @param chunk
     * @return
     */
    Chunk saveChunkIfAbsent(Chunk chunk);
}
