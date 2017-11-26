package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.model.resource.AmassedResource;
import com.chigix.resserver.domain.model.bucket.Bucket;
import com.chigix.resserver.domain.model.chunk.Chunk;
import com.chigix.resserver.domain.model.resource.ChunkedResource;
import com.chigix.resserver.domain.model.resource.Resource;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.domain.error.NoSuchKey;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import com.chigix.resserver.mybatis.bean.ResourceExtension;
import com.chigix.resserver.mybatis.dao.BucketMapper;
import com.chigix.resserver.mybatis.dao.ChunkMapper;
import com.chigix.resserver.mybatis.dao.ResourceMapper;
import com.chigix.resserver.mybatis.dao.SubresourceMapper;
import com.chigix.resserver.mybatis.mapstruct.BucketBeanMapper;
import com.chigix.resserver.mybatis.mapstruct.ResourceBeanMapper;
import com.chigix.resserver.mybatis.record.BucketExample;
import com.chigix.resserver.mybatis.record.ChunkExample;
import com.chigix.resserver.mybatis.record.ResourceExample;
import com.chigix.resserver.mybatis.record.ResourceExampleExtending;
import com.chigix.resserver.mybatis.record.Subresource;
import com.chigix.resserver.mybatis.record.Util;
import com.chigix.resserver.mybatis.specification.ResourceSpecification;
import com.chigix.resserver.application.util.Authorization;
import com.chigix.resserver.domain.model.resource.SubresourceSpecification;
import com.chigix.resserver.mybatis.bean.AmassedResourceBean;
import com.chigix.resserver.mybatis.record.SubresourceExample;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
import org.springframework.beans.factory.annotation.Autowired;
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
public class ResourceRepositoryImplTest {

    @Autowired
    private ResourceRepositoryExtend resourceRepository;

    @Autowired
    private BucketMapper bucketMapper;
    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private ChunkMapper chunkMapper;
    @Autowired
    private SubresourceMapper subResourceMapper;

    @Autowired
    private BucketBeanMapper bucketBeanMapper;
    @Autowired
    private ResourceBeanMapper resourceBeanMapper;

    public ResourceRepositoryImplTest() {
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
     * Test of findResource method, of class ResourceDaoImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testFindResource_String_String() throws Exception {
        System.out.println("findResource");
        BucketBean bucket = new BucketBean("test_bucket");
        bucketMapper.insert(bucketBeanMapper.toRecord(bucket));
        ChunkedResourceBean r = new ChunkedResourceBean("RES_1", ResourceExtension.hashKey(bucket.getUuid(), "RES_1"));
        r.setMetaData("Content-Type", "BANKAI");
        r.setBucket(bucket);
        resourceMapper.insert(resourceBeanMapper.toRecord(r));
        Resource result = resourceRepository.findResource(bucket.getName(), r.getKey());
        assertEquals(bucket.getUuid(), ((BucketBean) result.getBucket()).getUuid());
        assertEquals("BANKAI", result.snapshotMetaData().get("Content-Type"));
        BucketExample by_uuid_example = new BucketExample();
        by_uuid_example.createCriteria().andUuidEqualTo(bucket.getUuid());
        bucketMapper.deleteByExample(by_uuid_example);
        assertEquals(bucket.getUuid(), ((BucketBean) result.getBucket()).getUuid());
        try {
            resourceRepository.findResource(bucket.getName(), r.getKey());
            fail("NoSuchKey Exception should be thrown here.");
        } catch (NoSuchBucket | NoSuchKey e) {
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
        bucketMapper.insert(bucketBeanMapper.toRecord(bucket));
        ChunkedResourceBean r = new ChunkedResourceBean("RES_1", ResourceExtension.hashKey(bucket.getUuid(), "RES_1"));
        r.setMetaData("Content-Type", "BANKAI");
        r.setBucket(bucket);
        resourceMapper.insert(resourceBeanMapper.toRecord(r));
        Resource result = resourceRepository.findResource(bucket, r.getKey());
        assertEquals(bucket.getUuid(), ((BucketBean) result.getBucket()).getUuid());
        assertEquals("BANKAI", result.snapshotMetaData().get("Content-Type"));
        BucketExample by_uuid_example = new BucketExample();
        by_uuid_example.createCriteria().andUuidEqualTo(bucket.getUuid());
        bucketMapper.deleteByExample(by_uuid_example);
        assertEquals(bucket.getUuid(), ((BucketBean) result.getBucket()).getUuid());
        result = resourceRepository.findResource(bucket, r.getKey());
        try {
            result.getBucket();
            fail("NoSuchBucket Exception should be thrown here.");
        } catch (NoSuchBucket noSuchBucket) {
        }
    }

    /**
     * Test of saveResource method, of class ResourceDaoImpl. Save newly created
     * ChunkedResource.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSaveResource_1() throws Exception {
        System.out.println("saveResource_1");
        final BucketBean bb = new BucketBean("TEST_BUCKET");
        bucketMapper.insert(bucketBeanMapper.toRecord(bb));
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
        resourceRepository.saveResource(r);
        ResourceExample example = new ResourceExample();
        example.createCriteria().andBucketUuidEqualTo(bb.getUuid()).andKeyEqualTo(r.getKey());
        com.chigix.resserver.mybatis.record.Resource record
                = resourceMapper.selectByExampleWithRowbounds(example, Util.ONE_ROWBOUND).get(0);
        assertEquals(ResourceExtension.hashKey(bb.getUuid(), r.getKey()), record.getKeyhash());
        assertEquals("ChunkedResource", record.getType());
    }

    /**
     * Update Persisted ChunkedResource Object.
     *
     * @throws Exception
     */
    @Test
    public void testSaveResource_2() throws Exception {
        System.out.println("saveResource_2");
        final BucketBean bb = new BucketBean("TEST_BUCKET");
        bucketMapper.insert(bucketBeanMapper.toRecord(bb));
        ChunkedResourceBean r = new ChunkedResourceBean("TEST_RESOURCE", ResourceExtension.hashKey(bb.getUuid(), "TEST_RESOURCE"));
        r.setBucket(bb);
        resourceRepository.saveResource(r);
        ResourceExample example = new ResourceExample();
        example.createCriteria().andBucketUuidEqualTo(bb.getUuid()).andKeyEqualTo(r.getKey());
        com.chigix.resserver.mybatis.record.Resource record
                = resourceMapper.selectByExampleWithRowbounds(example, Util.ONE_ROWBOUND).get(0);
        assertEquals(r.getKeyHash(), record.getKeyhash());
        assertEquals(bb.getUuid(), record.getBucketUuid());
        assertEquals("ChunkedResource", record.getType());
    }

    /**
     * Save an {@link AmassedResource} with sub {@link ChunkedResource}.
     *
     * @throws Exception
     */
    @Test
    public void testSaveResource_3() throws Exception {
        System.out.println("saveResource_3");
        final BucketBean bb = new BucketBean("TEST_BUCKET");
        final AmassedResource parent = new AmassedResource("TEST_RESOURCE") {
            @Override
            public <T extends ChunkedResource> Iterator<T> getSubResources() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Bucket getBucket() throws NoSuchBucket {
                return bb;
            }
        };
        resourceRepository.saveResource(parent);
    }

    /**
     * Test of insertSubresource method, of class ResourceRepositoryImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInsertSubresource() throws Exception {
        System.out.println("insertSubresource");
        BigInteger parent_size = BigInteger.ZERO;
        final AmassedResourceBean parent = new AmassedResourceBean(
                "TEST_PARENT", "PARNET_KEYHASH");
        final BucketBean bb = new BucketBean("TEST_BUCKET");
        for (int i = 1; i <= 1000; i++) {
            BigInteger prev_size = parent_size;
            BigInteger updated_size = parent_size
                    .add(new BigInteger(i + "")
                            .multiply(new BigInteger(8 * 1024 * 1024 + "")));
            parent_size = updated_size;
            ChunkedResourceBean subresource = new ChunkedResourceBean(i * 2 + 1 + "", "TEST_HASH");
            subresource.setBucket(bb);
            resourceRepository.insertSubresource(subresource,
                    SubresourceSpecification.build(parent,
                            prev_size, updated_size.subtract(BigInteger.ONE),
                            i + ""));
        }
        List<Subresource> records = subResourceMapper.selectByExample(new SubresourceExample());
        assertEquals(records.size(), 1000);
        int counter = 1;
        BigInteger next_byte_pos = BigInteger.ZERO;
        for (Subresource record : records) {
            assertEquals(counter++ + "", record.getIndexInParent());
            assertEquals(next_byte_pos.toString(),
                    Integer.toUnsignedString(record.getRangeStartByte()));
            next_byte_pos = new BigInteger(
                    Integer.toUnsignedString(record.getRangeEndByte()))
                    .add(BigInteger.ONE);
        }
    }

    /**
     * Test of listResources method, of class ResourceDaoImpl.
     *
     * @TODO test with continuation token. --
     * {@link ResourceExampleExtending.VersionIdOffset} has been error occured
     * once time for missing ID field in SQL.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testListResources() throws Exception {
        System.out.println("listResources");
        BucketBean bb = new BucketBean("TEST_BUCKET");
        bucketMapper.insert(bucketBeanMapper.toRecord(bb));
        ChunkedResourceBean[] resources = new ChunkedResourceBean[10003];
        for (int i = 0; i < resources.length; i++) {
            resources[i] = new ChunkedResourceBean("REC_" + i, ResourceExtension.hashKey(bb.getUuid(), "REC_" + i));
            resources[i].setBucket(bb);
            resourceMapper.insert(resourceBeanMapper.toRecord(resources[i]));
        }
        Iterator<Resource> it = resourceRepository.listResources(bb, 10004);
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
        bucketMapper.insert(bucketBeanMapper.toRecord(bb));
        ChunkedResourceBean r = new ChunkedResourceBean("TEST_RESOURCE", ResourceExtension.hashKey(bb.getUuid(), "TEST_RESOURCE"));
        r.setBucket(bb);
        resourceMapper.insert(resourceBeanMapper.toRecord(r));
        ResourceExample example = new ResourceExample();
        example.createCriteria().andBucketUuidEqualTo(bb.getUuid());
        assertEquals(1, resourceMapper.selectByExample(example).size());
        resourceRepository.removeResource(r);
        assertEquals(0, resourceMapper.selectByExample(example).size());
    }

    /**
     * Test of putChunk method, of class ResourceDaoImpl.
     */
    @Test
    public void testPutChunk() {
        System.out.println("putChunk");
        ChunkedResource r = new ChunkedResourceBean("RES_TEST", "RES_TEST_KEYHASH");
        int size_count = 0;
        for (int i = 0; i < 100; i++) {
            resourceRepository.putChunk(r,
                    new Chunk(Authorization.HexEncode(Authorization.SHA256(("RES_" + i).getBytes())), i, "LOCATION_ID"),
                    i);
            size_count += i;
        }
        ChunkExample example = new ChunkExample();
        example.createCriteria().andParentVersionIdEqualTo(r.getVersionId());
        List<com.chigix.resserver.mybatis.record.Chunk> chunk_records = chunkMapper.selectByExample(example);
        for (int i = 0; i < 100; i++) {
            assertEquals(i + "", chunk_records.get(i).getSize() + "");
        }
        assertEquals(size_count + "", r.getSize());
    }

    /**
     * Test of listSubResources method, of class ResourceDaoImpl.
     */
    @Test
    public void testListSubResources() {
        System.out.println("listSubResources");
        final AmassedResource resource = new AmassedResource("TEST_KEY") {
            @Override
            public <T extends ChunkedResource> Iterator<T> getSubResources() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Bucket getBucket() throws NoSuchBucket {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        Subresource[] subresources = new Subresource[1000];
        BigInteger parent_size_count = BigInteger.ZERO;
        for (int i = 1; i < subresources.length + 1; i++) {
            int size = 8 * 1024 * 1024;
            Subresource record = new Subresource();
            record.setEtag(UUID.randomUUID().toString().replaceAll("-", ""));
            record.setIndexInParent(i + "");
            record.setLastModified(DateTime.now(DateTimeZone.forID("GMT")));
            record.setParentVersionId(resource.getVersionId());
            int[] byte4_values = Arrays.copyOf(
                    Util.toBase256(parent_size_count), 2);
            record.setRangeStartByte(byte4_values[0]);
            record.setRangeStart4byte(byte4_values[1]);
            parent_size_count = parent_size_count.add(new BigInteger(size + ""));
            byte4_values = Arrays.copyOf(
                    Util.toBase256(parent_size_count.subtract(BigInteger.ONE)),
                    2);
            record.setRangeEndByte(byte4_values[0]);
            record.setRangeEnd4byte(byte4_values[1]);
            record.setSize(size + "");
            record.setStorageClass("STANDARD");
            record.setType(ChunkedResourceBean.TYPE);
            record.setVersionId(UUID.randomUUID().toString());
            subresources[i - 1] = record;
            subResourceMapper.insert(record);
        }
        ResourceSpecification.byParentResource spec = new ResourceSpecification.byParentResource(resource, BigInteger.ZERO);
        Iterator<ChunkedResource> resources = resourceRepository.listSubResources(spec);
        ChunkedResource lastResource = null;
        for (Subresource subresource : subresources) {
            lastResource = resources.next();
            assertEquals(subresource.getVersionId(), lastResource.getVersionId());
        }
        SubresourceExample example = new SubresourceExample();
        example.createCriteria().andParentVersionIdEqualTo(resource.getVersionId())
                .andIndexInParentEqualTo(lastResource.getKey());
        Subresource r = subResourceMapper.selectByExample(example).get(0);
        int[] byte4_values = Util.toBase256(parent_size_count
                .subtract(BigInteger.ONE));
        assertEquals(Integer.toUnsignedString(byte4_values[0]),
                Integer.toUnsignedString(r.getRangeEndByte()));
        assertEquals(Integer.toUnsignedString(byte4_values[1]),
                Integer.toUnsignedString(r.getRangeEnd4byte()));
    }

}
