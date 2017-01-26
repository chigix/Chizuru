package com.chigix.resserver.PostBucket;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.DeleteResource.Context;
import com.chigix.resserver.entity.error.NoSuchBucket;
import com.chigix.resserver.entity.error.NoSuchKey;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.router.HttpRouted;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class MultiDeleteHandler extends ChannelHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(MultiDeleteHandler.class.getName());

    private static final AttributeKey<Context> CONTEXT = AttributeKey.valueOf(UUID.randomUUID().toString());

    private final ApplicationContext application;

    public MultiDeleteHandler(ApplicationContext application) {
        this.application = application;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRouted) {
            Context routing_ctx = new Context();
            ctx.channel().attr(CONTEXT).set(routing_ctx);
            handleRoutedInfo(routing_ctx, (HttpRouted) msg);
            ReferenceCountUtil.release(msg);
            return;
        }
        if (msg instanceof HttpContent) {
            ctx.channel().attr(CONTEXT).get().appendXml(((HttpContent) msg).content().toString(CharsetUtil.UTF_8));
        }
        if (msg instanceof LastHttpContent) {
            response(ctx, ctx.channel().attr(CONTEXT).getAndRemove());
        } else {
            ReferenceCountUtil.release(msg);
        }
    }

    private void handleRoutedInfo(Context ctx, HttpRouted routed) throws NoSuchBucket {
        ctx.setBucket(application.BucketDao.findBucketByName((String) routed.decodedParams().get("bucketName")));
    }

    private void response(ChannelHandlerContext ctx, Context routing_ctx) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(routing_ctx.getXml().getBytes("UTF-8")));
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            LOG.error("UNEXPECTED", ex);
            throw new RuntimeException(ex);
        }
        NodeList nodes;
        try {
            nodes = (NodeList) xpath.compile("//Delete/Object").evaluate(doc, XPathConstants.NODESET);
        } catch (XPathExpressionException ex) {
            LOG.error("UNEXPECTED", ex);
            throw new RuntimeException(ex);
        }
        StringWriter response = new StringWriter();
        XMLStreamWriter writer;
        try {
            writer = XMLOutputFactory.newFactory().createXMLStreamWriter(response);
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeStartElement("DeleteResult");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node key_node = (Node) xpath.compile("./Key").evaluate(nodes.item(i), XPathConstants.NODE);
                if (key_node == null) {
                    continue;
                }
                try {
                    application.ResourceDao.removeResource(application.ResourceDao.findResource(routing_ctx.getBucket(), key_node.getTextContent()));
                } catch (NoSuchKey | NoSuchBucket ex) {
                    continue;
                }
                writer.writeStartElement("Deleted");
                writer.writeStartElement("Key");
                writer.writeCharacters(key_node.getTextContent());
                writer.writeEndElement();
                writer.writeEndElement(); // DeleteResult.Deleted
            }
            writer.writeEndElement();// DeleteResult
            writer.writeEndDocument();
        } catch (XMLStreamException | XPathExpressionException ex) {
            LOG.error("Unexpected", ex);
            throw new RuntimeException(ex);
        }
        try {
            ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                    Unpooled.copiedBuffer(response.toString().getBytes("UTF-8"))
            ));
        } catch (UnsupportedEncodingException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

}
