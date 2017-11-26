package com.chigix.resserver.mybatis.mapstruct;

import com.chigix.resserver.domain.model.resource.AmassedResource;
import com.chigix.resserver.mybatis.ResourceRepositoryExtend;
import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import com.chigix.resserver.mybatis.specification.ResourceSpecification;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;

/**
 * @TODO: remove this class and introduce selectSubresource method in
 * ResourceDaoImpl instead.
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface SubresourcesAdapter {

    <T extends ChunkedResourceBean> Iterator<T> iterateFromByte(int byte_pos);

    public static class DefaultSubresourcesAdapter implements SubresourcesAdapter {

        private final AmassedResource parentResource;

        private final ResourceRepositoryExtend resourceRepository;

        public DefaultSubresourcesAdapter(AmassedResource parentResource,
                ResourceRepositoryExtend resourceRepository) {
            this.parentResource = parentResource;
            this.resourceRepository = resourceRepository;
        }

        @Override
        public <T extends ChunkedResourceBean> Iterator<T> iterateFromByte(int byte_pos) {
            return resourceRepository.listSubResources(
                    new ResourceSpecification.byParentResource(
                            parentResource, new BigInteger(byte_pos + "")));
        }
    }

    public static class EmptyAdapter implements SubresourcesAdapter {

        public EmptyAdapter() {
        }

        @Override
        public <T extends ChunkedResourceBean> Iterator<T> iterateFromByte(int byte_pos) {
            return Collections.emptyIterator();
        }

    }

}
