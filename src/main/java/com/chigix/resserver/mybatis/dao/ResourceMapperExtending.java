package com.chigix.resserver.mybatis.dao;

import com.chigix.resserver.mybatis.record.Resource;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface ResourceMapperExtending {

    int merge(Resource record);

    /**
     *
     * @param r
     * @return
     */
    int mergeUploadingResource(Resource r);

}
