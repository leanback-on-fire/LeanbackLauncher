package com.google.android.tvlauncher.analytics;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.android.tvlauncher.notifications.NotificationsContract;

public final class LogUtils {
    public static String getPackage(Intent intent) {
        ComponentName componentName = intent.getComponent();
        if (componentName != null) {
            return componentName.getPackageName();
        }
        return intent.getPackage();
    }

    public static String programTypeToString(int programType) {
        switch (programType) {
            case 0:
                return "movie";
            case 1:
                return "tv-series";
            case 2:
                return "tv-season";
            case 3:
                return "tv-episode";
            case 4:
                return "clip";
            case 5:
                return NotificationCompat.CATEGORY_EVENT;
            case 6:
                return NotificationsContract.COLUMN_CHANNEL;
            case 7:
                return "track";
            case 8:
                return "album";
            case 9:
                return "artist";
            case 10:
                return "playlist";
            case 11:
                return "station";
            default:
                return Integer.toString(programType);
        }
    }

    private LogUtils() {
    }
}
