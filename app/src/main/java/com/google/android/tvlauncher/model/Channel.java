package com.google.android.tvlauncher.model;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.android.tvlauncher.util.LauncherSharedConstants;

@TargetApi(26)
public class Channel implements Comparable<Channel> {
    public static final String[] PROJECTION = new String[]{"_id", "display_name", "browsable", "package_name", LauncherSharedConstants.LEGACY_PACKAGE_NAME_COLUMN};
    private boolean mBrowsable;
    private String mDisplayName;
    private long mId;
    private String mPackageName;

    public static Channel fromCursor(Cursor cursor) {
        boolean z = true;
        Channel channel = new Channel();
        int index = 1;
        channel.mId = cursor.getLong(0);
        int index2 = index + 1;
        channel.mDisplayName = cursor.getString(index);
        index = index2 + 1;
        if (cursor.getInt(index2) != 1) {
            z = false;
        }
        channel.mBrowsable = z;
        index2 = index + 1;
        channel.mPackageName = cursor.getString(index);
        if (LauncherSharedConstants.TVRECOMMENDATIONS_PACKAGE_NAME.equals(channel.mPackageName)) {
            index = index2 + 1;
            byte[] packageNameBlob = cursor.getBlob(index2);
            channel.mPackageName = new String(packageNameBlob, 0, packageNameBlob.length - 1);
            index2 = index;
        }
        return channel;
    }

    public long getId() {
        return this.mId;
    }

    @VisibleForTesting(otherwise = 2)
    public void setId(long id) {
        this.mId = id;
    }

    public String getDisplayName() {
        return this.mDisplayName;
    }

    @VisibleForTesting(otherwise = 2)
    void setDisplayName(String displayName) {
        this.mDisplayName = displayName;
    }

    public boolean isBrowsable() {
        return this.mBrowsable;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    @VisibleForTesting(otherwise = 2)
    public void setPackageName(String packageName) {
        this.mPackageName = packageName;
    }

    public String toString() {
        return "Channel{mId=" + this.mId + ", mDisplayName='" + this.mDisplayName + '\'' + ", mBrowsable=" + this.mBrowsable + ", mPackageName='" + this.mPackageName + '\'' + '}';
    }

    public int compareTo(@NonNull Channel o) {
        if (this.mDisplayName == null && o.getDisplayName() == null) {
            return 0;
        }
        if (this.mDisplayName == null) {
            return 1;
        }
        if (o.getDisplayName() == null) {
            return -1;
        }
        return this.mDisplayName.compareToIgnoreCase(o.getDisplayName());
    }
}
