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

    private int maxChunkSize = 8192;

    private File chunksDir;

    private final String currentNodeId;

    private DateTime creationDate = new DateTime(DateTimeZone.forID("GMT"));

    private SqlSessionFactory sessionFactory;

    public Configuration(String currentNodeId) {
        this.currentNodeId = currentNodeId;
    }

    public String getCurrentNodeId() {
        return currentNodeId;
    }

    public SqlSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
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

    public Map<String, String> getNodesMapping() {
        return nodesMapping;
    }

}
