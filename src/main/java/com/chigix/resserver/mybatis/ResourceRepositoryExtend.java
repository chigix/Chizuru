package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.ChunkedResource;
import com.chigix.resserver.mybatis.specification.ResourceSpecification;
import java.util.Iterator;
import com.chigix.resserver.domain.dao.ResourceDao;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface ResourceRepositoryExtend extends ResourceDao {

    <T extends ChunkedResource> Iterator<T> listSubResources(ResourceSpecification.byParentResource spec);
}
