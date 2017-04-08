package com.chigix.resserver.GetResource;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.entity.Chunk;
import com.chigix.resserver.util.IteratorInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    protected InputStream next(Chunk item) throws NoSuchElementException {
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
    public int available() throws IOException {
        return app.getTransferBufferSize();
    }

}
