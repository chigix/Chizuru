package com.chigix.resserver.domain.error;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class InvalidRequest extends DaoException {

    @Override
    public String getCode() {
        return "InvalidRequest";
    }

    @Override
    public String getMessage() {
        return "The specified copy source is not supported as a byte-range copy source.";
    }

}
