package com.chigix.resserver.domain.error;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public abstract class InvalidArgument extends DaoException {

    @Override
    public final String getCode() {
        return "InvalidArgument";
    }

    @Override
    abstract public String getMessage();

    public static interface ArgumentNameInclude {

        String getArgumentName();
    }

    public static interface ArgumentValueInclude {

        String getArgumentValue();
    }

}
