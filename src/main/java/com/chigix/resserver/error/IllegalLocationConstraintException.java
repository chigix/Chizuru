package com.chigix.resserver.error;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.router.HttpException;
import io.netty.handler.codec.http.router.HttpRouted;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class IllegalLocationConstraintException extends HttpException implements RESTError {

    private final HttpRouted routedInfo;

    public IllegalLocationConstraintException(String message, HttpRouted routedInfo) {
        super(message);
        this.routedInfo = routedInfo;
    }

    @Override
    public HttpRequest getHttpRequest() {
        return routedInfo.getRequestMsg();
    }

    @Override
    public HttpRouted getHttpRouted() {
        return routedInfo;
    }

    @Override
    public String getCode() {
        return "IllegalLocationConstraintException";
    }

}
