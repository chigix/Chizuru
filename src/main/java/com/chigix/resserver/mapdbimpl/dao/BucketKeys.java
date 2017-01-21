package com.chigix.resserver.mapdbimpl.dao;

import org.mapdb.DB;
import org.mapdb.Serializer;

/**
 * Primary Key is performed by bucket name.
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class BucketKeys {

    public static final String BUCKET_DB = "BUCKET_DB:[String]bucket_name:[Xml]Bucket";

    public static void updateDBScheme(DB db) {
        if (!db.exists(BUCKET_DB)) {
            db.hashMap(BUCKET_DB, Serializer.STRING_ASCII, Serializer.STRING).create();
        }
        db.commit();
    }

}
