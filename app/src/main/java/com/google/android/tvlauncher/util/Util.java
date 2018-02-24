package com.google.android.tvlauncher.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.accessibility.AccessibilityManager;
import java.util.List;

public class Util
{
  private static final String TAG = "TVLauncher.Util";
  
  public static boolean isAccessibilityEnabled(Context paramContext)
  {
    boolean bool2 = true;
    paramContext = (AccessibilityManager)paramContext.getSystemService("accessibility");
    boolean bool1;
    if ((paramContext == null) || (!paramContext.isEnabled())) {
      bool1 = false;
    }
    do
    {
      do
      {
        return bool1;
        bool1 = bool2;
      } while (paramContext.isTouchExplorationEnabled());
      bool1 = bool2;
    } while (!paramContext.getEnabledAccessibilityServiceList(16).isEmpty());
    return false;
  }
  
  public static boolean isRtl(Context paramContext)
  {
    return paramContext.getResources().getConfiguration().getLayoutDirection() == 1;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/util/Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */