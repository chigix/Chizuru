package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.model.resource.AmassedResource;
import com.chigix.resserver.domain.model.bucket.Bucket;
import com.chigix.resserver.domain.model.resource.ChunkedResource;
import com.chigix.resserver.domain.model.multiupload.MultipartUpload;
import com.chigix.resserver.domain.error.InvalidPart;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.domain.error.NoSuchUpload;
import com.chigix.resserver.domain.error.UnexpectedLifecycleException;
import com.chigix.resserver.mybatis.bean.AmassedResourceBean;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import com.chigix.resserver.mybatis.dao.ChunkMapper;
import com.chigix.resserver.mybatis.dao.MultipartUploadMapper;
import com.chigix.resserver.mybatis.dao.ResourceMapper;
import com.chigix.resserver.mybatis.dao.SubresourceMapper;
import com.chigix.resserver.mybatis.mapstruct.MultipartUploadBeanMapper;
import com.chigix.resserver.mybatis.mapstruct.UploadingResourceBeanMapper;
import com.chigix.resserver.mybatis.mapstruct.UploadingSubresourceBeanMapper;
import com.chigix.resserver.mybatis.record.MultipartUploadExample;
import com.chigix.resserver.mybatis.record.Resource;
import com.chigix.resserver.mybatis.record.ResourceExample;
import com.chigix.resserver.mybatis.record.Subresource;
import com.chigix.resserver.mybatis.record.SubresourceExample;
import com.chigix.resserver.mybatis.record.Util;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.chigix.resserver.domain.model.multiupload.MultipartUploadRepository;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class MultipartUploadRepositoryImpl implements MultipartUploadRepository {

    private final MultipartUploadMapper uploadMapper;

    private final ResourceMapper uploadingResourceDao;
    private final SubresourceMapper uploadingSubResourceDao;

    @Autowired
    private MultipartUploadBeanMapper multipartUploadBeanMapper;

    @Autowired
    private UploadingResourceBeanMapper resourceBeanMapper;

    @Autowired
    private UploadingSubresourceBeanMapper subResourceBeanMapper;

    public MultipartUploadRepositoryImpl(MultipartUploadMapper uploadMapper,
            ChunkMapper chunkMapper,
            ResourceMapper uploadingResourceMapper,
            SubresourceMapper uploadingSubResourceMapper) {
        this.uploadMapper = uploadMapper;
        this.uploadingResourceDao = uploadingResourceMapper;
        this.uploadingSubResourceDao = uploadingSubResourceMapper;
    }

    @Override
    public MultipartUpload initiateUpload(AmassedResource resource) throws NoSuchBucket {
        BucketBean bb;
        try {
            bb = (BucketBean) resource.getBucket();
        } catch (ClassCastException classCastException) {
            throw new InvalidParameterException("Bucket inside this resource is not a persisted bean object.");
        }
        final AtomicReference<AmassedResource> resource_ref = new AtomicReference<>();
        resource_ref.set(resource);
        MultipartUpload u = new MultipartUpload(resource) {
            @Override
            public AmassedResource getResource() {
                return resource_ref.get();
            }

        };
        Resource record = resourceBeanMapper.toRecord(resource);
        uploadingResourceDao.mergeUploadingResource(record);
        AmassedResourceBean resource_bean = (AmassedResourceBean) resourceBeanMapper.fromRecord(record);
        resource_bean.setBucket(bb);
        resource_ref.set(resource_bean);
        uploadMapper.insert(multipartUploadBeanMapper.toRecord(u));
        return u;
    }

    @Override
    public MultipartUpload findUpload(String uploadId) throws NoSuchUpload {
        MultipartUploadExample upload_example = new MultipartUploadExample();
        upload_example.createCriteria().andUuidEqualTo(uploadId);
        com.chigix.resserver.mybatis.record.MultipartUpload upload_record;
        try {
            upload_record = uploadMapper.selectByExampleWithRowbounds(upload_example, Util.ONE_ROWBOUND).get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new NoSuchUpload();
        }
        ResourceExample resource_example = new ResourceExample();
        resource_example.createCriteria()
                .andKeyhashEqualTo(upload_record.getResourceKeyhash())
                .andVersionIdEqualTo(upload_record.getResourceVersion());
        Resource b = uploadingResourceDao.selectByExampleWithBLOBsWithRowbounds(resource_example, Util.ONE_ROWBOUND).get(0);
        if (b == null) {
            MultipartUploadExample delete_by_uuid = new MultipartUploadExample();
            delete_by_uuid.createCriteria().andUuidEqualTo(upload_record.getUuid());
            uploadMapper.deleteByExample(delete_by_uuid);
            throw new NoSuchUpload();
        }
        AmassedResourceBean resource_bean = (AmassedResourceBean) resourceBeanMapper.fromRecord(b);
        resource_bean.setBucket(new BucketBean(upload_record.getBucketName(),
                new DateTime(DateTimeZone.UTC), upload_record.getBucketUuid()));
        MultipartUpload u = new MultipartUpload(resource_bean, uploadId, upload_record.getInitiatedAt());
        return u;
    }

    @Override
    public ChunkedResource findUploadPart(MultipartUpload upload, String partNumber, String etag) throws InvalidPart {
        String key = partNumber;
        if (key.length() > 32) {
            key = key.substring(key.length() - 32);
        }
        SubresourceExample example = new SubresourceExample();
        example.createCriteria().andKeyEqualTo(key).andParentVersionIdEqualTo(upload.getResource().getVersionId()).andEtagEqualTo(etag);
        List<Subresource> records = uploadingSubResourceDao.selectByExampleWithRowbounds(example, Util.ONE_ROWBOUND);
        if (records.size() < 1) {
            throw new InvalidPart();
        }
        ChunkedResourceBean resource_bean = subResourceBeanMapper.fromRecord(records.get(0));
        try {
            resource_bean.setBucket((BucketBean) upload.getResource().getBucket());
        } catch (NoSuchBucket ex) {
            throw new RuntimeException("Unexpected!!!!AmassedResource's BucketBean object isn't put previously.");
        }
        resource_bean.setParentResource((AmassedResourceBean) upload.getResource());
        return resource_bean;
    }

    @Override
    public void removeUpload(MultipartUpload upload) throws NoSuchUpload {
        MultipartUploadExample upload_example = new MultipartUploadExample();
        upload_example.createCriteria().andUuidEqualTo(upload.getUploadId());
        int upload_count = uploadMapper.deleteByExample(upload_example);
        ResourceExample example = new ResourceExample();
        example.createCriteria().andVersionIdEqualTo(upload.getResource().getVersionId());
        int resource_count = uploadingResourceDao.deleteByExample(example);
        if (upload_count < 1 || resource_count < 1) {
            throw new NoSuchUpload();
        }
    }

    @Override
    public Iterator<MultipartUpload> listUploadsByBucket(Bucket b) throws NoSuchBucket {
        BucketBean bb;
        if (b instanceof BucketBean) {
            bb = (BucketBean) b;
        } else {
            throw new UnexpectedLifecycleException("Bucket for this query is expected as a persisted bean.");
        }
        MultipartUploadExample example = new MultipartUploadExample();
        example.createCriteria().andBucketUuidEqualTo(bb.getUuid());
        // @TODO should use RowBounds in select.
        final Iterator<com.chigix.resserver.mybatis.record.MultipartUpload> rows = uploadMapper.selectByExample(example).iterator();
        return new Iterator<MultipartUpload>() {
            @Override
            public boolean hasNext() {
                return rows.hasNext();
            }

            @Override
            public MultipartUpload next() {
                com.chigix.resserver.mybatis.record.MultipartUpload row = rows.next();
                ResourceExample example = new ResourceExample();
                example.createCriteria().andVersionIdEqualTo(row.getResourceVersion());
                Resource b = uploadingResourceDao.selectByExampleWithBLOBsWithRowbounds(example, Util.ONE_ROWBOUND).get(0);
                if (b == null) {
                    MultipartUploadExample delete_by_uuid = new MultipartUploadExample();
                    delete_by_uuid.createCriteria().andUuidEqualTo(row.getUuid());
                    uploadMapper.deleteByExample(delete_by_uuid);
                    return next();
                }
                AmassedResourceBean resource_bean = (AmassedResourceBean) resourceBeanMapper.fromRecord(b);
                resource_bean.setBucket(new BucketBean(row.getBucketName(), new DateTime(DateTimeZone.UTC), row.getBucketUuid()));
                return new MultipartUpload(resource_bean, row.getUuid(), row.getInitiatedAt());
            }
        };
    }

    @Override
    public void appendChunkedResource(MultipartUpload upload, ChunkedResource r, String partNumber) throws NoSuchBucket {
        if (!(upload.getResource() instanceof AmassedResourceBean)) {
            throw new InvalidParameterException("MultipartUpload object should be fetched persisted object.");
        }
        final AmassedResourceBean amassed_resource = (AmassedResourceBean) upload.getResource();
        String key = partNumber;
        if (key.length() > 32) {
            key = key.substring(key.length() - 32);
        }
        Subresource record = subResourceBeanMapper.toRecord(r);
        record.setKey(key);
        record.setParentVersionId(amassed_resource.getVersionId());
        // @TODO: Involve Merge support.
        uploadingSubResourceDao.insert(record);
    }

}
