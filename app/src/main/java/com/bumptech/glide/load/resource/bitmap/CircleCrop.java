package com.bumptech.glide.load.resource.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import java.security.MessageDigest;

public class CircleCrop
  extends BitmapTransformation
{
  private static final String ID = "com.bumptech.glide.load.resource.bitmap.CircleCrop.1";
  private static final byte[] ID_BYTES = "com.bumptech.glide.load.resource.bitmap.CircleCrop.1".getBytes(CHARSET);
  private static final int VERSION = 1;
  
  public CircleCrop(Context paramContext)
  {
    super(paramContext);
  }
  
  public CircleCrop(BitmapPool paramBitmapPool)
  {
    super(paramBitmapPool);
  }
  
  public boolean equals(Object paramObject)
  {
    return paramObject instanceof CircleCrop;
  }
  
  public int hashCode()
  {
    return "com.bumptech.glide.load.resource.bitmap.CircleCrop.1".hashCode();
  }
  
  protected Bitmap transform(@NonNull BitmapPool paramBitmapPool, @NonNull Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    return TransformationUtils.circleCrop(paramBitmapPool, paramBitmap, paramInt1, paramInt2);
  }
  
  public void updateDiskCacheKey(MessageDigest paramMessageDigest)
  {
    paramMessageDigest.update(ID_BYTES);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/bitmap/CircleCrop.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */