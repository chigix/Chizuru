package com.chigix.resserver.mapdbimpl.iterator;

import com.chigix.resserver.entity.Chunk;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ChunksIterator implements Iterator<Chunk> {

    private ConcurrentMap<String, String> nextChunkList;

    @Override
    public boolean hasNext() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Chunk next() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void remove() {
        Iterator.super.remove(); //To change body of generated methods, choose Tools | Templates.
    }

}
