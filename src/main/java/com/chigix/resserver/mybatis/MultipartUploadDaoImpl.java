package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.AmassedResource;
import com.chigix.resserver.domain.Bucket;
import com.chigix.resserver.domain.ChunkedResource;
import com.chigix.resserver.domain.MultipartUpload;
import com.chigix.resserver.domain.dao.MultipartUploadDao;
import com.chigix.resserver.domain.error.InvalidPart;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.domain.error.NoSuchUpload;
import com.chigix.resserver.mybatis.bean.AmassedResourceBean;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import com.chigix.resserver.mybatis.dto.MultipartUploadDto;
import com.chigix.resserver.mybatis.dto.ResourceBuilder;
import com.chigix.resserver.mybatis.dto.ResourceDto;
import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class MultipartUploadDaoImpl implements MultipartUploadDao {

    private final MultipartUploadMapper uploadMapper;

    private final ChunkDaoImpl chunkdao;
    private final ResourceMapper uploadingResourceMapper;

    public MultipartUploadDaoImpl(MultipartUploadMapper uploadMapper,
            ChunkDaoImpl chunkdao,
            ResourceMapper uploadingResourceMapper) {
        this.uploadMapper = uploadMapper;
        this.uploadingResourceMapper = uploadingResourceMapper;
        this.chunkdao = chunkdao;
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
        uploadingResourceMapper.mergeUploadingResource(new ResourceDto(resource, bb), new MultipartUploadDto(u));
        ResourceBuilder b = uploadingResourceMapper
                .selectByUploadId(u.getUploadId());
        AmassedResourceBean resource_bean;
        try {
            resource_bean = (AmassedResourceBean) b.build(null, chunkdao, null);
        } catch (NullPointerException e) {
            if (b == null) {
                throw new RuntimeException("Unexpected!!! AmassedResource have "
                        + "been removed while belonged upload is still initiating. : ["
                        + resource.getKey() + "]");
            }
            throw e;
        }
        resource_bean.setBucket(bb);
        resource_ref.set(resource_bean);
        uploadMapper.insert(new MultipartUploadDto(u));
        return u;
    }

    @Override
    public MultipartUpload findUpload(String uploadId) throws NoSuchUpload {
        Map<String, String> row = uploadMapper.selectByUuid(uploadId);
        ResourceBuilder b = uploadingResourceMapper.selectByUploadId(uploadId);
        if (b == null) {
            throw new NoSuchUpload();
        }
        AmassedResourceBean resource_bean = (AmassedResourceBean) b.build(null, chunkdao, null);
        resource_bean.setBucket(new BucketBean(row.get("bucket_name"),
                new DateTime(DateTimeZone.UTC), row.get("bucket_uuid")));
        MultipartUpload u = new MultipartUpload(resource_bean, uploadId, DateTime.parse(row.get("initiated_at")));
        return u;
    }

    @Override
    public ChunkedResource findUploadPart(MultipartUpload upload, String partNumber, String etag) throws InvalidPart {
        String key = partNumber;
        if (key.length() > 32) {
            key = key.substring(key.length() - 32);
        }
        ResourceBuilder b = uploadingResourceMapper.selectSubResourceByKeyEtagParent(
                key, upload.getResource().getVersionId(), etag);
        if (b == null) {
            throw new InvalidPart();
        }
        ChunkedResourceBean resource_bean = (ChunkedResourceBean) b.build(null, chunkdao, null);
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
        int upload_count = uploadMapper.deleteByUuid(upload.getUploadId());
        int resource_count = uploadingResourceMapper.deleteByUploadId(upload.getUploadId());
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
            throw new InvalidParameterException("Parameter of Bucket is expected to be persisted bean object.");
        }
        final Iterator<Map<String, String>> rows = uploadMapper.selectAllByBucketUuid(bb.getUuid()).iterator();
        return new Iterator<MultipartUpload>() {
            @Override
            public boolean hasNext() {
                return rows.hasNext();
            }

            @Override
            public MultipartUpload next() {
                Map<String, String> row = rows.next();
                ResourceBuilder b = uploadingResourceMapper.selectByUploadId(row.get("uuid"));
                if (b == null) {
                    uploadMapper.deleteByUuid(row.get("uuid"));
                    return next();
                }
                AmassedResourceBean resource_bean = (AmassedResourceBean) b.build(null, chunkdao, null);
                resource_bean.setBucket(new BucketBean(row.get("bucket_name"), new DateTime(DateTimeZone.UTC), row.get("bucket_uuid")));
                return new MultipartUpload(resource_bean, row.get("uuid"), DateTime.parse(row.get("initiated_at")));
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
        ResourceBuilder chunked_resource_builder = ResourceBuilder.createFrom(
                new ResourceDto(r, amassed_resource.getBucket()));
        chunked_resource_builder.setResourceKey(key);
        ChunkedResourceBean chunk_bean
                = (ChunkedResourceBean) chunked_resource_builder.build(null, chunkdao, null);
        chunk_bean.setParentResource(amassed_resource);
        uploadingResourceMapper.insertSubResource(new ResourceDto(chunk_bean,
                amassed_resource.getBucket()));
    }

}
