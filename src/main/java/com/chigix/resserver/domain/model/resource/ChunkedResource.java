package com.chigix.resserver.domain.model.resource;

import com.chigix.resserver.domain.model.chunk.Chunk;
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
     * @TODO: Remove this method and introducing Chunks Query method in
     * ResourceRepository instead.
     *
     * An inputStream return method would be better for involving a method
     * inside {@link ChunkedResource} model.
     *
     * @deprecated
     * @return
     */
    @Deprecated
    public abstract Iterator<Chunk> getChunks();

}
