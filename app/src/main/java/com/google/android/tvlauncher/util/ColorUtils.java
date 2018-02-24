package com.google.android.tvlauncher.util;

import android.graphics.Color;
import android.support.annotation.ColorInt;

public class ColorUtils
{
  @ColorInt
  public static int darkenColor(@ColorInt int paramInt, float paramFloat)
  {
    float[] arrayOfFloat = new float[3];
    Color.RGBToHSV(Color.red(paramInt), Color.green(paramInt), Color.blue(paramInt), arrayOfFloat);
    arrayOfFloat[2] *= paramFloat;
    return Color.HSVToColor(arrayOfFloat);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/util/ColorUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */