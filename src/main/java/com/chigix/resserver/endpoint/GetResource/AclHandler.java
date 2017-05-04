package com.chigix.resserver.endpoint.GetResource;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.sharablehandlers.Context;
import com.chigix.resserver.util.HttpHeaderNames;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
 * GET Object ACL
 * http://docs.aws.amazon.com/AmazonS3/latest/API/RESTObjectGETacl.html
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
public class AclHandler extends SimpleChannelInboundHandler<Context> {

    private static AclHandler instance;

    public static final ChannelHandler getInstance(ApplicationContext app) {
        if (instance == null) {
            instance = new AclHandler(app);
        }
        return instance;
    }

    private final ApplicationContext application;

    public AclHandler(ApplicationContext application) {
        this.application = application;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Context msg) throws Exception {
        StringBuilder sb = new StringBuilder("<AccessControlPolicy>");
        sb.append("<Owner>");
        sb.append("<ID>").append("Chizuru").append("</ID>");
        sb.append("<DisplayName>").append("CHIGIX").append("</DisplayName>");
        sb.append("</Owner>");
        sb.append("<AccessControlList><Grant>")
                .append("<Grantee xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"CanonicalUser\">")
                .append("<ID>Chizuru</ID>")
                .append("<DisplayName>CHIGIX</DisplayName>")
                .append("</Grantee>")
                .append("<Permission>FULL_CONTROL</Permission></Grant></AccessControlList>");
        sb.append("</AccessControlPolicy>");
        DefaultFullHttpResponse resp = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.copiedBuffer(sb.toString().getBytes(CharsetUtil.UTF_8)));
        resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/xml");
        ctx.writeAndFlush(resp);
    }

}
