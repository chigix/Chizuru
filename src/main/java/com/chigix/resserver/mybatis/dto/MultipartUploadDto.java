package com.chigix.resserver.mybatis.dto;

import com.chigix.resserver.entity.AmassedResource;
import com.chigix.resserver.entity.MultipartUpload;
import com.chigix.resserver.entity.error.NoSuchBucket;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.bean.ResourceExtension;
import java.security.InvalidParameterException;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class MultipartUploadDto {

    private final MultipartUpload bean;

    private final String resourceKeyHash;

    public MultipartUploadDto(MultipartUpload bean) throws NoSuchBucket {
        this.bean = bean;
        resourceKeyHash = calcKeyHash();
    }

    public MultipartUpload getBean() {
        return bean;
    }

    public String getInitiatedAt() {
        return this.bean.getInitiated().toString();
    }

    public String getKeyHash() {
        return resourceKeyHash;
    }

    private String calcKeyHash() throws NoSuchBucket {
        AmassedResource resource = bean.getResource();
        if (resource instanceof ResourceExtension) {
            return ((ResourceExtension) resource).getKeyHash();
        }
        BucketBean bb;
        try {
            bb = (BucketBean) resource.getBucket();
        } catch (ClassCastException classCastException) {
            throw new InvalidParameterException("The bucket in AmassedResource is not persisted object.");
        }
        return ResourceExtension.hashKey(bb.getUuid(), resource.getKey());
    }

}
