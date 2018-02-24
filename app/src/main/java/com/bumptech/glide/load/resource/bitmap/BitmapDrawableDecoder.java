package com.bumptech.glide.load.resource.bitmap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Preconditions;
import java.io.IOException;

public class BitmapDrawableDecoder<DataType>
  implements ResourceDecoder<DataType, BitmapDrawable>
{
  private final BitmapPool bitmapPool;
  private final ResourceDecoder<DataType, Bitmap> decoder;
  private final Resources resources;
  
  public BitmapDrawableDecoder(Context paramContext, ResourceDecoder<DataType, Bitmap> paramResourceDecoder)
  {
    this(paramContext.getResources(), Glide.get(paramContext).getBitmapPool(), paramResourceDecoder);
  }
  
  public BitmapDrawableDecoder(Resources paramResources, BitmapPool paramBitmapPool, ResourceDecoder<DataType, Bitmap> paramResourceDecoder)
  {
    this.resources = ((Resources)Preconditions.checkNotNull(paramResources));
    this.bitmapPool = ((BitmapPool)Preconditions.checkNotNull(paramBitmapPool));
    this.decoder = ((ResourceDecoder)Preconditions.checkNotNull(paramResourceDecoder));
  }
  
  public Resource<BitmapDrawable> decode(DataType paramDataType, int paramInt1, int paramInt2, Options paramOptions)
    throws IOException
  {
    paramDataType = this.decoder.decode(paramDataType, paramInt1, paramInt2, paramOptions);
    if (paramDataType == null) {
      return null;
    }
    return LazyBitmapDrawableResource.obtain(this.resources, this.bitmapPool, (Bitmap)paramDataType.get());
  }
  
  public boolean handles(DataType paramDataType, Options paramOptions)
    throws IOException
  {
    return this.decoder.handles(paramDataType, paramOptions);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/bitmap/BitmapDrawableDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */