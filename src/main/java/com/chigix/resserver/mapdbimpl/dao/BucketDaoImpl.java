package com.chigix.resserver.mapdbimpl.dao;

import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.dao.BucketDao;
import com.chigix.resserver.entity.error.BucketAlreadyExists;
import com.chigix.resserver.entity.error.BucketAlreadyOwnedByYou;
import com.chigix.resserver.entity.error.NoSuchBucket;
import com.chigix.resserver.mapdbimpl.BucketInStorage;
import com.chigix.resserver.mapdbimpl.Serializer;
import com.chigix.resserver.mapdbimpl.iterator.BucketIterator;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;
import org.mapdb.DB;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class BucketDaoImpl implements BucketDao {

    private final DB db;

    public BucketDaoImpl(DB db) {
        this.db = db;
    }

    /**
     *
     * @param name
     * @return BucketInStorage
     * @throws NoSuchBucket
     */
    @Override
    public Bucket findBucketByName(String name) throws NoSuchBucket {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        String xml = ((ConcurrentMap<String, String>) db.hashMap(BucketKeys.BUCKET_DB).open()).get(name);
        if (xml == null) {
            throw new NoSuchBucket(name);
        }
        return Serializer.deserializeBucket(xml);
    }

    @Override
    public Iterator<Bucket> iteratorBucket() {
        return new BucketIterator(((ConcurrentMap<String, String>) db.hashMap(BucketKeys.BUCKET_DB).open()).entrySet().iterator());
    }

    @Override
    public Bucket createBucket(String name) throws BucketAlreadyExists {
        ConcurrentMap<String, String> map = (ConcurrentMap<String, String>) db.hashMap(BucketKeys.BUCKET_DB).open();
        BucketInStorage b = new BucketInStorage(name);
        String xml = Serializer.serializeBucket(b);
        if (map.putIfAbsent(name, xml) == null) {
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
    @Override
    public Bucket deleteBucketByName(String name) throws NoSuchBucket {
        String xml = ((ConcurrentMap<String, String>) db.hashMap(BucketKeys.BUCKET_DB).open()).remove(name);
        if (xml == null) {
            throw new NoSuchBucket(name);
        } else {
            db.commit();
            return Serializer.deserializeBucket(xml);
        }
    }

}
