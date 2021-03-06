package com.chigix.resserver.domain.model.resource;

import java.util.Iterator;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public abstract class AmassedResource extends Resource {

    public AmassedResource(String key) {
        super(key);
    }

    public AmassedResource(String key, String versionId) {
        super(key, versionId);
    }

    public abstract <T extends ChunkedResource> Iterator<T> getSubResources();

}
