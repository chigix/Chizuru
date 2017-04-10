package com.chigix.resserver.domain.error;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class BucketAlreadyExists extends DaoException {

    @Override
    public String getCode() {
        return "BucketAlreadyExists";
    }

}
