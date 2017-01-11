package com.chigix.resserver.entity.dao;

import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.db.BucketKeys;
import com.chigix.resserver.entity.error.BucketAlreadyExists;
import com.chigix.resserver.entity.error.BucketAlreadyOwnedByYou;
import com.chigix.resserver.entity.error.NoSuchBucket;
import com.chigix.resserver.entity.iterator.BucketIterator;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;
import org.joda.time.DateTime;
import org.mapdb.DB;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class BucketDao {

    private final DB db;

    public BucketDao(DB db) {
        this.db = db;
    }

    public Bucket findBucketByName(String name) throws NoSuchBucket {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        String datetime = (String) db.hashMap(BucketKeys.CREATED_VALUE).open().get(name);
        if (datetime == null) {
            throw new NoSuchBucket(name);
        }
        return new Bucket(name, DateTime.parse(datetime));
    }

    public Iterator<Bucket> iteratorBucket() {
        return new BucketIterator(((ConcurrentMap<String, String>) db.hashMap(BucketKeys.CREATED_VALUE).open()).entrySet().iterator());
    }

    public Bucket createBucket(String name) throws BucketAlreadyExists {
        Bucket b = new Bucket(name);
        if (((ConcurrentMap<String, String>) db.hashMap(BucketKeys.CREATED_VALUE).open())
                .putIfAbsent(name, b.getCreationTime().toString()) == null) {
            db.commit();
            return b;
        } else {
            throw new BucketAlreadyOwnedByYou();
        }
    }

    /**
     *
     * @param name
     * @return
     * @throws NoSuchBucket
     */
    public Bucket deleteBucketByName(String name) throws NoSuchBucket {
        String datetime = ((ConcurrentMap<String, String>) db.hashMap(BucketKeys.CREATED_VALUE).open())
                .remove(name);
        if (datetime == null) {
            throw new NoSuchBucket(name);
        } else {
            return new Bucket(name, DateTime.parse(datetime));
        }
    }

}
