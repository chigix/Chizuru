package com.chigix.resserver.entity.factory;

import com.chigix.resserver.entity.AmassedResource;
import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.ChunkedResource;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface ResourceFactory {

    ChunkedResource createChunkedResource(String key, String version_id, Bucket bucket);

    ChunkedResource createChunkedResource(String key, Bucket bucket);

    AmassedResource createAmassedResource(String key, String version_id, Bucket bucket);

    AmassedResource createAmassedResource(String key, Bucket bucket);
}
