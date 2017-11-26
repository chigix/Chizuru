package com.chigix.resserver.mybatis.mapstruct;

import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import com.chigix.resserver.mybatis.record.Subresource;
import org.mapstruct.ObjectFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@Component
public class BeanFactory {

    @ObjectFactory
    public ChunkedResourceBean createChunkedResource(Subresource record) {
        ChunkedResourceBean resource = new ChunkedResourceBean(record.getIndexInParent(), record.getVersionId(), record.getIndexInParent());
        return resource;
    }
}
