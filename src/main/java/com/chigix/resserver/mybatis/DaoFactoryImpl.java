package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.dao.BucketDao;
import com.chigix.resserver.domain.dao.ChunkDao;
import com.chigix.resserver.domain.dao.DaoFactory;
import com.chigix.resserver.domain.dao.MultipartUploadDao;
import com.chigix.resserver.domain.dao.ResourceDao;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class DaoFactoryImpl implements DaoFactory {

    private final ThreadLocal<SessionContext> threadContext = new ThreadLocal<>();

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

    private void ensureThreadSession() {
        if (threadContext.get() != null) {
            return;
        }
        final DaoFactoryImpl self = this;
        SessionContext sctx = new SessionContext(sessionFactory.openSession(true),
                sessionFactoryForUpload.openSession(true));
        threadContext.set(sctx);
        sctx.addListenerOnInit((ctx) -> {
            ctx.getChunkDao().setAspectForNewChunk(self.getChunkDao());
            return ctx;
        });
        sctx.init();
    }

    public void closeSessions() {
        SessionContext session = threadContext.get();
        if (session == null) {
            return;
        }
        threadContext.remove();
        session.getSessions().commit();
        session.getSessions().close();
        session.getUploadSessions().commit();
        session.getUploadSessions().close();
    }

    @Override
    public BucketDao getBucketDao() {
        ensureThreadSession();
        return threadContext.get().getBucketDao();
    }

    @Override
    public ChunkDao getChunkDao() {
        ensureThreadSession();
        return threadContext.get().getChunkDao();
    }

    @Override
    public ResourceDao getResourceDao() {
        ensureThreadSession();
        return threadContext.get().getResourceDao();
    }

    @Override
    public MultipartUploadDao getUploadDao() {
        ensureThreadSession();
        return threadContext.get().getUploadDao();
    }

}
