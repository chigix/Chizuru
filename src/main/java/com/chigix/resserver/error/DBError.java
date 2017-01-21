package com.chigix.resserver.error;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class DBError extends RuntimeException {

    public DBError(String message) {
        super(message);
    }

    public DBError(String message, Throwable cause) {
        super(message, cause);
    }

    public DBError(Throwable cause) {
        super(cause);
    }

    public DBError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
