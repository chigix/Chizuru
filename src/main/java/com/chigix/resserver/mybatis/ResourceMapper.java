package com.chigix.resserver.mybatis;

import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.mybatis.dto.ResourceBuilder;
import com.chigix.resserver.mybatis.dto.ResourceDto;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface ResourceMapper {

    List<ResourceBuilder> selectAllByBucketName(@Param("bucketName") String bucketName, @Param("limit") int limit);

    List<ResourceBuilder> selectAllByBucketName(@Param("bucketName") String bucketName, @Param("limit") int limit, @Param("continuation") String continuation);

    ResourceBuilder selectByKeyhash(@Param("keyhash") String keyhash);

    ResourceBuilder selectByBucketName_Key(@Param("bucketName") String bucketName, @Param("resourceKey") String key);

    ResourceBuilder selectByKeyhash_Version(@Param("keyhash") String keyhash, @Param("versionId") String versionId);

    List<ResourceBuilder> selectSubResourceByParentKeyhash(@Param("keyhash") String keyhash);

    /**
     *
     * @param keyhash
     * @param continuation SubResource's version_id is used as continuation
     * token.
     * @return
     */
    List<ResourceBuilder> selectSubResourceByParentKeyhash(@Param("keyhash") String keyhash, @Param("continuation") String continuation);

    ResourceBuilder selectSubResourceByKeyEtagParent(@Param("resourceKey") String key, @Param("parent_version_id") String parentVersionId, @Param("etag") String etag);

    int removeSubResourcesByParent(@Param("parent_version_id") String parentVersionId);

    int insertResource(ResourceDto r);

    int insertSubResource(ResourceDto r);

    int update(ResourceDto r);

    int mergeResource(ResourceDto r);

    int delete(Resource r);

    int deleteByKeyhash(@Param("keyhash") String keyhash);

}
