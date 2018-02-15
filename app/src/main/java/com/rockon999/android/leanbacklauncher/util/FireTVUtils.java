package com.rockon999.android.leanbacklauncher.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by rockon999 on 2/15/18.
 */

public class FireTVUtils {
    private static Object synchronize = new Object();

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
}
