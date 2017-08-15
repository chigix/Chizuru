package com.chigix.resserver.endpoint.GetResource;

import com.chigix.resserver.ApplicationContext;
import com.chigix.resserver.domain.ChunkedResource;
import com.chigix.resserver.util.IteratorInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class AmassedInputStream extends IteratorInputStream<ChunkedResource> {

    private final ApplicationContext app;

    public AmassedInputStream(Iterator<ChunkedResource> source, ApplicationContext app) {
        super(source);
        this.app = app;
    }

    @Override
    protected InputStream inputStreamProvider(ChunkedResource item) throws NoSuchElementException {
        return new ResourceInputStream(item.getChunks(), app);
    }

    @Override
    public int available() throws IOException {
        return app.getTransferBufferSize();
    }

}
