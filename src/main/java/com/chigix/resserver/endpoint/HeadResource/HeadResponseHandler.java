package com.chigix.resserver.endpoint.HeadResource;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.domain.error.NoSuchKey;
import com.chigix.resserver.sharablehandlers.Context;
import com.chigix.resserver.util.HttpHeaderNames;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.Locale;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
public class HeadResponseHandler extends SimpleChannelInboundHandler<Context> {

    private final ApplicationContext application;

    private static HeadResponseHandler handler = null;

    public static final ChannelHandler getInstance(ApplicationContext application) {
        if (handler == null) {
            handler = new HeadResponseHandler(application);
        }
        return handler;
    }

    public HeadResponseHandler(ApplicationContext application) {
        this.application = application;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Context routing) throws Exception {
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
        ctx.fireChannelRead(routing);
    }

}
