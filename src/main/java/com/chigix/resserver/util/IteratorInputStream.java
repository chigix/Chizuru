package com.chigix.resserver.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 * @param <T>
 */
public abstract class IteratorInputStream<T> extends InputStream {

    private final Iterator<T> it;

    private InputStream currentStream = null;

    public IteratorInputStream(Iterator<T> source) {
        this.it = source;
    }

    @Override
    public final int read() throws IOException {
        if (currentStream != null) {
            int r = currentStream.read();
            if (r > -1) {
                return r;
            }
            currentStream.close();
            currentStream = null;
        }
        if (!it.hasNext()) {
            return -1;
        }
        try {
            currentStream = next(it.next());
        } catch (NoSuchElementException noSuchElementException) {
            currentStream = null;
        }
        return read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        int read = -1;
        if (currentStream != null) {
            int length = currentStream.read(b);
            if (length >= b.length) {
                return length;
            }
            read = length;
            currentStream.close();
            currentStream = null;
        }
        if (!it.hasNext()) {
            return read;
        }
        try {
            currentStream = next(it.next());
        } catch (NoSuchElementException noSuchElementException) {
            currentStream = null;
        }
        if (read < 0) {
            read = 0;
        }
        int length = currentStream.read(b, read, b.length - read);
        return read + length;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        int read = -1;
        if (currentStream != null) {
            int length = currentStream.read(b, off, len);
            if (length >= len) {
                return length;
            }
            read = length;
            currentStream.close();
            currentStream = null;
        }
        if (!it.hasNext()) {
            return read;
        }
        try {
            currentStream = next(it.next());
        } catch (NoSuchElementException noSuchElementException) {
            currentStream = null;
        }
        if (read < 0) {
            read = 0;
        }
        int length = currentStream.read(b, read, len - read);
        return read + length;
    }

    protected abstract InputStream next(T item) throws NoSuchElementException;

}
