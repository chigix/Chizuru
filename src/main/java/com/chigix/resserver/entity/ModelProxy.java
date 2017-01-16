package com.chigix.resserver.entity;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 * @param <MODEL>
 */
public interface ModelProxy<MODEL> {

    MODEL getProxiedModel() throws ProxiedException;

    public static class ProxiedException extends RuntimeException {

        public ProxiedException(Throwable cause) {
            super(cause);
        }

    }

}
