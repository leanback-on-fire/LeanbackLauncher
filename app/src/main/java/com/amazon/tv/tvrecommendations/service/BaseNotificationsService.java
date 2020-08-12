package com.amazon.tv.tvrecommendations.service;

import android.app.Notification;
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

import com.amazon.tv.tvrecommendations.service.RecommendationsManager.NotificationResolver;

public abstract class BaseNotificationsService extends NotificationListenerService implements NotificationResolver {
    private ConnectivityManager mConnectivityManager;
    private UserHandle mCurrentUser;
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
        // UserManager manager = (UserManager) getApplicationContext().getSystemService(Context.USER_SERVICE);
        // this.mCurrentUser = manager.getUserProfiles().get(0); // todo hacky
        Context appContext = getApplicationContext();
        this.mConnectivityManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
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
                ret = getActiveNotifications(keySet); // 0
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
        final StatusBarNotification[] snotifications = getActiveNotifications();

        /* Get rid of any recommendations already ruined by Amazon's hand... */

        for (StatusBarNotification sbn : snotifications) {
            Notification notification = sbn.getNotification();
            // if (BuildConfig.DEBUG) Log.d(this.mTag, "fetchExistingNotifications: get " + notification);

            // FIXME (issue with com.amazon.device.sale.service)
            if (notification.largeIcon == null) {
                cancelNotification(sbn.getKey());
                // if (BuildConfig.DEBUG) Log.d(this.mTag, "fetchExistingNotifications: cancel " + notification);
            }
        }

        onFetchExistingNotifications(getActiveNotifications()); // 1
    }

    protected void onFetchExistingNotifications(StatusBarNotification[] notifications) {
        // if (BuildConfig.DEBUG) Log.d(this.mTag, " onFetchExistingNotifications +++ resetNotifications +++");
        this.mManager.resetNotifications();
        int i = 0;
        for (StatusBarNotification sbn : notifications) {
            // && sbn.getUser().equals(this.mCurrentUser)
            if (RecommendationsUtil.isRecommendation(sbn)) {
                i++;
                this.mManager.addNotification(sbn);
                // if (BuildConfig.DEBUG) Log.d(this.mTag, " onFetchExistingNotifications +++ isRecommendation: ADD +++ " + i);
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
        // todo setOnNotificationPostedTrim(1);
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
        // sbn.getUser().equals(this.mCurrentUser)
        if (RecommendationsUtil.isRecommendation(sbn)) {
            this.mManager.addNotification(sbn);
        }
        if (RecommendationsUtil.isCaptivePortal(getApplicationContext(), sbn)) {
            this.mManager.addCaptivePortalNotification(sbn);
        }
    }

    public void onNotificationRemoved(StatusBarNotification sbn) {
        // && sbn.getUser().equals(this.mCurrentUser)
        if (RecommendationsUtil.isRecommendation(sbn)) {
            this.mManager.removeNotification(sbn);
        }
    }
}
