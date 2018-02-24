package com.bumptech.glide.load.engine.bitmap_recycle;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.support.annotation.NonNull;

public class BitmapPoolAdapter
  implements BitmapPool
{
  public void clearMemory() {}
  
  @NonNull
  public Bitmap get(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    return Bitmap.createBitmap(paramInt1, paramInt2, paramConfig);
  }
  
  @NonNull
  public Bitmap getDirty(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    return get(paramInt1, paramInt2, paramConfig);
  }
  
  public int getMaxSize()
  {
    return 0;
  }
  
  public void put(Bitmap paramBitmap)
  {
    paramBitmap.recycle();
  }
  
  public void setSizeMultiplier(float paramFloat) {}
  
  public void trimMemory(int paramInt) {}
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/engine/bitmap_recycle/BitmapPoolAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */