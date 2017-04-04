package com.chigix.resserver.GetBucket;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.util.HttpHeaderUtil;
import com.chigix.resserver.util.InputStreamProxy;
import com.chigix.resserver.util.IteratorInputStream;
import com.chigix.resserver.util.OutputStreamProxy;
import io.netty.channel.ChannelFutureListener;
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
import io.netty.handler.stream.ChunkedStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.zip.GZIPOutputStream;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * GET Bucket (List Objects) Version 2
 * http://docs.aws.amazon.com/AmazonS3/latest/API/v2-RESTBucketGET.html
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
public class ResourceListHandler extends SimpleChannelInboundHandler<Context> {

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
     * @param route_ctx
     * @throws Exception
     */
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, final Context route_ctx) throws Exception {
        final boolean isGzip = HttpHeaderUtil.isGzip(route_ctx.getRoutedInfo().getRequestMsg());
        DefaultHttpResponse resp = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/xml");
        resp.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
        ctx.write(resp);
        final OutputStreamProxy<ByteArrayOutputStream> byte_stream = new OutputStreamProxy<>();
        byte_stream.setStream(new ByteArrayOutputStream());
        final XMLStreamWriter xml_writer;
        if (isGzip) {
            resp.headers().set(HttpHeaderNames.CONTENT_ENCODING, HttpHeaderValues.GZIP);
            xml_writer = XML_FACTORY.createXMLStreamWriter(new GZIPOutputStream(byte_stream, true), "UTF-8");
        } else {
            xml_writer = XML_FACTORY.createXMLStreamWriter(byte_stream, "UTF-8");
        }
        final QueryStringDecoder decoder = new QueryStringDecoder(route_ctx.getRoutedInfo().getRequestMsg().uri());
        InputStream result = new SequenceInputStream(
                new InputStreamProxy() {
            @Override
            public int read() throws IOException {
                try {
                    return super.read();
                } catch (NullPointerException nullPointerException) {
                    if (getStream() == null) {
                        try {
                            setStream(generateDocumentStart(xml_writer, byte_stream, decoder, route_ctx.getTargetBucket()));
                        } catch (XMLStreamException ex) {
                            throw new RuntimeException("Unexpected!!!XMLException", ex);
                        }
                        return read();
                    } else {
                        throw nullPointerException;
                    }
                }
            }

        },
                new SequenceInputStream(new IteratorInputStream<Resource>(application.getDaoFactory().getResourceDao().listResources(route_ctx.getTargetBucket())) {
                    @Override
                    protected InputStream next(Resource item) throws NoSuchElementException {
                        final ByteArrayOutputStream result = new ByteArrayOutputStream();
                        byte_stream.setStream(result);
                        try {
                            xmlWriteResourceContent(xml_writer, item);
                        } catch (XMLStreamException ex) {
                            throw new RuntimeException("Unexpected!! stringwriter closed", ex);
                        }
                        byte_stream.setStream(null);
                        return new ByteArrayInputStream(result.toByteArray());
                    }
                }, new InputStreamProxy() {
                    @Override
                    public int read() throws IOException {
                        try {
                            return super.read();
                        } catch (NullPointerException nullPointerException) {
                            if (getStream() == null) {
                                setStream(generateDocumentEnd(xml_writer, byte_stream));
                                return read();
                            } else {
                                throw nullPointerException;
                            }
                        }
                    }

                })
        );
        ctx.writeAndFlush(new HttpChunkedInput(new ChunkedStream(result))).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
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

    private InputStream generateDocumentStart(XMLStreamWriter xml_writer,
            OutputStreamProxy<ByteArrayOutputStream> stream,
            QueryStringDecoder query, Bucket bucket) throws XMLStreamException {
        final String delimiter = decodeQueryParamString(query, "delimiter");
        final String start_after = decodeQueryParamString(query, "start-after");
        final String encoding_type = decodeQueryParamString(query, "encoding-type");
        final String prefix = decodeQueryParamString(query, "prefix");
        final String max_keys = decodeQueryParamString(query, "max-keys");
        final String continuation_token = decodeQueryParamString(query, "continuation-token");
        final String fetch_owner = decodeQueryParamString(query, "fetch-owner");
        xml_writer.writeStartDocument("UTF-8", "1.0");
        xml_writer.writeStartElement("ListBucketResult");
        xmlWriteBucketName(xml_writer, bucket);
        xmlWriteStartAfter(xml_writer, start_after); //ListBucketResult.StartAfter
        if (encoding_type != null) {
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
        byte[] result = stream.getStream().toByteArray();
        stream.setStream(null);
        return new ByteArrayInputStream(result);
    }

    private InputStream generateDocumentEnd(XMLStreamWriter xml_writer,
            OutputStreamProxy<ByteArrayOutputStream> stream) {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        stream.setStream(bytes);
        try {
            xml_writer.writeStartElement("IsTruncated");
            xml_writer.writeCharacters("false");
            xml_writer.writeEndElement();//ListBucketResult.IsTruncated
            xml_writer.writeStartElement("KeyCount");
            xml_writer.writeCharacters(2 + "");
            xml_writer.writeEndElement();//ListBucketResult.KeyCount
            //xmlWriteCommonPrefixes(xml_writer, delimiter);//ListBucketResult.CommonPrefixes
            xml_writer.writeEndElement();// ListBucketResult
            xml_writer.writeEndDocument();
            xml_writer.flush();
            xml_writer.close();
        } catch (XMLStreamException ex) {
            throw new RuntimeException("Unexpected!! Stringwriter closed.", ex);
        }
        return new ByteArrayInputStream(stream.getStream().toByteArray());
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
