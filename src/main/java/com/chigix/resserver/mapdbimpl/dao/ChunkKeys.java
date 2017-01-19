package com.chigix.resserver.mapdbimpl.dao;

import com.chigix.resserver.mapdbimpl.SerializerChunk;
import org.mapdb.DB;
import org.mapdb.Serializer;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ChunkKeys {

    public static final String REF_COUNT = "CHUNK_DB:[String]content_hash:[Integer]REF_COUNT";

    public static final String CHUNK_DB = "CHUNK_DB:[String]content_hash:[Object]Chunk";

    public static void updateDBScheme(DB db) {
        if (!db.exists(REF_COUNT)) {
            db.hashMap(REF_COUNT, Serializer.STRING_ASCII, Serializer.INTEGER).create();
        }
        if (!db.exists(CHUNK_DB)) {
            db.hashMap(CHUNK_DB, Serializer.STRING_ASCII, SerializerChunk.DEFAULT).create();
        }
    }

}
