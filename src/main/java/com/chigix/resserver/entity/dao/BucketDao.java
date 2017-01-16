package com.chigix.resserver.entity.dao;

import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.ModelProxy;
import com.chigix.resserver.entity.error.BucketAlreadyExists;
import com.chigix.resserver.entity.error.NoSuchBucket;
import java.util.Iterator;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface BucketDao {

    Bucket findBucketByName(String name) throws NoSuchBucket;

    Iterator<Bucket> iteratorBucket();

    Bucket createBucket(String name) throws BucketAlreadyExists;

    Bucket deleteBucketByName(String name) throws NoSuchBucket;

    default ModelProxy<Bucket> newProxied(final String bucketName) {
        return () -> {
            try {
                return findBucketByName(bucketName);
            } catch (Exception ex) {
                throw new ModelProxy.ProxiedException(ex);
            }
        };
    }
}
