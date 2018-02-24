package com.google.android.tvrecommendations.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

class ServicePartner {
    private static final Object sLock = new Object();
    private static ServicePartner sPartner;
    private static boolean sSearched = false;
    private final String mPackageName;
    private final String mReceiverName;
    private final Resources mResources;

    public static ServicePartner get(Context context) {
        PackageManager pm = context.getPackageManager();
        synchronized (sLock) {
            if (!sSearched) {
                ResolveInfo info = getPartnerResolveInfo(pm, null);
                if (info != null) {
                    String packageName = info.activityInfo.packageName;
                    try {
                        sPartner = new ServicePartner(packageName, info.activityInfo.name, pm.getResourcesForApplication(packageName));
                    } catch (NameNotFoundException e) {
                        Log.w("ServicePartner", "Failed to find resources for " + packageName);
                    }
                }
                sSearched = true;
                if (sPartner == null) {
                    sPartner = new ServicePartner(null, null, null);
                }
            }
        }
        return sPartner;
    }

    private ServicePartner(String packageName, String receiverName, Resources res) {
        this.mPackageName = packageName;
        this.mReceiverName = receiverName;
        this.mResources = res;
    }

    public String[] getOutOfBoxOrder() {
        if (this.mResources == null || TextUtils.isEmpty(this.mPackageName)) {
            return null;
        }
        int resId = this.mResources.getIdentifier("partner_out_of_box_order", "array", this.mPackageName);
        if (resId != 0) {
            return this.mResources.getStringArray(resId);
        }
        return null;
    }

    private static ResolveInfo getPartnerResolveInfo(PackageManager pm, ComponentName name) {
        Intent intent = new Intent("com.google.android.leanbacklauncher.action.PARTNER_CUSTOMIZATION");
        if (name != null) {
            intent.setPackage(name.getPackageName());
        }
        for (ResolveInfo info : pm.queryBroadcastReceivers(intent, 0)) {
            if (isSystemApp(info)) {
                return info;
            }
        }
        return null;
    }

    private static boolean isSystemApp(ResolveInfo info) {
        return (info.activityInfo == null || (info.activityInfo.applicationInfo.flags & 1) == 0) ? false : true;
    }
}
