package com.chigix.resserver.mapdbimpl.dao;

import java.text.MessageFormat;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class BucketNotPersistedException extends RuntimeException {

    public BucketNotPersistedException(String name) {
        super(MessageFormat.format("Resource's bucket object [{0}] is not persisted in database.", name));
    }

}
