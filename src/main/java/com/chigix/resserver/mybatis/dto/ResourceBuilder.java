package com.chigix.resserver.mybatis.dto;

import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.Chunk;
import com.chigix.resserver.entity.ChunkedResource;
import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.entity.error.NoSuchBucket;
import com.chigix.resserver.mybatis.BucketDaoImpl;
import com.chigix.resserver.mybatis.ChunkDaoImpl;
import com.chigix.resserver.mybatis.bean.AmassedResourceBean;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    private InputStream metas;

    private String resourceKey;

    private String resourceType;

    private String keyHash;

    private String versionId;

    private String bucketUuid;

    private DateTime lastModified;

    private String etag;

    private String size;

    public void setMetas(String metas) {
        this.metas = new ByteArrayInputStream(metas.getBytes());
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
        try {
            return new BufferedReader(new InputStreamReader(metas)).readLine();
        } catch (IOException ex) {
            return null;
        }
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

    public Resource build(BucketDaoImpl bucketdao, ChunkDaoImpl chunkdao) {
        Resource b;
        switch (resourceType) {
            case "AmassedResource":
                b = constructAmassedResource(bucketdao);
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
                    parse(metas);
            NodeList meta_nodes = (NodeList) xpath.compile("//Metas/Meta")
                    .evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < meta_nodes.getLength(); i++) {
                Node meta = meta_nodes.item(i);
                b.setMetaData(meta.getAttributes().getNamedItem("name").getTextContent(),
                        meta.getAttributes().getNamedItem("content").getTextContent());
            }
        } catch (XPathExpressionException | ParserConfigurationException
                | SAXException | IOException | IllegalArgumentException ex) {
            if (metas != null) {
                throw new RuntimeException("Unexpected Xml Parse Config exception", ex);
            }
        }
        return b;
    }

    private AmassedResourceBean constructAmassedResource(final BucketDaoImpl bucketdao) {
        return new AmassedResourceBean(resourceKey, versionId, keyHash) {
            @Override
            public BucketBean getBucket() throws NoSuchBucket {
                BucketBean b = super.getBucket();
                if (b != null) {
                    return b;
                }
                BucketBean bb = bucketdao.findBucketByUuid(bucketUuid);
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
    }

    private ChunkedResourceBean constructChunkedResource(final BucketDaoImpl bucketdao, final ChunkDaoImpl chunkdao) {
        return new ChunkedResourceBean(resourceKey, versionId, keyHash) {
            @Override
            public Bucket getBucket() throws NoSuchBucket {
                Bucket b = super.getBucket();
                if (b != null) {
                    return b;
                }
                BucketBean bb = bucketdao.findBucketByUuid(bucketUuid);
                if (bb == null) {
                    throw new NoSuchBucket("Seems that bucket have been removed.");
                }
                setBucket(bb);
                return bb;
            }

            @Override
            public Iterator<Chunk> getChunks() {
                return chunkdao.listChunksByResource(this);
            }

        };
    }

}
