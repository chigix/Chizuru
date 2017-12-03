package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.Specification;
import com.chigix.resserver.domain.model.bucket.Bucket;
import com.chigix.resserver.domain.model.chunk.Chunk;
import com.chigix.resserver.domain.model.resource.ChunkedResource;
import com.chigix.resserver.domain.model.resource.Resource;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.domain.error.NoSuchKey;
import com.chigix.resserver.domain.error.UnexpectedLifecycleException;
import com.chigix.resserver.domain.model.resource.SubresourceSpecification;
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
import com.chigix.resserver.mybatis.specification.AbstractSpecification;
import com.chigix.resserver.mybatis.specification.InvalidSpecificationException;
import com.chigix.resserver.mybatis.specification.QueryCriteriaSpecification;
import com.chigix.resserver.mybatis.specification.ResourceSpecification;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ResourceRepositoryImpl implements ResourceRepositoryExtend {

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

    public ResourceRepositoryImpl(ResourceMapper resource_mapper, ChunkMapper chunk_mapper, BucketMapper bucket_mapper, SubresourceMapper subresource_mapper) {
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
        com.chigix.resserver.mybatis.record.Resource record
                = resourceBeanMapper.toRecord(resource);
        if (record.getBucketUuid().length() < 10) {
            throw new NoSuchBucket(resource.getBucket().getName());
        }
        ResourceExample example = new ResourceExample();
        example.createCriteria().andKeyhashEqualTo(record.getKeyhash());
        if (resourceMapper.merge(record) > 0) {
            return resource;
        }
        return null;
    }

    @Override
    public Resource insertSubresource(ChunkedResource r, SubresourceSpecification spec) throws NoSuchBucket {
        Subresource record = null;
        try {
            record = subResourceBeanMapper.toRecord((ChunkedResourceBean) r);
        } catch (ClassCastException ex) {
            if (!(r instanceof ChunkedResourceBean)) {
                throw new RuntimeException("Unexpected!! Unpersisted Subresource Domain Object was passed.");
            }
            throw ex;
        }
        int[] byte4s = Util.toBase256(spec.getRangeStartInParent());
        record.setRangeStartByte(byte4s[0]);
        record.setRangeStart4byte(0);
        if (byte4s.length > 1) {
            record.setRangeStart4byte(byte4s[1]);
        }
        byte4s = Util.toBase256(spec.getRangeEndInParent());
        record.setRangeEndByte(byte4s[0]);
        record.setRangeEnd4byte(0);
        if (byte4s.length > 1) {
            record.setRangeEnd4byte(byte4s[1]);
        }
        record.setIndexInParent(spec.getPartIndexInParent() + "");
        record.setParentVersionId(spec.getParentResource().getVersionId());
        return subResourceMapper.insert(record) > 0 ? r : null;
    }

    @Override
    public Iterator<Resource> fetchResources(Specification<Resource> specification, int limit) {
        if (!(specification instanceof AbstractSpecification)) {
            throw new InvalidSpecificationException();
        }
        QueryCriteriaSpecification<Resource, ResourceExample.Criteria> spec
                = (QueryCriteriaSpecification<Resource, ResourceExample.Criteria>) specification;
        final AtomicReference<String> continuation = new AtomicReference<>();
        if (spec.getContinuationSpec() != null) {
            continuation.set(spec.getContinuationSpec().getContinuationToken());
        }
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
                spec.appendCriteria(criteria);
                if (continuation.get() == null) {
                    include_continuation.set(false);
                } else {
                    criteria.getCriteria().add(
                            new ResourceExampleExtending.KeyhashOffset(
                                    continuation.get(),
                                    include_continuation.getAndSet(false))
                    );
                }
                return resourceMapper.selectByExampleWithBLOBsWithRowbounds(
                        example, limit).iterator();
            }
        }.addListener((resource) -> {
            continuation.set(resource.getKeyhash());
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
        final AtomicIntegerArray continuation_pos = new AtomicIntegerArray(new int[]{0, 0});
        int[] byte4_values = Util.toBase256(spec.getByteStart());
        for (int i = 0; i < byte4_values.length; i++) {
            continuation_pos.set(i, byte4_values[i]);
        }
        final Iterator<Subresource> records = new IteratorConcater<Subresource>() {
            @Override
            protected Iterator<Subresource> nextIterator() {
                SubresourceExample example = new SubresourceExample();
                SubresourceExample.Criteria c = example.createCriteria();
                spec.build(c);
                c.andRangeStartByteGreaterThanOrEqualTo(continuation_pos.get(0));
                c.andRangeStart4byteEqualTo(continuation_pos.get(1));
                c = example.or();
                spec.build(c);
                c.andRangeStart4byteGreaterThan(continuation_pos.get(1));
                // The order of inserting data is reliable, because all of 
                // subresources are appending in ascending order by part number 
                // list provided by the Complete Multipart 
                // Upload Request.
                return subResourceMapper
                        .selectByExampleWithRowbounds(example, new RowBounds(0, 20)).iterator();
            }
        }.addListener((r) -> {
            int[] byte4_values1 = new int[]{r.getRangeEndByte(),
                r.getRangeEnd4byte()};
            for (int i = 0; i < byte4_values1.length; i++) {
                continuation_pos.set(i, byte4_values1[i]);
            }
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
     * {@code chunkIndex} -- Should I involve a new property in
     * ChunkedResourceBean? -- no -- Because the index could be calculated
     * through size and chunksize.
     *
     * @param r
     * @param c
     * @param chunkIndex
     */
    @Override
    public void putChunk(ChunkedResource r, Chunk c, int chunkIndex) {
        r.setSize(new BigInteger(r.getSize()).add(new BigInteger(c.getSize() + "")).toString());
        com.chigix.resserver.mybatis.record.Chunk record = chunkBeanMapper.toRecord(c);
        record.setParentVersionId(r.getVersionId());
        record.setIndexInParent(chunkIndex + "");
        chunkMapper.insert(record);
    }

}
