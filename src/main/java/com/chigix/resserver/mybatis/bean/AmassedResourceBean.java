package com.chigix.resserver.mybatis.bean;

import com.chigix.resserver.entity.AmassedResource;
import com.chigix.resserver.entity.ChunkedResource;
import com.chigix.resserver.entity.error.NoSuchBucket;
import java.util.Iterator;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class AmassedResourceBean extends AmassedResource implements ResourceExtension {

    private final String keyhash;

    private BucketBean bucket;

    public AmassedResourceBean(String key, String keyhash) {
        super(key);
        this.keyhash = keyhash;
    }

    public AmassedResourceBean(String key, String versionId, String keyhash) {
        super(key, versionId);
        this.keyhash = keyhash;
    }

    @Override
    public Iterator<ChunkedResource> getSubResources() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BucketBean getBucket() throws NoSuchBucket {
        return bucket;
    }

    @Override
    public void setBucket(BucketBean bucket) {
        this.bucket = bucket;
    }

    @Override
    public Integer getId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setId(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getKeyHash() {
        return keyhash;
    }

}
