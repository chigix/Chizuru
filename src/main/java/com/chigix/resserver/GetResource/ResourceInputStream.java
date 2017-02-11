package com.chigix.resserver.GetResource;

import com.chigix.resserver.entity.Chunk;
import com.chigix.resserver.entity.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ResourceInputStream extends InputStream {

    private final Resource resource;

    private Iterator<Chunk> chunks = null;

    private InputStream currentChunkInput = null;

    private final byte[] buffer;

    private int bufferLength = 0;

    private final AtomicInteger bufferReaderIndex = new AtomicInteger();

    public ResourceInputStream(Resource resource, int buffer_size) {
        this.resource = resource;
        buffer = new byte[buffer_size];
        bufferReaderIndex.set(0);
    }

    @Override
    public int read() throws IOException {
        int reader_index = bufferReaderIndex.incrementAndGet();
        if (reader_index < bufferLength) {
            return Byte.toUnsignedInt(buffer[reader_index]);
        }
        if (bufferLength == -1) {
            return -1;
        }
        bufferLength = readBuffer(buffer);
        if (bufferLength < 0) {
            return -1;
        }
        bufferReaderIndex.set(0);
        return Byte.toUnsignedInt(buffer[0]);
    }

    private int readBuffer(byte[] buffer) throws IOException {
        if (chunks == null) {
            chunks = resource.getChunks();
        }
        if (currentChunkInput == null) {
            try {
                currentChunkInput = chunks.next().getInputStream();
            } catch (NullPointerException | NoSuchElementException ex) {
                return -1;
            }
        }
        int read_length = currentChunkInput.read(buffer);
        if (read_length == buffer.length) {
            return read_length;
        }
        byte[] fill_tmp = new byte[buffer.length - (read_length < 0 ? 0 : read_length)];
        currentChunkInput = null;
        int length_tmp = readBuffer(fill_tmp);
        if (length_tmp > 0) {
            if (read_length < 0) {
                read_length = 0;
            }
            System.arraycopy(fill_tmp, 0, buffer, read_length, buffer.length - read_length);
            return read_length + length_tmp;
        } else {
            return read_length;
        }
    }

}
