package com.chigix.resserver.mybatis.bean;

import com.chigix.resserver.domain.model.chunk.Chunk;
import com.chigix.resserver.domain.model.resource.ChunkedResource;
import com.chigix.resserver.domain.Lifecycle;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.mybatis.EntityManagerImpl;
import com.chigix.resserver.mybatis.mapstruct.BucketAdapter;
import com.chigix.resserver.mybatis.mapstruct.ChunksAdapter;
import java.util.Iterator;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ChunkedResourceBean extends ChunkedResource implements ResourceExtension, BeanExt {

    public static final String TYPE = "ChunkedResource";

    private final String keyHash;

    private BucketBean bucket;

    private BucketAdapter bucketAdapter = null;

    private ChunksAdapter chunksAdapter = new ChunksAdapter.EmptyChunksAdapter();

    private ReferenceAdapter<AmassedResourceBean> parentResource = new ReferenceAdapter.NullAdapter<>();

    private Lifecycle entityStatus = Lifecycle.MANAGED;

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
        return chunksAdapter.iterateChunks();
    }

    @Deprecated
    public void setChunksAdapter(ChunksAdapter chunksAdapter) {
        this.chunksAdapter = chunksAdapter;
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

    public void setBucket(BucketAdapter bucketAdapter) {
        this.bucketAdapter = bucketAdapter;
    }

    @Override
    public String getKeyHash() {
        return this.keyHash;
    }

    /**
     * @TODO remove. Try to involve a new method {@code appendSubResource} in
     * ResourceRepository instead this bean method hidden for domain level
     * operation.
     * @return
     * @deprecated
     */
    @Deprecated
    public AmassedResourceBean getParentResource() {
        return parentResource.getAdapted();
    }

    /**
     * @TODO remove.
     * @param parentResource
     * @deprecated
     */
    @Deprecated
    public void setParentResource(AmassedResourceBean parentResource) {
        this.parentResource = () -> parentResource;
    }

    public void setParentResource(ReferenceAdapter<AmassedResourceBean> parentResource) {
        this.parentResource = parentResource;
    }

    @Override
    public Lifecycle getEntityStatus(EntityManagerImpl em) {
        return this.entityStatus;
    }

    public void setEntityStatus(Lifecycle entityStatus) {
        this.entityStatus = entityStatus;
    }

}
