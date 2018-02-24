package com.bumptech.glide.load.resource.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Util;

public abstract class BitmapTransformation
  implements Transformation<Bitmap>
{
  private final BitmapPool bitmapPool;
  
  public BitmapTransformation(Context paramContext)
  {
    this(Glide.get(paramContext).getBitmapPool());
  }
  
  public BitmapTransformation(BitmapPool paramBitmapPool)
  {
    this.bitmapPool = paramBitmapPool;
  }
  
  protected abstract Bitmap transform(@NonNull BitmapPool paramBitmapPool, @NonNull Bitmap paramBitmap, int paramInt1, int paramInt2);
  
  public final Resource<Bitmap> transform(Resource<Bitmap> paramResource, int paramInt1, int paramInt2)
  {
    if (!Util.isValidDimensions(paramInt1, paramInt2)) {
      throw new IllegalArgumentException(128 + "Cannot apply transformation on width: " + paramInt1 + " or height: " + paramInt2 + " less than or equal to zero and not Target.SIZE_ORIGINAL");
    }
    Bitmap localBitmap1 = (Bitmap)paramResource.get();
    if (paramInt1 == Integer.MIN_VALUE)
    {
      paramInt1 = localBitmap1.getWidth();
      if (paramInt2 != Integer.MIN_VALUE) {
        break label116;
      }
      paramInt2 = localBitmap1.getHeight();
    }
    Bitmap localBitmap2;
    label116:
    for (;;)
    {
      localBitmap2 = transform(this.bitmapPool, localBitmap1, paramInt1, paramInt2);
      if (!localBitmap1.equals(localBitmap2)) {
        break label119;
      }
      return paramResource;
      break;
    }
    label119:
    return BitmapResource.obtain(localBitmap2, this.bitmapPool);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/bitmap/BitmapTransformation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */