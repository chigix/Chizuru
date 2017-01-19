package com.chigix.resserver.mapdbimpl.dao;

import org.mapdb.DB;
import org.mapdb.Serializer;

/**
 * Primary Key is performed by bucket name.
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class BucketKeys {

    public static final String CREATED_VALUE = "BUCKET_DB:[String]bucket_name:[String]creation_time_ISO8601";

    public static void updateDBScheme(DB db) {
        if (!db.exists(CREATED_VALUE)) {
            db.hashMap(CREATED_VALUE, Serializer.STRING_ASCII, Serializer.STRING_ASCII).create();
        }
        db.commit();
    }

}
