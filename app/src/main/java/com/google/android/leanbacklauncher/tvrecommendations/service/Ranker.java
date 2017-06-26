package com.google.android.leanbacklauncher.tvrecommendations.service;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.leanbacklauncher.R;
import com.google.android.leanbacklauncher.tvrecommendations.service.DbHelper.Listener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Ranker implements Listener {
    private static boolean DEBUG;
    private static String TAG;
    private static RankerParameters sRankerParameters;
    private AppUsageStatistics mAppUsageStatistics;
    private List<String> mBlacklistedPackages;
    private Queue<CachedAction> mCachedActions;
    private Context mContext;
    private Normalizer mCtrNormalizer;
    private DbHelper mDbHelper;
    private HashMap<String, Entity> mEntities;
    private final Object mEntitiesLock;
    private ArrayList<String> mLastNotficationRankingLogDump;
    private List<RankingListener> mListeners;
    private boolean mQueryingScores;

    private static class CachedAction {
        int action;
        String component;
        String group;
        String key;

        CachedAction(String k, String c, String g, int a) {
            this.key = k;
            this.component = c;
            this.group = g;
            this.action = a;
        }
    }

    public interface RankingListener {
        void onRankerReady();
    }

    static {
        TAG = "Ranker";
        DEBUG = false;
    }

    public Ranker(Context ctx, DbHelper dbHelper) {
        this.mEntitiesLock = new Object();
        this.mListeners = new ArrayList();
        this.mCachedActions = new LinkedList();
        this.mEntities = new HashMap();
        this.mBlacklistedPackages = new ArrayList();
        this.mCtrNormalizer = new Normalizer();
        this.mLastNotficationRankingLogDump = new ArrayList();
        this.mContext = ctx;
        this.mDbHelper = dbHelper;
        this.mQueryingScores = true;
        this.mDbHelper.getEntities(this);
        sRankerParameters = new RankerParameters(ctx);
        this.mAppUsageStatistics = new AppUsageStatistics(ctx);
    }

    public static final double getGroupStarterScore() {
        return (double) sRankerParameters.getGroupStarterScore();
    }

    public static final double getInstallBonus() {
        return (double) sRankerParameters.getInstallBonus();
    }

    public static final double getBonusFadePeriod() {
        return (double) (sRankerParameters.getBonusFadePeriodDays() * 8.64E7f);
    }

    public void addListener(RankingListener listener) {
        this.mListeners.add(listener);
    }

    public void reload() {
        this.mDbHelper.getEntities(this);
    }

    public boolean isBlacklisted(String packageName) {
        Log.d("NVID", "Ranker$isBlacklisted  " + packageName);
        return this.mBlacklistedPackages.contains(packageName);
    }

    public void onActionOpenLaunchPoint(String key, String group) {
        onAction(key, null, group, 1);
    }

    public void onActionOpenRecommendation(String key, String group) {
        onAction(key, null, group, 2);
    }

    public void onActionRecommendationImpression(String key, String group) {
        onAction(key, null, group, 4);
    }

    public void onActionPackageAdded(String packageName) {
        onAction(packageName, null, null, 0);
    }

    public void onActionPackageRemoved(String packageName) {
        onAction(packageName, null, null, 3);
    }

    public void onAction(String key, String component, String group, int actionType) {
        if (!TextUtils.isEmpty(key)) {
            synchronized (this.mCachedActions) {
                if (this.mQueryingScores) {
                    if (DEBUG) {
                        Log.d(TAG, "Scores not ready, caching this action");
                    }
                    this.mCachedActions.add(new CachedAction(key, component, group, actionType));
                    return;
                }
                if (DEBUG) {
                    Log.v(TAG, "action: " + actionType + " for " + key + " - group = " + group);
                }
                synchronized (this.mEntitiesLock) {
                    Entity entity = (Entity) this.mEntities.get(key);
                    if (actionType != 3) {
                        if (entity == null) {
                            entity = new Entity(this.mContext, this.mDbHelper, key);
                            this.mEntities.put(key, entity);
                        }
                        entity.onAction(actionType, component, group);
                        this.mDbHelper.saveEntity(entity);
                    } else if (entity != null) {
                        if (entity.getOrder(component) != 0) {
                            entity.onAction(actionType, component, null);
                            this.mDbHelper.removeEntity(key, false);
                        } else {
                            this.mEntities.remove(key);
                            this.mDbHelper.removeEntity(key, true);
                        }
                    }
                }
            }
        }
    }

    public void markPostedRecommendations(String packageName) {
        synchronized (this.mEntitiesLock) {
            Entity entity = (Entity) this.mEntities.get(packageName);
            if (!(entity == null || entity.hasPostedRecommendations())) {
                entity.markPostedRecommendations();
                this.mDbHelper.saveEntity(entity);
            }
        }
    }

    public void prepNormalizationValues() {
        this.mCtrNormalizer.reset();
        for (Entity e : this.mEntities.values()) {
            e.addNormalizeableValues(this.mCtrNormalizer);
        }
    }

    public final void calculateAdjustedScore(StatusBarNotification sbn, int position) {
        cacheScore(sbn, getBaseNotificationScore(sbn) * Math.pow((double) (position + 1), (double) (-sRankerParameters.getSpreadFactor())));
    }

    private double getRawScore(Notification notification) {
        try {
            return Math.max(0.0d, Math.min(1.0d, Double.parseDouble(notification.getSortKey())));
        } catch (Exception e) {
            return ((double) (notification.priority + 2)) / 4.0d;
        }
    }

    public double getBaseNotificationScore(StatusBarNotification sbn) {
        Bundle extras = sbn.getNotification().extras;
        if (extras != null) {
            if (extras.containsKey("cached_base_score")) {
                return extras.getDouble("cached_base_score");
            }
        }
        double value = -100.0d;
        String packageName = sbn.getPackageName();
        Notification notif = sbn.getNotification();
        if (!(notif == null || TextUtils.isEmpty(packageName))) {
            Entity entity;
            synchronized (this.mEntitiesLock) {
                entity = (Entity) this.mEntities.get(packageName);
            }
            if (entity != null) {
                double ctr = getCachedCtr(sbn);
                if (ctr == -1.0d) {
                    ctr = entity.getCtr(this.mCtrNormalizer, notif.getGroup());
                    cacheCtr(sbn, ctr);
                }
                double rawScore = getRawScore(notif);
                double appUsageScore = this.mAppUsageStatistics.getAppUsageScore(entity.getKey());
                double amortizedBonus = entity.getAmortizedBonus();
                double scorePerturbation = 0.0d;
                if (extras != null) {
                    CharSequence title = (CharSequence) extras.get("android.title");
                    if (title != null) {
                        scorePerturbation = ((((double) title.hashCode()) / 2.147483647E9d) / 2.0d) + 0.5d;
                    }
                }
                value = ((1.0d / (Math.exp(-(((((0.25d * ctr) + (0.25d * amortizedBonus)) + (0.25d * rawScore)) + (0.25d * appUsageScore)) + (0.01d * scorePerturbation))) + 1.0d)) - 0.5d) * 2.0d;
            }
        }
        if (extras == null) {
            extras = new Bundle();
            sbn.getNotification().extras = extras;
        }
        extras.putDouble("cached_base_score", value);
        return value;
    }

    private double getCachedCtr(StatusBarNotification sbn) {
        Bundle extras = sbn.getNotification().extras;
        if (extras == null || !extras.containsKey("cached_ctr")) {
            return -1.0d;
        }
        return extras.getDouble("cached_ctr");
    }

    private void cacheCtr(StatusBarNotification sbn, double ctr) {
        Bundle extras = sbn.getNotification().extras;
        if (extras == null) {
            extras = new Bundle();
            sbn.getNotification().extras = extras;
        }
        extras.putDouble("cached_ctr", ctr);
    }

    public double getCachedNotificationScore(StatusBarNotification sbn) {
        Bundle extras = sbn.getNotification().extras;
        if (extras == null || !extras.containsKey("cached_score")) {
            return -1.0d;
        }
        return extras.getDouble("cached_score");
    }

    private void cacheScore(StatusBarNotification sbn, double score) {
        Bundle extras = sbn.getNotification().extras;
        if (extras == null) {
            extras = new Bundle();
            sbn.getNotification().extras = extras;
        }
        extras.putDouble("cached_score", score);
    }

    public void onEntitiesLoaded(HashMap<String, Entity> entities, List<String> blacklistedPackages) {
        synchronized (this.mEntitiesLock) {
            this.mEntities = entities;
            this.mBlacklistedPackages = blacklistedPackages;
            Log.d("NVID", "Ranker$onEntitiesLoaded");
            for (String i : blacklistedPackages) {
                Log.d("NVID", "  " + i);
            }
        }
        synchronized (this.mCachedActions) {
            this.mQueryingScores = false;
            if (DEBUG) {
                Log.d(TAG, "Scores retrieved, playing back " + this.mCachedActions.size() + " actions");
            }
            while (!this.mCachedActions.isEmpty()) {
                CachedAction action = (CachedAction) this.mCachedActions.remove();
                onAction(action.key, action.component, action.group, action.action);
            }
            if (!DateUtil.initialRankingApplied(this.mContext)) {
                String[] outOfBoxOrder = this.mContext.getResources().getStringArray(R.array.out_of_box_order);
                String[] partnerOutOfBoxOrder = ServicePartner.get(this.mContext).getOutOfBoxOrder();
                int partnerLength = partnerOutOfBoxOrder != null ? partnerOutOfBoxOrder.length : 0;
                int totalOrderings = (outOfBoxOrder != null ? outOfBoxOrder.length : 0) + partnerLength;
                if (partnerOutOfBoxOrder != null) {
                    applyOutOfBoxOrdering(partnerOutOfBoxOrder, 0, totalOrderings);
                }
                if (outOfBoxOrder != null) {
                    applyOutOfBoxOrdering(outOfBoxOrder, partnerLength, totalOrderings);
                }
                DateUtil.setInitialRankingAppliedFlag(this.mContext, true);
            }
        }
        for (RankingListener listener : this.mListeners) {
            listener.onRankerReady();
        }
    }

    private void applyOutOfBoxOrdering(String[] order, int offsetEntities, int totalEntities) {
        if (order != null && order.length != 0 && offsetEntities >= 0 && totalEntities >= order.length + offsetEntities) {
            int entitiesBelow = (totalEntities - offsetEntities) - order.length;
            double bonusSum = (((double) totalEntities) * 0.5d) * ((double) (totalEntities + 1));
            int size = order.length;
            for (int i = 0; i < size; i++) {
                String key = order[(size - i) - 1];
                if (!this.mEntities.containsKey(key)) {
                    int score = (entitiesBelow + i) + 1;
                    Entity e = new Entity(this.mContext, this.mDbHelper, key, (long) score, (long) ((entitiesBelow + size) - i), true);
                    e.setBonusValues(((double) sRankerParameters.getOutOfBoxBonus()) * (((double) score) / bonusSum), new Date().getTime());
                    this.mEntities.put(key, e);
                    this.mDbHelper.saveEntity(e);
                }
            }
        }
    }
}
