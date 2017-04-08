package com.chigix.resserver.mybatis.dto;

import com.chigix.resserver.entity.AmassedResource;
import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.entity.error.NoSuchBucket;
import com.chigix.resserver.mybatis.bean.AmassedResourceBean;
import com.chigix.resserver.mybatis.bean.BucketBean;
import com.chigix.resserver.mybatis.bean.ChunkedResourceBean;
import com.chigix.resserver.mybatis.bean.ResourceExtension;
import java.io.StringWriter;
import java.util.Map;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ResourceDto {

    private final Resource bean;

    private ResourceExtension beanExt = null;

    private final BucketBean bucket;

    public ResourceDto(Resource bean, BucketBean bucket) {
        this.bean = bean;
        this.bucket = bucket;
        if (bean instanceof ResourceExtension) {
            beanExt = (ResourceExtension) bean;
        }
    }

    public ResourceDto(ResourceExtension bean) throws NoSuchBucket {
        this.bean = (Resource) bean;
        this.bucket = (BucketBean) this.bean.getBucket();
        this.beanExt = bean;
    }

    public AmassedResourceBean getParentResource() {
        if (bean instanceof ChunkedResourceBean) {
            return ((ChunkedResourceBean) bean).getParentResource();
        }
        return null;
    }

    public Resource getBean() {
        return bean;
    }

    public BucketBean getBucket() {
        return bucket;
    }

    public String getType() {
        if (bean instanceof AmassedResource) {
            return "AmassedResource";
        } else {
            return "ChunkedResource";
        }
    }

    public String getKeyHash() {
        if (beanExt == null) {
            return ResourceExtension.hashKey(bucket.getUuid(), bean.getKey());
        }
        return beanExt.getKeyHash();
    }

    public String getLastModified() {
        return bean.getLastModified().toString();
    }

    public String getMetaData() {
        StringWriter result = new StringWriter();
        XMLStreamWriter writer;
        try {
            writer = XMLOutputFactory.newFactory().createXMLStreamWriter(result);
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeStartElement("Metas");
            for (Map.Entry<String, String> meta : bean.snapshotMetaData().entrySet()) {
                writer.writeStartElement("Meta");
                writer.writeAttribute("name", meta.getKey());
                writer.writeAttribute("content", meta.getValue());
                writer.writeEndElement();// Metas.Meta
            }
            writer.writeEndElement();
            writer.writeEndDocument();
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
        return result.toString();
    }

}
