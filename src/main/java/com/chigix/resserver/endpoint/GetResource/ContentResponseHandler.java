package com.chigix.resserver.endpoint.GetResource;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.domain.AmassedResource;
import com.chigix.resserver.domain.ChunkedResource;
import com.chigix.resserver.domain.Resource;
import com.chigix.resserver.sharablehandlers.Context;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedStream;
import java.io.InputStream;
import java.security.InvalidParameterException;

/**
 * GET Resource
 * http://docs.aws.amazon.com/AmazonS3/latest/API/RESTObjectGET.html
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
public class ContentResponseHandler extends SimpleChannelInboundHandler<Context> {

    private static ContentResponseHandler instance = null;

    public static final ChannelHandler getInstance(ApplicationContext app) {
        if (instance == null) {
            instance = new ContentResponseHandler(app);
        }
        return instance;
    }

    private final ApplicationContext application;

    public ContentResponseHandler(ApplicationContext application) {
        this.application = application;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Context routing) throws Exception {
        ctx.write(new ChunkedStream(wrapToInputStream(routing.getResource())))
                .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
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
