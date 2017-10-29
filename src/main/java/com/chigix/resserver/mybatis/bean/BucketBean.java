package com.chigix.resserver.mybatis.bean;

import com.chigix.resserver.domain.Bucket;
import com.chigix.resserver.domain.Lifecycle;
import com.chigix.resserver.mybatis.EntityManagerImpl;
import java.util.UUID;
import org.joda.time.DateTime;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class BucketBean extends Bucket implements BeanExt {

    private Lifecycle status = Lifecycle.MANAGED;

    private final String Uuid;

    public BucketBean(String name, DateTime creationTime, String uuid) {
        super(name, creationTime);
        Uuid = uuid;
    }

    public BucketBean(String name, DateTime creationTime) {
        super(name, creationTime);
        Uuid = UUID.randomUUID().toString().replace("-", "");
    }

    public BucketBean(String name) {
        super(name);
        Uuid = UUID.randomUUID().toString().replace("-", "");
    }

    public String getUuid() {
        return Uuid;
    }

    @Override
    public Lifecycle getEntityStatus(EntityManagerImpl em) {
        return status;
    }

    public BucketBean setEntityStatus(Lifecycle status) {
        this.status = status;
        return this;
    }

}
