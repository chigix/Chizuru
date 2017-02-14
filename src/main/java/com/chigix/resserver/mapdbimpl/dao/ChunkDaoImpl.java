package com.chigix.resserver.mapdbimpl.dao;

import com.chigix.resserver.entity.Chunk;
import com.chigix.resserver.mapdbimpl.Serializer;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentMap;
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
        return new Chunk(contentHash, chunk_size, null) {
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
        ConcurrentMap<String, String> map = (ConcurrentMap<String, String>) db.hashMap(ChunkKeys.CHUNK_DB).open();
        String xml = map.get(contentHash);
        if (xml == null) {
            return null;
        }
        Chunk data = Serializer.deserializeChunk(xml);
        Chunk readerInputImpl = newChunk(data.getContentHash(), data.getSize());
        return readerInputImpl;
    }

    public Chunk saveChunkIfAbsent(Chunk c) {
        ConcurrentMap<String, String> map = (ConcurrentMap<String, String>) db.hashMap(ChunkKeys.CHUNK_DB).open();
        String result = map.putIfAbsent(c.getContentHash(), Serializer.serializeChunk(c));
        db.commit();
        if (result == null) {
            return null;
        } else {
            return Serializer.deserializeChunk(result);
        }
    }

}
