package com.google.android.tvlauncher.appsview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;

public class PackageChangedReceiver
  extends BroadcastReceiver
{
  private Listener mListener;
  
  public PackageChangedReceiver(Listener paramListener)
  {
    this.mListener = paramListener;
  }
  
  public static IntentFilter getIntentFilter()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.PACKAGE_ADDED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_FULLY_REMOVED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
    localIntentFilter.addDataScheme("package");
    return localIntentFilter;
  }
  
  private String getPackageName(Intent paramIntent)
  {
    paramIntent = paramIntent.getData();
    if (paramIntent != null) {
      return paramIntent.getSchemeSpecificPart();
    }
    return null;
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    paramContext = getPackageName(paramIntent);
    if ((paramContext == null) || (paramContext.length() == 0)) {}
    String str;
    do
    {
      do
      {
        return;
        str = paramIntent.getAction();
        if ("android.intent.action.PACKAGE_ADDED".equals(str))
        {
          this.mListener.onPackageAdded(paramContext);
          return;
        }
        if ("android.intent.action.PACKAGE_CHANGED".equals(str))
        {
          this.mListener.onPackageChanged(paramContext);
          return;
        }
        if ("android.intent.action.PACKAGE_FULLY_REMOVED".equals(str))
        {
          this.mListener.onPackageFullyRemoved(paramContext);
          return;
        }
        if (!"android.intent.action.PACKAGE_REMOVED".equals(str)) {
          break;
        }
      } while (paramIntent.getBooleanExtra("android.intent.extra.REPLACING", false));
      this.mListener.onPackageRemoved(paramContext);
      return;
    } while (!"android.intent.action.PACKAGE_REPLACED".equals(str));
    this.mListener.onPackageReplaced(paramContext);
  }
  
  public static abstract interface Listener
  {
    public abstract void onPackageAdded(String paramString);
    
    public abstract void onPackageChanged(String paramString);
    
    public abstract void onPackageFullyRemoved(String paramString);
    
    public abstract void onPackageRemoved(String paramString);
    
    public abstract void onPackageReplaced(String paramString);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/PackageChangedReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */