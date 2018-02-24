package android.support.v17.leanback.system;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.support.annotation.RestrictTo;
import android.support.v17.leanback.widget.ShadowOverlayContainer;
import java.util.Iterator;
import java.util.List;

public class Settings
{
  private static final String ACTION_PARTNER_CUSTOMIZATION = "android.support.v17.leanback.action.PARTNER_CUSTOMIZATION";
  private static final boolean DEBUG = false;
  public static final String PREFER_STATIC_SHADOWS = "PREFER_STATIC_SHADOWS";
  private static final String TAG = "Settings";
  private static Settings sInstance;
  private boolean mPreferStaticShadows;
  
  private Settings(Context paramContext)
  {
    generateShadowSetting(getCustomizations(paramContext));
  }
  
  private void generateShadowSetting(Customizations paramCustomizations)
  {
    if (ShadowOverlayContainer.supportsDynamicShadow())
    {
      this.mPreferStaticShadows = false;
      if (paramCustomizations != null) {
        this.mPreferStaticShadows = paramCustomizations.getBoolean("leanback_prefer_static_shadows", this.mPreferStaticShadows);
      }
      return;
    }
    this.mPreferStaticShadows = true;
  }
  
  private Customizations getCustomizations(Context paramContext)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    Object localObject1 = new Intent("android.support.v17.leanback.action.PARTNER_CUSTOMIZATION");
    paramContext = null;
    Object localObject2 = null;
    Iterator localIterator = localPackageManager.queryBroadcastReceivers((Intent)localObject1, 0).iterator();
    for (;;)
    {
      localObject1 = paramContext;
      String str;
      if (localIterator.hasNext())
      {
        localObject2 = (ResolveInfo)localIterator.next();
        str = ((ResolveInfo)localObject2).activityInfo.packageName;
        localObject1 = paramContext;
        if (str != null)
        {
          localObject1 = paramContext;
          if (!isSystemApp((ResolveInfo)localObject2)) {}
        }
      }
      try
      {
        localObject1 = localPackageManager.getResourcesForApplication(str);
        localObject2 = str;
        paramContext = (Context)localObject1;
        if (localObject1 == null) {
          continue;
        }
        localObject2 = str;
        if (localObject1 == null) {
          return null;
        }
        return new Customizations((Resources)localObject1, (String)localObject2);
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        for (;;)
        {
          Context localContext = paramContext;
        }
      }
    }
  }
  
  public static Settings getInstance(Context paramContext)
  {
    if (sInstance == null) {
      sInstance = new Settings(paramContext);
    }
    return sInstance;
  }
  
  private static boolean isSystemApp(ResolveInfo paramResolveInfo)
  {
    return (paramResolveInfo.activityInfo != null) && ((paramResolveInfo.activityInfo.applicationInfo.flags & 0x1) != 0);
  }
  
  public boolean getBoolean(String paramString)
  {
    return getOrSetBoolean(paramString, false, false);
  }
  
  boolean getOrSetBoolean(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramString.compareTo("PREFER_STATIC_SHADOWS") == 0)
    {
      if (paramBoolean1)
      {
        this.mPreferStaticShadows = paramBoolean2;
        return paramBoolean2;
      }
      return this.mPreferStaticShadows;
    }
    throw new IllegalArgumentException("Invalid key");
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public boolean preferStaticShadows()
  {
    return this.mPreferStaticShadows;
  }
  
  public void setBoolean(String paramString, boolean paramBoolean)
  {
    getOrSetBoolean(paramString, true, paramBoolean);
  }
  
  static class Customizations
  {
    String mPackageName;
    Resources mResources;
    
    public Customizations(Resources paramResources, String paramString)
    {
      this.mResources = paramResources;
      this.mPackageName = paramString;
    }
    
    public boolean getBoolean(String paramString, boolean paramBoolean)
    {
      int i = this.mResources.getIdentifier(paramString, "bool", this.mPackageName);
      if (i > 0) {
        paramBoolean = this.mResources.getBoolean(i);
      }
      return paramBoolean;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/system/Settings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */