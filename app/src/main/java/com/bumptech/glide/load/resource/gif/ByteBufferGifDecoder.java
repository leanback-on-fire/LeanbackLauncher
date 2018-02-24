package com.bumptech.glide.load.resource.gif;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.gifdecoder.GifDecoder.BitmapProvider;
import com.bumptech.glide.gifdecoder.GifHeader;
import com.bumptech.glide.gifdecoder.GifHeaderParser;
import com.bumptech.glide.gifdecoder.StandardGifDecoder;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.bitmap_recycle.LruArrayPool;
import com.bumptech.glide.load.resource.UnitTransformation;
import com.bumptech.glide.load.resource.bitmap.ImageHeaderParser;
import com.bumptech.glide.load.resource.bitmap.ImageHeaderParser.ImageType;
import com.bumptech.glide.util.LogTime;
import com.bumptech.glide.util.Util;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Queue;

public class ByteBufferGifDecoder implements ResourceDecoder<ByteBuffer, GifDrawable> {
    public static final Option<Boolean> DISABLE_ANIMATION = Option.memory("com.bumptech.glide.load.resource.gif.ByteBufferGifDecoder.DisableAnimation", Boolean.valueOf(false));
    private static final GifDecoderFactory GIF_DECODER_FACTORY = new GifDecoderFactory();
    private static final GifHeaderParserPool PARSER_POOL = new GifHeaderParserPool();
    private final BitmapPool bitmapPool;
    private final Context context;
    private final GifDecoderFactory gifDecoderFactory;
    private final GifHeaderParserPool parserPool;
    private final GifBitmapProvider provider;

    static class GifDecoderFactory {
        GifDecoderFactory() {
        }

        public GifDecoder build(BitmapProvider provider, GifHeader header, ByteBuffer data, int sampleSize) {
            return new StandardGifDecoder(provider, header, data, sampleSize);
        }
    }

    static class GifHeaderParserPool {
        private final Queue<GifHeaderParser> pool = Util.createQueue(0);

        GifHeaderParserPool() {
        }

        public synchronized GifHeaderParser obtain(ByteBuffer buffer) {
            GifHeaderParser result;
            result = (GifHeaderParser) this.pool.poll();
            if (result == null) {
                result = new GifHeaderParser();
            }
            return result.setData(buffer);
        }

        public synchronized void release(GifHeaderParser parser) {
            parser.clear();
            this.pool.offer(parser);
        }
    }

    public ByteBufferGifDecoder(Context context, BitmapPool bitmapPool, ArrayPool arrayPool) {
        this(context, bitmapPool, arrayPool, PARSER_POOL, GIF_DECODER_FACTORY);
    }

    ByteBufferGifDecoder(Context context, BitmapPool bitmapPool, ArrayPool arrayPool, GifHeaderParserPool parserPool, GifDecoderFactory gifDecoderFactory) {
        this.context = context;
        this.bitmapPool = bitmapPool;
        this.gifDecoderFactory = gifDecoderFactory;
        this.provider = new GifBitmapProvider(bitmapPool, arrayPool);
        this.parserPool = parserPool;
    }

    public boolean handles(ByteBuffer source, Options options) throws IOException {
        return !((Boolean) options.get(DISABLE_ANIMATION)).booleanValue() && new ImageHeaderParser(source, new LruArrayPool()).getType() == ImageType.GIF;
    }

    public GifDrawableResource decode(ByteBuffer source, int width, int height, Options options) {
        GifHeaderParser parser = this.parserPool.obtain(source);
        try {
            GifDrawableResource decode = decode(source, width, height, parser);
            return decode;
        } finally {
            this.parserPool.release(parser);
        }
    }

    private GifDrawableResource decode(ByteBuffer byteBuffer, int width, int height, GifHeaderParser parser) {
        long startTime = LogTime.getLogTime();
        GifHeader header = parser.parseHeader();
        if (header.getNumFrames() <= 0 || header.getStatus() != 0) {
            return null;
        }
        GifDecoder gifDecoder = this.gifDecoderFactory.build(this.provider, header, byteBuffer, getSampleSize(header, width, height));
        gifDecoder.advance();
        Bitmap firstFrame = gifDecoder.getNextFrame();
        if (firstFrame == null) {
            return null;
        }
        GifDrawable gifDrawable = new GifDrawable(this.context, gifDecoder, this.bitmapPool, UnitTransformation.get(), width, height, firstFrame);
        if (Log.isLoggable("BufferGifDecoder", 2)) {
            Log.v("BufferGifDecoder", "Decoded gif from stream in " + LogTime.getElapsedMillis(startTime));
        }
        return new GifDrawableResource(gifDrawable);
    }

    private static int getSampleSize(GifHeader gifHeader, int targetWidth, int targetHeight) {
        int exactSampleSize = Math.min(gifHeader.getHeight() / targetHeight, gifHeader.getWidth() / targetWidth);
        int sampleSize = Math.max(1, exactSampleSize == 0 ? 0 : Integer.highestOneBit(exactSampleSize));
        if (Log.isLoggable("BufferGifDecoder", 2)) {
            Log.v("BufferGifDecoder", "Downsampling gif, sampleSize: " + sampleSize + ", target dimens: [" + targetWidth + "x" + targetHeight + "], actual dimens: [" + gifHeader.getWidth() + "x" + gifHeader.getHeight() + "]");
        }
        return sampleSize;
    }
}
