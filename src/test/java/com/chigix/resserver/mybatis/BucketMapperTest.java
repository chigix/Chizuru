package com.chigix.resserver.mybatis;

import java.io.IOException;
import java.util.UUID;
import org.apache.ibatis.exceptions.PersistenceException;
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
public class BucketMapperTest {

    BucketMapper mapper;

    private SqlSession session;

    public BucketMapperTest() {
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
    }

    @After
    public void tearDown() {
        session.update("com.chigix.resserver.mybatis.DbInitMapper.deleteBucketTable");
    }

    /**
     * Test of createTable method, of class BucketMapper.
     */
    @Test
    public void testSelectAll() {
        System.out.println("selectAll");
        mapper.insert(
                UUID.randomUUID().toString().replaceAll("-", ""),
                "BANKAI",
                new DateTime(DateTimeZone.forID("GMT")).toString());
        mapper.insert(
                UUID.randomUUID().toString().replaceAll("-", ""),
                "JIKAI",
                new DateTime(DateTimeZone.forID("GMT")).toString());
        mapper.insert(
                UUID.randomUUID().toString().replaceAll("-", ""),
                "ICHIGO",
                new DateTime(DateTimeZone.forID("GMT")).toString());
        mapper.insert(
                UUID.randomUUID().toString().replaceAll("-", ""),
                "RUSHIA",
                new DateTime(DateTimeZone.forID("GMT")).toString());
        assertEquals(4, mapper.selectAll().size());
        assertEquals(2, mapper.selectAll((String) mapper.selectByName("ICHIGO").get("UUID")).size());
        assertEquals(2, mapper.selectAll((String) mapper.selectByName("ICHIGO").get("UUID"), 1000).size());
    }

    /**
     * Test of insert method, of class BucketMapper.
     */
    @Test
    public void testInsert() {
        System.out.println("insert");
        assertEquals(1, mapper.insert(
                UUID.randomUUID().toString().replace("-", ""),
                "BANKAI",
                new DateTime(DateTimeZone.forID("GMT")).toString()));
        try {
            mapper.insert(
                    UUID.randomUUID().toString().replace("-", ""),
                    "BANKAI",
                    new DateTime(DateTimeZone.forID("GMT")).toString());
            fail("Unique index constraint doesn't work");
        } catch (PersistenceException e) {
            if (e.getMessage().indexOf("Unique index or primary key violation") < 1) {
                fail("Unique index constraint doesn't work");
            }
        }
    }

    /**
     * Test of deleteByName method, of class BucketMapper.
     */
    @Test
    public void testDeleteByName() {
        System.out.println("deleteByName");
        mapper.insert(
                UUID.randomUUID().toString().replaceAll("-", ""),
                "BANKAI",
                new DateTime(DateTimeZone.forID("GMT")).toString());
        mapper.insert(
                UUID.randomUUID().toString().replaceAll("-", ""),
                "JIKAI",
                new DateTime(DateTimeZone.forID("GMT")).toString());
        assertEquals(1, mapper.deleteByName("BANKAI"));
    }

    /**
     * Test of deleteByUuid method, of class BucketMapper.
     */
    @Test
    public void testDeleteByUuid() {
        System.out.println("deleteByUuid");
        String delete_uuid = UUID.randomUUID().toString().replaceAll("-", "");
        mapper.insert(
                delete_uuid,
                "BANKAI",
                new DateTime(DateTimeZone.forID("GMT")).toString());
        mapper.insert(
                UUID.randomUUID().toString().replaceAll("-", ""),
                "JIKAI",
                new DateTime(DateTimeZone.forID("GMT")).toString());
        assertEquals(1, mapper.deleteByUuid(delete_uuid));
    }

    /**
     * Test of selectByName method, of class BucketMapper.
     */
    @Test
    public void testSelectByName() {
        System.out.println("selectByName");
        String delete_uuid = UUID.randomUUID().toString().replaceAll("-", "");
        mapper.insert(
                delete_uuid,
                "BANKAI",
                new DateTime(DateTimeZone.forID("GMT")).toString());
        mapper.insert(
                UUID.randomUUID().toString().replaceAll("-", ""),
                "JIKAI",
                new DateTime(DateTimeZone.forID("GMT")).toString());
        System.out.println(mapper.selectByName("BANKAI"));
    }

    /**
     * Test of selectByUUID method, of class BucketMapper.
     */
    @Test
    public void testSelectByUUID() {
        System.out.println("selectByUUID");
        String uuid = "";
    }

}
