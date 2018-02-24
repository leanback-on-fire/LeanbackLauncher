package com.google.android.tvlauncher.home;

import android.view.View;

public abstract interface HomeRow
{
  public abstract View getView();
  
  public abstract void setOnHomeRowRemovedListener(OnHomeRowRemovedListener paramOnHomeRowRemovedListener);
  
  public abstract void setOnHomeRowSelectedListener(OnHomeRowSelectedListener paramOnHomeRowSelectedListener);
  
  public abstract void setOnHomeStateChangeListener(OnHomeStateChangeListener paramOnHomeStateChangeListener);
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/HomeRow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */