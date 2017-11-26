package com.chigix.resserver.mybatis.record;

import java.math.BigInteger;
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
     * Test of toBase256 method, of class Util.
     */
    @Test
    public void testToBase256() {
        System.out.println("toBase256");
        String hex_1 = "22553333";
        String hex_0 = "00113344";
        BigInteger num = new BigInteger(hex_1 + hex_0, 16);
        int[] expResult = new int[]{
            Integer.parseInt(hex_0, 16),
            Integer.parseInt(hex_1, 16)};
        int[] result = Util.toBase256(num);
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of fromBase256 method, of class Util.
     */
    @Test
    public void testFromBase256() {
        System.out.println("fromBase256");
        final String hex_1 = "5533";
        final String hex_0 = "00113344";
        int[] place_value = new int[]{
            Integer.parseInt(hex_0, 16),
            Integer.parseInt(hex_1, 16),};
        BigInteger expResult = new BigInteger(hex_1 + hex_0);
        BigInteger result = Util.fromBase256(place_value);
        assertEquals(expResult, result);
    }

}
