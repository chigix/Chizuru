package com.chigix.resserver.mybatis.mapstruct;

import com.chigix.resserver.domain.model.resource.ChunkedResource;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import com.chigix.resserver.mybatis.record.Subresource;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@Mapper(config = SubresourceBeanMapper.CentralConfig.class)
public interface UploadingSubresourceBeanMapper {

    @InheritConfiguration
    ChunkedResourceBean fromRecord(final Subresource record);

    @InheritConfiguration
    Subresource toRecord(ChunkedResource bean) throws NoSuchBucket;

}
