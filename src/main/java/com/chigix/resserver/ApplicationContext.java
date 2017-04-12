package com.chigix.resserver;

import com.chigix.resserver.domain.dao.DaoFactory;
import io.netty.handler.codec.http.router.HttpRouted;
import java.io.File;
import org.joda.time.DateTime;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public interface ApplicationContext {

    File getChunksDir();

    DateTime getCreationDate();

    String getCurrentNodeId();

    int getMaxChunkSize();

    int getTransferBufferSize();

    String getRequestIdHeaderName();

    DaoFactory getDaoFactory();

    void addNode(String nodeId, String nodeIPAddress);

    void finishRequest(HttpRouted routed_info);

}
