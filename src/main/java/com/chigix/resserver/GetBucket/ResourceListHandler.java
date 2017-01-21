package com.chigix.resserver.GetBucket;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.Resource;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.router.HttpRouted;
import io.netty.handler.stream.ChunkedStream;
import io.netty.util.CharsetUtil;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
public class ResourceListHandler extends SimpleChannelInboundHandler<HttpRouted> {

    private static ResourceListHandler INSTANCE = null;

    private static final XMLOutputFactory XML_FACTORY = XMLOutputFactory.newInstance();

    private final ApplicationContext application;

    public static ResourceListHandler getInstance(ApplicationContext ctx) {
        if (INSTANCE == null) {
            INSTANCE = new ResourceListHandler(ctx);
        }
        return INSTANCE;
    }

    public ResourceListHandler(ApplicationContext application) {
        this.application = application;
    }

    /**
     * Response bucket objects list.
     *
     * @TODO Not support key search. ONLY list all the keys below this bucket.
     * @TODO ONLY SUPPORT standard storage class.
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, HttpRouted msg) throws Exception {
        Bucket target_bucket = application.BucketDao.findBucketByName((String) msg.decodedParams().get("bucketName"));
        QueryStringDecoder decoder = new QueryStringDecoder(msg.getRequestMsg().uri());
        String delimiter = decodeQueryParamString(decoder, "delimiter");
        String encoding_type = decodeQueryParamString(decoder, "encoding-type");
        String max_keys = decodeQueryParamString(decoder, "max-keys");
        String prefix = decodeQueryParamString(decoder, "prefix");
        String continuation_token = decodeQueryParamString(decoder, "continuation-token");
        String fetch_owner = decodeQueryParamString(decoder, "fetch-owner");
        String start_after = decodeQueryParamString(decoder, "start-after");
        DefaultHttpResponse resp = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/xml");
        resp.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
        ctx.write(resp);
        PipedInputStream inputstream = new PipedInputStream();
        ctx.write(new HttpChunkedInput(new ChunkedStream(inputstream)));
        try (OutputStreamWriter writer = new OutputStreamWriter(new PipedOutputStream(inputstream), CharsetUtil.UTF_8)) {
            XMLStreamWriter xml_writer = XML_FACTORY.createXMLStreamWriter(writer);
            xml_writer.writeStartDocument("UTF-8", "1.0");
            xml_writer.writeStartElement("ListBucketResult");
            xmlWriteBucketName(xml_writer, application.BucketDao.findBucketByName((String) msg.decodedParams().get("bucketName")));
            xmlWriteStartAfter(xml_writer, start_after); //ListBucketResult.StartAfter
            Charset encodingType;
            if (encoding_type == null) {
                encodingType = CharsetUtil.UTF_8;
            } else {
                encodingType = Charset.forName(encoding_type);
                xml_writer.writeStartElement("Encoding-Type");
                xml_writer.writeCharacters(encoding_type);
                xml_writer.writeEndElement();
            }
            xml_writer.writeStartElement("Prefix");
            if (prefix != null) {
                xml_writer.writeCharacters(prefix);
            }
            xml_writer.writeEndElement();//ListBucketResult.Prefix
            xmlWriteMaxKeys(xml_writer, max_keys);//ListBucketResult.MaxKeys
            if (delimiter != null) {
                xml_writer.writeStartElement("Delimiter");
                xml_writer.writeCharacters(delimiter);
                xml_writer.writeEndElement();//ListBucketResult.Delimiter
            }
            Iterator<Resource> resources = application.ResourceDao.listResources(target_bucket);
            while (resources.hasNext()) {
                Resource next = resources.next();
                xmlWriteResourceContent(xml_writer, next);//ListBucketResult.Contents
            }
            xml_writer.writeStartElement("IsTruncated");
            xml_writer.writeCharacters("false");
            xml_writer.writeEndElement();//ListBucketResult.IsTruncated
            xml_writer.writeStartElement("KeyCount");
            xml_writer.writeCharacters(2 + "");
            xml_writer.writeEndElement();//ListBucketResult.KeyCount
            //xmlWriteCommonPrefixes(xml_writer, delimiter);//ListBucketResult.CommonPrefixes
            xml_writer.writeEndElement();// ListBucketResult
            xml_writer.writeEndDocument();
        }
        ctx.flush();
    }

    private String decodeQueryParamString(QueryStringDecoder decoder, String key) {
        List decode_value = decoder.parameters().get(key);
        if (decode_value == null) {
            return null;
        }
        if (decode_value.size() > 0) {
            return (String) decode_value.get(0);
        } else {
            return null;
        }
    }

    private void xmlWriteCommonPrefixes(XMLStreamWriter writer, String delimiter) throws XMLStreamException {
        writer.writeStartElement("CommonPrefixes");
        writer.writeEndElement();
    }

    private void xmlWriteResourceContent(XMLStreamWriter writer, Resource res) throws XMLStreamException {
        writer.writeStartElement("Contents");
        writer.writeStartElement("Key");
        writer.writeCharacters(res.getKey());
        writer.writeEndElement();//Key
        writer.writeStartElement("LastModified");
        writer.writeCharacters(res.getLastModified().toString());
        writer.writeEndElement();//LastModified
        writer.writeStartElement("ETag");
        writer.writeCharacters(String.format("\"%s\"", res.getETag()));
        writer.writeEndElement();//ETag
        writer.writeStartElement("Size");
        writer.writeCharacters(res.getSize());
        writer.writeEndElement();//Size
        writer.writeStartElement("StorageClass");
        writer.writeCharacters(res.getStorageClass());
        writer.writeEndElement();//StorageClass
        writer.writeEndElement();
    }

    private void xmlWriteStartAfter(XMLStreamWriter writer, String start_after) throws XMLStreamException {
        if (start_after == null) {
            return;
        }
        writer.writeStartElement("StartAfter");
        writer.writeCharacters(start_after);
        writer.writeEndElement();
    }

    private void xmlWriteBucketName(XMLStreamWriter writer, Bucket bucket) throws XMLStreamException {
        writer.writeStartElement("Name");
        writer.writeCharacters(bucket.getName());
        writer.writeEndElement();
    }

    private void xmlWriteMaxKeys(XMLStreamWriter writer, String paramMaxKeys) throws XMLStreamException {
        writer.writeStartElement("MaxKeys");
        if (paramMaxKeys != null && Integer.valueOf(paramMaxKeys) < 100) {
            writer.writeCharacters(paramMaxKeys);
        } else {
            writer.writeCharacters("100");
        }
        writer.writeEndElement();//ListBucketResult.MaxKeys
    }

}
