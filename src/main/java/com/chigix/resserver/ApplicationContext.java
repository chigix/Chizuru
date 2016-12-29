package com.chigix.resserver;

import java.io.File;
import org.mapdb.DB;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ApplicationContext {

    private final File dataDir;

    private final DB db;

    public ApplicationContext(File dataDir, DB db) {
        this.dataDir = dataDir;
        this.db = db;
    }

}
