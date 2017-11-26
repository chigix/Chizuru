package com.chigix.resserver.mybatis.mapstruct;

import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.record.Bucket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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

    @Mapping(target = "id", ignore = true)
    Bucket toRecord(BucketBean bucket);

}
