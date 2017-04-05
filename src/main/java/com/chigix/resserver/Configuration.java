package com.chigix.resserver;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.ibatis.session.SqlSessionFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Configuration {

    private final Map<String, String> nodesMapping = new HashMap<>();

    private int maxChunkSize = 8 * 1024 * 1024;

    private int transferBufferSize = 2 * 1024 * 1024;

    private File chunksDir;

    private final String currentNodeId;

    private DateTime creationDate = new DateTime(DateTimeZone.forID("GMT"));

    private SqlSessionFactory mainSession;

    private SqlSessionFactory uploadSession;

    public Configuration(String currentNodeId) {
        this.currentNodeId = currentNodeId;
    }

    public String getCurrentNodeId() {
        return currentNodeId;
    }

    public SqlSessionFactory getMainSession() {
        return mainSession;
    }

    public void setMainSession(SqlSessionFactory sessionFactory) {
        this.mainSession = sessionFactory;
    }

    public SqlSessionFactory getUploadSession() {
        return uploadSession;
    }

    public void setUploadSession(SqlSessionFactory uploadSession) {
        this.uploadSession = uploadSession;
    }

    public File getChunksDir() {
        if (chunksDir == null) {
            throw new NullPointerException("There is no File Directory configured.");
        }
        return chunksDir;
    }

    public void setChunksDir(File chunksDir) {
        this.chunksDir = chunksDir;
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(DateTime creationDate) {
        if (creationDate == null) {
            throw new NullPointerException("Server Node Creation Date is invalid.");
        }
        this.creationDate = creationDate;
    }

    public int getMaxChunkSize() {
        return maxChunkSize;
    }

    public void setMaxChunkSize(int maxChunkSize) {
        this.maxChunkSize = maxChunkSize;
    }

    public int getTransferBufferSize() {
        return transferBufferSize;
    }

    public void setTransferBufferSize(int transferBufferSize) {
        this.transferBufferSize = transferBufferSize;
    }

    public Map<String, String> getNodesMapping() {
        return nodesMapping;
    }

}
