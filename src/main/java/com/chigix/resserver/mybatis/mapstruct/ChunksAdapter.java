package com.chigix.resserver.mybatis.mapstruct;

import com.chigix.resserver.domain.Chunk;
import com.chigix.resserver.mybatis.IteratorConcater;
import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import com.chigix.resserver.mybatis.dao.ChunkMapper;
import com.chigix.resserver.mybatis.record.ChunkExample;
import com.chigix.resserver.mybatis.record.ChunkExampleExtending;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ibatis.session.RowBounds;

/**
 * @TODO: Remove.
 *
 * @author Richard Lea <chigix@zoho.com>
 * @deprecated
 */
@Deprecated
public interface ChunksAdapter {

    Iterator<Chunk> iterateChunks();

    Iterator<Chunk> iterateChunks(int start_index);

    public static class DefaultChunksAdapter implements ChunksAdapter {

        private final ChunkMapper mapper;

        private final String parentVersionId;

        private final ChunkBeanMapper chunkBeanMapper;

        public DefaultChunksAdapter(ChunkedResourceBean parent_resource,
                ChunkMapper chunkMapper,
                ChunkBeanMapper chunkBeanMapper) {
            this.parentVersionId = parent_resource.getVersionId();
            this.mapper = chunkMapper;
            this.chunkBeanMapper = chunkBeanMapper;
        }

        @Override
        public Iterator<Chunk> iterateChunks() {
            return iterateChunks(0);
        }

        @Override
        public Iterator<Chunk> iterateChunks(final int start_index) {
            final AtomicInteger continuation_index = new AtomicInteger(0);
            final AtomicBoolean continuation_include = new AtomicBoolean(true);
            final Iterator<com.chigix.resserver.mybatis.record.Chunk> records = new IteratorConcater<com.chigix.resserver.mybatis.record.Chunk>() {
                @Override
                protected Iterator<com.chigix.resserver.mybatis.record.Chunk> nextIterator() {
                    ChunkExample example = new ChunkExample();
                    example.createCriteria().andParentVersionIdEqualTo(parentVersionId)
                            .getCriteria().add(
                                    new ChunkExampleExtending.OffsetIndexInParent(
                                            continuation_index.get(),
                                            parentVersionId,
                                            continuation_include.getAndSet(false)));
                    return mapper.selectByExampleWithRowbounds(example, new RowBounds(0, 20)).iterator();
                }
            }.addListener((chunk) -> {
                continuation_index.set(Integer.valueOf(chunk.getIndexInParent()));
            });
            return new Iterator<Chunk>() {
                @Override
                public boolean hasNext() {
                    return records.hasNext();
                }

                @Override
                public Chunk next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return chunkBeanMapper.fromRecord(records.next());
                }
            };
        }
    }

    public static class EmptyChunksAdapter implements ChunksAdapter {

        public EmptyChunksAdapter() {
        }

        @Override
        public Iterator<Chunk> iterateChunks() {
            return Collections.emptyIterator();
        }

        @Override
        public Iterator<Chunk> iterateChunks(int start_index) {
            return Collections.emptyIterator();
        }

    }

}
