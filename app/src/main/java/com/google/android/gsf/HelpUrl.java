package com.google.android.gsf;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.net.Uri.Builder;
import android.text.TextUtils;
import android.util.Log;
import java.util.Locale;

public class HelpUrl
{
  private static final String DEFAULT_HELP_URL = "https://support.google.com/mobile/?hl=%locale%";
  private static final String SMART_HELP_LINK_APP_VERSION = "version";
  private static final String SMART_HELP_LINK_PARAMETER_NAME = "p";
  private static final String TAG = "HelpUrl";
  
  public static Uri getHelpUrl(Context paramContext, String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("getHelpUrl(): fromWhere must be non-empty");
    }
    Uri.Builder localBuilder = Uri.parse(replaceLocale(Gservices.getString(paramContext.getContentResolver(), "context_sensitive_help_url", "https://support.google.com/mobile/?hl=%locale%"))).buildUpon();
    localBuilder.appendQueryParameter("p", paramString);
    try
    {
      localBuilder.appendQueryParameter("version", String.valueOf(paramContext.getPackageManager().getPackageInfo(paramContext.getApplicationInfo().packageName, 0).versionCode));
      return localBuilder.build();
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      for (;;)
      {
        Log.e("HelpUrl", "Error finding package " + paramContext.getApplicationInfo().packageName);
      }
    }
  }
  
  private static String replaceLocale(String paramString)
  {
    Object localObject = paramString;
    if (paramString.contains("%locale%"))
    {
      localObject = Locale.getDefault();
      localObject = paramString.replace("%locale%", ((Locale)localObject).getLanguage() + "-" + ((Locale)localObject).getCountry().toLowerCase());
    }
    return (String)localObject;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gsf/HelpUrl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */