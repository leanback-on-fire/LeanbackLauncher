package com.google.android.tvlauncher.model;

import android.database.Cursor;

public class HomeChannel
{
  public static final String[] PROJECTION = { "_id", "display_name", "app_link_intent_uri", "package_name", "internal_provider_data" };
  private String mDisplayName;
  private long mId;
  private String mLaunchUri;
  private boolean mLegacy = false;
  private String mPackageName;
  
  public static HomeChannel fromCursor(Cursor paramCursor)
  {
    HomeChannel localHomeChannel = new HomeChannel();
    int j = 0 + 1;
    localHomeChannel.mId = paramCursor.getLong(0);
    int i = j + 1;
    localHomeChannel.mDisplayName = paramCursor.getString(j);
    j = i + 1;
    localHomeChannel.mLaunchUri = paramCursor.getString(i);
    i = j + 1;
    localHomeChannel.mPackageName = paramCursor.getString(j);
    if ("com.google.android.tvrecommendations".equals(localHomeChannel.mPackageName))
    {
      paramCursor = paramCursor.getBlob(i);
      localHomeChannel.mPackageName = new String(paramCursor, 0, paramCursor.length - 1);
      localHomeChannel.mLegacy = true;
    }
    return localHomeChannel;
  }
  
  public String getDisplayName()
  {
    return this.mDisplayName;
  }
  
  public long getId()
  {
    return this.mId;
  }
  
  public String getLaunchUri()
  {
    return this.mLaunchUri;
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  public boolean isLegacy()
  {
    return this.mLegacy;
  }
  
  public String toString()
  {
    return "HomeChannel{id=" + this.mId + ", displayName='" + this.mDisplayName + '\'' + '}';
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/model/HomeChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */