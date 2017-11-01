package com.chigix.resserver.mybatis.mapstruct;

import com.chigix.resserver.domain.model.chunk.Chunk;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.chigix.resserver.domain.model.chunk.ChunkRepository;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@Mapper(componentModel = "spring")
public abstract class ChunkBeanMapper {
    
    @Autowired
    private ChunkRepository chunkRepository;
    
    public Chunk fromRecord(com.chigix.resserver.mybatis.record.Chunk record){
        return chunkRepository.newChunk(record.getContentHash(), record.getSize());
    }
    
    public com.chigix.resserver.mybatis.record.Chunk toRecord(Chunk chunk){
        com.chigix.resserver.mybatis.record.Chunk record = new com.chigix.resserver.mybatis.record.Chunk();
        record.setContentHash(chunk.getContentHash());
        record.setLocationId(chunk.getLocationId());
        record.setSize(chunk.getSize());
        return record;
    }
}
