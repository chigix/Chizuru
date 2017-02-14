package com.chigix.resserver.mapdbimpl;

import com.chigix.resserver.entity.Chunk;
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
        final BucketInStorage b = new BucketInStorage("TARGET_BUCKET");
        b.setUUID("JJJJJJ");
        Resource r = new Resource(() -> {
            return b;
        }, "BANKAI", "457002e0-f4cd-49e8-b46b-22409589a09c");
        r.setETag(UUID.randomUUID().toString());
        r.setLastModified(DateTime.parse("2017-01-12T09:06:00.863Z"));
        r.removeMetaData("Content-Type");
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<Resource><Bucket>TARGET_BUCKET</Bucket>"
                + "<Key>BANKAI</Key><Etag>" + r.getETag() + "</Etag>"
                + "<LastModified>2017-01-12T09:06:00.863Z</LastModified>"
                + "<KeyHash>fad83f6fd4131cbd6a3341bcba0c41ff</KeyHash>"
                + "<Size>0</Size><VersionId>457002e0-f4cd-49e8-b46b-22409589a09c</VersionId>"
                + "<StorageClass>STANDARD</StorageClass></Resource>",
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
        assertEquals(r.getVersionId(), result.getVersionId());
        assertEquals(random_key, result.snapshotMetaData().get("x-key"));
        assertNull(result.getBucketProxy().getProxied());
        try {
            result.getBucket();
            fail("ProxiedException should be thrown.");
        } catch (ModelProxy.ProxiedException e) {
        }
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

    /**
     * Test of serializeChunk method, of class Serializer.
     */
    @Test
    public void testSerializeChunk() {
        System.out.println("serializeChunk");
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<Chunk><ContentHash>c4ca4238a0b923820dcc509a6f75849b</ContentHash>"
                + "<Size>1</Size></Chunk>",
                Serializer.serializeChunk(new Chunk("c4ca4238a0b923820dcc509a6f75849b", 1, null))
        );// md5("1")
    }

    /**
     * Test of deserializeChunk method, of class Serializer.
     */
    @Test
    public void testDeserializeChunk() {
        System.out.println("deserializeChunk");
        Chunk c = new Chunk("c4ca4238a0b923820dcc509a6f75849b", 1, "Location_Id");// md5("1")
        assertEquals(c.getContentHash(),
                Serializer.deserializeChunk(Serializer.serializeChunk(c)).getContentHash());
        assertEquals(c.getSize(),
                Serializer.deserializeChunk(Serializer.serializeChunk(c)).getSize());
        assertEquals(c.getLocationId(),
                Serializer.deserializeChunk(Serializer.serializeChunk(c)).getLocationId());
    }

}
