package com.chigix.resserver.mybatis.mapstruct;

import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.record.Bucket;
import org.mapstruct.Mapper;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@Mapper(componentModel = "spring")
public interface BucketBeanMapper {

    default BucketBean fromRecord(Bucket record) {
        if (record == null) {
            return null;
        }
        return new BucketBean(record.getName(), record.getCreationTime(), record.getUuid());
    }

    default Bucket toRecord(BucketBean bucket) {
        Bucket record = new Bucket();
        record.setName(bucket.getName());
        record.setCreationTime(bucket.getCreationTime());
        record.setUuid(bucket.getUuid());
        return record;
    }

}
