package com.chigix.resserver.domain.error;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class UnexpectedLifecycleException extends RuntimeException {

    public UnexpectedLifecycleException() {
    }

    public UnexpectedLifecycleException(String message) {
        super(message);
    }

    public UnexpectedLifecycleException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedLifecycleException(Throwable cause) {
        super(cause);
    }

    public UnexpectedLifecycleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
