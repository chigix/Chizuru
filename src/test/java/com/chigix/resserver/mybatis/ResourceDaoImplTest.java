package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.AmassedResource;
import com.chigix.resserver.domain.Bucket;
import com.chigix.resserver.domain.Chunk;
import com.chigix.resserver.domain.ChunkedResource;
import com.chigix.resserver.domain.Resource;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.domain.error.NoSuchKey;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import com.chigix.resserver.mybatis.bean.ResourceExtension;
import com.chigix.resserver.mybatis.dto.ResourceBuilder;
import com.chigix.resserver.mybatis.dto.ResourceDto;
import java.io.IOException;
import java.util.Iterator;
import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ResourceDaoImplTest {

    private ResourceDaoImpl resourceDao;
    private SqlSession session;

    private BucketMapper bucketMapper;
    private ResourceMapper resourceMapper;

    public ResourceDaoImplTest() {
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
        session.update("com.chigix.resserver.mybatis.DbInitMapper.createResourceTable");
        session.update("com.chigix.resserver.mybatis.DbInitMapper.createChunkTable");
        resourceMapper = session.getMapper(ResourceMapper.class);
        bucketMapper = session.getMapper(BucketMapper.class);
        resourceDao = new ResourceDaoImpl(resourceMapper, session.getMapper(ChunkMapper.class));
        resourceDao.setBucketDao(new BucketDaoImpl(bucketMapper));
    }

    @After
    public void tearDown() {
        session.update("com.chigix.resserver.mybatis.DbInitMapper.deleteBucketTable");
        session.update("com.chigix.resserver.mybatis.DbInitMapper.deleteResourceTable");
        session.update("com.chigix.resserver.mybatis.DbInitMapper.deleteChunkTable");
    }

    /**
     * Test of setBucketDao method, of class ResourceDaoImpl.
     */
    @Test
    public void testSetBucketDao() {
        System.out.println("setBucketDao");
    }

    /**
     * Test of findResource method, of class ResourceDaoImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testFindResource_String_String() throws Exception {
        System.out.println("findResource");
        BucketBean bucket = new BucketBean("test_bucket");
        bucketMapper.insert(bucket.getUuid(), bucket.getName(), bucket.getCreationTime().toString());
        ChunkedResourceBean r = new ChunkedResourceBean("RES_1", ResourceExtension.hashKey(bucket.getUuid(), "RES_1"));
        r.setMetaData("Content-Type", "BANKAI");
        r.setBucket(bucket);
        resourceMapper.insertResource(new ResourceDto(r));
        Resource result = resourceDao.findResource(bucket.getName(), r.getKey());
        assertEquals(bucket.getUuid(), ((BucketBean) result.getBucket()).getUuid());
        assertEquals("BANKAI", result.snapshotMetaData().get("Content-Type"));
        bucketMapper.deleteByUuid(bucket.getUuid());
        assertEquals(bucket.getUuid(), ((BucketBean) result.getBucket()).getUuid());
        try {
            resourceDao.findResource(bucket.getName(), r.getKey());
            fail("NoSuchKey Exception should be thrown here.");
        } catch (NoSuchKey e) {
        }
    }

    /**
     * Test of findResource method, of class ResourceDaoImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testFindResource_Bucket_String() throws Exception {
        System.out.println("findResource");
        BucketBean bucket = new BucketBean("test_bucket");
        bucketMapper.insert(bucket.getUuid(), bucket.getName(), bucket.getCreationTime().toString());
        ChunkedResourceBean r = new ChunkedResourceBean("RES_1", ResourceExtension.hashKey(bucket.getUuid(), "RES_1"));
        r.setMetaData("Content-Type", "BANKAI");
        r.setBucket(bucket);
        resourceMapper.insertResource(new ResourceDto(r));
        Resource result = resourceDao.findResource(bucket, r.getKey());
        assertEquals(bucket.getUuid(), ((BucketBean) result.getBucket()).getUuid());
        assertEquals("BANKAI", result.snapshotMetaData().get("Content-Type"));
        bucketMapper.deleteByUuid(bucket.getUuid());
        assertEquals(bucket.getUuid(), ((BucketBean) result.getBucket()).getUuid());
        result = resourceDao.findResource(bucket, r.getKey());
        try {
            result.getBucket();
            fail("NoSuchBucket Exception should be thrown here.");
        } catch (NoSuchBucket noSuchBucket) {
        }
    }

    /**
     * Test of findResource method, of class ResourceDaoImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testFindResource_3args() throws Exception {
        System.out.println("findResource");
        BucketBean bucket = new BucketBean("test_bucket");
        bucketMapper.insert(bucket.getUuid(), bucket.getName(), bucket.getCreationTime().toString());
        ChunkedResourceBean r = new ChunkedResourceBean("RES_1", ResourceExtension.hashKey(bucket.getUuid(), "RES_1"));
        r.setMetaData("Content-Type", "BANKAI");
        r.setBucket(bucket);
        resourceMapper.insertResource(new ResourceDto(r));
        Resource result = resourceDao.findResource(bucket, r.getKey(), r.getVersionId());
        assertEquals(bucket.getUuid(), ((BucketBean) result.getBucket()).getUuid());
        assertEquals("BANKAI", result.snapshotMetaData().get("Content-Type"));
        bucketMapper.deleteByUuid(bucket.getUuid());
        assertEquals(bucket.getUuid(), ((BucketBean) result.getBucket()).getUuid());
        try {
            resourceDao.findResource(bucket.getName(), r.getKey());
            fail("NoSuchKey Exception should be thrown here.");
        } catch (NoSuchKey e) {
        }
    }

    /**
     * Test of saveResource method, of class ResourceDaoImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSaveResource_1() throws Exception {
        System.out.println("saveResource_1");
        final BucketBean bb = new BucketBean("TEST_BUCKET");
        bucketMapper.insert(bb.getUuid(), bb.getName(), bb.getCreationTime().toString());
        Resource r = new ChunkedResource("TEST_RESOURCE") {
            @Override
            public Iterator<Chunk> getChunks() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Bucket getBucket() throws NoSuchBucket {
                return bb;
            }

        };
        resourceDao.saveResource(r);
        ResourceBuilder map = resourceMapper.selectByBucketName_Key(bb.getName(), r.getKey());
        assertEquals(ResourceExtension.hashKey(bb.getUuid(), r.getKey()), map.getKeyHash());
        assertEquals(bb.getUuid(), map.getBucketUuid());
        assertEquals("ChunkedResource", map.getResourceType());
    }

    @Test
    public void testSaveResource_2() throws Exception {
        System.out.println("saveResource_1");
        final BucketBean bb = new BucketBean("TEST_BUCKET");
        bucketMapper.insert(bb.getUuid(), bb.getName(), bb.getCreationTime().toString());
        ChunkedResourceBean r = new ChunkedResourceBean("TEST_RESOURCE", ResourceExtension.hashKey(bb.getUuid(), "TEST_RESOURCE"));
        r.setBucket(bb);
        resourceDao.saveResource(r);
        ResourceBuilder map = resourceMapper.selectByBucketName_Key(bb.getName(), r.getKey());
        assertEquals(r.getKeyHash(), map.getKeyHash());
        assertEquals(bb.getUuid(), map.getBucketUuid());
        assertEquals("ChunkedResource", map.getResourceType());
    }

    /**
     * Test of listResources method, of class ResourceDaoImpl.
     *
     * @TODO test with continuation token.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testListResources() throws Exception {
        System.out.println("listResources");
        BucketBean bb = new BucketBean("TEST_BUCKET");
        bucketMapper.insert(bb.getUuid(), bb.getName(), bb.getCreationTime().toString());
        ChunkedResourceBean[] resources = new ChunkedResourceBean[6];
        for (int i = 0; i < resources.length; i++) {
            resources[i] = new ChunkedResourceBean("REC_" + i, ResourceExtension.hashKey(bb.getUuid(), "REC_" + i));
            resources[i].setBucket(bb);
            resourceMapper.insertResource(new ResourceDto(resources[i], bb));
        }
        Iterator<Resource> it = resourceDao.listResources(bb);
        for (ChunkedResourceBean resource : resources) {
            assertEquals(resource.getVersionId(), it.next().getVersionId());
        }
    }

    /**
     * Test of removeResource method, of class ResourceDaoImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testRemoveResource() throws Exception {
        System.out.println("removeResource");
        BucketBean bb = new BucketBean("TEST_BUCKET");
        bucketMapper.insert(bb.getUuid(), bb.getName(), bb.getCreationTime().toString());
        ChunkedResourceBean r = new ChunkedResourceBean("TEST_RESOURCE", ResourceExtension.hashKey(bb.getUuid(), "TEST_RESOURCE"));
        r.setBucket(bb);
        resourceMapper.insertResource(new ResourceDto(r));
        assertEquals(1, resourceMapper.selectAllByBucketName(bb.getName(), 1000).size());
        resourceDao.removeResource(r);
        assertEquals(0, resourceMapper.selectAllByBucketName(bb.getName(), 1000).size());
    }

    /**
     * Test of putChunk method, of class ResourceDaoImpl.
     */
    @Ignore
    @Test
    public void testAppendChunk() {
        System.out.println("appendChunk");
        ChunkedResource r = null;
        Chunk c = null;
        ResourceDaoImpl instance = null;
        instance.putChunk(r, c, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listSubResources method, of class ResourceDaoImpl.
     */
    @Ignore
    @Test
    public void testListSubResources() {
        System.out.println("listSubResources");
        AmassedResource parent = null;
        ResourceDaoImpl instance = null;
        Iterator<ChunkedResource> expResult = null;
        Iterator<ChunkedResource> result = instance.listSubResources(parent);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
