package com.google.android.tvlauncher.home.util;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;

public class ProgramPreviewImageData
{
  private final Bitmap mBitmap;
  private final Bitmap mBlurredBitmap;
  private final Palette mPalette;
  
  ProgramPreviewImageData(@NonNull Bitmap paramBitmap1, @NonNull Bitmap paramBitmap2, @NonNull Palette paramPalette)
  {
    this.mBitmap = paramBitmap1;
    this.mBlurredBitmap = paramBitmap2;
    this.mPalette = paramPalette;
  }
  
  public Bitmap getBitmap()
  {
    return this.mBitmap;
  }
  
  public Bitmap getBlurredBitmap()
  {
    return this.mBlurredBitmap;
  }
  
  public Palette getPalette()
  {
    return this.mPalette;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/util/ProgramPreviewImageData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */