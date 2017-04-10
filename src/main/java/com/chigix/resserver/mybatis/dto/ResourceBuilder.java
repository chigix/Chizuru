package com.chigix.resserver.mybatis.dto;

import com.chigix.resserver.domain.Bucket;
import com.chigix.resserver.domain.Chunk;
import com.chigix.resserver.domain.ChunkedResource;
import com.chigix.resserver.domain.Resource;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.mybatis.BucketDaoImpl;
import com.chigix.resserver.mybatis.ChunkDaoImpl;
import com.chigix.resserver.mybatis.ResourceDaoImpl;
import com.chigix.resserver.mybatis.bean.AmassedResourceBean;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
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
public class ResourceBuilder {

    public static ResourceBuilder createFrom(ResourceDto dto) {
        ResourceBuilder rb = new ResourceBuilder();
        rb.setBucketUuid(dto.getBucket().getUuid());
        rb.setEtag(dto.getBean().getETag());
        rb.setKeyHash(dto.getKeyHash());
        rb.setLastModified(dto.getLastModified());
        rb.setMetas(dto.getMetaData());
        rb.setResourceKey(dto.getBean().getKey());
        rb.setResourceType(dto.getType());
        rb.setSize(dto.getBean().getSize());
        rb.setVersionId(dto.getBean().getVersionId());
        return rb;
    }

    private String metas = null;

    private String resourceKey;

    private String resourceType;

    private String keyHash;

    private String versionId;

    private String bucketUuid;

    private DateTime lastModified;

    private String etag;

    private String size;

    public void setMetas(String metas) {
        this.metas = metas;
    }

    public void setResourceKey(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public void setKeyHash(String keyHash) {
        this.keyHash = keyHash;
    }

    public String getBucketUuid() {
        return bucketUuid;
    }

    public String getMetas() {
        return this.metas;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getKeyHash() {
        return keyHash;
    }

    public String getVersionId() {
        return versionId;
    }

    public DateTime getLastModified() {
        return lastModified;
    }

    public String getEtag() {
        return etag;
    }

    public String getSize() {
        return size;
    }

    public void setBucketUuid(String bucketUuid) {
        this.bucketUuid = bucketUuid;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = DateTime.parse(lastModified);
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Resource build(BucketDaoImpl bucketdao, ChunkDaoImpl chunkdao, ResourceDaoImpl resourceDao) {
        Resource b;
        switch (resourceType) {
            case "AmassedResource":
                b = constructAmassedResource(bucketdao, resourceDao);
                break;
            case "ChunkedResource":
                b = constructChunkedResource(bucketdao, chunkdao);
                break;
            default:
                throw new RuntimeException("Unexpected resource type: [" + resourceType + "}");
        }
        b.setETag(etag);
        b.setLastModified(lastModified);
        b.setSize(size);
        Document doc;
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().
                    parse(new ByteArrayInputStream(this.metas.getBytes()));
            NodeList meta_nodes = (NodeList) xpath.compile("//Metas/Meta")
                    .evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < meta_nodes.getLength(); i++) {
                Node meta = meta_nodes.item(i);
                b.setMetaData(meta.getAttributes().getNamedItem("name").getTextContent(),
                        meta.getAttributes().getNamedItem("content").getTextContent());
            }
        } catch (XPathExpressionException | ParserConfigurationException
                | SAXException | IOException | IllegalArgumentException | NullPointerException ex) {
            if (metas != null) {
                throw new RuntimeException("Unexpected Xml Parse Config exception", ex);
            }
        }
        return b;
    }

    private AmassedResourceBean constructAmassedResource(final BucketDaoImpl bucketdao, final ResourceDaoImpl resourceDao) {
        return new AmassedResourceBean(resourceKey, versionId, keyHash) {
            @Override
            public BucketBean getBucket() throws NoSuchBucket {
                BucketBean b = super.getBucket();
                if (b != null) {
                    return b;
                }
                try {
                    BucketBean bb = bucketdao.findBucketByUuid(bucketUuid);
                    if (bb == null) {
                        throw new NoSuchBucket("Seems that bucket have been removed.");
                    }
                    setBucket(bb);
                    return bb;
                } catch (NullPointerException ex) {
                    if (bucketdao == null) {
                        throw new UnsupportedOperationException("BucketDao is not given. This operation is disabled.");
                    }
                    throw ex;
                }
            }

            @Override
            public Iterator<ChunkedResource> getSubResources() {
                try {
                    return resourceDao.listSubResources(this);
                } catch (NullPointerException ex) {
                    if (resourceDao == null) {
                        throw new UnsupportedOperationException("ResourceDao is not given. This operation is disabled.");
                    }
                    throw ex;
                }
            }

        };
    }

    private ChunkedResourceBean constructChunkedResource(final BucketDaoImpl bucketdao, final ChunkDaoImpl chunkdao) {
        return new ChunkedResourceBean(resourceKey, versionId, keyHash) {
            @Override
            public Bucket getBucket() throws NoSuchBucket {
                Bucket b = super.getBucket();
                if (b != null) {
                    return b;
                }
                try {
                    BucketBean bb = bucketdao.findBucketByUuid(bucketUuid);
                    if (bb == null) {
                        throw new NoSuchBucket("Seems that bucket have been removed.");
                    }
                    setBucket(bb);
                    return bb;
                } catch (NullPointerException ex) {
                    if (bucketdao == null) {
                        throw new UnsupportedOperationException("BucketDao is not given. This operation is disabled.");
                    }
                    throw ex;
                }
            }

            @Override
            public Iterator<Chunk> getChunks() {
                try {
                    return chunkdao.listChunksByResource(this);
                } catch (NullPointerException e) {
                    if (chunkdao == null) {
                        throw new UnsupportedOperationException("ChunkDao is not given. This operation is disabled.");
                    }
                    throw e;
                }
            }

        };
    }

}
