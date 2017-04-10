package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.Chunk;
import java.io.IOException;
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
public class ChunkDaoImplTest {

    private ChunkDaoImpl chunkDao;

    private ChunkMapper chunkMapper;

    private SqlSession session;

    public ChunkDaoImplTest() {
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
        chunkMapper = session.getMapper(ChunkMapper.class);
        chunkDao = new ChunkDaoImpl(chunkMapper);
    }

    @After
    public void tearDown() {
        session.update("com.chigix.resserver.mybatis.DbInitMapper.deleteChunkTable");
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
        assertNull(chunkDao.saveChunkIfAbsent(new Chunk("AAAAAA", 0, "TEST_LOCATION")));
        assertNotNull(chunkDao.saveChunkIfAbsent(new Chunk("AAAAAA", 0, "TEST_LOCATION")));
    }

}
