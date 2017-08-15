package com.chigix.resserver.endpoint.GetResource;

import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import com.chigix.resserver.sharablehandlers.Context;
import com.chigix.resserver.util.HttpHeaderUtil.InvalidRangeHeader;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.router.HttpRouted;
import io.netty.handler.codec.http.router.RoutingConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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
public class ResourceContextTest {

    public ResourceContextTest() {
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
     * Test of getRange method, of class ResourceContext.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetRange() throws Exception {
        System.out.println("getRange");
        DefaultHttpRequest http_req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/testing/bankai");
        http_req.headers().set("Range", "bytes=520568-520568");
        assertEquals(buildContextTesting(http_req, "520569").getRange().start, "520568");
        assertEquals(buildContextTesting(http_req, "520569").getRange().end, "520568");
        try {
            assertEquals(buildContextTesting(http_req, "520567").getRange().end, "520568");
            fail("InvalidRangeHeader Exception should be thrown.");
        } catch (InvalidRangeHeader invalidRangeHeader) {
        }
        http_req.headers().set("Range", "bytes=520568-520567");
        try {
            assertEquals(buildContextTesting(http_req, "520569").getRange().end, "520568");
            fail("InvalidRangeHeader Exception is not thrown.");
        } catch (InvalidRangeHeader invalidRangeHeader) {
        }
        http_req.headers().set("Range", "bytes=500-600,601-999");
        assertEquals(buildContextTesting(http_req, "520569").getRange().start, "500");
        assertEquals(buildContextTesting(http_req, "520569").getRange().end, "999");
        http_req.headers().set("Range", "bytes=500-600,400-999");
        assertEquals(buildContextTesting(http_req, "520569").getRange().start, "500");
        assertEquals(buildContextTesting(http_req, "520569").getRange().end, "999");
        http_req.headers().set("Range", "bytes=-500");
        assertEquals(buildContextTesting(http_req, "520569").getRange().start, "520068");
        assertEquals(buildContextTesting(http_req, "520569").getRange().end, "520568");
        http_req.headers().set("Range", "bytes=500-");
        assertEquals(buildContextTesting(http_req, "520569").getRange().start, "500");
        assertEquals(buildContextTesting(http_req, "520569").getRange().end, "520568");
    }

    private ResourceContext buildContextTesting(HttpRequest http_req, String chunksize) throws InvalidRangeHeader {
        ChunkedResourceBean chunk_resource = new ChunkedResourceBean("TESTING_KEY", UUID.randomUUID().toString());
        chunk_resource.setSize(chunksize);
        return new ResourceContext(new Context(new HttpRouted(http_req) {
            @Override
            public Map<String, Object> decodedParams() {
                return new HashMap<>();
            }

            @Override
            public RoutingConfig unwrapRoutingConf() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public String getPatternName() {
                return "TESTING";
            }

            @Override
            public ChannelHandlerContext getChannelHandlerContext() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }, chunk_resource));
    }

    public void testGetRange_2() {
        //bytes=500-600,601-999
    }

}
