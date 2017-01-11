package com.chigix.resserver.entity.error;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class DaoException extends Exception {

    public String getCode() {
        return this.getClass().getSimpleName();
    }

}
