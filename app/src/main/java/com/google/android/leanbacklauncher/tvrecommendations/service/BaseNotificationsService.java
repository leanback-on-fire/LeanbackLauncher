package com.google.android.leanbacklauncher.tvrecommendations.service;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.IBinder;
import android.os.UserHandle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.leanbacklauncher.tvrecommendations.service.RecommendationsManager.NotificationResolver;
import com.google.android.leanbacklauncher.util.ReflectUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseNotificationsService extends NotificationListenerService implements NotificationResolver {
    private final BatchHandler mBatchHandler;
    private int mCurrentUser;
    private RecommendationsManager mManager;
    private final List<StatusBarNotification> mPostedBatch;
    private final List<StatusBarNotification> mRemovedBatch;
    private final String mTag;
    private final boolean mUnbundled;

    private class BatchHandler extends Handler {
        private BatchHandler() {
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public synchronized void handleMessage(android.os.Message r4) {
            /*
            r3 = this;
            r0 = 1;
            monitor-enter(r3);
            r1 = r4.what;	 Catch:{ all -> 0x0039 }
            switch(r1) {
                case 1: goto L_0x003f;
                case 2: goto L_0x0063;
                case 3: goto L_0x0009;
                case 4: goto L_0x0087;
                default: goto L_0x0007;
            };
        L_0x0007:
            monitor-exit(r3);
            return;
        L_0x0009:
            r0 = com.google.android.tvrecommendations.service.BaseNotificationsService.this;	 Catch:{ all -> 0x0039 }
            r1 = r0.mPostedBatch;	 Catch:{ all -> 0x0039 }
            monitor-enter(r1);	 Catch:{ all -> 0x0039 }
            r0 = com.google.android.tvrecommendations.service.BaseNotificationsService.this;	 Catch:{ all -> 0x003c }
            r0 = r0.mManager;	 Catch:{ all -> 0x003c }
            r2 = com.google.android.tvrecommendations.service.BaseNotificationsService.this;	 Catch:{ all -> 0x003c }
            r2 = r2.mPostedBatch;	 Catch:{ all -> 0x003c }
            r0.onRecommendationsReset(r2);	 Catch:{ all -> 0x003c }
            r0 = com.google.android.tvrecommendations.service.BaseNotificationsService.this;	 Catch:{ all -> 0x003c }
            r0 = r0.mManager;	 Catch:{ all -> 0x003c }
            r2 = com.google.android.tvrecommendations.service.BaseNotificationsService.this;	 Catch:{ all -> 0x003c }
            r2 = r2.mPostedBatch;	 Catch:{ all -> 0x003c }
            r0.onRecommendationBatchPosted(r2);	 Catch:{ all -> 0x003c }
            r0 = com.google.android.tvrecommendations.service.BaseNotificationsService.this;	 Catch:{ all -> 0x003c }
            r0 = r0.mPostedBatch;	 Catch:{ all -> 0x003c }
            r0.clear();	 Catch:{ all -> 0x003c }
            monitor-exit(r1);	 Catch:{ all -> 0x0039 }
            goto L_0x0007;
        L_0x0039:
            r0 = move-exception;
            monitor-exit(r3);
            throw r0;
        L_0x003c:
            r0 = move-exception;
            monitor-exit(r1);	 Catch:{ all -> 0x0039 }
            throw r0;	 Catch:{ all -> 0x0039 }
        L_0x003f:
            r0 = com.google.android.tvrecommendations.service.BaseNotificationsService.this;	 Catch:{ all -> 0x0039 }
            r1 = r0.mPostedBatch;	 Catch:{ all -> 0x0039 }
            monitor-enter(r1);	 Catch:{ all -> 0x0039 }
            r0 = com.google.android.tvrecommendations.service.BaseNotificationsService.this;	 Catch:{ all -> 0x0060 }
            r0 = r0.mManager;	 Catch:{ all -> 0x0060 }
            r2 = com.google.android.tvrecommendations.service.BaseNotificationsService.this;	 Catch:{ all -> 0x0060 }
            r2 = r2.mPostedBatch;	 Catch:{ all -> 0x0060 }
            r0.onRecommendationBatchPosted(r2);	 Catch:{ all -> 0x0060 }
            r0 = com.google.android.tvrecommendations.service.BaseNotificationsService.this;	 Catch:{ all -> 0x0060 }
            r0 = r0.mPostedBatch;	 Catch:{ all -> 0x0060 }
            r0.clear();	 Catch:{ all -> 0x0060 }
            monitor-exit(r1);	 Catch:{ all -> 0x0039 }
            goto L_0x0007;
        L_0x0060:
            r0 = move-exception;
            monitor-exit(r1);	 Catch:{ all -> 0x0039 }
            throw r0;	 Catch:{ all -> 0x0039 }
        L_0x0063:
            r0 = com.google.android.tvrecommendations.service.BaseNotificationsService.this;	 Catch:{ all -> 0x0039 }
            r1 = r0.mRemovedBatch;	 Catch:{ all -> 0x0039 }
            monitor-enter(r1);	 Catch:{ all -> 0x0039 }
            r0 = com.google.android.tvrecommendations.service.BaseNotificationsService.this;	 Catch:{ all -> 0x0084 }
            r0 = r0.mManager;	 Catch:{ all -> 0x0084 }
            r2 = com.google.android.tvrecommendations.service.BaseNotificationsService.this;	 Catch:{ all -> 0x0084 }
            r2 = r2.mRemovedBatch;	 Catch:{ all -> 0x0084 }
            r0.onRecommendationBatchRemoved(r2);	 Catch:{ all -> 0x0084 }
            r0 = com.google.android.tvrecommendations.service.BaseNotificationsService.this;	 Catch:{ all -> 0x0084 }
            r0 = r0.mRemovedBatch;	 Catch:{ all -> 0x0084 }
            r0.clear();	 Catch:{ all -> 0x0084 }
            monitor-exit(r1);	 Catch:{ all -> 0x0039 }
            goto L_0x0007;
        L_0x0084:
            r0 = move-exception;
            monitor-exit(r1);	 Catch:{ all -> 0x0039 }
            throw r0;	 Catch:{ all -> 0x0039 }
        L_0x0087:
            r1 = com.google.android.tvrecommendations.service.BaseNotificationsService.this;	 Catch:{ all -> 0x0039 }
            r1 = r1.mManager;	 Catch:{ all -> 0x0039 }
            r2 = r4.arg1;	 Catch:{ all -> 0x0039 }
            if (r2 != r0) goto L_0x0096;
        L_0x0091:
            r1.setConnectedToNotificationService(r0);	 Catch:{ all -> 0x0039 }
            goto L_0x0007;
        L_0x0096:
            r0 = 0;
            goto L_0x0091;
            */
            //throw new UnsupportedOperationException("Method not decompiled: com.google.android.tvrecommendations.service.BaseNotificationsService.BatchHandler.handleMessage(android.os.Message):void");
        }
    }

    public BaseNotificationsService(boolean unbundled) {
        this.mPostedBatch = new ArrayList();
        this.mRemovedBatch = new ArrayList();
        this.mBatchHandler = new BatchHandler();
        this.mUnbundled = unbundled;
        this.mTag = unbundled ? "UbNotificationsService" : "NotificationsServiceB";
    }

    public void onCreate() {
        super.onCreate();
        this.mCurrentUser =  (int)ReflectUtils.invokeMethod(UserHandle.class, null, "myUserId", new Class[]{}, new Object[]{});
        this.mManager = RecommendationsManager.getInstance(this, this.mUnbundled);
        this.mManager.setNotificationResolver(this);
        this.mManager.onCreate();
    }

    public void onDestroy() {
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
                ret = getActiveNotifications(keySet);
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
        this.mBatchHandler.removeMessages(1);
        this.mBatchHandler.removeMessages(2);
        this.mBatchHandler.removeMessages(3);
        try {
            StatusBarNotification[] existingNotifications = getActiveNotifications(new String[]{String.valueOf(1)});
            synchronized (this.mPostedBatch) {
                this.mPostedBatch.clear();
                for (StatusBarNotification sbn : existingNotifications) {
                    if (RecommendationsUtil.isRecommendation(sbn) && sbn.getUserId() == this.mCurrentUser) {
                        this.mPostedBatch.add(sbn);
                    }
                }
            }
            this.mBatchHandler.sendMessage(this.mBatchHandler.obtainMessage(3));
        } catch (SecurityException e) {
            Log.e(this.mTag, "Exception fetching existing notifications", e);
        }
    }

    public void onListenerConnected() {
        if (VERSION.SDK_INT >= 23) {
            requestInterruptionFilter(4);
        } else if (VERSION.SDK_INT >= 21) {
            requestListenerHints(1);
        }
        ReflectUtils.invokeMethod(NotificationListenerService.class.getSuperclass(), this, "setOnNotificationPostedTrim", new Class[]{int.class}, new Integer[]{1});
        //setOnNotificationPostedTrim(1);
        this.mBatchHandler.sendMessage(this.mBatchHandler.obtainMessage(4, 1, 0));
    }

    public IBinder onBind(Intent intent) {
        if ("android.service.notification.NotificationListenerService".equals(intent.getAction())) {
            return super.onBind(intent);
        }
        return null;
    }

    public boolean onUnbind(Intent intent) {
        if ("android.service.notification.NotificationListenerService".equals(intent.getAction())) {
            this.mBatchHandler.sendMessage(this.mBatchHandler.obtainMessage(4, 0, 0));
        }
        return super.onUnbind(intent);
    }

    public void onNotificationPosted(StatusBarNotification sbn) {
        if (RecommendationsUtil.isCaptivePortal(sbn) && sbn.getUserId() == this.mCurrentUser) {
            PendingIntent pendingIntent = sbn.getNotification().contentIntent;
            if (pendingIntent != null) {
                try {
                    pendingIntent.send();
                } catch (CanceledException e) {
                    Log.w(this.mTag, "Cannot start Captive Portal login.");
                }
            }
        } else if (RecommendationsUtil.isRecommendation(sbn) && sbn.getUserId() == this.mCurrentUser) {
            synchronized (this.mPostedBatch) {
                this.mPostedBatch.add(sbn);
            }
            this.mBatchHandler.removeMessages(1);
            this.mBatchHandler.sendMessageDelayed(this.mBatchHandler.obtainMessage(1), 100);
        }
    }

    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (RecommendationsUtil.isRecommendation(sbn) && sbn.getUserId() == this.mCurrentUser) {
            this.mBatchHandler.removeMessages(2);
            synchronized (this.mRemovedBatch) {
                this.mRemovedBatch.add(sbn);
            }
            this.mBatchHandler.sendMessageDelayed(this.mBatchHandler.obtainMessage(2), 100);
        }
    }
}
