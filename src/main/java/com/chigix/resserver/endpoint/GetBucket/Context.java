package com.chigix.resserver.endpoint.GetBucket;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.domain.Bucket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.router.HttpRouted;
import java.util.List;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Context {

    private final Bucket targetBucket;

    private final HttpRouted routedInfo;

    public Context(Bucket targetBucket, HttpRouted routedInfo) {
        this.targetBucket = targetBucket;
        this.routedInfo = routedInfo;
    }

    public Bucket getTargetBucket() {
        return targetBucket;
    }

    public HttpRouted getRoutedInfo() {
        return routedInfo;
    }

    @ChannelHandler.Sharable
    public static class Wrapper extends MessageToMessageDecoder<HttpRouted> {

        private final ApplicationContext application;

        private static Wrapper INSTANCE = null;

        private Wrapper(ApplicationContext application) {
            this.application = application;
        }

        public static final Wrapper getInstance(ApplicationContext application) {
            if (INSTANCE == null) {
                INSTANCE = new Wrapper(application);
            }
            return INSTANCE;
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, HttpRouted msg, List<Object> out) throws Exception {
            out.add(new Context(
                    application.getDaoFactory().getBucketDao()
                            .findBucketByName((String) msg.decodedParams().get("bucketName")),
                    msg));
        }
    }

}
