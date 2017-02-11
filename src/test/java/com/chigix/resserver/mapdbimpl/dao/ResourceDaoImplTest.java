package com.chigix.resserver.mapdbimpl.dao;

import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.Chunk;
import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.entity.dao.BucketDao;
import com.chigix.resserver.entity.error.NoSuchKey;
import com.chigix.resserver.mapdbimpl.BucketInStorage;
import com.chigix.resserver.mapdbimpl.ResourceInStorage;
import com.chigix.resserver.mapdbimpl.Serializer;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
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
    public void testSaveResource_1() throws Exception {
        System.out.println("saveResource_1");
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

    @Test
    public void testSaveResource_2() throws Exception {
        System.out.println("saveResource_2");
        BucketDao bucket_dao = new BucketDaoImpl(db);
        BucketInStorage b = (BucketInStorage) bucket_dao.findBucketByName("TESTING");
        Resource r1 = new Resource(b, "key1.file");
        Resource r2 = new Resource(b, "key2.file");
        Resource r3 = new Resource(b, "key3.file");
        dao.saveResource(r1);
        dao.saveResource(r2);
        dao.saveResource(r3);
        dao.removeResource(r2);
        dao.saveResource(r2);
        assertEquals(3, ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_DB).open()).keySet().size());
        assertEquals(ResourceInStorage.hashKey(b.getUUID(), r1.getKey()), ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_START_DB).open()).get(b.getUUID()));
        assertEquals(ResourceInStorage.hashKey(b.getUUID(), r2.getKey()), ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_END_DB).open()).get(b.getUUID()));
        assertNull(Serializer.deserializeResourceLinkNode(
                ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open())
                        .get(ResourceInStorage.hashKey(b.getUUID(), r1.getKey()))
        ).getPreviousResourceKeyHash());
        assertEquals(ResourceInStorage.hashKey(b.getUUID(), r3.getKey()),
                Serializer.deserializeResourceLinkNode(((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open())
                        .get(ResourceInStorage.hashKey(b.getUUID(), r1.getKey()))).getNextResourceKeyHash());
        assertEquals(ResourceInStorage.hashKey(b.getUUID(), r1.getKey()),
                Serializer.deserializeResourceLinkNode(((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open())
                        .get(ResourceInStorage.hashKey(b.getUUID(), r3.getKey()))).getPreviousResourceKeyHash());
        assertEquals(ResourceInStorage.hashKey(b.getUUID(), r2.getKey()),
                Serializer.deserializeResourceLinkNode(((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open())
                        .get(ResourceInStorage.hashKey(b.getUUID(), r3.getKey()))).getNextResourceKeyHash());
        assertEquals(ResourceInStorage.hashKey(b.getUUID(), r3.getKey()),
                Serializer.deserializeResourceLinkNode(((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open())
                        .get(ResourceInStorage.hashKey(b.getUUID(), r2.getKey()))).getPreviousResourceKeyHash());
        assertNull(Serializer.deserializeResourceLinkNode(
                ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open())
                        .get(ResourceInStorage.hashKey(b.getUUID(), r2.getKey()))
        ).getNextResourceKeyHash());
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
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testAppendChunk() throws Exception {
        System.out.println("appendChunk");
        BucketDao bucket_dao = new BucketDaoImpl(db);
        BucketInStorage b = (BucketInStorage) bucket_dao.findBucketByName("TESTING");
        dao.saveResource(new Resource(b, "testing_file"));
        ResourceInStorage r = (ResourceInStorage) dao.findResource(b, "testing_file");
        dao.appendChunk(r, new Chunk("c4ca4238a0b923820dcc509a6f75849b", 1));// md5("1")
        dao.appendChunk(r, new Chunk("c81e728d9d4c2f636f067f89cc14862c", 1));// md5("2")
        dao.appendChunk(r, new Chunk("eccbc87e4b5ce2fe28308fd9f2a7baf3", 1));// md5("3")
        dao.appendChunk(r, new Chunk("a87ff679a2f3e71d9181a67b7542122c", 1));// md5("4")
        dao.appendChunk(r, new Chunk("e4da3b7fbbce2345d7772b0674a318d5", 1));// md5("5")
        db.commit();
        Map<String, String> chunks = (Map<String, String>) db.hashMap(ResourceKeys.CHUNK_LIST_DB).open();
        assertEquals(6, chunks.size());
        assertEquals("c4ca4238a0b923820dcc509a6f75849b", chunks.get(r.getKeyHash() + "_0"));
        assertEquals("c81e728d9d4c2f636f067f89cc14862c", chunks.get(r.getKeyHash() + "_1"));
        assertEquals("eccbc87e4b5ce2fe28308fd9f2a7baf3", chunks.get(r.getKeyHash() + "_2"));
        assertEquals("a87ff679a2f3e71d9181a67b7542122c", chunks.get(r.getKeyHash() + "_3"));
        assertEquals("e4da3b7fbbce2345d7772b0674a318d5", chunks.get(r.getKeyHash() + "_4"));
        assertEquals("4", chunks.get(r.getKeyHash() + "__count"));
        assertNull(chunks.get(r.getKeyHash() + "_5"));
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
    public void testRemoveResource_1() throws Exception {
        System.out.println("removeResource_1");
        BucketDao bucket_dao = new BucketDaoImpl(db);
        BucketInStorage b = (BucketInStorage) bucket_dao.findBucketByName("TESTING");
        Resource r1 = new Resource(b, "key1.file");
        Resource r2 = new Resource(b, "key2.file");
        Resource r3 = new Resource(b, "key3.file");
        dao.saveResource(r1);
        dao.saveResource(r2);
        dao.saveResource(r3);
        dao.removeResource(r2);
        assertEquals(2, db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open().keySet().size());
        assertEquals(2, db.hashMap(ResourceKeys.RESOURCE_DB).open().keySet().size());
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
    }

    @Test
    public void testRemoveResource_2() throws Exception {
        System.out.println("removeResource_3");
        BucketDao bucket_dao = new BucketDaoImpl(db);
        BucketInStorage b = (BucketInStorage) bucket_dao.findBucketByName("TESTING");
        Resource r1 = new Resource(b, "key1.file");
        Resource r2 = new Resource(b, "key2.file");
        Resource r3 = new Resource(b, "key3.file");
        dao.saveResource(r1);
        dao.saveResource(r2);
        dao.saveResource(r3);
        dao.removeResource(r2);
        dao.removeResource(r3);
        assertEquals(1, db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open().keySet().size());
        assertEquals(1, db.hashMap(ResourceKeys.RESOURCE_DB).open().keySet().size());
        assertEquals(ResourceInStorage.hashKey(b.getUUID(), r1.getKey()), ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_START_DB).open()).get(b.getUUID()));
        assertEquals(ResourceInStorage.hashKey(b.getUUID(), r1.getKey()), ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_END_DB).open()).get(b.getUUID()));
        assertEquals(null,
                Serializer.deserializeResourceLinkNode(
                        ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open())
                                .get(ResourceInStorage.hashKey(b.getUUID(), r1.getKey())))
                        .getPreviousResourceKeyHash());
        assertEquals(null,
                Serializer.deserializeResourceLinkNode(
                        ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open())
                                .get(ResourceInStorage.hashKey(b.getUUID(), r1.getKey())))
                        .getNextResourceKeyHash());
    }

    @Test
    public void testRemoveResource_3() throws Exception {
        System.out.println("removeResource_3");
        BucketDao bucket_dao = new BucketDaoImpl(db);
        BucketInStorage b = (BucketInStorage) bucket_dao.findBucketByName("TESTING");
        Resource r1 = new Resource(b, "key1.file");
        Resource r2 = new Resource(b, "key2.file");
        Resource r3 = new Resource(b, "key3.file");
        dao.saveResource(r1);
        dao.saveResource(r2);
        dao.saveResource(r3);
        dao.removeResource(r3);
        dao.removeResource(r2);
        dao.removeResource(r1);
        assertEquals(0, db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open().keySet().size());
        assertEquals(0, db.hashMap(ResourceKeys.RESOURCE_DB).open().keySet().size());
        assertEquals(null, ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_START_DB).open()).get(b.getUUID()));
        assertEquals(null, ((Map<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_END_DB).open()).get(b.getUUID()));
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

    /**
     * Test of findChunkNode method, of class ResourceDaoImpl.
     */
    @Test
    public void testFindChunkNode() {
        System.out.println("findChunkNode");
        ResourceInStorage resource = null;
        String number = "";
        ResourceDaoImpl instance = null;
        String expResult = "";
        String result = instance.findChunkNode(resource, number);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of emptyResourceChunkNode method, of class ResourceDaoImpl.
     */
    @Test
    public void testEmptyResourceChunkNode() {
        System.out.println("emptyResourceChunkNode");
        ResourceInStorage resource = null;
        ResourceDaoImpl instance = null;
        instance.emptyResourceChunkNode(resource);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
