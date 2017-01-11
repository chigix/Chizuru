package com.chigix.resserver.entity;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Bucket {

    private final String name;

    private final DateTime creationTime;

    public Bucket(String name, DateTime creationTime) {
        this.name = name;
        this.creationTime = creationTime;
    }

    public Bucket(String name) {
        this(name, new DateTime(DateTimeZone.forID("GMT")));
    }

    public DateTime getCreationTime() {
        return creationTime;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Bucket) {
            return ((Bucket) obj).getName().equals(getName());
        }
        return false;
    }

}
