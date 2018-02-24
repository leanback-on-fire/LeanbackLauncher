package com.bumptech.glide.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;

public class ExceptionCatchingInputStream extends InputStream {
    private static final Queue<ExceptionCatchingInputStream> QUEUE = Util.createQueue(0);
    private IOException exception;
    private InputStream wrapped;

    public static ExceptionCatchingInputStream obtain(InputStream toWrap) {
        ExceptionCatchingInputStream result;
        synchronized (QUEUE) {
            result = (ExceptionCatchingInputStream) QUEUE.poll();
        }
        if (result == null) {
            result = new ExceptionCatchingInputStream();
        }
        result.setInputStream(toWrap);
        return result;
    }

    ExceptionCatchingInputStream() {
    }

    void setInputStream(InputStream toWrap) {
        this.wrapped = toWrap;
    }

    public int available() throws IOException {
        return this.wrapped.available();
    }

    public void close() throws IOException {
        this.wrapped.close();
    }

    public void mark(int readlimit) {
        this.wrapped.mark(readlimit);
    }

    public boolean markSupported() {
        return this.wrapped.markSupported();
    }

    public int read(byte[] buffer) throws IOException {
        try {
            return this.wrapped.read(buffer);
        } catch (IOException e) {
            this.exception = e;
            return -1;
        }
    }

    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        try {
            return this.wrapped.read(buffer, byteOffset, byteCount);
        } catch (IOException e) {
            this.exception = e;
            return -1;
        }
    }

    public synchronized void reset() throws IOException {
        this.wrapped.reset();
    }

    public long skip(long byteCount) throws IOException {
        try {
            return this.wrapped.skip(byteCount);
        } catch (IOException e) {
            this.exception = e;
            return 0;
        }
    }

    public int read() throws IOException {
        try {
            return this.wrapped.read();
        } catch (IOException e) {
            this.exception = e;
            return -1;
        }
    }

    public IOException getException() {
        return this.exception;
    }

    public void release() {
        this.exception = null;
        this.wrapped = null;
        synchronized (QUEUE) {
            QUEUE.offer(this);
        }
    }
}
