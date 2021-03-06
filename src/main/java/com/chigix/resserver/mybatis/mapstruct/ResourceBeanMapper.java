package com.chigix.resserver.mybatis.mapstruct;

import com.chigix.resserver.domain.model.resource.AmassedResource;
import com.chigix.resserver.domain.model.resource.ChunkedResource;
import com.chigix.resserver.domain.model.resource.Resource;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.mybatis.ResourceRepositoryExtend;
import com.chigix.resserver.mybatis.bean.AmassedResourceBean;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import com.chigix.resserver.mybatis.bean.ResourceExtension;
import com.chigix.resserver.mybatis.dao.BucketMapper;
import java.security.InvalidParameterException;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@Mapper(componentModel = "spring")
public abstract class ResourceBeanMapper {

    @Autowired
    private BucketBeanMapper bucketBeanMapper;

    @Autowired
    private BucketMapper bucketDao;

    @Autowired
    private MetaDataMapper metaDataMapper;

    @Deprecated
    @Autowired
    private ChunksAdapterMapper chunksAdapterMapper;

    /**
     * @TODO remove
     */
    @Autowired
    private ResourceRepositoryExtend resourceRepository;

    public Resource fromRecord(com.chigix.resserver.mybatis.record.Resource record) {
        final Resource result;
        switch (record.getType()) {
            case AmassedResourceBean.TYPE:
                result = createAmassedResourceBean(record);
                break;
            case ChunkedResourceBean.TYPE:
                result = createChunkedResourceBean(record);
                break;
            default:
                throw new RuntimeException("Unexpected resource type: [" + record.getType() + "}");
        }
        result.setETag(record.getEtag());
        result.setLastModified(record.getLastModified());
        result.setSize(record.getSize());
        ((ResourceExtension) result).setMetaData(metaDataMapper.fromXml(record.getMetaData()));
        return result;
    }

    private ChunkedResourceBean createChunkedResourceBean(com.chigix.resserver.mybatis.record.Resource record) {
        ChunkedResourceBean resource = new ChunkedResourceBean(record.getKey(), record.getVersionId(), record.getKeyhash());
        resource.setBucket(new BucketAdapter(record.getBucketUuid(), bucketDao, bucketBeanMapper));
        resource.setChunksAdapter(chunksAdapterMapper.asAdapter(resource.getVersionId()));
        return resource;
    }

    private AmassedResourceBean createAmassedResourceBean(com.chigix.resserver.mybatis.record.Resource record) {
        AmassedResourceBean resource = new AmassedResourceBean(record.getKey(), record.getVersionId(), record.getKeyhash());
        resource.setBucket(new BucketAdapter(record.getBucketUuid(), bucketDao, bucketBeanMapper));
        resource.setSubresourceAdapter(
                new SubresourcesAdapter.DefaultSubresourcesAdapter(
                        resource, resourceRepository));
        return resource;
    }

    public com.chigix.resserver.mybatis.record.Resource toRecord(Resource bean) throws NoSuchBucket {
        com.chigix.resserver.mybatis.record.Resource record = new com.chigix.resserver.mybatis.record.Resource();
        BucketBean bucket;
        try {
            bucket = (BucketBean) bean.getBucket();
        } catch (ClassCastException ex) {
            throw new InvalidParameterException("Bucket inside this resource is not a persisted bean object.");
        }
        record.setBucketUuid(bucket.getUuid());
        record.setEtag(bean.getETag());
        record.setKey(bean.getKey());
        record.setLastModified(bean.getLastModified());
        record.setSize(bean.getSize());
        record.setStorageClass(bean.getStorageClass());
        record.setVersionId(bean.getVersionId());
        record.setMetaData(metaDataMapper.toXml(bean.snapshotMetaData()));
        if (bean instanceof ResourceExtension) {
            record.setKeyhash(((ResourceExtension) bean).getKeyHash());
        } else {
            record.setKeyhash(ResourceExtension.hashKey(bucket.getUuid(), bean.getKey()));
        }
        if (bean instanceof ChunkedResource) {
            record.setType(ChunkedResourceBean.TYPE);
        } else if (bean instanceof AmassedResource) {
            record.setType(AmassedResourceBean.TYPE);
        }
        return record;
    }

}
