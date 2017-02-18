package com.chigix.resserver.mapdbimpl.dao;

import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.Chunk;
import com.chigix.resserver.entity.ChunkedResource;
import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.entity.dao.ResourceDao;
import com.chigix.resserver.entity.error.DaoException;
import com.chigix.resserver.entity.error.NoSuchBucket;
import com.chigix.resserver.entity.error.NoSuchKey;
import com.chigix.resserver.error.DBError;
import com.chigix.resserver.mapdbimpl.BucketInStorage;
import com.chigix.resserver.mapdbimpl.ResourceLinkNode;
import com.chigix.resserver.mapdbimpl.Serializer;
import com.chigix.resserver.mapdbimpl.entity.ResourceExtension;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import org.mapdb.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ResourceDaoImpl implements ResourceDao {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceDaoImpl.class.getName());

    private final DB db;

    private BucketDaoImpl bucketdao;

    private ChunkDaoImpl chunkdao;

    public ResourceDaoImpl(DB db) {
        this.db = db;
    }

    public void assembleDaos(BucketDaoImpl bucketdao, ChunkDaoImpl chunkdao) {
        this.bucketdao = bucketdao;
        this.chunkdao = chunkdao;
    }

    @Override
    public Resource findResource(Bucket b, String resourceKey) throws NoSuchKey, NoSuchBucket {
        if (resourceKey == null) {
            throw new IllegalArgumentException();
        }
        if (!(b instanceof BucketInStorage)) {
            b = bucketdao.findBucketByName(b.getName());
        }
        Resource result = findResourceByKeyHash(ResourceExtension.hashKey(((BucketInStorage) b).getUUID(), resourceKey));
        if (result == null) {
            throw new NoSuchKey(resourceKey);
        }
        return result;
    }

    public Resource findResourceByKeyHash(String resourceKeyHash) {
        if (resourceKeyHash == null) {
            throw new IllegalArgumentException();
        }
        String xml = ((ConcurrentMap<String, String>) db.hashMap(ResourceKeys.RESOURCE_DB).open()).get(resourceKeyHash);
        if (xml == null) {
            return null;
        }
        Resource result = Serializer.deserializeResource(xml);
        ResourceExtension ext = (ResourceExtension) result;
        ext.setResourceDao(this);
        ext.setChunkDao(chunkdao);
        BucketInStorage b;
        try {
            b = (BucketInStorage) bucketdao.findBucketByName(ext.getStoredBucketName());
        } catch (NoSuchBucket ex) {
            return null;
        }
        if (!ext.getStoredBucketUUID().equals(b.getUUID())) {
            return null;
        }
        ext.setBucket(b);
        return result;
    }

    public Resource saveResourceNoIndex(Resource resource) throws NoSuchBucket, DaoException {
        String key_hash;
        Bucket belonged_bucket;
        try {
            belonged_bucket = resource.getBucket();
        } catch (Exception e) {
            if (e.getCause() instanceof NoSuchBucket) {
                throw (NoSuchBucket) e.getCause();
            } else {
                throw e;
            }
        }
        if (!(belonged_bucket instanceof BucketInStorage)) {
            belonged_bucket = bucketdao.findBucketByName(belonged_bucket.getName());
        }
        if (resource instanceof ResourceExtension) {
            key_hash = ((ResourceExtension) resource).getKeyHash();
        } else if (resource.getBucket() instanceof BucketInStorage) {
            key_hash = ResourceExtension.hashKey(((BucketInStorage) belonged_bucket).getUUID(), resource.getKey());
        } else {
            throw new NoSuchBucket(belonged_bucket.getName());
        }
        String resource_xml = Serializer.serializeResource(resource);
        ((ConcurrentMap<String, String>) db.hashMap(ResourceKeys.RESOURCE_DB).open())
                .put(key_hash, resource_xml);
        ((ConcurrentMap<String, String>) db.hashMap(ResourceKeys.RESOURCE_DB_VERSION_INDEX).open())
                .putIfAbsent(resource.getVersionId(), key_hash);
        db.commit();
        Resource resource_to_return = Serializer.deserializeResource(resource_xml);
        ResourceExtension ext = (ResourceExtension) resource_to_return;
        ext.setResourceDao(this);
        ext.setBucket((BucketInStorage) belonged_bucket);
        return resource_to_return;
    }

    @Override
    public Resource saveResource(Resource resource) throws NoSuchBucket, DaoException {
        Resource r = saveResourceNoIndex(resource);
        int count = 0;
        BucketInStorage belonged_bucket = (BucketInStorage) r.getBucket();
        String key_hash = ((ResourceExtension) r).getKeyHash();
        while (!appendBucketResourceLink(belonged_bucket, r, key_hash)) {
            count++;
            if (count > 10) {
                throw new DaoException() {
                    @Override
                    public String getMessage() {
                        return MessageFormat.format("Resource [{0}] is trying to be added into bucket: {1}",
                                r.getKey(), key_hash);
                    }

                };
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
        return r;
    }

    private boolean appendBucketResourceLink(BucketInStorage belonged, Resource resource, String key_hash) {
        if (belonged == null) {
            throw new IllegalArgumentException("belonged is null");
        }
        final ConcurrentMap<String, String> RESOURCE_LINK_LOCK = (ConcurrentMap<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_LOCK_DB).open();
        final ConcurrentMap<String, String> RESOURCE_LINKs = (ConcurrentMap<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open();
        final ConcurrentMap<String, String> RESOURCE_LINK_START = (ConcurrentMap<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_START_DB).open();
        final ConcurrentMap<String, String> RESOURCE_LINK_END = (ConcurrentMap<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_END_DB).open();
        final String lock_uuid = UUID.randomUUID().toString();
        if (RESOURCE_LINK_LOCK.putIfAbsent(key_hash, lock_uuid) != null) {
            return false;
        }
        final ResourceLinkNode link_node = new ResourceLinkNode();
        String to_be_replaced_link_node_xml = Serializer.serializeResourceLinkNode(link_node);
        String saved_node = RESOURCE_LINKs.putIfAbsent(key_hash, to_be_replaced_link_node_xml);
        if (saved_node != null) {
            RESOURCE_LINK_LOCK.remove(key_hash, lock_uuid);
            return true;
        }
        final String previous_key_hash = RESOURCE_LINK_END.get(belonged.getUUID());
        ResourceLinkNode prev_node = null;
        String prev_node_xml;
        if (previous_key_hash != null) {
            if (RESOURCE_LINK_LOCK.putIfAbsent(previous_key_hash, lock_uuid) == null
                    && (prev_node_xml = RESOURCE_LINKs.get(previous_key_hash)) != null) {
                prev_node = Serializer.deserializeResourceLinkNode(prev_node_xml);
                db.commit();
            } else {
                RESOURCE_LINKs.remove(key_hash);
                RESOURCE_LINK_LOCK.remove(key_hash, lock_uuid);
                RESOURCE_LINK_LOCK.remove(previous_key_hash, lock_uuid);
                db.commit();
                return false;
            }
            link_node.setPreviousResourceKeyHash(previous_key_hash);
            if (!RESOURCE_LINKs.replace(key_hash, to_be_replaced_link_node_xml, Serializer.serializeResourceLinkNode(link_node))) {
                RESOURCE_LINKs.remove(key_hash);
                RESOURCE_LINK_LOCK.remove(key_hash, lock_uuid);
                RESOURCE_LINK_LOCK.remove(previous_key_hash, lock_uuid);
                db.commit();
                return false;
            }
            if (!RESOURCE_LINK_END.replace(belonged.getUUID(), previous_key_hash, key_hash)) {
                RESOURCE_LINK_LOCK.remove(key_hash, lock_uuid);
                RESOURCE_LINK_LOCK.remove(previous_key_hash, lock_uuid);
                RESOURCE_LINKs.remove(key_hash);
                db.commit();
                return false;
            }
            prev_node.setNextResourceKeyHash(key_hash);
            RESOURCE_LINKs.put(previous_key_hash, Serializer.serializeResourceLinkNode(prev_node));
            if (!RESOURCE_LINK_LOCK.remove(previous_key_hash, lock_uuid)) {
                throw new DBError("RESOURCE_LINK_LOCK has been removed without lock_uuid constraint.");
            }
        } else if (RESOURCE_LINK_END.putIfAbsent(belonged.getUUID(), key_hash) != null) {
            RESOURCE_LINKs.remove(key_hash);
            RESOURCE_LINK_LOCK.remove(key_hash, lock_uuid);
            db.commit();
            return false;
        }
        db.commit();
        if (prev_node == null) {
            RESOURCE_LINK_START.put(belonged.getUUID(), key_hash);
            db.commit();
            link_node.setPreviousResourceKeyHash(null);
        }
        link_node.setNextResourceKeyHash(null);
        RESOURCE_LINKs.put(key_hash, Serializer.serializeResourceLinkNode(link_node));
        RESOURCE_LINK_LOCK.remove(key_hash, lock_uuid);
        db.commit();
        return true;
    }

    private boolean removeFromBucketResourceLink(String bucket_uuid, final String key_hash) {
        final ConcurrentMap<String, String> RESOURCE_LINK_LOCK = (ConcurrentMap<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_LOCK_DB).open();
        final ConcurrentMap<String, String> RESOURCE_LINKs = (ConcurrentMap<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open();
        final ConcurrentMap<String, String> RESOURCE_LINK_START = (ConcurrentMap<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_START_DB).open();
        final ConcurrentMap<String, String> RESOURCE_LINK_END = (ConcurrentMap<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_END_DB).open();
        final String lock_uuid = UUID.randomUUID().toString();
        if (RESOURCE_LINK_LOCK.putIfAbsent(key_hash, lock_uuid) == null) {
            db.commit();
        } else {
            return false;
        }
        String link_node_xml = RESOURCE_LINKs.get(key_hash);
        if (link_node_xml == null) {
            RESOURCE_LINK_LOCK.remove(key_hash, lock_uuid);
            db.commit();
            return true;
        }
        ResourceLinkNode link_node = Serializer.deserializeResourceLinkNode(link_node_xml);
        ResourceLinkNode prev_node = null;
        String prev_node_xml;
        ResourceLinkNode next_node = null;
        String next_node_xml;
        if (link_node.getPreviousResourceKeyHash() != null) {
            if (RESOURCE_LINK_LOCK.putIfAbsent(link_node.getPreviousResourceKeyHash(), lock_uuid) == null) {
                db.commit();
            } else {
                RESOURCE_LINK_LOCK.remove(key_hash, lock_uuid);
                db.commit();
                return false;
            }
            prev_node_xml = RESOURCE_LINKs.get(link_node.getPreviousResourceKeyHash());
            if (prev_node_xml == null) {
                RESOURCE_LINK_LOCK.remove(key_hash, lock_uuid);
                RESOURCE_LINK_LOCK.remove(link_node.getPreviousResourceKeyHash(), lock_uuid);
                db.commit();
                return false;
            } else {
                prev_node = Serializer.deserializeResourceLinkNode(prev_node_xml);
            }
        }
        if (link_node.getNextResourceKeyHash() != null) {
            if (RESOURCE_LINK_LOCK.putIfAbsent(link_node.getNextResourceKeyHash(), lock_uuid) == null) {
                db.commit();
            } else {
                RESOURCE_LINK_LOCK.remove(key_hash, lock_uuid);
                if (link_node.getPreviousResourceKeyHash() != null) {
                    RESOURCE_LINK_LOCK.remove(link_node.getPreviousResourceKeyHash(), lock_uuid);
                }
                db.commit();
                return false;
            }
            next_node_xml = RESOURCE_LINKs.get(link_node.getNextResourceKeyHash());
            if (next_node_xml == null) {
                RESOURCE_LINK_LOCK.remove(key_hash, lock_uuid);
                if (link_node.getPreviousResourceKeyHash() != null) {
                    RESOURCE_LINK_LOCK.remove(link_node.getPreviousResourceKeyHash(), lock_uuid);
                }
                RESOURCE_LINK_LOCK.remove(link_node.getNextResourceKeyHash(), lock_uuid);
                db.commit();
                return false;
            } else {
                next_node = Serializer.deserializeResourceLinkNode(next_node_xml);
            }
        }
        if (prev_node != null) {
            prev_node.setNextResourceKeyHash(link_node.getNextResourceKeyHash());
            RESOURCE_LINKs.put(link_node.getPreviousResourceKeyHash(), Serializer.serializeResourceLinkNode(prev_node));
        }
        if (next_node != null) {
            next_node.setPreviousResourceKeyHash(link_node.getPreviousResourceKeyHash());
            RESOURCE_LINKs.put(link_node.getNextResourceKeyHash(), Serializer.serializeResourceLinkNode(next_node));
        }
        if (prev_node == null && next_node != null) {
            RESOURCE_LINK_START.put(bucket_uuid, link_node.getNextResourceKeyHash());
        } else if (prev_node != null && next_node == null) {
            RESOURCE_LINK_END.put(bucket_uuid, link_node.getPreviousResourceKeyHash());
        } else if (prev_node == null && next_node == null) {
            RESOURCE_LINK_START.remove(bucket_uuid);
            RESOURCE_LINK_END.remove(bucket_uuid);
        }
        RESOURCE_LINKs.remove(key_hash);
        db.commit();
        RESOURCE_LINK_LOCK.remove(key_hash, lock_uuid);
        if (prev_node != null) {
            RESOURCE_LINK_LOCK.remove(link_node.getPreviousResourceKeyHash(), lock_uuid);
        }
        if (next_node != null) {
            RESOURCE_LINK_LOCK.remove(link_node.getNextResourceKeyHash(), lock_uuid);
        }
        return true;
    }

    public void appendChunk(final ChunkedResource resource, Chunk chunk) {
        ConcurrentMap<String, String> chunk_list = (ConcurrentMap<String, String>) db.hashMap(ResourceKeys.CHUNK_LIST_DB).open();
        String key_count = resource.getVersionId() + "__count";
        BigInteger chunk_number = BigInteger.ZERO;
        String prev_count = chunk_list.putIfAbsent(key_count, chunk_number.toString(32));
        if (prev_count != null) {
            chunk_number = new BigInteger(prev_count, 32);
            while (true) {
                chunk_number = chunk_number.add(BigInteger.ONE);
                if (chunk_list.replace(key_count, prev_count, chunk_number.toString(32))) {
                    break;
                }
                prev_count = chunk_list.get(key_count);
            }
        }
        chunk_list.put(resource.getVersionId() + "_" + chunk_number.toString(32), chunk.getContentHash());
        resource.setSize(new BigInteger(resource.getSize()).add(new BigInteger(chunk.getSize() + "")).toString());
        db.commit();
    }

    public String findChunkNode(final ChunkedResource resource, String number) {
        BigInteger chunk_number = new BigInteger((String) db.hashMap(ResourceKeys.CHUNK_LIST_DB).open().get(resource.getVersionId() + "__count"), 32);
        if (new BigInteger(number).compareTo(chunk_number) > 0) {
            return null;
        }
        return (String) db.hashMap(ResourceKeys.CHUNK_LIST_DB).open().get(resource.getVersionId() + "_" + number);
    }

    public void emptyResourceChunkNode(final ChunkedResource resource) {
        db.hashMap(ResourceKeys.CHUNK_LIST_DB).open().remove(resource.getVersionId() + "_0");
        db.hashMap(ResourceKeys.CHUNK_LIST_DB).open().remove(resource.getVersionId() + "__count");
        db.commit();
    }

    public Resource getBucketFirstResource(String bucket_uuid) {
        String resource_keyhash = ((ConcurrentMap<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_START_DB).open()).get(bucket_uuid);
        if (resource_keyhash == null) {
            return null;
        }
        return findResourceByKeyHash(resource_keyhash);
    }

    private ResourceLinkNode getResourceLinkNode(ResourceExtension resource) throws ResourceKeys.ResourceNotIndexed {
        String xml = ((ConcurrentMap<String, String>) db.hashMap(ResourceKeys.RESOURCE_LINK_DB).open()).get(resource.getKeyHash());
        if (xml == null) {
            throw new ResourceKeys.ResourceNotIndexed((Resource) resource);
        }
        ResourceLinkNode linknode = Serializer.deserializeResourceLinkNode(xml);
        linknode.setResource((Resource) resource);
        return linknode;
    }

    @Override
    public Iterator<Resource> listResources(Bucket bucket) throws NoSuchBucket {
        final BucketInStorage real_bucket;
        if (bucket instanceof BucketInStorage) {
            real_bucket = (BucketInStorage) bucket;
        } else {
            real_bucket = (BucketInStorage) bucketdao.findBucketByName(bucket.getName());
        }
        Resource head = getBucketFirstResource(real_bucket.getUUID());
        if (head == null) {
            return new Iterator<Resource>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Resource next() {
                    throw new NoSuchElementException();
                }
            };
        }
        try {
            return new ResourceLinkIterator((ResourceExtension) head);
        } catch (ResourceKeys.ResourceNotIndexed ex) {
            LOG.error(MessageFormat.format(""
                    + "There is Problem about the start Resource in the linked list storage. "
                    + "START RESOURCE: [{0}#{1}]",
                    head.getKey(), ((ResourceExtension) head).getKeyHash()), ex);
            return new Iterator<Resource>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Resource next() {
                    throw new NoSuchElementException();
                }
            };
        }
    }

    @Override
    public Iterator<Resource> listResources(Bucket bucket, String continuation) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeResource(Resource resource) throws NoSuchBucket {
        final String key_hash;
        Bucket belonged_bucket;
        try {
            belonged_bucket = resource.getBucket();
        } catch (Exception e) {
            if (e.getCause() instanceof NoSuchBucket) {
                throw (NoSuchBucket) e.getCause();
            } else {
                throw e;
            }
        }
        if (!(belonged_bucket instanceof BucketInStorage)) {
            belonged_bucket = bucketdao.findBucketByName(belonged_bucket.getName());
        }
        if (resource instanceof ResourceExtension) {
            key_hash = ((ResourceExtension) resource).getKeyHash();
        } else if (resource.getBucket() instanceof BucketInStorage) {
            key_hash = ResourceExtension.hashKey(((BucketInStorage) belonged_bucket).getUUID(), resource.getKey());
        } else {
            throw new NoSuchBucket(belonged_bucket.getName());
        }
        emptyResourceChunkNode((ChunkedResource) resource);
        ((ConcurrentMap<String, String>) db.hashMap(ResourceKeys.RESOURCE_DB).open()).remove(key_hash);
        db.commit();
        int count = 0;
        while (!removeFromBucketResourceLink(((BucketInStorage) belonged_bucket).getUUID(), key_hash)) {
            count++;
            if (count > 10) {
                LOG.warn("Resource [{}] is trying to be removed times: {}", resource.getKey(), count);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
        }
    }

    @Override
    public Resource findResource(String bucketName, String resourceKey) throws NoSuchKey, NoSuchBucket {
        return findResource(bucketdao.findBucketByName(bucketName), resourceKey);
    }

    private class ResourceLinkIterator implements Iterator<Resource> {

        private ResourceLinkNode next;

        private ResourceLinkNode current;

        private boolean isRemoved = false;

        public ResourceLinkIterator(ResourceExtension start) throws ResourceKeys.ResourceNotIndexed {
            next = getResourceLinkNode(start);
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Resource next() {
            current = next;
            if (current == null) {
                throw new NoSuchElementException();
            }
            isRemoved = false;
            next = null;
            if (current.getNextResourceKeyHash() != null) {
                Resource nextResource = findResourceByKeyHash(current.getNextResourceKeyHash());
                try {
                    next = getResourceLinkNode((ResourceExtension) nextResource);
                } catch (ResourceKeys.ResourceNotIndexed ex) {
                    LOG.error(ex.getMessage(), ex);
                    next = null;
                }
            }
            return current.getResource();
        }

        @Override
        public void remove() {
            isRemoved = true;
            try {
                removeResource(current.getResource());
            } catch (NoSuchBucket ex) {
                throw new IllegalStateException(ex);
            }
        }

    }

}
