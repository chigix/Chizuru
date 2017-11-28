package com.chigix.resserver.mybatis.mapstruct;

import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.domain.model.resource.ChunkedResource;
import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import com.chigix.resserver.mybatis.record.Subresource;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@Mapper(
        config = SubresourceBeanMapper.CentralConfig.class
)
public interface SubresourceBeanMapper {

    @MapperConfig(
            componentModel = "spring",
            uses = {BeanFactory.class,
                ChunksAdapterMapper.class,
                MetaDataMapper.class}
    )
    public interface CentralConfig {

        @Mappings({
            @Mapping(target = "id", ignore = true)
            ,@Mapping(target = "parentVersionId", ignore = true)
            ,@Mapping(target = "type", expression = "java(com.chigix.resserver.mybatis.bean.ChunkedResourceBean.TYPE)")
            ,@Mapping(source = "ETag", target = "etag")
            ,@Mapping(source = "key", target = "indexInParent")
            ,@Mapping(target = "rangeStartByte", ignore = true)
            ,@Mapping(target = "rangeEndByte", ignore = true)
            ,@Mapping(target = "rangeStart4byte", ignore = true)
            ,@Mapping(target = "rangeEnd4byte", ignore = true)
        })
        Subresource toRecord(ChunkedResource bean) throws NoSuchBucket;

        @Mappings({
            @Mapping(target = "metaData", constant = "<Metas></Metas>")
            ,@Mapping(source = "etag", target = "ETag")
            ,@Mapping(source = "versionId", target = "chunksAdapter")
            ,@Mapping(target = "bucket", ignore = true)
            ,@Mapping(target = "entityStatus", ignore = true)
        })
        ChunkedResourceBean fromRecord(final Subresource record);
    }

    @InheritConfiguration
    Subresource toRecord(ChunkedResourceBean bean) throws NoSuchBucket;

    @InheritConfiguration
    ChunkedResourceBean fromRecord(final Subresource record);

}
