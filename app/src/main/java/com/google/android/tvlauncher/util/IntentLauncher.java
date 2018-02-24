package com.google.android.tvlauncher.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.content.IntentCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.android.tvlauncher.R;
import java.net.URISyntaxException;
import java.util.List;

public class IntentLauncher {
    private static final boolean DEBUG = false;
    private static final String TAG = "IntentLauncher";

    public static void launchMediaIntentFromUri(Context context, String uri) {
        launchIntentFromUri(context, uri, true);
    }

    public static void launchIntentFromUri(Context context, String uri) {
        launchIntentFromUri(context, uri, false);
    }

    private static void launchIntentFromUri(Context context, String uri, boolean launchMedia) {
        if (!launchIntent(context, parseUri(uri, launchMedia))) {
            Toast.makeText(context, R.string.failed_launch, 0).show();
        }
    }

    private static Intent parseUri(String uri, boolean launchMedia) {
        if (uri == null) {
            Log.e(TAG, "No URI provided");
            return null;
        }
        try {
            Intent intent = Intent.parseUri(uri, 1);
            if (!launchMedia) {
                return intent;
            }
            intent.putExtra(IntentCompat.EXTRA_START_PLAYBACK, true);
            return intent;
        } catch (URISyntaxException e) {
            Log.e(TAG, "Bad URI syntax: " + uri);
            return null;
        }
    }

    private static boolean launchIntent(Context context, Intent intent) {
        if (intent == null) {
            return false;
        }
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
        if (activities == null || activities.size() <= 0) {
            intent.setPackage(LauncherSharedConstants.TVRECOMMENDATIONS_PACKAGE_NAME);
            List<ResolveInfo> receivers = pm.queryBroadcastReceivers(intent, 0);
            if (receivers == null || receivers.size() <= 0) {
                Log.e(TAG, "Activity not found for intent: " + intent);
                return false;
            }
            context.sendBroadcast(intent);
            return true;
        }
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Failed to launch " + intent, e);
            return false;
        }
    }
}
