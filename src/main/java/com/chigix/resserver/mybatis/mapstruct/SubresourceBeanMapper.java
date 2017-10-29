package com.chigix.resserver.mybatis.mapstruct;

import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.mybatis.bean.AmassedResourceBean;
import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import com.chigix.resserver.mybatis.dao.ResourceMapper;
import com.chigix.resserver.mybatis.record.Resource;
import com.chigix.resserver.mybatis.record.ResourceExample;
import com.chigix.resserver.mybatis.record.Subresource;
import com.chigix.resserver.mybatis.record.Util;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@Mapper(componentModel = "spring")
public abstract class SubresourceBeanMapper {

    @Autowired
    protected ResourceBeanMapper resourceBeanMapper;

    @Autowired
    protected ResourceMapper resourceDao;

    public Subresource toRecord(ChunkedResourceBean bean) throws NoSuchBucket {
        Resource record_base = resourceBeanMapper.toRecord(bean);
        Subresource record = new Subresource();
        record.setEtag(record_base.getEtag());
        record.setId(record_base.getId());
        record.setKey(record_base.getKey());
        record.setLastModified(record_base.getLastModified());
        record.setSize(record_base.getSize());
        record.setStorageClass(record_base.getStorageClass());
        record.setVersionId(record_base.getVersionId());
        record.setParentVersionId(bean.getParentResource().getVersionId());
        record.setType(ChunkedResourceBean.TYPE);
        return record;
    }

    public ChunkedResourceBean fromRecord(final Subresource record) {
        Resource record_base = new Resource();
        record_base.setEtag(record.getEtag());
        record_base.setId(record.getId());
        record_base.setKey(record.getKey());
        record_base.setLastModified(record.getLastModified());
        record_base.setSize(record.getSize());
        record_base.setStorageClass(record.getStorageClass());
        record_base.setVersionId(record.getVersionId());
        record_base.setType(ChunkedResourceBean.TYPE);
        record_base.setMetaData("<Metas></Metas>");
        ChunkedResourceBean resource = (ChunkedResourceBean) resourceBeanMapper
                .fromRecord(record_base);
        resource.setParentResource(() -> {
            ResourceExample example = new ResourceExample();
            example.createCriteria().andVersionIdEqualTo(record.getParentVersionId());
            return (AmassedResourceBean) resourceBeanMapper.fromRecord(
                    resourceDao.selectByExampleWithBLOBsWithRowbounds(
                            example, Util.ONE_ROWBOUND).get(0));
        });
        return resource;
    }

}
