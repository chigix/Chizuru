package com.chigix.resserver.mybatis.mapstruct;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.security.InvalidParameterException;
import java.util.HashMap;
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
import org.mapstruct.Mapper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@Mapper(componentModel = "spring")
public class MetaDataMapper {

    public String toXml(Map<String, String> metaData) {
        StringWriter meta_xml = new StringWriter();
        XMLStreamWriter writer;
        try {
            writer = XMLOutputFactory.newFactory().createXMLStreamWriter(meta_xml);
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeStartElement("Metas");
            for (Map.Entry<String, String> meta : metaData.entrySet()) {
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
        return meta_xml.toString();
    }

    public Map<String, String> fromXml(String xml) {
        if (xml == null) {
            throw new InvalidParameterException("MetaDataXml is null");
        }
        final HashMap<String, String> map = new HashMap<>();
        Document doc;
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().
                    parse(new ByteArrayInputStream(xml.getBytes()));
            NodeList meta_nodes = (NodeList) xpath.compile("//Metas/Meta")
                    .evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < meta_nodes.getLength(); i++) {
                Node meta = meta_nodes.item(i);
                map.put(meta.getAttributes().getNamedItem("name").getTextContent(),
                        meta.getAttributes().getNamedItem("content").getTextContent());
            }
        } catch (XPathExpressionException | ParserConfigurationException
                | SAXException | IOException | IllegalArgumentException | NullPointerException ex) {
            throw new RuntimeException("Unexpected Xml Parse Config exception", ex);
        }
        return map;
    }

}
