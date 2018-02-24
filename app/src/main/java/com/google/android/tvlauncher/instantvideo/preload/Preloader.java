package com.google.android.tvlauncher.instantvideo.preload;

public abstract class Preloader
{
  public abstract void startPreload(OnPreloadFinishedListener paramOnPreloadFinishedListener);
  
  public abstract void stopPreload();
  
  public static abstract interface OnPreloadFinishedListener
  {
    public abstract void onPreloadFinishedListener();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/instantvideo/preload/Preloader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */