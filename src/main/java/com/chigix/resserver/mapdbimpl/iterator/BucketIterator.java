package com.chigix.resserver.mapdbimpl.iterator;

import com.chigix.resserver.entity.Bucket;
import java.util.Iterator;
import java.util.Map;
import org.joda.time.DateTime;

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
        return new Bucket(next.getKey(), DateTime.parse(next.getValue()));
    }

    @Override
    public void remove() {
        it.remove();
    }

}
