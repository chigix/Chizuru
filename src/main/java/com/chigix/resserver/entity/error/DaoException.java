package com.chigix.resserver.entity.error;

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

}
