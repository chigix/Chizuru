package com.chigix.resserver.endpoint.GetResource;

import com.chigix.resserver.config.ApplicationContext;
import com.chigix.resserver.domain.model.chunk.Chunk;
import com.chigix.resserver.interfaces.io.IteratorInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ResourceInputStream extends IteratorInputStream<Chunk> {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceInputStream.class.getName());

    private final ApplicationContext app;

    public ResourceInputStream(Iterator<Chunk> source, ApplicationContext app) {
        super(source);
        this.app = app;
    }

    @Override
    protected InputStream inputStreamProvider(Chunk item) throws NoSuchElementException {
        try {
            return item.getInputStream();
        } catch (FileNotFoundException ex) {
            LOG.error("Chunk File isn't existing on disk, however registered in database."
                    + ex.getMessage(), ex);
            throw new NoSuchElementException();
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new NoSuchElementException();
        }
    }

    @Override
    public long skip(long n) throws IOException {
        if (n <= 0) {
            return 0;
        }
        long nr = 0;
        nr += skipCurrent(n);
        long skipChunks = (n - nr) / this.app.getMaxChunkSize();
        try {
            for (int i = 0; i < skipChunks; i++) {
                Chunk c = next();
                if (c != null) {
                    nr += c.getSize();
                }
            }
        } catch (NoSuchElementException e) {
            throw new RuntimeException(MessageFormat.format("Unexpected!! "
                    + "Current Node's MaxChunkSize setting [{}] is less than "
                    + "some persisted chunk size.", this.app.getMaxChunkSize()));
        }
        next();
        long remainingBytes = n - nr;
        if (remainingBytes > 0) {
            return skipCurrent(remainingBytes);
        }
        return 0;
    }

    @Override
    public int available() throws IOException {
        return app.getTransferBufferSize();
    }

}
