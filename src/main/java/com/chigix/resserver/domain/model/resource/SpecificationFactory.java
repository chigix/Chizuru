package com.chigix.resserver.domain.model.resource;

import com.chigix.resserver.domain.Specification;
import com.chigix.resserver.domain.model.bucket.Bucket;
import java.math.BigInteger;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface SpecificationFactory {

    <T extends Specification<Resource>> T bucketIs(Bucket bucket);

    /**
     *
     * @param <T>
     * @param keyhash Keyhash of a resource is used as the continuation token
     * for Resource model.
     * @return
     */
    <T extends Specification<Resource>> T continuateFrom(String keyhash);

    <T extends Specification<Resource>> T subresourceParentIs(AmassedResource parent);

    <T extends Specification<Resource>> T subresourceByteRangeInParent(BigInteger byte_pos);

    <T extends Specification<Resource>> T subresourceByteRangeInParent(BigInteger byte_pos, BigInteger byte_end);

    <T extends Specification<Resource>> T subresourceIndexInParentIs(String index);

    SubresourceSpecification subresourceInfo(AmassedResource parent,
            BigInteger range_start, BigInteger range_end,
            String index_in_parent);

    <T extends Specification<Resource>> T keyStartWith(String prefix);
}
