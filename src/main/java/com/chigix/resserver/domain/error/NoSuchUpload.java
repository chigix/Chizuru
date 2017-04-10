package com.chigix.resserver.domain.error;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class NoSuchUpload extends DaoException {

    @Override
    public String getMessage() {
        return "The specified multipart upload does not exist. "
                + "The upload ID might be invalid, or the multipart upload "
                + "might have been aborted or completed.";
    }

    @Override
    public String getCode() {
        return "NoSuchUpload";
    }

}
