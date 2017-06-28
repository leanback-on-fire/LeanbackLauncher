package com.rockchips.android.leanbacklauncher.tvrecommendations.service;

import android.app.Notification;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.rockchips.android.leanbacklauncher.R;
import com.rockchips.android.leanbacklauncher.tvrecommendations.IRecommendationsClient;
import com.rockchips.android.leanbacklauncher.tvrecommendations.TvRecommendation;
import com.rockchips.android.leanbacklauncher.tvrecommendations.service.Ranker.RankingListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

class RecommendationsManager implements RankingListener {
    private static RecommendationsManager mInstance;
    private ServiceAppListener mAppListener;
    private int mBannerMaxHeight;
    private int mBannerMaxWidth;
    private int mCardMaxHeight;
    private int mCardMaxWidth;
    private final RemoteCallbackList<IRecommendationsClient> mClients;
    private boolean mConnectedToNotificationService;
    private final Context mContext;
    private DbHelper mDbHelper;
    private int mMaxRecsPerApp;
    private NotificationResolver mNotificationResolver;
    private final HashMap<String, ArrayList<StatusBarNotification>> mPackageToRecSet;
    private final RemoteCallbackList<IRecommendationsClient> mPartnerClients;
    private final ArrayList<StatusBarNotification> mPartnerList;
    private Ranker mRanker;
    private boolean mRankerReady;
    private boolean mStarted;
    private final String mTag;
    private final List<StatusBarNotification> mToClientAdded;
    private final List<StatusBarNotification> mToClientChanged;
    private final List<StatusBarNotification> mToClientRemoved;

    public interface NotificationResolver {
        void cancelRecommendation(String str);

        void fetchExistingNotifications();

        StatusBarNotification getNotification(String str);
    }

    public void setNotificationResolver(NotificationResolver notificationResolver) {
        this.mNotificationResolver = notificationResolver;
    }

    private RecommendationsManager(Context context, boolean unbundled) {
        this.mPartnerClients = new RemoteCallbackList();
        this.mClients = new RemoteCallbackList();
        this.mPartnerList = new ArrayList();
        this.mConnectedToNotificationService = false;
        this.mRankerReady = false;
        this.mToClientAdded = new ArrayList();
        this.mToClientChanged = new ArrayList();
        this.mToClientRemoved = new ArrayList();
        this.mPackageToRecSet = new HashMap();
        this.mTag = unbundled ? "RecommendationsManager" : "RecommendationsManagerB";
        this.mContext = context;
        Resources res = context.getResources();
        this.mCardMaxWidth = res.getDimensionPixelOffset(R.dimen.notif_card_img_max_width);
        this.mCardMaxHeight = res.getDimensionPixelOffset(R.dimen.notif_card_img_height);
        this.mBannerMaxWidth = res.getDimensionPixelOffset(R.dimen.banner_width);
        this.mBannerMaxHeight = res.getDimensionPixelOffset(R.dimen.banner_height);
        this.mMaxRecsPerApp = res.getInteger(R.integer.max_recommendations_per_app);
        this.mDbHelper = DbHelper.getInstance(context);
        this.mRanker = new Ranker(context, this.mDbHelper);
        this.mRanker.addListener(this);
        this.mAppListener = new ServiceAppListener(context, this.mRanker);
    }

    static {
        mInstance = null;
    }

    public static RecommendationsManager getInstance(Context context, boolean unbundled) {
        if (mInstance == null) {
            mInstance = new RecommendationsManager(context.getApplicationContext(), unbundled);
        }
        return mInstance;
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

    private final void startIfReady() {
        if (this.mRankerReady && this.mConnectedToNotificationService && this.mClients.getRegisteredCallbackCount() != 0) {
            this.mStarted = true;
            this.mPackageToRecSet.clear();
            this.mPartnerList.clear();
            notifyServiceStatusChange(true);
            this.mNotificationResolver.fetchExistingNotifications();
        }
    }

    void setConnectedToNotificationService(boolean state) {
        Log.d("NVID", "RecommendationsManager$setConnectedToNotificationService  " + state);
        this.mConnectedToNotificationService = state;
        if (state) {
            startIfReady();
        } else if (this.mStarted) {
            clearAllRecommendations(3);
            notifyServiceStatusChange(false);
            this.mStarted = false;
        }
    }

    public void onRankerReady() {
        this.mRankerReady = true;
        startIfReady();
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
        if (isPartnerClient) {
            this.mPartnerClients.register(client);
        } else {
            this.mClients.register(client);
        }
        startIfReady();
    }

    public void unregisterNotificationsClient(IRecommendationsClient client, boolean isPartnerClient) throws RemoteException {
        if (client == null) {
            return;
        }
        if (isPartnerClient) {
            this.mPartnerClients.unregister(client);
        } else {
            this.mClients.unregister(client);
        }
    }

    private void notifyServiceStatusChange(boolean isReady) {
        try {
            int i;
            int count = this.mClients.beginBroadcast();
            for (i = 0; i < count; i++) {
                ((IRecommendationsClient) this.mClients.getBroadcastItem(i)).onServiceStatusChanged(isReady);
            }
            this.mClients.finishBroadcast();
            count = this.mPartnerClients.beginBroadcast();
            for (i = 0; i < count; i++) {
                ((IRecommendationsClient) this.mPartnerClients.getBroadcastItem(i)).onServiceStatusChanged(isReady);
            }
            this.mPartnerClients.finishBroadcast();
        } catch (RemoteException e) {
            Log.e(this.mTag, "RemoteException", e);
        } catch (Throwable th) {
            this.mClients.finishBroadcast();
        }
    }

    private void removeRemoteViewsIfPresent(StatusBarNotification sbn) {
        Notification notif = sbn.getNotification();
        if (notif != null) {
            notif.contentView = null;
            notif.bigContentView = null;
        }
    }

    void onRecommendationsReset(List<StatusBarNotification> postedBatch) {
        Log.d("NVID", "RecommendationsManager$onRecommendationsReset");
        if (this.mStarted) {
            int reasonForClearing;
            if (postedBatch.isEmpty()) {
                reasonForClearing = 3;
            } else {
                int blacklistedCount = 0;
                for (StatusBarNotification sbn : postedBatch) {
                    if (this.mRanker.isBlacklisted(sbn.getPackageName())) {
                        blacklistedCount++;
                    }
                }
                Log.d("NVID", "  blacklistedCount  " + blacklistedCount);
                Log.d("NVID", "  postedBatch.size()  " + postedBatch.size());
                if (blacklistedCount == postedBatch.size()) {
                    reasonForClearing = 2;
                } else {
                    reasonForClearing = 3;
                }
            }
            clearAllRecommendations(reasonForClearing);
        }
    }

    private void clearAllRecommendations(int reasonForClearing) {
        int i;
        Log.d("NVID", "RecommendationsManager$clearAllRecommendations  " + reasonForClearing);
        int count = this.mClients.beginBroadcast();
        for (i = 0; i < count; i++) {
            try {
                ((IRecommendationsClient) this.mClients.getBroadcastItem(i)).onClearRecommendations(reasonForClearing);
            } catch (RemoteException e) {
                Log.e(this.mTag, "RemoteException", e);
            }
        }
        this.mClients.finishBroadcast();
        count = this.mPartnerClients.beginBroadcast();
        for (i = 0; i < count; i++) {
            try {
                ((IRecommendationsClient) this.mPartnerClients.getBroadcastItem(i)).onClearRecommendations(reasonForClearing);
            } catch (RemoteException e2) {
                Log.e(this.mTag, "RemoteException", e2);
            }
        }
        this.mPartnerClients.finishBroadcast();
    }

    void logRankedAction() {
        HashMap<StatusBarNotification, Double> rawScoreMap = new HashMap();
        HashMap<StatusBarNotification, Double> scoreMap = new HashMap();
        ArrayList<StatusBarNotification> notifications = new ArrayList();
        for (ArrayList<StatusBarNotification> recSet : this.mPackageToRecSet.values()) {
            for (StatusBarNotification sbn : recSet) {
                notifications.add(sbn);
                double rawScore = this.mRanker.getBaseNotificationScore(sbn);
                double score = this.mRanker.getCachedNotificationScore(sbn);
                rawScoreMap.put(sbn, Double.valueOf(rawScore));
                scoreMap.put(sbn, Double.valueOf(score));
            }
        }
        LoggingUtils.logRecommendationRankEvent(notifications, rawScoreMap, scoreMap, this.mContext);
    }

    private void postRecommendationRowUpdatesToClients() {
        int count;
        int i;
        if (!this.mToClientAdded.isEmpty()) {
            count = this.mClients.beginBroadcast();
            for (i = 0; i < count; i++) {
                try {
                    for (StatusBarNotification sbn : this.mToClientAdded) {
                        ((IRecommendationsClient) this.mClients.getBroadcastItem(i)).onAddRecommendation(RecommendationsUtil.fromStatusBarNotification(sbn));
                    }
                } catch (RemoteException e) {
                    Log.e(this.mTag, "RemoteException", e);
                }
            }
            this.mClients.finishBroadcast();
            this.mToClientAdded.clear();
        }
        if (!this.mToClientChanged.isEmpty()) {
            count = this.mClients.beginBroadcast();
            for (i = 0; i < count; i++) {
                try {
                    for (StatusBarNotification sbn2 : this.mToClientChanged) {
                        ((IRecommendationsClient) this.mClients.getBroadcastItem(i)).onUpdateRecommendation(RecommendationsUtil.fromStatusBarNotification(sbn2));
                    }
                } catch (RemoteException e2) {
                    Log.e(this.mTag, "RemoteException", e2);
                }
            }
            this.mClients.finishBroadcast();
            this.mToClientChanged.clear();
        }
        if (!this.mToClientRemoved.isEmpty()) {
            count = this.mClients.beginBroadcast();
            for (i = 0; i < count; i++) {
                try {
                    for (StatusBarNotification sbn22 : this.mToClientRemoved) {
                        ((IRecommendationsClient) this.mClients.getBroadcastItem(i)).onRemoveRecommendation(RecommendationsUtil.fromStatusBarNotification(sbn22));
                    }
                } catch (RemoteException e22) {
                    Log.e(this.mTag, "RemoteException", e22);
                }
            }
            this.mClients.finishBroadcast();
            this.mToClientRemoved.clear();
        }
        logRankedAction();
    }

    public final void onRecommendationBatchPosted(List<StatusBarNotification> postedBatch) {
        if (this.mStarted) {
            for (StatusBarNotification sbn : postedBatch) {
                StatusBarNotification sbn2 = null;
                int i;
                boolean needToProcessRecommendation = false;
                boolean postToPartnerClients = false;
                boolean found = false;
                if (RecommendationsUtil.isInPartnerRow(this.mContext, sbn2)) {
                    i = 0;
                    while (i < this.mPartnerList.size()) {
                        if (RecommendationsUtil.equals((StatusBarNotification) this.mPartnerList.get(i), sbn2)) {
                            found = true;
                            if (sbn2.getNotification().largeIcon == null) {
                                sbn2 = this.mNotificationResolver.getNotification(sbn2.getKey());
                            }
                            this.mPartnerList.set(i, sbn2);
                            needToProcessRecommendation = true;
                            if (!found) {
                                this.mPartnerList.add(sbn2);
                                needToProcessRecommendation = true;
                            }
                            postToPartnerClients = true;
                        } else {
                            i++;
                        }
                    }
                    if (found) {
                        this.mPartnerList.add(sbn2);
                        needToProcessRecommendation = true;
                    }
                    postToPartnerClients = true;
                } else {
                    this.mRanker.prepNormalizationValues();
                    String pkg = sbn2.getPackageName();
                    this.mRanker.markPostedRecommendations(pkg);
                    if (this.mRanker.isBlacklisted(pkg)) {
                        continue;
                    } else {
                        ArrayList<StatusBarNotification> recSet = (ArrayList) this.mPackageToRecSet.get(pkg);
                        if (recSet == null) {
                            needToProcessRecommendation = true;
                            recSet = new ArrayList();
                            recSet.add(sbn2);
                            this.mPackageToRecSet.put(pkg, recSet);
                            this.mRanker.calculateAdjustedScore(sbn2, 0);
                            this.mToClientAdded.add(sbn2);
                        } else {
                            StatusBarNotification rec;
                            double sbnScore = this.mRanker.getBaseNotificationScore(sbn2);
                            int wantPosition = -1;
                            int position = 0;
                            while (position < recSet.size()) {
                                rec = (StatusBarNotification) recSet.get(position);
                                if (this.mRanker.getBaseNotificationScore(rec) < sbnScore && wantPosition == -1) {
                                    wantPosition = position;
                                }
                                if (RecommendationsUtil.equals(rec, sbn2)) {
                                    found = true;
                                    copyLargeIconAndSize(rec, sbn2);
                                    break;
                                }
                                position++;
                            }
                            if (found) {
                                recSet.remove(position);
                                if (wantPosition == -1) {
                                    wantPosition = recSet.size();
                                    while (position < wantPosition) {
                                        if (this.mRanker.getBaseNotificationScore((StatusBarNotification) recSet.get(position)) < sbnScore) {
                                            wantPosition = position;
                                            break;
                                        }
                                        position++;
                                    }
                                }
                                recSet.add(wantPosition, sbn2);
                                if (wantPosition < position) {
                                    wantPosition = position;
                                }
                            } else {
                                if (this.mMaxRecsPerApp > 0) {
                                    int size = recSet.size();
                                    int i2 = this.mMaxRecsPerApp;
                                    if (size >= i2) {
                                        this.mNotificationResolver.cancelRecommendation(sbn2.getKey());
                                    }
                                }
                                needToProcessRecommendation = true;
                                if (wantPosition == -1) {
                                    wantPosition = recSet.size();
                                }
                                this.mRanker.calculateAdjustedScore(sbn2, wantPosition);
                                recSet.add(wantPosition, sbn2);
                                this.mToClientAdded.add(sbn2);
                                wantPosition++;
                            }
                            boolean haveAdjustedScore = false;
                            double adjustedScore = 0.0d;
                            int recPosition = wantPosition;
                            while (wantPosition < recSet.size()) {
                                rec = (StatusBarNotification) recSet.get(wantPosition);
                                this.mRanker.calculateAdjustedScore(rec, wantPosition);
                                if (!haveAdjustedScore) {
                                    haveAdjustedScore = true;
                                    adjustedScore = this.mRanker.getCachedNotificationScore(rec);
                                }
                                if (!this.mToClientChanged.contains(rec)) {
                                    this.mToClientChanged.add(rec);
                                }
                                wantPosition++;
                            }
                            if (haveAdjustedScore) {
                                LoggingUtils.logRecommendationInsertAction(sbn2, sbnScore, adjustedScore, recPosition, this.mContext);
                            }
                        }
                    }
                }
                if (needToProcessRecommendation) {
                    removeRemoteViewsIfPresent(sbn2);
                    processRecommendationImage(sbn2);
                }
                if (postToPartnerClients) {
                    int count = this.mPartnerClients.beginBroadcast();
                    i = 0;
                    while (i < count) {
                        try {
                            TvRecommendation recommendation = RecommendationsUtil.fromStatusBarNotification(sbn2);
                            if (found) {
                                ((IRecommendationsClient) this.mPartnerClients.getBroadcastItem(i)).onUpdateRecommendation(recommendation);
                            } else {
                                ((IRecommendationsClient) this.mPartnerClients.getBroadcastItem(i)).onAddRecommendation(recommendation);
                            }
                            i++;
                        } catch (RemoteException e) {
                            Log.e(this.mTag, "RemoteException", e);
                            this.mPartnerClients.finishBroadcast();
                        } catch (Throwable th) {
                            this.mPartnerClients.finishBroadcast();
                        }
                    }
                    this.mPartnerClients.finishBroadcast();
                } else {
                    continue;
                }
            }
            postRecommendationRowUpdatesToClients();
        }
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

    public final void onRecommendationBatchRemoved(List<StatusBarNotification> removedBatch) {
        if (this.mStarted) {
            this.mToClientRemoved.addAll(removedBatch);
            for (StatusBarNotification sbn : removedBatch) {
                int i;
                if (RecommendationsUtil.isInPartnerRow(this.mContext, sbn)) {
                    TvRecommendation recommendation = RecommendationsUtil.fromStatusBarNotification(sbn);
                    for (i = 0; i < this.mPartnerList.size(); i++) {
                        if (RecommendationsUtil.equals((StatusBarNotification) this.mPartnerList.get(i), sbn)) {
                            this.mPartnerList.remove(i);
                            break;
                        }
                    }
                    int count = this.mPartnerClients.beginBroadcast();
                    i = 0;
                    while (i < count) {
                        try {
                            ((IRecommendationsClient) this.mPartnerClients.getBroadcastItem(i)).onRemoveRecommendation(recommendation);
                            i++;
                        } catch (RemoteException e) {
                            Log.e(this.mTag, "RemoteException", e);
                        } finally {
                            this.mPartnerClients.finishBroadcast();
                        }
                    }
                    this.mPartnerClients.finishBroadcast();
                } else {
                    ArrayList<StatusBarNotification> recSet = (ArrayList) this.mPackageToRecSet.get(sbn.getPackageName());
                    if (recSet != null) {
                        for (i = 0; i < recSet.size(); i++) {
                            if (RecommendationsUtil.equals((StatusBarNotification) recSet.get(i), sbn)) {
                                recSet.remove(i);
                                break;
                            }
                        }
                    }
                    this.mToClientRemoved.add(sbn);
                }
            }
            postRecommendationRowUpdatesToClients();
        }
    }

    public Bitmap getRecomendationImage(String key) {
        if (this.mNotificationResolver == null) {
            return null;
        }
        StatusBarNotification sbn = this.mNotificationResolver.getNotification(key);
        if (sbn == null) {
            return null;
        }
        return getResizedRecommendationBitmap(sbn.getNotification().largeIcon, RecommendationsUtil.isInPartnerRow(this.mContext, sbn), false);
    }

    protected void processRecommendationImage(StatusBarNotification sbn) {
        if (RecommendationsUtil.isRecommendation(sbn)) {
            Notification notif = sbn.getNotification();
            boolean partner = RecommendationsUtil.isInPartnerRow(this.mContext, sbn);
            Bitmap img = notif.largeIcon != null ? notif.largeIcon : getRecomendationImage(sbn.getKey());
            notif.extras.remove("android.largeIcon");
            if (img != null) {
                if (partner) {
                    notif.largeIcon = getResizedRecommendationBitmap(img, true, false);
                } else {
                    Point dim = getResizedCardDimensions(img.getWidth(), img.getHeight());
                    if (dim != null) {
                        notif.extras.putInt("notif_img_width", dim.x);
                        notif.extras.putInt("notif_img_height", dim.y);
                    }
                    notif.largeIcon = getResizedRecommendationBitmap(img, false, true);
                }
            }
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
            ApplicationInfo appInfo = (ApplicationInfo) apps.get(i);
            if (appInfo.enabled) {
                installedPackages.add(appInfo.packageName);
            }
        }
        List<String> installedRecommendationPackages = new ArrayList(this.mDbHelper.loadRecommendationsPackages());
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
}
