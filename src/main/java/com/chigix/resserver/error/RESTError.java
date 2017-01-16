package com.chigix.resserver.error;

import io.netty.handler.codec.http.HttpRequest;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface RESTError {

    String getCode();

    String getMessage();

    HttpRequest getHttpRequest();

}
