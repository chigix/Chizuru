package com.chigix.resserver.mybatis.mapstruct;

import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.dao.BucketMapper;
import com.chigix.resserver.mybatis.record.BucketExample;
import com.chigix.resserver.mybatis.record.Util;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class BucketAdapter {

    private final String uuid;

    private final BucketMapper mapper;
    private final BucketBeanMapper beanMapper;

    private BucketBean adapted = null;

    public BucketAdapter(String uuid, BucketMapper mapper, BucketBeanMapper bucketBeanMapper) {
        this.uuid = uuid;
        this.mapper = mapper;
        this.beanMapper = bucketBeanMapper;
    }

    public BucketBean getBucket() throws NoSuchBucket {
        if (adapted == null) {
            BucketExample example = new BucketExample();
            example.createCriteria().andUuidEqualTo(uuid);
            try {
                adapted = beanMapper.fromRecord(mapper.selectByExampleWithRowbounds(example, Util.ONE_ROWBOUND).get(0));
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchBucket(uuid);
            }
        }
        return adapted;
    }

}
