package com.chigix.resserver.interfaces.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 * @param <S>
 */
public class OutputStreamProxy<S extends OutputStream> extends OutputStream {

    S stream;

    public S getStream() {
        return stream;
    }

    public void setStream(S stream) {
        this.stream = stream;
    }

    @Override
    public void write(int b) throws IOException {
        stream.write(b);
    }

}
