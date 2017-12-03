package com.chigix.resserver.mybatis.specification;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 * @param <T>
 * @param <C>
 */
public abstract class QueryCriteriaSpecification<T, C>
        extends AbstractSpecification<T> {

    public abstract C appendCriteria(final C criteria);

    public static class EmptyQueryCriteriaSpecification<T, C>
            extends QueryCriteriaSpecification<T, C> {

        @Override
        public C appendCriteria(C criteria) {
            return criteria;
        }
    }

}
