package com.chigix.resserver.mybatis.bean;

import com.chigix.resserver.domain.Bucket;
import com.chigix.resserver.domain.Chunk;
import com.chigix.resserver.domain.ChunkedResource;
import com.chigix.resserver.domain.error.NoSuchBucket;
import java.util.Iterator;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ChunkedResourceBean extends ChunkedResource implements ResourceExtension {

    private final String keyHash;

    private BucketBean bucket;

    private Integer id = null;

    private AmassedResourceBean parentResource = null;

    public ChunkedResourceBean(String key, String keyhash) {
        super(key);
        this.keyHash = keyhash;
    }

    public ChunkedResourceBean(String key, String versionId, String keyhash) {
        super(key, versionId);
        this.keyHash = keyhash;
    }

    @Override
    public Iterator<Chunk> getChunks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Bucket getBucket() throws NoSuchBucket {
        return bucket;
    }

    @Override
    public void setBucket(BucketBean bucket) {
        this.bucket = bucket;
    }

    @Override
    public String getKeyHash() {
        return this.keyHash;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public AmassedResourceBean getParentResource() {
        return parentResource;
    }

    public void setParentResource(AmassedResourceBean parentResource) {
        this.parentResource = parentResource;
    }

}
