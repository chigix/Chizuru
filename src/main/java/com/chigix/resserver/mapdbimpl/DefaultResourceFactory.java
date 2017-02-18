package com.chigix.resserver.mapdbimpl;

import com.chigix.resserver.entity.AmassedResource;
import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.ChunkedResource;
import com.chigix.resserver.entity.factory.ResourceFactory;
import com.chigix.resserver.mapdbimpl.dao.ChunkDaoImpl;
import com.chigix.resserver.mapdbimpl.dao.ResourceDaoImpl;
import com.chigix.resserver.mapdbimpl.entity.ResourceExtension;
import java.security.InvalidParameterException;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class DefaultResourceFactory implements ResourceFactory {

    private final ResourceDaoImpl resourcedao;

    private final ChunkDaoImpl chunkdao;

    public DefaultResourceFactory(ResourceDaoImpl resourcedao, ChunkDaoImpl chunkdao) {
        this.resourcedao = resourcedao;
        this.chunkdao = chunkdao;
    }

    @Override
    public ChunkedResource createChunkedResource(String key, String version_id, Bucket bucket) {
        if (!(bucket instanceof BucketInStorage)) {
            throw new InvalidParameterException();
        }
        BucketInStorage b = (BucketInStorage) bucket;
        com.chigix.resserver.mapdbimpl.entity.ChunkedResource result
                = version_id == null ? new com.chigix.resserver.mapdbimpl.entity.ChunkedResource(key,
                                ResourceExtension.hashKey(b.getUUID(), key),
                                b.getUUID(),
                                bucket.getName())
                        : new com.chigix.resserver.mapdbimpl.entity.ChunkedResource(key,
                                ResourceExtension.hashKey(b.getUUID(), key),
                                version_id, b.getUUID(),
                                bucket.getName());
        result.setBucket(b);
        result.setResourceDao(resourcedao);
        return result;
    }

    @Override
    public ChunkedResource createChunkedResource(String key, Bucket bucket) {
        return createChunkedResource(key, null, bucket);
    }

    @Override
    public AmassedResource createAmassedResource(String key, String version_id, Bucket bucket) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AmassedResource createAmassedResource(String key, Bucket bucket) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
