package com.chigix.resserver.GetBucket;

import com.chigix.resserver.entity.Bucket;
import io.netty.handler.codec.http.router.HttpRouted;

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

}
