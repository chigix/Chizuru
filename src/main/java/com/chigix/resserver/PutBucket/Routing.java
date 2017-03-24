package com.chigix.resserver.PutBucket;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.entity.Bucket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.router.HttpRouted;
import io.netty.handler.codec.http.router.RoutingConfig;
import io.netty.handler.routing.DefaultExceptionForwarder;

/**
 * Creates a new bucket.
 * http://docs.aws.amazon.com/AmazonS3/latest/API/RESTBucketPUT.html
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Routing extends RoutingConfig.PUT {

    private final ApplicationContext application;

    public Routing(ApplicationContext application) {
        this.application = application;
    }

    @Override
    public String configureRoutingName() {
        return "PUT_BUCKET";
    }

    @Override
    public String configurePath() {
        return "/:bucketName";
    }

    @Override
    public void configurePipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new SimpleChannelInboundHandler<HttpRouted>() {
            @Override
            protected void messageReceived(ChannelHandlerContext ctx, HttpRouted msg) throws Exception {
                msg.allow();
                Bucket bucket = application.getDaoFactory().getBucketDao().createBucket((String) msg.decodedParams().get("bucketName"));
                DefaultFullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                resp.headers().add(HttpHeaderNames.LOCATION, "/" + bucket.getName());
                ctx.writeAndFlush(resp);
            }

        }).addLast(new DefaultExceptionForwarder());
    }

}
