package com.google.android.gsf;

import android.webkit.CookieManager;

public class SAMLUtils
{
  private static final String DEFAULT_HOSTED_BASE_PATH = "https://www.google.com";
  private static final String HOSTED_PREFIX = "/a/";
  private static final String TEST_GAIA_HOSTED_BASE_PATH = "http://dasher-qa.corp.google.com";
  
  private static String extractCookie(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      return "";
    }
    paramString1 = paramString1.split("; ");
    int j = paramString1.length;
    int i = 0;
    while (i < j)
    {
      String[] arrayOfString = paramString1[i].split("=");
      if ((arrayOfString.length == 2) && (arrayOfString[0].equalsIgnoreCase(paramString2))) {
        return arrayOfString[1];
      }
      i += 1;
    }
    return "";
  }
  
  public static String extractHID(CookieManager paramCookieManager, String paramString)
  {
    String str2 = extractCookie(paramCookieManager.getCookie(makeHIDCookieExtractionPath(false, paramString)), "HID");
    String str1 = str2;
    if (str2.length() == 0) {
      str1 = extractCookie(paramCookieManager.getCookie(makeLSIDCookieExtractionPath(false, paramString)), "LSID");
    }
    return str1;
  }
  
  private static final String getHostedBaseUrl(boolean paramBoolean)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (paramBoolean) {}
    for (String str = "http://dasher-qa.corp.google.com";; str = "https://www.google.com") {
      return str + "/a/";
    }
  }
  
  private static String makeHIDCookieExtractionPath(boolean paramBoolean, String paramString)
  {
    return makeHostedGaiaBasePath(paramBoolean, paramString);
  }
  
  private static String makeHostedGaiaBasePath(boolean paramBoolean, String paramString)
  {
    return getHostedBaseUrl(paramBoolean) + paramString + "/";
  }
  
  private static String makeLSIDCookieExtractionPath(boolean paramBoolean, String paramString)
  {
    if (paramBoolean) {}
    for (paramString = "http://dasher-qa.corp.google.com";; paramString = "https://www.google.com") {
      return paramString + "/accounts/";
    }
  }
  
  public static String makeWebLoginStartUrl(boolean paramBoolean, String paramString)
  {
    return makeHostedGaiaBasePath(paramBoolean, paramString) + "ServiceLogin";
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gsf/SAMLUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */