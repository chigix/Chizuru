package com.chigix.resserver.mybatis.specification;

import com.chigix.resserver.domain.AmassedResource;
import com.chigix.resserver.mybatis.record.SubresourceExample;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface ResourceSpecification {

    public static class byParentResource implements IndexOfParentOffset {

        private final AmassedResource parentResource;

        private final String beginIndex;

        /**
         *
         * @param parentResource
         * @param beginIndex The begin index should be an integer greater than
         * 1, inclusive.
         */
        public byParentResource(AmassedResource parentResource, String beginIndex) {
            this.parentResource = parentResource;
            this.beginIndex = beginIndex;
        }

        public SubresourceExample.Criteria build(SubresourceExample.Criteria criteria) {
            return criteria.andParentVersionIdEqualTo(parentResource.getVersionId());
        }

        @Override
        public String getBeginIndex() {
            return beginIndex;
        }
    }

}
