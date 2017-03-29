package com.chigix.resserver.mybatis;

import com.chigix.resserver.mybatis.dto.MultipartUploadDto;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface MultipartUploadMapper {

    int insert(MultipartUploadDto upload);

    int deleteByUuid(@Param("uuid") String uuid);

    Map<String, String> selectByUuid(@Param("uuid") String uuid);

    List<Map<String, String>> selectAllByBucketUuid(@Param("bucket_uuid") String bucketUuid);

}
