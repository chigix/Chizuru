package com.chigix.resserver.mapdbimpl.dao;

import com.chigix.resserver.mapdbimpl.SerializerBucket;
import org.mapdb.DB;
import org.mapdb.Serializer;

/**
 * Primary Key is performed by bucket name.
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class BucketKeys {

    public static final String BUCKET_DB = "BUCKET_DB:[String]bucket_name:[Object]Bucket";

    public static void updateDBScheme(DB db) {
        if (!db.exists(BUCKET_DB)) {
            db.hashMap(BUCKET_DB, Serializer.STRING_ASCII, new SerializerBucket()).create();
        }
        db.commit();
    }

}
