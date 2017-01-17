package com.chigix.resserver.mapdbimpl.dao;

import org.mapdb.DB;
import org.mapdb.Serializer;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ChunkKeys {

    public static final String REF_COUNT = "CHUNK_DB:content_hash:REF_COUNT";

    public static void updateDBScheme(DB db) {
        if (!db.exists(REF_COUNT)) {
            db.hashMap(REF_COUNT, Serializer.STRING_ASCII, Serializer.INTEGER).create();
        }
    }

}
