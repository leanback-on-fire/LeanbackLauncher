package com.bumptech.glide.util;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Build.VERSION;
import android.os.Looper;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public final class Util
{
  private static final char[] HEX_CHAR_ARRAY = "0123456789abcdef".toCharArray();
  private static final char[] SHA_256_CHARS = new char[64];
  
  public static void assertBackgroundThread()
  {
    if (!isOnBackgroundThread()) {
      throw new IllegalArgumentException("You must call this method on a background thread");
    }
  }
  
  public static void assertMainThread()
  {
    if (!isOnMainThread()) {
      throw new IllegalArgumentException("You must call this method on the main thread");
    }
  }
  
  public static boolean bothNullOrEqual(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 == null) {
      return paramObject2 == null;
    }
    return paramObject1.equals(paramObject2);
  }
  
  private static String bytesToHex(byte[] paramArrayOfByte, char[] paramArrayOfChar)
  {
    int i = 0;
    while (i < paramArrayOfByte.length)
    {
      int j = paramArrayOfByte[i] & 0xFF;
      paramArrayOfChar[(i * 2)] = HEX_CHAR_ARRAY[(j >>> 4)];
      paramArrayOfChar[(i * 2 + 1)] = HEX_CHAR_ARRAY[(j & 0xF)];
      i += 1;
    }
    return new String(paramArrayOfChar);
  }
  
  public static <T> Queue<T> createQueue(int paramInt)
  {
    return new ArrayDeque(paramInt);
  }
  
  public static int getBitmapByteSize(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    return paramInt1 * paramInt2 * getBytesPerPixel(paramConfig);
  }
  
  @TargetApi(19)
  public static int getBitmapByteSize(Bitmap paramBitmap)
  {
    int i;
    if (paramBitmap.isRecycled())
    {
      String str = String.valueOf(paramBitmap);
      i = paramBitmap.getWidth();
      int j = paramBitmap.getHeight();
      paramBitmap = String.valueOf(paramBitmap.getConfig());
      throw new IllegalStateException(String.valueOf(str).length() + 66 + String.valueOf(paramBitmap).length() + "Cannot obtain size for recycled Bitmap: " + str + "[" + i + "x" + j + "] " + paramBitmap);
    }
    if (Build.VERSION.SDK_INT >= 19) {
      try
      {
        i = paramBitmap.getAllocationByteCount();
        return i;
      }
      catch (NullPointerException localNullPointerException) {}
    }
    return paramBitmap.getHeight() * paramBitmap.getRowBytes();
  }
  
  private static int getBytesPerPixel(Bitmap.Config paramConfig)
  {
    Bitmap.Config localConfig = paramConfig;
    if (paramConfig == null) {
      localConfig = Bitmap.Config.ARGB_8888;
    }
    switch (localConfig)
    {
    default: 
      return 4;
    case ???: 
      return 1;
    }
    return 2;
  }
  
  @Deprecated
  public static int getSize(Bitmap paramBitmap)
  {
    return getBitmapByteSize(paramBitmap);
  }
  
  public static <T> List<T> getSnapshot(Collection<T> paramCollection)
  {
    ArrayList localArrayList = new ArrayList(paramCollection.size());
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext()) {
      localArrayList.add(paramCollection.next());
    }
    return localArrayList;
  }
  
  public static boolean isOnBackgroundThread()
  {
    return !isOnMainThread();
  }
  
  public static boolean isOnMainThread()
  {
    return Looper.myLooper() == Looper.getMainLooper();
  }
  
  private static boolean isValidDimension(int paramInt)
  {
    return (paramInt > 0) || (paramInt == Integer.MIN_VALUE);
  }
  
  public static boolean isValidDimensions(int paramInt1, int paramInt2)
  {
    return (isValidDimension(paramInt1)) && (isValidDimension(paramInt2));
  }
  
  public static String sha256BytesToHex(byte[] paramArrayOfByte)
  {
    synchronized (SHA_256_CHARS)
    {
      paramArrayOfByte = bytesToHex(paramArrayOfByte, SHA_256_CHARS);
      return paramArrayOfByte;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/util/Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */