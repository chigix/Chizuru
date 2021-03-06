package com.chigix.resserver.endpoint.GetResource;

import com.chigix.resserver.config.ApplicationContext;
import com.chigix.resserver.domain.model.resource.AmassedResource;
import com.chigix.resserver.domain.model.resource.ChunkedResource;
import com.chigix.resserver.domain.model.resource.Resource;
import com.chigix.resserver.interfaces.handling.http.HttpHeaderNames;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import org.apache.commons.io.input.BoundedInputStream;

/**
 * GET Resource
 * http://docs.aws.amazon.com/AmazonS3/latest/API/RESTObjectGET.html
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
public class ContentStreamingHandler extends SimpleChannelInboundHandler<ResourceContext> {

    private static ContentStreamingHandler instance = null;

    public static final ChannelHandler getInstance(ApplicationContext app) {
        if (instance == null) {
            instance = new ContentStreamingHandler(app);
        }
        return instance;
    }

    private final ApplicationContext application;

    public ContentStreamingHandler(ApplicationContext application) {
        this.application = application;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, ResourceContext routing) throws Exception {
        ctx.writeAndFlush(routing);
        InputStream in = wrapToInputStream(routing.getResource());
        if (routing.getResourceResp().status().equals(HttpResponseStatus.PARTIAL_CONTENT)
                && routing.getRange() != null) {
            in.skip(BigInteger.ZERO.max(new BigInteger(routing.getRange().start)).longValue());
            in = new BoundedInputStream(in,
                    routing.getResourceResp().headers().getLong(HttpHeaderNames.CONTENT_LENGTH));
        }
        ctx.write(new ChunkedStream(in)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);
    }

    private InputStream wrapToInputStream(Resource r) {
        if (r instanceof ChunkedResource) {
            return new ResourceInputStream(((ChunkedResource) r).getChunks(), application);
        } else if (r instanceof AmassedResource) {
            return new AmassedInputStream(((AmassedResource) r).getSubResources(), application);
        }
        throw new InvalidParameterException("Unexpected Resource was the parameter, with unknown ResourceType: ["
                + r.getClass() + ":" + r.getKey()
                + "]");
    }

}
