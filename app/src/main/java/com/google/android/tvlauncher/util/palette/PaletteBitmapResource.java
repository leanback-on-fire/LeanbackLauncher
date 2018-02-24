package com.google.android.tvlauncher.util.palette;

import android.support.annotation.NonNull;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Util;

class PaletteBitmapResource
  implements Resource<PaletteBitmapContainer>
{
  private final BitmapPool mBitmapPool;
  private final PaletteBitmapContainer mPaletteBitmapContainer;
  
  PaletteBitmapResource(@NonNull PaletteBitmapContainer paramPaletteBitmapContainer, @NonNull BitmapPool paramBitmapPool)
  {
    this.mPaletteBitmapContainer = paramPaletteBitmapContainer;
    this.mBitmapPool = paramBitmapPool;
  }
  
  public PaletteBitmapContainer get()
  {
    return this.mPaletteBitmapContainer;
  }
  
  public Class<PaletteBitmapContainer> getResourceClass()
  {
    return PaletteBitmapContainer.class;
  }
  
  public int getSize()
  {
    return Util.getBitmapByteSize(this.mPaletteBitmapContainer.getBitmap());
  }
  
  public void recycle()
  {
    this.mBitmapPool.put(this.mPaletteBitmapContainer.getBitmap());
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/util/palette/PaletteBitmapResource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */