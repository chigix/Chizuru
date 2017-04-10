package com.chigix.resserver.endpoint.DeleteResource;

import com.chigix.resserver.domain.Bucket;
import com.chigix.resserver.domain.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Context {

    private final List<Resource> toDeleteResources = new ArrayList<>();

    private Bucket bucket;

    private StringBuffer sb = new StringBuffer();

    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }

    public Bucket getBucket() {
        return bucket;
    }

    public void appendXml(String part) {
        sb.append(part);
    }

    public String getXml() {
        return sb.toString();
    }

    public void addResourceToDelete(Resource r) {
        toDeleteResources.add(r);
    }

    public List<Resource> getToDeleteResources() {
        return toDeleteResources;
    }

}
