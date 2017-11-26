package com.chigix.resserver.mybatis.mapstruct;

import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.domain.error.UnexpectedLifecycleException;
import com.chigix.resserver.mybatis.bean.AmassedResourceBean;
import com.chigix.resserver.mybatis.bean.ResourceExtension;
import com.chigix.resserver.mybatis.dao.ResourceMapper;
import com.chigix.resserver.mybatis.record.MultipartUpload;
import com.chigix.resserver.mybatis.record.ResourceExample;
import com.chigix.resserver.mybatis.record.Util;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
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

    public com.chigix.resserver.domain.model.multiupload.MultipartUpload fromRecord(MultipartUpload record) {
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
        com.chigix.resserver.domain.model.multiupload.MultipartUpload upload
                = new com.chigix.resserver.domain.model.multiupload.MultipartUpload(
                        resource, record.getUuid(), record.getInitiatedAt());
        return upload;
    }

    @Mappings({
        @Mapping(target = "id", ignore = true)
        ,@Mapping(source = "upload.uploadId", target = "uuid")
        ,@Mapping(source = "upload.initiated", target = "initiatedAt")
        ,@Mapping(source = "upload.resource.key", target = "resourceKey")
        ,@Mapping(source = "upload.resource.versionId", target = "resourceVersion")
        ,@Mapping(source = "innerResource.keyHash", target = "resourceKeyhash")
        ,@Mapping(source = "innerResource.bucket.uuid", target = "bucketUuid")
        ,@Mapping(source = "innerResource.bucket.name", target = "bucketName")
    })
    abstract protected MultipartUpload toRecord(
            com.chigix.resserver.domain.model.multiupload.MultipartUpload upload,
            ResourceExtension innerResource) throws NoSuchBucket;

    public MultipartUpload toRecord(com.chigix.resserver.domain.model.multiupload.MultipartUpload bean) throws NoSuchBucket {
        ResourceExtension resource_bean;
        try {
            resource_bean = (ResourceExtension) bean.getResource();
        } catch (ClassCastException e) {
            throw new UnexpectedLifecycleException("MultipartUpload should contain"
                    + " a reference to a ResourceBean rather than unpersisted bean.");
        }
        return toRecord(bean, resource_bean);
    }
}
