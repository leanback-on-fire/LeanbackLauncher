package com.bumptech.glide.load.resource.bitmap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;

public class LazyBitmapDrawableResource
  implements Resource<BitmapDrawable>
{
  private final Bitmap bitmap;
  private final BitmapPool bitmapPool;
  private final Resources resources;
  
  LazyBitmapDrawableResource(Resources paramResources, BitmapPool paramBitmapPool, Bitmap paramBitmap)
  {
    this.resources = ((Resources)Preconditions.checkNotNull(paramResources));
    this.bitmapPool = ((BitmapPool)Preconditions.checkNotNull(paramBitmapPool));
    this.bitmap = ((Bitmap)Preconditions.checkNotNull(paramBitmap));
  }
  
  public static LazyBitmapDrawableResource obtain(Context paramContext, Bitmap paramBitmap)
  {
    return obtain(paramContext.getResources(), Glide.get(paramContext).getBitmapPool(), paramBitmap);
  }
  
  public static LazyBitmapDrawableResource obtain(Resources paramResources, BitmapPool paramBitmapPool, Bitmap paramBitmap)
  {
    return new LazyBitmapDrawableResource(paramResources, paramBitmapPool, paramBitmap);
  }
  
  public BitmapDrawable get()
  {
    return new BitmapDrawable(this.resources, this.bitmap);
  }
  
  public Class<BitmapDrawable> getResourceClass()
  {
    return BitmapDrawable.class;
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


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/bitmap/LazyBitmapDrawableResource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */