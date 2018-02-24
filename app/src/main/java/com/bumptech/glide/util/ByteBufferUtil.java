package com.bumptech.glide.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.concurrent.atomic.AtomicReference;

public final class ByteBufferUtil {
    private static final AtomicReference<byte[]> BUFFER_REF = new AtomicReference();

    private static class ByteBufferStream extends InputStream {
        private final ByteBuffer byteBuffer;
        private int markPos = -1;

        public ByteBufferStream(ByteBuffer byteBuffer) {
            this.byteBuffer = byteBuffer;
        }

        public int available() throws IOException {
            return this.byteBuffer.remaining();
        }

        public int read() throws IOException {
            if (this.byteBuffer.hasRemaining()) {
                return this.byteBuffer.get();
            }
            return -1;
        }

        public synchronized void mark(int readlimit) {
            this.markPos = this.byteBuffer.position();
        }

        public boolean markSupported() {
            return true;
        }

        public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
            if (!this.byteBuffer.hasRemaining()) {
                return -1;
            }
            int toRead = Math.min(byteCount, available());
            this.byteBuffer.get(buffer, byteOffset, toRead);
            return toRead;
        }

        public synchronized void reset() throws IOException {
            if (this.markPos == -1) {
                throw new IOException("Cannot reset to unset mark position");
            }
            this.byteBuffer.position(this.markPos);
        }

        public long skip(long byteCount) throws IOException {
            if (!this.byteBuffer.hasRemaining()) {
                return -1;
            }
            long toSkip = Math.min(byteCount, (long) available());
            this.byteBuffer.position((int) (((long) this.byteBuffer.position()) + toSkip));
            return toSkip;
        }
    }

    static final class SafeArray {
        private final byte[] data;
        private final int limit;
        private final int offset;

        public SafeArray(byte[] data, int offset, int limit) {
            this.data = data;
            this.offset = offset;
            this.limit = limit;
        }
    }

    public static ByteBuffer fromFile(File file) throws IOException {
        Throwable th;
        RandomAccessFile raf = null;
        FileChannel channel = null;
        try {
            RandomAccessFile raf2 = new RandomAccessFile(file, "r");
            try {
                channel = raf2.getChannel();
                ByteBuffer load = channel.map(MapMode.READ_ONLY, 0, file.length()).load();
                if (channel != null) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                    }
                }
                if (raf2 != null) {
                    try {
                        raf2.close();
                    } catch (IOException e2) {
                    }
                }
                return load;
            } catch (Throwable th2) {
                th = th2;
                raf = raf2;
                if (channel != null) {
                    try {
                        channel.close();
                    } catch (IOException e3) {
                    }
                }
                if (raf != null) {
                    try {
                        raf.close();
                    } catch (IOException e4) {
                    }
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            if (channel != null) {
                channel.close();
            }
            if (raf != null) {
                raf.close();
            }
            throw th;
        }
    }

    public static void toFile(ByteBuffer buffer, File file) throws IOException {
        Throwable th;
        RandomAccessFile raf = null;
        FileChannel channel = null;
        try {
            RandomAccessFile raf2 = new RandomAccessFile(file, "rw");
            try {
                channel = raf2.getChannel();
                channel.write(buffer);
                channel.force(false);
                channel.close();
                raf2.close();
                if (channel != null) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                    }
                }
                if (raf2 != null) {
                    try {
                        raf2.close();
                    } catch (IOException e2) {
                    }
                }
            } catch (Throwable th2) {
                th = th2;
                raf = raf2;
                if (channel != null) {
                    try {
                        channel.close();
                    } catch (IOException e3) {
                    }
                }
                if (raf != null) {
                    try {
                        raf.close();
                    } catch (IOException e4) {
                    }
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            if (channel != null) {
                channel.close();
            }
            if (raf != null) {
                raf.close();
            }
            throw th;
        }
    }

    public static byte[] toBytes(ByteBuffer byteBuffer) {
        SafeArray safeArray = getSafeArray(byteBuffer);
        if (safeArray != null && safeArray.offset == 0 && safeArray.limit == safeArray.data.length) {
            return byteBuffer.array();
        }
        ByteBuffer toCopy = byteBuffer.asReadOnlyBuffer();
        byte[] result = new byte[toCopy.limit()];
        toCopy.position(0);
        toCopy.get(result);
        return result;
    }

    public static InputStream toStream(ByteBuffer buffer) {
        return new ByteBufferStream(buffer);
    }

    private static SafeArray getSafeArray(ByteBuffer byteBuffer) {
        if (byteBuffer.isReadOnly() || !byteBuffer.hasArray()) {
            return null;
        }
        return new SafeArray(byteBuffer.array(), byteBuffer.arrayOffset(), byteBuffer.limit());
    }
}
