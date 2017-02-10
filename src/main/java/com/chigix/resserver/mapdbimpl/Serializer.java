package com.chigix.resserver.mapdbimpl;

import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.Chunk;
import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.mapdbimpl.dao.BucketNotPersistedException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Serializer {

    private static final Logger LOG = LoggerFactory.getLogger(Serializer.class.getName());

    public static String serializeResource(Resource resource) {
        if (!(resource.getBucket() instanceof BucketInStorage)) {
            throw new BucketNotPersistedException(resource.getBucket().getName());
        }
        StringWriter result = new StringWriter();
        XMLStreamWriter writer;
        try {
            writer = XMLOutputFactory.newFactory().createXMLStreamWriter(result);
        } catch (XMLStreamException ex) {
            LOG.error("Unexpected", ex);
            throw new RuntimeException(ex);
        }
        try {
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeStartElement("Resource");
            writer.writeStartElement("Bucket");
            writer.writeCharacters(resource.getBucket().getName());
            writer.writeEndElement();// Resource.Bucket
            writer.writeStartElement("Key");
            writer.writeCharacters(resource.getKey());
            writer.writeEndElement();// Resource.key
            writer.writeStartElement("Etag");
            writer.writeCharacters(resource.getETag());
            writer.writeEndElement();// Resource.Etag
            writer.writeStartElement("LastModified");
            writer.writeCharacters(resource.getLastModified().toString());
            writer.writeEndElement();// Resource.LastModified
            writer.writeStartElement("KeyHash");
            writer.writeCharacters(ResourceInStorage.hashKey(((BucketInStorage) resource.getBucket()).getUUID(), resource.getKey()));
            writer.writeEndElement();// Resource.KeyHash
            writer.writeStartElement("Size");
            writer.writeCharacters(resource.getSize());
            writer.writeEndElement();// Resource.Size
            writer.writeStartElement("StorageClass");
            writer.writeCharacters(resource.getStorageClass());
            writer.writeEndElement();// Resource.StorageClass
            for (Map.Entry<String, String> meta : resource.snapshotMetaData().entrySet()) {
                writer.writeStartElement("Meta");
                writer.writeAttribute("name", meta.getKey());
                writer.writeAttribute("content", meta.getValue());
                writer.writeEndElement();// Resource.Meta
            }
            writer.writeEndElement();// Resource
            writer.writeEndDocument();
            writer.close();
        } catch (XMLStreamException ex) {
            LOG.error("Unexpected", ex);
            throw new RuntimeException(ex);
        }
        return result.toString();
    }

    public static ResourceInStorage deserializeResource(String xml) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            LOG.error("UNEXPECTED", ex);
            throw new RuntimeException(ex);
        }
        final ResourceInStorage result;
        try {
            String bucket_name = ((Node) xpath.compile("//Resource/Bucket").evaluate(doc, XPathConstants.NODE)).getTextContent();
            BucketNameSearchProxy bucket_proxy = new BucketNameSearchProxy(bucket_name);
            bucket_proxy.setProxied(new Bucket(bucket_name));
            result = new ResourceInStorage(bucket_proxy,
                    ((Node) xpath.compile("//Resource/Key").evaluate(doc, XPathConstants.NODE)).getTextContent(),
                    ((Node) xpath.compile("//Resource/KeyHash").evaluate(doc, XPathConstants.NODE)).getTextContent());
            result.setETag(((Node) xpath.compile("//Resource/Etag").evaluate(doc, XPathConstants.NODE)).getTextContent());
            result.setLastModified(DateTime.parse(((Node) xpath.compile("//Resource/LastModified").evaluate(doc, XPathConstants.NODE)).getTextContent()));
            result.setSize(((Node) xpath.compile("//Resource/Size").evaluate(doc, XPathConstants.NODE)).getTextContent());
            // result.setStorageClass(((Node) xpath.compile("//Resource/StorageClass").evaluate(doc, XPathConstants.NODE)).getTextContent());
            NodeList meta_nodes = (NodeList) xpath.compile("//Resource/Meta").evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < meta_nodes.getLength(); i++) {
                Node meta = meta_nodes.item(i);
                result.setMetaData(meta.getAttributes().getNamedItem("name").getTextContent(), meta.getAttributes().getNamedItem("content").getTextContent());
            }
            bucket_proxy.resetProxy();
        } catch (XPathExpressionException ex) {
            LOG.error("UNEXPECTED", ex);
            throw new RuntimeException(ex);
        }
        return result;
    }

    public static String serializeResourceLinkNode(ResourceLinkNode node) {
        StringWriter result = new StringWriter();
        XMLStreamWriter writer;
        try {
            writer = XMLOutputFactory.newFactory().createXMLStreamWriter(result);
        } catch (XMLStreamException ex) {
            LOG.error("Unexpected", ex);
            throw new RuntimeException(ex);
        }
        try {
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeStartElement("ResourceLinkNode");
            if (node.getPreviousResourceKeyHash() != null) {
                writer.writeStartElement("PreviousResourceKeyHash");
                writer.writeCharacters(node.getPreviousResourceKeyHash());
                writer.writeEndElement();// ResourceLinkNode.PreviousResourceKeyHash
            }
            if (node.getNextResourceKeyHash() != null) {
                writer.writeStartElement("NextResourceKeyHash");
                writer.writeCharacters(node.getNextResourceKeyHash());
                writer.writeEndElement();// ResourceLinkNode.NextResourceKeyHash
            }
            writer.writeEndElement();// ResourceLinkNode
            writer.writeEndDocument();
            writer.close();
        } catch (XMLStreamException ex) {
            LOG.error("Unexpected", ex);
            throw new RuntimeException(ex);
        }
        return result.toString();
    }

    public static ResourceLinkNode deserializeResourceLinkNode(String xml) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            LOG.error("UNEXPECTED", ex);
            throw new RuntimeException(ex);
        }
        final ResourceLinkNode result;
        try {
            result = new ResourceLinkNode();
            Node next_resource_key_hash = ((Node) xpath.compile("//ResourceLinkNode/NextResourceKeyHash").evaluate(doc, XPathConstants.NODE));
            if (next_resource_key_hash != null) {
                result.setNextResourceKeyHash(next_resource_key_hash.getTextContent());
            }
            Node prev_resource_key_hash = ((Node) xpath.compile("//ResourceLinkNode/PreviousResourceKeyHash").evaluate(doc, XPathConstants.NODE));
            if (prev_resource_key_hash != null) {
                result.setPreviousResourceKeyHash(prev_resource_key_hash.getTextContent());
            }
        } catch (XPathExpressionException ex) {
            LOG.error("UNEXPECTED", ex);
            throw new RuntimeException(ex);
        }
        return result;
    }

    public static String serializeBucket(BucketInStorage b) {
        StringWriter string = new StringWriter();
        XMLStreamWriter writer;
        try {
            writer = XMLOutputFactory.newFactory().createXMLStreamWriter(string);
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeStartElement("Bucket");
            writer.writeStartElement("Name");
            writer.writeCharacters(b.getName());
            writer.writeEndElement(); // Bucket.Name
            writer.writeStartElement("CreationTime");
            writer.writeCharacters(b.getCreationTime().toString());
            writer.writeEndElement(); // Bucket.CreationTime
            writer.writeStartElement("ChizuruUUID");
            writer.writeCharacters(b.getUUID());
            writer.writeEndElement(); // Bucket.ChizuruUUID
            writer.writeEndElement();// Bucket
            writer.writeEndDocument();
        } catch (XMLStreamException ex) {
            LOG.error("Unexpected", ex);
            throw new RuntimeException(ex);
        }
        return string.toString();
    }

    public static BucketInStorage deserializeBucket(String xml) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            LOG.error("UNEXPECTED", ex);
            throw new RuntimeException(ex);
        }
        final BucketInStorage result;
        try {
            result = new BucketInStorage(
                    ((Node) xpath.compile("//Bucket/Name").evaluate(doc, XPathConstants.NODE)).getTextContent(),
                    DateTime.parse(((Node) xpath.compile("//Bucket/CreationTime").evaluate(doc, XPathConstants.NODE)).getTextContent()));
            result.setUUID(((Node) xpath.compile("//Bucket/ChizuruUUID").evaluate(doc, XPathConstants.NODE)).getTextContent());
        } catch (XPathExpressionException ex) {
            LOG.error("UNEXPECTED", ex);
            throw new RuntimeException(ex);
        }
        return result;
    }

    public static String serializeChunk(Chunk c) {
        StringWriter string = new StringWriter();
        XMLStreamWriter writer;
        try {
            writer = XMLOutputFactory.newFactory().createXMLStreamWriter(string);
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeStartElement("Chunk");
            writer.writeStartElement("ContentHash");
            writer.writeCharacters(c.getContentHash());
            writer.writeEndElement(); // Chunk.ContentHash
            writer.writeStartElement("Size");
            writer.writeCharacters(c.getSize() + "");
            writer.writeEndElement(); // Chunk.Size
            writer.writeEndElement();// Chunk
            writer.writeEndDocument();
        } catch (XMLStreamException ex) {
            LOG.error("Unexpected", ex);
            throw new RuntimeException(ex);
        }
        return string.toString();
    }

    public static Chunk deserializeChunk(String xml) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            LOG.error("UNEXPECTED", ex);
            throw new RuntimeException(ex);
        }
        final Chunk result;
        try {
            result = new Chunk(((Node) xpath.compile("//Chunk/ContentHash").evaluate(doc, XPathConstants.NODE)).getTextContent(),
                    Integer.valueOf(((Node) xpath.compile("//Chunk/Size").evaluate(doc, XPathConstants.NODE)).getTextContent()));
        } catch (XPathExpressionException ex) {
            LOG.error("UNEXPECTED", ex);
            throw new RuntimeException(ex);
        }
        return result;
    }

}
