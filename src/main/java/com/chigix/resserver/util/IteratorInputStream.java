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

    /**
     * Skips over and discards <code>n</code> bytes of data from current sub
     * input stream. The <code>skip</code> method may, for a variety of reasons,
     * end up skipping over some smaller number of bytes, possibly
     * <code>0</code>. This may result from any of a number of conditions;
     * reaching end of file before <code>n</code> bytes have been skipped is
     * only one possibility. The actual number of bytes skipped is returned. If
     * {@code n} is negative, the {@code skip} method for class
     * {@code InputStream} always returns 0, and no bytes are skipped.
     * Subclasses may handle the negative value differently.
     *
     * @param n the number of bytes to be skipped.
     * @return the actual number of bytes skipped.
     * @exception IOException if the stream does not support seek, or if some
     * other I/O error occurs.
     */
    protected final long skipCurrent(long n) throws IOException {
        if (currentStream == null) {
            return 0;
        }
        return currentStream.skip(n);
    }

    protected final T discardNext() throws NoSuchElementException {
        return it.next();
    }

    protected abstract InputStream next(T item) throws NoSuchElementException;

    @Override
    public void close() throws IOException {
        if (currentStream != null) {
            currentStream.close();
        }
    }

}
