package com.chigix.resserver.PostResource;

import com.chigix.resserver.entity.MultipartUpload;
import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.sharablehandlers.Context;
import com.chigix.resserver.util.XPathNode;
import com.fasterxml.aalto.AsyncByteArrayFeeder;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.stax.InputFactoryImpl;
import io.netty.handler.codec.http.router.HttpRouted;
import java.io.StringWriter;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
class MultipartUploadContext extends Context {

    private MultipartUpload upload;

    private final XMLStreamReader xmlStreamReader;

    private final AsyncByteArrayFeeder xmlStreamFeeder;

    private XPathNode currentXmlStreamNode = null;

    private StringWriter currentXmlStreamTextCache = null;

    private CompleteMultipartUploadPart currentXmlStreamUploadPart = null;

    private final MultiUploadCompleteContentHandler.CalculateEtag currentEtagCalculator
            = new MultiUploadCompleteContentHandler.CalculateEtag();

    public MultipartUploadContext(HttpRouted routedInfo, Resource resource) {
        super(routedInfo, resource);
        AsyncXMLStreamReader<AsyncByteArrayFeeder> reader = new InputFactoryImpl().createAsyncForByteArray();
        xmlStreamReader = reader;
        xmlStreamFeeder = reader.getInputFeeder();
    }

    public void setUpload(MultipartUpload upload) {
        this.upload = upload;
    }

    public MultipartUpload getUpload() {
        return upload;
    }

    public XMLStreamReader getXmlStreamReader() {
        return xmlStreamReader;
    }

    public AsyncByteArrayFeeder getXmlStreamFeeder() {
        return xmlStreamFeeder;
    }

    public XPathNode getCurrentXmlStreamNode() {
        return currentXmlStreamNode;
    }

    public void setCurrentXmlStreamNode(XPathNode currentXmlStreamNode) {
        this.currentXmlStreamNode = currentXmlStreamNode;
    }

    public void clearCurrentXmlStreamTextCache() {
        this.currentXmlStreamTextCache = new StringWriter();
    }

    public StringWriter getCurrentXmlStreamTextCache() {
        return currentXmlStreamTextCache;
    }

    public MultiUploadCompleteContentHandler.CalculateEtag getCurrentEtagCalculator() {
        return currentEtagCalculator;
    }

    public CompleteMultipartUploadPart getCurrentXmlStreamUploadPart() {
        return currentXmlStreamUploadPart;
    }

    public void newCurrentXmlStreamUploadPart() {
        this.currentXmlStreamUploadPart = new CompleteMultipartUploadPart();
    }

    public static class CompleteMultipartUploadPart {

        private String etag;
        private String partNumber;

        public String getEtag() {
            return etag;
        }

        public void setEtag(String etag) {
            this.etag = etag;
        }

        public String getPartNumber() {
            return partNumber;
        }

        public void setPartNumber(String partNumber) {
            this.partNumber = partNumber;
        }

    }

}
