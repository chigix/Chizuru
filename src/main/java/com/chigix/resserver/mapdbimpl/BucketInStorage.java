package com.chigix.resserver.mapdbimpl;

import com.chigix.resserver.entity.Bucket;
import org.joda.time.DateTime;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class BucketInStorage extends Bucket {

    private String UUID;

    public BucketInStorage(String name, DateTime creationTime) {
        super(name, creationTime);
    }

    public BucketInStorage(String name) {
        super(name);
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getUUID() {
        return UUID;
    }

}
