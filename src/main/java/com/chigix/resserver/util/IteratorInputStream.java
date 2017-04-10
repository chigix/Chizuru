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
        if (currentStream != null) {
            final int read = currentStream.read(b);
            if (read < 0) {
                currentStream.close();
                currentStream = null;
                return read(b);
            }
            return read;
        }
        if (!it.hasNext()) {
            return -1;
        }
        try {
            currentStream = next(it.next());
        } catch (NoSuchElementException noSuchElementException) {
            currentStream = null;
        }
        return read(b);
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (currentStream != null) {
            final int read = currentStream.read(b, off, len);
            if (read < 0) {
                currentStream.close();
                currentStream = null;
                return read(b, off, len);
            }
            return read;
        }
        if (!it.hasNext()) {
            return -1;
        }
        try {
            currentStream = next(it.next());
        } catch (NoSuchElementException noSuchElementException) {
            currentStream = null;
        }
        return read(b, off, len);
    }

    protected abstract InputStream next(T item) throws NoSuchElementException;

}
