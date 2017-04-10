package com.chigix.resserver.endpoint.GetResource;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.domain.AmassedResource;
import com.chigix.resserver.domain.ChunkedResource;
import com.chigix.resserver.domain.Resource;
import com.chigix.resserver.domain.error.NoSuchKey;
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
import io.netty.util.AttributeKey;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.Locale;
import java.util.UUID;

/**
 * GET Resource
 * http://docs.aws.amazon.com/AmazonS3/latest/API/RESTObjectGET.html
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Routing extends RoutingConfig.GET {

    private final ApplicationContext application;

    private static final AttributeKey<Context> ROUTING_CTX = AttributeKey.newInstance(UUID.randomUUID().toString());

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
                ctx.attr(ROUTING_CTX).set(msg);
            }
        }, new SimpleChannelInboundHandler<LastHttpContent>() {
            @Override
            protected void messageReceived(ChannelHandlerContext ctx, LastHttpContent msg) throws Exception {
                Context routing = ctx.attr(ROUTING_CTX).getAndRemove();
                if (routing.getResource() instanceof Context.UnpersistedResource) {
                    routing.getRoutedInfo().deny();
                    throw new NoSuchKey(routing.getResource().getKey());
                }
                routing.getRoutedInfo().allow();
                DefaultHttpResponse resp = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                routing.getResource().snapshotMetaData().forEach((name, value) -> {
                    resp.headers().set(name, value);
                });
                resp.headers().set(HttpHeaderNames.CONTENT_LENGTH, routing.getResource().getSize());
                resp.headers().set(HttpHeaderNames.LAST_MODIFIED, routing.getResource().getLastModified().toString("E, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US));
                // resp.headers().set("x-amz-version-id", msg.getResource().getVersionId());
                ctx.write(resp);
                ctx.write(new ChunkedStream(wrapToInputStream(routing.getResource())))
                        .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);
            }
        }, new DefaultExceptionForwarder());
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
