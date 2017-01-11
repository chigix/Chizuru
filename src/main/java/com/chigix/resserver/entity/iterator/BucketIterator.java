package com.chigix.resserver.entity.iterator;

import com.chigix.resserver.entity.Bucket;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
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

    @Override
    public void forEachRemaining(Consumer<? super Bucket> action) {
        Iterator.super.forEachRemaining(action); //To change body of generated methods, choose Tools | Templates.
    }

}
