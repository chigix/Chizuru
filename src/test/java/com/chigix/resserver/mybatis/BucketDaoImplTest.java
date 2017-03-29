package com.chigix.resserver.mybatis;

import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.error.BucketAlreadyExists;
import com.chigix.resserver.entity.error.NoSuchBucket;
import com.chigix.resserver.mybatis.bean.BucketBean;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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
public class BucketDaoImplTest {

    private BucketDaoImpl dao;

    private BucketMapper mapper;

    private SqlSession session;

    public BucketDaoImplTest() {
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
        session.update("com.chigix.resserver.mybatis.DbInitMapper.createBucketTable");
        mapper = session.getMapper(BucketMapper.class);
        dao = new BucketDaoImpl(mapper);
    }

    @After
    public void tearDown() {
        session.update("com.chigix.resserver.mybatis.DbInitMapper.deleteBucketTable");
    }

    /**
     * Test of findBucketByName method, of class BucketDaoImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testFindBucketByName() throws Exception {
        System.out.println("findBucketByName");
        DateTime to_test = new DateTime(DateTimeZone.forID("GMT"));
        mapper.insert(
                UUID.randomUUID().toString().replaceAll("-", ""),
                "BANKAI",
                to_test.toString());
        mapper.insert(
                UUID.randomUUID().toString().replaceAll("-", ""),
                "JIKAI",
                new DateTime(DateTimeZone.forID("GMT")).toString());
        assertEquals(to_test.toString(), dao.findBucketByName("BANKAI").getCreationTime().toString());
        assertEquals(32, ((BucketBean) dao.findBucketByName("BANKAI")).getUuid().length());
    }

    /**
     * Test of iteratorBucket method, of class BucketDaoImpl.
     */
    @Test
    public void testIteratorBucket() {
        System.out.println("iteratorBucket");
        String[] names = new String[]{"BANKAI", "JIKAI", "ICHIGO", "RUKIA"};
        for (String name : names) {
            mapper.insert(
                    UUID.randomUUID().toString().replaceAll("-", ""),
                    name,
                    new DateTime(DateTimeZone.forID("GMT")).toString());
        }
        Iterator<Bucket> it = dao.iteratorBucket();
        for (String name : names) {
            assertEquals(name, it.next().getName());
        }
    }

    /**
     * Test of createBucket method, of class BucketDaoImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateBucket() throws Exception {
        System.out.println("createBucket");
        assertEquals("BANKAI", dao.createBucket("BANKAI").getName());
        try {
            dao.createBucket("BANKAI");
            fail("BucketAlreadyOwnedByYou should be thrown");
        } catch (BucketAlreadyExists bucketAlreadyExists) {
        }
    }

    /**
     * Test of deleteBucketByName method, of class BucketDaoImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDeleteBucketByName() throws Exception {
        System.out.println("deleteBucketByName");
        String[] names = new String[]{"BANKAI", "JIKAI", "ICHIGO", "RUKIA"};
        for (String name : names) {
            mapper.insert(
                    UUID.randomUUID().toString().replaceAll("-", ""),
                    name,
                    new DateTime(DateTimeZone.forID("GMT")).toString());
        }
        assertEquals("BANKAI", dao.deleteBucketByName("BANKAI").getName());
        try {
            dao.deleteBucketByName("NO_EXISTED");
            fail("NoSuchBucket Should be thrown.");
        } catch (NoSuchBucket noSuchBucket) {
        }
    }

}
