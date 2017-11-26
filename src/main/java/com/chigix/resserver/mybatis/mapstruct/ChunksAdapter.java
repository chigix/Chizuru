package com.chigix.resserver.mybatis.mapstruct;

import com.chigix.resserver.domain.model.chunk.Chunk;
import java.util.Collections;
import java.util.Iterator;

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
