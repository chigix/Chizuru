package com.chigix.resserver.scheduledtask;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Richard Lea <chigix@zoho.com>
 */
public class ReadStreamTaskHooker implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ReadStreamTaskHooker.class.getName());

    private final InputStream inputstream;

    final byte[] buffer;

    int readableBytes = 0;

    private final List<ReadingTask> readingTasks = new ArrayList<>();

    public ReadStreamTaskHooker(InputStream inputstream, int buffer_size) {
        this.inputstream = inputstream;
        this.buffer = new byte[buffer_size];
    }

    public ReadStreamTaskHooker(InputStream inputstream) {
        this(inputstream, 8192);
    }

    public void hookTask(ReadingTask task) {
        readingTasks.add(task);
    }

    @Override
    public void run() {
        while (true) {
            try {
                readableBytes = inputstream.read(buffer);
            } catch (IOException ex) {
                return;
            }
            if (readableBytes == -1) {
                return;
            }
            readingTasks.forEach((task) -> {
                if (buffer.length == readableBytes) {
                    task.run(buffer);
                } else {
                    task.run(Arrays.copyOf(buffer, readableBytes));
                }
            });
        }
    }

    @FunctionalInterface
    public static interface ReadingTask {

        public void run(byte[] buffer);
    }

    @FunctionalInterface
    public static interface FinishTask {

        public void run();
    }

}
