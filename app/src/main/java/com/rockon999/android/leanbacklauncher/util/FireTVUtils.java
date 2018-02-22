package com.rockon999.android.leanbacklauncher.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * Created by rockon999 on 2/15/18.
 */

public class FireTVUtils {

    public static void startAppSettings(Context context, String packageName) {
        try {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", packageName, null));

            context.startActivity(intent);
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            String errorReason = errors.toString();
            Log.d("Leanback on Fire", "Failed to launch settings-activity: \n" + errorReason);
        }
    }

    public static boolean isAmazonStoreInstalled(Context context) {
        Intent localIntent = new Intent();
        localIntent.setPackage("com.amazon.venezia");

        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(localIntent, 0);

        return activities.size() > 0;
    }

    public static void openAppInAmazonStore(Context context, String packageName) {
        try {
            Intent localIntent = new Intent();
            localIntent.setPackage("com.amazon.venezia");
            localIntent.setComponent(ComponentName.unflattenFromString("com.amazon.venezia/com.amazon.venezia.details.AppDetailsActivity"));
            localIntent.setData(Uri.fromParts("application", packageName, null));
            localIntent.putExtra("asin", "");
            localIntent.putExtra("packageName", packageName);
            localIntent.putExtra("clickStreamReftag", AmazonStoreSpoofer.buildRefTag());
            localIntent.setFlags(32768);

            context.startActivity(localIntent);
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            String errorReason = errors.toString();
            Log.d("Leanback on Fire", "Failed to launch store-activity: \n" + errorReason);
        }
    }

    public static Intent getNotificationPreferencesIntent() {
        String settingsAct = "com.amazon.tv.notificationcenter/com.amazon.tv.notificationcenter.settings.NotificationSettingsActivity";

        Intent intent = new Intent();
        intent.setPackage("com.amazon.tv.notificationcenter");
        intent.setComponent(ComponentName.unflattenFromString(settingsAct));
        intent.setFlags(32768);
        // KNOWN KEY: com.amazon.device.settings..PACKAGE_NAME
        // intent.putExtra("com.amazon.device.settings..PACKAGE_NAME", packageName);

        return intent;
    }

    public static Intent getNotificationCenterIntent() {
        String settingsAct = "com.amazon.tv.notificationcenter/com.amazon.tv.notificationcenter.NotificationCenterActivity";

        Intent intent = new Intent();
        intent.setPackage("com.amazon.tv.notificationcenter");
        intent.setComponent(ComponentName.unflattenFromString(settingsAct));
        intent.setFlags(32768);

        return intent;
    }

    private static class AmazonStoreSpoofer {

        static final String SPOOFED_WIDGET = "CONTEXT_MENU";
        static final String SPOOFED_PAGE = "UNKNOWN";

        static String buildRefTag() {
            return getReferenceTagPrefix() + buildSpoofedReferenceTag(SPOOFED_WIDGET, SPOOFED_PAGE);
        }

        static String getReferenceTagPrefix() {
            return "apps_e1_"; // this is "bueller" (the default) // todo actually "match" device.
        }


        static String buildSpoofedReferenceTag(String widget, String page) {
            return widget + '_' + page;
        }
    }

}
