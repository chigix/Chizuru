package com.chigix.resserver.domain.error;

import com.chigix.resserver.domain.error.InvalidArgument.ArgumentValueInclude;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class InvalidPartNumber extends InvalidArgument implements
        InvalidArgument.ArgumentNameInclude,
        ArgumentValueInclude {

    private final String errorNumber;

    public InvalidPartNumber(String errorNumber) {
        this.errorNumber = errorNumber;
    }

    @Override
    public String getMessage() {
        return "Part number must be an integer greater than 1.";
    }

    @Override
    public String getArgumentName() {
        return "partNumber";
    }

    @Override
    public String getArgumentValue() {
        return errorNumber;
    }

}
