package com.chigix.resserver.mybatis.mapstruct;

import com.chigix.resserver.domain.model.resource.AmassedResource;
import com.chigix.resserver.mybatis.ResourceRepositoryExtend;
import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import com.chigix.resserver.mybatis.specification.ResourceSpecification;
import java.util.Collections;
import java.util.Iterator;

/**
 * @TODO: remove this class and introduce selectSubresource method in
 * ResourceDaoImpl instead.
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface SubresourcesAdapter {

    <T extends ChunkedResourceBean> Iterator<T> iterate(int start_index);

    public static class DefaultSubresourcesAdapter implements SubresourcesAdapter {

        private final AmassedResource parentResource;

        private final ResourceRepositoryExtend resourceRepository;

        public DefaultSubresourcesAdapter(AmassedResource parentResource,
                ResourceRepositoryExtend resourceRepository) {
            this.parentResource = parentResource;
            this.resourceRepository = resourceRepository;
        }

        @Override
        public Iterator<ChunkedResourceBean> iterate(final int start_index) {
            return resourceRepository.listSubResources(
                    new ResourceSpecification.byParentResource(
                            parentResource, start_index + ""));
        }
    }

    public static class EmptyAdapter implements SubresourcesAdapter {

        public EmptyAdapter() {
        }

        @Override
        public Iterator<ChunkedResourceBean> iterate(int start_index) {
            return Collections.emptyIterator();
        }

    }

}
