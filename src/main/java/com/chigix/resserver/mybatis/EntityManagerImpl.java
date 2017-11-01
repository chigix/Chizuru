package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.Lifecycle;
import com.chigix.resserver.mybatis.bean.BeanExt;
import org.springframework.beans.factory.annotation.Autowired;
import com.chigix.resserver.domain.model.bucket.BucketRepository;
import com.chigix.resserver.domain.model.chunk.ChunkRepository;
import com.chigix.resserver.domain.model.multiupload.MultipartUploadRepository;
import com.chigix.resserver.domain.model.resource.ResourceRepository;
import com.chigix.resserver.domain.DaoFactory;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class EntityManagerImpl implements DaoFactory {

    @Autowired
    private BucketRepository bucketRepository;
    @Autowired
    private ChunkRepositoryImpl chunkRepository;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private MultipartUploadRepository uploadRepository;

    public EntityManagerImpl() {
    }

    @Override
    public void close() {
    }

    @Override
    public BucketRepository getBucketRepository() {
        return bucketRepository;
    }

    @Override
    public ChunkRepository getChunkRepository() {
        return chunkRepository;
    }

    @Override
    public ResourceRepository getResourceRepository() {
        return resourceRepository;
    }

    @Override
    public MultipartUploadRepository getUploadRepository() {
        return uploadRepository;
    }

    @Override
    public Lifecycle getEntityState(Object entity) throws UnknownEntityTypeException {
        if (entity instanceof BeanExt) {
            return ((BeanExt) entity).getEntityStatus(this);
        }
        return Lifecycle.NEW;
    }

}
