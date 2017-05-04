package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.Chunk;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface ChunkMapper {

    int putChunkToVersion(@Param("versionId") String versionId, @Param("chunk") Chunk c, @Param("chunkIndex") int chunkIndex);

    Map<String, String> selectFirstChunkReference(@Param("contentHash") String contentHash);

    List<Map<String, String>> selectAll();

    List<Map<String, String>> selectByVersion(@Param("versionId") String version_id);

    /**
     *
     * @param version_id
     * @param continuation Content Hash is used as the continuation token to get
     * following list.
     * @return
     */
    List<Map<String, String>> selectByVersion(@Param("versionId") String version_id, @Param("continuation") String continuation);
}
