package com.chigix.resserver.mybatis.dao;

import com.chigix.resserver.mybatis.record.Resource;
import org.apache.ibatis.annotations.Param;

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

    /**
     * @TODO: remove
     *
     * @param uploadId
     * @return
     */
    int deleteByUploadId(@Param("upload_id") String uploadId);
}
