package com.bumptech.glide.load.data;

import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.resource.bitmap.RecyclableBufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class InputStreamRewinder implements DataRewinder<InputStream> {
    private final RecyclableBufferedInputStream bufferedStream;

    public static final class Factory implements com.bumptech.glide.load.data.DataRewinder.Factory<InputStream> {
        private final ArrayPool byteArrayPool;

        public Factory(ArrayPool byteArrayPool) {
            this.byteArrayPool = byteArrayPool;
        }

        public DataRewinder<InputStream> build(InputStream data) {
            return new InputStreamRewinder(data, this.byteArrayPool);
        }

        public Class<InputStream> getDataClass() {
            return InputStream.class;
        }
    }

    InputStreamRewinder(InputStream is, ArrayPool byteArrayPool) {
        this.bufferedStream = new RecyclableBufferedInputStream(is, byteArrayPool);
        this.bufferedStream.mark(5242880);
    }

    public InputStream rewindAndGet() throws IOException {
        this.bufferedStream.reset();
        return this.bufferedStream;
    }

    public void cleanup() {
        this.bufferedStream.release();
    }
}
