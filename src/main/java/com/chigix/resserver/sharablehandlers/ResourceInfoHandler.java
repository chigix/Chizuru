package com.chigix.resserver.sharablehandlers;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.entity.Bucket;
import com.chigix.resserver.entity.Resource;
import com.chigix.resserver.entity.error.DaoException;
import com.chigix.resserver.entity.error.NoSuchBucket;
import com.chigix.resserver.entity.error.NoSuchKey;
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
        Bucket b = application.BucketDao.findBucketByName((String) msg.decodedParams().get("bucketName"));
        Resource r;
        try {
            r = application.ResourceDao.findResource(
                    b.getName(),
                    (String) msg.decodedParams().get("resource_key")
            );
        } catch (NoSuchKey noSuchKey) {
            r = fixUnexistedResource(new Resource(b, (String) msg.decodedParams().get("resource_key")), msg, b);
        } catch (Exception ex) {
            msg.deny();
            throw ex;
        }
        Context routing_ctx = new Context(msg, r);
        msg.allow();
        ctx.fireChannelRead(routing_ctx);
    }

    protected Resource fixUnexistedResource(Resource r, HttpRouted routed_info, Bucket bucket) throws NoSuchKey, DaoException {
        try {
            return application.ResourceDao.saveResource(r);
        } catch (NoSuchBucket ex) {
            throw new RuntimeException(ex);
        }
    }

}
