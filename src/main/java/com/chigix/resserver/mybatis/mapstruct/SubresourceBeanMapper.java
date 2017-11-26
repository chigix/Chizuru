package com.chigix.resserver.mybatis.mapstruct;

import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import com.chigix.resserver.mybatis.dao.ResourceMapper;
import com.chigix.resserver.mybatis.record.Subresource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@Mapper(
        componentModel = "spring",
        uses = {BeanFactory.class,
            ChunksAdapterMapper.class,
            MetaDataMapper.class}
)
public abstract class SubresourceBeanMapper {

    @Autowired
    protected ResourceBeanMapper resourceBeanMapper;

    @Autowired
    protected ResourceMapper resourceDao;

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
    abstract public Subresource toRecord(ChunkedResourceBean bean) throws NoSuchBucket;

    @Mappings({
        @Mapping(target = "metaData", constant = "<Metas></Metas>")
        ,@Mapping(source = "etag", target = "ETag")
        ,@Mapping(source = "versionId", target = "chunksAdapter")
        ,@Mapping(target = "bucket", ignore = true)
        ,@Mapping(target = "entityStatus", ignore = true)
    })
    abstract public ChunkedResourceBean fromRecord(final Subresource record);

}
