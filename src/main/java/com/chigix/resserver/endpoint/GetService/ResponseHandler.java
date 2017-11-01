package com.chigix.resserver.endpoint.GetService;

import com.chigix.resserver.config.ApplicationContext;
import com.chigix.resserver.domain.model.bucket.Bucket;
import com.chigix.resserver.interfaces.handling.http.HttpHeaderNames;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedStream;
import io.netty.util.CharsetUtil;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Writer;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import org.joda.time.format.ISODateTimeFormat;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ResponseHandler extends SimpleChannelInboundHandler<LastHttpContent> {

    private final ApplicationContext application;

    public ResponseHandler(ApplicationContext ctx) {
        super();
        application = ctx;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, LastHttpContent msg) throws Exception {
        DefaultHttpResponse resp = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/xml");
        resp.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
        ctx.write(resp);
        PipedInputStream inputstream = new PipedInputStream();
        ctx.write(new HttpChunkedInput(new ChunkedStream(inputstream)));
        try (PipedOutputStream outputstream = new PipedOutputStream(inputstream)) {
            Writer outputWriter = new OutputStreamWriter(outputstream, CharsetUtil.UTF_8);
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(outputWriter);
            xmlWriter.writeStartDocument("UTF-8", "1.0");
            xmlWriter.writeStartElement("ListAllMyBucketsResult");
            xmlWriter.writeStartElement("Owner");
            xmlWriter.writeStartElement("ID");
            xmlWriter.writeCharacters("CHIZURU");
            xmlWriter.writeEndElement();
            xmlWriter.writeStartElement("DisplayName");
            xmlWriter.writeCharacters("CHIGIX");
            xmlWriter.writeEndElement();// DisplayName
            xmlWriter.writeEndElement();// Owner
            xmlWriter.writeStartElement("Buckets");
            Iterator<Bucket> it = application.getEntityManager().getBucketRepository().iteratorBucket();
            while (it.hasNext()) {
                Bucket next;
                try {
                    next = it.next();
                } catch (NoSuchElementException e) {
                    break;
                }
                xmlWriter.writeStartElement("Bucket");
                xmlWriter.writeStartElement("Name");
                xmlWriter.writeCharacters(next.getName());
                xmlWriter.writeEndElement();// Name
                xmlWriter.writeStartElement("CreationDate");
                xmlWriter.writeCharacters(
                        ISODateTimeFormat.dateTime().print(next.getCreationTime())
                );
                xmlWriter.writeEndElement();// CreationDate
                xmlWriter.writeEndElement();// Bucket
            }
            xmlWriter.writeEndElement();// Buckets
            xmlWriter.writeEndElement();// ListAllMyBucketsResult
            xmlWriter.writeEndDocument();
            outputWriter.flush();
        }
        ctx.flush();
    }

}
