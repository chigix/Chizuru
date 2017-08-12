package com.chigix.resserver.endpoint.HeadResource;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.endpoint.GetResource.ContentRespHeaderBuildingHandler;
import com.chigix.resserver.sharablehandlers.ExtractGetResponseHandler;
import com.chigix.resserver.sharablehandlers.ResourceInfoHandler;
import com.chigix.resserver.sharablehandlers.ResourceRespEncoder;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.router.RoutingConfig;

/**
 * HEAD Object
 * http://docs.aws.amazon.com/AmazonS3/latest/API/RESTObjectHEAD.html
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class Routing extends RoutingConfig.HEAD {

    private final ApplicationContext application;

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
        pipeline.addLast(ResourceRespEncoder.getInstance(),
                ExtractGetResponseHandler.getInstance(),
                ResourceInfoHandler.getInstance(application),
                ContentRespHeaderBuildingHandler.getInstance(application),
                RespHeaderFixer.DEFAULT
        );
    }

}
