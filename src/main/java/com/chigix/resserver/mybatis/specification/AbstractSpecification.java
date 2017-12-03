package com.chigix.resserver.mybatis.specification;

import com.chigix.resserver.domain.Specification;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 * @param <T> Model Type
 */
public abstract class AbstractSpecification<T> implements Specification<T> {

    private ContinuationOffsetSpecification<T> continuationSpec = null;

    public ContinuationOffsetSpecification<T> getContinuationSpec() {
        return continuationSpec;
    }

    @Override
    public Specification<T> and(Specification<T> specification) {
        if (specification instanceof ContinuationOffsetSpecification) {
            this.continuationSpec = (ContinuationOffsetSpecification<T>) specification;
            return this;
        }
        if (this instanceof ContinuationOffsetSpecification
                && specification instanceof QueryCriteriaSpecification) {
            ((AbstractSpecification<T>) specification).continuationSpec
                    = (ContinuationOffsetSpecification<T>) this;
            return this;
        }
        return new AndSpecification<>((QueryCriteriaSpecification) this, (QueryCriteriaSpecification) specification);
    }

    @Override
    public Specification<T> or(Specification<T> specification) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static class AndSpecification<T, C> extends QueryCriteriaSpecification<T, C> {

        private final QueryCriteriaSpecification<T, C> spec1;

        private final QueryCriteriaSpecification<T, C> spec2;

        public AndSpecification(QueryCriteriaSpecification<T, C> spec1, QueryCriteriaSpecification<T, C> spec2) {
            this.spec1 = spec1;
            this.spec2 = spec2;
        }

        @Override
        public C appendCriteria(final C criteria) {
            C spec = spec1.appendCriteria(criteria);
            spec = spec2.appendCriteria(spec);
            return spec;
        }

    }
}
