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
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
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
import java.util.concurrent.locks.Lock;

public final class Downsampler
{
  public static final Option<DecodeFormat> DECODE_FORMAT = Option.memory("com.bumptech.glide.load.resource.bitmap.Downsampler.DecodeFormat", DecodeFormat.DEFAULT);
  public static final Option<DownsampleStrategy> DOWNSAMPLE_STRATEGY = Option.memory("com.bumptech.glide.load.resource.bitmap.Downsampler.DownsampleStrategy", DownsampleStrategy.AT_LEAST);
  private static final DecodeCallbacks EMPTY_CALLBACKS;
  private static final String ICO_MIME_TYPE = "image/x-ico";
  private static final int MARK_POSITION = 5242880;
  private static final Set<String> NO_DOWNSAMPLE_PRE_N_MIME_TYPES = Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[] { "image/vnd.wap.wbmp", "image/x-ico" })));
  private static final Queue<BitmapFactory.Options> OPTIONS_QUEUE = Util.createQueue(0);
  private static final String TAG = "Downsampler";
  private static final Set<ImageHeaderParser.ImageType> TYPES_THAT_USE_POOL_PRE_KITKAT;
  private static final String WBMP_MIME_TYPE = "image/vnd.wap.wbmp";
  private final BitmapPool bitmapPool;
  private final ArrayPool byteArrayPool;
  private final DisplayMetrics displayMetrics;
  
  static
  {
    EMPTY_CALLBACKS = new DecodeCallbacks()
    {
      public void onDecodeComplete(BitmapPool paramAnonymousBitmapPool, Bitmap paramAnonymousBitmap)
        throws IOException
      {}
      
      public void onObtainBounds() {}
    };
    TYPES_THAT_USE_POOL_PRE_KITKAT = Collections.unmodifiableSet(EnumSet.of(ImageHeaderParser.ImageType.JPEG, ImageHeaderParser.ImageType.PNG_A, ImageHeaderParser.ImageType.PNG));
  }
  
  public Downsampler(DisplayMetrics paramDisplayMetrics, BitmapPool paramBitmapPool, ArrayPool paramArrayPool)
  {
    this.displayMetrics = ((DisplayMetrics)Preconditions.checkNotNull(paramDisplayMetrics));
    this.bitmapPool = ((BitmapPool)Preconditions.checkNotNull(paramBitmapPool));
    this.byteArrayPool = ((ArrayPool)Preconditions.checkNotNull(paramArrayPool));
  }
  
  static void calculateScaling(DownsampleStrategy paramDownsampleStrategy, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, BitmapFactory.Options paramOptions)
  {
    if ((paramInt2 <= 0) || (paramInt3 <= 0)) {}
    label29:
    label116:
    label119:
    label134:
    label230:
    label432:
    label474:
    label487:
    for (;;)
    {
      return;
      if (paramInt5 == Integer.MIN_VALUE)
      {
        paramInt5 = paramInt3;
        if (paramInt4 != Integer.MIN_VALUE) {
          break label116;
        }
        paramInt4 = paramInt2;
        if ((paramInt1 != 90) && (paramInt1 != 270)) {
          break label119;
        }
      }
      for (float f1 = paramDownsampleStrategy.getScaleFactor(paramInt3, paramInt2, paramInt4, paramInt5);; f1 = paramDownsampleStrategy.getScaleFactor(paramInt2, paramInt3, paramInt4, paramInt5))
      {
        if (f1 > 0.0F) {
          break label134;
        }
        paramDownsampleStrategy = String.valueOf(paramDownsampleStrategy);
        throw new IllegalArgumentException(String.valueOf(paramDownsampleStrategy).length() + 48 + "Cannot scale with factor: " + f1 + " from: " + paramDownsampleStrategy);
        break;
        break label29;
      }
      paramDownsampleStrategy = paramDownsampleStrategy.getSampleSizeRounding(paramInt2, paramInt3, paramInt4, paramInt5);
      if (paramDownsampleStrategy == null) {
        throw new IllegalArgumentException("Cannot round with null rounding");
      }
      int i = (int)(paramInt2 * f1 + 0.5F);
      paramInt1 = (int)(paramInt3 * f1 + 0.5F);
      i = paramInt2 / i;
      paramInt1 = paramInt3 / paramInt1;
      float f2;
      if (paramDownsampleStrategy == DownsampleStrategy.SampleSizeRounding.MEMORY)
      {
        paramInt1 = Math.max(i, paramInt1);
        if ((Build.VERSION.SDK_INT > 23) || (!NO_DOWNSAMPLE_PRE_N_MIME_TYPES.contains(paramOptions.outMimeType))) {
          break label432;
        }
        paramInt1 = 1;
        f2 = paramInt1 * f1;
        paramOptions.inSampleSize = paramInt1;
        if (Build.VERSION.SDK_INT >= 19)
        {
          paramOptions.inTargetDensity = ((int)(1000.0F * f2 + 0.5F));
          paramOptions.inDensity = 1000;
        }
        if (!isScaling(paramOptions)) {
          break label474;
        }
        paramOptions.inScaled = true;
      }
      for (;;)
      {
        if (!Log.isLoggable("Downsampler", 2)) {
          break label487;
        }
        i = paramOptions.inTargetDensity;
        int j = paramOptions.inDensity;
        Log.v("Downsampler", 252 + "Calculate scaling, source: [" + paramInt2 + "x" + paramInt3 + "], target: [" + paramInt4 + "x" + paramInt5 + "], exact scale factor: " + f1 + ", power of 2 sample size: " + paramInt1 + ", adjusted scale factor: " + f2 + ", target density: " + i + ", density: " + j);
        return;
        paramInt1 = Math.min(i, paramInt1);
        break;
        i = Math.max(1, Integer.highestOneBit(paramInt1));
        paramInt1 = i;
        if (paramDownsampleStrategy != DownsampleStrategy.SampleSizeRounding.MEMORY) {
          break label230;
        }
        paramInt1 = i;
        if (i >= 1.0F / f1) {
          break label230;
        }
        paramInt1 = i << 1;
        break label230;
        paramOptions.inTargetDensity = 0;
        paramOptions.inDensity = 0;
      }
    }
  }
  
  private Bitmap decodeFromWrappedStreams(InputStream paramInputStream, BitmapFactory.Options paramOptions, DownsampleStrategy paramDownsampleStrategy, DecodeFormat paramDecodeFormat, int paramInt1, int paramInt2, DecodeCallbacks paramDecodeCallbacks)
    throws IOException
  {
    Object localObject = getDimensions(paramInputStream, paramOptions, paramDecodeCallbacks);
    int i = localObject[0];
    int j = localObject[1];
    localObject = paramOptions.outMimeType;
    int k = getOrientation(paramInputStream);
    int m = TransformationUtils.getExifOrientationDegrees(k);
    paramOptions.inPreferredConfig = getConfig(paramInputStream, paramDecodeFormat);
    if (paramOptions.inPreferredConfig != Bitmap.Config.ARGB_8888) {
      paramOptions.inDither = true;
    }
    calculateScaling(paramDownsampleStrategy, m, i, j, paramInt1, paramInt2, paramOptions);
    paramDownsampleStrategy = downsampleWithSize(paramInputStream, paramOptions, this.bitmapPool, i, j, paramDecodeCallbacks);
    paramDecodeCallbacks.onDecodeComplete(this.bitmapPool, paramDownsampleStrategy);
    if (Log.isLoggable("Downsampler", 2)) {
      logDecode(i, j, (String)localObject, paramOptions, paramDownsampleStrategy, paramInt1, paramInt2);
    }
    paramInputStream = null;
    if (paramDownsampleStrategy != null)
    {
      paramDownsampleStrategy.setDensity(this.displayMetrics.densityDpi);
      paramOptions = TransformationUtils.rotateImageExif(this.bitmapPool, paramDownsampleStrategy, k);
      paramInputStream = paramOptions;
      if (!paramDownsampleStrategy.equals(paramOptions))
      {
        this.bitmapPool.put(paramDownsampleStrategy);
        paramInputStream = paramOptions;
      }
    }
    return paramInputStream;
  }
  
  private static Bitmap decodeStream(InputStream paramInputStream, BitmapFactory.Options paramOptions, DecodeCallbacks paramDecodeCallbacks)
    throws IOException
  {
    if (paramOptions.inJustDecodeBounds) {
      paramInputStream.mark(5242880);
    }
    for (;;)
    {
      i = paramOptions.outWidth;
      j = paramOptions.outHeight;
      paramDecodeCallbacks = paramOptions.outMimeType;
      TransformationUtils.getBitmapDrawableLock().lock();
      try
      {
        Bitmap localBitmap = BitmapFactory.decodeStream(paramInputStream, null, paramOptions);
        TransformationUtils.getBitmapDrawableLock().unlock();
        if (paramOptions.inJustDecodeBounds) {
          paramInputStream.reset();
        }
        return localBitmap;
      }
      catch (IllegalArgumentException paramInputStream)
      {
        throw newIoExceptionForInBitmapAssertion(paramInputStream, i, j, paramDecodeCallbacks, paramOptions);
      }
      finally
      {
        TransformationUtils.getBitmapDrawableLock().unlock();
      }
      paramDecodeCallbacks.onObtainBounds();
    }
  }
  
  private Bitmap downsampleWithSize(InputStream paramInputStream, BitmapFactory.Options paramOptions, BitmapPool paramBitmapPool, int paramInt1, int paramInt2, DecodeCallbacks paramDecodeCallbacks)
    throws IOException
  {
    if (((paramOptions.inSampleSize == 1) || (19 <= Build.VERSION.SDK_INT)) && (shouldUsePool(paramInputStream))) {
      if (!isScaling(paramOptions)) {
        break label257;
      }
    }
    label257:
    for (float f = paramOptions.inTargetDensity / paramOptions.inDensity;; f = 1.0F)
    {
      int i = paramOptions.inSampleSize;
      int j = (int)Math.ceil(paramInt1 / i);
      int k = (int)Math.ceil(paramInt2 / i);
      j = Math.round(j * f);
      k = Math.round(k * f);
      if (Log.isLoggable("Downsampler", 2))
      {
        int m = paramOptions.inTargetDensity;
        int n = paramOptions.inDensity;
        Log.v("Downsampler", 192 + "Calculated target [" + j + "x" + k + "] for source [" + paramInt1 + "x" + paramInt2 + "], sampleSize: " + i + ", targetDensity: " + m + ", density: " + n + ", density multiplier: " + f);
      }
      if ((j > 0) && (k > 0)) {
        setInBitmap(paramOptions, paramBitmapPool, j, k, paramOptions.inPreferredConfig);
      }
      return decodeStream(paramInputStream, paramOptions, paramDecodeCallbacks);
    }
  }
  
  @TargetApi(19)
  private static String getBitmapString(Bitmap paramBitmap)
  {
    if (paramBitmap == null) {
      return null;
    }
    int i;
    if (Build.VERSION.SDK_INT >= 19) {
      i = paramBitmap.getAllocationByteCount();
    }
    for (String str = 14 + " (" + i + ")";; str = "")
    {
      i = paramBitmap.getWidth();
      int j = paramBitmap.getHeight();
      paramBitmap = String.valueOf(paramBitmap.getConfig());
      return String.valueOf(paramBitmap).length() + 26 + String.valueOf(str).length() + "[" + i + "x" + j + "] " + paramBitmap + str;
    }
  }
  
  private Bitmap.Config getConfig(InputStream paramInputStream, DecodeFormat paramDecodeFormat)
    throws IOException
  {
    if ((paramDecodeFormat == DecodeFormat.PREFER_ARGB_8888) || (Build.VERSION.SDK_INT == 16)) {
      return Bitmap.Config.ARGB_8888;
    }
    int i = 0;
    paramInputStream.mark(5242880);
    try
    {
      boolean bool = new ImageHeaderParser(paramInputStream, this.byteArrayPool).hasAlpha();
      i = bool;
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        if (Log.isLoggable("Downsampler", 3))
        {
          paramDecodeFormat = String.valueOf(paramDecodeFormat);
          Log.d("Downsampler", String.valueOf(paramDecodeFormat).length() + 72 + "Cannot determine whether the image has alpha or not from header, format " + paramDecodeFormat, localIOException);
        }
        paramInputStream.reset();
      }
    }
    finally
    {
      paramInputStream.reset();
    }
    if (i != 0) {
      return Bitmap.Config.ARGB_8888;
    }
    return Bitmap.Config.RGB_565;
  }
  
  /* Error */
  @TargetApi(11)
  private static BitmapFactory.Options getDefaultOptions()
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 125	com/bumptech/glide/load/resource/bitmap/Downsampler:OPTIONS_QUEUE	Ljava/util/Queue;
    //   6: astore_0
    //   7: aload_0
    //   8: monitorenter
    //   9: getstatic 125	com/bumptech/glide/load/resource/bitmap/Downsampler:OPTIONS_QUEUE	Ljava/util/Queue;
    //   12: invokeinterface 473 1 0
    //   17: checkcast 212	android/graphics/BitmapFactory$Options
    //   20: astore_1
    //   21: aload_0
    //   22: monitorexit
    //   23: aload_1
    //   24: astore_0
    //   25: aload_1
    //   26: ifnonnull +15 -> 41
    //   29: new 212	android/graphics/BitmapFactory$Options
    //   32: dup
    //   33: invokespecial 474	android/graphics/BitmapFactory$Options:<init>	()V
    //   36: astore_0
    //   37: aload_0
    //   38: invokestatic 478	com/bumptech/glide/load/resource/bitmap/Downsampler:resetOptions	(Landroid/graphics/BitmapFactory$Options;)V
    //   41: ldc 2
    //   43: monitorexit
    //   44: aload_0
    //   45: areturn
    //   46: astore_1
    //   47: aload_0
    //   48: monitorexit
    //   49: aload_1
    //   50: athrow
    //   51: astore_0
    //   52: ldc 2
    //   54: monitorexit
    //   55: aload_0
    //   56: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   51	5	0	localObject2	Object
    //   20	6	1	localOptions	BitmapFactory.Options
    //   46	4	1	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   9	23	46	finally
    //   47	49	46	finally
    //   3	9	51	finally
    //   29	41	51	finally
    //   49	51	51	finally
  }
  
  private static int[] getDimensions(InputStream paramInputStream, BitmapFactory.Options paramOptions, DecodeCallbacks paramDecodeCallbacks)
    throws IOException
  {
    paramOptions.inJustDecodeBounds = true;
    decodeStream(paramInputStream, paramOptions, paramDecodeCallbacks);
    paramOptions.inJustDecodeBounds = false;
    return new int[] { paramOptions.outWidth, paramOptions.outHeight };
  }
  
  @TargetApi(11)
  private static String getInBitmapString(BitmapFactory.Options paramOptions)
  {
    if (Build.VERSION.SDK_INT >= 11) {
      return getBitmapString(paramOptions.inBitmap);
    }
    return null;
  }
  
  private int getOrientation(InputStream paramInputStream)
    throws IOException
  {
    paramInputStream.mark(5242880);
    try
    {
      int i = new ImageHeaderParser(paramInputStream, this.byteArrayPool).getOrientation();
      return i;
    }
    catch (IOException localIOException)
    {
      if (Log.isLoggable("Downsampler", 3)) {
        Log.d("Downsampler", "Cannot determine the image orientation from header", localIOException);
      }
      return -1;
    }
    finally
    {
      paramInputStream.reset();
    }
  }
  
  private static boolean isScaling(BitmapFactory.Options paramOptions)
  {
    return (paramOptions.inTargetDensity > 0) && (paramOptions.inDensity > 0) && (paramOptions.inTargetDensity != paramOptions.inDensity);
  }
  
  private static void logDecode(int paramInt1, int paramInt2, String paramString, BitmapFactory.Options paramOptions, Bitmap paramBitmap, int paramInt3, int paramInt4)
  {
    paramBitmap = String.valueOf(getBitmapString(paramBitmap));
    String str = String.valueOf(getInBitmapString(paramOptions));
    int i = paramOptions.inSampleSize;
    int j = paramOptions.inDensity;
    int k = paramOptions.inTargetDensity;
    paramOptions = String.valueOf(Thread.currentThread().getName());
    Log.v("Downsampler", String.valueOf(paramBitmap).length() + 172 + String.valueOf(paramString).length() + String.valueOf(str).length() + String.valueOf(paramOptions).length() + "Decoded " + paramBitmap + " from [" + paramInt1 + "x" + paramInt2 + "] " + paramString + " with inBitmap " + str + " for [" + paramInt3 + "x" + paramInt4 + "], sample size: " + i + ", density: " + j + ", target density: " + k + ", thread: " + paramOptions);
  }
  
  @TargetApi(11)
  private static IOException newIoExceptionForInBitmapAssertion(IllegalArgumentException paramIllegalArgumentException, int paramInt1, int paramInt2, String paramString, BitmapFactory.Options paramOptions)
  {
    paramOptions = String.valueOf(getInBitmapString(paramOptions));
    return new IOException(String.valueOf(paramString).length() + 99 + String.valueOf(paramOptions).length() + "Exception decoding bitmap, outWidth: " + paramInt1 + ", outHeight: " + paramInt2 + ", outMimeType: " + paramString + ", inBitmap: " + paramOptions, paramIllegalArgumentException);
  }
  
  private static void releaseOptions(BitmapFactory.Options paramOptions)
  {
    resetOptions(paramOptions);
    synchronized (OPTIONS_QUEUE)
    {
      OPTIONS_QUEUE.offer(paramOptions);
      return;
    }
  }
  
  @TargetApi(11)
  private static void resetOptions(BitmapFactory.Options paramOptions)
  {
    paramOptions.inTempStorage = null;
    paramOptions.inDither = false;
    paramOptions.inScaled = false;
    paramOptions.inSampleSize = 1;
    paramOptions.inPreferredConfig = null;
    paramOptions.inJustDecodeBounds = false;
    paramOptions.inDensity = 0;
    paramOptions.inTargetDensity = 0;
    paramOptions.outWidth = 0;
    paramOptions.outHeight = 0;
    paramOptions.outMimeType = null;
    if (11 <= Build.VERSION.SDK_INT)
    {
      paramOptions.inBitmap = null;
      paramOptions.inMutable = true;
    }
  }
  
  @TargetApi(11)
  private static void setInBitmap(BitmapFactory.Options paramOptions, BitmapPool paramBitmapPool, int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    if (11 <= Build.VERSION.SDK_INT) {
      paramOptions.inBitmap = paramBitmapPool.getDirty(paramInt1, paramInt2, paramConfig);
    }
  }
  
  private boolean shouldUsePool(InputStream paramInputStream)
    throws IOException
  {
    if (Build.VERSION.SDK_INT >= 19) {
      return true;
    }
    paramInputStream.mark(5242880);
    try
    {
      ImageHeaderParser.ImageType localImageType = new ImageHeaderParser(paramInputStream, this.byteArrayPool).getType();
      boolean bool = TYPES_THAT_USE_POOL_PRE_KITKAT.contains(localImageType);
      return bool;
    }
    catch (IOException localIOException)
    {
      if (Log.isLoggable("Downsampler", 3)) {
        Log.d("Downsampler", "Cannot determine the image type from header", localIOException);
      }
      return false;
    }
    finally
    {
      paramInputStream.reset();
    }
  }
  
  public Resource<Bitmap> decode(InputStream paramInputStream, int paramInt1, int paramInt2, Options paramOptions)
    throws IOException
  {
    return decode(paramInputStream, paramInt1, paramInt2, paramOptions, EMPTY_CALLBACKS);
  }
  
  public Resource<Bitmap> decode(InputStream paramInputStream, int paramInt1, int paramInt2, Options paramOptions, DecodeCallbacks paramDecodeCallbacks)
    throws IOException
  {
    Preconditions.checkArgument(paramInputStream.markSupported(), "You must provide an InputStream that supports mark()");
    byte[] arrayOfByte = (byte[])this.byteArrayPool.get(65536, byte[].class);
    BitmapFactory.Options localOptions = getDefaultOptions();
    localOptions.inTempStorage = arrayOfByte;
    DecodeFormat localDecodeFormat = (DecodeFormat)paramOptions.get(DECODE_FORMAT);
    paramOptions = (DownsampleStrategy)paramOptions.get(DOWNSAMPLE_STRATEGY);
    try
    {
      paramInputStream = BitmapResource.obtain(decodeFromWrappedStreams(paramInputStream, localOptions, paramOptions, localDecodeFormat, paramInt1, paramInt2, paramDecodeCallbacks), this.bitmapPool);
      return paramInputStream;
    }
    finally
    {
      releaseOptions(localOptions);
      this.byteArrayPool.put(arrayOfByte, byte[].class);
    }
  }
  
  public boolean handles(InputStream paramInputStream)
  {
    return true;
  }
  
  public boolean handles(ByteBuffer paramByteBuffer)
  {
    return true;
  }
  
  public static abstract interface DecodeCallbacks
  {
    public abstract void onDecodeComplete(BitmapPool paramBitmapPool, Bitmap paramBitmap)
      throws IOException;
    
    public abstract void onObtainBounds();
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/bitmap/Downsampler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */