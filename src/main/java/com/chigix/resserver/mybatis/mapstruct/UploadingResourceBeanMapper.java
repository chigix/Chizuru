package com.chigix.resserver.mybatis.mapstruct;

import com.chigix.resserver.domain.AmassedResource;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.mybatis.bean.AmassedResourceBean;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.bean.ResourceExtension;
import com.chigix.resserver.mybatis.dao.BucketMapper;
import com.chigix.resserver.mybatis.record.Resource;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@Mapper(componentModel = "spring")
public abstract class UploadingResourceBeanMapper {

    @Autowired
    private BucketMapper bucketDao;
    @Autowired
    private BucketBeanMapper bucketBeanMapper;

    @Autowired
    private MetaDataMapper metadataMapper;

    public AmassedResourceBean fromRecord(Resource record) {
        AmassedResourceBean r = new AmassedResourceBean(record.getKey(), record.getVersionId(), record.getKeyhash());
        r.setBucket(new BucketAdapter(record.getBucketUuid(), bucketDao, bucketBeanMapper));
        r.setSubresourceAdapter(new SubresourcesAdapter.EmptyAdapter());
        r.setETag(record.getEtag());
        r.setLastModified(record.getLastModified());
        r.setMetaData(metadataMapper.fromXml(record.getMetaData()));
        r.setSize(record.getSize());
        return r;
    }

    public Resource toRecord(AmassedResource bean) throws NoSuchBucket {
        BucketBean bb;
        try {
            bb = (BucketBean) bean.getBucket();
        } catch (ClassCastException classCastException) {
            throw new RuntimeException("Unexpected!! Unpersisted Bucket Bean Object.");
        }
        Resource record = new Resource();
        record.setBucketUuid(bb.getUuid());
        record.setEtag(bean.getETag());
        record.setKey(bean.getKey());
        record.setLastModified(bean.getLastModified());
        record.setMetaData(metadataMapper.toXml(bean.snapshotMetaData()));
        record.setSize(bean.getSize());
        record.setStorageClass(bean.getStorageClass());
        record.setType(AmassedResourceBean.TYPE);
        record.setVersionId(bean.getVersionId());
        if (bean instanceof ResourceExtension) {
            record.setKeyhash(((ResourceExtension) bean).getKeyHash());
        } else {
            record.setKeyhash(ResourceExtension.hashKey(bb.getUuid(), bean.getKey()));
        }
        return record;
    }

}
