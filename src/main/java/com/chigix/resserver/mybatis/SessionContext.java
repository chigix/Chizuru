package com.chigix.resserver.mybatis;

import java.util.ArrayList;
import java.util.function.Function;
import org.apache.ibatis.session.SqlSession;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class SessionContext {

    private final SqlSession sessions;
    private final SqlSession uploadSessions;
    private BucketDaoImpl bucketDao = null;
    private ResourceDaoImpl resourceDao = null;
    private ChunkDaoImpl chunkDao = null;
    private MultipartUploadDaoImpl uploadDao = null;

    private final ArrayList<Function<SessionContext, SessionContext>> onInitListeners
            = new ArrayList<>();

    public SessionContext(SqlSession sessions, SqlSession uploadSessions) {
        this.sessions = sessions;
        this.uploadSessions = uploadSessions;
    }

    public void addListenerOnInit(Function<SessionContext, SessionContext> e) {
        onInitListeners.add(e);
    }

    public void init() {
        for (Function<SessionContext, SessionContext> onInitListener : onInitListeners) {
            onInitListener.apply(this);
        }
    }

    public SqlSession getSessions() {
        return sessions;
    }

    public SqlSession getUploadSessions() {
        return uploadSessions;
    }

    public BucketDaoImpl getBucketDao() {
        if (bucketDao == null) {
            bucketDao = new BucketDaoImpl(sessions.getMapper(BucketMapper.class));
        }
        return bucketDao;
    }

    public ResourceDaoImpl getResourceDao() {
        if (resourceDao == null) {
            resourceDao = new ResourceDaoImpl(sessions.getMapper(ResourceMapper.class),
                    sessions.getMapper(ChunkMapper.class));
        }
        resourceDao.setBucketDao(getBucketDao());
        resourceDao.setChunkDao(getChunkDao());
        return resourceDao;
    }

    public ChunkDaoImpl getChunkDao() {
        if (chunkDao == null) {
            chunkDao = new ChunkDaoImpl(sessions.getMapper(ChunkMapper.class));
        }
        return chunkDao;
    }

    public MultipartUploadDaoImpl getUploadDao() {
        if (uploadDao == null) {
            uploadDao = new MultipartUploadDaoImpl(
                    uploadSessions.getMapper(MultipartUploadMapper.class),
                    getChunkDao(),
                    uploadSessions.getMapper(ResourceMapper.class)
            );
        }
        return uploadDao;
    }

}
