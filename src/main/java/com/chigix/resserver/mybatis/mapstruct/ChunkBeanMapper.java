package com.chigix.resserver.mybatis.mapstruct;

import com.chigix.resserver.domain.model.chunk.Chunk;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.chigix.resserver.domain.model.chunk.ChunkRepository;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@Mapper(componentModel = "spring")
public abstract class ChunkBeanMapper {

    @Autowired
    private ChunkRepository chunkRepository;

    public Chunk fromRecord(com.chigix.resserver.mybatis.record.Chunk record) {
        return chunkRepository.newChunk(record.getContentHash(), record.getSize());
    }

    @Mappings({
        @Mapping(target = "id", ignore = true)
        ,@Mapping(target = "parentVersionId", ignore = true)
        ,@Mapping(target = "indexInParent", ignore = true)
    })
    abstract public com.chigix.resserver.mybatis.record.Chunk toRecord(Chunk chunk);
}
