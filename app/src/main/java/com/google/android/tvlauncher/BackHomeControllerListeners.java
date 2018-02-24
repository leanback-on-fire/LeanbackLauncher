package com.google.android.tvlauncher;

import android.content.Context;

public class BackHomeControllerListeners
{
  public static abstract interface OnBackNotHandledListener
  {
    public abstract void onBackNotHandled(Context paramContext);
  }
  
  public static abstract interface OnBackPressedListener
  {
    public abstract void onBackPressed(Context paramContext);
  }
  
  public static abstract interface OnHomeNotHandledListener
  {
    public abstract void onHomeNotHandled(Context paramContext);
  }
  
  public static abstract interface OnHomePressedListener
  {
    public abstract void onHomePressed(Context paramContext);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/BackHomeControllerListeners.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */