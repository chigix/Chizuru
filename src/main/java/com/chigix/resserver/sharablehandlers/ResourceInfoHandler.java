package com.chigix.resserver.sharablehandlers;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.domain.Bucket;
import com.chigix.resserver.domain.Resource;
import com.chigix.resserver.domain.error.NoSuchKey;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.router.HttpRouted;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
public class ResourceInfoHandler extends SimpleChannelInboundHandler<HttpRouted> {

    private static ResourceInfoHandler INSTANCE = null;

    private final ApplicationContext application;

    public static ResourceInfoHandler getInstance(ApplicationContext application) {
        if (INSTANCE == null) {
            INSTANCE = new ResourceInfoHandler(application);
        }
        return INSTANCE;
    }

    public ResourceInfoHandler(ApplicationContext ctx) {
        application = ctx;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, HttpRouted msg) throws Exception {
        Bucket b = application.getDaoFactory().getBucketDao().findBucketByName((String) msg.decodedParams().get("bucketName"));
        Resource r;
        try {
            r = application.getDaoFactory().getResourceDao().findResource(
                    b,
                    (String) msg.decodedParams().get("resource_key")
            );
        } catch (NoSuchKey noSuchKey) {
            r = new Context.UnpersistedResource(b, (String) msg.decodedParams().get("resource_key"));
        } catch (Exception ex) {
            msg.deny();
            throw ex;
        }
        Context routing_ctx = new Context(msg, r);
        ctx.fireChannelRead(routing_ctx);
    }

}
