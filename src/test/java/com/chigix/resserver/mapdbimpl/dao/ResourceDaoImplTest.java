package com.chigix.resserver.mapdbimpl.dao;

import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.Chunk;
import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.entity.dao.BucketDao;
import com.chigix.resserver.entity.error.NoSuchKey;
import com.chigix.resserver.mapdbimpl.BucketInStorage;
import com.chigix.resserver.mapdbimpl.ChunkNode;
import com.chigix.resserver.mapdbimpl.ResourceInStorage;
import com.chigix.resserver.mapdbimpl.Serializer;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mapdb.DB;
import org.mapdb.DBMaker;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ResourceDaoImplTest {

    private DB db;

    private ResourceDaoImpl dao;

    public ResourceDaoImplTest() {
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
        ResourceKeys.updateDBScheme(db);
        BucketKeys.updateDBScheme(db);
        ChunkKeys.updateDBScheme(db);
        dao = new ResourceDaoImpl(db);
        BucketDaoImpl bucket_dao = new BucketDaoImpl(db);
        ChunkDaoImpl chunk_dao = new ChunkDaoImpl(db);
        setUpBuckets(db);
        dao.assembleDaos(bucket_dao, chunk_dao);
    }

    private void setUpBuckets(DB db) {
        BucketInStorage testing = new BucketInStorage("TESTING", DateTime.parse("2017-01-20T02:19:36.037Z"));
        testing.setUUID("f9b5cbea-d168-4af0-880b-8e976662bfeb");
        ((Map<String, String>) db.hashMap(BucketKeys.BUCKET_DB).open()).put("TESTING", Serializer.serializeBucket(testing));
        db.commit();
    }

    @After
    public void tearDown() {
        db.close();
        db = null;
    }

    /**
     * Test of saveResource method, of class ResourceDaoImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSaveResource() throws Exception {
        System.out.println("saveResource");
        BucketDao bucket_dao = new BucketDaoImpl(db);
        BucketInStorage b = (BucketInStorage) bucket_dao.findBucketByName("TESTING");
        Resource r1 = new Resource(b, "key1.file");
        Resource r2 = new Resource(b, "key2.file");
        Resource r3 = new Resource(b, "key3.file");
        dao.saveResource(r1);
        dao.saveResource(r2);
        dao.saveResource(r1);
        dao.saveResource(r3);
        dao.saveResource(r2);
        assertEquals(3, ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_DB).open()).keySet().size());
        assertEquals(ResourceInStorage.hashKey(b.getUUID(), r1.getKey()), ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_START_DB).open()).get(b.getUUID()));
        assertEquals(ResourceInStorage.hashKey(b.getUUID(), r3.getKey()), ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_END_DB).open()).get(b.getUUID()));
        assertNull(Serializer.deserializeResourceLinkNode(
                ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open())
                        .get(ResourceInStorage.hashKey(b.getUUID(), r1.getKey()))
        ).getPreviousResourceKeyHash());
        assertEquals(ResourceInStorage.hashKey(b.getUUID(), r2.getKey()),
                Serializer.deserializeResourceLinkNode(((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open())
                        .get(ResourceInStorage.hashKey(b.getUUID(), r1.getKey()))).getNextResourceKeyHash());
        assertEquals(ResourceInStorage.hashKey(b.getUUID(), r1.getKey()),
                Serializer.deserializeResourceLinkNode(((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open())
                        .get(ResourceInStorage.hashKey(b.getUUID(), r2.getKey()))).getPreviousResourceKeyHash());
        assertEquals(ResourceInStorage.hashKey(b.getUUID(), r3.getKey()),
                Serializer.deserializeResourceLinkNode(((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open())
                        .get(ResourceInStorage.hashKey(b.getUUID(), r2.getKey()))).getNextResourceKeyHash());
        assertEquals(ResourceInStorage.hashKey(b.getUUID(), r2.getKey()),
                Serializer.deserializeResourceLinkNode(((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open())
                        .get(ResourceInStorage.hashKey(b.getUUID(), r3.getKey()))).getPreviousResourceKeyHash());
        assertNull(Serializer.deserializeResourceLinkNode(
                ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open())
                        .get(ResourceInStorage.hashKey(b.getUUID(), r3.getKey()))
        ).getNextResourceKeyHash());
    }

    /**
     * Test of saveChunkNode method, of class ResourceDaoImpl.
     */
    @Test
    public void testSaveChunkNode() {
        System.out.println("saveChunkNode");
        ResourceDaoImpl dao = new ResourceDaoImpl(db);
        final AtomicInteger ato = new AtomicInteger();
        for (int i = 0; i < 100; i++) {
            ato.set(i);
            new Thread() {
                @Override
                public void run() {
                    System.out.println("#" + ato.get());
                    ((ConcurrentMap<String, ChunkNode>) db.hashMap(ResourceKeys.CHUNK_LIST_DB).open()).put("JIJIJI", new ChunkNode("JJJJ", "KKKKKK"));
                }

            }.start();
        }
//        dao.saveChunkNode(new ChunkNode("JaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaIJI", "JAAAfffffffffffffffffffffffffffffffffffA"));
    }

    /**
     * Test of findResource method, of class ResourceDaoImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testFindResource_Bucket_String() throws Exception {
        System.out.println("findResource");
        BucketDao bucket_dao = new BucketDaoImpl(db);
        System.out.println("findResource#1");
        try {
            dao.findResource("TESTING", "BANKAI.log");
            fail("The key [BANKAI.log] should not exists.");
        } catch (NoSuchKey noSuchKey) {
        }
        System.out.println("findResource#2");
        Resource r = new Resource(bucket_dao.findBucketByName("TESTING"), "testing-2.log");
        r.setETag(UUID.randomUUID().toString());
        dao.saveResource(r);
        assertEquals(r.getETag(), dao.findResource(bucket_dao.findBucketByName("TESTING"), "testing-2.log").getETag());
    }

    /**
     * Test of assembleDaos method, of class ResourceDaoImpl.
     */
    @Test
    public void testAssembleDaos() {
        System.out.println("assembleDaos");
    }

    /**
     * Test of findResourceByKeyHash method, of class ResourceDaoImpl.
     */
    @Test
    public void testFindResourceByKeyHash() {
        System.out.println("findResourceByKeyHash");
        String resourceKeyHash = "";
        ResourceDaoImpl instance = null;
        ResourceInStorage expResult = null;
        ResourceInStorage result = instance.findResourceByKeyHash(resourceKeyHash);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of appendChunk method, of class ResourceDaoImpl.
     */
    @Test
    public void testAppendChunk() {
        System.out.println("appendChunk");
        Resource resource = null;
        Chunk chunk = null;
        ResourceDaoImpl instance = null;
        instance.appendChunk(resource, chunk);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findChunkNode method, of class ResourceDaoImpl.
     */
    @Test
    public void testFindChunkNode() {
        System.out.println("findChunkNode");
        String resource_chunk_hash = "";
        ResourceDaoImpl instance = null;
        ChunkNode expResult = null;
        ChunkNode result = instance.findChunkNode(resource_chunk_hash);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setBucketFirstResource method, of class ResourceDaoImpl.
     */
    @Test
    public void testSetBucketFirstResource() {
        System.out.println("setBucketFirstResource");
        ResourceInStorage resource = null;
        ResourceDaoImpl instance = null;
        instance.setBucketFirstResource(resource);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBucketFirstResource method, of class ResourceDaoImpl.
     */
    @Test
    public void testGetBucketFirstResource() {
        System.out.println("getBucketFirstResource");
        String bucket_uuid = "";
        ResourceDaoImpl instance = null;
        ResourceInStorage expResult = null;
        ResourceInStorage result = instance.getBucketFirstResource(bucket_uuid);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listResources method, of class ResourceDaoImpl.
     */
    @Test
    public void testListResources_Bucket() throws Exception {
        System.out.println("listResources");
        Bucket bucket = null;
        ResourceDaoImpl instance = null;
        Iterator<Resource> expResult = null;
        Iterator<Resource> result = instance.listResources(bucket);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listResources method, of class ResourceDaoImpl.
     */
    @Test
    public void testListResources_Bucket_String() {
        System.out.println("listResources");
        Bucket bucket = null;
        String continuation = "";
        ResourceDaoImpl instance = null;
        Iterator<Resource> expResult = null;
        Iterator<Resource> result = instance.listResources(bucket, continuation);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeResource method, of class ResourceDaoImpl.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testRemoveResource() throws Exception {
        System.out.println("removeResource");
        BucketDao bucket_dao = new BucketDaoImpl(db);
        BucketInStorage b = (BucketInStorage) bucket_dao.findBucketByName("TESTING");
        Resource r1 = new Resource(b, "key1.file");
        Resource r2 = new Resource(b, "key2.file");
        Resource r3 = new Resource(b, "key3.file");
        dao.saveResource(r1);
        dao.saveResource(r2);
        dao.saveResource(r1);
        dao.saveResource(r3);
        dao.saveResource(r2);
        dao.removeResource(r2);
        assertEquals(2, ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_DB).open()).keySet().size());
        assertEquals(ResourceInStorage.hashKey(b.getUUID(), r1.getKey()), ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_START_DB).open()).get(b.getUUID()));
        assertEquals(ResourceInStorage.hashKey(b.getUUID(), r3.getKey()), ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_END_DB).open()).get(b.getUUID()));
        assertEquals(ResourceInStorage.hashKey(b.getUUID(), r1.getKey()),
                Serializer.deserializeResourceLinkNode(
                        ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open())
                                .get(ResourceInStorage.hashKey(b.getUUID(), r3.getKey())))
                        .getPreviousResourceKeyHash());
        assertEquals(ResourceInStorage.hashKey(b.getUUID(), r3.getKey()),
                Serializer.deserializeResourceLinkNode(
                        ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open())
                                .get(ResourceInStorage.hashKey(b.getUUID(), r1.getKey())))
                        .getNextResourceKeyHash());
        dao.removeResource(r1);
        assertEquals(1, ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_DB).open()).keySet().size());
        assertEquals(ResourceInStorage.hashKey(b.getUUID(), r3.getKey()), ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_START_DB).open()).get(b.getUUID()));
        assertEquals(ResourceInStorage.hashKey(b.getUUID(), r3.getKey()), ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_END_DB).open()).get(b.getUUID()));
        assertNull(Serializer.deserializeResourceLinkNode(
                ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open())
                        .get(ResourceInStorage.hashKey(b.getUUID(), r3.getKey())))
                .getNextResourceKeyHash());
        assertNull(Serializer.deserializeResourceLinkNode(
                ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open())
                        .get(ResourceInStorage.hashKey(b.getUUID(), r3.getKey())))
                .getPreviousResourceKeyHash());
    }

    /**
     * Test of findResource method, of class ResourceDaoImpl.
     */
    @Test
    public void testFindResource_String_String() throws Exception {
        System.out.println("findResource");
        String bucketName = "";
        String resourceKey = "";
        ResourceDaoImpl instance = null;
        Resource expResult = null;
        Resource result = instance.findResource(bucketName, resourceKey);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
