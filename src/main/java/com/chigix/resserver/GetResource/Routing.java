package com.chigix.resserver.GetResource;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.entity.AmassedResource;
import com.chigix.resserver.entity.ChunkedResource;
import com.chigix.resserver.entity.error.NoSuchKey;
import com.chigix.resserver.sharablehandlers.Context;
import com.chigix.resserver.sharablehandlers.ResourceInfoHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.router.RoutingConfig;
import io.netty.handler.routing.DefaultExceptionForwarder;
import io.netty.handler.stream.ChunkedStream;
import io.netty.handler.stream.ChunkedWriteHandler;
import java.util.Locale;

/**
 * GET Resource
 * http://docs.aws.amazon.com/AmazonS3/latest/API/RESTObjectGET.html
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Routing extends RoutingConfig.GET {

    private final ApplicationContext application;

    public Routing(ApplicationContext application) {
        this.application = application;
    }

    @Override
    public String configureRoutingName() {
        return "GET_RESOURCE";
    }

    @Override
    public String configurePath() {
        return "/:bucketName/:resource_key";
    }

    @Override
    public void configurePipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new ChunkedWriteHandler(),
                ResourceInfoHandler.getInstance(application),
                new SimpleChannelInboundHandler<Context>() {
            @Override
            protected void messageReceived(ChannelHandlerContext ctx, Context msg) throws Exception {
                if (msg.getResource() instanceof Context.UnpersistedResource) {
                    msg.getRoutedInfo().deny();
                    throw new NoSuchKey(msg.getResource().getKey());
                }
                msg.getRoutedInfo().allow();
                DefaultHttpResponse resp = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                msg.getResource().snapshotMetaData().forEach((name, value) -> {
                    resp.headers().set(name, value);
                });
                resp.headers().set(HttpHeaderNames.CONTENT_LENGTH, msg.getResource().getSize());
                resp.headers().set(HttpHeaderNames.LAST_MODIFIED, msg.getResource().getLastModified().toString("E, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US));
                // resp.headers().set("x-amz-version-id", msg.getResource().getVersionId());
                ctx.write(resp);
                // @TODO: Check Gzip and support GZIP.
                if (msg.getResource() instanceof ChunkedResource) {
                    ctx.write(new ChunkedStream(new ResourceInputStream(
                            ((ChunkedResource) msg.getResource()).getChunks(),
                            application))).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                } else if (msg.getResource() instanceof AmassedResource) {
                    ctx.write(new ChunkedStream(new AmassedInputStream(
                            ((AmassedResource) msg.getResource()).getSubResources(),
                            application))).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                }
                ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);
            }
        }, new DefaultExceptionForwarder());
    }

}
