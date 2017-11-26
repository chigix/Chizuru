package com.chigix.resserver.domain.model.resource;

import java.math.BigInteger;

/**
 *
 * @author　Richard Lea <chigix@zoho.com>
 */
public interface SubresourceSpecification {

    AmassedResource getParentResource();

    BigInteger getRangeStartInParent();

    BigInteger getRangeEndInParent();

    String getPartIndexInParent();

    public static SubresourceSpecification build(
            final AmassedResource parent,
            final BigInteger range_start,
            final BigInteger range_end,
            final String index_in_parent) {
        return new SubresourceSpecification() {
            @Override
            public AmassedResource getParentResource() {
                return parent;
            }

            @Override
            public BigInteger getRangeStartInParent() {
                return range_start;
            }

            @Override
            public BigInteger getRangeEndInParent() {
                return range_end;
            }

            @Override
            public String getPartIndexInParent() {
                return index_in_parent;
            }

        };
    }

}
