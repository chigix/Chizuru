package com.chigix.resserver.config;

import io.netty.handler.codec.http.router.HttpRouted;
import java.io.File;
import org.joda.time.DateTime;
import com.chigix.resserver.domain.EntityManager;
import io.netty.channel.ChannelHandler;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface ApplicationContext {

    String getChizuruVersion();

    File getChunksDir();

    DateTime getCreationDate();

    String getCurrentNodeId();

    int getMaxChunkSize();

    int getTransferBufferSize();

    String getRequestIdHeaderName();

    EntityManager getEntityManager();

    void addNode(String nodeId, String nodeIPAddress);

    void finishRequest(HttpRouted routed_info);

    /**
     * @TODO This method should be removed after application entry and routers
     * are redesigned to be structured totally by spring.<p>
     * ----- Sharable handler seems able to be directly got via auto-wire.
     *
     * @param <T> Type of required ChannelHandler.
     * @param handler
     * @return
     */
    <T extends ChannelHandler> T getSharableHandler(Class<T> handler);

}
