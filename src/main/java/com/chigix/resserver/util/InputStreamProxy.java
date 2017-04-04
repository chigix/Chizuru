package com.chigix.resserver.util;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class InputStreamProxy extends InputStream {

    InputStream stream;

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    public InputStream getStream() {
        return stream;
    }

    @Override
    public int read() throws IOException {
        return stream.read();
    }

}
