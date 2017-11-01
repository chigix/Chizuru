package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.model.bucket.Bucket;
import com.chigix.resserver.domain.Lifecycle;
import com.chigix.resserver.domain.error.BucketAlreadyExists;
import com.chigix.resserver.domain.error.BucketAlreadyOwnedByYou;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.dao.BucketMapper;
import java.util.Iterator;
import com.chigix.resserver.mybatis.mapstruct.BucketBeanMapper;
import com.chigix.resserver.mybatis.record.BucketExample;
import com.chigix.resserver.mybatis.record.Util;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class BucketRepositoryImpl implements BucketRepositoryExtend {

    private final BucketMapper bucketMapper;

    @Autowired
    private BucketBeanMapper bucketBeanMapper;

    public BucketRepositoryImpl(BucketMapper dbMapper) {
        this.bucketMapper = dbMapper;
    }

    @Transactional("transactionManager_Chizuru")
    @Override
    public BucketBean findBucketByName(String name) throws NoSuchBucket {
        BucketExample ex_by_name = new BucketExample();
        ex_by_name.createCriteria().andNameEqualTo(name);
        List<com.chigix.resserver.mybatis.record.Bucket> records
                = bucketMapper.selectByExampleWithRowbounds(ex_by_name, Util.ONE_ROWBOUND);
        if (records.size() < 1) {
            throw new NoSuchBucket(name);
        }
        return bucketBeanMapper.fromRecord(records.get(0));
    }

    @Transactional("transactionManager_Chizuru")
    @Override
    public Iterator<Bucket> iteratorBucket() {
        final AtomicInteger continuation_id = new AtomicInteger(0);
        return new IteratorConcater<Bucket>() {
            @Override
            protected Iterator<Bucket> nextIterator() {
                final BucketExample example = new BucketExample();
                example.createCriteria().andIdGreaterThan(continuation_id.get());
                final Iterator<com.chigix.resserver.mybatis.record.Bucket> records
                        = bucketMapper.selectByExampleWithRowbounds(
                                example, new RowBounds(0, 1000)
                        ).iterator();
                return new Iterator<Bucket>() {
                    @Override
                    public boolean hasNext() {
                        return records.hasNext();
                    }

                    @Override
                    public Bucket next() {
                        com.chigix.resserver.mybatis.record.Bucket record = records.next();
                        continuation_id.set(record.getId());
                        return bucketBeanMapper.fromRecord(record);
                    }
                };
            }
        };
    }

    @Transactional("transactionManager_Chizuru")
    @Override
    public BucketBean createBucket(String name) throws BucketAlreadyExists {
        BucketBean result = new BucketBean(name);
        try {
            if (bucketMapper.insert(bucketBeanMapper.toRecord(result)) == 1) {
                return result.setEntityStatus(Lifecycle.MANAGED);
            }
            throw new BucketAlreadyOwnedByYou();
        } catch (DuplicateKeyException e) {
            throw new BucketAlreadyOwnedByYou();
        }
    }

    @Transactional("transactionManager_Chizuru")
    @Override
    public Bucket deleteBucketByName(String name) throws NoSuchBucket {
        BucketBean b = findBucketByName(name);
        BucketExample delete_example = new BucketExample();
        delete_example.createCriteria().andUuidEqualTo(b.getUuid());
        bucketMapper.deleteByExample(delete_example);
        return b;
    }

}
