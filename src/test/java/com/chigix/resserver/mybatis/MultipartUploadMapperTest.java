package com.chigix.resserver.mybatis;

import com.chigix.resserver.entity.AmassedResource;
import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.ChunkedResource;
import com.chigix.resserver.entity.MultipartUpload;
import com.chigix.resserver.entity.error.NoSuchBucket;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.dto.MultipartUploadDto;
import java.io.IOException;
import java.util.Iterator;
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
public class MultipartUploadMapperTest {

    private MultipartUploadMapper mapper;

    private SqlSession session;

    public MultipartUploadMapperTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException {
        session = TestUtils.setUpDatabase("upload");
        session.update("com.chigix.resserver.mybatis.MultipartUploadMapper.createUploadTable");
        mapper = session.getMapper(MultipartUploadMapper.class);
    }

    @After
    public void tearDown() {
        session.update("com.chigix.resserver.mybatis.DbInitMapper.deleteUploadTable");
    }

    /**
     * Test of insert method, of class MultipartUploadMapper.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInsert() throws Exception {
        System.out.println("insert");
        final BucketBean bb = new BucketBean("TEST_BUCKET");
        AmassedResource r = new AmassedResource("TEST_RESOURCE") {
            @Override
            public Iterator<ChunkedResource> getSubResources() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Bucket getBucket() throws NoSuchBucket {
                return bb;
            }
        };
        assertEquals(1, mapper.insert(new MultipartUploadDto(new MultipartUpload(r))));
        assertEquals(1, mapper.insert(new MultipartUploadDto(new MultipartUpload(r))));
        assertEquals(1, mapper.insert(new MultipartUploadDto(new MultipartUpload(r))));
        assertEquals(3, session.selectList("com.chigix.resserver.mybatis.MultipartUploadMapper.selectAll").size());
    }

    /**
     * Test of selectByUuid method, of class MultipartUploadMapper.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSelectByUuid() throws Exception {
        System.out.println("selectByUuid");
        final BucketBean bb = new BucketBean("TEST_BUCKET");
        AmassedResource r = new AmassedResource("TEST_RESOURCE") {
            @Override
            public Iterator<ChunkedResource> getSubResources() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Bucket getBucket() throws NoSuchBucket {
                return bb;
            }
        };
        MultipartUpload u = new MultipartUpload(r);
        assertEquals(1, mapper.insert(new MultipartUploadDto(new MultipartUpload(r))));
        assertEquals(1, mapper.insert(new MultipartUploadDto(u)));
        assertEquals(1, mapper.insert(new MultipartUploadDto(new MultipartUpload(r))));
        assertEquals(1, mapper.insert(new MultipartUploadDto(new MultipartUpload(r))));
        assertEquals(2, mapper.selectByUuid(u.getUploadId()).get("ID"));
    }

    /**
     * Test of deleteByUuid method, of class MultipartUploadMapper.
     */
    @Test
    public void testDeleteByUuid() {
        System.out.println("deleteByUuid");
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of selectAllByBucketUuid method, of class MultipartUploadMapper.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSelectAllByBucketUuid() throws Exception {
        System.out.println("selectAllByBucketUuid");
        final BucketBean bb = new BucketBean("TEST_BUCKET");
        AmassedResource r = new AmassedResource("TEST_RESOURCE") {
            @Override
            public Iterator<ChunkedResource> getSubResources() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Bucket getBucket() throws NoSuchBucket {
                return bb;
            }
        };
        assertEquals(1, mapper.insert(new MultipartUploadDto(new MultipartUpload(r))));
        assertEquals(1, mapper.insert(new MultipartUploadDto(new MultipartUpload(r))));
        assertEquals(1, mapper.insert(new MultipartUploadDto(new MultipartUpload(r))));
        assertEquals(1, mapper.insert(new MultipartUploadDto(new MultipartUpload(r))));
        assertEquals(4, mapper.selectAllByBucketUuid(bb.getUuid()).size());
    }

}
