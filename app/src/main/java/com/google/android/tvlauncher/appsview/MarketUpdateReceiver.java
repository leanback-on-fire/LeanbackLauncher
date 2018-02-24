package com.google.android.tvlauncher.appsview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.tvlauncher.R;

public class MarketUpdateReceiver
        extends BroadcastReceiver {
    private static final String ACTION_PACKAGE_DEQUEUED = "com.android.launcher.action.ACTION_PACKAGE_DEQUEUED";
    private static final String ACTION_PACKAGE_DOWNLOADING = "com.android.launcher.action.ACTION_PACKAGE_DOWNLOADING";
    private static final String ACTION_PACKAGE_ENQUEUED = "com.android.launcher.action.ACTION_PACKAGE_ENQUEUED";
    private static final String ACTION_PACKAGE_INSTALLING = "com.android.launcher.action.ACTION_PACKAGE_INSTALLING";
    private static final boolean DEBUG = false;
    private static final String EXTRA_KEY_APP_DETAIL_INTENT = "app_detailIntent";
    private static final String EXTRA_KEY_APP_ICON = "app_icon";
    private static final String EXTRA_KEY_APP_IS_GAME = "app_is_game";
    private static final String EXTRA_KEY_APP_NAME = "app_name";
    private static final String EXTRA_KEY_INSTALL_COMPLETED = "com.android.launcher.action.INSTALL_COMPLETED";
    private static final String EXTRA_KEY_PROGRESS = "progress";
    private static final String EXTRA_KEY_REASON = "reason";
    private static final String EXTRA_USER_INITIATED = "user_initiated";
    private static final String EXTRA_VALUE_REASON_INSTALL = "install";
    private static final String EXTRA_VALUE_REASON_RESTORE = "restore";
    private static final String EXTRA_VALUE_REASON_UPDATE = "update";
    private static final String TAG = "MarketUpdateReceiver";
    private InstallingLaunchItemListener mListener;

    private LaunchItem createInstallLaunchItem(Context paramContext, String paramString, Intent paramIntent) {
        String str1 = paramIntent.getStringExtra("app_name");
        String str2 = paramIntent.getStringExtra("app_icon");
        boolean bool = paramIntent.getBooleanExtra("app_is_game", false);
        return new LaunchItem(paramContext, str1, str2, paramString, (Intent) paramIntent.getParcelableExtra("app_detailIntent"), bool, this.mListener);
    }

    private boolean isLeanbackApp(Context paramContext, String paramString) {
        PackageManager pm = paramContext.getPackageManager();
        try {
            pm.getPackageInfo(paramString, 1);

            return pm.getLeanbackLaunchIntentForPackage(paramString) != null;
        } catch (PackageManager.NameNotFoundException ex) {
        }
        return true;
    }

    public void onReceive(Context context, Intent intent) {
        Uri uri = intent.getData();
        if (uri != null) {
            String pkgName = uri.getSchemeSpecificPart();
            if (pkgName != null && isLeanbackApp(context, pkgName)) {
                String action = intent.getAction();
                LaunchItem installLaunchItem = AppsManager.getInstance(context).getInstallingLaunchItem(pkgName);
                if (installLaunchItem != null || intent.getBooleanExtra("user_initiated", false)) {
                    boolean wasAdded = false;
                    if (installLaunchItem == null) {
                        wasAdded = true;
                        installLaunchItem = createInstallLaunchItem(context, pkgName, intent);
                        this.mListener.onInstallingLaunchItemAdded(installLaunchItem);
                    }
                    boolean success = false;
                    if ("com.android.launcher.action.ACTION_PACKAGE_ENQUEUED".equals(action)) {
                        installLaunchItem.setInstallProgressPercent(-1);
                        String reason = "install";
                        if (intent.hasExtra("reason")) {
                            reason = intent.getStringExtra("reason");
                        }
                        if ("install".equals(reason)) {
                            installLaunchItem.setInstallStateStringResourceId(R.string.install_pending);
                        } else if ("update".equals(reason)) {
                            installLaunchItem.setInstallStateStringResourceId(R.string.update_pending);
                        } else if ("restore".equals(reason)) {
                            installLaunchItem.setInstallStateStringResourceId(R.string.restore_pending);
                        } else {
                            installLaunchItem.setInstallStateStringResourceId(R.string.unknown_state);
                        }
                        Log.d("MarketUpdateReceiver", "market has promised to " + reason + ": " + pkgName + " user initiated: " + intent.getBooleanExtra("user_initiated", false));
                    } else if ("com.android.launcher.action.ACTION_PACKAGE_DOWNLOADING".equals(action)) {
                        int progress = intent.getIntExtra("progress", 0);
                        installLaunchItem.setInstallProgressPercent(progress);
                        installLaunchItem.setInstallStateStringResourceId(R.string.downloading);
                        Log.d("MarketUpdateReceiver", "market is downloading (" + progress + "%): " + pkgName);
                    } else if ("com.android.launcher.action.ACTION_PACKAGE_INSTALLING".equals(action)) {
                        installLaunchItem.setInstallProgressPercent(-1);
                        installLaunchItem.setInstallStateStringResourceId(R.string.installing);
                        Log.d("MarketUpdateReceiver", "market is installing: " + pkgName);
                    } else if ("com.android.launcher.action.ACTION_PACKAGE_DEQUEUED".equals(action)) {
                        this.mListener.onInstallingLaunchItemRemoved(installLaunchItem, false);
                        installLaunchItem.setInstallStateStringResourceId(0);
                        installLaunchItem.setInstallProgressPercent(-1);
                        success = intent.getBooleanExtra("com.android.launcher.action.INSTALL_COMPLETED", false);
                        if (success) {
                            Log.d("MarketUpdateReceiver", "market has installed: " + pkgName);
                        } else {
                            Log.d("MarketUpdateReceiver", "market has decided not to install: " + pkgName);
                        }
                    } else {
                        installLaunchItem.setInstallProgressPercent(-1);
                        installLaunchItem.setInstallStateStringResourceId(R.string.unknown_state);
                        Log.d("MarketUpdateReceiver", "unknown message " + action);
                    }
                    if ("com.android.launcher.action.ACTION_PACKAGE_DEQUEUED".equals(action)) {
                        this.mListener.onInstallingLaunchItemRemoved(installLaunchItem, success);
                    } else if (wasAdded) {
                        this.mListener.onInstallingLaunchItemAdded(installLaunchItem);
                    } else {
                        this.mListener.onInstallingLaunchItemChanged(installLaunchItem);
                    }
                }
            }
        }
    }

    /* todo use 1.1.7 version for now
    public void onReceive(Context paramContext, Intent paramIntent) {
        Object localObject = paramIntent.getData();
        if (localObject == null) {
        }
        String str2;
        String str1;
        do {
            LaunchItem localLaunchItem;
            do {
                do {
                    do {
                        return;
                        this.mListener = AppsManager.getInstance(paramContext);
                        str2 = ((Uri) localObject).getSchemeSpecificPart();
                    } while (TextUtils.isEmpty(str2));
                    str1 = paramIntent.getAction();
                    localLaunchItem = AppsManager.getInstance(paramContext).getInstallingLaunchItem(str2);
                }
                while ((localLaunchItem == null) && (!paramIntent.getBooleanExtra("user_initiated", false)));
                i = 0;
                if (isLeanbackApp(paramContext, str2)) {
                    break;
                }
            }
            while ((localLaunchItem == null) || (!"com.android.launcher.action.ACTION_PACKAGE_DEQUEUED".equals(str1)) || (paramIntent.getBooleanExtra("com.android.launcher.action.INSTALL_COMPLETED", false)));
            localLaunchItem.setInstallStateStringResourceId(0);
            localLaunchItem.setInstallProgressPercent(-1);
            this.mListener.onInstallingLaunchItemRemoved(localLaunchItem, false);
            return;
            localObject = localLaunchItem;
            if (localLaunchItem != null) {
                break;
            }
        } while ("com.android.launcher.action.ACTION_PACKAGE_DEQUEUED".equals(str1));
        int i = 1;
        localObject = createInstallLaunchItem(paramContext, str2, paramIntent);
        boolean bool = false;
        if ("com.android.launcher.action.ACTION_PACKAGE_ENQUEUED".equals(str1)) {
            ((LaunchItem) localObject).setInstallProgressPercent(-1);
            paramContext = "install";
            if (paramIntent.hasExtra("reason")) {
                paramContext = paramIntent.getStringExtra("reason");
            }
            if ("install".equals(paramContext)) {
                ((LaunchItem) localObject).setInstallStateStringResourceId(2131493003);
                paramIntent.getBooleanExtra("user_initiated", false);
            }
        }
        for (; ; ) {
            if (!"com.android.launcher.action.ACTION_PACKAGE_DEQUEUED".equals(str1)) {
                break label402;
            }
            this.mListener.onInstallingLaunchItemRemoved((LaunchItem) localObject, bool);
            return;
            if ("update".equals(paramContext)) {
                ((LaunchItem) localObject).setInstallStateStringResourceId(2131493083);
                break;
            }
            if ("restore".equals(paramContext)) {
                ((LaunchItem) localObject).setInstallStateStringResourceId(2131493067);
                break;
            }
            ((LaunchItem) localObject).setInstallStateStringResourceId(2131493082);
            break;
            if ("com.android.launcher.action.ACTION_PACKAGE_DOWNLOADING".equals(str1)) {
                ((LaunchItem) localObject).setInstallProgressPercent(paramIntent.getIntExtra("progress", 0));
                ((LaunchItem) localObject).setInstallStateStringResourceId(2131492982);
            } else if ("com.android.launcher.action.ACTION_PACKAGE_INSTALLING".equals(str1)) {
                ((LaunchItem) localObject).setInstallProgressPercent(-1);
                ((LaunchItem) localObject).setInstallStateStringResourceId(2131493004);
            } else if ("com.android.launcher.action.ACTION_PACKAGE_DEQUEUED".equals(str1)) {
                ((LaunchItem) localObject).setInstallStateStringResourceId(0);
                ((LaunchItem) localObject).setInstallProgressPercent(-1);
                bool = paramIntent.getBooleanExtra("com.android.launcher.action.INSTALL_COMPLETED", false);
            } else {
                ((LaunchItem) localObject).setInstallProgressPercent(-1);
                ((LaunchItem) localObject).setInstallStateStringResourceId(2131493082);
            }
        }
        label402:
        if (i != 0) {
            this.mListener.onInstallingLaunchItemAdded((LaunchItem) localObject);
            return;
        }
        this.mListener.onInstallingLaunchItemChanged((LaunchItem) localObject);
    }*/
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/MarketUpdateReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */