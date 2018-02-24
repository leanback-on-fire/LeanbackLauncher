package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.ByteBufferUtil;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteBufferBitmapDecoder implements ResourceDecoder<ByteBuffer, Bitmap> {
    private final Downsampler downsampler;

    public ByteBufferBitmapDecoder(Downsampler downsampler) {
        this.downsampler = downsampler;
    }

    public boolean handles(ByteBuffer source, Options options) throws IOException {
        return this.downsampler.handles(source);
    }

    public Resource<Bitmap> decode(ByteBuffer source, int width, int height, Options options) throws IOException {
        return this.downsampler.decode(ByteBufferUtil.toStream(source), width, height, options);
    }
}
