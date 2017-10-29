package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.Bucket;
import com.chigix.resserver.domain.Chunk;
import com.chigix.resserver.domain.ChunkedResource;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.mybatis.dao.ChunkMapper;
import com.chigix.resserver.util.Authorization;
import java.util.Iterator;
import java.util.UUID;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import com.chigix.resserver.domain.dao.ChunkDao;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:appContext.xml")
@Transactional
@TransactionConfiguration(transactionManager = "transactionManager_Chizuru")
public class ChunkDaoImplTest {

    @Autowired
    private ChunkDao chunkRepository;

    @Autowired
    private ChunkMapper chunkDao;

    public ChunkDaoImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test of newChunk method, of class ChunkDaoImpl.
     */
    @Test
    public void testNewChunk() {
        System.out.println("newChunk");
    }

    /**
     * Test of saveChunkIfAbsent method, of class ChunkDaoImpl.
     */
    @Test
    public void testSaveChunkIfAbsent() {
        System.out.println("saveChunkIfAbsent");
        assertNull(chunkRepository.saveChunkIfAbsent(new Chunk("AAAAAA", 0, "TEST_LOCATION")));
        assertNotNull(chunkRepository.saveChunkIfAbsent(new Chunk("AAAAAA", 0, "TEST_LOCATION")));
    }

    /**
     * Test of listChunksByResource method, of class ChunkDaoImpl.
     */
    @Test
    public void testListChunksByResource() {
        System.out.println("listChunksByResource");
        final ChunkedResource r = new ChunkedResource("TEST RESOURCE") {
            @Override
            public Iterator<Chunk> getChunks() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Bucket getBucket() throws NoSuchBucket {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        for (int i = 0; i < 1000; i++) {
            final com.chigix.resserver.mybatis.record.Chunk record = new com.chigix.resserver.mybatis.record.Chunk();
            record.setContentHash(Authorization.HexEncode(
                    Authorization.SHA256(
                            UUID.randomUUID().toString().getBytes())));
            record.setIndexInParent(i + "");
            record.setLocationId("TEST_LOCATION");
            record.setParentVersionId(r.getVersionId());
            record.setSize(2048);
            chunkDao.insert(record);
        }
        ChunkDaoImpl repository = (ChunkDaoImpl) chunkRepository;
        Iterator<Chunk> chunks = repository.listChunksByResource(r);
        for (int i = 0; i < 1000; i++) {
            assertNotNull(chunks.next());
        }
    }

}
