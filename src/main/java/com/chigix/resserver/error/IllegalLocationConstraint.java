package com.chigix.resserver.error;

import com.chigix.resserver.util.XPathNode;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.router.HttpException;
import io.netty.handler.codec.http.router.HttpRouted;

/**
 * IllegalLocationConstraint Restful API Error with 400 BadRequest.
 *
 * Example of Message: The unspecified location constraint is incompatible for
 * the region specific endpoint this request was sent to.
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class IllegalLocationConstraint extends HttpException implements RESTError {

    private final HttpRouted routedInfo;

    public IllegalLocationConstraint(String message, HttpRouted routedInfo) {
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

    @Override
    public HttpResponseStatus getResponseCode() {
        return HttpResponseStatus.BAD_REQUEST;
    }

    @Override
    public XPathNode[] getExtraMessage() {
        return new XPathNode[0];
    }

}
