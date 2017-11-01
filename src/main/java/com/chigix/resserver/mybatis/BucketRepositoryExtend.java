package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.error.BucketAlreadyExists;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.domain.model.bucket.BucketRepository;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface BucketRepositoryExtend extends BucketRepository {

    @Override
    public BucketBean createBucket(String name) throws BucketAlreadyExists;

}
