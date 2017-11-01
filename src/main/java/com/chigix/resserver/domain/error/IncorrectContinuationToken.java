package com.chigix.resserver.domain.error;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class IncorrectContinuationToken extends InvalidArgument implements InvalidArgument.ArgumentNameInclude {

    @Override
    public String getMessage() {
        return "The continuation token provided is incorrect";
    }

    @Override
    public String getArgumentName() {
        return "continuation-token";
    }

}
