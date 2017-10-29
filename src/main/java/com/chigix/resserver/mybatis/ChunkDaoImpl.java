package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.Chunk;
import com.chigix.resserver.domain.ChunkedResource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import com.chigix.resserver.mybatis.dao.ChunkMapper;
import com.chigix.resserver.mybatis.record.ChunkExample;
import com.chigix.resserver.mybatis.record.ChunkExampleExtending;
import com.chigix.resserver.mybatis.record.Util;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ibatis.session.RowBounds;
import com.chigix.resserver.domain.dao.ChunkDao;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 * @TODO Rename to ChunkRepositoryImpl
 */
public class ChunkDaoImpl implements ChunkDao {

    private final ChunkMapper chunkMapper;

    private ChunkDao aspectForNewChunk = null;

    public ChunkDaoImpl(ChunkMapper dbMapper) {
        this.chunkMapper = dbMapper;
    }

    /**
     * @TODO: This method is also a confusing design. But now there is no
     * solution to work around Filesystem support into Database Implementation
     * class in my head.
     *
     * @param aspectForNewChunk
     */
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
        ChunkExample example = new ChunkExample();
        example.createCriteria().andContentHashEqualTo(chunk.getContentHash());
        if (chunkMapper.selectByExampleWithRowbounds(example, Util.ONE_ROWBOUND).size() > 0) {
            return chunk;
        } else {
            return null;
        }
    }

    /**
     * @TODO: expose to ChunkRepository Domain interface.
     *
     * @param r
     * @return
     */
    public Iterator<Chunk> listChunksByResource(final ChunkedResource r) {
        final AtomicInteger continuation_index = new AtomicInteger(0);
        final AtomicBoolean continuation_include = new AtomicBoolean(true);
        final IteratorConcater<com.chigix.resserver.mybatis.record.Chunk> rows
                = new IteratorConcater<com.chigix.resserver.mybatis.record.Chunk>() {
                    @Override
                    protected Iterator<com.chigix.resserver.mybatis.record.Chunk> nextIterator() {
                        ChunkExample example = new ChunkExample();
                        ChunkExample.Criteria criteria = example.createCriteria()
                                .andParentVersionIdEqualTo(r.getVersionId());
                        criteria.getCriteria().add(
                                new ChunkExampleExtending.OffsetIndexInParent(
                                        continuation_index.get(),
                                        r.getVersionId(),
                                        continuation_include.getAndSet(false)));
                        return chunkMapper.selectByExampleWithRowbounds(example, new RowBounds(0, 100)).iterator();
                    }
                }.addListener((e) -> {
                    continuation_index.set(Integer.valueOf(e.getIndexInParent()));
                });
        return new Iterator<Chunk>() {
            @Override
            public boolean hasNext() {
                return rows.hasNext();
            }

            @Override
            public Chunk next() {
                com.chigix.resserver.mybatis.record.Chunk record = rows.next();
                return newChunk(record.getContentHash(), record.getSize());
            }
        };
    }

}
