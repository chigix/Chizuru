package com.chigix.resserver.endpoint.GetBucket;

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
public class UtilTest {

    public UtilTest() {
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
     * Test of extractCommonPrefix method, of class Util.
     */
    @Test
    public void testExtractCommonPrefix() {
        System.out.println("extractCommonPrefix");
        assertNull(Util.extractCommonPrefix("ExampleObject.txt", null, null));
        assertNull(Util.extractCommonPrefix("ExampleObject.txt", null, "/"));
        assertEquals("bankai/", Util.extractCommonPrefix("bankai/ExampleObject.txt", null, "/"));
        assertEquals("bankai/", Util.extractCommonPrefix("bankai/subdir/ExampleObject.txt", "bankai", "/"));
        assertEquals("bankai/subdir/", Util.extractCommonPrefix("bankai/subdir/ExampleObject.txt", "bankai/", "/"));
        assertNull(Util.extractCommonPrefix("bankai/subdir/ExampleObject.txt", "bankai/subdir/ExampleObject", "/"));
        assertNull(Util.extractCommonPrefix("photos/2006/", "photos/2006/", "/"));
    }

}
