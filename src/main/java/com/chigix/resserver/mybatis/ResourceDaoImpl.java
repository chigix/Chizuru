package com.chigix.resserver.mybatis;

import com.chigix.resserver.entity.AmassedResource;
import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.Chunk;
import com.chigix.resserver.entity.ChunkedResource;
import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.entity.dao.ResourceDao;
import com.chigix.resserver.entity.error.NoSuchBucket;
import com.chigix.resserver.entity.error.NoSuchKey;
import com.chigix.resserver.mybatis.bean.AmassedResourceBean;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import com.chigix.resserver.mybatis.bean.ResourceExtension;
import com.chigix.resserver.mybatis.dto.ResourceDto;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ResourceDaoImpl implements ResourceDao {

    private final ResourceMapper resourceMapper;

    private final ChunkMapper chunkMapper;

    private BucketDaoImpl bucketDao;

    private ChunkDaoImpl chunkDao;

    private static final Function<ConverterSpecification, Resource> TO_DOMAIN = (specs) -> {
        final ResourceDaoImpl resource_dao = specs.resourceDao;
        final Map<String, String> m = specs.dto;
        Resource b;
        if ("AmassedResource".equals(m.get("type"))) {
            b = new AmassedResourceBean(m.get("key"), m.get("version_id"), m.get("keyhash")) {
                @Override
                public BucketBean getBucket() throws NoSuchBucket {
                    BucketBean b = super.getBucket();
                    if (b != null) {
                        return b;
                    }
                    BucketBean bb = resource_dao.bucketDao.findBucketByUuid(m.get("bucket_uuid"));
                    if (bb == null) {
                        throw new NoSuchBucket("Seems that bucket have been removed.");
                    }
                    setBucket(bb);
                    return bb;
                }

                @Override
                public Iterator<ChunkedResource> getSubResources() {
                    return super.getSubResources(); //To change body of generated methods, choose Tools | Templates.
                }

            };
        } else if ("ChunkedResource".equals(m.get("type"))) {
            b = new ChunkedResourceBean(m.get("key"), m.get("version_id"), m.get("keyhash")) {
                @Override
                public Bucket getBucket() throws NoSuchBucket {
                    Bucket b = super.getBucket();
                    if (b != null) {
                        return b;
                    }
                    BucketBean bb = resource_dao.bucketDao.findBucketByUuid(m.get("bucket_uuid"));
                    if (bb == null) {
                        throw new NoSuchBucket("Seems that bucket have been removed.");
                    }
                    setBucket(bb);
                    return bb;
                }

                @Override
                public Iterator<Chunk> getChunks() {
                    return resource_dao.chunkDao.listChunksByResource(this);
                }

            };
        } else {
            throw new RuntimeException("Unexpected resource type: [" + m.get("type") + "}");
        }
        b.setETag(m.get("etag"));
        b.setLastModified(DateTime.parse(m.get("last_modified")));
        b.setSize(m.get("size"));
        System.out.println();
        Document doc;
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().
                    parse(specs.metas);
            NodeList meta_nodes = (NodeList) xpath.compile("//Metas/Meta")
                    .evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < meta_nodes.getLength(); i++) {
                Node meta = meta_nodes.item(i);
                b.setMetaData(meta.getAttributes().getNamedItem("name").getTextContent(),
                        meta.getAttributes().getNamedItem("content").getTextContent());
            }
        } catch (XPathExpressionException | ParserConfigurationException
                | SAXException | IOException | IllegalArgumentException ex) {
            if (specs.metas != null) {
                throw new RuntimeException("Unexpected Xml Parse Config exception", ex);
            }
        }
        return b;
    };

    public ResourceDaoImpl(ResourceMapper resource_mapper, ChunkMapper chunk_mapper) {
        this.resourceMapper = resource_mapper;
        this.chunkMapper = chunk_mapper;
    }

    public void setBucketDao(BucketDaoImpl bucketDao) {
        this.bucketDao = bucketDao;
    }

    public void setChunkDao(ChunkDaoImpl chunkDao) {
        this.chunkDao = chunkDao;
    }

    @Override
    public Resource findResource(String bucketName, String resourceKey) throws NoSuchKey, NoSuchBucket {
        Map data = resourceMapper.selectByBucketName_Key(bucketName, resourceKey);
        if (data == null) {
            throw new NoSuchKey(resourceKey);
        }
        return TO_DOMAIN.apply(generateConverterSpecs(data));
    }

    @Override
    public Resource findResource(Bucket bucket, String resourceKey) throws NoSuchKey, NoSuchBucket {
        if (bucket instanceof BucketBean) {
            Map data = resourceMapper.selectByKeyhash(
                    ResourceExtension.hashKey(((BucketBean) bucket).getUuid(),
                            resourceKey)
            );
            if (data == null) {
                throw new NoSuchKey(resourceKey);
            }
            Resource r = TO_DOMAIN.apply(generateConverterSpecs(data));
            ((ResourceExtension) r).setBucket((BucketBean) bucket);
            return r;
        } else {
            return findResource(bucket.getName(), resourceKey);
        }
    }

    public Resource findResource(BucketBean bucket, String resourceKey, String versionId) throws NoSuchKey, NoSuchBucket {
        Map data = resourceMapper.selectByKeyhash_Version(ResourceExtension.hashKey(bucket.getUuid(), resourceKey), versionId);
        if (data == null) {
            throw new NoSuchKey(resourceKey);
        }
        return TO_DOMAIN.apply(generateConverterSpecs(data));
    }

    @Override
    public Resource saveResource(final Resource resource) throws NoSuchBucket {
        return saveResource(resource, (t) -> {
            if (resource instanceof ChunkedResourceBean && ((ChunkedResourceBean) resource).getParentResource() != null) {
                t.setParentResource(((ChunkedResourceBean) resource).getParentResource());
            }
            return t;
        });
    }

    // @TODO: check again is this method necessary???
    public Resource saveResource(Resource resource, Function<ResourceDto, ResourceDto> config) throws NoSuchBucket {
        Bucket b = resource.getBucket();
        ResourceDto dto;
        if (b instanceof BucketBean) {
            dto = new ResourceDto(resource, (BucketBean) b);
        } else {
            BucketBean bb = (BucketBean) bucketDao.findBucketByName(b.getName());
            if (bb == null) {
                throw new NoSuchBucket(b.getName());
            }
            dto = new ResourceDto(resource, bb);
        }
        dto = config.apply(dto);
        if (dto.getParentResource() != null) {
            return resourceMapper.insert(dto) > 0 ? resource : null;
        } else {
            return resourceMapper.merge(dto) > 0 ? resource : null;
        }
    }

    public ChunkedResourceBean findSubResourcePart(String partKey, String partEtag, AmassedResource parent) {
        return (ChunkedResourceBean) TO_DOMAIN.apply(generateConverterSpecs(
                resourceMapper.selectSubResourceByKeyEtagParent(partKey, parent.getVersionId(), partEtag
                )));
    }

    @Override
    public Iterator<Resource> listResources(Bucket bucket) throws NoSuchBucket {
        final Iterator<Map<String, Object>> it = resourceMapper.selectAllByBucketName(bucket.getName(), 1000).iterator();
        return new Iterator<Resource>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Resource next() {
                return TO_DOMAIN.apply(generateConverterSpecs(it.next()));
            }
        };
    }

    @Override
    public Iterator<Resource> listResources(Bucket bucket, String continuation) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeResource(Resource resource) throws NoSuchKey, NoSuchBucket {
        if (resource instanceof ResourceExtension) {
            resourceMapper.deleteByKeyhash(((ResourceExtension) resource).getKeyHash());
        } else {
            resourceMapper.delete(resource);
        }
    }

    private ConverterSpecification generateConverterSpecs(final Map<String, Object> dto) {
        ConverterSpecification specs = new ConverterSpecification();
        specs.resourceDao = this;
        specs.dto = new HashMap<String, String>() {
            @Override
            public String get(Object key) {
                return (String) dto.get(key);
            }

        };
        Clob meta_data = (Clob) dto.get("meta_data");
        if (meta_data != null) {
            try {
                specs.metas = ((Clob) dto.get("meta_data")).getAsciiStream();
            } catch (SQLException ex) {
                throw new RuntimeException("Unexpected when get inputstream from Resource Clob info.", ex);
            }
        }
        return specs;
    }

    @Override
    public void appendChunk(ChunkedResource r, Chunk c) {
        r.setSize(new BigInteger(r.getSize()).add(new BigInteger(c.getSize() + "")).toString());
        chunkMapper.appendChunkToVersion(r.getVersionId(), c);
    }

    // @TODO:  Check later if this method is used and necessary.
    @Override
    public void appendChunkedResource(AmassedResource parent, ChunkedResource c, String partNumber) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static class ConverterSpecification {

        private ResourceDaoImpl resourceDao;

        private Map<String, String> dto;

        private InputStream metas = null;

    }

}
