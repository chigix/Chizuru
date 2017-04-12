package com.chigix.resserver.endpoint.DeleteResource;

import com.chigix.resserver.domain.Bucket;
import com.chigix.resserver.domain.Resource;
import io.netty.handler.codec.http.router.HttpRouted;
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

    private final HttpRouted routedInfo;

    public Context(HttpRouted routedInfo) {
        this.routedInfo = routedInfo;
    }

    public HttpRouted getRoutedInfo() {
        return routedInfo;
    }

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
