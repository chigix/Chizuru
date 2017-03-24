package com.chigix.resserver.mybatis;

import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.mybatis.dto.ResourceDto;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface ResourceMapper {

    List<Map<String, Object>> selectAllByBucketName(@Param("bucketName") String bucketName, @Param("limit") int limit);

    List<Map<String, Object>> selectAllByBucketName(@Param("bucketName") String bucketName, @Param("limit") int limit, @Param("continuation") String continuation);

    Map<String, Object> selectByKeyhash(@Param("keyhash") String keyhash);

    Map<String, Object> selectByBucketName_Key(@Param("bucketName") String bucketName, @Param("resourceKey") String key);

    int insert(ResourceDto r);

    int update(ResourceDto r);

    int merge(ResourceDto r);

    int delete(Resource r);

    int deleteByKeyhash(@Param("keyhash") String keyhash);

}
