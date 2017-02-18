package com.chigix.resserver.mapdbimpl;

import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.Chunk;
import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.mapdbimpl.entity.ChunkedResource;
import com.chigix.resserver.mapdbimpl.entity.ResourceExtension;
import java.util.Iterator;
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
        ChunkedResource r = new ChunkedResource("BANKAI", "457002e0-f4cd", "22409589a09c", b.getUUID(), b.getName());
        r.setBucket(b);
        r.setETag(UUID.randomUUID().toString());
        r.setLastModified(DateTime.parse("2017-01-12T09:06:00.863Z"));
        r.removeMetaData("Content-Type");
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<Resource><Type>ChunkedResource</Type><BucketName>TARGET_BUCKET</BucketName>"
                + "<BucketUUID>JJJJJJ</BucketUUID>"
                + "<Key>BANKAI</Key><Etag>" + r.getETag() + "</Etag>"
                + "<LastModified>2017-01-12T09:06:00.863Z</LastModified>"
                + "<KeyHash>457002e0-f4cd</KeyHash>"
                + "<Size>0</Size><VersionId>22409589a09c</VersionId>"
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
        com.chigix.resserver.entity.ChunkedResource r = new com.chigix.resserver.entity.ChunkedResource("BANKAI", "22409589a09c") {
            @Override
            public Iterator<Chunk> getChunks() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void appendChunk(Chunk chunk) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Bucket getBucket() {
                return b;
            }

            @Override
            public void empty() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        r.setETag(UUID.randomUUID().toString());
        r.setLastModified(DateTime.parse("2017-01-12T09:06:00.863Z"));
        r.removeMetaData("Content-Type");
        String random_key = UUID.randomUUID().toString();
        r.setMetaData("x-key", random_key);
        Resource result = Serializer.deserializeResource(Serializer.serializeResource(r));
        assertTrue(result instanceof com.chigix.resserver.entity.ChunkedResource);
        assertEquals(ResourceExtension.hashKey(b.getUUID(), result.getKey()), ((ResourceExtension) result).getKeyHash());
        assertEquals(r.getVersionId(), result.getVersionId());
        assertEquals(random_key, result.snapshotMetaData().get("x-key"));
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
