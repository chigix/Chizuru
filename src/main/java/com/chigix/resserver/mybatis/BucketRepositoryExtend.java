package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.error.BucketAlreadyExists;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.domain.dao.BucketDao;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface BucketRepositoryExtend extends BucketDao {

    @Override
    public BucketBean createBucket(String name) throws BucketAlreadyExists;

}
