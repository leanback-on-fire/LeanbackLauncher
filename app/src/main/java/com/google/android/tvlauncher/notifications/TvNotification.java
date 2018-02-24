package com.google.android.tvlauncher.notifications;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Parcel;

public class TvNotification {
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
    public static final String[] PROJECTION = new String[]{"sbn_key", "package_name", NotificationsContract.COLUMN_NOTIF_TITLE, "text", NotificationsContract.COLUMN_DISMISSIBLE, NotificationsContract.COLUMN_ONGOING, NotificationsContract.COLUMN_SMALL_ICON, NotificationsContract.COLUMN_CHANNEL, "progress", NotificationsContract.COLUMN_PROGRESS_MAX, NotificationsContract.COLUMN_HAS_CONTENT_INTENT, NotificationsContract.COLUMN_BIG_PICTURE, NotificationsContract.COLUMN_CONTENT_BUTTON_LABEL, NotificationsContract.COLUMN_DISMISS_BUTTON_LABEL, NotificationsContract.COLUMN_TAG};
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

    public TvNotification(String key, String packageName, String title, String text, boolean dismissible, boolean ongoing, Icon smallIcon, int channel, int progress, int progressMax, boolean hasContentIntent, Bitmap bigPicture, String contentButtonLabel, String dismissButtonLabel, String tag) {
        this.mNotificationKey = key;
        this.mPackageName = packageName;
        this.mTitle = title;
        this.mText = text;
        this.mDismissible = dismissible;
        this.mIsOngoing = ongoing;
        this.mSmallIcon = smallIcon;
        this.mChannel = channel;
        this.mProgress = progress;
        this.mProgressMax = progressMax;
        this.mHasContentIntent = hasContentIntent;
        this.mBigPicture = bigPicture;
        this.mContentButtonLabel = contentButtonLabel;
        this.mDismissButtonLabel = dismissButtonLabel;
        this.mTag = tag;
    }

    public String getNotificationKey() {
        return this.mNotificationKey;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getText() {
        return this.mText;
    }

    public boolean isDismissible() {
        return this.mDismissible;
    }

    public boolean isOngoing() {
        return this.mIsOngoing;
    }

    public Icon getSmallIcon() {
        return this.mSmallIcon;
    }

    public int getChannel() {
        return this.mChannel;
    }

    public int getProgress() {
        return this.mProgress;
    }

    public int getProgressMax() {
        return this.mProgressMax;
    }

    public boolean hasContentIntent() {
        return this.mHasContentIntent;
    }

    public Bitmap getBigPicture() {
        return this.mBigPicture;
    }

    public String getContentButtonLabel() {
        return this.mContentButtonLabel;
    }

    public String getDismissButtonLabel() {
        return this.mDismissButtonLabel;
    }

    public String getTag() {
        return this.mTag;
    }

    public static TvNotification fromCursor(Cursor cursor) {
        int i = 0 + 1;
        String key = cursor.getString(0);
        int i2 = i + 1;
        String packageName = cursor.getString(i);
        i = i2 + 1;
        String title = cursor.getString(i2);
        i2 = i + 1;
        String text = cursor.getString(i);
        i = i2 + 1;
        boolean dismissible = cursor.getInt(i2) != 0;
        i2 = i + 1;
        boolean ongoing = cursor.getInt(i) != 0;
        i = i2 + 1;
        Icon smallIcon = getIconFromBytes(cursor.getBlob(i2));
        i2 = i + 1;
        int channel = cursor.getInt(i);
        i = i2 + 1;
        int progress = cursor.getInt(i2);
        i2 = i + 1;
        int progressMax = cursor.getInt(i);
        i = i2 + 1;
        boolean hasContentIntent = cursor.getInt(i2) != 0;
        i2 = i + 1;
        i = i2 + 1;
        return new TvNotification(key, packageName, title, text, dismissible, ongoing, smallIcon, channel, progress, progressMax, hasContentIntent, getBitmapFromBytes(cursor.getBlob(i)), cursor.getString(i2), cursor.getString(i), cursor.getString(i + 1));
    }

    private static Bitmap getBitmapFromBytes(byte[] blob) {
        if (blob != null) {
            return BitmapFactory.decodeByteArray(blob, 0, blob.length);
        }
        return null;
    }

    private static Icon getIconFromBytes(byte[] blob) {
        Parcel in = Parcel.obtain();
        Icon icon = null;
        if (blob != null) {
            in.unmarshall(blob, 0, blob.length);
            in.setDataPosition(0);
            // icon = (Icon) in.readParcelable(Icon.class.getClassLoader());
        }
        in.recycle();
        return icon;
    }
}
