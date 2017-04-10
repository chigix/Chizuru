package com.chigix.resserver.domain.error;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class NoSuchKey extends DaoException implements ResourceInfo {

    private final String resourceKey;

    public NoSuchKey(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    @Override
    public String getCode() {
        return "NoSuchKey";
    }

    @Override
    public String getMessage() {
        return "The specified key does not exist.";
    }

    @Override
    public String getResourceKey() {
        return resourceKey;
    }

}
