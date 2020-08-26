package com.amazon.tv.tvrecommendations.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.amazon.tv.leanbacklauncher.BuildConfig;
import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.tvrecommendations.IRecommendationsClient;
import com.amazon.tv.tvrecommendations.TvRecommendation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

class RecommendationsManager implements Ranker.RankingListener {
    @SuppressLint({"StaticFieldLeak"})
    private static RecommendationsManager sInstance = null;
    private ServiceAppListener mAppListener;
    private int mBannerMaxHeight;
    private int mBannerMaxWidth;
    private int mCardMaxHeight;
    private int mCardMaxWidth;
    private final ClientHandler mClientHandler = new ClientHandler();
    private boolean mConnectedToNotificationService = false;
    private final Context mContext;
    private DbHelper mDbHelper;
    private int mMaxRecsPerApp;
    private NotificationResolver mNotificationResolver;
    private final HashMap<String, ArrayList<StatusBarNotification>> mPackageToRecSet = new HashMap<>();
    private final ArrayList<StatusBarNotification> mPartnerList = new ArrayList<>();
    private Ranker mRanker;
    private boolean mRankerReady = false;
    private boolean mStarted;
    private final String mTag;

    public interface NotificationResolver {
        void cancelRecommendation(String str);

        void fetchExistingNotifications();

        StatusBarNotification getNotification(String str);
    }

    @SuppressLint({"HandlerLeak"})
    private class ClientHandler extends Handler {
        private final List<StatusBarNotification> mCaptivePortalPosted;
        private final List<StatusBarNotification> mCaptivePortalRemoved;
        private final List<StatusBarNotification> mCaptivePortalShowing;
        private final RemoteCallbackList<IRecommendationsClient> mClients;
        private final RemoteCallbackList<IRecommendationsClient> mPartnerClients;
        private final List<RecOperation> mRecBatch;

        private ClientHandler() {
            this.mPartnerClients = new RemoteCallbackList();
            this.mClients = new RemoteCallbackList();
            this.mCaptivePortalPosted = new ArrayList();
            this.mCaptivePortalShowing = new ArrayList();
            this.mCaptivePortalRemoved = new ArrayList();
            this.mRecBatch = new ArrayList();
        }

        public void registerNotificationsClient(IRecommendationsClient client, boolean isPartnerClient) {
            if (isPartnerClient) {
                this.mPartnerClients.register(client);
            } else {
                this.mClients.register(client);
            }
        }

        public void unregisterNotificationsClient(IRecommendationsClient client, boolean isPartnerClient) throws RemoteException {
            if (BuildConfig.DEBUG)
                Log.d(RecommendationsManager.this.mTag, "unregisterNotificationsClient: " + client);
            if (client == null) {
                return;
            }
            if (isPartnerClient) {
                this.mPartnerClients.unregister(client);
            } else {
                this.mClients.unregister(client);
            }
        }

        public int getRegisteredClientCount() {
            return this.mClients.getRegisteredCallbackCount();
        }

        public void enqueueStartIfReady() {
            removeMessages(0);
            sendEmptyMessage(0);
        }

        public void enqueueNotificationReset() {
            removeMessages(1);
            removeMessages(4);
            removeMessages(5);
            this.mRecBatch.clear();
            this.mCaptivePortalPosted.clear();
            this.mCaptivePortalShowing.clear();
            this.mCaptivePortalRemoved.clear();
            removeMessages(2);
            sendEmptyMessageDelayed(2, 100);
        }

        public synchronized void enqueueNotificationPosted(StatusBarNotification sbn) {
            this.mRecBatch.add(RecOperation.newAdd(sbn));
            removeMessages(1);
            sendEmptyMessageDelayed(1, 100);
        }

        public synchronized void enqueueNotificationRemoved(StatusBarNotification sbn) {
            this.mRecBatch.add(RecOperation.newRemove(sbn));
            removeMessages(1);
            sendEmptyMessageDelayed(1, 100);
        }

        public void enqueueConnectionStatus(boolean connected) {
            int i;
            if (connected) {
                i = 1;
            } else {
                i = 0;
            }
            sendMessage(obtainMessage(3, i, 0));
        }

        public synchronized void enqueueCaptivePortalPosted(StatusBarNotification sbn) {
            if (!(this.mCaptivePortalPosted.contains(sbn) || this.mCaptivePortalShowing.contains(sbn))) {
                if (this.mCaptivePortalShowing.size() > 0) {
                    enqueueAllCaptivePortalRemoved();
                }
                this.mCaptivePortalPosted.clear();
                this.mCaptivePortalPosted.add(sbn);
                removeMessages(4);
                sendEmptyMessageDelayed(4, 100);
            }
        }

        public synchronized void enqueueAllCaptivePortalRemoved() {
            if (this.mCaptivePortalShowing.size() > 0) {
                synchronized (this.mCaptivePortalRemoved) {
                    this.mCaptivePortalRemoved.addAll(this.mCaptivePortalShowing);
                }
                removeMessages(5);
                sendEmptyMessageDelayed(5, 100);
            }
        }

        private String messageCodeToString(int messageCode) {
            switch (messageCode) {
                case 0:
                    return "MSG_START (" + messageCode + ")";
                case 1:
                    return "MSG_NOTIFICATION (" + messageCode + ")";
                case 2:
                    return "MSG_NOTIFICATION_RESET (" + messageCode + ")";
                case 3:
                    return "MSG_CONNECTION_STATUS (" + messageCode + ")";
                case 4:
                    return "MSG_CAPTIVE_PORTAL_POSTED (" + messageCode + ")";
                case 5:
                    return "MSG_CAPTIVE_PORTAL_REMOVED (" + messageCode + ")";
                default:
                    return "UNKNOWN (" + messageCode + ")";
            }
        }

        public synchronized void handleMessage(Message msg) {
            boolean z = true;
            synchronized (this) {
                //if (BuildConfig.DEBUG)
                //    Log.d(RecommendationsManager.this.mTag, "ClientHandler#handleMessage: msg=" + msg + "\n\tmsg.what=" + messageCodeToString(msg.what));
                ArrayList<RecOperation> operations;
                switch (msg.what) {
                    case 0: // MSG_START
                        RecommendationsManager.this.startIfReady(this.mClients, this.mPartnerClients);
                        break;
                    case 1: // MSG_NOTIFICATION
                        RecommendationsManager.this.recommendationBatchPostedInt(this.mClients, this.mPartnerClients, this.mRecBatch);
                        this.mRecBatch.clear();
                        break;
                    case 2: // MSG_NOTIFICATION_RESET
                        RecommendationsManager.this.onRecommendationsReset(this.mClients, this.mPartnerClients, this.mRecBatch);
                        RecommendationsManager.this.recommendationBatchPostedInt(this.mClients, this.mPartnerClients, this.mRecBatch);
                        this.mRecBatch.clear();
                        break;
                    case 3: // MSG_CONNECTION_STATUS
                        RecommendationsManager recommendationsManager = RecommendationsManager.this;
                        RemoteCallbackList remoteCallbackList = this.mClients;
                        RemoteCallbackList remoteCallbackList2 = this.mPartnerClients;
                        if (msg.arg1 != 1) {
                            z = false;
                        }
                        recommendationsManager.setConnectedToNotificationService(remoteCallbackList, remoteCallbackList2, z);
                        break;
                    case 4: // MSG_CAPTIVE_PORTAL_POSTED
                        operations = new ArrayList(this.mCaptivePortalPosted.size());
                        for (StatusBarNotification sbn : this.mCaptivePortalPosted) {
                            operations.add(RecOperation.newAdd(sbn));
                        }
                        RecommendationsManager.this.recommendationBatchPostedInt(this.mClients, this.mPartnerClients, operations);
                        this.mCaptivePortalShowing.addAll(this.mCaptivePortalPosted);
                        this.mCaptivePortalPosted.clear();
                        break;
                    case 5: // MSG_CAPTIVE_PORTAL_REMOVED
                        operations = new ArrayList(this.mCaptivePortalRemoved.size());
                        for (StatusBarNotification sbn2 : this.mCaptivePortalRemoved) {
                            operations.add(RecOperation.newRemove(sbn2));
                        }
                        RecommendationsManager.this.recommendationBatchPostedInt(this.mClients, this.mPartnerClients, operations);
                        this.mCaptivePortalShowing.removeAll(this.mCaptivePortalRemoved);
                        this.mCaptivePortalRemoved.clear();
                        break;
                }
            }
        }
    }

    private static class NotificationComparator implements Comparator<StatusBarNotification> {
        private final Context mContext;
        private final Ranker mRanker;

        public NotificationComparator(Context context, Ranker ranker) {
            this.mContext = context;
            this.mRanker = ranker;
        }

        public int compare(StatusBarNotification o1, StatusBarNotification o2) {
            if (RecommendationsUtil.isCaptivePortal(this.mContext, o1) && !RecommendationsUtil.isCaptivePortal(this.mContext, o2)) {
                return -1;
            }
            if (RecommendationsUtil.isCaptivePortal(this.mContext, o1) || !RecommendationsUtil.isCaptivePortal(this.mContext, o2)) {
                return Double.compare(this.mRanker.getBaseNotificationScore(o1), this.mRanker.getBaseNotificationScore(o2));
            }
            return 1;
        }
    }

    private static class RecOperation {
        private final StatusBarNotification mNotification;
        private final int mOperation;

        public static RecOperation newAdd(StatusBarNotification notification) {
            return new RecOperation(notification, 0);
        }

        public static RecOperation newChange(StatusBarNotification notification) {
            return new RecOperation(notification, 1);
        }

        public static RecOperation newRemove(StatusBarNotification notification) {
            return new RecOperation(notification, 2);
        }

        private RecOperation(StatusBarNotification notification, int operation) {
            this.mNotification = notification;
            this.mOperation = operation;
        }

        public int getOperation() {
            return this.mOperation;
        }

        public StatusBarNotification getNotification() {
            return this.mNotification;
        }

        public int hashCode() {
            return Objects.hash(this.mNotification, Integer.valueOf(this.mOperation));
        }

        public boolean equals(Object other) {
            return other.getClass() == RecOperation.class && ((RecOperation) other).mOperation == this.mOperation && Objects.equals(((RecOperation) other).mNotification, this.mNotification);
        }

        public String toString() {
            String operation;
            if (this.mOperation == 0) {
                operation = "ADD";
            } else if (this.mOperation == 1) {
                operation = "CHANGE";
            } else if (this.mOperation == 2) {
                operation = "REMOVE";
            } else {
                operation = "?!";
            }
            return "RecOperation(" + operation + "): " + this.mNotification;
        }
    }

    public void setNotificationResolver(NotificationResolver notificationResolver) {
        this.mNotificationResolver = notificationResolver;
    }

    private RecommendationsManager(Context context, boolean unbundled, RankerParameters rankerParameters) {
        this.mTag = unbundled ? "UB-RecommendationsManager" : "B-RecommendationsManager";
        if (BuildConfig.DEBUG)
            Log.d(this.mTag, "RecommendationsManager(Context)");
        this.mContext = context;
        Resources res = context.getResources();
        this.mCardMaxWidth = res.getDimensionPixelOffset(R.dimen.notif_card_img_max_width);
        this.mCardMaxHeight = res.getDimensionPixelOffset(R.dimen.notif_card_img_height);
        this.mBannerMaxWidth = res.getDimensionPixelOffset(R.dimen.banner_width);
        this.mBannerMaxHeight = res.getDimensionPixelOffset(R.dimen.banner_height);
        this.mMaxRecsPerApp = res.getInteger(R.integer.max_recommendations_per_app);
        this.mDbHelper = DbHelper.getInstance(context);
        this.mRanker = new Ranker(context, this.mDbHelper, rankerParameters);
        this.mRanker.addListener(this);
        this.mAppListener = new ServiceAppListener(context, this.mRanker);
    }

    public static RecommendationsManager getInstance(Context context, boolean unbundled, RankerParametersFactory rankerParametersFactory) {
        if (sInstance == null) {
            sInstance = new RecommendationsManager(context.getApplicationContext(), unbundled, rankerParametersFactory.create(context));
        }
        return sInstance;
    }

    public void onCreate() {
        this.mAppListener.onCreate();
    }

    public void onDestroy() {
        this.mAppListener.onDestroy();
    }

    boolean isConnectedToNotificationService() {
        return this.mConnectedToNotificationService;
    }

    private void startIfReady(RemoteCallbackList<IRecommendationsClient> clients, RemoteCallbackList<IRecommendationsClient> partnerClients) {
        //if (BuildConfig.DEBUG)
        //    Log.d(this.mTag, "startIfReady:\n\tmRankerReady=" + this.mRankerReady + "\n\tmConnectedToNotificationService=" + this.mConnectedToNotificationService + "\n\tmClients.getRegisteredCallbackCount()=" + this.mClientHandler.getRegisteredClientCount() + "\n\tmStarted=" + this.mStarted);
        if (this.mRankerReady && this.mConnectedToNotificationService && this.mClientHandler.getRegisteredClientCount() != 0) {
            this.mStarted = true;
            this.mPackageToRecSet.clear();
            this.mPartnerList.clear();
            notifyServiceStatusChange(clients, partnerClients, true);
            this.mNotificationResolver.fetchExistingNotifications();
        }
    }

    private void setConnectedToNotificationService(RemoteCallbackList<IRecommendationsClient> clients, RemoteCallbackList<IRecommendationsClient> partnerClients, boolean connected) {
        //if (BuildConfig.DEBUG)
        //    Log.d(this.mTag, "setConnectedToNotificationService: state=" + connected);
        this.mConnectedToNotificationService = connected;
        if (connected) {
            startIfReady(clients, partnerClients);
        } else if (this.mStarted) {
            clearAllAppRecommendations(clients, 3);
            clearAllPartnerRecommendations(partnerClients, 3);
            notifyServiceStatusChange(clients, partnerClients, false);
            this.mStarted = false;
        }
    }

    public void onRankerReady() {
        this.mRankerReady = true;
        this.mClientHandler.enqueueStartIfReady();
    }

    public void onActionOpenLaunchPoint(String key, String group) {
        this.mRanker.onActionOpenLaunchPoint(key, group);
    }

    public void onActionOpenRecommendation(String key, String group) {
        this.mRanker.onActionOpenRecommendation(key, group);
    }

    public void onActionRecommendationImpression(String key, String group) {
        this.mRanker.onActionRecommendationImpression(key, group);
    }

    public void registerNotificationsClient(IRecommendationsClient client, boolean isPartnerClient) {
        this.mClientHandler.registerNotificationsClient(client, isPartnerClient);
        this.mClientHandler.enqueueStartIfReady();
    }

    public void unregisterNotificationsClient(IRecommendationsClient client, boolean isPartnerClient) throws RemoteException {
        this.mClientHandler.unregisterNotificationsClient(client, isPartnerClient);
    }

    private void notifyServiceStatusChange(RemoteCallbackList<IRecommendationsClient> clients, RemoteCallbackList<IRecommendationsClient> partnerClients, boolean isReady) {
        try {
            int i;
            int count = clients.beginBroadcast();
            for (i = 0; i < count; i++) {
                clients.getBroadcastItem(i).onServiceStatusChanged(isReady);
            }
            clients.finishBroadcast();
            count = partnerClients.beginBroadcast();
            for (i = 0; i < count; i++) {
                partnerClients.getBroadcastItem(i).onServiceStatusChanged(isReady);
            }
            partnerClients.finishBroadcast();
        } catch (RemoteException e) {
            Log.e(this.mTag, "RemoteException", e);
        } catch (Throwable th) {
            clients.finishBroadcast();
        }
    }

    private void removeRemoteViewsIfPresent(StatusBarNotification sbn) {
        Notification notif = sbn.getNotification();
        if (notif != null) {
            notif.contentView = null;
            notif.bigContentView = null;
        }
    }

    private void onRecommendationsReset(RemoteCallbackList<IRecommendationsClient> clients, RemoteCallbackList<IRecommendationsClient> partnerClients, List<RecOperation> notifications) {
        //if (BuildConfig.DEBUG)
        //    Log.d(this.mTag, "onRecommendationsReset:\n\tmStarted=" + this.mStarted + "\n\tnotifications.size()=" + notifications.size() + "\n\tnotifications:");
        //if (BuildConfig.DEBUG) for (RecOperation operation : notifications) {
        //    Log.d(this.mTag, "\n\t" + operation.getNotification());
        //}
        if (this.mStarted) {
            int reasonForClearing;
            int totalRecommendations = 0;
            int blacklistedRecommendations = 0;
            for (RecOperation operation2 : notifications) {
                StatusBarNotification sbn = operation2.getNotification();
                if (!RecommendationsUtil.isInPartnerRow(this.mContext, sbn)) {
                    if (operation2.getOperation() == 0) {
                        totalRecommendations++;
                        if (this.mRanker.isBlacklisted(sbn.getPackageName())) {
                            blacklistedRecommendations++;
                        }
                    } else if (operation2.getOperation() == 2) {
                        totalRecommendations--;
                        if (this.mRanker.isBlacklisted(sbn.getPackageName())) {
                            blacklistedRecommendations--;
                        }
                    }
                }
            }
            //if (BuildConfig.DEBUG)
            //    Log.d(this.mTag, "\ttotalRecommendations=" + totalRecommendations + ", blacklistedRecommendations=" + blacklistedRecommendations + ", notifications.size()=" + notifications.size() + ", mRanker.hasBlacklistedPackages()=" + this.mRanker.hasBlacklistedPackages());
            if (totalRecommendations == 0) {
                if (this.mRanker.hasBlacklistedPackages()) {
                    reasonForClearing = 4; // CLEAR_RECOMMENDATIONS_PENDING_DISABLED
                } else {
                    reasonForClearing = 3; // CLEAR_RECOMMENDATIONS_PENDING
                }
            } else if (totalRecommendations == blacklistedRecommendations) {
                reasonForClearing = 2; // CLEAR_RECOMMENDATIONS_DISABLED
            } else {
                reasonForClearing = 3;
            }
            clearAllAppRecommendations(clients, reasonForClearing);
            clearAllPartnerRecommendations(partnerClients, 3);
        }
    }

    private void clearAllAppRecommendations(RemoteCallbackList<IRecommendationsClient> clients, int reasonForClearing) {
        //if (BuildConfig.DEBUG)
        //    Log.d(this.mTag, "clearAllAppRecommendations:\n\treasonForClearing=" + RecommendationsClient.clearReasonToString(reasonForClearing));
        int count = clients.beginBroadcast();
        for (int i = 0; i < count; i++) {
            try {
                clients.getBroadcastItem(i).onClearRecommendations(reasonForClearing);
            } catch (RemoteException e) {
                Log.e(this.mTag, "RemoteException", e);
            } catch (Throwable th) {
                clients.finishBroadcast();
            }
        }
        clients.finishBroadcast();
    }

    private void clearAllPartnerRecommendations(RemoteCallbackList<IRecommendationsClient> partnerClients, int reasonForClearing) {
        //if (BuildConfig.DEBUG)
        //    Log.d(this.mTag, "clearAllPartnerRecommendations:\n\treasonForClearing=" + RecommendationsClient.clearReasonToString(reasonForClearing));
        int count = partnerClients.beginBroadcast();
        for (int i = 0; i < count; i++) {
            try {
                partnerClients.getBroadcastItem(i).onClearRecommendations(reasonForClearing);
            } catch (RemoteException e) {
                Log.e(this.mTag, "RemoteException", e);
            } catch (Throwable th) {
                partnerClients.finishBroadcast();
            }
        }
        partnerClients.finishBroadcast();
    }

    private void logRankedAction() {
        HashMap<StatusBarNotification, Double> rawScoreMap = new HashMap();
        HashMap<StatusBarNotification, Double> scoreMap = new HashMap();
        ArrayList<StatusBarNotification> notifications = new ArrayList();
        for (ArrayList<StatusBarNotification> recSet : this.mPackageToRecSet.values()) {
            Iterator it = recSet.iterator();
            while (it.hasNext()) {
                StatusBarNotification sbn = (StatusBarNotification) it.next();
                notifications.add(sbn);
                double rawScore = this.mRanker.getBaseNotificationScore(sbn);
                double score = this.mRanker.getCachedNotificationScore(sbn);
                rawScoreMap.put(sbn, Double.valueOf(rawScore));
                scoreMap.put(sbn, Double.valueOf(score));
            }
        }
        // LoggingUtils.logRecommendationRankEvent(notifications, rawScoreMap, scoreMap, this.mContext);
    }

    private void postRecommendationChangesToClients(RemoteCallbackList<IRecommendationsClient> clients, List<RecOperation> changed) {
        if (BuildConfig.DEBUG)
            Log.d(this.mTag, "postRecommendationChangesToClients");
        if (!changed.isEmpty()) {
            int count = clients.beginBroadcast();
            try {
                for (int i = 0; i < count; i++) {
                    try {
                        IRecommendationsClient broadcastItem = clients.getBroadcastItem(i);
                        for (RecOperation operation : changed) {
                            TvRecommendation recommendation = RecommendationsUtil.fromStatusBarNotification(mContext, operation.getNotification());
                            switch (operation.getOperation()) {
                                case 0:
                                    broadcastItem.onAddRecommendation(recommendation);
                                    break;
                                case 1:
                                    broadcastItem.onUpdateRecommendation(recommendation);
                                    break;
                                case 2:
                                    broadcastItem.onRemoveRecommendation(recommendation);
                                    break;
                                default:
                                    break;
                            }
                        }
                    } catch (RemoteException e) {
                        Log.e(this.mTag, "RemoteException", e);
                    }
                }
            } finally {
                clients.finishBroadcast();
            }
        }
    }

    private void recommendationBatchPostedInt(RemoteCallbackList<IRecommendationsClient> clients, RemoteCallbackList<IRecommendationsClient> partnerClients, List<RecOperation> postedBatch) {
        //if (BuildConfig.DEBUG)
        //    Log.d(this.mTag, "recommendationBatchPostedInt:\n\tpostedBatch=" + postedBatch + "\n\tmStarted=" + this.mStarted);
        if (this.mStarted) {
            List<RecOperation> changes = new ArrayList(postedBatch.size());
            for (RecOperation operation : postedBatch) {
                StatusBarNotification sbn = operation.getNotification();
                boolean inPartnerRow = RecommendationsUtil.isInPartnerRow(this.mContext, sbn);
                if (operation.getOperation() == 0) {
                    if (inPartnerRow) {
                        handlePartnerRecommendationAdded(partnerClients, sbn);
                    } else {
                        scoreAndInsertRecommendationAdd(sbn, changes);
                    }
                } else if (operation.getOperation() == 2) {
                    if (inPartnerRow) {
                        handlePartnerRecommendationRemoved(partnerClients, sbn);
                    } else {
                        ArrayList<StatusBarNotification> recSet = this.mPackageToRecSet.get(sbn.getPackageName());
                        if (recSet != null) {
                            for (int i = 0; i < recSet.size(); i++) {
                                if (RecommendationsUtil.equals(recSet.get(i), sbn)) {
                                    recSet.remove(i);
                                    break;
                                }
                            }
                        }
                        changes.add(RecOperation.newRemove(sbn));
                    }
                }
            }
            postRecommendationChangesToClients(clients, changes);
            logRankedAction();
        }
    }

    private void tidyRecommendation(StatusBarNotification sbn) {
        removeRemoteViewsIfPresent(sbn);
        processRecommendationImage(sbn);
    }

    private void scoreAndInsertRecommendationAdd(StatusBarNotification sbn, List<RecOperation> changes) {
        this.mRanker.prepNormalizationValues();
        String pkg = sbn.getPackageName();
        this.mRanker.markPostedRecommendations(pkg);
        if (!this.mRanker.isBlacklisted(pkg)) {
            ArrayList<StatusBarNotification> recSet = this.mPackageToRecSet.get(pkg);
            if (recSet == null) {
                tidyRecommendation(sbn);
                recSet = new ArrayList<>();
                recSet.add(sbn);
                this.mPackageToRecSet.put(pkg, recSet);
                this.mRanker.calculateAdjustedScore(sbn, 0, RecommendationsUtil.isCaptivePortal(this.mContext, sbn));
                changes.add(RecOperation.newAdd(sbn));
                return;
            }
            StatusBarNotification rec;
            NotificationComparator comparator = new NotificationComparator(this.mContext, this.mRanker);
            int wantPosition = -1;
            boolean found = false;
            int position = 0;
            while (position < recSet.size()) {
                rec = recSet.get(position);
                if (comparator.compare(rec, sbn) < 0 && wantPosition == -1) {
                    wantPosition = position;
                }
                if (RecommendationsUtil.equals(rec, sbn)) {
                    found = true;
                    copyLargeIconAndSize(rec, sbn);
                    break;
                }
                position++;
            }
            if (found) {
                recSet.remove(position);
                if (wantPosition == -1) {
                    wantPosition = recSet.size();
                    while (position < wantPosition) {
                        if (comparator.compare(recSet.get(position), sbn) < 0) {
                            wantPosition = position;
                            break;
                        }
                        position++;
                    }
                }
                recSet.add(wantPosition, sbn);
                if (wantPosition < position) {
                    wantPosition = position;
                }
            } else if (this.mMaxRecsPerApp <= 0 || recSet.size() < this.mMaxRecsPerApp) {
                tidyRecommendation(sbn);
                if (wantPosition == -1) {
                    wantPosition = recSet.size();
                }
                this.mRanker.calculateAdjustedScore(sbn, wantPosition, RecommendationsUtil.isCaptivePortal(this.mContext, sbn));
                recSet.add(wantPosition, sbn);
                changes.add(RecOperation.newAdd(sbn));
                wantPosition++;
            } else {
                this.mNotificationResolver.cancelRecommendation(sbn.getKey());
                return;
            }
            boolean haveAdjustedScore = false;
            double adjustedScore = 0.0d;
            int recPosition = wantPosition;
            while (wantPosition < recSet.size()) {
                rec = recSet.get(wantPosition);
                this.mRanker.calculateAdjustedScore(rec, wantPosition, RecommendationsUtil.isCaptivePortal(this.mContext, rec));
                if (!haveAdjustedScore) {
                    haveAdjustedScore = true;
                    adjustedScore = this.mRanker.getCachedNotificationScore(rec);
                }
                changes.add(RecOperation.newChange(sbn));
                wantPosition++;
            }
            if (haveAdjustedScore) {
                //  LoggingUtils.logRecommendationInsertAction(sbn, this.mRanker.getBaseNotificationScore(sbn), adjustedScore, recPosition, this.mContext);
            }
        }
    }

    private void handlePartnerRecommendationAdded(RemoteCallbackList<IRecommendationsClient> partnerClients, StatusBarNotification sbn) {
        int i;
        int count;
        TvRecommendation recommendation;
        boolean isValidSbn = true;
        boolean found = false;
        for (i = 0; i < this.mPartnerList.size(); i++) {
            if (RecommendationsUtil.equals(this.mPartnerList.get(i), sbn)) {
                found = true;
            }
            if (sbn != null && sbn.getNotification().largeIcon == null) {
                sbn = this.mNotificationResolver.getNotification(sbn.getKey());

                if (!found) {
                    this.mPartnerList.add(sbn);
                }
                tidyRecommendation(sbn);
                count = partnerClients.beginBroadcast();
                while (i < count) {
                    try {
                        recommendation = RecommendationsUtil.fromStatusBarNotification(mContext, sbn);
                        if (found) {
                            partnerClients.getBroadcastItem(i).onUpdateRecommendation(recommendation);
                        } else
                            partnerClients.getBroadcastItem(i).onAddRecommendation(recommendation);
                    } catch (RemoteException e) {
                        Log.e(this.mTag, "RemoteException", e);
                        return;
                    } finally {
                        partnerClients.finishBroadcast();
                    }
                }
                partnerClients.finishBroadcast();
            }


            this.mPartnerList.set(i, sbn);
            if (!isValidSbn) {
                if (found) {
                    this.mPartnerList.add(sbn);
                }
                tidyRecommendation(sbn);
                count = partnerClients.beginBroadcast();
                for (i = 0; i < count; i++) {
                    recommendation = RecommendationsUtil.fromStatusBarNotification(mContext, sbn);
                    try {
                        if (found) {
                            partnerClients.getBroadcastItem(i).onUpdateRecommendation(recommendation);
                        } else {
                            partnerClients.getBroadcastItem(i).onAddRecommendation(recommendation);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                partnerClients.finishBroadcast();
            }
        }

        if (!isValidSbn) {
            if (found) {
                this.mPartnerList.add(sbn);
            }
            tidyRecommendation(sbn);
            count = partnerClients.beginBroadcast();
            for (i = 0; i < count; i++) {
                recommendation = RecommendationsUtil.fromStatusBarNotification(mContext, sbn);
                try {
                    if (found) {
                        partnerClients.getBroadcastItem(i).onAddRecommendation(recommendation);
                    } else {
                        partnerClients.getBroadcastItem(i).onUpdateRecommendation(recommendation);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            partnerClients.finishBroadcast();
        }
    }

    private void handlePartnerRecommendationRemoved(RemoteCallbackList<IRecommendationsClient> partnerClients, StatusBarNotification sbn) {
        int i;
        TvRecommendation recommendation = RecommendationsUtil.fromStatusBarNotification(mContext, sbn);
        for (i = 0; i < this.mPartnerList.size(); i++) {
            if (RecommendationsUtil.equals(this.mPartnerList.get(i), sbn)) {
                this.mPartnerList.remove(i);
                break;
            }
        }
        int count = partnerClients.beginBroadcast();
        i = 0;
        while (i < count) {
            try {
                partnerClients.getBroadcastItem(i).onRemoveRecommendation(recommendation);
                i++;
            } catch (RemoteException e) {
                Log.e(this.mTag, "RemoteException", e);
                return;
            } finally {
                partnerClients.finishBroadcast();
            }
        }
        partnerClients.finishBroadcast();
    }

    private void copyLargeIconAndSize(StatusBarNotification from, StatusBarNotification to) {
        Notification toNotification = to.getNotification();
        if (toNotification.largeIcon == null) {
            Notification fromNotification = from.getNotification();
            if (fromNotification.largeIcon != null) {
                toNotification.largeIcon = fromNotification.largeIcon;
            }
        }
        Bundle toExtras = toNotification.extras;
        if (toExtras != null) {
            int toWidth = toExtras.getInt("notif_img_width", -1);
            int toHeight = toExtras.getInt("notif_img_height", -1);
            if (toWidth <= 0 || toHeight <= 0) {
                Bundle fromExtras = from.getNotification().extras;
                int fromWidth = -1;
                int fromHeight = -1;
                if (fromExtras != null) {
                    fromWidth = fromExtras.getInt("notif_img_width", -1);
                    fromHeight = fromExtras.getInt("notif_img_height", -1);
                }
                if (fromWidth > 0 && toHeight > 0) {
                    toExtras.putInt("notif_img_width", fromWidth);
                    toExtras.putInt("notif_img_height", fromHeight);
                }
            }
        }
    }

    public Bitmap getRecomendationImage(String key) {
        if (this.mNotificationResolver == null) {
            return null;
        }
        // Log.d(this.mTag, "+++ getRecomendationImage for: " + key);
        StatusBarNotification sbn = this.mNotificationResolver.getNotification(key);
        if (sbn != null) {
            return getResizedRecommendationBitmap(sbn.getNotification().largeIcon, RecommendationsUtil.isInPartnerRow(this.mContext, sbn), false);
        }
        return null;
    }

    private void processRecommendationImage(StatusBarNotification sbn) {
        if (RecommendationsUtil.isRecommendation(sbn)) {
            // Log.d(this.mTag, "+++ processRecommendationImage for: " + sbn);
            Bitmap img;
            Notification notif = sbn.getNotification();
            boolean partner = RecommendationsUtil.isInPartnerRow(this.mContext, sbn);
            if (notif.largeIcon != null) {
                img = notif.largeIcon;
            } else {
                img = getRecomendationImage(sbn.getKey());
            }
            // notif.extras.remove("android.largeIcon");
            if (img == null) {
                return;
            }
            if (partner) {
                notif.largeIcon = getResizedRecommendationBitmap(img, true, false);
                return;
            }
            Point dim = getResizedCardDimensions(img.getWidth(), img.getHeight());
            if (dim != null) {
                notif.extras.putInt("notif_img_width", dim.x);
                notif.extras.putInt("notif_img_height", dim.y);
            }
            notif.largeIcon = getResizedRecommendationBitmap(img, false, false); // why lowres? (true)
        }
    }

    private Bitmap getResizedRecommendationBitmap(Bitmap image, boolean isBanner, boolean lowRes) {
        int maxWidth;
        int maxHeight;
        float f = 1.0f;
        if (isBanner) {
            maxWidth = this.mBannerMaxWidth;
        } else {
            maxWidth = (int) ((!lowRes ? 1.0f : 0.1f) * ((float) this.mCardMaxWidth));
        }
        if (isBanner) {
            maxHeight = this.mBannerMaxHeight;
        } else {
            float f2 = (float) this.mCardMaxHeight;
            if (lowRes) {
                f = 0.1f;
            }
            maxHeight = (int) (f2 * f);
        }
        return RecommendationsUtil.getSizeCappedBitmap(image, maxWidth, maxHeight);
    }

    private Point getResizedCardDimensions(int imgWidth, int imgHeight) {
        if (imgWidth <= this.mCardMaxWidth && imgHeight <= this.mCardMaxHeight) {
            return new Point(imgWidth, imgHeight);
        }
        if (imgWidth > 0 && imgHeight > 0) {
            float scale = Math.min(1.0f, ((float) this.mCardMaxHeight) / ((float) imgHeight));
            if (((double) scale) < 1.0d || imgWidth > this.mCardMaxWidth) {
                return new Point((int) Math.min(((float) imgWidth) * scale, (float) this.mCardMaxWidth), (int) (((float) imgHeight) * scale));
            }
        }
        return new Point(imgWidth, imgHeight);
    }

    public void cancelRecommendation(String key) {
        if (this.mNotificationResolver != null) {
            this.mNotificationResolver.cancelRecommendation(key);
        }
    }

    public List<String> getRecommendationsPackages() {
        HashSet<String> installedPackages = new HashSet();
        List<ApplicationInfo> apps = this.mContext.getPackageManager().getInstalledApplications(0);
        for (int i = 0; i < apps.size(); i++) {
            ApplicationInfo appInfo = apps.get(i);
            if (appInfo.enabled && !appInfo.packageName.equals("android")) {
                installedPackages.add(appInfo.packageName);
            }
        }
        List<String> installedRecommendationPackages = new ArrayList<>(this.mDbHelper.loadRecommendationsPackages());
        installedRecommendationPackages.retainAll(installedPackages);
        return installedRecommendationPackages;
    }

    public List<String> getBlacklistedPackages() {
        return this.mDbHelper.loadBlacklistedPackages();
    }

    public void setBlacklistedPackages(String[] blacklistedPackages) {
        this.mDbHelper.saveBlacklistedPackages(blacklistedPackages);
        this.mRanker.reload();
    }

    public void sendConnectionStatus(boolean connected) {
        this.mClientHandler.enqueueConnectionStatus(connected);
    }

    public void addNotification(StatusBarNotification sbn) {
        this.mClientHandler.enqueueNotificationPosted(sbn);
    }

    public void removeNotification(StatusBarNotification sbn) {
        this.mClientHandler.enqueueNotificationRemoved(sbn);
    }

    public void resetNotifications() {
        this.mClientHandler.enqueueNotificationReset();
    }

    public void addCaptivePortalNotification(StatusBarNotification sbn) {
        this.mClientHandler.enqueueCaptivePortalPosted(sbn);
    }

    public void removeAllCaptivePortalNotifications() {
        this.mClientHandler.enqueueAllCaptivePortalRemoved();
    }
}
