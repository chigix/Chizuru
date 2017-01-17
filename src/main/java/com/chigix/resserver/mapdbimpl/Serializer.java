package com.chigix.resserver.mapdbimpl;

import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.Resource;
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
            if (resource instanceof ResourceInStorage) {
                if (((ResourceInStorage) resource).getFirstChunk() != null) {
                    writer.writeStartElement("FirstChunk");
                    writer.writeCharacters(((ResourceInStorage) resource).getFirstChunk().getContentHash());
                    writer.writeEndElement();// Resource.FirstChunk
                }
                if (((ResourceInStorage) resource).getLastChunk() != null) {
                    writer.writeStartElement("LastChunk");
                    writer.writeCharacters(((ResourceInStorage) resource).getLastChunk().getContentHash());
                    writer.writeEndElement();// Resource.LastChunk
                }
            }
            writer.writeStartElement("Etag");
            writer.writeCharacters(resource.getETag());
            writer.writeEndElement();// Resource.Etag
            writer.writeStartElement("LastModified");
            writer.writeCharacters(resource.getLastModified().toString());
            writer.writeEndElement();// Resource.LastModified
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
            result = new ResourceInStorage(bucket_proxy, ((Node) xpath.compile("//Resource/Key").evaluate(doc, XPathConstants.NODE)).getTextContent());
            result.setETag(((Node) xpath.compile("//Resource/Etag").evaluate(doc, XPathConstants.NODE)).getTextContent());
            result.setLastModified(DateTime.parse(((Node) xpath.compile("//Resource/LastModified").evaluate(doc, XPathConstants.NODE)).getTextContent()));
            result.setSize(((Node) xpath.compile("//Resource/Size").evaluate(doc, XPathConstants.NODE)).getTextContent());
            // result.setStorageClass(((Node) xpath.compile("//Resource/StorageClass").evaluate(doc, XPathConstants.NODE)).getTextContent());
            NodeList meta_nodes = (NodeList) xpath.compile("//Resource/Meta").evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < meta_nodes.getLength(); i++) {
                Node meta = meta_nodes.item(i);
                result.setMetaData(meta.getAttributes().getNamedItem("name").getTextContent(), meta.getAttributes().getNamedItem("content").getTextContent());
            }
            Node first_chunk_node = (Node) xpath.compile("//Resource/FirstChunk").evaluate(doc, XPathConstants.NODE);
            Node last_chunk_node = (Node) xpath.compile("//Resource/LastChunk").evaluate(doc, XPathConstants.NODE);
            if (first_chunk_node != null) {
                result.setFirstChunk(first_chunk_node.getTextContent());
            }
            if (last_chunk_node != null) {
                result.setFirstChunk(last_chunk_node.getTextContent());
            }
            bucket_proxy.resetProxy();
        } catch (XPathExpressionException ex) {
            LOG.error("UNEXPECTED", ex);
            throw new RuntimeException(ex);
        }
        return result;
    }

    public static String serializeChunkNode(ChunkNode chunkNode) {
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
            writer.writeStartElement("ChunkNode");
            writer.writeStartElement("ContentHash");
            writer.writeCharacters(chunkNode.getContentHash());
            writer.writeEndElement();// ChunkNode.ContentHash
            writer.writeStartElement("ParentResourceKeyHash");
            writer.writeCharacters(chunkNode.getParentResourceKeyHash());
            writer.writeEndElement();// ChunkNode.ParentResourceKeyHash
            writer.writeEndElement();// ChunkNode
            writer.writeEndDocument();
            writer.close();
        } catch (XMLStreamException ex) {
            LOG.error("Unexpected", ex);
            throw new RuntimeException(ex);
        }
        return result.toString();
    }

    public static ChunkNode deserializeChunkNode(String xml) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            LOG.error("UNEXPECTED", ex);
            throw new RuntimeException(ex);
        }
        final ChunkNode result;
        try {
            result = new ChunkNode(((Node) xpath.compile("//ChunkNode/ParentResourceKeyHash").evaluate(doc, XPathConstants.NODE)).getTextContent(),
                    ((Node) xpath.compile("//ChunkNode/ContentHash").evaluate(doc, XPathConstants.NODE)).getTextContent());
        } catch (XPathExpressionException ex) {
            LOG.error("UNEXPECTED", ex);
            throw new RuntimeException(ex);
        }
        return result;
    }

}
