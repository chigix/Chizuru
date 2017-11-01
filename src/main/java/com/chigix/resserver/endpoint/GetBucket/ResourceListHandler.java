package com.chigix.resserver.endpoint.GetBucket;

import com.chigix.resserver.config.ApplicationContext;
import com.chigix.resserver.domain.model.bucket.Bucket;
import com.chigix.resserver.domain.model.resource.Resource;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.interfaces.handling.http.HttpHeaderNames;
import com.chigix.resserver.interfaces.handling.http.HttpHeaderUtil;
import com.chigix.resserver.interfaces.io.InputStreamProxy;
import com.chigix.resserver.interfaces.io.IteratorInputStream;
import com.chigix.resserver.interfaces.io.OutputStreamProxy;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.zip.GZIPOutputStream;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.io.input.ClosedInputStream;

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
        final OutputStreamProxy<ByteArrayOutputStream> byte_charging = new OutputStreamProxy<>();
        byte_charging.setStream(new ByteArrayOutputStream());
        final XMLStreamWriter xml_writer;
        if (isGzip) {
            resp.headers().set(HttpHeaderNames.CONTENT_ENCODING, HttpHeaderValues.GZIP);
            xml_writer = XML_FACTORY.createXMLStreamWriter(new GZIPOutputStream(byte_charging, true), "UTF-8");
        } else {
            xml_writer = XML_FACTORY.createXMLStreamWriter(byte_charging, "UTF-8");
        }
        final ListResponseContext listCtx = buildListResponseContext(
                new QueryStringDecoder(route_ctx.getRoutedInfo().getRequestMsg().uri()),
                route_ctx.getTargetBucket());
        InputStream result = new SequenceInputStream(
                new InputStreamProxy() {
            @Override
            public int read() throws IOException {
                try {
                    return super.read();
                } catch (NullPointerException nullPointerException) {
                    if (getStream() == null) {
                        try {
                            generateDocumentStart(xml_writer, listCtx);
                            xml_writer.flush();
                            setStream(new ByteArrayInputStream(byte_charging.getStream().toByteArray()));
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
                new SequenceInputStream(new InputStreamProxy() {
                    @Override
                    public int read() throws IOException {
                        try {
                            return super.read();
                        } catch (NullPointerException e) {
                            if (getStream() != null) {
                                throw e;
                            }
                        }
                        final Iterator<Resource> resources;
                        try {
                            if (listCtx.continuationToken == null) {
                                resources = application.getEntityManager().getResourceRepository().listResources(route_ctx.getTargetBucket(), listCtx.maxKeys + 1);
                            } else {
                                resources = application.getEntityManager().getResourceRepository().listResources(route_ctx.getTargetBucket(), listCtx.nextContinuationToken, listCtx.maxKeys + 1);
                            }
                        } catch (NoSuchBucket ex) {
                            setStream(new ClosedInputStream());
                            return super.read();
                        }
                        setStream(new IteratorInputStream<Resource>(resources) {
                            @Override
                            protected InputStream inputStreamProvider(Resource item) throws NoSuchElementException {
                                if (listCtx.keyCountTotal >= listCtx.maxKeys) {
                                    listCtx.isTruncated = true;
                                    listCtx.nextContinuationToken = item.getVersionId();
                                    return new ClosedInputStream();
                                }
                                byte_charging.setStream(new ByteArrayOutputStream());
                                try {
                                    xmlWriteResourceContent(xml_writer, item, listCtx);
                                } catch (XMLStreamException ex) {
                                    throw new RuntimeException("Unexpected!! stringwriter closed", ex);
                                }
                                return new ByteArrayInputStream(byte_charging.getStream().toByteArray());
                            }
                        });
                        return super.read();
                    }

                }, new InputStreamProxy() {
                    @Override
                    public int read() throws IOException {
                        try {
                            return super.read();
                        } catch (NullPointerException nullPointerException) {
                            if (getStream() == null) {
                                byte_charging.setStream(new ByteArrayOutputStream());
                                try {
                                    generateDocumentEnd(xml_writer, listCtx);
                                    xml_writer.flush();
                                } catch (XMLStreamException ex) {
                                    throw new RuntimeException("Unexpected!!!XMLException", ex);
                                }
                                setStream(new ByteArrayInputStream(byte_charging.getStream().toByteArray()));
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

    private ListResponseContext buildListResponseContext(QueryStringDecoder query, Bucket bucket) {
        ListResponseContext resp = new ListResponseContext(bucket);
        resp.delimiter = decodeQueryParamString(query, "delimiter");
        resp.startAfter = decodeQueryParamString(query, "start-after");
        resp.encodingType = decodeQueryParamString(query, "encoding-type");
        resp.prefix = decodeQueryParamString(query, "prefix");
        String max_keys = decodeQueryParamString(query, "max-keys");
        if (max_keys != null) {
            // @TODO Check exception for invalid format of integer number.
            int max_keys_int = Integer.valueOf(max_keys);
            resp.maxKeys = max_keys_int;
        }
        resp.continuationToken = decodeQueryParamString(query, "continuation-token");
        if ("true".equalsIgnoreCase(decodeQueryParamString(query, "fetch-owner"))) {
            resp.fetchOwner = true;
        }
        return resp;
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

    private void generateDocumentStart(XMLStreamWriter xml_writer,
            ListResponseContext resp_ctx) throws XMLStreamException {
        xml_writer.writeStartDocument("UTF-8", "1.0");
        xml_writer.writeStartElement("ListBucketResult");
        XMLResponseUtil.xmlWriteBucketName(xml_writer, resp_ctx.bucket);
        XMLResponseUtil.xmlWriteStartAfter(xml_writer, resp_ctx.startAfter); //ListBucketResult.StartAfter
        if (resp_ctx.encodingType != null) {
            xml_writer.writeStartElement("Encoding-Type");
            xml_writer.writeCharacters(resp_ctx.encodingType);
            xml_writer.writeEndElement();
        }
        xml_writer.writeStartElement("Prefix");
        if (resp_ctx.prefix != null) {
            xml_writer.writeCharacters(resp_ctx.prefix);
        }
        xml_writer.writeEndElement();//ListBucketResult.Prefix
        if (resp_ctx.continuationToken != null) {
            xml_writer.writeStartElement("ContinuationToken");
            xml_writer.writeCharacters(resp_ctx.continuationToken);
            xml_writer.writeEndElement();
        }
        XMLResponseUtil.xmlWriteMaxKeys(xml_writer, "" + resp_ctx.maxKeys);//ListBucketResult.MaxKeys
        if (resp_ctx.delimiter != null) {
            xml_writer.writeStartElement("Delimiter");
            xml_writer.writeCharacters(resp_ctx.delimiter);
            xml_writer.writeEndElement();//ListBucketResult.Delimiter
        }
    }

    private void generateDocumentEnd(XMLStreamWriter xml_writer, ListResponseContext resp) throws XMLStreamException {
        if (resp.nextContinuationToken != null) {
            xml_writer.writeStartElement("NextContinuationToken");
            xml_writer.writeCharacters(resp.nextContinuationToken);
            xml_writer.writeEndElement();
        }
        xml_writer.writeStartElement("IsTruncated");
        xml_writer.writeCharacters(resp.isTruncated ? "true" : "false");
        xml_writer.writeEndElement();//ListBucketResult.IsTruncated
        xml_writer.writeStartElement("KeyCount");
        xml_writer.writeCharacters(resp.keyCountTotal - resp.keyCountUnderCommonPrefixes + resp.commonPrefixes.size() + "");
        xml_writer.writeEndElement();//ListBucketResult.KeyCount
        if (resp.keyCountUnderCommonPrefixes > 0) {
            XMLResponseUtil.xmlWriteCommonPrefixes(xml_writer, resp.commonPrefixes.keySet().iterator());//ListBucketResult.CommonPrefixes
        }
        xml_writer.writeEndElement();// ListBucketResult
        xml_writer.writeEndDocument();
        xml_writer.flush();
        xml_writer.close();
    }

    private void xmlWriteResourceContent(XMLStreamWriter writer, Resource res, ListResponseContext resp_ctx) throws XMLStreamException {
        resp_ctx.keyCountTotal++;
        String commonPrefix = Util.extractCommonPrefix(res.getKey(), resp_ctx.prefix, resp_ctx.delimiter);
        if (commonPrefix != null) {
            resp_ctx.keyCountUnderCommonPrefixes++;
            resp_ctx.commonPrefixes.putIfAbsent(commonPrefix, true);
            return;
        }
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
        if (resp_ctx.fetchOwner) {
            writer.writeStartElement("Owner");
            writer.writeStartElement("DisplayName");
            writer.writeCharacters("CHIGIX");
            writer.writeEndElement();//Owner.DisplayName 
            writer.writeStartElement("ID");
            writer.writeCharacters("CHIZURU");
            writer.writeEndElement();//Owner.ID 
            writer.writeEndElement();//Owner
        }
        writer.writeEndElement();
    }

    private static class XMLResponseUtil {

        public static final void xmlWriteBucketName(XMLStreamWriter writer, Bucket bucket) throws XMLStreamException {
            writer.writeStartElement("Name");
            writer.writeCharacters(bucket.getName());
            writer.writeEndElement();
        }

        public static final void xmlWriteStartAfter(XMLStreamWriter writer, String start_after) throws XMLStreamException {
            if (start_after == null) {
                return;
            }
            writer.writeStartElement("StartAfter");
            writer.writeCharacters(start_after);
            writer.writeEndElement();
        }

        public static final void xmlWriteCommonPrefixes(XMLStreamWriter writer, Iterator<String> prefixes) throws XMLStreamException {
            while (prefixes.hasNext()) {
                String next = prefixes.next();
                writer.writeStartElement("CommonPrefixes");
                writer.writeStartElement("Prefix");
                writer.writeCharacters(next);
                writer.writeEndElement();
                writer.writeEndElement();
            }
        }

        public static final void xmlWriteMaxKeys(XMLStreamWriter writer, String paramMaxKeys) throws XMLStreamException {
            writer.writeStartElement("MaxKeys");
            if (paramMaxKeys != null && Integer.valueOf(paramMaxKeys) < 1000) {
                writer.writeCharacters(paramMaxKeys);
            } else {
                writer.writeCharacters("1000");
            }
            writer.writeEndElement();//ListBucketResult.MaxKeys
        }
    }

    private static class ListResponseContext {

        String delimiter = null;
        String encodingType = null;
        int maxKeys = 1000;
        int keyCountTotal = 0;
        int keyCountUnderCommonPrefixes = 0;
        String prefix = null;
        final String listType = "2";
        String continuationToken = null;
        String nextContinuationToken = null;
        boolean fetchOwner = false;
        String startAfter = null;
        HashMap<String, Boolean> commonPrefixes = new HashMap<>();
        String ownerDisplayName = null;
        String ownerId = null;
        boolean isTruncated = false;
        final Bucket bucket;

        public ListResponseContext(Bucket bucket) {
            this.bucket = bucket;
        }

    }

}
