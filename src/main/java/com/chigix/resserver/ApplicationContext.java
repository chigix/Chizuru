package com.chigix.resserver;

import com.chigix.resserver.entity.dao.DaoFactory;
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

}
