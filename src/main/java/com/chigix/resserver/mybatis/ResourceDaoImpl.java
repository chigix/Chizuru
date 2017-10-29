package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.Bucket;
import com.chigix.resserver.domain.Chunk;
import com.chigix.resserver.domain.ChunkedResource;
import com.chigix.resserver.domain.Resource;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.domain.error.NoSuchKey;
import com.chigix.resserver.domain.error.UnexpectedLifecycleException;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import com.chigix.resserver.mybatis.bean.ResourceExtension;
import com.chigix.resserver.mybatis.dao.BucketMapper;
import com.chigix.resserver.mybatis.dao.ChunkMapper;
import com.chigix.resserver.mybatis.dao.ResourceMapper;
import com.chigix.resserver.mybatis.dao.SubresourceMapper;
import com.chigix.resserver.mybatis.mapstruct.ChunkBeanMapper;
import com.chigix.resserver.mybatis.mapstruct.ResourceBeanMapper;
import com.chigix.resserver.mybatis.mapstruct.SubresourceBeanMapper;
import com.chigix.resserver.mybatis.record.BucketExample;
import com.chigix.resserver.mybatis.record.ResourceExample;
import com.chigix.resserver.mybatis.record.ResourceExampleExtending;
import com.chigix.resserver.mybatis.record.Subresource;
import com.chigix.resserver.mybatis.record.SubresourceExample;
import com.chigix.resserver.mybatis.record.Util;
import com.chigix.resserver.mybatis.specification.ResourceSpecification;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 * @TODO Rename to ResourceRepositoryImpl
 */
public class ResourceDaoImpl implements ResourceRepositoryExtend {

    private final ResourceMapper resourceMapper;

    private final SubresourceMapper subResourceMapper;

    private final ChunkMapper chunkMapper;

    private final BucketMapper bucketMapper;

    @Autowired
    private ResourceBeanMapper resourceBeanMapper;

    @Autowired
    private ChunkBeanMapper chunkBeanMapper;

    @Autowired
    private SubresourceBeanMapper subResourceBeanMapper;

    public ResourceDaoImpl(ResourceMapper resource_mapper, ChunkMapper chunk_mapper, BucketMapper bucket_mapper, SubresourceMapper subresource_mapper) {
        this.resourceMapper = resource_mapper;
        this.chunkMapper = chunk_mapper;
        this.bucketMapper = bucket_mapper;
        this.subResourceMapper = subresource_mapper;
    }

    @Override
    public Resource findResource(String bucketName, String resourceKey) throws NoSuchKey, NoSuchBucket {
        BucketExample bucket_example = new BucketExample();
        bucket_example.createCriteria().andNameEqualTo(bucketName);
        List<com.chigix.resserver.mybatis.record.Bucket> bucket_records
                = bucketMapper.selectByExampleWithRowbounds(bucket_example, Util.ONE_ROWBOUND);
        if (bucket_records.size() < 1) {
            throw new NoSuchBucket(bucketName);
        }
        com.chigix.resserver.mybatis.record.Bucket bucket_record = bucket_records.get(0);
        ResourceExample resource_example = new ResourceExample();
        resource_example.createCriteria().andBucketUuidEqualTo(bucket_record.getUuid())
                .andKeyEqualTo(resourceKey);
        List<com.chigix.resserver.mybatis.record.Resource> records
                = resourceMapper.selectByExampleWithBLOBsWithRowbounds(resource_example, Util.ONE_ROWBOUND);
        if (records.size() < 1) {
            throw new NoSuchKey(resourceKey);
        }
        return resourceBeanMapper.fromRecord(records.get(0));
    }

    @Override
    public Resource findResource(Bucket bucket, String resourceKey) throws NoSuchKey, NoSuchBucket {
        if (!(bucket instanceof BucketBean)) {
            return findResource(bucket.getName(), resourceKey);
        }
        ResourceExample example = new ResourceExample();
        example.createCriteria().andBucketUuidEqualTo(((BucketBean) bucket).getUuid())
                .andKeyEqualTo(resourceKey);
        List<com.chigix.resserver.mybatis.record.Resource> records
                = resourceMapper.selectByExampleWithBLOBsWithRowbounds(example, Util.ONE_ROWBOUND);
        if (records.size() < 1) {
            throw new NoSuchKey(resourceKey);
        }
        return resourceBeanMapper.fromRecord(records.get(0));
    }

    @Override
    public Resource saveResource(final Resource resource) throws NoSuchBucket {
        if (resource instanceof ChunkedResourceBean
                && ((ChunkedResourceBean) resource).getParentResource() != null) {
            return subResourceMapper.insert(subResourceBeanMapper.toRecord((ChunkedResourceBean) resource)) > 0 ? resource : null;
        }
        com.chigix.resserver.mybatis.record.Resource record
                = resourceBeanMapper.toRecord(resource);
        if (record.getBucketUuid().length() < 10) {
            throw new NoSuchBucket(resource.getBucket().getName());
        }
        ResourceExample example = new ResourceExample();
        example.createCriteria().andKeyhashEqualTo(record.getKeyhash());
        if (resource instanceof ChunkedResourceBean && ((ChunkedResourceBean) resource).getParentResource() != null) {
        }
        if (resourceMapper.merge(record) > 0) {
            return resource;
        }
        return null;
    }

    @Override
    public Iterator<Resource> listResources(Bucket bucket, int limit) throws NoSuchBucket {
        return listResources(bucket, null, limit);
    }

    /**
     *
     * @param bucket
     * @param con_token KeyHash of the resource is used as the continuation
     * token.
     * @param limit
     * @return
     */
    @Override
    public Iterator<Resource> listResources(Bucket bucket, String con_token, int limit) {
        if (!(bucket instanceof BucketBean)) {
            throw new RuntimeException("Unexpected!! Unpersisted Bucket Domain Object was passed.");
        }
        BucketBean bb = (BucketBean) bucket;
        final AtomicReference<String> continuation = new AtomicReference<>(con_token);
        final AtomicBoolean include_continuation = new AtomicBoolean(true);
        final AtomicInteger remaining = new AtomicInteger(limit);
        final IteratorConcater<com.chigix.resserver.mybatis.record.Resource> records = new IteratorConcater<com.chigix.resserver.mybatis.record.Resource>() {
            @Override
            protected Iterator<com.chigix.resserver.mybatis.record.Resource> nextIterator() {
                final RowBounds limit;
                if (remaining.get() < 100) {
                    limit = new RowBounds(0, remaining.get());
                } else {
                    limit = new RowBounds(0, 100);
                }
                if (limit.getLimit() < 1) {
                    return Collections.emptyIterator();
                }
                final ResourceExample example = new ResourceExample();
                ResourceExample.Criteria criteria = example.createCriteria();
                criteria.andBucketUuidEqualTo(bb.getUuid());
                if (continuation.get() == null) {
                    include_continuation.set(false);
                } else {
                    criteria.getCriteria().add(new ResourceExampleExtending.VersionIdOffset(continuation.get(), include_continuation.getAndSet(false)));
                }
                return resourceMapper.selectByExampleWithBLOBsWithRowbounds(example, limit).iterator();
            }
        }.addListener((resource) -> {
            continuation.set(resource.getVersionId());
            remaining.decrementAndGet();
        });
        return new Iterator<Resource>() {
            @Override
            public boolean hasNext() {
                return records.hasNext();
            }

            @Override
            public Resource next() {
                return resourceBeanMapper.fromRecord(records.next());
            }
        };
    }

    @Override
    public Iterator<ChunkedResourceBean> listSubResources(final ResourceSpecification.byParentResource spec) {
        // Subresource querying is disabled for the AmassedResourceBean which 
        // is still persisted in UploadingDatabase.
        final AtomicInteger continuation_index = new AtomicInteger(Integer.valueOf(spec.getBeginIndex()) - 1);
        final Iterator<Subresource> records = new IteratorConcater<Subresource>() {
            @Override
            protected Iterator<Subresource> nextIterator() {
                SubresourceExample example = new SubresourceExample();
                ArrayList<String> index_range = new ArrayList<>();
                for (int i = continuation_index.addAndGet(1); i < continuation_index.get() + 20; i++) {
                    index_range.add(i + "");
                }
                spec.build(example.createCriteria()).andKeyIn(index_range);
                // Because index field stored in database is a string, 
                // Order through SQL in database system should not be used here.
                final List<Subresource> records = subResourceMapper
                        .selectByExampleWithRowbounds(example, new RowBounds(0, 20));
                final Subresource[] sorted_records = new Subresource[20];
                for (Subresource record : records) {
                    sorted_records[Integer.valueOf(record.getKey()) - continuation_index.get()] = record;
                }
                final AtomicInteger nextIndex = new AtomicInteger(0);
                return new Iterator<Subresource>() {
                    @Override
                    public boolean hasNext() {
                        try {
                            return sorted_records[nextIndex.get()] != null;
                        } catch (ArrayIndexOutOfBoundsException e) {
                            return false;
                        }
                    }

                    @Override
                    public Subresource next() {
                        return sorted_records[nextIndex.getAndAdd(1)];
                    }
                };
            }
        }.addListener((r) -> {
            continuation_index.set(Integer.valueOf(r.getKey()));
        });
        return new Iterator<ChunkedResourceBean>() {
            @Override
            public boolean hasNext() {
                return records.hasNext();
            }

            @Override
            public ChunkedResourceBean next() {
                if (hasNext() == false) {
                    throw new NoSuchElementException();
                }
                return subResourceBeanMapper.fromRecord(records.next());
            }
        };
    }

    @Override
    public void removeResource(Resource resource) throws NoSuchKey, NoSuchBucket {
        if (resource instanceof ResourceExtension) {
            ResourceExample example = new ResourceExample();
            example.createCriteria().andKeyhashEqualTo(((ResourceExtension) resource).getKeyHash());
            resourceMapper.deleteByExample(example);
        } else {
            throw new UnexpectedLifecycleException("Unexpected!! Received data object is not bean.");
        }
    }

    /**
     * @TODO: The resource parameter should be managed ChunkedResourceBean,
     * instead of domain object, which could remove the parameter of
     * {@code chunkIndex}
     *
     * @param r
     * @param c
     * @param chunkIndex
     */
    @Override
    public void putChunk(ChunkedResource r, Chunk c, int chunkIndex) {
        com.chigix.resserver.mybatis.record.Chunk record = chunkBeanMapper.toRecord(c);
        record.setParentVersionId(r.getVersionId());
        record.setIndexInParent(chunkIndex + "");
        chunkMapper.insert(record);
    }

}
