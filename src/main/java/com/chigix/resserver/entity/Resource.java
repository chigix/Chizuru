package com.chigix.resserver.entity;

import java.util.HashMap;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Resource {

    private final String key;

    private final Map<String, String> metaData;

    public Resource(String key) {
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

}