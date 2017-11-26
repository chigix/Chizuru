package com.chigix.resserver.mybatis.bean;

import com.chigix.resserver.domain.model.resource.AmassedResource;
import com.chigix.resserver.domain.Lifecycle;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.mybatis.EntityManagerImpl;
import com.chigix.resserver.mybatis.mapstruct.BucketAdapter;
import com.chigix.resserver.mybatis.mapstruct.SubresourcesAdapter;
import java.util.Iterator;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class AmassedResourceBean extends AmassedResource implements ResourceExtension, BeanExt {

    public static final String TYPE = "AmassedResource";

    private final String keyhash;

    private BucketAdapter bucketAdapter = null;

    private BucketBean bucket;

    private SubresourcesAdapter subresourceAdapter;

    private Lifecycle entityStatus = Lifecycle.MANAGED;

    public AmassedResourceBean(String key, String keyhash) {
        super(key);
        this.keyhash = keyhash;
    }

    public AmassedResourceBean(String key, String versionId, String keyhash) {
        super(key, versionId);
        this.keyhash = keyhash;
    }

    @Override
    public final Iterator<ChunkedResourceBean> getSubResources() {
        return subresourceAdapter.iterateFromByte(0);
    }

    public void setSubresourceAdapter(SubresourcesAdapter subresourceAdapter) {
        this.subresourceAdapter = subresourceAdapter;
    }

    @Override
    public BucketBean getBucket() throws NoSuchBucket {
        if (bucketAdapter == null) {
            return bucket;
        }
        return bucketAdapter.getBucket();
    }

    @Override
    public void setBucket(BucketBean bucket) {
        this.bucket = bucket;
    }

    public void setBucket(BucketAdapter adapter) {
        this.bucketAdapter = adapter;
    }

    @Override
    public String getKeyHash() {
        return keyhash;
    }

    @Override
    public Lifecycle getEntityStatus(EntityManagerImpl em) {
        return this.entityStatus;
    }

    public void setEntityStatus(Lifecycle entityStatus) {
        this.entityStatus = entityStatus;
    }

}
