package com.bumptech.glide.load.resource.gif;

import android.content.Context;
import android.graphics.Bitmap;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.util.Preconditions;
import java.security.MessageDigest;

public class GifDrawableTransformation
  implements Transformation<GifDrawable>
{
  private final BitmapPool bitmapPool;
  private final Transformation<Bitmap> wrapped;
  
  public GifDrawableTransformation(Context paramContext, Transformation<Bitmap> paramTransformation)
  {
    this(paramTransformation, Glide.get(paramContext).getBitmapPool());
  }
  
  public GifDrawableTransformation(Transformation<Bitmap> paramTransformation, BitmapPool paramBitmapPool)
  {
    this.wrapped = ((Transformation)Preconditions.checkNotNull(paramTransformation));
    this.bitmapPool = ((BitmapPool)Preconditions.checkNotNull(paramBitmapPool));
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof GifDrawableTransformation))
    {
      paramObject = (GifDrawableTransformation)paramObject;
      return this.wrapped.equals(((GifDrawableTransformation)paramObject).wrapped);
    }
    return false;
  }
  
  public int hashCode()
  {
    return this.wrapped.hashCode();
  }
  
  public Resource<GifDrawable> transform(Resource<GifDrawable> paramResource, int paramInt1, int paramInt2)
  {
    GifDrawable localGifDrawable = (GifDrawable)paramResource.get();
    Object localObject = new BitmapResource(localGifDrawable.getFirstFrame(), this.bitmapPool);
    Resource localResource = this.wrapped.transform((Resource)localObject, paramInt1, paramInt2);
    if (!localObject.equals(localResource)) {
      ((Resource)localObject).recycle();
    }
    localObject = (Bitmap)localResource.get();
    localGifDrawable.setFrameTransformation(this.wrapped, (Bitmap)localObject);
    return paramResource;
  }
  
  public void updateDiskCacheKey(MessageDigest paramMessageDigest)
  {
    this.wrapped.updateDiskCacheKey(paramMessageDigest);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/gif/GifDrawableTransformation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */