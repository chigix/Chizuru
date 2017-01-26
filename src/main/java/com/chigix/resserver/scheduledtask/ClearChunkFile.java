package com.chigix.resserver.scheduledtask;

import java.util.TimerTask;

/**
 * Scheduled tasks which would be executed aumatically for some manipulation
 * assignments.
 *
 * 1. Scan and remove unused file chunks: Build a chunkName-referenceCount
 * mapping within Scan task. Conclusionly, current resource keys would be
 * scanned once when the task launched. With temperary mapping built, Scan all
 * chunks in file system and remove the chunk which is not referenced.
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ClearChunkFile extends TimerTask {

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
