package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.dao.BucketDao;
import com.chigix.resserver.domain.dao.ChunkDao;
import com.chigix.resserver.domain.dao.DaoFactory;
import com.chigix.resserver.domain.dao.MultipartUploadDao;
import com.chigix.resserver.domain.dao.ResourceDao;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class DaoFactoryImpl implements DaoFactory {

    ThreadLocal<SqlSession> sessions = new ThreadLocal<>();

    ThreadLocal<SqlSession> uploadSessions = new ThreadLocal<>();

    ThreadLocal<BucketDaoImpl> bucketDao = new ThreadLocal<>();
    ThreadLocal<ResourceDaoImpl> resourceDao = new ThreadLocal<>();
    ThreadLocal<ChunkDaoImpl> chunkDao = new ThreadLocal<>();
    ThreadLocal<MultipartUploadDaoImpl> uploadDao = new ThreadLocal<>();

    private final SqlSessionFactory sessionFactory;

    private final SqlSessionFactory sessionFactoryForUpload;

    public DaoFactoryImpl(SqlSessionFactory main, SqlSessionFactory upload) {
        if (main == null) {
            throw new NullPointerException("SqlSessionFactory set is invalid.");
        }
        if (upload == null) {
            throw new NullPointerException("SqlSessionFactory set is invalid.");
        }
        this.sessionFactory = main;
        this.sessionFactoryForUpload = upload;
    }

    private SqlSession currentSession() {
        if (sessions.get() == null) {
            sessions.set(sessionFactory.openSession(true));
        }
        return sessions.get();
    }

    private SqlSession currentUploadSession() {
        if (uploadSessions.get() == null) {
            uploadSessions.set(sessionFactoryForUpload.openSession(true));
        }
        return uploadSessions.get();
    }

    public void closeSessions() {
        SqlSession session = sessions.get();
        SqlSession uploading = uploadSessions.get();
        if (session != null) {
            sessions.remove();
            session.commit();
            session.close();
        }
        if (uploading != null) {
            uploadSessions.remove();
            uploading.commit();
            uploading.close();
        }
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
            resourceDao.set(createResourceDao(currentSession().getMapper(ResourceMapper.class)));
        }
        return resourceDao.get();
    }

    public ResourceDaoImpl createResourceDao(ResourceMapper mapper) {
        ResourceDaoImpl resource_dao = new ResourceDaoImpl(mapper,
                currentSession().getMapper(ChunkMapper.class));
        resource_dao.setBucketDao((BucketDaoImpl) getBucketDao());
        if (chunkDao.get() == null) {
            getChunkDao();
        }
        resource_dao.setChunkDao(chunkDao.get());
        resourceDao.set(resource_dao);
        return resource_dao;
    }

    @Override
    public MultipartUploadDao getUploadDao() {
        if (uploadDao.get() == null) {
            if (chunkDao.get() == null) {
                getResourceDao();
            }
            MultipartUploadDaoImpl upload_dao = new MultipartUploadDaoImpl(
                    currentUploadSession().getMapper(MultipartUploadMapper.class),
                    chunkDao.get(),
                    currentUploadSession().getMapper(ResourceMapper.class)
            );
            uploadDao.set(upload_dao);
        }
        return uploadDao.get();
    }

}
