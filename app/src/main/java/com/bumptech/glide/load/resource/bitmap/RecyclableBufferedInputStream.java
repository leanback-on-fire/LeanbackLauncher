package com.bumptech.glide.load.resource.bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RecyclableBufferedInputStream extends FilterInputStream {
    private volatile byte[] buf;
    private final ArrayPool byteArrayPool;
    private int count;
    private int marklimit;
    private int markpos;
    private int pos;

    public static class InvalidMarkException extends IOException {
        public InvalidMarkException(String detailMessage) {
            super(detailMessage);
        }
    }

    public RecyclableBufferedInputStream(InputStream in, ArrayPool byteArrayPool) {
        this(in, byteArrayPool, 65536);
    }

    RecyclableBufferedInputStream(InputStream in, ArrayPool byteArrayPool, int bufferSize) {
        super(in);
        this.markpos = -1;
        this.byteArrayPool = byteArrayPool;
        this.buf = (byte[]) byteArrayPool.get(bufferSize, byte[].class);
    }

    public synchronized int available() throws IOException {
        InputStream localIn;
        localIn = this.in;
        if (this.buf == null || localIn == null) {
            throw streamClosed();
        }
        return (this.count - this.pos) + localIn.available();
    }

    private static IOException streamClosed() throws IOException {
        throw new IOException("BufferedInputStream is closed");
    }

    public synchronized void fixMarkLimit() {
        this.marklimit = this.buf.length;
    }

    public synchronized void release() {
        if (this.buf != null) {
            this.byteArrayPool.put(this.buf, byte[].class);
            this.buf = null;
        }
    }

    public void close() throws IOException {
        if (this.buf != null) {
            this.byteArrayPool.put(this.buf, byte[].class);
            this.buf = null;
        }
        InputStream localIn = this.in;
        this.in = null;
        if (localIn != null) {
            localIn.close();
        }
    }

    private int fillbuf(InputStream localIn, byte[] localBuf) throws IOException {
        if (this.markpos == -1 || this.pos - this.markpos >= this.marklimit) {
            int result = localIn.read(localBuf);
            if (result > 0) {
                this.markpos = -1;
                this.pos = 0;
                this.count = result;
            }
            return result;
        }
        int i;
        if (this.markpos == 0 && this.marklimit > localBuf.length && this.count == localBuf.length) {
            int newLength = localBuf.length * 2;
            if (newLength > this.marklimit) {
                newLength = this.marklimit;
            }
            byte[] newbuf = (byte[]) this.byteArrayPool.get(newLength, byte[].class);
            System.arraycopy(localBuf, 0, newbuf, 0, localBuf.length);
            byte[] oldbuf = localBuf;
            this.buf = newbuf;
            localBuf = newbuf;
            this.byteArrayPool.put(oldbuf, byte[].class);
        } else if (this.markpos > 0) {
            System.arraycopy(localBuf, this.markpos, localBuf, 0, localBuf.length - this.markpos);
        }
        this.pos -= this.markpos;
        this.markpos = 0;
        this.count = 0;
        int bytesread = localIn.read(localBuf, this.pos, localBuf.length - this.pos);
        if (bytesread <= 0) {
            i = this.pos;
        } else {
            i = this.pos + bytesread;
        }
        this.count = i;
        return bytesread;
    }

    public synchronized void mark(int readlimit) {
        this.marklimit = Math.max(this.marklimit, readlimit);
        this.markpos = this.pos;
    }

    public boolean markSupported() {
        return true;
    }

    public synchronized int read() throws IOException {
        int i = -1;
        synchronized (this) {
            byte[] localBuf = this.buf;
            InputStream localIn = this.in;
            if (localBuf == null || localIn == null) {
                throw streamClosed();
            }
            if (this.pos < this.count || fillbuf(localIn, localBuf) != -1) {
                if (localBuf != this.buf) {
                    localBuf = this.buf;
                    if (localBuf == null) {
                        throw streamClosed();
                    }
                }
                if (this.count - this.pos > 0) {
                    i = this.pos;
                    this.pos = i + 1;
                    i = localBuf[i] & 255;
                }
            }
        }
        return i;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int read(byte[] r9, int r10, int r11) throws java.io.IOException {
        /*
        r8 = this;
        r5 = -1;
        monitor-enter(r8);
        r1 = r8.buf;	 Catch:{ all -> 0x000b }
        if (r1 != 0) goto L_0x000e;
    L_0x0006:
        r5 = streamClosed();	 Catch:{ all -> 0x000b }
        throw r5;	 Catch:{ all -> 0x000b }
    L_0x000b:
        r5 = move-exception;
        monitor-exit(r8);
        throw r5;
    L_0x000e:
        if (r11 != 0) goto L_0x0013;
    L_0x0010:
        r5 = 0;
    L_0x0011:
        monitor-exit(r8);
        return r5;
    L_0x0013:
        r2 = r8.in;	 Catch:{ all -> 0x000b }
        if (r2 != 0) goto L_0x001c;
    L_0x0017:
        r5 = streamClosed();	 Catch:{ all -> 0x000b }
        throw r5;	 Catch:{ all -> 0x000b }
    L_0x001c:
        r6 = r8.pos;	 Catch:{ all -> 0x000b }
        r7 = r8.count;	 Catch:{ all -> 0x000b }
        if (r6 >= r7) goto L_0x005a;
    L_0x0022:
        r6 = r8.count;	 Catch:{ all -> 0x000b }
        r7 = r8.pos;	 Catch:{ all -> 0x000b }
        r6 = r6 - r7;
        if (r6 < r11) goto L_0x003e;
    L_0x0029:
        r0 = r11;
    L_0x002a:
        r6 = r8.pos;	 Catch:{ all -> 0x000b }
        java.lang.System.arraycopy(r1, r6, r9, r10, r0);	 Catch:{ all -> 0x000b }
        r6 = r8.pos;	 Catch:{ all -> 0x000b }
        r6 = r6 + r0;
        r8.pos = r6;	 Catch:{ all -> 0x000b }
        if (r0 == r11) goto L_0x003c;
    L_0x0036:
        r6 = r2.available();	 Catch:{ all -> 0x000b }
        if (r6 != 0) goto L_0x0045;
    L_0x003c:
        r5 = r0;
        goto L_0x0011;
    L_0x003e:
        r6 = r8.count;	 Catch:{ all -> 0x000b }
        r7 = r8.pos;	 Catch:{ all -> 0x000b }
        r0 = r6 - r7;
        goto L_0x002a;
    L_0x0045:
        r10 = r10 + r0;
        r4 = r11 - r0;
    L_0x0048:
        r6 = r8.markpos;	 Catch:{ all -> 0x000b }
        if (r6 != r5) goto L_0x005c;
    L_0x004c:
        r6 = r1.length;	 Catch:{ all -> 0x000b }
        if (r4 < r6) goto L_0x005c;
    L_0x004f:
        r3 = r2.read(r9, r10, r4);	 Catch:{ all -> 0x000b }
        if (r3 != r5) goto L_0x0086;
    L_0x0055:
        if (r4 == r11) goto L_0x0011;
    L_0x0057:
        r5 = r11 - r4;
        goto L_0x0011;
    L_0x005a:
        r4 = r11;
        goto L_0x0048;
    L_0x005c:
        r6 = r8.fillbuf(r2, r1);	 Catch:{ all -> 0x000b }
        if (r6 != r5) goto L_0x0067;
    L_0x0062:
        if (r4 == r11) goto L_0x0011;
    L_0x0064:
        r5 = r11 - r4;
        goto L_0x0011;
    L_0x0067:
        r6 = r8.buf;	 Catch:{ all -> 0x000b }
        if (r1 == r6) goto L_0x0074;
    L_0x006b:
        r1 = r8.buf;	 Catch:{ all -> 0x000b }
        if (r1 != 0) goto L_0x0074;
    L_0x006f:
        r5 = streamClosed();	 Catch:{ all -> 0x000b }
        throw r5;	 Catch:{ all -> 0x000b }
    L_0x0074:
        r6 = r8.count;	 Catch:{ all -> 0x000b }
        r7 = r8.pos;	 Catch:{ all -> 0x000b }
        r6 = r6 - r7;
        if (r6 < r4) goto L_0x008b;
    L_0x007b:
        r3 = r4;
    L_0x007c:
        r6 = r8.pos;	 Catch:{ all -> 0x000b }
        java.lang.System.arraycopy(r1, r6, r9, r10, r3);	 Catch:{ all -> 0x000b }
        r6 = r8.pos;	 Catch:{ all -> 0x000b }
        r6 = r6 + r3;
        r8.pos = r6;	 Catch:{ all -> 0x000b }
    L_0x0086:
        r4 = r4 - r3;
        if (r4 != 0) goto L_0x0092;
    L_0x0089:
        r5 = r11;
        goto L_0x0011;
    L_0x008b:
        r6 = r8.count;	 Catch:{ all -> 0x000b }
        r7 = r8.pos;	 Catch:{ all -> 0x000b }
        r3 = r6 - r7;
        goto L_0x007c;
    L_0x0092:
        r6 = r2.available();	 Catch:{ all -> 0x000b }
        if (r6 != 0) goto L_0x009c;
    L_0x0098:
        r5 = r11 - r4;
        goto L_0x0011;
    L_0x009c:
        r10 = r10 + r3;
        goto L_0x0048;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.resource.bitmap.RecyclableBufferedInputStream.read(byte[], int, int):int");
    }

    public synchronized void reset() throws IOException {
        if (this.buf == null) {
            throw new IOException("Stream is closed");
        } else if (-1 == this.markpos) {
            int i = this.pos;
            throw new InvalidMarkException("Mark has been invalidated, pos: " + i + " markLimit: " + this.marklimit);
        } else {
            this.pos = this.markpos;
        }
    }

    public synchronized long skip(long byteCount) throws IOException {
        byte[] localBuf = this.buf;
        InputStream localIn = this.in;
        if (localBuf == null) {
            throw streamClosed();
        } else if (byteCount < 1) {
            byteCount = 0;
        } else if (localIn == null) {
            throw streamClosed();
        } else if (((long) (this.count - this.pos)) >= byteCount) {
            this.pos = (int) (((long) this.pos) + byteCount);
        } else {
            long read = (long) (this.count - this.pos);
            this.pos = this.count;
            if (this.markpos == -1 || byteCount > ((long) this.marklimit)) {
                byteCount = read + localIn.skip(byteCount - read);
            } else if (fillbuf(localIn, localBuf) == -1) {
                byteCount = read;
            } else if (((long) (this.count - this.pos)) >= byteCount - read) {
                this.pos = (int) (((long) this.pos) + (byteCount - read));
            } else {
                read = (((long) this.count) + read) - ((long) this.pos);
                this.pos = this.count;
                byteCount = read;
            }
        }
        return byteCount;
    }
}
