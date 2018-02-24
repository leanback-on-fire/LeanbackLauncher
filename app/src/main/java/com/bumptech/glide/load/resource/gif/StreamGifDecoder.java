package com.bumptech.glide.load.resource.gif;

import android.util.Log;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.resource.bitmap.ImageHeaderParser;
import com.bumptech.glide.load.resource.bitmap.ImageHeaderParser.ImageType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class StreamGifDecoder implements ResourceDecoder<InputStream, GifDrawable> {
    public static final Option<Boolean> DISABLE_ANIMATION = Option.memory("com.bumptech.glide.load.resource.gif.ByteBufferGifDecoder.DisableAnimation", Boolean.valueOf(false));
    private final ArrayPool byteArrayPool;
    private final ResourceDecoder<ByteBuffer, GifDrawable> byteBufferDecoder;

    public StreamGifDecoder(ResourceDecoder<ByteBuffer, GifDrawable> byteBufferDecoder, ArrayPool byteArrayPool) {
        this.byteBufferDecoder = byteBufferDecoder;
        this.byteArrayPool = byteArrayPool;
    }

    public boolean handles(InputStream source, Options options) throws IOException {
        return !((Boolean) options.get(DISABLE_ANIMATION)).booleanValue() && new ImageHeaderParser(source, this.byteArrayPool).getType() == ImageType.GIF;
    }

    public Resource<GifDrawable> decode(InputStream source, int width, int height, Options options) throws IOException {
        byte[] data = inputStreamToBytes(source);
        if (data == null) {
            return null;
        }
        return this.byteBufferDecoder.decode(ByteBuffer.wrap(data), width, height, options);
    }

    private static byte[] inputStreamToBytes(InputStream is) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(16384);
        try {
            byte[] data = new byte[16384];
            while (true) {
                int nRead = is.read(data);
                if (nRead != -1) {
                    buffer.write(data, 0, nRead);
                } else {
                    buffer.flush();
                    return buffer.toByteArray();
                }
            }
        } catch (IOException e) {
            if (Log.isLoggable("StreamGifDecoder", 5)) {
                Log.w("StreamGifDecoder", "Error reading data from stream", e);
            }
            return null;
        }
    }
}
