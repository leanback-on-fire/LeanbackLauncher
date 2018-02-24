package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.ParcelFileDescriptor;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Option.CacheKeyUpdater;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class VideoBitmapDecoder implements ResourceDecoder<ParcelFileDescriptor, Bitmap> {
    private static final MediaMetadataRetrieverFactory DEFAULT_FACTORY = new MediaMetadataRetrieverFactory();
    public static final Option<Integer> FRAME_OPTION = Option.disk("com.bumptech.glide.load.resource.bitmap.VideoBitmapDecode.FrameOption", null, new CacheKeyUpdater<Integer>() {
        private final ByteBuffer buffer = ByteBuffer.allocate(4);

        public void update(byte[] keyBytes, Integer value, MessageDigest messageDigest) {
            if (value != null) {
                messageDigest.update(keyBytes);
                synchronized (this.buffer) {
                    this.buffer.position(0);
                    messageDigest.update(this.buffer.putInt(value.intValue()).array());
                }
            }
        }
    });
    public static final Option<Long> TARGET_FRAME = Option.disk("com.bumptech.glide.load.resource.bitmap.VideoBitmapDecode.TargetFrame", Long.valueOf(-1), new CacheKeyUpdater<Long>() {
        private final ByteBuffer buffer = ByteBuffer.allocate(8);

        public void update(byte[] keyBytes, Long value, MessageDigest messageDigest) {
            messageDigest.update(keyBytes);
            synchronized (this.buffer) {
                this.buffer.position(0);
                messageDigest.update(this.buffer.putLong(value.longValue()).array());
            }
        }
    });
    private final BitmapPool bitmapPool;
    private final MediaMetadataRetrieverFactory factory;

    static class MediaMetadataRetrieverFactory {
        MediaMetadataRetrieverFactory() {
        }

        public MediaMetadataRetriever build() {
            return new MediaMetadataRetriever();
        }
    }

    public VideoBitmapDecoder(BitmapPool bitmapPool) {
        this(bitmapPool, DEFAULT_FACTORY);
    }

    VideoBitmapDecoder(BitmapPool bitmapPool, MediaMetadataRetrieverFactory factory) {
        this.bitmapPool = bitmapPool;
        this.factory = factory;
    }

    public boolean handles(ParcelFileDescriptor data, Options options) {
        boolean z;
        MediaMetadataRetriever retriever = this.factory.build();
        try {
            retriever.setDataSource(data.getFileDescriptor());
            z = true;
        } catch (RuntimeException e) {
            z = false;
        } finally {
            retriever.release();
        }
        return z;
    }

    public Resource<Bitmap> decode(ParcelFileDescriptor resource, int outWidth, int outHeight, Options options) throws IOException {
        long frameTimeMicros = ((Long) options.get(TARGET_FRAME)).longValue();
        if (frameTimeMicros >= 0 || frameTimeMicros == -1) {
            Integer frameOption = (Integer) options.get(FRAME_OPTION);
            MediaMetadataRetriever mediaMetadataRetriever = this.factory.build();
            try {
                Bitmap result;
                mediaMetadataRetriever.setDataSource(resource.getFileDescriptor());
                if (frameTimeMicros == -1) {
                    result = mediaMetadataRetriever.getFrameAtTime();
                } else if (frameOption == null) {
                    result = mediaMetadataRetriever.getFrameAtTime(frameTimeMicros);
                } else {
                    result = mediaMetadataRetriever.getFrameAtTime(frameTimeMicros, frameOption.intValue());
                }
                mediaMetadataRetriever.release();
                resource.close();
                return BitmapResource.obtain(result, this.bitmapPool);
            } catch (Throwable th) {
                mediaMetadataRetriever.release();
            }
        } else {
            throw new IllegalArgumentException("Requested frame must be non-negative, or DEFAULT_FRAME, given: " + frameTimeMicros);
        }
    }
}
