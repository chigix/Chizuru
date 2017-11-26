package com.chigix.resserver.mybatis.specification;

import com.chigix.resserver.domain.model.resource.AmassedResource;
import com.chigix.resserver.mybatis.record.SubresourceExample;
import java.math.BigInteger;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface ResourceSpecification {

    public static class byParentResource implements IndexOfParentOffset {

        private final AmassedResource parentResource;

        private final BigInteger byteStart;

        /**
         * @TODO This specification is designed for byte range support, however
         * currently it is used as a Criteria for querying a list of Subresources,
         * where the fromByte is actually the byte included in the first position
         * of the Subresources list rather than the real first byte of this list.
         *
         * @param parentResource
         * @param fromByte An integer in byte unit indicating the beginning of
         * the request range.
         */
        public byParentResource(AmassedResource parentResource, BigInteger fromByte) {
            this.parentResource = parentResource;
            this.byteStart = fromByte;
        }

        public SubresourceExample.Criteria build(SubresourceExample.Criteria criteria) {
            return criteria.andParentVersionIdEqualTo(parentResource.getVersionId());
        }

        @Override
        public BigInteger getByteStart() {
            return byteStart;
        }

    }

}
