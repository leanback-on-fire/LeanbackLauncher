package com.google.android.tvlauncher.home.contentrating;

import android.media.tv.TvContentRating;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class ContentRatingsUtil
{
  @Nullable
  public static TvContentRating[] stringToContentRatings(@Nullable String paramString)
  {
    if (TextUtils.isEmpty(paramString))
    {
      paramString = null;
      return paramString;
    }
    String[] arrayOfString = paramString.split("\\s*,\\s*");
    TvContentRating[] arrayOfTvContentRating = new TvContentRating[arrayOfString.length];
    int i = 0;
    for (;;)
    {
      paramString = arrayOfTvContentRating;
      if (i >= arrayOfTvContentRating.length) {
        break;
      }
      arrayOfTvContentRating[i] = TvContentRating.unflattenFromString(arrayOfString[i]);
      i += 1;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/contentrating/ContentRatingsUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */