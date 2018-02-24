package com.bumptech.glide.load.resource.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Preconditions;
import java.security.MessageDigest;

public class BitmapDrawableTransformation
  implements Transformation<BitmapDrawable>
{
  private final BitmapPool bitmapPool;
  private final Context context;
  private final Transformation<Bitmap> wrapped;
  
  public BitmapDrawableTransformation(Context paramContext, Transformation<Bitmap> paramTransformation)
  {
    this(paramContext, Glide.get(paramContext).getBitmapPool(), paramTransformation);
  }
  
  BitmapDrawableTransformation(Context paramContext, BitmapPool paramBitmapPool, Transformation<Bitmap> paramTransformation)
  {
    this.context = paramContext.getApplicationContext();
    this.bitmapPool = ((BitmapPool)Preconditions.checkNotNull(paramBitmapPool));
    this.wrapped = ((Transformation)Preconditions.checkNotNull(paramTransformation));
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof BitmapDrawableTransformation))
    {
      paramObject = (BitmapDrawableTransformation)paramObject;
      return this.wrapped.equals(((BitmapDrawableTransformation)paramObject).wrapped);
    }
    return false;
  }
  
  public int hashCode()
  {
    return this.wrapped.hashCode();
  }
  
  public Resource<BitmapDrawable> transform(Resource<BitmapDrawable> paramResource, int paramInt1, int paramInt2)
  {
    BitmapResource localBitmapResource = BitmapResource.obtain(((BitmapDrawable)paramResource.get()).getBitmap(), this.bitmapPool);
    Resource localResource = this.wrapped.transform(localBitmapResource, paramInt1, paramInt2);
    if (localResource.equals(localBitmapResource)) {
      return paramResource;
    }
    return LazyBitmapDrawableResource.obtain(this.context, (Bitmap)localResource.get());
  }
  
  public void updateDiskCacheKey(MessageDigest paramMessageDigest)
  {
    this.wrapped.updateDiskCacheKey(paramMessageDigest);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/bitmap/BitmapDrawableTransformation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */