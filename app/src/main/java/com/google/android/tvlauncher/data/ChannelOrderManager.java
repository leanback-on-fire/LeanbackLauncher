package com.google.android.tvlauncher.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.tvlauncher.model.HomeChannel;
import com.google.android.tvlauncher.util.porting.Edited;
import com.google.android.tvlauncher.util.porting.Reason;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChannelOrderManager {
    private static final boolean DEBUG = false;
    private static final int DIRECTION_DOWN = 1;
    private static final int DIRECTION_UP = -1;
    private static final String KEY_ORDERED_CHANNEL_IDS = "ORDERED_CHANNEL_IDS";
    private static final String PREF_CHANNEL_ORDER_MANAGER = "CHANNEL_ORDER_MANAGER";
    private static final String TAG = "ChannelOrderManager";
    private Map<Long, Integer> mChannelPositions;
    private List<HomeChannel> mChannels;
    private List<HomeChannelsObserver> mChannelsObservers;
    private Context mContext;

    ChannelOrderManager(Context paramContext) {
        this.mContext = paramContext.getApplicationContext();
        readChannelOrderFromStorage();
    }

    private int moveChannelOnePosition(long paramLong, int paramInt1, int paramInt2) {
        paramInt2 = paramInt1 + paramInt2;
        HomeChannel localHomeChannel1 = (HomeChannel) this.mChannels.get(paramInt1);
        HomeChannel localHomeChannel2 = (HomeChannel) this.mChannels.get(paramInt2);
        this.mChannelPositions.put(Long.valueOf(paramLong), paramInt2);
        this.mChannelPositions.put(Long.valueOf(localHomeChannel2.getId()), Integer.valueOf(paramInt1));
        this.mChannels.set(paramInt2, localHomeChannel1);
        this.mChannels.set(paramInt1, localHomeChannel2);
        notifyChannelMoved(paramInt1, paramInt2);
        saveChannelOrderToStorage();
        return paramInt2;
    }

    private void notifyChannelMoved(int paramInt1, int paramInt2) {
        Iterator<HomeChannelsObserver> localIterator = this.mChannelsObservers.iterator();
        while (localIterator.hasNext()) {
            (localIterator.next()).onChannelMove(paramInt1, paramInt2);
        }
    }

    @SuppressLint({"UseSparseArrays"})
    @Edited(reason = Reason.HOT_MESS)
    // todo this feels like a loop but isn't...
    private void readChannelOrderFromStorage() {
        int j = 0;
        String[] arrayOfString = TextUtils.split(this.mContext.getSharedPreferences("CHANNEL_ORDER_MANAGER", 0).getString("ORDERED_CHANNEL_IDS", ""), ",");
        this.mChannelPositions = new HashMap<>(arrayOfString.length);
        int m = arrayOfString.length;
        int i = 0;
        if (j < m) {
            String str = arrayOfString[j];

            Map<Long, Integer> localMap;
            try {
                localMap = this.mChannelPositions;
                long l = Long.parseLong(str);
                //  int i = 0;
                int k = 1;

                localMap.put(l, i);
                // todo  i = k;
                //  j += 1;
            } catch (NumberFormatException localNumberFormatException2) {
                Log.e("ChannelOrderManager", "Invalid channel ID: " + str + " at position " + i);
            }
        }
    }

    private void saveChannelOrderToStorage() {
        SharedPreferences.Editor localEditor = this.mContext.getSharedPreferences("CHANNEL_ORDER_MANAGER", 0).edit();
        Object localObject = "";
        if (this.mChannelPositions.size() > 0) {
            localObject = new StringBuilder(this.mChannels.size() * 12);
            Iterator<HomeChannel> localIterator = this.mChannels.iterator();
            while (localIterator.hasNext()) {
                ((StringBuilder) localObject).append((localIterator.next()).getId()).append(',');
            }
            ((StringBuilder) localObject).setLength(((StringBuilder) localObject).length() - 1);
            localObject = ((StringBuilder) localObject).toString();
        }
        localEditor.putString("ORDERED_CHANNEL_IDS", (String) localObject).apply();
    }

    public boolean canMoveChannelDown(long paramLong) {
        Integer localInteger = (Integer) this.mChannelPositions.get(Long.valueOf(paramLong));
        return (localInteger != null) && (localInteger.intValue() < this.mChannelPositions.size() - 1);
    }

    public boolean canMoveChannelUp(long paramLong) {
        Integer localInteger = (Integer) this.mChannelPositions.get(Long.valueOf(paramLong));
        return (localInteger != null) && (localInteger.intValue() > 0);
    }

    @Nullable
    Integer getChannelPosition(long paramLong) {
        return (Integer) this.mChannelPositions.get(Long.valueOf(paramLong));
    }

    public int moveChannelDown(long paramLong) {
        if (!canMoveChannelDown(paramLong)) {
            throw new IllegalArgumentException("Can't move channel " + paramLong + " down");
        }
        return moveChannelOnePosition(paramLong, ((Integer) this.mChannelPositions.get(Long.valueOf(paramLong))).intValue(), 1);
    }

    public int moveChannelUp(long paramLong) {
        if (!canMoveChannelUp(paramLong)) {
            throw new IllegalArgumentException("Can't move channel " + paramLong + " up");
        }
        return moveChannelOnePosition(paramLong, ((Integer) this.mChannelPositions.get(Long.valueOf(paramLong))).intValue(), -1);
    }

    void refreshChannelPositions() {
        if (this.mChannels == null) {
            throw new IllegalStateException("Channels must be set");
        }
        HashMap<Long, Integer> localHashMap = new HashMap<Long, Integer>(this.mChannels.size());
        int i = 0;
        Iterator<HomeChannel> localIterator = this.mChannels.iterator();
        while (localIterator.hasNext()) {
            localHashMap.put(Long.valueOf((localIterator.next()).getId()), Integer.valueOf(i));
            i += 1;
        }
        this.mChannelPositions = localHashMap;
        saveChannelOrderToStorage();
    }

    void setChannels(List<HomeChannel> paramList) {
        this.mChannels = paramList;
    }

    void setChannelsObservers(List<HomeChannelsObserver> paramList) {
        this.mChannelsObservers = paramList;
    }

    @Retention(RetentionPolicy.SOURCE)
    private static @interface Direction {
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/data/ChannelOrderManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */