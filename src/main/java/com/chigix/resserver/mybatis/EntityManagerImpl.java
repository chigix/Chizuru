package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.Lifecycle;
import com.chigix.resserver.mybatis.bean.BeanExt;
import org.springframework.beans.factory.annotation.Autowired;
import com.chigix.resserver.domain.dao.BucketDao;
import com.chigix.resserver.domain.dao.ChunkDao;
import com.chigix.resserver.domain.dao.MultipartUploadDao;
import com.chigix.resserver.domain.dao.ResourceDao;
import com.chigix.resserver.domain.dao.DaoFactory;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class EntityManagerImpl implements DaoFactory {

    @Autowired
    private BucketDao bucketRepository;
    @Autowired
    private ChunkDaoImpl chunkRepository;
    @Autowired
    private ResourceDao resourceRepository;
    @Autowired
    private MultipartUploadDao uploadRepository;

    public EntityManagerImpl() {
    }

    @Override
    public void close() {
    }

    @Override
    public BucketDao getBucketDao() {
        return bucketRepository;
    }

    @Override
    public ChunkDao getChunkDao() {
        return chunkRepository;
    }

    @Override
    public ResourceDao getResourceDao() {
        return resourceRepository;
    }

    @Override
    public MultipartUploadDao getUploadDao() {
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
