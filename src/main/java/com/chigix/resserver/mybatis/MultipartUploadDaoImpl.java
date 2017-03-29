package com.chigix.resserver.mybatis;

import com.chigix.resserver.entity.AmassedResource;
import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.ChunkedResource;
import com.chigix.resserver.entity.MultipartUpload;
import com.chigix.resserver.entity.dao.MultipartUploadDao;
import com.chigix.resserver.entity.error.NoSuchBucket;
import com.chigix.resserver.entity.error.NoSuchKey;
import com.chigix.resserver.entity.error.NoSuchUpload;
import com.chigix.resserver.mybatis.bean.AmassedResourceBean;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import com.chigix.resserver.mybatis.dto.MultipartUploadDto;
import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.Map;
import org.joda.time.DateTime;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class MultipartUploadDaoImpl implements MultipartUploadDao {

    private final MultipartUploadMapper uploadMapper;

    private final ResourceDaoImpl uploadedResourceDao;

    private final ResourceDaoImpl uploadingResourceDao;

    public MultipartUploadDaoImpl(MultipartUploadMapper uploadMapper,
            ResourceDaoImpl uploadedResourceDao,
            ResourceDaoImpl uploadingResourceDao) {
        this.uploadMapper = uploadMapper;
        this.uploadingResourceDao = uploadingResourceDao;
        this.uploadedResourceDao = uploadedResourceDao;
    }

    @Override
    public MultipartUpload initiateUpload(AmassedResource resource) throws NoSuchBucket {
        BucketBean bb;
        try {
            bb = (BucketBean) resource.getBucket();
        } catch (ClassCastException classCastException) {
            throw new InvalidParameterException("Bucket inside this resource is not a persisted bean object.");
        }
        uploadingResourceDao.saveResource(resource);
        MultipartUpload u;
        try {
            u = new MultipartUpload((AmassedResource) uploadingResourceDao.findResource(bb, resource.getKey(), resource.getVersionId()));
        } catch (NoSuchKey ex) {
            throw new RuntimeException("Unexpected Exception that amassedResource should be saved previously.", ex);
        }
        uploadMapper.insert(new MultipartUploadDto(u));
        return u;
    }

    @Override
    public MultipartUpload findUpload(String uploadId) throws NoSuchUpload, NoSuchBucket {
        Map<String, String> row = uploadMapper.selectByUuid(uploadId);
        MultipartUpload u;
        try {
            u = new MultipartUpload((AmassedResource) uploadingResourceDao.findResource(
                    new BucketBean(row.get("bucket_name"), new DateTime(), row.get("bucket_uuid")),
                    row.get("resource_key"), row.get("resource_version")),
                    uploadId, DateTime.parse(row.get("initiated_at")));
        } catch (NoSuchKey ex) {
            throw new NoSuchUpload();
        }
        return u;
    }

    @Override
    public ChunkedResource findUploadPart(MultipartUpload upload, String partNumber, String etag) {
        String key = partNumber;
        if (key.length() > 32) {
            key = key.substring(key.length() - 32);
        }
        return uploadingResourceDao.findSubResourcePart(key, etag, upload.getResource());
    }

    @Override
    public MultipartUpload completeUpload(MultipartUpload upload) throws NoSuchBucket, NoSuchUpload {
        if (uploadMapper.deleteByUuid(upload.getUploadId()) < 1) {
            throw new NoSuchUpload();
        }
        try {
            uploadingResourceDao.removeResource(upload.getResource());
        } catch (NoSuchKey ex) {
            return null;
        }
        uploadedResourceDao.saveResource(upload.getResource());
        return upload;
    }

    @Override
    public void removeUpload(MultipartUpload upload) throws NoSuchUpload {
        if (uploadMapper.deleteByUuid(upload.getUploadId()) > 0) {
            return;
        }
        throw new NoSuchUpload();
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
                try {
                    return new MultipartUpload(
                            (AmassedResource) uploadingResourceDao.findResource(
                                    new BucketBean(row.get("bucket_name"), new DateTime(), row.get("bucket_uuid")),
                                    row.get("resource_key"), row.get("resource_version")),
                            row.get("uuid"), DateTime.parse(row.get("initiated_at")));
                } catch (NoSuchKey | NoSuchBucket ex) {
                    uploadMapper.deleteByUuid(row.get("uuid"));
                    return next();
                }
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
        ChunkedResourceBean chunk_bean = new ChunkedResourceBean(key, r.getVersionId(), r.getETag());
        chunk_bean.setParentResource(amassed_resource);
        chunk_bean.setBucket(amassed_resource.getBucket());
        uploadingResourceDao.saveResource(chunk_bean);
    }

}
