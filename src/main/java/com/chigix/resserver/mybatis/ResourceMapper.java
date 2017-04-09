package com.chigix.resserver.mybatis;

import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.mybatis.dto.MultipartUploadDto;
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

    /**
     *
     * @param bucketName
     * @param limit
     * @param continuation Resource's Keyhash is used as continuation.
     * @return
     */
    List<ResourceBuilder> selectAllByBucketName(@Param("bucketName") String bucketName, @Param("limit") int limit, @Param("continuation") String continuation);

    ResourceBuilder selectByKeyhash(@Param("keyhash") String keyhash);

    ResourceBuilder selectByBucketName_Key(@Param("bucketName") String bucketName, @Param("resourceKey") String key);

    ResourceBuilder selectByKeyhash_Version(@Param("keyhash") String keyhash, @Param("versionId") String versionId);

    ResourceBuilder selectByUploadId(@Param("upload_id") String uploadId);

    /**
     *
     * @param parentVersionId
     * @param continuation SubResource's version_id is used as continuation
     * token.
     * @return
     */
    List<ResourceBuilder> selectSubResourcesByParentVersionId(@Param("parent_version_id") String parentVersionId, @Param("continuation") String continuation);

    List<ResourceBuilder> selectSubResourcesByParentVersionId(@Param("parent_version_id") String parentVersionId);

    ResourceBuilder selectSubResourceByKeyEtagParent(@Param("resourceKey") String key, @Param("parent_version_id") String parentVersionId, @Param("etag") String etag);

    int removeSubResourcesByParent(@Param("parent_version_id") String parentVersionId);

    int insertResource(@Param("resourceDto") ResourceDto r);

    int insertSubResource(ResourceDto r);

    int update(ResourceDto r);

    int mergeResource(@Param("resourceDto") ResourceDto r);

    int mergeUploadingResource(@Param("resourceDto") ResourceDto r, @Param("uploadDto") MultipartUploadDto u);

    int delete(Resource r);

    int deleteByKeyhash(@Param("keyhash") String keyhash);

    int deleteByUploadId(@Param("upload_id") String uploadId);

}
