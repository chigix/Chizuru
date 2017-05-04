package com.chigix.resserver.util;

import io.netty.handler.codec.http.HttpRequest;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class HttpHeaderUtil {

    public static boolean isGzip(HttpRequest req) {
        String[] accepting_encodings;
        try {
            accepting_encodings = req.headers().get(HttpHeaderNames.ACCEPT_ENCODING).toString().split(",");
        } catch (NullPointerException e) {
            accepting_encodings = new String[]{};
        }
        for (String accepting_encoding : accepting_encodings) {
            if (accepting_encoding.trim().equalsIgnoreCase("gzip")) {
                return true;
            }
        }
        return false;
    }
}
