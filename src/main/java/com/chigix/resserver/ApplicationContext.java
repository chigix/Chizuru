package com.chigix.resserver;

import com.chigix.resserver.entity.Chunk;
import com.chigix.resserver.entity.dao.BucketDao;
import com.chigix.resserver.entity.dao.ResourceDao;
import com.chigix.resserver.mapdbimpl.dao.BucketKeys;
import com.chigix.resserver.mapdbimpl.dao.ResourceKeys;
import com.chigix.resserver.mapdbimpl.dao.BucketDaoImpl;
import com.chigix.resserver.mapdbimpl.dao.ChunkDaoImpl;
import com.chigix.resserver.mapdbimpl.dao.ChunkKeys;
import com.chigix.resserver.mapdbimpl.dao.ResourceDaoImpl;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.joda.time.DateTime;
import org.mapdb.DB;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ApplicationContext {

    private final String requestIdHeaderName = Long.toHexString(Double.doubleToLongBits(Math.random()));

    private final Map<String, String> nodesMapping;

    private final int maxChunkSize;

    private final File chunksDir;

    private final DB db;

    private final String currentNodeId;

    private final DateTime creationDate;

    public final BucketDao BucketDao;

    public final ResourceDao ResourceDao;

    public final ChunkDaoImpl ChunkDao;

    public ApplicationContext(String currentNodeId, DateTime creationDate, int maxChunkSize, File dataDir, DB db) {
        this.currentNodeId = currentNodeId;
        this.chunksDir = dataDir;
        this.maxChunkSize = maxChunkSize;
        this.db = db;
        this.nodesMapping = new HashMap<>();
        this.nodesMapping.put(currentNodeId, "127.0.0.1");
        this.creationDate = creationDate;
        BucketDao = new BucketDaoImpl(db);
        ResourceDao = new ResourceDaoImpl(db);
        ChunkDao = new ChunkDaoImpl(db) {
            @Override
            public Chunk newChunk(String contentHash, int chunk_size) {
                return new Chunk(contentHash, chunk_size, currentNodeId) {
                    @Override
                    public InputStream getInputStream() throws IOException {
                        return new FileInputStream(new File(chunksDir, this.getContentHash()));
                    }

                };
            }
        };
        ((ResourceDaoImpl) ResourceDao).assembleDaos((BucketDaoImpl) BucketDao, ChunkDao);
    }

    public String getRequestIdHeaderName() {
        return requestIdHeaderName;
    }

    public void updateDBScheme() {
        BucketKeys.updateDBScheme(db);
        ResourceKeys.updateDBScheme(db);
        ChunkKeys.updateDBScheme(db);
    }

    public String getCurrentNodeId() {
        return currentNodeId;
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    public File getChunksDir() {
        return chunksDir;
    }

    public int getMaxChunkSize() {
        return maxChunkSize;
    }

}
