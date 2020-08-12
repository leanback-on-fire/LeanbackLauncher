package com.amazon.tv.tvrecommendations.service;

import android.app.Notification;
import android.content.Context;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import com.amazon.tv.leanbacklauncher.BuildConfig;
import com.amazon.tv.leanbacklauncher.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Ranker implements DbHelper.Listener {
    static RankerParameters sRankerParameters;
    private AppUsageStatistics mAppUsageStatistics;
    private List<String> mBlacklistedPackages = new ArrayList();
    private final Queue<CachedAction> mCachedActions = new LinkedList();
    private Context mContext;
    private Normalizer mCtrNormalizer = new Normalizer();
    private DbHelper mDbHelper;
    private HashMap<String, Entity> mEntities = new HashMap();
    private final Object mEntitiesLock = new Object();
    private List<RankingListener> mListeners = new ArrayList();
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

    public Ranker(Context ctx, DbHelper dbHelper, RankerParameters rankerParameters) {
        this.mContext = ctx;
        this.mDbHelper = dbHelper;
        this.mQueryingScores = true;
        sRankerParameters = rankerParameters;
        this.mDbHelper.getEntities(this);
        this.mAppUsageStatistics = new AppUsageStatistics(ctx);
    }

    public static double getGroupStarterScore() {
        return sRankerParameters.getGroupStarterScore();
    }

    public static double getInstallBonus() {
        return sRankerParameters.getInstallBonus();
    }

    public static double getBonusFadePeriod() {
        return sRankerParameters.getBonusFadePeriodDays() * 8.64E7f;
    }

    public void addListener(RankingListener listener) {
        this.mListeners.add(listener);
    }

    public void reload() {
        this.mDbHelper.getEntities(this);
    }

    public boolean isBlacklisted(String packageName) {
        if (BuildConfig.DEBUG)
            Log.d("Ranker", "isBlacklisted: packageName=\"" + packageName + "\" -> " + this.mBlacklistedPackages.contains(packageName));
        return this.mBlacklistedPackages.contains(packageName);
    }

    boolean hasBlacklistedPackages() {
        return this.mBlacklistedPackages.size() > 0;
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
        if (BuildConfig.DEBUG)
            Log.d("Ranker", "onActionPackageRemoved: packageName=" + packageName);
        onAction(packageName, null, null, 3);
    }

    private void onAction(String paramString1, String paramString2, String paramString3, int actionType) {
        if (TextUtils.isEmpty(paramString1)) {
            return;
        }
        synchronized (this.mCachedActions) {
            if (this.mQueryingScores) {
                if (BuildConfig.DEBUG)
                    Log.d("Ranker", "onAction: Scores not ready, caching this action\nkey=" + paramString1 + ", component=" + paramString2 + ", group=" + paramString3 + ", actionType=" + RankerActions.actionToString(actionType));
                this.mCachedActions.add(new CachedAction(paramString1, paramString2, paramString3, actionType));
                return;
            }
        }

        if (BuildConfig.DEBUG)
            Log.d("Ranker", "onAction: key=" + paramString1 + ", component=" + paramString2 + ", group=" + paramString3 + ", actionType=" + RankerActions.actionToString(actionType));

        Entity localEntity;

        synchronized (this.mEntitiesLock) {
            localEntity = this.mEntities.get(paramString1);

            if (actionType != 3) {
                if (localEntity == null) {
                    localEntity = new Entity(this.mContext, this.mDbHelper, paramString1);
                    this.mEntities.put(paramString1, localEntity);
                }
                localEntity.onAction(actionType, paramString2, paramString3);
                this.mDbHelper.saveEntity(localEntity);
            } else {
                if (localEntity != null) {
                    if (localEntity.getOrder(paramString2) != 0L) {
                        localEntity.onAction(actionType, paramString2, null);
                        this.mDbHelper.removeEntity(paramString1, false);
                    }
                } else {
                    this.mEntities.remove(paramString1);
                    this.mDbHelper.removeEntity(paramString1, true);

                }
            }
        }


        /*
        r8 = this;
        r1 = android.text.TextUtils.isEmpty(r9);
        if (r1 == 0) goto L_0x0007;
    L_0x0006:
        return;
    L_0x0007:
        r2 = r8.mCachedActions;
        monitor-enter(r2);
        r1 = r8.mQueryingScores;	 Catch:{ all -> 0x0054 }
        if (r1 == 0) goto L_0x0057;
    L_0x000e:
        r1 = "Ranker";
        r3 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0054 }
        r3.<init>();	 Catch:{ all -> 0x0054 }
        r4 = "onAction: Scores not ready, caching this action\nkey=";
        r3 = r3.append(r4);	 Catch:{ all -> 0x0054 }
        r3 = r3.append(r9);	 Catch:{ all -> 0x0054 }
        r4 = ", component=";
        r3 = r3.append(r4);	 Catch:{ all -> 0x0054 }
        r3 = r3.append(r10);	 Catch:{ all -> 0x0054 }
        r4 = ", group=";
        r3 = r3.append(r4);	 Catch:{ all -> 0x0054 }
        r3 = r3.append(r11);	 Catch:{ all -> 0x0054 }
        r4 = ", actionType=";
        r3 = r3.append(r4);	 Catch:{ all -> 0x0054 }
        r4 = RankerActions.actionToString(r12);	 Catch:{ all -> 0x0054 }
        r3 = r3.append(r4);	 Catch:{ all -> 0x0054 }
        r3 = r3.toString();	 Catch:{ all -> 0x0054 }
        android.util.Log.d(r1, r3);	 Catch:{ all -> 0x0054 }
        r1 = r8.mCachedActions;	 Catch:{ all -> 0x0054 }
        r3 = new Ranker$CachedAction;	 Catch:{ all -> 0x0054 }
        r3.<init>(r9, r10, r11, r12);	 Catch:{ all -> 0x0054 }
        r1.add(r3);	 Catch:{ all -> 0x0054 }
        monitor-exit(r2);	 Catch:{ all -> 0x0054 }
        goto L_0x0006;
    L_0x0054:
        r1 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x0054 }
        throw r1;
    L_0x0057:
        monitor-exit(r2);	 Catch:{ all -> 0x0054 }
        r1 = "Ranker";
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "onAction: key=";
        r2 = r2.append(r3);
        r2 = r2.append(r9);
        r3 = ", component=";
        r2 = r2.append(r3);
        r2 = r2.append(r10);
        r3 = ", group=";
        r2 = r2.append(r3);
        r2 = r2.append(r11);
        r3 = ", actionType=";
        r2 = r2.append(r3);
        r3 = RankerActions.actionToString(r12);
        r2 = r2.append(r3);
        r2 = r2.toString();
        android.util.Log.d(r1, r2);
        r2 = r8.mEntitiesLock;
        monitor-enter(r2);
        r1 = r8.mEntities;	 Catch:{ all -> 0x00b9 }
        r0 = r1.get(r9);	 Catch:{ all -> 0x00b9 }
        r0 = (Entity) r0;	 Catch:{ all -> 0x00b9 }
        r1 = 3;
        if (r12 != r1) goto L_0x00c8;
    L_0x00a0:
        if (r0 == 0) goto L_0x00b6;
    L_0x00a2:
        r4 = r0.getOrder(r10);	 Catch:{ all -> 0x00b9 }
        r6 = 0;
        r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r1 == 0) goto L_0x00bc;
    L_0x00ac:
        r1 = 0;
        r0.onAction(r12, r10, r1);	 Catch:{ all -> 0x00b9 }
        r1 = r8.mDbHelper;	 Catch:{ all -> 0x00b9 }
        r3 = 0;
        r1.removeEntity(r9, r3);	 Catch:{ all -> 0x00b9 }
    L_0x00b6:
        monitor-exit(r2);	 Catch:{ all -> 0x00b9 }
        goto L_0x0006;
    L_0x00b9:
        r1 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x00b9 }
        throw r1;
    L_0x00bc:
        r1 = r8.mEntities;	 Catch:{ all -> 0x00b9 }
        r1.remove(r9);	 Catch:{ all -> 0x00b9 }
        r1 = r8.mDbHelper;	 Catch:{ all -> 0x00b9 }
        r3 = 1;
        r1.removeEntity(r9, r3);	 Catch:{ all -> 0x00b9 }
        goto L_0x00b6;
    L_0x00c8:
        if (r0 != 0) goto L_0x00d8;
    L_0x00ca:
        r0 = new Entity;	 Catch:{ all -> 0x00b9 }
        r1 = r8.mContext;	 Catch:{ all -> 0x00b9 }
        r3 = r8.mDbHelper;	 Catch:{ all -> 0x00b9 }
        r0.<init>(r1, r3, r9);	 Catch:{ all -> 0x00b9 }
        r1 = r8.mEntities;	 Catch:{ all -> 0x00b9 }
        r1.put(r9, r0);	 Catch:{ all -> 0x00b9 }
    L_0x00d8:
        r0.onAction(r12, r10, r11);	 Catch:{ all -> 0x00b9 }
        r1 = r8.mDbHelper;	 Catch:{ all -> 0x00b9 }
        r1.saveEntity(r0);	 Catch:{ all -> 0x00b9 }
        goto L_0x00b6;
        */
//        throw new UnsupportedOperationException("Method not decompiled: Ranker.onAction(java.lang.String, java.lang.String, java.lang.String, int):void");
    }

    public void markPostedRecommendations(String packageName) {
        if (BuildConfig.DEBUG)
            Log.d("Ranker", "markPostedRecommendations: packageName=" + packageName);
        synchronized (this.mEntitiesLock) {
            Entity entity = this.mEntities.get(packageName);
            if (entity == null) {
                entity = new Entity(this.mContext, this.mDbHelper, packageName);
                this.mEntities.put(packageName, entity);
            }
            if (!entity.hasPostedRecommendations()) {
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

    public final void calculateAdjustedScore(StatusBarNotification sbn, int position, boolean forceFirst) {
        cacheScore(sbn, (getBaseNotificationScore(sbn) * Math.pow(position + 1, -sRankerParameters.getSpreadFactor())) + ((double) (forceFirst ? 1 : 0)));
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
        if (extras != null && extras.containsKey("cached_base_score")) {
            return extras.getDouble("cached_base_score");
        }
        double value = -100.0d;
        String packageName = sbn.getPackageName();
        Notification notif = sbn.getNotification();
        if (!(notif == null || TextUtils.isEmpty(packageName))) {
            Entity entity;
            synchronized (this.mEntitiesLock) {
                entity = this.mEntities.get(packageName);
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
                value = ((1.0d / (1.0d + Math.exp(-(((((0.25d * ctr) + (0.25d * amortizedBonus)) + (0.25d * rawScore)) + (0.25d * appUsageScore)) + (0.01d * scorePerturbation))))) - 0.5d) * 2.0d;
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
        int partnerLength = 0;
        if (BuildConfig.DEBUG)
            Log.d("Ranker", "onEntitiesLoaded:\n\tentities=" + entities + "\n\tblacklistedPackages=" + blacklistedPackages);
        synchronized (this.mEntitiesLock) {
            this.mEntities = entities;
            this.mBlacklistedPackages = blacklistedPackages;
        }
        synchronized (this.mCachedActions) {
            this.mQueryingScores = false;
            if (BuildConfig.DEBUG)
                Log.d("Ranker", "onEntitiesLoaded: Scores retrieved, playing back " + this.mCachedActions.size() + " actions");
            while (!this.mCachedActions.isEmpty()) {
                CachedAction action = this.mCachedActions.remove();
                onAction(action.key, action.component, action.group, action.action);
            }
            if (!DateUtil.initialRankingApplied(this.mContext)) {
                String[] outOfBoxOrder = this.mContext.getResources().getStringArray(R.array.out_of_box_order);
                String[] partnerOutOfBoxOrder = ServicePartner.get(this.mContext).getOutOfBoxOrder();
                if (partnerOutOfBoxOrder != null) {
                    partnerLength = partnerOutOfBoxOrder.length;
                }
                int totalOrderings = outOfBoxOrder.length + partnerLength;
                if (partnerOutOfBoxOrder != null) {
                    applyOutOfBoxOrdering(partnerOutOfBoxOrder, 0, totalOrderings);
                }
                applyOutOfBoxOrdering(outOfBoxOrder, partnerLength, totalOrderings);
                DateUtil.setInitialRankingAppliedFlag(this.mContext, true);
            }
        }
        for (RankingListener listener : this.mListeners) {
            listener.onRankerReady();
        }
    }

    private void applyOutOfBoxOrdering(String[] order, int offsetEntities, int totalEntities) {
        if (BuildConfig.DEBUG)
            Log.d("Ranker", "applyOutOfBoxOrdering: order=" + Arrays.toString(order) + ", offsetEntities=" + offsetEntities + ", totalEntities=" + totalEntities);
        if (order != null && order.length != 0 && offsetEntities >= 0 && totalEntities >= order.length + offsetEntities) {
            int entitiesBelow = (totalEntities - offsetEntities) - order.length;
            double bonusSum = (0.5d * ((double) totalEntities)) * ((double) (totalEntities + 1));
            int size = order.length;
            for (int i = 0; i < size; i++) {
                String key = order[(size - i) - 1];
                if (!this.mEntities.containsKey(key)) {
                    int score = (entitiesBelow + i) + 1;
                    Entity e = new Entity(this.mContext, this.mDbHelper, key, score, (entitiesBelow + size) - i, true);
                    e.setBonusValues(((double) sRankerParameters.getOutOfBoxBonus()) * (((double) score) / bonusSum), new Date().getTime());
                    this.mEntities.put(key, e);
                    this.mDbHelper.saveEntity(e);
                }
            }
        }
    }
}
