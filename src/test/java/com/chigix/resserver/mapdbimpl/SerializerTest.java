package com.chigix.resserver.mapdbimpl;

import com.chigix.resserver.entity.ModelProxy;
import com.chigix.resserver.entity.Resource;
import java.util.UUID;
import org.joda.time.DateTime;
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
public class SerializerTest {

    public SerializerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of serializeResource method, of class Serializer.
     */
    @Test
    public void testSerializeResource() {
        System.out.println("serializeResource");
        BucketInStorage b = new BucketInStorage("TARGET_BUCKET");
        b.setUUID("JJJJJJ");
        Resource r = new Resource(b, "BANKAI");
        r.setETag(UUID.randomUUID().toString());
        r.setLastModified(DateTime.parse("2017-01-12T09:06:00.863Z"));
        r.removeMetaData("Content-Type");
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Resource><Bucket>TARGET_BUCKET</Bucket><Key>BANKAI</Key><Etag>" + r.getETag() + "</Etag><LastModified>2017-01-12T09:06:00.863Z</LastModified><KeyHash>fad83f6fd4131cbd6a3341bcba0c41ff</KeyHash><Size>0</Size><StorageClass>STANDARD</StorageClass></Resource>",
                Serializer.serializeResource(r));
    }

    /**
     * Test of deserializeResource method, of class Serializer.
     */
    @Test
    public void testDeserializeResource() {
        System.out.println("deserializeResource");
        BucketInStorage b = new BucketInStorage("TARGET_BUCKET");
        b.setUUID(UUID.randomUUID().toString());
        Resource r = new Resource(b, "BANKAI");
        r.setETag(UUID.randomUUID().toString());
        r.setLastModified(DateTime.parse("2017-01-12T09:06:00.863Z"));
        r.removeMetaData("Content-Type");
        String random_key = UUID.randomUUID().toString();
        r.setMetaData("x-key", random_key);
        ResourceInStorage result = Serializer.deserializeResource(Serializer.serializeResource(r));
        assertEquals(ResourceInStorage.hashKey(b.getUUID(), result.getKey()), result.getKeyHash());
        assertEquals(random_key, result.snapshotMetaData().get("x-key"));
        assertNull(result.getBucketProxy().getProxied());
        try {
            result.getBucket();
            fail("ProxiedException should be thrown.");
        } catch (ModelProxy.ProxiedException e) {
        }
    }

    /**
     * Test of serializeChunkNode method, of class Serializer.
     */
    @Test
    public void testSerializeChunkNode() {
        System.out.println("serializeChunkNode");
        ChunkNode chunkNode = new ChunkNode("RESOURCE_KEY_HASH", "CONTENT_HASH");
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ChunkNode><ContentHash>CONTENT_HASH</ContentHash><ParentResourceKeyHash>RESOURCE_KEY_HASH</ParentResourceKeyHash></ChunkNode>",
                Serializer.serializeChunkNode(chunkNode));
    }

    /**
     * Test of deserializeChunkNode method, of class Serializer.
     */
    @Test
    public void testDeserializeChunkNode() {
        System.out.println("deserializeChunkNode");
        ChunkNode chunkNode = new ChunkNode("RESOURCE_KEY_HASH", "CONTENT_HASH");
        ChunkNode result = Serializer.deserializeChunkNode(Serializer.serializeChunkNode(chunkNode));
        assertEquals(chunkNode.getContentHash(), result.getContentHash());
        assertEquals(chunkNode.getParentResourceKeyHash(), result.getParentResourceKeyHash());
    }

    /**
     * Test of serializeResourceLinkNode method, of class Serializer.
     */
    @Test
    public void testSerializeResourceLinkNode() {
        System.out.println("serializeResourceLinkNode");
        ResourceLinkNode node = new ResourceLinkNode();
        System.out.println(Serializer.serializeResourceLinkNode(node));
    }

    /**
     * Test of deserializeResourceLinkNode method, of class Serializer.
     */
    @Test
    public void testDeserializeResourceLinkNode() {
        System.out.println("deserializeResourceLinkNode");
        String xml = Serializer.serializeResourceLinkNode(new ResourceLinkNode());
        assertNull(Serializer.deserializeResourceLinkNode(xml).getNextResourceKeyHash());
        assertNull(Serializer.deserializeResourceLinkNode(xml).getPreviousResourceKeyHash());
        ResourceLinkNode node = new ResourceLinkNode();
        node.setNextResourceKeyHash(UUID.randomUUID().toString());
        node.setPreviousResourceKeyHash(UUID.randomUUID().toString());
        xml = Serializer.serializeResourceLinkNode(node);
        assertEquals(node.getNextResourceKeyHash(), Serializer.deserializeResourceLinkNode(xml).getNextResourceKeyHash());
        assertEquals(node.getPreviousResourceKeyHash(), Serializer.deserializeResourceLinkNode(xml).getPreviousResourceKeyHash());
    }

    /**
     * Test of serializeBucket method, of class Serializer.
     */
    @Test
    public void testSerializeBucket() {
        System.out.println("serializeBucket");
        BucketInStorage b = new BucketInStorage("TESTING_BUCKET", DateTime.parse("2017-01-20T02:19:36.037Z"));
        b.setUUID(UUID.randomUUID().toString());
        String result = Serializer.serializeBucket(b);
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Bucket><Name>TESTING_BUCKET</Name>"
                + "<CreationTime>2017-01-20T02:19:36.037Z</CreationTime><ChizuruUUID>"
                + b.getUUID()
                + "</ChizuruUUID></Bucket>", Serializer.serializeBucket(b));
    }

    /**
     * Test of deserializeBucket method, of class Serializer.
     */
    @Test
    public void testDeserializeBucket() {
        System.out.println("deserializeBucket");
        BucketInStorage b = new BucketInStorage("TARGET");
        b.setUUID(UUID.randomUUID().toString());
        assertEquals(b.getCreationTime().toString(), Serializer.deserializeBucket(Serializer.serializeBucket(b)).getCreationTime().toString());
    }

}
