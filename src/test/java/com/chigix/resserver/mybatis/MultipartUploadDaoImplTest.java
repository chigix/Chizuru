package com.chigix.resserver.mybatis;

import com.chigix.resserver.entity.AmassedResource;
import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.Chunk;
import com.chigix.resserver.entity.ChunkedResource;
import com.chigix.resserver.entity.MultipartUpload;
import com.chigix.resserver.entity.error.NoSuchBucket;
import com.chigix.resserver.mybatis.bean.BucketBean;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
public class MultipartUploadDaoImplTest {

    private SqlSession uploadingSession;
    private SqlSession mainSession;

    private ResourceMapper uploadingResourceMapper;
    private ResourceMapper uploadedResourceMapper;
    private MultipartUploadMapper uploadMapper;
    private BucketDaoImpl bucketDao;
    private MultipartUploadDaoImpl uploadDao;
    private ResourceDaoImpl uploadingResourceDao;
    private ResourceDaoImpl uploadedResourceDao;

    public MultipartUploadDaoImplTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException {
        mainSession = TestUtils.setUpDatabase("chizuru");
        uploadingSession = TestUtils.setUpDatabase("upload");
        mainSession.update("com.chigix.resserver.mybatis.DbInitMapper.createBucketTable");
        mainSession.update("com.chigix.resserver.mybatis.DbInitMapper.createResourceTable");
        mainSession.update("com.chigix.resserver.mybatis.DbInitMapper.createChunkTable");
        uploadingSession.update("com.chigix.resserver.mybatis.MultipartUploadMapper.createUploadTable");
        uploadMapper = uploadingSession.getMapper(MultipartUploadMapper.class);
        BucketMapper bucketMapper = mainSession.getMapper(BucketMapper.class);
        ChunkMapper chunkMapper = mainSession.getMapper(ChunkMapper.class);
        uploadingResourceMapper = uploadingSession.getMapper(ResourceMapper.class);
        uploadedResourceMapper = mainSession.getMapper(ResourceMapper.class);
        uploadMapper = uploadingSession.getMapper(MultipartUploadMapper.class);
        bucketDao = new BucketDaoImpl(bucketMapper);
        uploadingResourceDao = new ResourceDaoImpl(uploadingResourceMapper, chunkMapper);
        uploadedResourceDao = new ResourceDaoImpl(uploadedResourceMapper, chunkMapper);
        uploadingResourceDao.setBucketDao(bucketDao);
        uploadedResourceDao.setBucketDao(bucketDao);
        uploadDao = new MultipartUploadDaoImpl(uploadMapper, uploadedResourceDao, uploadingResourceDao);
    }

    @After
    public void tearDown() {
        mainSession.update("com.chigix.resserver.mybatis.DbInitMapper.deleteBucketTable");
        mainSession.update("com.chigix.resserver.mybatis.DbInitMapper.deleteResourceTable");
        mainSession.update("com.chigix.resserver.mybatis.DbInitMapper.deleteChunkTable");
        uploadingSession.update("com.chigix.resserver.mybatis.DbInitMapper.deleteUploadTable");
    }

    private List<Map<String, String>> selectAllUploadRows() {
        return uploadingSession.selectList("com.chigix.resserver.mybatis.MultipartUploadMapper.selectAll");
    }

    private List<Map<String, String>> selectAllResourceRows(SqlSession session) {
        return session.selectList("com.chigix.resserver.mybatis.ResourceMapper.selectAll");
    }

    private List<Map<String, String>> selectAllSubResourceRows(SqlSession session) {
        return session.selectList("com.chigix.resserver.mybatis.ResourceMapper.selectAllSubResources");
    }

    /**
     * Test of initiateUpload method, of class MultipartUploadDaoImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInitiateUpload() throws Exception {
        System.out.println("initiateUpload");
        final BucketBean bb = bucketDao.createBucket("TEST_BUCKET");
        AmassedResource r = new AmassedResource("TEST_AMASSEDRESOURCE") {
            @Override
            public Iterator<ChunkedResource> getSubResources() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Bucket getBucket() throws NoSuchBucket {
                return bb;
            }
        };
        uploadDao.initiateUpload(r);
        List<Map<String, String>> uploads = selectAllUploadRows();
        assertEquals(r.getVersionId(), uploads.get(0).get("resource_version"));
    }

    /**
     * Test of findUpload method, of class MultipartUploadDaoImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testFindUpload() throws Exception {
        System.out.println("findUpload");
        final MultipartUpload upload;
        final BucketBean bb = bucketDao.createBucket("TEST_BUCKET");
        AmassedResource r = new AmassedResource("TEST_AMASSEDRESOURCE") {
            @Override
            public Iterator<ChunkedResource> getSubResources() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Bucket getBucket() throws NoSuchBucket {
                return bb;
            }
        };
        uploadDao.initiateUpload(r);
        upload = uploadDao.initiateUpload(r);
        uploadDao.initiateUpload(r);
        uploadDao.initiateUpload(r);
        uploadDao.initiateUpload(r);
        uploadDao.initiateUpload(r);
        assertEquals(upload.getInitiated().toString(),
                uploadDao.findUpload(upload.getUploadId()).getInitiated().toString());
    }

    /**
     * Test of findSubResourcePart method, of class MultipartUploadDaoImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testFindUploadPart() throws Exception {
        System.out.println("findUploadPart");
        final MultipartUpload upload;
        final BucketBean bb = bucketDao.createBucket("TEST_BUCKET");
        AmassedResource r = new AmassedResource("TEST_AMASSEDRESOURCE") {
            @Override
            public Iterator<ChunkedResource> getSubResources() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Bucket getBucket() throws NoSuchBucket {
                return bb;
            }
        };
        upload = uploadDao.initiateUpload(r);
        ChunkedResource ch = new ChunkedResource("KEY_SHOULD_NOT_BE_USED") {
            @Override
            public Iterator<Chunk> getChunks() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Bucket getBucket() throws NoSuchBucket {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        uploadDao.appendChunkedResource(upload, ch, "123");
        uploadDao.appendChunkedResource(upload, ch, "123");
        uploadDao.appendChunkedResource(upload, ch, "123");
        assertEquals(ch.getVersionId(),
                uploadDao.findUploadPart(upload, "123", ch.getETag()).getVersionId());
    }

    /**
     * Test of completeUpload method, of class MultipartUploadDaoImpl.
     */
    @Ignore
    @Test
    public void testCompleteUpload() throws Exception {
        System.out.println("completeUpload");
        MultipartUpload upload = null;
        MultipartUploadDaoImpl instance = null;
        MultipartUpload expResult = null;
        MultipartUpload result = instance.completeUpload(upload);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeUpload method, of class MultipartUploadDaoImpl.
     */
    @Ignore
    @Test
    public void testRemoveUpload() throws Exception {
        System.out.println("removeUpload");
        MultipartUpload upload = null;
        MultipartUploadDaoImpl instance = null;
        instance.removeUpload(upload);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listUploadsByBucket method, of class MultipartUploadDaoImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testListUploadsByBucket() throws Exception {
        System.out.println("listUploadsByBucket");
        final BucketBean bb = bucketDao.createBucket("TEST_BUCKET");
        AmassedResource r = new AmassedResource("TEST_AMASSEDRESOURCE") {
            @Override
            public Iterator<ChunkedResource> getSubResources() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Bucket getBucket() throws NoSuchBucket {
                return bb;
            }
        };
        uploadDao.initiateUpload(r);
        uploadDao.initiateUpload(r);
        uploadDao.initiateUpload(r);
        uploadDao.initiateUpload(r);
        uploadDao.initiateUpload(r);
        uploadDao.initiateUpload(r);
        uploadDao.listUploadsByBucket(bb);
    }

    /**
     * Test of appendChunkedResource method, of class MultipartUploadDaoImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAppendChunkedResource() throws Exception {
        System.out.println("appendChunkedResource");
        final MultipartUpload upload;
        final BucketBean bb = bucketDao.createBucket("TEST_BUCKET");
        AmassedResource r = new AmassedResource("TEST_AMASSEDRESOURCE") {
            @Override
            public Iterator<ChunkedResource> getSubResources() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Bucket getBucket() throws NoSuchBucket {
                return bb;
            }
        };
        Function<Object, ChunkedResource> create = (Object t) -> new ChunkedResource("KEY_SHOULD_NOT_BE_USED") {
            @Override
            public Iterator<Chunk> getChunks() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Bucket getBucket() throws NoSuchBucket {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        upload = uploadDao.initiateUpload(r);
        uploadDao.appendChunkedResource(upload, create.apply(null), "123");
        uploadDao.appendChunkedResource(upload, create.apply(null), "512345678901234567890123456789012");
        uploadDao.appendChunkedResource(upload, create.apply(null), "612345678901234567890123456789012");
        List<Map<String, String>> rows = selectAllSubResourceRows(uploadingSession);
        assertEquals(3, rows.size());
        assertEquals("123", rows.get(0).get("key"));
        assertEquals(r.getVersionId(), rows.get(0).get("parent_version_id"));
        assertEquals("ChunkedResource", rows.get(0).get("type"));
        assertEquals("12345678901234567890123456789012", rows.get(1).get("key"));
        assertEquals("12345678901234567890123456789012", rows.get(2).get("key"));
    }

}
