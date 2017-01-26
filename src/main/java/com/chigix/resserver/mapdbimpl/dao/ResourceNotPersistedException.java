package com.chigix.resserver.mapdbimpl.dao;

import java.text.MessageFormat;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ResourceNotPersistedException extends RuntimeException {

    public ResourceNotPersistedException(String resource_key) {
        super(MessageFormat.format("Resource#[{0}] object usage has not been persisted.", resource_key));
    }

}
