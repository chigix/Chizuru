package com.chigix.resserver.mapdbimpl;

import com.chigix.resserver.entity.Resource;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ResourceLinkNode {

    private Resource resource;
    private String nextResourceKeyHash;
    private String previousResourceKeyHash;

    public Resource getResource() {
        return resource;
    }

    public String getNextResourceKeyHash() {
        return nextResourceKeyHash;
    }

    public String getPreviousResourceKeyHash() {
        return previousResourceKeyHash;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void setNextResourceKeyHash(String nextResourceKeyHash) {
        this.nextResourceKeyHash = nextResourceKeyHash;
    }

    public void setPreviousResourceKeyHash(String previousResourceKeyHash) {
        this.previousResourceKeyHash = previousResourceKeyHash;
    }

}
