package com.chigix.resserver.interfaces.io;

import com.chigix.resserver.interfaces.io.IteratorInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
public class IteratorInputStreamIT {

    public IteratorInputStreamIT() {
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
     * Test of read method, of class IteratorInputStream.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testRead() throws Exception {
        System.out.println("read");
        final String unit = "TEST_STRING";
        List<String> strings = new ArrayList<>(1000);
        for (int i = 0; i < strings.size(); i++) {
            strings.set(i, unit);
        }
        IteratorInputStream instance = new IteratorInputStream<String>(strings.iterator()) {
            @Override
            protected InputStream inputStreamProvider(String item) throws NoSuchElementException {
                return new ByteArrayInputStream(item.getBytes());
            }
        };
        for (int i = 0; i < unit.length() * strings.size(); i++) {
            assertEquals(unit.charAt(i % unit.length()), (char) instance.read());
        }
        assertEquals(-1, instance.read());
    }

    /**
     * Test of read method, of class IteratorInputStream.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testRead_byteArr() throws Exception {
        System.out.println("read");
        final String unit = "TEST_STRING";
        List<String> strings = new ArrayList<>(1000);
        for (int i = 0; i < strings.size(); i++) {
            strings.set(i, unit);
        }
        IteratorInputStream instance = new IteratorInputStream<String>(strings.iterator()) {
            @Override
            protected InputStream inputStreamProvider(String item) throws NoSuchElementException {
                return new ByteArrayInputStream(item.getBytes());
            }
        };
        for (int i = 0; i < strings.size(); i++) {
            assertEquals(unit.length(), instance.read(new byte[unit.length()]));
        }
        assertEquals(-1, instance.read());
    }

    /**
     * Test of read method, of class IteratorInputStream.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testRead_3args() throws Exception {
        System.out.println("read");
        final String unit = "TEST_STRING";
        List<String> strings = new ArrayList<>(1000);
        for (int i = 0; i < strings.size(); i++) {
            strings.set(i, unit);
        }
        IteratorInputStream instance = new IteratorInputStream<String>(strings.iterator()) {
            @Override
            protected InputStream inputStreamProvider(String item) throws NoSuchElementException {
                return new ByteArrayInputStream(item.getBytes());
            }
        };
        for (int i = 0; i < unit.length() * strings.size(); i++) {
            assertEquals(unit.charAt(i % unit.length()), (char) instance.read());
        }
        assertEquals(-1, instance.read());
    }

}
