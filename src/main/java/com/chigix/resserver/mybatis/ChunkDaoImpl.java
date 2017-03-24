package com.chigix.resserver.mybatis;

import com.chigix.resserver.entity.Chunk;
import com.chigix.resserver.entity.ChunkedResource;
import com.chigix.resserver.entity.dao.ChunkDao;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
        final AtomicReference<Iterator<Map<String, String>>> it = new AtomicReference<>();
        final AtomicReference<String> continuation = new AtomicReference<>();
        return new Iterator<Chunk>() {
            @Override
            public boolean hasNext() {
                if (it.get() == null) {
                    List<Map<String, String>> rows = chunkMapper.selectByVersion(r.getVersionId());
                    if (rows.isEmpty()) {
                        return false;
                    }
                    it.set(rows.iterator());
                }
                if (it.get().hasNext() == true) {
                    return true;
                }
                Iterator<Map<String, String>> new_it = chunkMapper.selectByVersion(r.getVersionId(), continuation.get()).iterator();
                it.set(new_it);
                if (!continuation.get().equals(new_it.next().get("CONTENT_HASH"))) {
                    return false;
                }
                return it.get().hasNext();
            }

            @Override
            public Chunk next() {
                if (hasNext() == false) {
                    throw new NoSuchElementException();
                }
                Map<String, String> row = it.get().next();
                Object i = row.get("SIZE");
                Chunk c = newChunk(row.get("CONTENT_HASH"), Integer.parseInt(i.toString()));
                continuation.set(c.getContentHash());
                return c;
            }
        };
    }

}
