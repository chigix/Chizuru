package com.chigix.resserver.mybatis.specification;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 * @param <T>
 */
public class ContinuationOffsetSpecification<T> extends AbstractSpecification<T> {

    private final String continuationToken;

    public ContinuationOffsetSpecification(String continuationToken) {
        this.continuationToken = continuationToken;
    }

    public String getContinuationToken() {
        return continuationToken;
    }

}
