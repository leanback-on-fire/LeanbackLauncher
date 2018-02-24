package com.google.android.tvlauncher.appsview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;

import com.google.android.tvlauncher.util.porting.Edited;
import com.google.android.tvlauncher.util.porting.Reason;

public class PackageChangedReceiver
        extends BroadcastReceiver {
    private Listener mListener;

    public PackageChangedReceiver(Listener paramListener) {
        this.mListener = paramListener;
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        localIntentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        localIntentFilter.addAction("android.intent.action.PACKAGE_FULLY_REMOVED");
        localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        localIntentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        localIntentFilter.addDataScheme("package");
        return localIntentFilter;
    }

    private String getPackageName(Intent paramIntent) {
        Uri uri = paramIntent.getData();
        if (uri != null) {
            return uri.getSchemeSpecificPart();
        }
        return null;
    }

    @Edited(reason = Reason.IF_ELSE_DECOMPILE_ERROR)
    public void onReceive(Context paramContext, Intent paramIntent) {
        String packageName = getPackageName(paramIntent);
        if ((packageName == null) || (packageName.length() == 0)) {
            return;
        }

        String str = paramIntent.getAction();

        if ("android.intent.action.PACKAGE_REPLACED".equals(str)) {
            if (paramIntent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                this.mListener.onPackageReplaced(packageName);
                return;
            } // todo
        }

        if ("android.intent.action.PACKAGE_ADDED".equals(str)) {
            this.mListener.onPackageAdded(packageName);
            return;
        }
        if ("android.intent.action.PACKAGE_CHANGED".equals(str)) {
            this.mListener.onPackageChanged(packageName);
            return;
        }
        if ("android.intent.action.PACKAGE_FULLY_REMOVED".equals(str)) {
            this.mListener.onPackageFullyRemoved(packageName);
            return;
        }
        if (!"android.intent.action.PACKAGE_REMOVED".equals(str)) {
            this.mListener.onPackageRemoved(packageName);
        }
    }

    public static abstract interface Listener {
        public abstract void onPackageAdded(String paramString);

        public abstract void onPackageChanged(String paramString);

        public abstract void onPackageFullyRemoved(String paramString);

        public abstract void onPackageRemoved(String paramString);

        public abstract void onPackageReplaced(String paramString);
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/PackageChangedReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */