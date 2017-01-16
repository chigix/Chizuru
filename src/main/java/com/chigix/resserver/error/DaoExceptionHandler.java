package com.chigix.resserver.error;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.entity.error.BucketInfo;
import com.chigix.resserver.entity.error.DaoException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.router.HttpException;
import io.netty.handler.codec.http.router.HttpExceptionInboundHandler;
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
@ChannelHandler.Sharable
public class DaoExceptionHandler extends HttpExceptionInboundHandler<DaoException> {

    private static DaoExceptionHandler instance = null;

    private final ApplicationContext application;

    private static final XMLOutputFactory XML_FACTORY = XMLOutputFactory.newInstance();
    private static final Logger LOG = LoggerFactory.getLogger(DaoExceptionHandler.class.getName());

    public static final DaoExceptionHandler getInstance(ApplicationContext application) {
        if (instance == null) {
            instance = new DaoExceptionHandler(application);
        }
        if (instance.application == application) {
            return instance;
        } else {
            return new DaoExceptionHandler(application);
        }
    }

    public DaoExceptionHandler(ApplicationContext application) {
        this.application = application;
    }

    @Override
    protected void handleException(ChannelHandlerContext ctx, HttpException exc) {
        DaoException daoexception = (DaoException) exc.getCause();
        switch (daoexception.getCode()) {
            case "NoSuchBucket":
                ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, generateXmlMessage(daoexception, exc.getHttpRequest())));
                return;
            case "BucketAlreadyOwnedByYou":
            case "BucketAlreadyExists":
                ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONFLICT, generateXmlMessage(daoexception, exc.getHttpRequest())));
                return;
        }
        LOG.error("DAOException: [{}] is not handled.", daoexception.getCode());
    }

    private ByteBuf generateXmlMessage(DaoException daoerror, HttpRequest request) {
        StringWriter writer = new StringWriter();
        try {
            XMLStreamWriter xml_writer = XML_FACTORY.createXMLStreamWriter(writer);
            xml_writer.writeStartDocument("UTF-8", "1.0");
            xml_writer.writeStartElement("Error");
            xml_writer.writeStartElement("Code");
            xml_writer.writeCharacters(daoerror.getCode());
            xml_writer.writeEndElement();//Error.Code
            xml_writer.writeStartElement("Message");
            xml_writer.writeCharacters(daoerror.getMessage());
            xml_writer.writeEndElement();//Error.Message
            if (daoerror instanceof BucketInfo) {
                writeBucketInfo(xml_writer, (BucketInfo) daoerror);
            }
            xml_writer.writeStartElement("RequestId");
            xml_writer.writeCharacters(request.headers().get(application.getRequestIdHeaderName()).toString());
            xml_writer.writeEndElement();//Error.RequestId
            xml_writer.writeEndElement();//Error
            xml_writer.writeEndDocument();
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
        return Unpooled.copiedBuffer(writer.toString().getBytes());
    }

    private void writeBucketInfo(XMLStreamWriter writer, BucketInfo info) throws XMLStreamException {
        writer.writeStartElement("BucketName");
        writer.writeCharacters(info.getBucketName());
        writer.writeEndElement();
    }

}
