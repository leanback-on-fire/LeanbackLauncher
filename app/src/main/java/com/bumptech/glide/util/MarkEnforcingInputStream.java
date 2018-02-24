package com.bumptech.glide.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MarkEnforcingInputStream extends FilterInputStream {
    private int availableBytes = Integer.MIN_VALUE;

    public MarkEnforcingInputStream(InputStream in) {
        super(in);
    }

    public void mark(int readlimit) {
        super.mark(readlimit);
        this.availableBytes = readlimit;
    }

    public int read() throws IOException {
        if (getBytesToRead(1) == -1) {
            return -1;
        }
        int result = super.read();
        updateAvailableBytesAfterRead(1);
        return result;
    }

    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        int toRead = (int) getBytesToRead((long) byteCount);
        if (toRead == -1) {
            return -1;
        }
        int read = super.read(buffer, byteOffset, toRead);
        updateAvailableBytesAfterRead((long) read);
        return read;
    }

    public void reset() throws IOException {
        super.reset();
        this.availableBytes = Integer.MIN_VALUE;
    }

    public long skip(long byteCount) throws IOException {
        long toSkip = getBytesToRead(byteCount);
        if (toSkip == -1) {
            return -1;
        }
        long read = super.skip(toSkip);
        updateAvailableBytesAfterRead(read);
        return read;
    }

    public int available() throws IOException {
        if (this.availableBytes == Integer.MIN_VALUE) {
            return super.available();
        }
        return Math.min(this.availableBytes, super.available());
    }

    private long getBytesToRead(long targetByteCount) {
        if (this.availableBytes == 0) {
            return -1;
        }
        if (this.availableBytes == Integer.MIN_VALUE || targetByteCount <= ((long) this.availableBytes)) {
            return targetByteCount;
        }
        return (long) this.availableBytes;
    }

    private void updateAvailableBytesAfterRead(long bytesRead) {
        if (this.availableBytes != Integer.MIN_VALUE && bytesRead != -1) {
            this.availableBytes = (int) (((long) this.availableBytes) - bytesRead);
        }
    }
}
