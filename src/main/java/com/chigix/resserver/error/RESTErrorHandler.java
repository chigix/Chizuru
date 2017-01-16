package com.chigix.resserver.error;

import com.chigix.resserver.ApplicationContext;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.router.HttpException;
import java.io.StringWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class RESTErrorHandler extends SimpleChannelInboundHandler<RESTError> {

    private static final Logger LOG = LoggerFactory.getLogger(RESTErrorHandler.class.getName());

    private static final XMLOutputFactory XML_FACTORY = XMLOutputFactory.newInstance();

    private final ApplicationContext application;

    public RESTErrorHandler(ApplicationContext application) {
        this.application = application;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, RESTError msg) throws Exception {
        StringWriter writer = new StringWriter();
        try {
            XMLStreamWriter xml_writer = XML_FACTORY.createXMLStreamWriter(writer);
            xml_writer.writeStartDocument("UTF-8", "1.0");
            xml_writer.writeStartElement("Error");
            xml_writer.writeStartElement("Code");
            xml_writer.writeCharacters(msg.getCode());
            xml_writer.writeEndElement();//Error.Code
            xml_writer.writeStartElement("Message");
            xml_writer.writeCharacters(msg.getMessage());
            xml_writer.writeEndElement();//Error.Message
            xml_writer.writeStartElement("RequestId");
            xml_writer.writeCharacters(msg.getHttpRequest().headers().get(application.getRequestIdHeaderName()).toString());
            xml_writer.writeEndElement();//Error.RequestId
            xml_writer.writeEndElement();//Error
            xml_writer.writeEndDocument();
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
        if (msg instanceof HttpException) {
            ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, ((HttpException) msg).getResponseCode(), Unpooled.copiedBuffer(writer.toString().getBytes())));
        } else {
            LOG.error("Not support Non- HttpException class implementing RESTError.");
        }
    }

}
