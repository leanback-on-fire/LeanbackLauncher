package com.google.android.gsf;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class UseLocationForServices
{
  public static final String ACTION_SET_USE_LOCATION_FOR_SERVICES = "com.google.android.gsf.action.SET_USE_LOCATION_FOR_SERVICES";
  public static final String EXTRA_DISABLE_USE_LOCATION_FOR_SERVICES = "disable";
  private static final String[] GOOGLE_GEOLOCATION_ORIGINS = { "http://www.google.com", "http://www.google.co.uk" };
  private static final String TAG = "UseLocationForServices";
  public static final int USE_LOCATION_FOR_SERVICES_NOT_SET = 2;
  public static final int USE_LOCATION_FOR_SERVICES_OFF = 0;
  public static final int USE_LOCATION_FOR_SERVICES_ON = 1;
  
  private static String addGoogleOrigins(String paramString)
  {
    paramString = parseAllowGeolocationOrigins(paramString);
    String[] arrayOfString = GOOGLE_GEOLOCATION_ORIGINS;
    int j = arrayOfString.length;
    int i = 0;
    while (i < j)
    {
      paramString.add(arrayOfString[i]);
      i += 1;
    }
    return formatAllowGeolocationOrigins(paramString);
  }
  
  @Deprecated
  public static boolean forceSetUseLocationForServices(Context paramContext, boolean paramBoolean)
  {
    setGoogleBrowserGeolocation(paramContext, paramBoolean);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      paramBoolean = GoogleSettingsContract.Partner.putInt(paramContext.getContentResolver(), "use_location_for_services", i);
      paramContext.sendBroadcast(new Intent("com.google.android.gsf.settings.GoogleLocationSettings.UPDATE_LOCATION_SETTINGS"));
      return paramBoolean;
    }
  }
  
  private static String formatAllowGeolocationOrigins(Collection<String> paramCollection)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext())
    {
      String str = (String)paramCollection.next();
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(' ');
      }
      localStringBuilder.append(str);
    }
    return localStringBuilder.toString();
  }
  
  public static int getUseLocationForServices(Context paramContext)
  {
    return GoogleSettingsContract.Partner.getInt(paramContext.getContentResolver(), "use_location_for_services", 2);
  }
  
  private static HashSet<String> parseAllowGeolocationOrigins(String paramString)
  {
    HashSet localHashSet = new HashSet();
    if (!TextUtils.isEmpty(paramString))
    {
      paramString = paramString.split("\\s+");
      int j = paramString.length;
      int i = 0;
      while (i < j)
      {
        CharSequence localCharSequence = paramString[i];
        if (!TextUtils.isEmpty(localCharSequence)) {
          localHashSet.add(localCharSequence);
        }
        i += 1;
      }
    }
    return localHashSet;
  }
  
  public static void registerUseLocationForServicesObserver(Context paramContext, ContentObserver paramContentObserver)
  {
    Uri localUri = GoogleSettingsContract.Partner.getUriFor("use_location_for_services");
    paramContext.getContentResolver().registerContentObserver(localUri, false, paramContentObserver);
  }
  
  private static String removeGoogleOrigins(String paramString)
  {
    paramString = parseAllowGeolocationOrigins(paramString);
    String[] arrayOfString = GOOGLE_GEOLOCATION_ORIGINS;
    int j = arrayOfString.length;
    int i = 0;
    while (i < j)
    {
      paramString.remove(arrayOfString[i]);
      i += 1;
    }
    return formatAllowGeolocationOrigins(paramString);
  }
  
  private static void setGoogleBrowserGeolocation(Context paramContext, boolean paramBoolean)
  {
    try
    {
      ContentResolver localContentResolver = paramContext.getContentResolver();
      paramContext = Settings.Secure.getString(localContentResolver, "allowed_geolocation_origins");
      if (paramBoolean) {}
      for (paramContext = addGoogleOrigins(paramContext);; paramContext = removeGoogleOrigins(paramContext))
      {
        Settings.Secure.putString(localContentResolver, "allowed_geolocation_origins", paramContext);
        return;
      }
      return;
    }
    catch (RuntimeException paramContext)
    {
      Log.e("UseLocationForServices", "Failed to set browser geolocation permissions: " + paramContext);
    }
  }
  
  @Deprecated
  public static boolean setUseLocationForServices(Context paramContext, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if (getUseLocationForServices(paramContext) != 1)
      {
        Intent localIntent = new Intent("com.google.android.gsf.GOOGLE_APPS_LOCATION_SETTINGS");
        localIntent.setFlags(268435456);
        paramContext.startActivity(localIntent);
      }
      return false;
    }
    return forceSetUseLocationForServices(paramContext, paramBoolean);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gsf/UseLocationForServices.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */