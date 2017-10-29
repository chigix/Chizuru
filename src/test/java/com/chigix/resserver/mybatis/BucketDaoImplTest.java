package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.Bucket;
import com.chigix.resserver.domain.error.BucketAlreadyExists;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.dao.BucketMapper;
import com.chigix.resserver.mybatis.record.BucketExample;
import com.chigix.resserver.mybatis.record.Util;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:appContext.xml")
@Transactional
@TransactionConfiguration(transactionManager = "transactionManager_Chizuru")
public class BucketDaoImplTest implements ApplicationContextAware {

    @Autowired
    private BucketRepositoryExtend repository;

    @Autowired
    private BucketMapper mapper;

    private ApplicationContext springContext;

    public BucketDaoImplTest() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of findBucketByName method, of class BucketRepositoryImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testFindBucketByName() throws Exception {
        System.out.println(repository instanceof BucketRepositoryExtend);
        System.out.println("findBucketByName");
        DateTime to_test = new DateTime(DateTimeZone.forID("GMT"));
        com.chigix.resserver.mybatis.record.Bucket record_1 = new com.chigix.resserver.mybatis.record.Bucket();
        com.chigix.resserver.mybatis.record.Bucket record_2 = new com.chigix.resserver.mybatis.record.Bucket();
        record_1.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
        record_2.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
        record_1.setName("BANKAI");
        record_2.setName("JIKAI");
        record_1.setCreationTime(to_test);
        record_2.setCreationTime(new DateTime(DateTimeZone.forID("GMT")));
        mapper.insert(record_1);
        mapper.insert(record_2);
        assertEquals(to_test.toString(), repository.findBucketByName("BANKAI").getCreationTime().toString());
        assertEquals(32, ((BucketBean) repository.findBucketByName("BANKAI")).getUuid().length());
        System.out.println(mapper.selectByExample(new BucketExample()));
        System.out.println(mapper.selectByExampleWithRowbounds(new BucketExample(), Util.ONE_ROWBOUND));
        BucketExample ex = new BucketExample();
    }

    /**
     * Test of iteratorBucket method, of class BucketRepositoryImpl.
     */
    @Test
    @DirtiesContext
    public void testIteratorBucket() {
        System.out.println("iteratorBucket");
        String[] names = new String[]{"YUI", "MIO", "TUMUGI", "RITU", "AZUSA"};
        for (String name : names) {
            com.chigix.resserver.mybatis.record.Bucket record = new com.chigix.resserver.mybatis.record.Bucket();
            record.setName(name);
            record.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
            record.setCreationTime(new DateTime(DateTimeZone.forID("GMT")));
            mapper.insert(record);
        }
        Iterator<Bucket> it = repository.iteratorBucket();
        for (String name : names) {
            assertEquals(name, it.next().getName());
        }
    }

    /**
     * Test of createBucket method, of class BucketRepositoryImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateBucket() throws Exception {
        System.out.println("createBucket");
        assertEquals("BANKAI", repository.createBucket("BANKAI").getName());
        try {
            repository.createBucket("BANKAI");
            fail("BucketAlreadyOwnedByYou should be thrown");
        } catch (BucketAlreadyExists bucketAlreadyExists) {
        }
    }

    /**
     * Test of deleteBucketByName method, of class BucketRepositoryImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDeleteBucketByName() throws Exception {
        System.out.println("deleteBucketByName");
        String[] names = new String[]{"YUI", "MIO", "TUMUGI", "RITU", "AZUSA"};
        for (String name : names) {
            com.chigix.resserver.mybatis.record.Bucket record = new com.chigix.resserver.mybatis.record.Bucket();
            record.setName(name);
            record.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
            record.setCreationTime(new DateTime(DateTimeZone.forID("GMT")));
            mapper.insert(record);
        }
        assertEquals("MIO", repository.deleteBucketByName("MIO").getName());
        try {
            repository.deleteBucketByName("NO_EXISTED");
            fail("NoSuchBucket Should be thrown.");
        } catch (NoSuchBucket noSuchBucket) {
        }
    }

}
