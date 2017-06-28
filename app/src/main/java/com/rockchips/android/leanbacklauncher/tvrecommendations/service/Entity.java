package com.rockchips.android.leanbacklauncher.tvrecommendations.service;

import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

class Entity {
    private double mBonus;
    private long mBonusTime;
    private LinkedHashMap<String, Bucket> mBucketList;
    private DbHelper mDbHelper;
    private boolean mHasPostedRecommendations;
    private String mKey;
    private HashMap<String, Long> mLastOpened;
    private HashMap<String, Long> mRankOrder;
    private final SignalsAggregator mSignalsAggregator;

    public Entity(Context context, DbHelper helper, String key, long lastOpenTime, long initialOrder, boolean postedRec) {
        this(context, helper, key, initialOrder, postedRec);
        setLastOpenedTimeStamp(null, lastOpenTime);
    }

    public Entity(Context context, DbHelper helper, String key, long initialOrder, boolean postedRec) {
        this(context, helper, key);
        this.mRankOrder.put(null, Long.valueOf(initialOrder));
        this.mHasPostedRecommendations = postedRec;
    }

    public Entity(Context ctx, DbHelper helper, String key) {
        this.mBucketList = new LinkedHashMap();
        this.mSignalsAggregator = new SignalsAggregator();
        this.mLastOpened = new HashMap();
        this.mRankOrder = new HashMap();
        this.mDbHelper = helper;
        this.mBonus = 0.0d;
        this.mBonusTime = 0;
        this.mKey = key;
        this.mHasPostedRecommendations = false;
    }

    public Set<String> getEntityComponents() {
        return this.mRankOrder.keySet();
    }

    public void setLastOpenedTimeStamp(String component, long timeStamp) {
        this.mLastOpened.put(component, Long.valueOf(timeStamp));
    }

    public long getLastOpenedTimeStamp(String component) {
        Long lastOpened = (Long) this.mLastOpened.get(component);
        if (lastOpened == null) {
            lastOpened = (Long) this.mLastOpened.get(null);
            if (lastOpened == null) {
                lastOpened = Long.valueOf(0);
            }
        }
        return lastOpened.longValue();
    }

    public long getOrder(String component) {
        Long rankOrder = (Long) this.mRankOrder.get(component);
        if ((rankOrder == null || rankOrder.longValue() == 0) && this.mRankOrder.keySet().size() == 1) {
            rankOrder = (Long) this.mRankOrder.get(null);
            if (rankOrder == null) {
                rankOrder = Long.valueOf(0);
            }
            setOrder(component, rankOrder.longValue());
        }
        if (rankOrder != null) {
            return rankOrder.longValue();
        }
        return 0;
    }

    public void setOrder(String component, long order) {
        this.mRankOrder.put(component, Long.valueOf(order));
    }

    public boolean hasPostedRecommendations() {
        return this.mHasPostedRecommendations;
    }

    public void markPostedRecommendations() {
        this.mHasPostedRecommendations = true;
    }

    public String getKey() {
        return new String(this.mKey);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void onAction(int r13, String r14, String r15) {
        /*
        r12 = this;
        r10 = 0;
        monitor-enter(r12);
        r2 = new java.util.Date;	 Catch:{ all -> 0x0079 }
        r2.<init>();	 Catch:{ all -> 0x0079 }
        r4 = r2.getTime();	 Catch:{ all -> 0x0079 }
        r6 = r12.mDbHelper;	 Catch:{ all -> 0x0079 }
        r6 = r6.getMostRecentTimeStamp();	 Catch:{ all -> 0x0079 }
        r6 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1));
        if (r6 < 0) goto L_0x0020;
    L_0x0016:
        r6 = r12.mDbHelper;	 Catch:{ all -> 0x0079 }
        r6 = r6.getMostRecentTimeStamp();	 Catch:{ all -> 0x0079 }
        r8 = 1;
        r4 = r6 + r8;
    L_0x0020:
        switch(r13) {
            case 0: goto L_0x002b;
            case 1: goto L_0x0053;
            case 2: goto L_0x0023;
            case 3: goto L_0x003f;
            default: goto L_0x0023;
        };	 Catch:{ all -> 0x0079 }
    L_0x0023:
        r0 = r12.getOrAddBucket(r15);	 Catch:{ all -> 0x0079 }
        if (r0 != 0) goto L_0x0058;
    L_0x0029:
        monitor-exit(r12);
        return;
    L_0x002b:
        r6 = r12.getLastOpenedTimeStamp(r14);	 Catch:{ all -> 0x0079 }
        r6 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1));
        if (r6 != 0) goto L_0x003d;
    L_0x0033:
        r6 = com.rockchips.android.tvrecommendations.service.Ranker.getInstallBonus();	 Catch:{ all -> 0x0079 }
        r12.addBonusValue(r6);	 Catch:{ all -> 0x0079 }
        r12.setLastOpenedTimeStamp(r14, r4);	 Catch:{ all -> 0x0079 }
    L_0x003d:
        monitor-exit(r12);
        return;
    L_0x003f:
        r6 = r12.mLastOpened;	 Catch:{ all -> 0x0079 }
        r6.clear();	 Catch:{ all -> 0x0079 }
        r6 = 0;
        r12.mBonus = r6;	 Catch:{ all -> 0x0079 }
        r6 = 0;
        r12.mBonusTime = r6;	 Catch:{ all -> 0x0079 }
        r6 = r12.mBucketList;	 Catch:{ all -> 0x0079 }
        r6.clear();	 Catch:{ all -> 0x0079 }
        monitor-exit(r12);
        return;
    L_0x0053:
        r12.setLastOpenedTimeStamp(r14, r4);	 Catch:{ all -> 0x0079 }
        monitor-exit(r12);
        return;
    L_0x0058:
        r1 = r0.getBuffer();	 Catch:{ all -> 0x0079 }
        r3 = r1.get(r2);	 Catch:{ all -> 0x0079 }
        if (r3 != 0) goto L_0x0067;
    L_0x0062:
        r3 = new com.rockchips.android.tvrecommendations.service.Signals;	 Catch:{ all -> 0x0079 }
        r3.<init>();	 Catch:{ all -> 0x0079 }
    L_0x0067:
        switch(r13) {
            case 2: goto L_0x006c;
            case 3: goto L_0x006a;
            case 4: goto L_0x007c;
            default: goto L_0x006a;
        };
    L_0x006a:
        monitor-exit(r12);
        return;
    L_0x006c:
        r6 = r3.mClicks;	 Catch:{ all -> 0x0079 }
        r6 = r6 + 1;
        r3.mClicks = r6;	 Catch:{ all -> 0x0079 }
        r1.set(r2, r3);	 Catch:{ all -> 0x0079 }
        r12.touchBucket(r15);	 Catch:{ all -> 0x0079 }
        goto L_0x006a;
    L_0x0079:
        r6 = move-exception;
        monitor-exit(r12);
        throw r6;
    L_0x007c:
        r6 = r3.mImpressions;	 Catch:{ all -> 0x0079 }
        r6 = r6 + 1;
        r3.mImpressions = r6;	 Catch:{ all -> 0x0079 }
        r1.set(r2, r3);	 Catch:{ all -> 0x0079 }
        r12.touchBucket(r15);	 Catch:{ all -> 0x0079 }
        goto L_0x006a;
        */
       // throw new UnsupportedOperationException("Method not decompiled: com.rockchips.android.tvrecommendations.service.Entity.onAction(int, java.lang.String, java.lang.String):void");
    }

    public synchronized Bucket addBucket(String group, long timeStamp) {
        group = safeGroupId(group);
        if (this.mBucketList.containsKey(group)) {
            Log.e("Entity", "Entity.addBucket: Got duplicated Group ID: " + group);
            Bucket bucket = (Bucket) this.mBucketList.get(group);
            bucket.setTimestamp(timeStamp);
            this.mBucketList.remove(group);
            this.mBucketList.put(group, bucket);
            return bucket;
        }
        if (this.mBucketList.size() >= 100) {
            String removedGroup = (String) this.mBucketList.keySet().iterator().next();
            this.mBucketList.remove(removedGroup);
            if (this.mDbHelper != null) {
                this.mDbHelper.removeGroupData(this.mKey, removedGroup);
            }
        }
        Bucket bucket = new Bucket(timeStamp);
        this.mBucketList.put(group, bucket);
        return bucket;
    }

    public synchronized ArrayList<String> getGroupIds() {
        ArrayList<String> groups;
        groups = new ArrayList();
        for (String groupId : this.mBucketList.keySet()) {
            if (groupId == null) {
                break;
            }
            groups.add(groupId);
        }
        return groups;
    }

    public synchronized long getGroupTimeStamp(String group) {
        Bucket bucket = (Bucket) this.mBucketList.get(safeGroupId(group));
        if (bucket == null) {
            return 0;
        }
        return bucket.getTimestamp();
    }

    public synchronized ActiveDayBuffer getSignalsBuffer(String group) {
        Bucket bucket = (Bucket) this.mBucketList.get(safeGroupId(group));
        if (bucket == null) {
            return null;
        }
        return bucket.getBuffer();
    }

    public synchronized void setBonusValues(double bonus, long timeStamp) {
        if (((double) (timeStamp - System.currentTimeMillis())) >= Ranker.getBonusFadePeriod()) {
            this.mBonus = 0.0d;
            this.mBonusTime = 0;
        } else {
            this.mBonus = bonus;
            this.mBonusTime = timeStamp;
        }
    }

    public double getBonus() {
        return this.mBonus;
    }

    public long getBonusTimeStamp() {
        return this.mBonusTime;
    }

    public double getAmortizedBonus() {
        double d = 0.0d;
        if (this.mBonusTime == 0 && this.mBonus == 0.0d) {
            return 0.0d;
        }
        double factor = 1.0d - (((double) (System.currentTimeMillis() - this.mBonusTime)) / Ranker.getBonusFadePeriod());
        if (factor >= 0.0d) {
            d = this.mBonus * factor;
        }
        return d;
    }

    private void addBonusValue(double newBonus) {
        this.mBonus = getAmortizedBonus() + newBonus;
        this.mBonusTime = System.currentTimeMillis();
    }

    public synchronized double getCtr(Normalizer ctrNormalizer, String group) {
        double ctr;
        ctr = 0.0d;
        Bucket bucket = (Bucket) this.mBucketList.get(safeGroupId(group));
        if (bucket != null) {
            double aggregatedCtr = bucket.getBuffer().getAggregatedScore(this.mSignalsAggregator);
            if (aggregatedCtr != -1.0d) {
                ctr = ctrNormalizer.getNormalizedValue(aggregatedCtr);
            }
        }
        return ctr;
    }

    public synchronized void addNormalizeableValues(Normalizer ctrNormalizer) {
        for (Bucket bucket : this.mBucketList.values()) {
            if (bucket == null) {
                break;
            } else if (bucket.getBuffer().hasData()) {
                ctrNormalizer.addNormalizeableValue(bucket.getBuffer().getAggregatedScore(this.mSignalsAggregator));
            }
        }
    }

    private String safeGroupId(String id) {
        return id == null ? new String("") : id;
    }

    private Bucket getOrAddBucket(String group) {
        group = safeGroupId(group);
        Bucket bucket = (Bucket) this.mBucketList.get(group);
        if (bucket == null) {
            return addBucket(group, System.currentTimeMillis());
        }
        return bucket;
    }

    private void touchBucket(String group) {
        Bucket bucket = (Bucket) this.mBucketList.remove(group);
        if (bucket != null) {
            bucket.updateTimestamp();
            this.mBucketList.put(group, bucket);
        }
    }
}
