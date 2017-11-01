package com.chigix.resserver.application;

import com.chigix.resserver.config.ApplicationContext;
import com.chigix.resserver.domain.model.bucket.Bucket;
import com.chigix.resserver.domain.model.resource.Resource;
import com.chigix.resserver.domain.error.NoSuchBucket;
import com.chigix.resserver.domain.error.NoSuchKey;
import static com.chigix.resserver.application.ResourceInfoHandler.ROUTING_PATH_PARAM.BUCKET_NAME;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.router.HttpRouted;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
@ChannelHandler.Sharable
public class ResourceInfoHandler extends SimpleChannelInboundHandler<HttpRouted> {

    public static final String ROUTING_PATH = "/:" + ROUTING_PATH_PARAM.BUCKET_NAME + "/:" + ROUTING_PATH_PARAM.RESOURCE_KEY;

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
        final Bucket b = application.getEntityManager().getBucketRepository().findBucketByName((String) msg.decodedParams().get(BUCKET_NAME));
        Resource r;
        try {
            r = application.getEntityManager().getResourceRepository().findResource(
                    b,
                    QueryStringDecoder.decodeComponent((String) msg.decodedParams().get(ROUTING_PATH_PARAM.RESOURCE_KEY))
            );
        } catch (NoSuchKey noSuchKey) {
            r = new Resource(QueryStringDecoder.decodeComponent((String) msg.decodedParams().get(ROUTING_PATH_PARAM.RESOURCE_KEY))) {
                @Override
                public Bucket getBucket() throws NoSuchBucket {
                    return b;
                }
            };
        } catch (NoSuchBucket ex) {
            msg.deny();
            throw ex;
        } catch (Exception ex) {
            msg.deny();
            throw ex;
        }
        Context routing_ctx = new Context(msg, r);
        ctx.fireChannelRead(routing_ctx);
    }

    public static class ROUTING_PATH_PARAM {

        public static final String BUCKET_NAME = "bucketName";
        public static final String RESOURCE_KEY = "*";
    }

}
