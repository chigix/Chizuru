package com.chigix.resserver.endpoint.GetResource;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.domain.error.NoSuchKey;
import com.chigix.resserver.sharablehandlers.Context;
import com.chigix.resserver.util.HttpHeaderNames;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpResponse;
import java.util.Locale;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
public class ContentRespHeaderBuildingHandler extends SimpleChannelInboundHandler<Context> {

    private static ContentRespHeaderBuildingHandler instance = null;

    private final ApplicationContext application;

    public static final ContentRespHeaderBuildingHandler getInstance(ApplicationContext app) {
        if (instance == null) {
            instance = new ContentRespHeaderBuildingHandler(app);
        }
        return instance;
    }

    public ContentRespHeaderBuildingHandler(ApplicationContext application) {
        this.application = application;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Context routing) throws Exception {
        if (routing.getResource() instanceof Context.UnpersistedResource) {
            routing.getRoutedInfo().deny();
            throw new NoSuchKey(routing.getResource().getKey());
        }
        routing.getRoutedInfo().allow();
        HttpResponse resp = routing.getResourceResp();
        routing.getResource().snapshotMetaData().forEach((name, value) -> {
            resp.headers().set(name, value);
        });
        resp.headers().set(HttpHeaderNames.CONTENT_LENGTH, routing.getResource().getSize());
        resp.headers().set(HttpHeaderNames.LAST_MODIFIED, routing.getResource().getLastModified().toString("E, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US));
        // resp.headers().set("x-amz-version-id", msg.getResource().getVersionId());
        ctx.fireChannelRead(routing);
    }

}
