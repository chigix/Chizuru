package com.chigix.resserver.endpoint.GetResource;

import com.chigix.resserver.config.ApplicationContext;
import com.chigix.resserver.domain.Lifecycle;
import com.chigix.resserver.domain.error.NoSuchKey;
import com.chigix.resserver.error.InvalidRange;
import com.chigix.resserver.application.Context;
import com.chigix.resserver.interfaces.handling.http.HttpHeaderNames;
import com.chigix.resserver.interfaces.handling.http.HttpHeaderUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.math.BigInteger;
import java.text.MessageFormat;
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
        if (application.getEntityManager().getEntityState(routing.getResource()) == Lifecycle.NEW) {
            routing.getRoutedInfo().deny();
            throw new NoSuchKey(routing.getResource().getKey());
        }
        routing.getRoutedInfo().allow();
        final ResourceContext resource_routing;
        try {
            resource_routing = new ResourceContext(routing);
        } catch (HttpHeaderUtil.InvalidRangeHeader invalidRangeHeader) {
            throw new InvalidRange(routing.getRoutedInfo(),
                    invalidRangeHeader.getRawRangeHeader(), routing.getResource().getSize());
        }
        HttpResponse resp = resource_routing.getResourceResp();
        routing.getResource().snapshotMetaData().forEach((name, value) -> {
            resp.headers().set(name, value);
        });
        resp.headers().set(HttpHeaderNames.CONTENT_LENGTH, routing.getResource().getSize());
        resp.headers().set(HttpHeaderNames.LAST_MODIFIED, routing.getResource().getLastModified().toString("E, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US));
        resp.headers().set(HttpHeaderNames.ETAG, "\"" + routing.getResource().getETag() + "\"");
        resp.headers().set(HttpHeaderNames.ACCEPT_RANGES, HttpHeaderValues.BYTES);
        if (resource_routing.getRange() != null) {
            resp.setStatus(HttpResponseStatus.PARTIAL_CONTENT);
            resp.headers().set(HttpHeaderNames.CONTENT_RANGE,
                    MessageFormat.format("bytes {0}-{1}/{2}",
                            resource_routing.getRange().start,
                            resource_routing.getRange().end,
                            resource_routing.getResource().getSize()));
            resp.headers().set(HttpHeaderNames.CONTENT_LENGTH,
                    new BigInteger(resource_routing.getRange().end)
                            .subtract(new BigInteger(resource_routing.getRange().start))
                            .add(BigInteger.ONE).toString());
        }
        // resp.headers().set("x-amz-version-id", msg.getResource().getVersionId());
        ctx.fireChannelRead(resource_routing);
    }

}
