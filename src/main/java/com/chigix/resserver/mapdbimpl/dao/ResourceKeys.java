package com.chigix.resserver.mapdbimpl.dao;

import com.chigix.resserver.entity.Resource;
import org.mapdb.DB;
import org.mapdb.Serializer;

/**
 * Primary key is performed by Resource key hash.
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ResourceKeys {

    public static final String RESOURCE_DB = "RESOURCE_DB:[String]resource_key_hash:[Xml]ResourceInStorage";
    public static final String CHUNK_LIST_DB = "RESOURCE_CHUNK_DB:[String]resource_chunk_no:[String]chunk_content_hash";
    public static final String RESOURCE_LINK_LOCK_DB = "RESOURCE_LINK_LOCK_DB:[String]resource_key_hash:[String]lock_uuid";
    public static final String RESOURCE_LINK_DB = "RESOURCE_LINK_DB:[String]resource_key_hash:[Xml]ResourceLinkNode";
    public static final String RESOURCE_LINK_START_DB = "RESOURCE_LINK_START_DB:[String]bucket_UUID:[String]resource_key_hash";
    public static final String RESOURCE_LINK_END_DB = "RESOURCE_LINK_END_DB:[String]bucket_UUID:[String]resource_key_hash";

    public static void updateDBScheme(DB db) {
        if (!db.exists(RESOURCE_DB)) {
            db.hashMap(RESOURCE_DB, Serializer.STRING_ASCII, Serializer.STRING).create();
        }
        if (!db.exists(CHUNK_LIST_DB)) {
            db.hashMap(CHUNK_LIST_DB, Serializer.STRING_ASCII, Serializer.STRING_ASCII).create();
        }
        if (!db.exists(RESOURCE_LINK_DB)) {
            db.hashMap(RESOURCE_LINK_DB, Serializer.STRING_ASCII, Serializer.STRING_ASCII).create();
        }
        if (!db.exists(RESOURCE_LINK_START_DB)) {
            db.hashMap(RESOURCE_LINK_START_DB, Serializer.STRING_ASCII, Serializer.STRING_ASCII).create();
        }
        if (!db.exists(RESOURCE_LINK_END_DB)) {
            db.hashMap(RESOURCE_LINK_END_DB, Serializer.STRING_ASCII, Serializer.STRING_ASCII).create();
        }
        db.hashMap(RESOURCE_LINK_LOCK_DB, Serializer.STRING_ASCII, Serializer.STRING_ASCII).createOrOpen().clear();
    }

    public static class ResourceNotIndexed extends Exception {

        public ResourceNotIndexed(Resource resource) {
            super("Resource[" + resource.getKey() + "] is not existed in index list.");
        }

    }

}
