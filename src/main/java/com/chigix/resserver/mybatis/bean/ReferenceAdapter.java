package com.chigix.resserver.mybatis.bean;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 * @param <T>
 */
public interface ReferenceAdapter<T> {

    T getAdapted();

    public static class NullAdapter<T> implements ReferenceAdapter<T> {

        @Override
        public T getAdapted() {
            return null;
        }
    }

}
