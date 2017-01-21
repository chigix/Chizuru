package com.chigix.resserver.mapdbimpl.iterator;

import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.mapdbimpl.Serializer;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class BucketIterator implements Iterator<Bucket> {

    private final Iterator<Map.Entry<String, String>> it;

    public BucketIterator(Iterator<Map.Entry<String, String>> it) {
        this.it = it;
    }

    @Override
    public boolean hasNext() {
        return it.hasNext();
    }

    @Override
    public Bucket next() {
        Map.Entry<String, String> next = it.next();
        if (next.getValue() == null) {
            throw new NoSuchElementException();
        }
        return Serializer.deserializeBucket(next.getValue());
    }

    @Override
    public void remove() {
        it.remove();
    }

}
