package com.chigix.resserver.domain.model.resource;

import com.chigix.resserver.domain.Specification;
import java.math.BigInteger;

/**
 *
 * @author　Richard Lea <chigix@zoho.com>
 */
public interface SubresourceSpecification extends Specification<ChunkedResource> {

    AmassedResource getParentResource();

    BigInteger getRangeStartInParent();

    BigInteger getRangeEndInParent();

    String getPartIndexInParent();

}
