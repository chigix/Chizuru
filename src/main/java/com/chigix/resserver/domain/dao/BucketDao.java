package com.chigix.resserver.domain.dao;

import com.chigix.resserver.domain.Bucket;
import com.chigix.resserver.domain.error.BucketAlreadyExists;
import com.chigix.resserver.domain.error.NoSuchBucket;
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

}
