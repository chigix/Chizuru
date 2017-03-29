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

    Map<String, Object> selectByKeyhash_Version(@Param("keyhash") String keyhash, @Param("versionId") String versionId);

    List<Map<String, Object>> selectSubResourceByParentKeyhash(@Param("keyhash") String keyhash);

    /**
     *
     * @param keyhash
     * @param continuation SubResource's version_id is used as continuation
     * token.
     * @return
     */
    List<Map<String, Object>> selectSubResourceByParentKeyhash(@Param("keyhash") String keyhash, @Param("continuation") String continuation);

    Map<String, Object> selectSubResourceByKeyEtagParent(@Param("resourceKey") String key, @Param("parent_version_id") String parentVersionId, @Param("etag") String etag);

    int removeSubResourcesByParent(@Param("parent_version_id") String parentVersionId);

    int insert(ResourceDto r);

    int update(ResourceDto r);

    int merge(ResourceDto r);

    int delete(Resource r);

    int deleteByKeyhash(@Param("keyhash") String keyhash);

}
