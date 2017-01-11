package com.chigix.resserver.entity.error;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class NoSuchBucket extends DaoException implements BucketInfo {

    private final String bucketName;

    public NoSuchBucket(String bucketName) {
        this.bucketName = bucketName;
    }

    @Override
    public String getCode() {
        return "NoSuchBucket";
    }

    @Override
    public String getMessage() {
        return "The specified bucket does not exist";
    }

    @Override
    public String getBucketName() {
        return bucketName;
    }

}
