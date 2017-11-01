package com.chigix.resserver.mybatis.mapstruct;

import com.chigix.resserver.domain.model.resource.ChunkedResource;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import com.chigix.resserver.mybatis.dao.ResourceMapper;
import com.chigix.resserver.mybatis.record.Resource;
import com.chigix.resserver.mybatis.record.ResourceExample;
import com.chigix.resserver.mybatis.record.Subresource;
import com.chigix.resserver.mybatis.record.Util;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@Mapper(componentModel = "spring")
public abstract class UploadingSubresourceBeanMapper {

    @Autowired
    private UploadingResourceBeanMapper uploadingResourceBeanMapper;

    @Autowired
    @Qualifier("uploadingResourceMapper")
    private ResourceMapper uploadingResourceDao;

    @Autowired
    private ResourceBeanMapper resourceBeanMapper;

    /**
     * @TODO refactor upon mapstruct sharable configuration.
     *
     * @param record
     * @return
     */
    public ChunkedResourceBean fromRecord(final Subresource record) {
        Resource base_record = new Resource();
        base_record.setEtag(record.getEtag());
        base_record.setKey(record.getKey());
        base_record.setLastModified(record.getLastModified());
        base_record.setSize(record.getSize());
        base_record.setStorageClass(record.getStorageClass());
        base_record.setType(ChunkedResourceBean.TYPE);
        base_record.setVersionId(record.getVersionId());
        base_record.setMetaData("<Metas></Metas>");
        ChunkedResourceBean bean = (ChunkedResourceBean) resourceBeanMapper
                .fromRecord(base_record);
        bean.setParentResource(() -> {
            ResourceExample example = new ResourceExample();
            example.createCriteria().andVersionIdEqualTo(
                    record.getParentVersionId());
            try {
                return uploadingResourceBeanMapper.fromRecord(
                        uploadingResourceDao.selectByExampleWithBLOBsWithRowbounds(
                                example, Util.ONE_ROWBOUND).get(0));
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        });
        return bean;
    }

    public Subresource toRecord(ChunkedResource bean) throws NoSuchBucket {
        Subresource record = new Subresource();
        record.setEtag(bean.getETag());
        record.setKey(bean.getKey());
        record.setLastModified(bean.getLastModified());
        record.setSize(bean.getSize());
        record.setStorageClass(bean.getStorageClass());
        record.setVersionId(bean.getVersionId());
        record.setType(ChunkedResourceBean.TYPE);
        return record;
    }
}
