package com.chigix.resserver.mybatis;

import com.chigix.resserver.entity.AmassedResource;
import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.Chunk;
import com.chigix.resserver.entity.ChunkedResource;
import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.entity.dao.ResourceDao;
import com.chigix.resserver.entity.error.NoSuchBucket;
import com.chigix.resserver.entity.error.NoSuchKey;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import com.chigix.resserver.mybatis.bean.ResourceExtension;
import com.chigix.resserver.mybatis.dto.ResourceBuilder;
import com.chigix.resserver.mybatis.dto.ResourceDto;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ResourceDaoImpl implements ResourceDao {

    private final ResourceMapper resourceMapper;

    private final ChunkMapper chunkMapper;

    private BucketDaoImpl bucketDao;

    private ChunkDaoImpl chunkDao;

    public ResourceDaoImpl(ResourceMapper resource_mapper, ChunkMapper chunk_mapper) {
        this.resourceMapper = resource_mapper;
        this.chunkMapper = chunk_mapper;
    }

    public void setBucketDao(BucketDaoImpl bucketDao) {
        this.bucketDao = bucketDao;
    }

    public void setChunkDao(ChunkDaoImpl chunkDao) {
        this.chunkDao = chunkDao;
    }

    @Override
    public Resource findResource(String bucketName, String resourceKey) throws NoSuchKey, NoSuchBucket {
        ResourceBuilder data = resourceMapper.selectByBucketName_Key(bucketName, resourceKey);
        if (data == null) {
            throw new NoSuchKey(resourceKey);
        }
        return data.build(bucketDao, chunkDao, this);
    }

    @Override
    public Resource findResource(Bucket bucket, String resourceKey) throws NoSuchKey, NoSuchBucket {
        if (bucket instanceof BucketBean) {
            ResourceBuilder data = resourceMapper.selectByKeyhash(
                    ResourceExtension.hashKey(((BucketBean) bucket).getUuid(),
                            resourceKey)
            );
            if (data == null) {
                throw new NoSuchKey(resourceKey);
            }
            return data.build(bucketDao, chunkDao, this);
        } else {
            return findResource(bucket.getName(), resourceKey);
        }
    }

    public Resource findResource(BucketBean bucket, String resourceKey, String versionId) throws NoSuchKey, NoSuchBucket {
        ResourceBuilder data = resourceMapper.selectByKeyhash_Version(ResourceExtension.hashKey(bucket.getUuid(), resourceKey), versionId);
        if (data == null) {
            throw new NoSuchKey(resourceKey);
        }
        return data.build(bucketDao, chunkDao, this);
    }

    @Override
    public Resource saveResource(final Resource resource) throws NoSuchBucket {
        Bucket b = resource.getBucket();
        ResourceDto dto;
        if (b instanceof BucketBean) {
            dto = new ResourceDto(resource, (BucketBean) b);
        } else {
            BucketBean bb = (BucketBean) bucketDao.findBucketByName(b.getName());
            if (bb == null) {
                throw new NoSuchBucket(b.getName());
            }
            dto = new ResourceDto(resource, bb);
        }
        if (dto.getParentResource() != null) {
            return resourceMapper.insertSubResource(dto) > 0 ? resource : null;
        } else {
            return resourceMapper.mergeResource(dto) > 0 ? resource : null;
        }
    }

    @Override
    public Iterator<Resource> listResources(Bucket bucket) throws NoSuchBucket {
        return listResources(bucket, null);
    }

    @Override
    public Iterator<Resource> listResources(Bucket bucket, String con_token) {
        final ResourceDaoImpl self_dao = this;
        final AtomicReference<String> continuation = new AtomicReference<>(con_token);
        final IteratorConcater<ResourceBuilder> builders = new IteratorConcater<ResourceBuilder>() {
            @Override
            protected Iterator<ResourceBuilder> nextIterator() {
                if (continuation.get() == null) {
                    return resourceMapper.selectAllByBucketName(bucket.getName(), 1000).iterator();
                }
                return Collections.emptyIterator();
            }
        }.addListener((resourceBuilder) -> {
            continuation.set(resourceBuilder.getKeyHash());
        });
        return new Iterator<Resource>() {
            @Override
            public boolean hasNext() {
                return builders.hasNext();
            }

            @Override
            public Resource next() {
                return builders.next().build(bucketDao, chunkDao, self_dao);
            }
        };
    }

    public Iterator<ChunkedResource> listSubResources(final AmassedResource parent) {
        final ResourceDaoImpl self_dao = this;
        final AtomicReference<String> continuation = new AtomicReference<>();
        final IteratorConcater<ResourceBuilder> builders = new IteratorConcater<ResourceBuilder>() {
            @Override
            protected Iterator<ResourceBuilder> nextIterator() {
                if (continuation.get() == null) {
                    return resourceMapper.selectSubResourcesByParentVersionId(parent.getVersionId()).iterator();
                }
                Iterator<ResourceBuilder> new_it = resourceMapper.selectSubResourcesByParentVersionId(parent.getVersionId(), continuation.get()).iterator();
                if (!continuation.get().equals(new_it.next().getVersionId())) {
                    return Collections.emptyIterator();
                }
                return new_it;
            }
        }.addListener((resourceBuilder) -> {
            continuation.set(resourceBuilder.getVersionId());
        });
        return new Iterator<ChunkedResource>() {
            @Override
            public boolean hasNext() {
                return builders.hasNext();
            }

            @Override
            public ChunkedResourceBean next() {
                if (hasNext() == false) {
                    throw new NoSuchElementException();
                }
                return (ChunkedResourceBean) builders.next().build(bucketDao, chunkDao, self_dao);
            }
        };
    }

    @Override
    public void removeResource(Resource resource) throws NoSuchKey, NoSuchBucket {
        if (resource instanceof ResourceExtension) {
            resourceMapper.deleteByKeyhash(((ResourceExtension) resource).getKeyHash());
        } else {
            resourceMapper.delete(resource);
        }
    }

    @Override
    public void appendChunk(ChunkedResource r, Chunk c) {
        r.setSize(new BigInteger(r.getSize()).add(new BigInteger(c.getSize() + "")).toString());
        chunkMapper.appendChunkToVersion(r.getVersionId(), c);
    }

}
