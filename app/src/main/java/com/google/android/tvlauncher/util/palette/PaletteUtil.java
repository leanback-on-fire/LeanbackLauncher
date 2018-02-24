package com.google.android.tvlauncher.util.palette;

import android.content.Context;
import android.graphics.Bitmap;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;

public class PaletteUtil
{
  private static boolean sPaletteTranscoderRegistered = false;
  
  public static void registerGlidePaletteTranscoder(Context paramContext)
  {
    if (!sPaletteTranscoderRegistered)
    {
      Glide.get(paramContext).getRegistry().register(Bitmap.class, PaletteBitmapContainer.class, new PaletteBitmapTranscoder(paramContext));
      sPaletteTranscoderRegistered = true;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/util/palette/PaletteUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */