package com.google.android.tvlauncher.model;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

@TargetApi(26)
public class Channel
  implements Comparable<Channel>
{
  public static final String[] PROJECTION = { "_id", "display_name", "browsable", "package_name", "internal_provider_data" };
  private boolean mBrowsable;
  private String mDisplayName;
  private long mId;
  private String mPackageName;
  
  public static Channel fromCursor(Cursor paramCursor)
  {
    boolean bool = true;
    Channel localChannel = new Channel();
    int j = 0 + 1;
    localChannel.mId = paramCursor.getLong(0);
    int i = j + 1;
    localChannel.mDisplayName = paramCursor.getString(j);
    j = i + 1;
    if (paramCursor.getInt(i) == 1) {}
    for (;;)
    {
      localChannel.mBrowsable = bool;
      i = j + 1;
      localChannel.mPackageName = paramCursor.getString(j);
      if ("com.google.android.tvrecommendations".equals(localChannel.mPackageName))
      {
        paramCursor = paramCursor.getBlob(i);
        localChannel.mPackageName = new String(paramCursor, 0, paramCursor.length - 1);
      }
      return localChannel;
      bool = false;
    }
  }
  
  public int compareTo(@NonNull Channel paramChannel)
  {
    if ((this.mDisplayName == null) && (paramChannel.getDisplayName() == null)) {
      return 0;
    }
    if (this.mDisplayName == null) {
      return 1;
    }
    if (paramChannel.getDisplayName() == null) {
      return -1;
    }
    return this.mDisplayName.compareToIgnoreCase(paramChannel.getDisplayName());
  }
  
  public String getDisplayName()
  {
    return this.mDisplayName;
  }
  
  public long getId()
  {
    return this.mId;
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  public boolean isBrowsable()
  {
    return this.mBrowsable;
  }
  
  @VisibleForTesting(otherwise=2)
  void setDisplayName(String paramString)
  {
    this.mDisplayName = paramString;
  }
  
  @VisibleForTesting(otherwise=2)
  public void setId(long paramLong)
  {
    this.mId = paramLong;
  }
  
  @VisibleForTesting(otherwise=2)
  public void setPackageName(String paramString)
  {
    this.mPackageName = paramString;
  }
  
  public String toString()
  {
    return "Channel{mId=" + this.mId + ", mDisplayName='" + this.mDisplayName + '\'' + ", mBrowsable=" + this.mBrowsable + ", mPackageName='" + this.mPackageName + '\'' + '}';
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/model/Channel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */