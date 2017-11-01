package com.chigix.resserver.config;

import io.netty.handler.codec.http.router.HttpRouted;
import java.io.File;
import org.joda.time.DateTime;
import com.chigix.resserver.domain.DaoFactory;

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

    DaoFactory getEntityManager();

    void addNode(String nodeId, String nodeIPAddress);

    void finishRequest(HttpRouted routed_info);

}
