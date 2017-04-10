package com.chigix.resserver.mybatis;

import com.chigix.resserver.domain.Bucket;
import com.chigix.resserver.domain.dao.BucketDao;
import com.chigix.resserver.domain.error.BucketAlreadyExists;
import com.chigix.resserver.domain.error.BucketAlreadyOwnedByYou;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.mybatis.bean.BucketBean;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import org.apache.ibatis.exceptions.PersistenceException;
import org.joda.time.DateTime;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class BucketDaoImpl implements BucketDao {

    private final BucketMapper bucketMapper;

    public static final Function<Map<String, String>, BucketBean> TO_BUCKET = (Map<String, String> t) -> {
        BucketBean result = new BucketBean(t.get("NAME"), DateTime.parse(t.get("CREATION_TIME")), t.get("UUID"));
        return result;
    };

    public BucketDaoImpl(BucketMapper dbMapper) {
        this.bucketMapper = dbMapper;
    }

    @Override
    public Bucket findBucketByName(String name) throws NoSuchBucket {
        Map<String, String> result = bucketMapper.selectByName(name);
        if (result == null) {
            throw new NoSuchBucket(name);
        }
        return TO_BUCKET.apply(result);
    }

    public BucketBean findBucketByUuid(String uuid) {
        Map<String, String> result = bucketMapper.selectByUUID(uuid);
        if (result == null) {
            return null;
        }
        return TO_BUCKET.apply(result);
    }

    @Override
    public Iterator<Bucket> iteratorBucket() {
        final Iterator<Map<String, String>> rows = bucketMapper.selectAll(1000).iterator();
        return new Iterator<Bucket>() {
            @Override
            public boolean hasNext() {
                return rows.hasNext();
            }

            @Override
            public Bucket next() {
                return TO_BUCKET.apply(rows.next());
            }
        };
    }

    @Override
    public BucketBean createBucket(String name) throws BucketAlreadyExists {
        BucketBean result = new BucketBean(name);
        try {
            if (bucketMapper.insert(result.getUuid(), result.getName(), result.getCreationTime().toString()) == 1) {
                return result;
            }
            throw new BucketAlreadyOwnedByYou();
        } catch (PersistenceException e) {
            throw new BucketAlreadyOwnedByYou();
        }
    }

    @Override
    public Bucket deleteBucketByName(String name) throws NoSuchBucket {
        Bucket b = findBucketByName(name);
        bucketMapper.deleteByName(name);
        return b;
    }

}
