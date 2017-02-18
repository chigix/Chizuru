package com.chigix.resserver.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public abstract class Resource {

    private final String key;

    private final Map<String, String> metaData;

    private final String versionId;
    private DateTime lastModified = new DateTime(DateTimeZone.forID("GMT"));
    /**
     * The entity tag is an MD5 hash of the object. The ETag only reflects
     * changes to the contents of an object, not its metadata. Default as the
     * hash of empty string.
     */
    private String eTag = "d41d8cd98f00b204e9800998ecf8427e";

    private String size = "0";

    public Resource(String key) {
        this(key, UUID.randomUUID().toString());
    }

    public Resource(String key, String versionId) {
        this.key = key;
        this.metaData = new HashMap<>();
        this.versionId = versionId;
        metaData.put("Content-Type", "application/octet-stream");
    }

    public String getKey() {
        return key;
    }

    public String getVersionId() {
        return versionId;
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

    public abstract Bucket getBucket();

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
     * Empty the content in this resource.
     */
    public abstract void empty();
}
