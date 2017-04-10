package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.Chunk;
import com.chigix.resserver.domain.ChunkedResource;
import com.chigix.resserver.domain.dao.ChunkDao;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ChunkDaoImpl implements ChunkDao {

    private final ChunkMapper chunkMapper;

    private ChunkDao aspectForNewChunk = null;

    public ChunkDaoImpl(ChunkMapper dbMapper) {
        this.chunkMapper = dbMapper;
    }

    public void setAspectForNewChunk(ChunkDao aspectForNewChunk) {
        this.aspectForNewChunk = aspectForNewChunk;
    }

    @Override
    public Chunk newChunk(String contentHash, int chunk_size) {
        if (aspectForNewChunk != null) {
            return aspectForNewChunk.newChunk(contentHash, chunk_size);
        }
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

    @Override
    public Chunk saveChunkIfAbsent(Chunk chunk) {
        if (chunkMapper.selectFirstChunkReference(chunk.getContentHash()) == null) {
            return null;
        } else {
            return chunk;
        }
    }

    public Iterator<Chunk> listChunksByResource(final ChunkedResource r) {
        final AtomicReference<String> continuation = new AtomicReference<>();
        final IteratorConcater<Map<String, String>> rows = new IteratorConcater<Map<String, String>>() {
            @Override
            protected Iterator<Map<String, String>> nextIterator() {
                if (continuation.get() == null) {
                    return chunkMapper.selectByVersion(r.getVersionId()).iterator();
                }
                Iterator<Map<String, String>> new_it = chunkMapper.selectByVersion(r.getVersionId(), continuation.get()).iterator();
                if (continuation.get().equals(new_it.next().get("CONTENT_HASH"))) {
                    return Collections.emptyIterator();
                }
                return new_it;
            }
        }.addListener((e) -> {
            continuation.set(e.get("CONTENT_HASH"));
        });
        return new Iterator<Chunk>() {
            @Override
            public boolean hasNext() {
                return rows.hasNext();
            }

            @Override
            public Chunk next() {
                Map<String, String> row = rows.next();
                Object i = row.get("SIZE");
                return newChunk(row.get("CONTENT_HASH"), Integer.parseInt(i.toString()));
            }
        };
    }

}
