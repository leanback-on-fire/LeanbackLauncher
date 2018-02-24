package com.google.android.tvlauncher.model;

import android.database.Cursor;
import com.google.android.tvlauncher.util.LauncherSharedConstants;

public class HomeChannel {
    public static final String[] PROJECTION = new String[]{"_id", "display_name", "app_link_intent_uri", "package_name", LauncherSharedConstants.LEGACY_PACKAGE_NAME_COLUMN};
    private String mDisplayName;
    private long mId;
    private String mLaunchUri;
    private boolean mLegacy = false;
    private String mPackageName;

    public static HomeChannel fromCursor(Cursor cursor) {
        HomeChannel channel = new HomeChannel();
        int index = 0 + 1;
        channel.mId = cursor.getLong(0);
        int index2 = index + 1;
        channel.mDisplayName = cursor.getString(index);
        index = index2 + 1;
        channel.mLaunchUri = cursor.getString(index2);
        index2 = index + 1;
        channel.mPackageName = cursor.getString(index);
        if (LauncherSharedConstants.TVRECOMMENDATIONS_PACKAGE_NAME.equals(channel.mPackageName)) {
            index = index2 + 1;
            byte[] packageNameBlob = cursor.getBlob(index2);
            channel.mPackageName = new String(packageNameBlob, 0, packageNameBlob.length - 1);
            channel.mLegacy = true;
            index2 = index;
        }
        return channel;
    }

    public long getId() {
        return this.mId;
    }

    public String getDisplayName() {
        return this.mDisplayName;
    }

    public String getLaunchUri() {
        return this.mLaunchUri;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public boolean isLegacy() {
        return this.mLegacy;
    }

    public String toString() {
        return "HomeChannel{id=" + this.mId + ", displayName='" + this.mDisplayName + '\'' + '}';
    }
}
