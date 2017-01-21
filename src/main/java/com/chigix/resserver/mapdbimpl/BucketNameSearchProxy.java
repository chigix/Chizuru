package com.chigix.resserver.mapdbimpl;

import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.ModelProxy;
import com.chigix.resserver.entity.dao.BucketDao;
import com.chigix.resserver.entity.error.NoSuchBucket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class BucketNameSearchProxy implements ModelProxy<Bucket> {

    private Bucket proxied = null;

    private BucketDao bucketDao = null;

    private final String searchName;

    private final List<Callable<Boolean>> checkers;

    public BucketNameSearchProxy(String name) {
        this.checkers = new LinkedList<>();
        searchName = name;
    }

    public BucketNameSearchProxy(Bucket proxied) {
        this.checkers = new LinkedList<>();
        searchName = proxied.getName();
        this.proxied = proxied;
    }

    public void setProxied(Bucket proxied) {
        this.proxied = proxied;
    }

    /**
     * Just return the proxied object cached currently without querying any
     * database.
     *
     * @return
     */
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
        checkers.forEach((checker) -> {
            try {
                if (checker.call() == false) {
                    throw new ProxiedException(new NoSuchBucket(searchName));
                }
            } catch (Exception ex) {
                throw new ProxiedException(ex);
            }
        });
        return proxied;
    }

    public void addCheckers(Callable<Boolean> checkerFunction) {
        checkers.add(checkerFunction);
    }

}
