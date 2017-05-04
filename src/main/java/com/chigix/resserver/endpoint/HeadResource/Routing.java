package com.chigix.resserver.endpoint.HeadResource;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.sharablehandlers.CloseChannelInReadHandler;
import com.chigix.resserver.sharablehandlers.Context;
import com.chigix.resserver.sharablehandlers.ResourceInfoHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.router.RoutingConfig;
import io.netty.util.AttributeKey;
import java.util.UUID;

/**
 * HEAD Object
 * http://docs.aws.amazon.com/AmazonS3/latest/API/RESTObjectHEAD.html
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Routing extends RoutingConfig.HEAD {

    private final ApplicationContext application;

    private static final AttributeKey<Context> RESOURCE_CTX = AttributeKey.newInstance(UUID.randomUUID().toString());

    public Routing(ApplicationContext application) {
        this.application = application;
    }

    @Override
    public String configureRoutingName() {
        return "HEAD_RESOURCE";
    }

    @Override
    public String configurePath() {
        return "/:bucketName/:resource_key";
    }

    @Override
    public void configurePipeline(ChannelPipeline pipeline) {
        pipeline.addLast(ResourceInfoHandler.getInstance(application),
                RespHeaderFixer.DEFAULT,
                new ChannelHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof LastHttpContent) {
                    ctx.fireChannelRead(ctx.attr(RESOURCE_CTX).getAndRemove());
                    return;
                } else if (msg instanceof Context) {
                    ctx.attr(RESOURCE_CTX).set((Context) msg);
                    return;
                }
                super.channelRead(ctx, msg);
            }

        },
                HeadResponseHandler.getInstance(application),
                CloseChannelInReadHandler.DEFAULT
        );
    }

}
