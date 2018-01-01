package com.chigix.resserver.domain.error;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class DaoException extends Exception {

    public String getCode() {
        return "InternalError";
    }

    @Override
    public String getMessage() {
        return "We encountered an internal error. Please try again.";
    }

    public HttpResponse fixResponse(FullHttpResponse resp) {
        return resp;
    }

}
