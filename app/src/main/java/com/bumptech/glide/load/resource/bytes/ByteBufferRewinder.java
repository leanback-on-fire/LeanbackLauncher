package com.bumptech.glide.load.resource.bytes;

import com.bumptech.glide.load.data.DataRewinder;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteBufferRewinder implements DataRewinder<ByteBuffer> {
    private final ByteBuffer buffer;

    public static class Factory implements com.bumptech.glide.load.data.DataRewinder.Factory<ByteBuffer> {
        public DataRewinder<ByteBuffer> build(ByteBuffer data) {
            return new ByteBufferRewinder(data);
        }

        public Class<ByteBuffer> getDataClass() {
            return ByteBuffer.class;
        }
    }

    public ByteBufferRewinder(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public ByteBuffer rewindAndGet() throws IOException {
        this.buffer.position(0);
        return this.buffer;
    }

    public void cleanup() {
    }
}
