package com.chigix.resserver.mybatis;

import com.chigix.resserver.entity.dao.BucketDao;
import com.chigix.resserver.entity.dao.ChunkDao;
import com.chigix.resserver.entity.dao.DaoFactory;
import com.chigix.resserver.entity.dao.ResourceDao;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class DaoFactoryImpl implements DaoFactory {

    ThreadLocal<SqlSession> sessions = new ThreadLocal<>();

    ThreadLocal<BucketDaoImpl> bucketDao = new ThreadLocal<>();
    ThreadLocal<ResourceDaoImpl> resourceDao = new ThreadLocal<>();
    ThreadLocal<ChunkDaoImpl> chunkDao = new ThreadLocal<>();

    private final SqlSessionFactory sessionFactory;

    public DaoFactoryImpl(SqlSessionFactory sessionFactory) {
        if (sessionFactory == null) {
            throw new NullPointerException("SqlSessionFactory set is invalid.");
        }
        this.sessionFactory = sessionFactory;
    }

    private SqlSession currentSession() {
        if (sessions.get() == null) {
            sessions.set(sessionFactory.openSession(true));
        }
        return sessions.get();
    }

    @Override
    public BucketDao getBucketDao() {
        if (bucketDao.get() == null) {
            bucketDao.set(new BucketDaoImpl(currentSession().getMapper(BucketMapper.class)));
        }
        return bucketDao.get();
    }

    @Override
    public ChunkDao getChunkDao() {
        if (chunkDao.get() == null) {
            chunkDao.set(new ChunkDaoImpl(currentSession().getMapper(ChunkMapper.class)));
        }
        return chunkDao.get();
    }

    @Override
    public ResourceDao getResourceDao() {
        if (resourceDao.get() == null) {
            ResourceDaoImpl resource_dao = new ResourceDaoImpl(currentSession().getMapper(ResourceMapper.class),
                    currentSession().getMapper(ChunkMapper.class));
            resource_dao.setBucketDao((BucketDaoImpl) getBucketDao());
            if (chunkDao.get() == null) {
                getChunkDao();
            }
            resource_dao.setChunkDao(chunkDao.get());
            resourceDao.set(resource_dao);
            return resource_dao;
        }
        return resourceDao.get();
    }

}
