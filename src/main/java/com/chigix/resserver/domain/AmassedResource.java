package com.chigix.resserver.domain;

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

    public abstract Iterator<ChunkedResource> getSubResources();

}
