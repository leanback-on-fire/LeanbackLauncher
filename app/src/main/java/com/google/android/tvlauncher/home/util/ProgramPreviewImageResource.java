package com.google.android.tvlauncher.home.util;

import android.support.annotation.NonNull;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Util;

class ProgramPreviewImageResource
  implements Resource<ProgramPreviewImageData>
{
  private final BitmapPool mBitmapPool;
  private final ProgramPreviewImageData mProgramPreviewImageData;
  
  ProgramPreviewImageResource(@NonNull ProgramPreviewImageData paramProgramPreviewImageData, @NonNull BitmapPool paramBitmapPool)
  {
    this.mProgramPreviewImageData = paramProgramPreviewImageData;
    this.mBitmapPool = paramBitmapPool;
  }
  
  public ProgramPreviewImageData get()
  {
    return this.mProgramPreviewImageData;
  }
  
  public Class<ProgramPreviewImageData> getResourceClass()
  {
    return ProgramPreviewImageData.class;
  }
  
  public int getSize()
  {
    return Util.getBitmapByteSize(this.mProgramPreviewImageData.getBitmap());
  }
  
  public void recycle()
  {
    this.mBitmapPool.put(this.mProgramPreviewImageData.getBitmap());
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/util/ProgramPreviewImageResource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */