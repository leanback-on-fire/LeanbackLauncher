package com.google.android.tvrecommendations.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.UserHandle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.tvrecommendations.service.RecommendationsManager.NotificationResolver;

public abstract class BaseNotificationsService extends NotificationListenerService implements NotificationResolver {
    private ConnectivityManager mConnectivityManager;
    private int mCurrentUser;
    private RecommendationsManager mManager;
    private final RankerParametersFactory mRankerParametersFactory;
    private BroadcastReceiver mReceiver;
    private final String mTag;
    private final boolean mUnbundled;

    public BaseNotificationsService(boolean unbundled, RankerParametersFactory rankerParametersFactory) {
        this.mUnbundled = unbundled;
        this.mRankerParametersFactory = rankerParametersFactory;
        this.mTag = unbundled ? "UB-BaseNotifService" : "B-BaseNotifService";
    }

    public void onCreate() {
        super.onCreate();
        this.mCurrentUser = UserHandle.myUserId();
        Context appContext = getApplicationContext();
        this.mConnectivityManager = (ConnectivityManager) appContext.getSystemService("connectivity");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.mManager = RecommendationsManager.getInstance(this, this.mUnbundled, this.mRankerParametersFactory);
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                NetworkInfo info = BaseNotificationsService.this.mConnectivityManager.getActiveNetworkInfo();
                if (info == null || !info.isConnected()) {
                    BaseNotificationsService.this.mManager.removeAllCaptivePortalNotifications();
                }
            }
        };
        appContext.registerReceiver(this.mReceiver, filter);
        this.mManager.setNotificationResolver(this);
        this.mManager.onCreate();
    }

    public void onDestroy() {
        getApplicationContext().unregisterReceiver(this.mReceiver);
        this.mManager.onDestroy();
    }

    public void cancelRecommendation(String key) {
        super.cancelNotification(key);
    }

    public StatusBarNotification getNotification(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        String[] keySet = new String[]{key};
        StatusBarNotification[] ret = null;
        if (this.mManager.isConnectedToNotificationService()) {
            try {
                ret = getActiveNotifications(keySet, 0);
            } catch (SecurityException e) {
                Log.d(this.mTag, "Exception fetching notification", e);
            }
        } else {
            Log.e(this.mTag, "Image request with DISCONNECTED service, ignoring request.");
        }
        if (ret == null || ret.length <= 0) {
            return null;
        }
        return ret[0];
    }

    public void fetchExistingNotifications() {
        try {
            onFetchExistingNotifications(getActiveNotifications(1));
        } catch (SecurityException e) {
            Log.e(this.mTag, "Exception fetching existing notifications", e);
        }
    }

    protected void onFetchExistingNotifications(StatusBarNotification[] notifications) {
        this.mManager.resetNotifications();
        for (StatusBarNotification sbn : notifications) {
            if (RecommendationsUtil.isRecommendation(sbn) && sbn.getUserId() == this.mCurrentUser) {
                this.mManager.addNotification(sbn);
            } else if (RecommendationsUtil.isCaptivePortal(getApplicationContext(), sbn)) {
                this.mManager.addCaptivePortalNotification(sbn);
            }
        }
    }

    public void onListenerConnected() {
        if (VERSION.SDK_INT >= 23) {
            requestInterruptionFilter(4);
        } else if (VERSION.SDK_INT >= 21) {
            requestListenerHints(1);
        }
        setOnNotificationPostedTrim(1);
        this.mManager.sendConnectionStatus(true);
    }

    public IBinder onBind(Intent intent) {
        if ("android.service.notification.NotificationListenerService".equals(intent.getAction())) {
            return super.onBind(intent);
        }
        return null;
    }

    public boolean onUnbind(Intent intent) {
        if ("android.service.notification.NotificationListenerService".equals(intent.getAction())) {
            this.mManager.sendConnectionStatus(false);
        }
        return super.onUnbind(intent);
    }

    public void onNotificationPosted(StatusBarNotification sbn) {
        if (RecommendationsUtil.isRecommendation(sbn) && sbn.getUserId() == this.mCurrentUser) {
            this.mManager.addNotification(sbn);
        }
        if (RecommendationsUtil.isCaptivePortal(getApplicationContext(), sbn)) {
            this.mManager.addCaptivePortalNotification(sbn);
        }
    }

    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (RecommendationsUtil.isRecommendation(sbn) && sbn.getUserId() == this.mCurrentUser) {
            this.mManager.removeNotification(sbn);
        }
    }
}
