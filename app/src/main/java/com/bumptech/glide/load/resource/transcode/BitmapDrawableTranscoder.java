package com.bumptech.glide.load.resource.transcode;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.LazyBitmapDrawableResource;
import com.bumptech.glide.util.Preconditions;

public class BitmapDrawableTranscoder
  implements ResourceTranscoder<Bitmap, BitmapDrawable>
{
  private final BitmapPool bitmapPool;
  private final Resources resources;
  
  public BitmapDrawableTranscoder(Context paramContext)
  {
    this(paramContext.getResources(), Glide.get(paramContext).getBitmapPool());
  }
  
  public BitmapDrawableTranscoder(Resources paramResources, BitmapPool paramBitmapPool)
  {
    this.resources = ((Resources)Preconditions.checkNotNull(paramResources));
    this.bitmapPool = ((BitmapPool)Preconditions.checkNotNull(paramBitmapPool));
  }
  
  public Resource<BitmapDrawable> transcode(Resource<Bitmap> paramResource)
  {
    return LazyBitmapDrawableResource.obtain(this.resources, this.bitmapPool, (Bitmap)paramResource.get());
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/transcode/BitmapDrawableTranscoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */