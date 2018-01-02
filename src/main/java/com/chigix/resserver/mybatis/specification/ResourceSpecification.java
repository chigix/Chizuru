package com.chigix.resserver.mybatis.specification;

import com.chigix.resserver.domain.model.bucket.Bucket;
import com.chigix.resserver.domain.model.resource.AmassedResource;
import com.chigix.resserver.domain.model.resource.ChunkedResource;
import com.chigix.resserver.domain.model.resource.Resource;
import com.chigix.resserver.domain.model.resource.SpecificationFactory;
import com.chigix.resserver.domain.model.resource.SubresourceSpecification;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.record.ResourceExample;
import com.chigix.resserver.mybatis.record.SubresourceExample;
import java.math.BigInteger;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ResourceSpecification implements SpecificationFactory {

    public ResourceSpecification() {
        // Intentionally Empty
    }

    /**
     * {@inheritDoc }
     *
     * @return
     */
    @Override
    public QueryCriteriaSpecification<Resource, ResourceExample.Criteria> bucketIs(Bucket bucket) {
        if (bucket instanceof BucketBean) {
            return new QueryCriteriaSpecification<Resource, ResourceExample.Criteria>() {
                @Override
                public ResourceExample.Criteria appendCriteria(ResourceExample.Criteria criteria) {
                    return criteria.andBucketUuidEqualTo(((BucketBean) bucket).getUuid());
                }
            };
        }
        throw new RuntimeException("Unexpected!! Unpersisted Bucket Bean Object.");
    }

    @Override
    public ContinuationOffsetSpecification<Resource> continuateFrom(String keyhash) {
        return new ContinuationOffsetSpecification<>(keyhash);
    }

    @Override
    public QueryCriteriaSpecification<Resource, SubresourceExample.Criteria> subresourceParentIs(AmassedResource parent) {
        return new QueryCriteriaSpecification<Resource, SubresourceExample.Criteria>() {
            @Override
            public SubresourceExample.Criteria appendCriteria(SubresourceExample.Criteria criteria) {
                return criteria.andParentVersionIdEqualTo(parent.getVersionId());
            }
        };
    }

    @Override
    public QueryCriteriaSpecification<Resource, SubresourceExample.Criteria> subresourceByteRangeInParent(BigInteger byte_pos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public QueryCriteriaSpecification<Resource, SubresourceExample.Criteria> subresourceByteRangeInParent(BigInteger byte_pos, BigInteger byte_end) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public QueryCriteriaSpecification<Resource, SubresourceExample.Criteria> subresourceIndexInParentIs(String index) {
        return new QueryCriteriaSpecification<Resource, SubresourceExample.Criteria>() {
            @Override
            public SubresourceExample.Criteria appendCriteria(SubresourceExample.Criteria criteria) {
                return criteria.andIndexInParentEqualTo(index);
            }
        };
    }

    @Override
    public SubresourceSpecification subresourceInfo(AmassedResource parent, BigInteger range_start, BigInteger range_end, String index_in_parent) {
        SubresourceSpecificationImpl spec = new SubresourceSpecificationImpl();
        spec.byteRangeEnd = range_end;
        spec.byteRangeStart = range_start;
        spec.indexInParent = index_in_parent;
        spec.parent = parent;
        return spec;
    }

    @Override
    public QueryCriteriaSpecification<Resource, ResourceExample.Criteria>
            keyStartWith(final String prefix) {
        return new QueryCriteriaSpecification<Resource, ResourceExample.Criteria>() {
            @Override
            public ResourceExample.Criteria appendCriteria(ResourceExample.Criteria criteria) {
                if (prefix == null || prefix.length() < 1) {
                    return criteria;
                }
                return criteria.andKeyLike(prefix + "%");
            }
        };
    }

    public static class byParentResource implements IndexOfParentOffset {

        private final AmassedResource parentResource;

        private final BigInteger byteStart;

        /**
         * @TODO This specification is designed for byte range support, however
         * currently it is used as a Criteria for querying a list of
         * Subresources, where the fromByte is actually the byte included in the
         * first position of the Subresources list rather than the real first
         * byte of this list.
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

    public static class SubresourceSpecificationImpl
            extends QueryCriteriaSpecification<ChunkedResource, ResourceExample.Criteria>
            implements SubresourceSpecification {

        private AmassedResource parent = null;
        private BigInteger byteRangeStart = null;
        private BigInteger byteRangeEnd = null;
        private String indexInParent = null;

        @Override
        public ResourceExample.Criteria appendCriteria(ResourceExample.Criteria criteria) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public AmassedResource getParentResource() {
            return parent;
        }

        @Override
        public BigInteger getRangeStartInParent() {
            return byteRangeStart;
        }

        @Override
        public BigInteger getRangeEndInParent() {
            return byteRangeEnd;
        }

        @Override
        public String getPartIndexInParent() {
            return indexInParent;
        }
    }

}
