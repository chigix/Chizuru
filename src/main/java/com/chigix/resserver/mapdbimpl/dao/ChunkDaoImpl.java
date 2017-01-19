package com.chigix.resserver.mapdbimpl.dao;

import com.chigix.resserver.entity.Chunk;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import org.mapdb.DB;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ChunkDaoImpl {

    private final DB db;

    public ChunkDaoImpl(DB db) {
        this.db = db;
    }

    public int increaseChunkRef(String chunk_hash) {
        ConcurrentMap<String, Integer> map = (ConcurrentMap<String, Integer>) db.hashMap(ChunkKeys.REF_COUNT).open();
        map.putIfAbsent(chunk_hash, 0);
        int current, target;
        do {
            current = map.get(chunk_hash);
            if (current >= 1) {
                target = current + 1;
            } else {
                target = 1;
            }
        } while (!map.replace(chunk_hash, current, target));
        db.commit();
        return target;
    }

    public Chunk newChunk(String contentHash, int chunk_size) {
        return new Chunk(contentHash, chunk_size) {
            @Override
            public InputStream getInputStream() throws IOException {
                return new InputStream() {
                    @Override
                    public int read() throws IOException {
                        return -1;
                    }
                };
            }

        };
    }

    public Chunk findChunk(String contentHash) {
        ConcurrentMap<String, Chunk> map = (ConcurrentMap<String, Chunk>) db.hashMap(ChunkKeys.CHUNK_DB).open();
        return map.get(contentHash);
    }

    public Chunk newChunkProxy(String contentHash) {
        final AtomicReference<Chunk> chunk = new AtomicReference<>();
        chunk.set(null);
        Chunk r = new Chunk(contentHash, 0) {
            @Override
            public int getSize() {
                if (chunk.get() == null) {
                    chunk.set(findChunk(contentHash));
                }
                if (chunk.get() == null) {
                    throw new NoSuchElementException();
                }
                return chunk.get().getSize();
            }

            @Override
            public InputStream getInputStream() throws IOException {
                if (chunk.get() == null) {
                    chunk.set(findChunk(contentHash));
                }
                if (chunk.get() == null) {
                    throw new NoSuchElementException();
                }
                return chunk.get().getInputStream();
            }

        };
        return r;
    }

}
