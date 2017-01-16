package com.chigix.resserver.mapdbimpl;

import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.ModelProxy;
import com.chigix.resserver.entity.dao.BucketDao;
import com.chigix.resserver.entity.error.NoSuchBucket;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class BucketNameSearchProxy implements ModelProxy<Bucket> {

    private Bucket proxied = null;

    private BucketDao bucketDao = null;

    private final String searchName;

    public BucketNameSearchProxy(String name) {
        searchName = name;
    }

    public BucketNameSearchProxy(Bucket proxied) {
        searchName = proxied.getName();
        this.proxied = proxied;
    }

    public void setProxied(Bucket proxied) {
        this.proxied = proxied;
    }

    public Bucket getProxied() {
        return proxied;
    }

    public void resetProxy() {
        proxied = null;
    }

    public void setBucketDao(BucketDao bucketDao) {
        this.bucketDao = bucketDao;
    }

    @Override
    public Bucket getProxiedModel() throws ProxiedException {
        if (proxied == null) {
            if (bucketDao == null) {
                throw new ProxiedException(new NullPointerException("BucketDao is not set in BucketNameSearchProxy"));
            }
            try {
                proxied = bucketDao.findBucketByName(searchName);
            } catch (NoSuchBucket ex) {
                throw new ProxiedException(ex);
            }
        }
        return proxied;
    }

}
