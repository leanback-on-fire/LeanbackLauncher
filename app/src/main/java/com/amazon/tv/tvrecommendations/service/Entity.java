package com.amazon.tv.tvrecommendations.service;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
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
        Long lastOpened = this.mLastOpened.get(component);
        if (lastOpened == null) {
            lastOpened = this.mLastOpened.get(null);
            if (lastOpened == null) {
                lastOpened = Long.valueOf(0);
            }
        }
        return lastOpened.longValue();
    }

    public long getOrder(String component) {
        Long rankOrder = this.mRankOrder.get(component);
        if ((rankOrder == null || rankOrder.longValue() == 0) && this.mRankOrder.keySet().size() == 1) {
            rankOrder = this.mRankOrder.get(null);
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
        return this.mKey;
    }

    public synchronized void onAction(int actionType, String component, String group) {
        Date date = new Date();
        long time = date.getTime();
        if (this.mDbHelper.getMostRecentTimeStamp() >= time) {
            time = this.mDbHelper.getMostRecentTimeStamp() + 1;
        }
        switch (actionType) {
            case 0:
                if (getLastOpenedTimeStamp(component) == 0) {
                    addBonusValue(Ranker.getInstallBonus());
                    setLastOpenedTimeStamp(component, time);
                    break;
                }
                break;
            case 1:
                setLastOpenedTimeStamp(component, time);
                break;
            case 3:
                this.mLastOpened.clear();
                this.mBonus = 0.0d;
                this.mBonusTime = 0;
                this.mBucketList.clear();
                break;
            default:
                Bucket bucket = getOrAddBucket(group);
                if (bucket != null) {
                    ActiveDayBuffer buffer = bucket.getBuffer();
                    Signals value = buffer.get(date);
                    if (value == null) {
                        value = new Signals();
                    }
                    switch (actionType) {
                        case 2:
                            value.mClicks++;
                            buffer.set(date, value);
                            touchBucket(group);
                            break;
                        case 4:
                            value.mImpressions++;
                            buffer.set(date, value);
                            touchBucket(group);
                            break;
                        default:
                            break;
                    }
                }
                break;
        }
    }

    public synchronized Bucket addBucket(String group, long timeStamp) {
        Bucket bucket = null;
        group = safeGroupId(group);
        Bucket bucket2;
        if (this.mBucketList.containsKey(group)) {
            Log.e("Entity", "Entity.addBucket: Got duplicated Group ID: " + group);
            bucket2 = this.mBucketList.get(group);
            bucket2.setTimestamp(timeStamp);
            this.mBucketList.remove(group);
            this.mBucketList.put(group, bucket2);
            bucket = bucket2;
        } else {
            if (this.mBucketList.size() >= 100) {
                String removedGroup = this.mBucketList.keySet().iterator().next();
                this.mBucketList.remove(removedGroup);
                if (this.mDbHelper != null) {
                    this.mDbHelper.removeGroupData(this.mKey, removedGroup);
                }
            }
            bucket2 = new Bucket(timeStamp);
            this.mBucketList.put(group, bucket2);
            Bucket bucket3 = bucket2;
        }
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
        long timestamp;
        Bucket bucket = this.mBucketList.get(safeGroupId(group));
        if (bucket != null) {
            timestamp = bucket.getTimestamp();
        } else {
            timestamp = 0;
        }
        return timestamp;
    }

    public synchronized ActiveDayBuffer getSignalsBuffer(String group) {
        ActiveDayBuffer buffer;
        Bucket bucket = this.mBucketList.get(safeGroupId(group));
        if (bucket != null) {
            buffer = bucket.getBuffer();
        } else {
            buffer = null;
        }
        return buffer;
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
        if (this.mBonusTime == 0 && this.mBonus == 0.0d) {
            return 0.0d;
        }
        double factor = 1.0d - (((double) (System.currentTimeMillis() - this.mBonusTime)) / Ranker.getBonusFadePeriod());
        if (factor >= 0.0d) {
            return this.mBonus * factor;
        }
        return 0.0d;
    }

    private void addBonusValue(double newBonus) {
        this.mBonus = getAmortizedBonus() + newBonus;
        this.mBonusTime = System.currentTimeMillis();
    }

    public synchronized double getCtr(Normalizer ctrNormalizer, String group) {
        double ctr;
        ctr = 0.0d;
        Bucket bucket = this.mBucketList.get(safeGroupId(group));
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
        return id == null ? "" : id;
    }

    private Bucket getOrAddBucket(String group) {
        group = safeGroupId(group);
        Bucket bucket = this.mBucketList.get(group);
        if (bucket == null) {
            return addBucket(group, System.currentTimeMillis());
        }
        return bucket;
    }

    private void touchBucket(String group) {
        Bucket bucket = this.mBucketList.remove(group);
        if (bucket != null) {
            bucket.updateTimestamp();
            this.mBucketList.put(group, bucket);
        }
    }

    public String toString() {
        return "Entity{hashCode='" + Integer.toHexString(hashCode()) + '\'' + "mKey='" + this.mKey + '\'' + ", mHasPostedRecommendations=" + this.mHasPostedRecommendations + '}';
    }
}
