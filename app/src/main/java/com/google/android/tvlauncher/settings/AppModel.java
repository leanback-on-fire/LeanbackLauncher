package com.google.android.tvlauncher.settings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.google.android.tvlauncher.data.PackagesWithChannelsObserver;
import com.google.android.tvlauncher.data.TvDataManager;
import com.google.android.tvlauncher.model.ChannelPackage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class AppModel
{
  private Context mContext;
  private LoadAppsCallback mLoadAppsCallback;
  @VisibleForTesting(otherwise=2)
  final PackagesWithChannelsObserver mPackagesObserver = new PackagesWithChannelsObserver()
  {
    public void onPackagesChange()
    {
      AppModel.this.onPackagesDataLoaded();
    }
  };
  private TvDataManager mTvDataManager;
  
  AppModel(Context paramContext)
  {
    this.mContext = paramContext;
    this.mTvDataManager = TvDataManager.getInstance(paramContext);
    this.mTvDataManager.registerPackagesWithChannelsObserver(this.mPackagesObserver);
  }
  
  @VisibleForTesting
  AppModel(Context paramContext, TvDataManager paramTvDataManager)
  {
    this.mContext = paramContext;
    this.mTvDataManager = paramTvDataManager;
  }
  
  private static Drawable getApplicationBanner(String paramString, PackageManager paramPackageManager)
  {
    Object localObject2 = null;
    Object localObject1 = new Intent("android.intent.action.MAIN");
    ((Intent)localObject1).setPackage(paramString).addCategory("android.intent.category.LEANBACK_LAUNCHER");
    Object localObject3 = paramPackageManager.queryIntentActivities((Intent)localObject1, 1);
    localObject1 = localObject2;
    if (localObject3 != null)
    {
      localObject1 = localObject2;
      if (((List)localObject3).size() > 0)
      {
        localObject3 = ((ResolveInfo)((List)localObject3).get(0)).activityInfo;
        localObject1 = localObject2;
        if (localObject3 != null) {
          localObject1 = ((ActivityInfo)localObject3).loadBanner(paramPackageManager);
        }
      }
    }
    localObject2 = localObject1;
    if (localObject1 == null) {}
    try
    {
      localObject2 = paramPackageManager.getApplicationBanner(paramString);
      return (Drawable)localObject2;
    }
    catch (PackageManager.NameNotFoundException paramString) {}
    return (Drawable)localObject1;
  }
  
  private ApplicationInfo getApplicationInfo(String paramString, PackageManager paramPackageManager)
  {
    try
    {
      paramString = paramPackageManager.getApplicationInfo(paramString, 0);
      return paramString;
    }
    catch (PackageManager.NameNotFoundException paramString) {}
    return null;
  }
  
  private void onPackagesDataLoaded()
  {
    if (this.mLoadAppsCallback != null)
    {
      Object localObject = this.mTvDataManager.getPackagesWithChannels();
      ArrayList localArrayList = new ArrayList(((List)localObject).size());
      PackageManager localPackageManager = this.mContext.getPackageManager();
      localObject = ((List)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        ChannelPackage localChannelPackage = (ChannelPackage)((Iterator)localObject).next();
        String str = localChannelPackage.getPackageName();
        ApplicationInfo localApplicationInfo = getApplicationInfo(str, localPackageManager);
        if (localApplicationInfo != null) {
          localArrayList.add(new AppInfo(str, localChannelPackage.getChannelCount(), localApplicationInfo, localPackageManager));
        }
      }
      this.mLoadAppsCallback.onAppsLoaded(localArrayList);
    }
  }
  
  void loadApps(LoadAppsCallback paramLoadAppsCallback)
  {
    this.mLoadAppsCallback = paramLoadAppsCallback;
    this.mTvDataManager.registerPackagesWithChannelsObserver(this.mPackagesObserver);
    if (this.mTvDataManager.isPackagesWithChannelsDataLoaded())
    {
      onPackagesDataLoaded();
      return;
    }
    this.mTvDataManager.loadPackagesWithChannelsData();
  }
  
  void onPause()
  {
    this.mLoadAppsCallback = null;
    this.mTvDataManager.unregisterPackagesWithChannelsObserver(this.mPackagesObserver);
  }
  
  static class AppInfo
    implements Comparable<AppInfo>
  {
    Drawable mBanner;
    Drawable mIcon;
    int mNumberOfChannels;
    final String mPackageName;
    CharSequence mTitle;
    
    AppInfo(String paramString, int paramInt, ApplicationInfo paramApplicationInfo, PackageManager paramPackageManager)
    {
      this.mPackageName = paramString;
      this.mNumberOfChannels = paramInt;
      this.mTitle = paramPackageManager.getApplicationLabel(paramApplicationInfo);
      this.mBanner = AppModel.getApplicationBanner(paramString, paramPackageManager);
      if (this.mBanner == null) {}
      try
      {
        this.mIcon = paramPackageManager.getApplicationIcon(paramString);
        return;
      }
      catch (PackageManager.NameNotFoundException paramString) {}
    }
    
    public int compareTo(@NonNull AppInfo paramAppInfo)
    {
      if (this.mTitle == null)
      {
        if (paramAppInfo.mTitle != null) {
          return 1;
        }
        return 0;
      }
      return this.mTitle.toString().compareToIgnoreCase(paramAppInfo.mTitle.toString());
    }
  }
  
  static abstract interface LoadAppsCallback
  {
    public abstract void onAppsChanged();
    
    public abstract void onAppsLoaded(List<AppModel.AppInfo> paramList);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/settings/AppModel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */