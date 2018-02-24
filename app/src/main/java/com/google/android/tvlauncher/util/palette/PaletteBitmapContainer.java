package com.google.android.tvlauncher.util.palette;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;

public class PaletteBitmapContainer
{
  private final Bitmap mBitmap;
  private final Palette mPalette;
  
  PaletteBitmapContainer(@NonNull Bitmap paramBitmap, @NonNull Palette paramPalette)
  {
    this.mBitmap = paramBitmap;
    this.mPalette = paramPalette;
  }
  
  public Bitmap getBitmap()
  {
    return this.mBitmap;
  }
  
  public Palette getPalette()
  {
    return this.mPalette;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/util/palette/PaletteBitmapContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */