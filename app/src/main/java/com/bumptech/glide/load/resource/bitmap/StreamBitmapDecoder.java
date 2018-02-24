package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.Downsampler.DecodeCallbacks;
import com.bumptech.glide.util.ExceptionCatchingInputStream;
import com.bumptech.glide.util.MarkEnforcingInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamBitmapDecoder implements ResourceDecoder<InputStream, Bitmap> {
    private final ArrayPool byteArrayPool;
    private final Downsampler downsampler;

    static class UntrustedCallbacks implements DecodeCallbacks {
        private final RecyclableBufferedInputStream bufferedStream;
        private final ExceptionCatchingInputStream exceptionStream;

        public UntrustedCallbacks(RecyclableBufferedInputStream bufferedStream, ExceptionCatchingInputStream exceptionStream) {
            this.bufferedStream = bufferedStream;
            this.exceptionStream = exceptionStream;
        }

        public void onObtainBounds() {
            this.bufferedStream.fixMarkLimit();
        }

        public void onDecodeComplete(BitmapPool bitmapPool, Bitmap downsampled) throws IOException {
            IOException streamException = this.exceptionStream.getException();
            if (streamException != null) {
                if (downsampled != null) {
                    bitmapPool.put(downsampled);
                }
                throw streamException;
            }
        }
    }

    public StreamBitmapDecoder(Downsampler downsampler, ArrayPool byteArrayPool) {
        this.downsampler = downsampler;
        this.byteArrayPool = byteArrayPool;
    }

    public boolean handles(InputStream source, Options options) throws IOException {
        return this.downsampler.handles(source);
    }

    public Resource<Bitmap> decode(InputStream source, int width, int height, Options options) throws IOException {
        RecyclableBufferedInputStream bufferedStream;
        boolean ownsBufferedStream;
        if (source instanceof RecyclableBufferedInputStream) {
            bufferedStream = (RecyclableBufferedInputStream) source;
            ownsBufferedStream = false;
        } else {
            bufferedStream = new RecyclableBufferedInputStream(source, this.byteArrayPool);
            ownsBufferedStream = true;
        }
        ExceptionCatchingInputStream exceptionStream = ExceptionCatchingInputStream.obtain(bufferedStream);
        try {
            Resource<Bitmap> decode = this.downsampler.decode(new MarkEnforcingInputStream(exceptionStream), width, height, options, new UntrustedCallbacks(bufferedStream, exceptionStream));
            return decode;
        } finally {
            exceptionStream.release();
            if (ownsBufferedStream) {
                bufferedStream.release();
            }
        }
    }
}
