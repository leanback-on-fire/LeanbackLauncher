package com.rockchips.android.leanbacklauncher.tvrecommendations.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.rockchips.android.leanbacklauncher.tvrecommendations.service.ServiceAppReceiver.Listener;

public class ServiceAppListener implements Listener {
    private final Context mContext;
    private BroadcastReceiver mExternalAppsUpdateReceiver;
    private final Ranker mRanker;
    private final ServiceAppReceiver mServiceAppReceiver;

    /* renamed from: com.rockchips.android.tvrecommendations.service.ServiceAppListener.1 */
    class C02291 extends BroadcastReceiver {
        C02291() {
        }

        public void onReceive(Context context, Intent intent) {
            ServiceAppListener.this.onExternalPackagesStatusChanged();
        }
    }

    public ServiceAppListener(Context context, Ranker ranker) {
        this.mContext = context;
        this.mRanker = ranker;
        this.mServiceAppReceiver = new ServiceAppReceiver(this);
    }

    public void onCreate() {
        registerReceivers();
    }

    public void onDestroy() {
        unregisterReceivers();
    }

    public void onPackageAdded(String packageName) {
        this.mRanker.onActionPackageAdded(packageName);
    }

    public void onPackageChanged(String packageName) {
    }

    public void onPackageFullyRemoved(String packageName) {
        this.mRanker.onActionPackageRemoved(packageName);
    }

    public void onPackageRemoved(String packageName) {
    }

    public void onPackageReplaced(String packageName) {
    }

    private void registerReceivers() {
        this.mContext.registerReceiver(this.mServiceAppReceiver, ServiceAppReceiver.getIntentFilter());
        registerExternalAppsReceiver();
    }

    private void unregisterReceivers() {
        this.mContext.unregisterReceiver(this.mServiceAppReceiver);
        unregisterExternalAppsReceiver();
    }

    private void onExternalPackagesStatusChanged() {
    }

    private void registerExternalAppsReceiver() {
        this.mExternalAppsUpdateReceiver = new C02291();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
        filter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
        this.mContext.registerReceiver(this.mExternalAppsUpdateReceiver, filter);
    }

    private void unregisterExternalAppsReceiver() {
        if (this.mExternalAppsUpdateReceiver != null) {
            this.mContext.unregisterReceiver(this.mExternalAppsUpdateReceiver);
        }
    }
}
