package com.google.android.leanbacklauncher.apps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.leanbacklauncher.R;
import java.util.HashMap;

public class MarketUpdateReceiver extends BroadcastReceiver {
    private final HashMap<String, LaunchPoint> mInstallLaunchPoints = new HashMap();
    private final InstallingLaunchPointListener mListener;

    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.launcher.action.ACTION_PACKAGE_ENQUEUED");
        filter.addAction("com.android.launcher.action.ACTION_PACKAGE_DOWNLOADING");
        filter.addAction("com.android.launcher.action.ACTION_PACKAGE_INSTALLING");
        filter.addAction("com.android.launcher.action.ACTION_PACKAGE_DEQUEUED");
        filter.addDataScheme("package");
        return filter;
    }

    public static String getBroadcastPermission() {
        return "android.permission.INSTALL_PACKAGES";
    }

    public MarketUpdateReceiver(InstallingLaunchPointListener listener) {
        this.mListener = listener;
    }

    private boolean isNonLeanbackApp(Context context, String pkgName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(pkgName, 1);
            if (pm.getLeanbackLaunchIntentForPackage(pkgName) == null) {
                return true;
            }
            return false;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public void onReceive(Context context, Intent intent) {
        Uri uri = intent.getData();
        if (uri == null) {
            Log.d("MarketUpdateReceiver", "Null URI");
            return;
        }
        String pkgName = uri.getSchemeSpecificPart();
        if (TextUtils.isEmpty(pkgName)) {
            Log.d("MarketUpdateReceiver", "Missing package name");
        } else if (isNonLeanbackApp(context, pkgName)) {
            Log.d("MarketUpdateReceiver", pkgName + " is non-leanback App");
        } else {
            String action = intent.getAction();
            LaunchPoint installLaunchPoint = (LaunchPoint) this.mInstallLaunchPoints.get(pkgName);
            if (installLaunchPoint != null || intent.getBooleanExtra("user_initiated", false)) {
                boolean wasAdded = false;
                if (installLaunchPoint == null) {
                    if ("com.android.launcher.action.ACTION_PACKAGE_DEQUEUED".equals(action)) {
                        Log.d("MarketUpdateReceiver", "Removing app " + pkgName + " we don't actually know about, ignore");
                        return;
                    }
                    wasAdded = true;
                    installLaunchPoint = createInstallLaunchPoint(context, pkgName, intent);
                    this.mInstallLaunchPoints.put(pkgName, installLaunchPoint);
                }
                boolean success = false;
                if ("com.android.launcher.action.ACTION_PACKAGE_ENQUEUED".equals(action)) {
                    installLaunchPoint.setInstallProgressPercent(-1);
                    String reason = "install";
                    if (intent.hasExtra("reason")) {
                        reason = intent.getStringExtra("reason");
                    }
                    if ("install".equals(reason)) {
                        installLaunchPoint.setInstallStateStringResourceId(R.string.install_pending);
                    } else if ("update".equals(reason)) {
                        installLaunchPoint.setInstallStateStringResourceId(R.string.update_pending);
                    } else if ("restore".equals(reason)) {
                        installLaunchPoint.setInstallStateStringResourceId(R.string.restore_pending);
                    } else {
                        installLaunchPoint.setInstallStateStringResourceId(R.string.unknown_state);
                    }
                    Log.d("MarketUpdateReceiver", "market has promised to " + reason + ": " + pkgName + " user initiated: " + intent.getBooleanExtra("user_initiated", false));
                } else if ("com.android.launcher.action.ACTION_PACKAGE_DOWNLOADING".equals(action)) {
                    int progress = intent.getIntExtra("progress", 0);
                    installLaunchPoint.setInstallProgressPercent(progress);
                    installLaunchPoint.setInstallStateStringResourceId(R.string.downloading);
                    Log.d("MarketUpdateReceiver", "market is downloading (" + progress + "%): " + pkgName);
                } else if ("com.android.launcher.action.ACTION_PACKAGE_INSTALLING".equals(action)) {
                    installLaunchPoint.setInstallProgressPercent(-1);
                    installLaunchPoint.setInstallStateStringResourceId(R.string.installing);
                    Log.d("MarketUpdateReceiver", "market is installing: " + pkgName);
                } else if ("com.android.launcher.action.ACTION_PACKAGE_DEQUEUED".equals(action)) {
                    this.mInstallLaunchPoints.remove(pkgName);
                    installLaunchPoint.setInstallStateStringResourceId(0);
                    installLaunchPoint.setInstallProgressPercent(-1);
                    success = intent.getBooleanExtra("com.android.launcher.action.INSTALL_COMPLETED", false);
                    if (success) {
                        Log.d("MarketUpdateReceiver", "market has installed: " + pkgName);
                    } else {
                        Log.d("MarketUpdateReceiver", "market has decided not to install: " + pkgName);
                    }
                } else {
                    installLaunchPoint.setInstallProgressPercent(-1);
                    installLaunchPoint.setInstallStateStringResourceId(R.string.unknown_state);
                    Log.d("MarketUpdateReceiver", "unknown message " + action);
                }
                if ("com.android.launcher.action.ACTION_PACKAGE_DEQUEUED".equals(action)) {
                    this.mListener.onInstallingLaunchPointRemoved(installLaunchPoint, success);
                    return;
                } else if (wasAdded) {
                    this.mListener.onInstallingLaunchPointAdded(installLaunchPoint);
                    return;
                } else {
                    this.mListener.onInstallingLaunchPointChanged(installLaunchPoint);
                    return;
                }
            }
            if (intent.hasExtra("user_initiated")) {
                Log.d("MarketUpdateReceiver", "EXTRA_USER_INITIATED was specified and false");
            } else {
                Log.d("MarketUpdateReceiver", "EXTRA_USER_INITIATED was not specified");
            }
            Log.d("MarketUpdateReceiver", "Extras = " + intent.getExtras());
        }
    }

    private LaunchPoint createInstallLaunchPoint(Context context, String pkgName, Intent intent) {
        return new LaunchPoint(context, intent.getStringExtra("app_name"), intent.getStringExtra("app_icon"), pkgName, (Intent) intent.getParcelableExtra("app_detailIntent"), intent.getBooleanExtra("app_is_game", false), this.mListener);
    }
}
