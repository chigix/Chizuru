package com.chigix.resserver.entity.dao;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 * @param <MODEL>
 */
public interface ModelProxy<MODEL> {

    MODEL getProxiedModel() throws ProxiedException;

    MODEL setProxy(MODEL model);

    MODEL resetProxy();

    public static class ProxiedException extends RuntimeException {

        public ProxiedException(Throwable cause) {
            super(cause);
        }

    }

}
