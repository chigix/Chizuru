package com.chigix.resserver.error;

import com.chigix.resserver.interfaces.xml.XPathNode;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.router.HttpException;
import io.netty.handler.codec.http.router.HttpRouted;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class InvalidRange extends HttpException implements RESTError {

    private final HttpRouted routedInfo;

    private final String requestRange;
    private final String actualSize;

    public InvalidRange(HttpRouted routedInfo, String requestedRange, String actualSize) {
        super("The requested range is not satisfiable.");
        this.routedInfo = routedInfo;
        this.requestRange = requestedRange;
        this.actualSize = actualSize;
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
        return "InvalidRange";
    }

    @Override
    public XPathNode[] getExtraMessage() {
        XPathNode[] extras = new XPathNode[2];
        extras[0] = new XPathNode("RangeRequested");
        if (requestRange == null) {
            extras[0].setContentText("null");
        } else {
            extras[0].setContentText(requestRange);
        }
        extras[1] = new XPathNode("ActualObjectSize");
        if (actualSize == null) {
            extras[1].setContentText("null");
        } else {
            extras[1].setContentText(actualSize);
        }
        return extras;
    }

}
