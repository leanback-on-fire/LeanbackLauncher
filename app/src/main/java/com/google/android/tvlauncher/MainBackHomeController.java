package com.google.android.tvlauncher;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

public class MainBackHomeController
  implements BackHomeControllerListeners.OnHomePressedListener, BackHomeControllerListeners.OnBackPressedListener, BackHomeControllerListeners.OnBackNotHandledListener
{
  private static MainBackHomeController sInstance = null;
  private BackHomeControllerListeners.OnBackPressedListener mOnBackPressedListener;
  private BackHomeControllerListeners.OnHomePressedListener mOnHomePressedListener;
  
  public static MainBackHomeController getInstance()
  {
    if (sInstance == null) {
      sInstance = new MainBackHomeController();
    }
    return sInstance;
  }
  
  public void onBackNotHandled(Context paramContext) {}
  
  public void onBackPressed(Context paramContext)
  {
    if (this.mOnBackPressedListener != null)
    {
      this.mOnBackPressedListener.onBackPressed(paramContext);
      return;
    }
    onBackNotHandled(paramContext);
  }
  
  public void onHomePressed(Context paramContext)
  {
    if (this.mOnHomePressedListener != null) {
      this.mOnHomePressedListener.onHomePressed(paramContext);
    }
  }
  
  public void setOnBackPressedListener(BackHomeControllerListeners.OnBackPressedListener paramOnBackPressedListener)
  {
    this.mOnBackPressedListener = paramOnBackPressedListener;
  }
  
  public void setOnHomePressedListener(BackHomeControllerListeners.OnHomePressedListener paramOnHomePressedListener)
  {
    this.mOnHomePressedListener = paramOnHomePressedListener;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/MainBackHomeController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */