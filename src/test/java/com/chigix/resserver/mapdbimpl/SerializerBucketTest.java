package com.chigix.resserver.mapdbimpl;

import com.chigix.resserver.entity.Bucket;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mapdb.DB;
import org.mapdb.DBException;
import org.mapdb.DBMaker;
import org.mapdb.DataOutput2;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class SerializerBucketTest {

    private DB db;

    public SerializerBucketTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        db = DBMaker.memoryDB().make();
    }

    @After
    public void tearDown() {
        db.close();
        db = null;
    }

    /**
     * Test of serialize method, of class SerializerBucket.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSerialize() throws Exception {
        System.out.println("serialize");
        Bucket b = new Bucket("TESTING_BUCKET", DateTime.parse("2017-01-20T02:19:36.037Z"));
        StringBuilder sb = new StringBuilder();
        ConcurrentMap<String, Bucket> map = db.hashMap("TESTING_DB", org.mapdb.Serializer.STRING_ASCII, new SerializerBucket() {
            @Override
            public void serialize(DataOutput2 out, Bucket value) throws IOException {
                super.serialize(out, value);
                sb.append(new String(out.copyBytes()));
            }

        }).createOrOpen();
        map.put("TESTING_BUCKET", b);
        assertTrue(sb.toString().indexOf("<Name>TESTING_BUCKET</Name>") > 0);
        assertTrue(sb.toString().indexOf("<CreationTime>2017-01-20T02:19:36.037Z</CreationTime>") > 0);
        try {
            map.put("JJ", new BucketInStorage("JJ"));
            fail("ResourceInStorageNotSupportException Should be thrown.");
        } catch (DBException.SerializationError e) {
            if (e.getCause() instanceof SerializerBucket.ResourceInStorageNotSupportException) {
            } else {
                throw e;
            }
        }
    }

    /**
     * Test of deserialize method, of class SerializerBucket.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDeserialize() throws Exception {
        System.out.println("deserialize");
        Bucket b = new Bucket("Testing_bucket");
        ConcurrentMap<String, Bucket> map = db.hashMap("TESTING_DB", org.mapdb.Serializer.STRING_ASCII, new SerializerBucket()).createOrOpen();
        assertNull(map.get("NOT_EXISTED"));
        map.put("Testing_bucket", b);
        BucketInStorage r = (BucketInStorage) map.get("Testing_bucket");
        assertEquals(b.getName(), r.getName());
        assertEquals(b.getCreationTime().toString(), r.getCreationTime().toString());
        assertTrue(r.getUUID().length() > 0);
    }

}
