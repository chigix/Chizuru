package com.chigix.resserver.mapdbimpl;

import com.chigix.resserver.entity.Chunk;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class SerializerChunkTest {

    private DB testingDb;

    public SerializerChunkTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        testingDb = DBMaker.memoryDB().make();
    }

    @After
    public void tearDown() {
        testingDb.close();
        testingDb = null;
    }

    /**
     * Test of serialize method, of class SerializerChunk.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSerialize() throws Exception {
        System.out.println("serialize");
        final StringBuffer sb = new StringBuffer();
        ConcurrentMap<String, Chunk> map = testingDb.hashMap("TestingDB", Serializer.STRING_ASCII, new SerializerChunk() {
            @Override
            public void serialize(DataOutput2 out, Chunk value) throws IOException {
                super.serialize(out, value);
                sb.append(new String(out.copyBytes()));
            }

        }).createOrOpen();
        map.put("BANKAI", new Chunk("JJJJ", 123));
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Chunk><ContentHash>JJJJ</ContentHash><Size>123</Size></Chunk>", sb.toString());
    }

    /**
     * Test of deserialize method, of class SerializerChunk.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDeserialize() throws Exception {
        System.out.println("deserialize");
        Chunk c = new Chunk(UUID.randomUUID().toString(), 4511);
        ConcurrentMap<String, Chunk> map = testingDb.hashMap("TestingDB", Serializer.STRING_ASCII, new SerializerChunk()).createOrOpen();
        map.put("EXISTED", c);
        Chunk r = map.get("NOT_EXISTED");
        assertNull(r);
        r = map.get("EXISTED");
        assertEquals(c.getSize(), r.getSize());
        assertEquals(c.getContentHash(), r.getContentHash());
    }

}
