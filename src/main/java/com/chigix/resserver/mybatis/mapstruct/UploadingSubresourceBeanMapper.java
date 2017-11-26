package com.chigix.resserver.mybatis.mapstruct;

import com.chigix.resserver.domain.model.resource.ChunkedResource;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import com.chigix.resserver.mybatis.record.Subresource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@Mapper(
        componentModel = "spring",
        uses = {BeanFactory.class,
            MetaDataMapper.class}
)
public abstract class UploadingSubresourceBeanMapper {

    /**
     * @TODO Refactoring upon mapstruct sharable configuration.
     *
     * @param record
     * @return
     */
    @Mappings({
        @Mapping(target = "metaData", constant = "<Metas></Metas>")
        ,@Mapping(source = "etag", target = "ETag")
        ,@Mapping(target = "chunksAdapter", ignore = true)
        ,@Mapping(target = "bucket", ignore = true)
        ,@Mapping(target = "entityStatus", ignore = true)
    })
    abstract public ChunkedResourceBean fromRecord(final Subresource record);

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
    abstract public Subresource toRecord(ChunkedResource bean) throws NoSuchBucket;

}
