package com.google.android.tvlauncher.notifications;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Parcel;

public class TvNotification
{
  public static final int COLUMN_INDEX_BIG_PICTURE = 11;
  public static final int COLUMN_INDEX_CHANNEL = 7;
  public static final int COLUMN_INDEX_CONTENT_BUTTON_LABEL = 12;
  public static final int COLUMN_INDEX_DISMISSIBLE = 4;
  public static final int COLUMN_INDEX_DISMISS_BUTTON_LABEL = 13;
  public static final int COLUMN_INDEX_HAS_CONTENT_INTENT = 10;
  public static final int COLUMN_INDEX_KEY = 0;
  public static final int COLUMN_INDEX_NOTIF_TEXT = 3;
  public static final int COLUMN_INDEX_NOTIF_TITLE = 2;
  public static final int COLUMN_INDEX_ONGOING = 5;
  public static final int COLUMN_INDEX_PACKAGE_NAME = 1;
  public static final int COLUMN_INDEX_PROGRESS = 8;
  public static final int COLUMN_INDEX_PROGRESS_MAX = 9;
  public static final int COLUMN_INDEX_SMALL_ICON = 6;
  public static final int COLUMN_INDEX_TAG = 14;
  public static final String[] PROJECTION = { "sbn_key", "package_name", "title", "text", "dismissible", "ongoing", "small_icon", "channel", "progress", "progress_max", "has_content_intent", "big_picture", "content_button_label", "dismiss_button_label", "tag" };
  private Bitmap mBigPicture;
  private int mChannel;
  private String mContentButtonLabel;
  private String mDismissButtonLabel;
  private boolean mDismissible;
  private boolean mHasContentIntent;
  private boolean mIsOngoing;
  private String mNotificationKey;
  private String mPackageName;
  private int mProgress;
  private int mProgressMax;
  private Icon mSmallIcon;
  private String mTag;
  private String mText;
  private String mTitle;
  
  public TvNotification(String paramString1, String paramString2, String paramString3, String paramString4, boolean paramBoolean1, boolean paramBoolean2, Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean3, Bitmap paramBitmap, String paramString5, String paramString6, String paramString7)
  {
    this.mNotificationKey = paramString1;
    this.mPackageName = paramString2;
    this.mTitle = paramString3;
    this.mText = paramString4;
    this.mDismissible = paramBoolean1;
    this.mIsOngoing = paramBoolean2;
    this.mSmallIcon = paramIcon;
    this.mChannel = paramInt1;
    this.mProgress = paramInt2;
    this.mProgressMax = paramInt3;
    this.mHasContentIntent = paramBoolean3;
    this.mBigPicture = paramBitmap;
    this.mContentButtonLabel = paramString5;
    this.mDismissButtonLabel = paramString6;
    this.mTag = paramString7;
  }
  
  public static TvNotification fromCursor(Cursor paramCursor)
  {
    int j = 0 + 1;
    String str1 = paramCursor.getString(0);
    int i = j + 1;
    String str2 = paramCursor.getString(j);
    j = i + 1;
    String str3 = paramCursor.getString(i);
    i = j + 1;
    String str4 = paramCursor.getString(j);
    j = i + 1;
    boolean bool1;
    boolean bool2;
    label86:
    int k;
    Icon localIcon;
    int m;
    int n;
    if (paramCursor.getInt(i) != 0)
    {
      bool1 = true;
      i = j + 1;
      if (paramCursor.getInt(j) == 0) {
        break label245;
      }
      bool2 = true;
      k = i + 1;
      localIcon = getIconFromBytes(paramCursor.getBlob(i));
      j = k + 1;
      i = paramCursor.getInt(k);
      k = j + 1;
      j = paramCursor.getInt(j);
      m = k + 1;
      k = paramCursor.getInt(k);
      n = m + 1;
      if (paramCursor.getInt(m) == 0) {
        break label251;
      }
    }
    label245:
    label251:
    for (boolean bool3 = true;; bool3 = false)
    {
      m = n + 1;
      Bitmap localBitmap = getBitmapFromBytes(paramCursor.getBlob(n));
      n = m + 1;
      return new TvNotification(str1, str2, str3, str4, bool1, bool2, localIcon, i, j, k, bool3, localBitmap, paramCursor.getString(m), paramCursor.getString(n), paramCursor.getString(n + 1));
      bool1 = false;
      break;
      bool2 = false;
      break label86;
    }
  }
  
  private static Bitmap getBitmapFromBytes(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte != null) {
      return BitmapFactory.decodeByteArray(paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    return null;
  }
  
  private static Icon getIconFromBytes(byte[] paramArrayOfByte)
  {
    Parcel localParcel = Parcel.obtain();
    Icon localIcon = null;
    if (paramArrayOfByte != null)
    {
      localParcel.unmarshall(paramArrayOfByte, 0, paramArrayOfByte.length);
      localParcel.setDataPosition(0);
      localIcon = (Icon)localParcel.readParcelable(Icon.class.getClassLoader());
    }
    localParcel.recycle();
    return localIcon;
  }
  
  public Bitmap getBigPicture()
  {
    return this.mBigPicture;
  }
  
  public int getChannel()
  {
    return this.mChannel;
  }
  
  public String getContentButtonLabel()
  {
    return this.mContentButtonLabel;
  }
  
  public String getDismissButtonLabel()
  {
    return this.mDismissButtonLabel;
  }
  
  public String getNotificationKey()
  {
    return this.mNotificationKey;
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  public int getProgress()
  {
    return this.mProgress;
  }
  
  public int getProgressMax()
  {
    return this.mProgressMax;
  }
  
  public Icon getSmallIcon()
  {
    return this.mSmallIcon;
  }
  
  public String getTag()
  {
    return this.mTag;
  }
  
  public String getText()
  {
    return this.mText;
  }
  
  public String getTitle()
  {
    return this.mTitle;
  }
  
  public boolean hasContentIntent()
  {
    return this.mHasContentIntent;
  }
  
  public boolean isDismissible()
  {
    return this.mDismissible;
  }
  
  public boolean isOngoing()
  {
    return this.mIsOngoing;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/notifications/TvNotification.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */