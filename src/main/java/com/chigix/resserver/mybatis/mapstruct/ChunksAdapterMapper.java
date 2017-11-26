package com.chigix.resserver.mybatis.mapstruct;

import com.chigix.resserver.domain.model.chunk.Chunk;
import com.chigix.resserver.mybatis.IteratorConcater;
import com.chigix.resserver.mybatis.dao.ChunkMapper;
import com.chigix.resserver.mybatis.record.ChunkExample;
import com.chigix.resserver.mybatis.record.ChunkExampleExtending;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ibatis.session.RowBounds;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@Mapper(componentModel = "spring")
public class ChunksAdapterMapper {

    @Autowired
    private ChunkBeanMapper chunkBeanMapper;

    @Autowired
    private ChunkMapper mapper;

    public ChunksAdapter asAdapter(final String resource_version_id) {
        return new ChunksAdapter() {
            @Override
            public Iterator<Chunk> iterateChunks() {
                return iterateChunks(0);
            }

            @Override
            public Iterator<Chunk> iterateChunks(int start_index) {
                final AtomicInteger continuation_index = new AtomicInteger(0);
                final AtomicBoolean continuation_include = new AtomicBoolean(true);
                final Iterator<com.chigix.resserver.mybatis.record.Chunk> records = new IteratorConcater<com.chigix.resserver.mybatis.record.Chunk>() {
                    @Override
                    protected Iterator<com.chigix.resserver.mybatis.record.Chunk> nextIterator() {
                        ChunkExample example = new ChunkExample();
                        example.createCriteria().andParentVersionIdEqualTo(resource_version_id)
                                .getCriteria().add(
                                        new ChunkExampleExtending.OffsetIndexInParent(
                                                continuation_index.get(),
                                                resource_version_id,
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
        };
    }

}
