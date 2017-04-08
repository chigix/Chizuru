package com.chigix.resserver;

import com.fasterxml.aalto.AsyncByteArrayFeeder;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.stax.InputFactoryImpl;
import java.io.StringWriter;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class XMLTest {

    @Test
    public void testGenerateXML() {
        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
        doc.appendChild(doc.createElement("BANKAI"));
        doc.getElementsByTagName("BANKAI").item(0).setTextContent("JIKAI");
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            System.out.println(xpath.evaluate("/BANKAI", doc));
        } catch (XPathExpressionException ex) {
            Logger.getLogger(XMLTest.class.getName()).log(Level.SEVERE, null, ex);
        }
//        doc.getElementById("BANKAI").setTextContent("JIKAI");
        StringWriter writer = new StringWriter();
        try {
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc), new StreamResult(writer));
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(XMLTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(XMLTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(writer.toString());
    }

    @Test
    public void testXmlStreamReader() throws Exception {
        final AsyncXMLStreamReader<AsyncByteArrayFeeder> reader = new InputFactoryImpl().createAsyncForByteArray();
        Function<Integer, String> fun = (i) -> {
            switch (i) {
                case AsyncXMLStreamReader.EVENT_INCOMPLETE:
                    return "EVENT_INCOMPLETE";
                case XMLStreamConstants.START_ELEMENT:
                    return "START_ELEMENT";
                case XMLStreamConstants.END_ELEMENT:
                    return "END_ELEMENT";
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                    return "PROCESSING_INSTRUCTION";
                case XMLStreamConstants.CHARACTERS:
                    return "CHARACTERS";
                case XMLStreamConstants.SPACE:
                    return "SPACE";
                case XMLStreamConstants.START_DOCUMENT:
                    return "START_DOCUMENT";
                case XMLStreamConstants.END_DOCUMENT:
                    return "END_DOCUMENT";
                default:
                    return i + "";
            }
        };
        final Function<String, Object> feedString = (t) -> {
            byte[] bytes = t.getBytes();
            try {
                reader.getInputFeeder().feedInput(bytes, 0, bytes.length);
            } catch (XMLStreamException ex) {
                throw new RuntimeException("Unexpected:XMLStreamException:" + ex.getMessage(), ex);
            }
            return null;
        };
        feedString.apply("<outer>");
        Assert.assertEquals(XMLStreamConstants.START_DOCUMENT, reader.next());
        Assert.assertEquals(XMLStreamConstants.START_ELEMENT, reader.next());
        Assert.assertEquals("outer", reader.getLocalName());
        feedString.apply("<bankai>asdfasdf");
        Assert.assertEquals(XMLStreamConstants.START_ELEMENT, reader.next());
        Assert.assertEquals("bankai", reader.getLocalName());
        Assert.assertEquals(XMLStreamConstants.CHARACTERS, reader.next());
        Assert.assertEquals("asdfasdf", reader.getText());
        Assert.assertEquals(AsyncXMLStreamReader.EVENT_INCOMPLETE, reader.next());
        feedString.apply("follwed String contents.");
        Assert.assertEquals(XMLStreamConstants.CHARACTERS, reader.next());
        Assert.assertEquals("follwed String contents.", reader.getText());
        feedString.apply("content end. </bankai>");
        Assert.assertEquals(XMLStreamConstants.CHARACTERS, reader.next());
        Assert.assertEquals("content end. ", reader.getText());
        Assert.assertEquals(XMLStreamConstants.END_ELEMENT, reader.next());
        Assert.assertEquals("bankai", reader.getLocalName());
        Assert.assertEquals(AsyncXMLStreamReader.EVENT_INCOMPLETE, reader.next());
        feedString.apply("<asdfasdf");
        Assert.assertEquals(AsyncXMLStreamReader.EVENT_INCOMPLETE, reader.next());
        feedString.apply("end>");
        Assert.assertEquals(XMLStreamConstants.START_ELEMENT, reader.next());
        Assert.assertEquals("asdfasdfend", reader.getLocalName());
        feedString.apply("</asdfasdfend>");
        Assert.assertEquals(XMLStreamConstants.END_ELEMENT, reader.next());
        Assert.assertEquals("asdfasdfend", reader.getLocalName());
        feedString.apply("</outer>");
        Assert.assertEquals(XMLStreamConstants.END_ELEMENT, reader.next());
        Assert.assertEquals("outer", reader.getLocalName());
    }

}
