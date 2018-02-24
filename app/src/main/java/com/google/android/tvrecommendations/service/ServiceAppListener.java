package com.google.android.tvrecommendations.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.google.android.tvrecommendations.service.ServiceAppReceiver.Listener;

public class ServiceAppListener implements Listener {
    private final Context mContext;
    private BroadcastReceiver mExternalAppsUpdateReceiver;
    private final Ranker mRanker;
    private final ServiceAppReceiver mServiceAppReceiver = new ServiceAppReceiver(this);

    public ServiceAppListener(Context context, Ranker ranker) {
        this.mContext = context;
        this.mRanker = ranker;
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
        this.mRanker.onActionPackageRemoved(packageName);
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
        this.mExternalAppsUpdateReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                ServiceAppListener.this.onExternalPackagesStatusChanged();
            }
        };
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
