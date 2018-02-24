package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;

public class BitmapResource
  implements Resource<Bitmap>
{
  private final Bitmap bitmap;
  private final BitmapPool bitmapPool;
  
  public BitmapResource(Bitmap paramBitmap, BitmapPool paramBitmapPool)
  {
    this.bitmap = ((Bitmap)Preconditions.checkNotNull(paramBitmap, "Bitmap must not be null"));
    this.bitmapPool = ((BitmapPool)Preconditions.checkNotNull(paramBitmapPool, "BitmapPool must not be null"));
  }
  
  @Nullable
  public static BitmapResource obtain(@Nullable Bitmap paramBitmap, BitmapPool paramBitmapPool)
  {
    if (paramBitmap == null) {
      return null;
    }
    return new BitmapResource(paramBitmap, paramBitmapPool);
  }
  
  public Bitmap get()
  {
    return this.bitmap;
  }
  
  public Class<Bitmap> getResourceClass()
  {
    return Bitmap.class;
  }
  
  public int getSize()
  {
    return Util.getBitmapByteSize(this.bitmap);
  }
  
  public void recycle()
  {
    this.bitmapPool.put(this.bitmap);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/bitmap/BitmapResource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */