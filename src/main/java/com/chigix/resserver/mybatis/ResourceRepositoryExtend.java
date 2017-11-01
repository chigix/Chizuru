package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.model.resource.ChunkedResource;
import com.chigix.resserver.mybatis.specification.ResourceSpecification;
import java.util.Iterator;
import com.chigix.resserver.domain.model.resource.ResourceRepository;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface ResourceRepositoryExtend extends ResourceRepository {

    <T extends ChunkedResource> Iterator<T> listSubResources(ResourceSpecification.byParentResource spec);
}
