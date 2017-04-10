package com.chigix.resserver.domain.error;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class BucketAlreadyOwnedByYou extends BucketAlreadyExists {

    @Override
    public String getCode() {
        return "BucketAlreadyOwnedByYou";
    }

    @Override
    public String getMessage() {
        return "Your previous request to create the named bucket succeeded and you already own it.";
    }

}
