package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.Chunk;
import com.chigix.resserver.domain.ChunkedResource;
import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ChunkMapperTest {

    private ChunkMapper mapper;

    private SqlSession session;

    public ChunkMapperTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException {
        session = TestUtils.setUpDatabase("chizuru");
        session.update("com.chigix.resserver.mybatis.DbInitMapper.createChunkTable");
        mapper = session.getMapper(ChunkMapper.class);
    }

    @After
    public void tearDown() {
        session.update("com.chigix.resserver.mybatis.DbInitMapper.deleteChunkTable");
    }

    /**
     * Test of appendChunkToVersion method, of class ChunkMapper.
     */
    @Test
    public void testAppendChunkToVersion() {
        System.out.println("appendChunkToVersion");
        ChunkedResource r = new ChunkedResourceBean("TEST_KEY", "KEY_HASH");
        Chunk c = new Chunk("BANKAI", 1234, "LOCATION_ID");
        mapper.appendChunkToVersion(r.getVersionId(), c);
        assertEquals(1, mapper.selectAll().size());
    }

    /**
     * Test of selectFirstChunkReference method, of class ChunkMapper.
     */
    @Test
    public void testSelectFirstChunkReference() {
        System.out.println("selectFirstChunkReference");
        ChunkedResource r_1 = new ChunkedResourceBean("TEST_KEY", "KEY_HASH");
        ChunkedResource r_2 = new ChunkedResourceBean("TEST_KEY", "KEY_HASH");
        Chunk c = new Chunk("CONTENT_HASH", 1234, UUID.randomUUID().toString().replace("-", ""));
        mapper.appendChunkToVersion(r_1.getVersionId(), c);
        mapper.appendChunkToVersion(r_2.getVersionId(), c);
        assertEquals(r_1.getVersionId(), mapper.selectFirstChunkReference("CONTENT_HASH").get("PARENT_VERSION_ID"));
    }

    /**
     * Test of selectAll method, of class ChunkMapper.
     */
    @Test
    public void testSelectAll() {
        System.out.println("selectAll");
    }

    /**
     * Test of selectByVersion method, of class ChunkMapper.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSelectByVersion() throws Exception {
        System.out.println("selectByVersion");
        final String location_id = UUID.randomUUID().toString();
        final ChunkedResourceBean[] resources = new ChunkedResourceBean[10];
        final Map<String, Chunk[]> resourceChunks = new HashMap<>();
        final CountDownLatch signal_1 = new CountDownLatch(resources.length);
        final AtomicInteger count = new AtomicInteger();
        for (count.set(0); count.get() < resources.length; count.incrementAndGet()) {
            final int i = count.get();
            new Thread() {
                @Override
                public void run() {
                    ChunkedResourceBean resource = new ChunkedResourceBean("KEY_" + i, UUID.randomUUID().toString().replace("-", ""));
                    resources[i] = resource;
                    resourceChunks.put(resource.getKey(), new Chunk[10000]);
                    Chunk[] chunks = resourceChunks.get(resource.getKey());
                    for (int j = 0; j < chunks.length; j++) {
                        Chunk chunk = new Chunk(UUID.randomUUID().toString().replace("-", ""), i, location_id);
                        chunks[j] = chunk;
                        mapper.appendChunkToVersion(resource.getVersionId(), chunk);
                    }
                    signal_1.countDown();
                }
            }.start();
        }
        signal_1.await();
        final CountDownLatch signal_2 = new CountDownLatch(resources.length);
        for (int i = 0; i < resources.length; i++) {
            final int j = i;
            new Thread() {
                @Override
                public void run() {
                    ChunkedResourceBean resource = new ChunkedResourceBean(resources[j].getKey(),
                            resources[j].getKeyHash());
                    resources[j] = resource;
                    Chunk[] chunks = resourceChunks.get(resource.getKey());
                    for (Chunk chunk : chunks) {
                        mapper.appendChunkToVersion(resource.getVersionId(), chunk);
                    }
                    signal_2.countDown();
                }
            }.start();
        }
        signal_2.await();
        for (ChunkedResource resource : resources) {
            String continuation = null;
            List<Map<String, String>> versions;
            int index = 0;
            Chunk[] chunks = resourceChunks.get(resource.getKey());
            while (true) {
                String skip = null;
                if (continuation == null) {
                    versions = mapper.selectByVersion(resource.getVersionId());
                } else {
                    versions = mapper.selectByVersion(resource.getVersionId(), continuation);
                    skip = continuation;
                }
                for (Map<String, String> version : versions) {
                    if (version.get("CONTENT_HASH").equals(skip)) {
                        continue;
                    }
                    System.out.println("Comparison === " + index);
                    assertEquals(chunks[index].getContentHash(), version.get("CONTENT_HASH"));
                    continuation = version.get("CONTENT_HASH");
                    index++;
                }
                if (versions.size() < 1000) {
                    break;
                }
            }
        }
    }

}
