package com.rockon999.android.leanbacklauncher.recline.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CachedInputStream extends FilterInputStream {
    private ArrayList<byte[]> mBufs;
    private int mCount;
    private int mMarkLimit;
    private int mMarkPos;
    private int mOverrideMarkLimit;
    private int mPos;
    private byte[] tmp;

    public CachedInputStream(InputStream in) {
        super(in);
        this.mBufs = new ArrayList<>();
        this.mPos = 0;
        this.mCount = 0;
        this.mMarkPos = -1;
        this.tmp = new byte[1];
    }

    public boolean markSupported() {
        return true;
    }

    public void setOverrideMarkLimit(int overrideMarkLimit) {
        this.mOverrideMarkLimit = overrideMarkLimit;
    }

    public void mark(int readlimit) {
        if (readlimit < this.mOverrideMarkLimit) {
            readlimit = this.mOverrideMarkLimit;
        }
        if (this.mMarkPos >= 0) {
            int chunks = this.mPos / 16384;
            if (chunks > 0) {
                int removedBytes = chunks * 16384;
                List<byte[]> subList = this.mBufs.subList(0, chunks);
                releaseChunks(subList);
                subList.clear();
                this.mPos -= removedBytes;
                this.mCount -= removedBytes;
            }
        }
        this.mMarkPos = this.mPos;
        this.mMarkLimit = readlimit;
    }

    public void reset() throws IOException {
        if (this.mMarkPos < 0) {
            throw new IOException("mark has been invalidated");
        }
        this.mPos = this.mMarkPos;
    }

    public int read() throws IOException {
        if (read(this.tmp, 0, 1) <= 0) {
            return -1;
        }
        return this.tmp[0] & 255;
    }

    public void close() throws IOException {
        if (this.in != null) {
            this.in.close();
            this.in = null;
        }
        releaseChunks(this.mBufs);
    }

    private static void releaseChunks(List<byte[]> bufs) {
        ByteArrayPool.get16KBPool().releaseChunks(bufs);
    }

    private byte[] allocateChunk() {
        return ByteArrayPool.get16KBPool().allocateChunk();
    }

    private boolean invalidate() {
        if (this.mCount - this.mMarkPos <= this.mMarkLimit) {
            return false;
        }
        this.mMarkPos = -1;
        this.mCount = 0;
        this.mPos = 0;
        releaseChunks(this.mBufs);
        this.mBufs.clear();
        return true;
    }

    public int read(byte[] buffer, int offset, int count) throws IOException {
        if (this.in == null) {
            throw streamClosed();
        } else if (this.mMarkPos == -1) {
            return this.in.read(buffer, offset, count);
        } else {
            if (count == 0) {
                return 0;
            }
            int copied = copyMarkedBuffer(buffer, offset, count);
            count -= copied;
            offset += copied;
            int totalReads = copied;
            while (count > 0) {
                if (this.mPos == this.mBufs.size() * 16384) {
                    this.mBufs.add(allocateChunk());
                }
                int currentBuf = this.mPos / 16384;
                int indexInBuf = this.mPos - (currentBuf * 16384);
                byte[] buf = this.mBufs.get(currentBuf);
                int leftInBuffer = ((currentBuf + 1) * 16384) - this.mPos;
                int reads = this.in.read(buf, indexInBuf, count > leftInBuffer ? leftInBuffer : count);
                if (reads <= 0) {
                    break;
                }
                System.arraycopy(buf, indexInBuf, buffer, offset, reads);
                this.mPos += reads;
                this.mCount += reads;
                totalReads += reads;
                offset += reads;
                count -= reads;
                if (invalidate()) {
                    reads = this.in.read(buffer, offset, count);
                    if (reads > 0) {
                        totalReads += reads;
                    }
                }
            }
            if (totalReads == 0) {
                return -1;
            }
            return totalReads;
        }
    }

    private int copyMarkedBuffer(byte[] buffer, int offset, int read) {
        int totalRead = 0;
        while (read > 0 && this.mPos < this.mCount) {
            int toRead;
            int currentBuf = this.mPos / 16384;
            int indexInBuf = this.mPos - (currentBuf * 16384);
            byte[] buf = this.mBufs.get(currentBuf);
            int end = (currentBuf + 1) * 16384;
            if (end > this.mCount) {
                end = this.mCount;
            }
            int leftInBuffer = end - this.mPos;
            if (read > leftInBuffer) {
                toRead = leftInBuffer;
            } else {
                toRead = read;
            }
            System.arraycopy(buf, indexInBuf, buffer, offset, toRead);
            offset += toRead;
            read -= toRead;
            totalRead += toRead;
            this.mPos += toRead;
        }
        return totalRead;
    }

    public int available() throws IOException {
        if (this.in != null) {
            return (this.mCount - this.mPos) + this.in.available();
        }
        throw streamClosed();
    }

    public long skip(long byteCount) throws IOException {
        if (this.in == null) {
            throw streamClosed();
        } else if (this.mMarkPos < 0) {
            return this.in.skip(byteCount);
        } else {
            long totalSkip = (long) (this.mCount - this.mPos);
            if (totalSkip > byteCount) {
                totalSkip = byteCount;
            }
            this.mPos = (int) (((long) this.mPos) + totalSkip);
            byteCount -= totalSkip;
            while (byteCount > 0) {
                long j;
                if (this.mPos == this.mBufs.size() * 16384) {
                    this.mBufs.add(allocateChunk());
                }
                int currentBuf = this.mPos / 16384;
                int indexInBuf = this.mPos - (currentBuf * 16384);
                byte[] buf = this.mBufs.get(currentBuf);
                int leftInBuffer = ((currentBuf + 1) * 16384) - this.mPos;
                if (byteCount > ((long) leftInBuffer)) {
                    j = (long) leftInBuffer;
                } else {
                    j = byteCount;
                }
                int reads = this.in.read(buf, indexInBuf, (int) j);
                if (reads <= 0) {
                    break;
                }
                this.mPos += reads;
                this.mCount += reads;
                byteCount -= (long) reads;
                totalSkip += (long) reads;
                if (invalidate()) {
                    if (byteCount > 0) {
                        totalSkip += this.in.skip(byteCount);
                    }
                }
            }
            return totalSkip;
        }
    }

    private static IOException streamClosed() {
        return new IOException("stream closed");
    }
}
