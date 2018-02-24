package com.google.android.tvlauncher.notifications;

import android.content.Context;
import android.content.Intent;
import com.google.android.tvlauncher.util.LauncherSharedConstants;

public class NotificationsUtils {
    static void dismissNotification(Context context, String key) {
        Intent dismiss = new Intent("android.intent.action.DELETE");
        dismiss.setPackage(LauncherSharedConstants.TVRECOMMENDATIONS_PACKAGE_NAME);
        dismiss.putExtra("sbn_key", key);
        context.sendBroadcast(dismiss);
    }

    static void openNotification(Context context, String key) {
        Intent open = new Intent("android.intent.action.VIEW");
        open.setPackage(LauncherSharedConstants.TVRECOMMENDATIONS_PACKAGE_NAME);
        open.putExtra("sbn_key", key);
        context.sendBroadcast(open);
    }

    static void hideNotification(Context context, String key) {
        Intent mark = new Intent(NotificationsContract.ACTION_NOTIFICATION_HIDE);
        mark.setPackage(LauncherSharedConstants.TVRECOMMENDATIONS_PACKAGE_NAME);
        mark.putExtra("sbn_key", key);
        context.sendBroadcast(mark);
    }

    public static void showUnshownNotifications(Context context) {
        Intent show = new Intent(NotificationsContract.ACTION_SHOW_UNSHOWN_NOTIFICATIONS);
        show.setPackage(LauncherSharedConstants.TVRECOMMENDATIONS_PACKAGE_NAME);
        context.sendBroadcast(show);
    }
}
