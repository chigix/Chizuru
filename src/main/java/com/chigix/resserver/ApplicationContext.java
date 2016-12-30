package com.chigix.resserver;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.mapdb.DB;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ApplicationContext {

    private final Map<String, String> nodesMapping;

    private final File dataDir;

    private final DB db;

    private final String currentNodeId;

    public ApplicationContext(String currentNodeId, File dataDir, DB db) {
        this.currentNodeId = currentNodeId;
        this.dataDir = dataDir;
        this.db = db;
        this.nodesMapping = new HashMap<>();
        this.nodesMapping.put(currentNodeId, "127.0.0.1");
    }

    public DB getDb() {
        return db;
    }

    public String getCurrentNodeId() {
        return currentNodeId;
    }

}
