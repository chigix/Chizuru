package com.chigix.resserver.mybatis;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface BucketMapper {

    List<Map<String, String>> selectAll();

    List<Map<String, String>> selectAll(@Param("limit") int limit);

    List<Map<String, String>> selectAll(@Param("continuation") String continuation);

    List<Map<String, String>> selectAll(@Param("continuation") String continuation, @Param("limit") int limit);

    Map<String, String> selectByName(@Param("name") String name);

    Map<String, String> selectByUUID(@Param("uuid") String uuid);

    int insert(@Param("uuid") String uuid, @Param("name") String name, @Param("created_at") String created_at);

    int deleteByName(@Param("name") String name);

    int deleteByUuid(@Param("uuid") String uuid);

}
