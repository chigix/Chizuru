package com.chigix.resserver.mybatis.mapstruct;

import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.domain.error.UnexpectedLifecycleException;
import com.chigix.resserver.mybatis.bean.AmassedResourceBean;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.bean.ResourceExtension;
import com.chigix.resserver.mybatis.dao.ResourceMapper;
import com.chigix.resserver.mybatis.record.MultipartUpload;
import com.chigix.resserver.mybatis.record.ResourceExample;
import com.chigix.resserver.mybatis.record.Util;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@Mapper(componentModel = "spring")
public abstract class MultipartUploadBeanMapper {

    @Autowired
    private UploadingResourceBeanMapper resourceBeanMapper;

    @Autowired
    @Qualifier("uploadingResourceMapper")
    private ResourceMapper resourceDao;

    public com.chigix.resserver.domain.MultipartUpload fromRecord(MultipartUpload record) {
        ResourceExample example = new ResourceExample();
        example.createCriteria().andVersionIdEqualTo(record.getResourceVersion());
        AmassedResourceBean resource;
        try {
            resource = resourceBeanMapper.fromRecord(
                    resourceDao.selectByExampleWithBLOBsWithRowbounds(
                            example, Util.ONE_ROWBOUND).get(0));
        } catch (IndexOutOfBoundsException e) {
            resource = null;
        }
        com.chigix.resserver.domain.MultipartUpload upload
                = new com.chigix.resserver.domain.MultipartUpload(
                        resource, record.getUuid(), record.getInitiatedAt());
        return upload;
    }

    public MultipartUpload toRecord(com.chigix.resserver.domain.MultipartUpload bean) throws NoSuchBucket {
        MultipartUpload record = new MultipartUpload();
        ResourceExtension resource_bean;
        try {
            resource_bean = (ResourceExtension) bean.getResource();
        } catch (ClassCastException e) {
            throw new UnexpectedLifecycleException("MultipartUpload should contain"
                    + " a reference to a ResourceBean rather than unpersisted bean.");
        }
        BucketBean bb = resource_bean.getBucket();
        record.setBucketName(bb.getName());
        record.setBucketUuid(bb.getUuid());
        record.setInitiatedAt(bean.getInitiated());
        record.setResourceKey(bean.getResource().getKey());
        record.setResourceKeyhash(resource_bean.getKeyHash());
        record.setResourceVersion(bean.getResource().getVersionId());
        record.setUuid(bean.getUploadId());
        return record;
    }
}
