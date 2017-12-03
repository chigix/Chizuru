package com.chigix.resserver.domain;

/**
 * Specification Interface.
 *
 * @author Richard Lea <chigix@zoho.com>
 * @param <T>
 */
public interface Specification<T> {

    /**
     * Create a new specification that is the {@code AND} operation of
     * {@code this} specification and another specification.
     *
     * @param specification Specification to AND.
     * @return A new specification.
     */
    Specification<T> and(Specification<T> specification);

    /**
     * Create a new specification that is the {@code OR} operation of
     * {@code this} specification and another specification.
     *
     * @param specification Specification to OR.
     * @return A new specification.
     */
    Specification<T> or(Specification<T> specification);

}
