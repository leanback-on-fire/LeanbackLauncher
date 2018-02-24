package com.google.android.tvlauncher.util;

import android.content.ContentResolver;
import com.google.android.gsf.Gservices;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GservicesUtils
{
  private static final String TV_LAUNCHER_BLACKLIST_REMOVAL_KEY = "tvlauncher:blacklisted_from_removal";
  private static final String TV_LAUNCHER_BLACKLIST_WATCHLIST_KEY = "tvlauncher:blacklisted_from_watchlist";
  
  private static Set<String> readCommaSeparatedSet(ContentResolver paramContentResolver, String paramString)
  {
    paramContentResolver = Gservices.getString(paramContentResolver, paramString, "");
    if (paramContentResolver.isEmpty()) {
      return Collections.emptySet();
    }
    return new HashSet(Arrays.asList(paramContentResolver.replaceAll("\\s", "").split(",")));
  }
  
  public static Set<String> retrievePackagesBlacklistedForProgramRemoval(ContentResolver paramContentResolver)
  {
    return readCommaSeparatedSet(paramContentResolver, "tvlauncher:blacklisted_from_removal");
  }
  
  public static Set<String> retrievePackagesBlacklistedForWatchNext(ContentResolver paramContentResolver)
  {
    return readCommaSeparatedSet(paramContentResolver, "tvlauncher:blacklisted_from_watchlist");
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/util/GservicesUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */