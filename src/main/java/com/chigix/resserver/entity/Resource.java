package com.chigix.resserver.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Resource {

    private final String key;

    private final ModelProxy<Bucket> bucket;

    private final Map<String, String> metaData;

    public Resource(final Bucket bucket, String key) {
        this(() -> bucket, key);
    }

    public Resource(ModelProxy<Bucket> bucket, String key) {
        this.bucket = bucket;
        this.key = key;
        metaData = new HashMap<>();
        metaData.put("Content-Type", "application/octet-stream");
    }

    private DateTime lastModified = new DateTime(DateTimeZone.forID("GMT"));

    /**
     * The entity tag is an MD5 hash of the object. The ETag only reflects
     * changes to the contents of an object, not its metadata. Default as the
     * hash of empty string.
     */
    private String eTag = "d41d8cd98f00b204e9800998ecf8427e";

    private String size = "0";

    public String getKey() {
        return key;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public DateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(DateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getETag() {
        return eTag;
    }

    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    public String getStorageClass() {
        return "STANDARD";
    }

    public Bucket getBucket() {
        return bucket.getProxiedModel();
    }

    public void setMetaData(String key, String value) {
        metaData.put(key, value);
    }

    public void removeMetaData(String key) {
        metaData.remove(key);
    }

    public Map<String, String> snapshotMetaData() {
        Map<String, String> result = new HashMap<>();
        metaData.entrySet().forEach((entry) -> {
            result.put(entry.getKey(), entry.getValue());
        });
        return result;
    }

    /**
     * It depends on Dao implementation. Defaultly return empty chunks list for
     * the manually created Resource object.
     *
     * @return
     */
    public Iterator<Chunk> getChunks() {
        return new Iterator<Chunk>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Chunk next() {
                throw new NoSuchElementException();
            }
        };
    }

    /**
     * Empty the content in this resource.
     */
    public void empty() {
    }

}
