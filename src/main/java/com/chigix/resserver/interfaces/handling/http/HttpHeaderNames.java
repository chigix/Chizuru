package com.chigix.resserver.interfaces.handling.http;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface HttpHeaderNames {

    public static final String AMZ_COPY_RESOURCE = "x-amz-copy-source";

    public static final String AMZ_METADATA_DIRECTIVE = "x-amz-metadata-directive";

    public static final String CONNECTION = "Connection";

    public static final String DATE = "Date";

    public static final String SERVER = "Server";

    //http://www.w3.org/Protocols/rfc1341/4_Content-Type.html
    public static final String CONTENT_TYPE = "Content-Type";

    //http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.41
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";

    //http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.11
    public static final String CONTENT_ENCODING = "Content-Encoding";

    //http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.13
    public static final String CONTENT_LENGTH = "Content-Length";

    //http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.29
    public static final String LAST_MODIFIED = "Last-Modified";

    //http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.30
    public static final String LOCATION = "Location";

    //http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.19
    public static final String ETAG = "ETag";

    //http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9
    public static final String CACHE_CONTROL = "Cache-Control";

    //http://www.w3.org/Protocols/rfc2616/rfc2616-sec19.html#sec19.5.1
    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    //http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.3
    public static final String ACCEPT_ENCODING = "Accept-Encoding";

    //http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.35
    public static final String ACCEPT_RANGES = "Accept-Ranges";

    //http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.16
    public static final String CONTENT_RANGE = "Content-Range";

}
