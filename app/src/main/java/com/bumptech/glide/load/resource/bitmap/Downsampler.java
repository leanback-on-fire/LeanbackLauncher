package com.bumptech.glide.load.resource.bitmap;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.util.Log;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy.SampleSizeRounding;
import com.bumptech.glide.load.resource.bitmap.ImageHeaderParser.ImageType;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public final class Downsampler {
    public static final Option<DecodeFormat> DECODE_FORMAT = Option.memory("com.bumptech.glide.load.resource.bitmap.Downsampler.DecodeFormat", DecodeFormat.DEFAULT);
    public static final Option<DownsampleStrategy> DOWNSAMPLE_STRATEGY = Option.memory("com.bumptech.glide.load.resource.bitmap.Downsampler.DownsampleStrategy", DownsampleStrategy.AT_LEAST);
    private static final DecodeCallbacks EMPTY_CALLBACKS = new DecodeCallbacks() {
        public void onObtainBounds() {
        }

        public void onDecodeComplete(BitmapPool bitmapPool, Bitmap downsampled) throws IOException {
        }
    };
    private static final Set<String> NO_DOWNSAMPLE_PRE_N_MIME_TYPES = Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[]{"image/vnd.wap.wbmp", "image/x-ico"})));
    private static final Queue<Options> OPTIONS_QUEUE = Util.createQueue(0);
    private static final Set<ImageType> TYPES_THAT_USE_POOL_PRE_KITKAT = Collections.unmodifiableSet(EnumSet.of(ImageType.JPEG, ImageType.PNG_A, ImageType.PNG));
    private final BitmapPool bitmapPool;
    private final ArrayPool byteArrayPool;
    private final DisplayMetrics displayMetrics;

    public interface DecodeCallbacks {
        void onDecodeComplete(BitmapPool bitmapPool, Bitmap bitmap) throws IOException;

        void onObtainBounds();
    }

    public Downsampler(DisplayMetrics displayMetrics, BitmapPool bitmapPool, ArrayPool byteArrayPool) {
        this.displayMetrics = (DisplayMetrics) Preconditions.checkNotNull(displayMetrics);
        this.bitmapPool = (BitmapPool) Preconditions.checkNotNull(bitmapPool);
        this.byteArrayPool = (ArrayPool) Preconditions.checkNotNull(byteArrayPool);
    }

    public boolean handles(InputStream is) {
        return true;
    }

    public boolean handles(ByteBuffer byteBuffer) {
        return true;
    }

    public Resource<Bitmap> decode(InputStream is, int outWidth, int outHeight, com.bumptech.glide.load.Options options) throws IOException {
        return decode(is, outWidth, outHeight, options, EMPTY_CALLBACKS);
    }

    public Resource<Bitmap> decode(InputStream is, int requestedWidth, int requestedHeight, com.bumptech.glide.load.Options options, DecodeCallbacks callbacks) throws IOException {
        Preconditions.checkArgument(is.markSupported(), "You must provide an InputStream that supports mark()");
        byte[] bytesForOptions = (byte[]) this.byteArrayPool.get(65536, byte[].class);
        Options bitmapFactoryOptions = getDefaultOptions();
        bitmapFactoryOptions.inTempStorage = bytesForOptions;
        try {
            Resource<Bitmap> obtain = BitmapResource.obtain(decodeFromWrappedStreams(is, bitmapFactoryOptions, (DownsampleStrategy) options.get(DOWNSAMPLE_STRATEGY), (DecodeFormat) options.get(DECODE_FORMAT), requestedWidth, requestedHeight, callbacks), this.bitmapPool);
            return obtain;
        } finally {
            releaseOptions(bitmapFactoryOptions);
            this.byteArrayPool.put(bytesForOptions, byte[].class);
        }
    }

    private Bitmap decodeFromWrappedStreams(InputStream is, Options options, DownsampleStrategy downsampleStrategy, DecodeFormat decodeFormat, int requestedWidth, int requestedHeight, DecodeCallbacks callbacks) throws IOException {
        int[] sourceDimensions = getDimensions(is, options, callbacks);
        int sourceWidth = sourceDimensions[0];
        int sourceHeight = sourceDimensions[1];
        String sourceMimeType = options.outMimeType;
        int orientation = getOrientation(is);
        int degreesToRotate = TransformationUtils.getExifOrientationDegrees(orientation);
        options.inPreferredConfig = getConfig(is, decodeFormat);
        if (options.inPreferredConfig != Config.ARGB_8888) {
            options.inDither = true;
        }
        calculateScaling(downsampleStrategy, degreesToRotate, sourceWidth, sourceHeight, requestedWidth, requestedHeight, options);
        Bitmap downsampled = downsampleWithSize(is, options, this.bitmapPool, sourceWidth, sourceHeight, callbacks);
        callbacks.onDecodeComplete(this.bitmapPool, downsampled);
        if (Log.isLoggable("Downsampler", 2)) {
            logDecode(sourceWidth, sourceHeight, sourceMimeType, options, downsampled, requestedWidth, requestedHeight);
        }
        Bitmap rotated = null;
        if (downsampled != null) {
            downsampled.setDensity(this.displayMetrics.densityDpi);
            rotated = TransformationUtils.rotateImageExif(this.bitmapPool, downsampled, orientation);
            if (!downsampled.equals(rotated)) {
                this.bitmapPool.put(downsampled);
            }
        }
        return rotated;
    }

    static void calculateScaling(DownsampleStrategy downsampleStrategy, int degreesToRotate, int sourceWidth, int sourceHeight, int requestedWidth, int requestedHeight, Options options) {
        if (sourceWidth > 0 && sourceHeight > 0) {
            int targetHeight;
            int targetWidth;
            float exactScaleFactor;
            if (requestedHeight == Integer.MIN_VALUE) {
                targetHeight = sourceHeight;
            } else {
                targetHeight = requestedHeight;
            }
            if (requestedWidth == Integer.MIN_VALUE) {
                targetWidth = sourceWidth;
            } else {
                targetWidth = requestedWidth;
            }
            if (degreesToRotate == 90 || degreesToRotate == 270) {
                exactScaleFactor = downsampleStrategy.getScaleFactor(sourceHeight, sourceWidth, targetWidth, targetHeight);
            } else {
                exactScaleFactor = downsampleStrategy.getScaleFactor(sourceWidth, sourceHeight, targetWidth, targetHeight);
            }
            if (exactScaleFactor <= 0.0f) {
                String valueOf = String.valueOf(downsampleStrategy);
                throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 48).append("Cannot scale with factor: ").append(exactScaleFactor).append(" from: ").append(valueOf).toString());
            }
            SampleSizeRounding rounding = downsampleStrategy.getSampleSizeRounding(sourceWidth, sourceHeight, targetWidth, targetHeight);
            if (rounding == null) {
                throw new IllegalArgumentException("Cannot round with null rounding");
            }
            int scaleFactor;
            int powerOfTwoSampleSize;
            int widthScaleFactor = sourceWidth / ((int) ((((float) sourceWidth) * exactScaleFactor) + 0.5f));
            int heightScaleFactor = sourceHeight / ((int) ((((float) sourceHeight) * exactScaleFactor) + 0.5f));
            if (rounding == SampleSizeRounding.MEMORY) {
                scaleFactor = Math.max(widthScaleFactor, heightScaleFactor);
            } else {
                scaleFactor = Math.min(widthScaleFactor, heightScaleFactor);
            }
            if (VERSION.SDK_INT > 23 || !NO_DOWNSAMPLE_PRE_N_MIME_TYPES.contains(options.outMimeType)) {
                powerOfTwoSampleSize = Math.max(1, Integer.highestOneBit(scaleFactor));
                if (rounding == SampleSizeRounding.MEMORY && ((float) powerOfTwoSampleSize) < 1.0f / exactScaleFactor) {
                    powerOfTwoSampleSize <<= 1;
                }
            } else {
                powerOfTwoSampleSize = 1;
            }
            float adjustedScaleFactor = ((float) powerOfTwoSampleSize) * exactScaleFactor;
            options.inSampleSize = powerOfTwoSampleSize;
            if (VERSION.SDK_INT >= 19) {
                options.inTargetDensity = (int) ((1000.0f * adjustedScaleFactor) + 0.5f);
                options.inDensity = 1000;
            }
            if (isScaling(options)) {
                options.inScaled = true;
            } else {
                options.inTargetDensity = 0;
                options.inDensity = 0;
            }
            if (Log.isLoggable("Downsampler", 2)) {
                int i = options.inTargetDensity;
                Log.v("Downsampler", "Calculate scaling, source: [" + sourceWidth + "x" + sourceHeight + "], target: [" + targetWidth + "x" + targetHeight + "], exact scale factor: " + exactScaleFactor + ", power of 2 sample size: " + powerOfTwoSampleSize + ", adjusted scale factor: " + adjustedScaleFactor + ", target density: " + i + ", density: " + options.inDensity);
            }
        }
    }

    private int getOrientation(InputStream is) throws IOException {
        is.mark(5242880);
        int orientation = -1;
        try {
            orientation = new ImageHeaderParser(is, this.byteArrayPool).getOrientation();
            is.reset();
        } catch (IOException e) {
            if (Log.isLoggable("Downsampler", 3)) {
                Log.d("Downsampler", "Cannot determine the image orientation from header", e);
            }
            is.reset();
        } catch (Throwable th) {
            is.reset();
        }
        return orientation;
    }

    private Bitmap downsampleWithSize(InputStream is, Options options, BitmapPool pool, int sourceWidth, int sourceHeight, DecodeCallbacks callbacks) throws IOException {
        if ((options.inSampleSize == 1 || 19 <= VERSION.SDK_INT) && shouldUsePool(is)) {
            float densityMultiplier = isScaling(options) ? ((float) options.inTargetDensity) / ((float) options.inDensity) : 1.0f;
            int sampleSize = options.inSampleSize;
            int downsampledHeight = (int) Math.ceil((double) (((float) sourceHeight) / ((float) sampleSize)));
            int expectedWidth = Math.round(((float) ((int) Math.ceil((double) (((float) sourceWidth) / ((float) sampleSize))))) * densityMultiplier);
            int expectedHeight = Math.round(((float) downsampledHeight) * densityMultiplier);
            if (Log.isLoggable("Downsampler", 2)) {
                Log.v("Downsampler", "Calculated target [" + expectedWidth + "x" + expectedHeight + "] for source [" + sourceWidth + "x" + sourceHeight + "], sampleSize: " + sampleSize + ", targetDensity: " + options.inTargetDensity + ", density: " + options.inDensity + ", density multiplier: " + densityMultiplier);
            }
            if (expectedWidth > 0 && expectedHeight > 0) {
                setInBitmap(options, pool, expectedWidth, expectedHeight, options.inPreferredConfig);
            }
        }
        return decodeStream(is, options, callbacks);
    }

    private boolean shouldUsePool(InputStream is) throws IOException {
        if (VERSION.SDK_INT >= 19) {
            return true;
        }
        is.mark(5242880);
        try {
            boolean contains = TYPES_THAT_USE_POOL_PRE_KITKAT.contains(new ImageHeaderParser(is, this.byteArrayPool).getType());
            is.reset();
            return contains;
        } catch (IOException e) {
            if (Log.isLoggable("Downsampler", 3)) {
                Log.d("Downsampler", "Cannot determine the image type from header", e);
            }
            is.reset();
            return false;
        } catch (Throwable th) {
            is.reset();
        }
    }

    private Config getConfig(InputStream is, DecodeFormat format) throws IOException {
        if (format == DecodeFormat.PREFER_ARGB_8888 || VERSION.SDK_INT == 16) {
            return Config.ARGB_8888;
        }
        boolean hasAlpha = false;
        is.mark(5242880);
        try {
            hasAlpha = new ImageHeaderParser(is, this.byteArrayPool).hasAlpha();
            is.reset();
        } catch (IOException e) {
            if (Log.isLoggable("Downsampler", 3)) {
                String valueOf = String.valueOf(format);
                Log.d("Downsampler", new StringBuilder(String.valueOf(valueOf).length() + 72).append("Cannot determine whether the image has alpha or not from header, format ").append(valueOf).toString(), e);
            }
            is.reset();
        } catch (Throwable th) {
            is.reset();
        }
        if (hasAlpha) {
            return Config.ARGB_8888;
        }
        return Config.RGB_565;
    }

    private static int[] getDimensions(InputStream is, Options options, DecodeCallbacks decodeCallbacks) throws IOException {
        options.inJustDecodeBounds = true;
        decodeStream(is, options, decodeCallbacks);
        options.inJustDecodeBounds = false;
        return new int[]{options.outWidth, options.outHeight};
    }

    private static Bitmap decodeStream(InputStream is, Options options, DecodeCallbacks callbacks) throws IOException {
        if (options.inJustDecodeBounds) {
            is.mark(5242880);
        } else {
            callbacks.onObtainBounds();
        }
        int sourceWidth = options.outWidth;
        int sourceHeight = options.outHeight;
        String outMimeType = options.outMimeType;
        TransformationUtils.getBitmapDrawableLock().lock();
        try {
            Bitmap result = BitmapFactory.decodeStream(is, null, options);
            TransformationUtils.getBitmapDrawableLock().unlock();
            if (options.inJustDecodeBounds) {
                is.reset();
            }
            return result;
        } catch (IllegalArgumentException e) {
            throw newIoExceptionForInBitmapAssertion(e, sourceWidth, sourceHeight, outMimeType, options);
        } catch (Throwable th) {
            TransformationUtils.getBitmapDrawableLock().unlock();
        }
    }

    private static boolean isScaling(Options options) {
        return options.inTargetDensity > 0 && options.inDensity > 0 && options.inTargetDensity != options.inDensity;
    }

    private static void logDecode(int sourceWidth, int sourceHeight, String outMimeType, Options options, Bitmap result, int requestedWidth, int requestedHeight) {
        String valueOf = String.valueOf(getBitmapString(result));
        String valueOf2 = String.valueOf(getInBitmapString(options));
        int i = options.inSampleSize;
        int i2 = options.inDensity;
        int i3 = options.inTargetDensity;
        String valueOf3 = String.valueOf(Thread.currentThread().getName());
        Log.v("Downsampler", new StringBuilder((((String.valueOf(valueOf).length() + 172) + String.valueOf(outMimeType).length()) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length()).append("Decoded ").append(valueOf).append(" from [").append(sourceWidth).append("x").append(sourceHeight).append("] ").append(outMimeType).append(" with inBitmap ").append(valueOf2).append(" for [").append(requestedWidth).append("x").append(requestedHeight).append("], sample size: ").append(i).append(", density: ").append(i2).append(", target density: ").append(i3).append(", thread: ").append(valueOf3).toString());
    }

    @TargetApi(11)
    private static String getInBitmapString(Options options) {
        return VERSION.SDK_INT >= 11 ? getBitmapString(options.inBitmap) : null;
    }

    @TargetApi(19)
    private static String getBitmapString(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        String sizeString;
        if (VERSION.SDK_INT >= 19) {
            sizeString = " (" + bitmap.getAllocationByteCount() + ")";
        } else {
            sizeString = "";
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        String valueOf = String.valueOf(bitmap.getConfig());
        return new StringBuilder((String.valueOf(valueOf).length() + 26) + String.valueOf(sizeString).length()).append("[").append(width).append("x").append(height).append("] ").append(valueOf).append(sizeString).toString();
    }

    @TargetApi(11)
    private static IOException newIoExceptionForInBitmapAssertion(IllegalArgumentException e, int outWidth, int outHeight, String outMimeType, Options options) {
        String valueOf = String.valueOf(getInBitmapString(options));
        return new IOException(new StringBuilder((String.valueOf(outMimeType).length() + 99) + String.valueOf(valueOf).length()).append("Exception decoding bitmap, outWidth: ").append(outWidth).append(", outHeight: ").append(outHeight).append(", outMimeType: ").append(outMimeType).append(", inBitmap: ").append(valueOf).toString(), e);
    }

    @TargetApi(11)
    private static void setInBitmap(Options options, BitmapPool bitmapPool, int width, int height, Config config) {
        if (11 <= VERSION.SDK_INT) {
            options.inBitmap = bitmapPool.getDirty(width, height, config);
        }
    }

    @TargetApi(11)
    private static synchronized Options getDefaultOptions() {
        Options decodeBitmapOptions;
        synchronized (Downsampler.class) {
            synchronized (OPTIONS_QUEUE) {
                decodeBitmapOptions = (Options) OPTIONS_QUEUE.poll();
            }
            if (decodeBitmapOptions == null) {
                decodeBitmapOptions = new Options();
                resetOptions(decodeBitmapOptions);
            }
        }
        return decodeBitmapOptions;
    }

    private static void releaseOptions(Options decodeBitmapOptions) {
        resetOptions(decodeBitmapOptions);
        synchronized (OPTIONS_QUEUE) {
            OPTIONS_QUEUE.offer(decodeBitmapOptions);
        }
    }

    @TargetApi(11)
    private static void resetOptions(Options decodeBitmapOptions) {
        decodeBitmapOptions.inTempStorage = null;
        decodeBitmapOptions.inDither = false;
        decodeBitmapOptions.inScaled = false;
        decodeBitmapOptions.inSampleSize = 1;
        decodeBitmapOptions.inPreferredConfig = null;
        decodeBitmapOptions.inJustDecodeBounds = false;
        decodeBitmapOptions.inDensity = 0;
        decodeBitmapOptions.inTargetDensity = 0;
        decodeBitmapOptions.outWidth = 0;
        decodeBitmapOptions.outHeight = 0;
        decodeBitmapOptions.outMimeType = null;
        if (11 <= VERSION.SDK_INT) {
            decodeBitmapOptions.inBitmap = null;
            decodeBitmapOptions.inMutable = true;
        }
    }
}
