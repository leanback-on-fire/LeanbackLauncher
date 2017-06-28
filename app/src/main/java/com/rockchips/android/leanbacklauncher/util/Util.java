package com.rockchips.android.leanbacklauncher.util;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.preference.R.styleable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

public class Util {
    private Util() {
    }

    public static Intent getSearchIntent() {
        return new Intent("android.intent.action.ASSIST").addFlags(270532608);
    }

    public static boolean isContentUri(String uriString) {
        if (TextUtils.isEmpty(uriString)) {
            return false;
        }
        return isContentUri(Uri.parse(uriString));
    }

    public static boolean isContentUri(Uri uri) {
        if ("content".equals(uri.getScheme())) {
            return true;
        }
        return "file".equals(uri.getScheme());
    }

    public static boolean isPackagePresent(PackageManager pkgMan, String packageName) {
        try {
            if (pkgMan.getApplicationInfo(packageName, 0) != null) {
                return true;
            }
            return false;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static boolean initialRankingApplied(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("launcher_oob_ranking_marker", false);
    }

    public static void setInitialRankingAppliedFlag(Context ctx, boolean applied) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putBoolean("launcher_oob_ranking_marker", applied).apply();
    }

    private static boolean startActivitySafely(Context context, Intent intent) {
        try {
            context.startActivity(intent);
            return true;
        } catch (Throwable t) {
            Log.e("LeanbackLauncher", "Could not launch intent", t);
            Toast.makeText(context, 2131296420, 0).show();
            return false;
        }
    }

    public static void startActivity(Context context, PendingIntent intent) throws SendIntentException {
        context.startIntentSender(intent.getIntentSender(), null, 268435456, 268435456, 0);
    }

    public static long getInstallTimeForPackage(Context context, String pkgName) {
        PackageManager pkgMan = context.getPackageManager();
        if (pkgMan != null) {
            try {
                PackageInfo info = pkgMan.getPackageInfo(pkgName, 0);
                if (info != null) {
                    return info.firstInstallTime;
                }
            } catch (NameNotFoundException e) {
            }
        }
        return -1;
    }

    public static int getWidgetId(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getInt("widget_id", 0);
    }

    public static ComponentName getWidgetComponentName(Context ctx) {
        String name = PreferenceManager.getDefaultSharedPreferences(ctx).getString("widget_component_name", null);
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        return ComponentName.unflattenFromString(name);
    }

    public static void setWidget(Context ctx, int id, ComponentName name) {
        if (id == 0 || name == null) {
            clearWidget(ctx);
        } else {
            PreferenceManager.getDefaultSharedPreferences(ctx).edit().putInt("widget_id", id).putString("widget_component_name", name.flattenToString()).apply();
        }
    }

    public static void clearWidget(Context ctx) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().remove("widget_id").remove("widget_component_name").apply();
    }

    public static Bitmap getSizeCappedBitmap(Bitmap image, int maxWidth, int maxHeight) {
        if (image == null) {
            return null;
        }
        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();
        if ((imgWidth > maxWidth || imgHeight > maxHeight) && imgWidth > 0 && imgHeight > 0) {
            float scale = Math.min(1.0f, ((float) maxHeight) / ((float) imgHeight));
            if (((double) scale) < 1.0d || imgWidth > maxWidth) {
                float deltaW = ((float) Math.max(((int) (((float) imgWidth) * scale)) - maxWidth, 0)) / scale;
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);
                Bitmap newImage = Bitmap.createBitmap(image, (int) (deltaW / 2.0f), 0, (int) (((float) imgWidth) - deltaW), imgHeight, matrix, true);
                if (newImage != null) {
                    return newImage;
                }
            }
        }
        return image;
    }

    public static boolean startSearchActivitySafely(Context context, Intent intent, int deviceId, boolean isKeyboardSearch) {
        intent.putExtra("android.intent.extra.ASSIST_INPUT_DEVICE_ID", deviceId);
        intent.putExtra("search_type", isKeyboardSearch ? 2 : 1);
        return startActivitySafely(context, intent);
    }

    public static boolean startSearchActivitySafely(Context context, Intent intent, boolean isKeyboardSearch) {
        intent.putExtra("search_type", isKeyboardSearch ? 2 : 1);
        return startActivitySafely(context, intent);
    }

    public static boolean isSystemApp(Context context, String packageName) {
        try {
            if ((context.getPackageManager().getApplicationInfo(packageName, 0).flags & 1) != 0) {
                return true;
            }
            return false;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static void playErrorSound(Context context) {
        ((AudioManager) context.getSystemService("audio")).playSoundEffect(9);
    }

    public static final boolean isConfirmKey(int keyCode) {
        switch (keyCode) {
            case android.support.v7.preference.R.styleable.Preference_defaultValue /*23*/:
            case 62:
            case 66:
            case 96:
            case 160:
                return true;
            default:
                return false;
        }
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public static boolean isInTouchExploration(Context context) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService("accessibility");
        return am.isEnabled() ? am.isTouchExplorationEnabled() : false;
    }
}
