package com.chigix.resserver.GetService;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedStream;
import io.netty.util.CharsetUtil;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ResponseHandler extends SimpleChannelInboundHandler<LastHttpContent> {

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, LastHttpContent msg) throws Exception {
        PipedInputStream inputstream = new PipedInputStream();
        PipedOutputStream outputstream = new PipedOutputStream(inputstream);
        Writer outputWriter = new OutputStreamWriter(outputstream, CharsetUtil.UTF_8);
        DateTime dt = new DateTime(DateTimeZone.forID("GMT"));
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlWriter = factory.createXMLStreamWriter(outputWriter);
        xmlWriter.writeStartDocument("UTF-8", "1.0");
        xmlWriter.writeStartDocument();
        xmlWriter.writeStartElement("ListAllMyBucketsResult");
        xmlWriter.writeStartElement("Owner");
        xmlWriter.writeStartElement("ID");
        xmlWriter.writeCharacters("BANKAI");
        xmlWriter.writeEndElement();
        xmlWriter.writeStartElement("DisplayName");
        xmlWriter.writeCharacters("CHIGIX");
        xmlWriter.writeEndElement();// DisplayName
        xmlWriter.writeEndElement();// Owner
        xmlWriter.writeStartElement("Buckets");
        xmlWriter.writeStartElement("Bucket");
        xmlWriter.writeStartElement("Name");
        xmlWriter.writeCharacters("oos-for-learning");
        xmlWriter.writeEndElement();// Name
        xmlWriter.writeStartElement("CreationDate");
        xmlWriter.writeCharacters(ISODateTimeFormat.dateTime().print(dt));
        xmlWriter.writeEndElement();// CreationDate
        xmlWriter.writeEndElement();// Bucket
        xmlWriter.writeEndElement();// Buckets
        xmlWriter.writeEndElement();// ListAllMyBucketsResult
        xmlWriter.writeEndDocument();
        outputWriter.flush();
        outputstream.close();
        DefaultHttpResponse resp = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/xml");
        resp.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
        ctx.write(resp);
        ctx.write(new HttpChunkedInput(new ChunkedStream(inputstream)));
        ctx.flush();
    }

}
