package com.chigix.resserver.entity.error;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class InvalidPart extends DaoException {

    @Override
    public String getCode() {
        return "InvalidPart";
    }

    @Override
    public String getMessage() {
        return "One or more of the specified parts could not be found. "
                + "The part might not have been uploaded, or the specified "
                + "entity tag might not have matched the part's entity tag.";
    }

}
