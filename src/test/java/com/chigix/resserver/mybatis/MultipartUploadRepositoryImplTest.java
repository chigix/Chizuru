package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.model.resource.AmassedResource;
import com.chigix.resserver.domain.model.bucket.Bucket;
import com.chigix.resserver.domain.model.chunk.Chunk;
import com.chigix.resserver.domain.model.resource.ChunkedResource;
import com.chigix.resserver.domain.model.multiupload.MultipartUpload;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.domain.error.NoSuchUpload;
import com.chigix.resserver.mybatis.bean.AmassedResourceBean;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.dao.MultipartUploadMapper;
import com.chigix.resserver.mybatis.dao.ResourceMapper;
import com.chigix.resserver.mybatis.dao.SubresourceMapper;
import com.chigix.resserver.mybatis.mapstruct.MultipartUploadBeanMapper;
import com.chigix.resserver.mybatis.mapstruct.UploadingResourceBeanMapper;
import com.chigix.resserver.mybatis.record.MultipartUploadExample;
import com.chigix.resserver.mybatis.record.Subresource;
import com.chigix.resserver.mybatis.record.SubresourceExample;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@TransactionConfiguration(transactionManager = "transactionManager_Upload")
public class MultipartUploadRepositoryImplTest {

    @Autowired
    private UploadingResourceBeanMapper uploadingResourceBeanMapper;
    @Autowired
    private MultipartUploadBeanMapper uploadBeanMapper;

    @Autowired
    @Qualifier("uploadingResourceMapper")
    private ResourceMapper uploadingResourceDao;
    @Autowired
    @Qualifier("uploadingSubResourceMapper")
    private SubresourceMapper uploadingSubresourceDao;
    @Autowired
    private MultipartUploadMapper uploadDao;
    @Autowired
    private BucketRepositoryExtend bucketRepository;
    @Autowired
    private MultipartUploadRepositoryImpl uploadRepository;

    public MultipartUploadRepositoryImplTest() {
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
     * Test of initiateUpload method, of class MultipartUploadRepositoryImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInitiateUpload() throws Exception {
        System.out.println("initiateUpload");
        final BucketBean bb = bucketRepository.createBucket("TEST_BUCKET");
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
        uploadRepository.initiateUpload(r);
        List<com.chigix.resserver.mybatis.record.MultipartUpload> records = uploadDao.selectByExample(new MultipartUploadExample());
        assertEquals(r.getVersionId(), records.get(0).getResourceVersion());
    }

    /**
     * Test of findUpload method, of class MultipartUploadRepositoryImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testFindUpload() throws Exception {
        System.out.println("findUpload");
        final MultipartUpload upload;
        final BucketBean bb = bucketRepository.createBucket("TEST_BUCKET");
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
        uploadRepository.initiateUpload(r);
        upload = uploadRepository.initiateUpload(r);
        uploadRepository.initiateUpload(r);
        uploadRepository.initiateUpload(r);
        uploadRepository.initiateUpload(r);
        uploadRepository.initiateUpload(r);
        assertEquals(upload.getInitiated().toString(),
                uploadRepository.findUpload(upload.getUploadId()).getInitiated().toString());
    }

    /**
     * Test of findSubResourcePart method, of class
     * MultipartUploadRepositoryImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testFindUploadPart() throws Exception {
        System.out.println("findUploadPart");
        final MultipartUpload upload;
        final BucketBean bb = bucketRepository.createBucket("TEST_BUCKET");
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
        upload = uploadRepository.initiateUpload(r);
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
        uploadRepository.saveSubresource(upload, ch, "123");
        uploadRepository.saveSubresource(upload, ch, "123");
        uploadRepository.saveSubresource(upload, ch, "123");
        assertEquals(ch.getVersionId(),
                uploadRepository.findUploadPart(upload, "123", ch.getETag()).getVersionId());
    }

    /**
     * Test of removeUpload method, of class MultipartUploadRepositoryImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testRemoveUpload() throws Exception {
        System.out.println("removeUpload");
        final BucketBean bb = new BucketBean("TEST_BUCKET");
        MultipartUpload[] uploads = new MultipartUpload[10];
        // @TODO Add Test into UploadBeanMapper for the case of AmassedResource 
        // Domain Object, which should throw NotBeanException.
        for (int i = 0; i < uploads.length; i++) {
            uploads[i] = new MultipartUpload(
                    new AmassedResourceBean("TEST_AMASSED", "TEST_KEYHASH") {
                @Override
                public BucketBean getBucket() throws NoSuchBucket {
                    return bb;
                }
            });
        }
        for (MultipartUpload upload : uploads) {
            uploadDao.insert(uploadBeanMapper.toRecord(upload));
            uploadingResourceDao.mergeUploadingResource(uploadingResourceBeanMapper.toRecord(upload.getResource()));
        }
        uploadRepository.removeUpload(uploads[4]);
        final MultipartUploadExample byBucketUuid = new MultipartUploadExample();
        byBucketUuid.createCriteria().andBucketUuidEqualTo(bb.getUuid());
        assertEquals(9, uploadDao.selectByExample(byBucketUuid).size());
        try {
            uploadRepository.removeUpload(uploads[4]);
            fail("NoSuchUpload Is not thrown.");
        } catch (NoSuchUpload noSuchUpload) {
        }
    }

    /**
     * Test of listUploadsByBucket method, of class
     * MultipartUploadRepositoryImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testListUploadsByBucket() throws Exception {
        System.out.println("listUploadsByBucket");
        final BucketBean bb = bucketRepository.createBucket("TEST_BUCKET");
        MultipartUpload[] uploads = new MultipartUpload[6];
        for (int i = 0; i < uploads.length; i++) {
            uploads[i] = uploadRepository.initiateUpload(new AmassedResource("TEST_AMASSEDRESOURCE") {
                @Override
                public Iterator<ChunkedResource> getSubResources() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public Bucket getBucket() throws NoSuchBucket {
                    return bb;
                }
            });
        }
        Iterator<MultipartUpload> list = uploadRepository.listUploadsByBucket(bb);
        for (MultipartUpload upload : uploads) {
            assertEquals(upload.getUploadId(), list.next().getUploadId());
        }
    }

    /**
     * Test of appendChunkedResource method, of class
     * MultipartUploadRepositoryImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAppendChunkedResource() throws Exception {
        System.out.println("appendChunkedResource");
        final MultipartUpload upload;
        final BucketBean bb = bucketRepository.createBucket("TEST_BUCKET");
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
        upload = uploadRepository.initiateUpload(r);
        uploadRepository.saveSubresource(upload, create.apply(null),
                "123");
        uploadRepository.saveSubresource(upload, create.apply(null),
                "512345678901234567890123456789012");
        uploadRepository.saveSubresource(upload, create.apply(null),
                "612345678901234567890123456789012");
        List<Subresource> records = uploadingSubresourceDao.selectByExample(
                new SubresourceExample());
        assertEquals(3, records.size());
        assertEquals("123", records.get(0).getIndexInParent());
        assertEquals(r.getVersionId(), records.get(0).getParentVersionId());
        assertEquals("ChunkedResource", records.get(0).getType());
        assertEquals("123456789012",
                records.get(1).getIndexInParent());
        assertEquals("123456789012",
                records.get(2).getIndexInParent());
    }

}
