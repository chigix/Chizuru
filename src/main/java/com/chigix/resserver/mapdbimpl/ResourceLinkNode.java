package com.chigix.resserver.mapdbimpl;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ResourceLinkNode {

    private ResourceInStorage resource;
    private String nextResourceKeyHash;
    private String previousResourceKeyHash;

    public ResourceInStorage getResource() {
        return resource;
    }

    public String getNextResourceKeyHash() {
        return nextResourceKeyHash;
    }

    public String getPreviousResourceKeyHash() {
        return previousResourceKeyHash;
    }

    public void setResource(ResourceInStorage resource) {
        this.resource = resource;
    }

    public void setNextResourceKeyHash(String nextResourceKeyHash) {
        this.nextResourceKeyHash = nextResourceKeyHash;
    }

    public void setPreviousResourceKeyHash(String previousResourceKeyHash) {
        this.previousResourceKeyHash = previousResourceKeyHash;
    }

}
