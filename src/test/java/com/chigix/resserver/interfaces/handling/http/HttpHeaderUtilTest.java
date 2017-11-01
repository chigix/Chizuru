package com.chigix.resserver.interfaces.handling.http;

import io.netty.handler.codec.http.HttpRequest;
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
public class HttpHeaderUtilTest {

    public HttpHeaderUtilTest() {
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
     * Test of isGzip method, of class HttpHeaderUtil.
     */
    @Test
    public void testIsGzip() {
        System.out.println("isGzip");
        HttpRequest req = null;
        boolean expResult = false;
        boolean result = HttpHeaderUtil.isGzip(req);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of decodeRange method, of class HttpHeaderUtil.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDecodeRange() throws Exception {
        System.out.println("decodeRange");
        // The first 500 bytes (byte offsets 0-499, inclusive):  
        String first500bytes = "bytes=0-499";
        // The second 500 bytes (byte offsets 500-999, inclusive):
        String second500bytes = "bytes=500-999";
        // The final 500 bytes (byte offsets 9500-9999, inclusive):
        String final500bytes = "bytes=-500";
        String final500bytes_2 = "bytes=9500-";
        // The first and last bytes only (bytes 0 and 9999):
        String firstAndLast = "bytes=0-0,-1";
        // legal but not canonical specifications 
        String uncanonical_1 = "bytes=500-600,601-999";
        String uncanonical_2 = "bytes=500-700,601-999";
        for (HttpHeaderUtil.Range range : HttpHeaderUtil.decodeRange(first500bytes)) {
            assertEquals("0", range.start);
            assertEquals("499", range.end);
            assertNull(range.suffixLength);
        }
        for (HttpHeaderUtil.Range range : HttpHeaderUtil.decodeRange(second500bytes)) {
            assertEquals("500", range.start);
            assertEquals("999", range.end);
            assertNull(range.suffixLength);
        }
        for (HttpHeaderUtil.Range range : HttpHeaderUtil.decodeRange(final500bytes)) {
            assertNull(range.start);
            assertNull(range.end);
            assertEquals("500", range.suffixLength);
        }
        for (HttpHeaderUtil.Range range : HttpHeaderUtil.decodeRange(final500bytes_2)) {
            assertEquals("9500", range.start);
            assertNull(range.end);
            assertNull(range.suffixLength);
        }
        HttpHeaderUtil.Range[] ranges = HttpHeaderUtil.decodeRange(uncanonical_1);
        assertEquals("500", ranges[0].start);
        assertEquals("600", ranges[0].end);
        assertNull(ranges[0].suffixLength);
        assertEquals("601", ranges[1].start);
        assertEquals("999", ranges[1].end);
        assertNull(ranges[1].suffixLength);
        ranges = HttpHeaderUtil.decodeRange(uncanonical_2);
        assertEquals("500", ranges[0].start);
        assertEquals("700", ranges[0].end);
        assertNull(ranges[0].suffixLength);
        assertEquals("601", ranges[1].start);
        assertEquals("999", ranges[1].end);
        assertNull(ranges[1].suffixLength);
    }

}
