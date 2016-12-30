package com.chigix.resserver;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.joda.time.DateTime;
import org.mapdb.DB;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ApplicationContext {

    private final Map<String, String> nodesMapping;

    private final File chunksDir;

    private final DB db;

    private final String currentNodeId;

    private final String bucketName;

    private final DateTime creationDate;

    public ApplicationContext(String currentNodeId, String bucketName, DateTime creationDate, File dataDir, DB db) {
        this.currentNodeId = currentNodeId;
        this.chunksDir = dataDir;
        this.db = db;
        this.nodesMapping = new HashMap<>();
        this.nodesMapping.put(currentNodeId, "127.0.0.1");
        this.bucketName = bucketName;
        this.creationDate = creationDate;
    }

    public DB getDb() {
        return db;
    }

    public String getCurrentNodeId() {
        return currentNodeId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

}
