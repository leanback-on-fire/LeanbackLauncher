package com.google.android.tvlauncher.data;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.android.tvlauncher.util.ExtendableTimer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

class DataSourceObserver {
    private static final int ALL_PROGRAMS_INVALIDATION_DELAY_MS = 3000;
    private static final int ALL_PROGRAMS_INVALIDATION_MAXIMUM_DELAY_MS = 15000;
    private static final int ALL_PROGRAMS_INVALIDATION_TIMER_ID = -2000;
    private static final int CHANNELS_INVALIDATION_DELAY_MS = 3000;
    private static final int CHANNELS_INVALIDATION_MAXIMUM_DELAY_MS = 10000;
    private static final int CHANNELS_INVALIDATION_TIMER_ID = -1000;
    private static final int CHANNEL_ID_COLUMN_INDEX = 1;
    private static final int CHANNEL_PROGRAMS_INVALIDATION_DELAY_MS = 3000;
    private static final int CHANNEL_PROGRAMS_INVALIDATION_MAXIMUM_DELAY_MS = 10000;
    private static final boolean DEBUG = false;
    private static final int ID_COLUMN_INDEX = 0;
    private static final int MATCH_CHANNEL = 1;
    private static final int MATCH_CHANNEL_ID = 2;
    private static final int MATCH_CHANNEL_ID_LOGO = 3;
    private static final int MATCH_PROGRAM = 4;
    private static final int MATCH_PROGRAM_ID = 5;
    private static final int MATCH_WATCH_NEXT_PROGRAM = 6;
    private static final int MATCH_WATCH_NEXT_PROGRAM_ID = 7;
    private static final int PROGRAMS_DATA_LOAD_BATCH = 100;
    private static final int PROGRAMS_DATA_LOAD_DELAY_MS = 1000;
    private static final int PROGRAMS_DATA_LOAD_MAXIMUM_DELAY_MS = 5000;
    private static final int PROGRAMS_DATA_LOAD_TIMER_ID = -4000;
    private static final String[] PROJECTION = {"_id", "channel_id"};
    private static final String TAG = "DataSourceObserver";
    private static final int WATCH_NEXT_INVALIDATION_TIMER_ID = -3000;
    private static final int WATCH_NEXT_PROGRAMS_INVALIDATION_DELAY_MS = 3000;
    private static final int WATCH_NEXT_PROGRAMS_INVALIDATION_MAXIMUM_DELAY_MS = 10000;
    private static UriMatcher sUriMatcher = new UriMatcher(-1);
    private ExtendableTimer mAllProgramsInvalidationTimer;
    private BackgroundTaskCallbacks mBackgroundTaskCallbacks;
    private Callbacks mCallbacks;
    @SuppressLint({"UseSparseArrays"})
    private Map<Long, ExtendableTimer> mChannelProgramsInvalidationTimers = new HashMap(10);
    private ExtendableTimer mChannelsInvalidationTimer;
    private final ContentObserver mContentObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean paramAnonymousBoolean) {
            onChange(paramAnonymousBoolean, null);
        }

        public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri) {
            long l;
            switch (DataSourceObserver.sUriMatcher.match(paramAnonymousUri)) {
                case 1:
                case 2:
                    DataSourceObserver.this.invalidateAllChannels();
                    return;
                case 3:
                    List localList = paramAnonymousUri.getPathSegments();
                    if (localList.size() >= 2) {
                        try {
                            l = Long.parseLong((String) localList.get(1));
                            DataSourceObserver.this.invalidateChannelLogo(l);
                            DataSourceObserver.this.invalidateAllChannels();
                            return;
                        } catch (NumberFormatException localNumberFormatException1) {
                            Log.e("DataSourceObserver", "Invalid channel ID in URI: " + paramAnonymousUri);
                            return;
                        }
                    }
                    Log.e("DataSourceObserver", "Invalid channel logo URI: " + paramAnonymousUri);
                    return;
                case 4:
                    String str = paramAnonymousUri.getQueryParameter("channel");
                    if (str != null) {
                        try {
                            l = Long.parseLong(str);
                            DataSourceObserver.this.invalidateChannelProgramsIfCached(l);
                            return;
                        } catch (NumberFormatException localNumberFormatException2) {
                            Log.e("DataSourceObserver", "Invalid channel ID in URI: " + paramAnonymousUri);
                            return;
                        }
                    }
                    DataSourceObserver.this.invalidateAllPrograms();
                    return;
                case 5:
                    try {
                        l = Long.parseLong(paramAnonymousUri.getLastPathSegment());
                        DataSourceObserver.this.invalidateProgram(l);
                        return;
                    } catch (NumberFormatException localNumberFormatException3) {
                        Log.e("DataSourceObserver", "Invalid program ID in URI: " + paramAnonymousUri);
                        return;
                    }
                case 6: // todo 6?
                    DataSourceObserver.this.invalidateWatchNextPrograms();
                    TvDataManager.getInstance(DataSourceObserver.this.mContext).reloadWatchNextCache();
                    break;
                default:
            }

        }
    };
    private final Context mContext;
    private ExtendableTimerListener mExtendableTimerListener;
    private ExtendableTimer mProgramsDataLoadTimer;
    private Set<Long> mProgramsToLoadData = new HashSet(100);
    private boolean mRegistered;
    private ExtendableTimer mWatchNextInvalidationTimer;

    static {
        sUriMatcher.addURI("android.media.tv", "channel", 1);
        sUriMatcher.addURI("android.media.tv", "channel/#", 2);
        sUriMatcher.addURI("android.media.tv", "channel/#/logo", 3);
        sUriMatcher.addURI("android.media.tv", "preview_program", 4);
        sUriMatcher.addURI("android.media.tv", "preview_program/#", 5);
        sUriMatcher.addURI("android.media.tv", "watch_next_program", 6);
        sUriMatcher.addURI("android.media.tv", "watch_next_program/#", 7);
    }

    DataSourceObserver(Context paramContext, Callbacks paramCallbacks) {
        this.mContext = paramContext.getApplicationContext();
        this.mCallbacks = paramCallbacks;
    }

    @NonNull
    private String buildProgramSelection(Set<Long> paramSet) {
        StringBuilder localStringBuilder = new StringBuilder("_id IN (");
        for (Long aParamSet : paramSet) {
            localStringBuilder.append((aParamSet).toString()).append(",");
        }
        localStringBuilder.setLength(localStringBuilder.length() - 1);
        localStringBuilder.append(")");
        return localStringBuilder.toString();
    }

    private ExtendableTimerListener getTimerListener() {
        if (this.mExtendableTimerListener == null) {
            this.mExtendableTimerListener = new ExtendableTimerListener();
        }
        return this.mExtendableTimerListener;
    }

    private void invalidateAllChannels() {
        if (this.mChannelsInvalidationTimer == null) {
            this.mChannelsInvalidationTimer = new ExtendableTimer();
            this.mChannelsInvalidationTimer.setTimeout(3000L);
            this.mChannelsInvalidationTimer.setMaximumTimeout(10000L);
            this.mChannelsInvalidationTimer.setId(-1000L);
            this.mChannelsInvalidationTimer.setListener(getTimerListener());
        }
        this.mChannelsInvalidationTimer.start();
    }

    private void invalidateAllPrograms() {
        if (this.mAllProgramsInvalidationTimer == null) {
            this.mAllProgramsInvalidationTimer = new ExtendableTimer();
            this.mAllProgramsInvalidationTimer.setTimeout(3000L);
            this.mAllProgramsInvalidationTimer.setMaximumTimeout(15000L);
            this.mAllProgramsInvalidationTimer.setId(-2000L);
            this.mAllProgramsInvalidationTimer.setListener(getTimerListener());
        }
        if ((this.mProgramsDataLoadTimer != null) && (this.mProgramsDataLoadTimer.isStarted())) {
            this.mProgramsDataLoadTimer.cancel();
        }
        this.mProgramsToLoadData.clear();
        Iterator localIterator = this.mChannelProgramsInvalidationTimers.values().iterator();
        while (localIterator.hasNext()) {
            ExtendableTimer localExtendableTimer = (ExtendableTimer) localIterator.next();
            localExtendableTimer.cancel();
            localExtendableTimer.recycle();
        }
        this.mChannelProgramsInvalidationTimers.clear();
        this.mAllProgramsInvalidationTimer.start();
    }

    private void invalidateChannelLogo(long paramLong) {
        this.mCallbacks.invalidateChannelLogo(paramLong);
    }

    private void invalidateChannelPrograms(long paramLong) {
        if ((this.mAllProgramsInvalidationTimer != null) && (this.mAllProgramsInvalidationTimer.isStarted())) {
            return;
        }
        ExtendableTimer localExtendableTimer2 = (ExtendableTimer) this.mChannelProgramsInvalidationTimers.get(Long.valueOf(paramLong));
        ExtendableTimer localExtendableTimer1 = localExtendableTimer2;
        if (localExtendableTimer2 == null) {
            localExtendableTimer1 = ExtendableTimer.obtain();
            localExtendableTimer1.setTimeout(3000L);
            localExtendableTimer1.setMaximumTimeout(10000L);
            localExtendableTimer1.setId(paramLong);
            localExtendableTimer1.setListener(getTimerListener());
            this.mChannelProgramsInvalidationTimers.put(Long.valueOf(paramLong), localExtendableTimer1);
        }
        localExtendableTimer1.start();
    }

    private void invalidateChannelProgramsIfCached(long paramLong) {
        if ((this.mAllProgramsInvalidationTimer != null) && (this.mAllProgramsInvalidationTimer.isStarted())) {
        }
        while (!this.mCallbacks.areProgramsCached(paramLong)) {
            return;
        }
        invalidateChannelPrograms(paramLong);
    }

    private void invalidateProgram(long paramLong) {
        if ((this.mAllProgramsInvalidationTimer != null) && (this.mAllProgramsInvalidationTimer.isStarted())) {
            return;
        }
        Long localLong = this.mCallbacks.findCachedChannelId(paramLong);
        if (localLong != null) {
            invalidateChannelPrograms(localLong.longValue());
        }
        scheduleProgramsDataLoad(paramLong);
    }

    private void invalidateWatchNextPrograms() {
        if (this.mWatchNextInvalidationTimer == null) {
            this.mWatchNextInvalidationTimer = ExtendableTimer.obtain();
            this.mWatchNextInvalidationTimer.setTimeout(3000L);
            this.mWatchNextInvalidationTimer.setMaximumTimeout(10000L);
            this.mWatchNextInvalidationTimer.setId(-3000L);
            this.mWatchNextInvalidationTimer.setListener(getTimerListener());
        }
        this.mWatchNextInvalidationTimer.start();
    }

    private void loadProgramData() {
        if (this.mProgramsDataLoadTimer != null) {
            this.mProgramsDataLoadTimer.cancel();
        }
        if (this.mProgramsToLoadData.isEmpty()) {
            return;
        }
        String str = buildProgramSelection(this.mProgramsToLoadData);
        this.mProgramsToLoadData.clear();
        if (this.mBackgroundTaskCallbacks == null) {
            this.mBackgroundTaskCallbacks = new BackgroundTaskCallbacks();
        }
        // todo find this "uri" and manually insert it?
        // DataLoadingBackgroundTask.obtain(this.mContext).setUri(TvContract.PreviewPrograms.CONTENT_URI).setProjection(PROJECTION).setSelection(str).setCallbacks(this.mBackgroundTaskCallbacks).execute();
    }

    private void scheduleProgramsDataLoad(long paramLong) {
        this.mProgramsToLoadData.add(paramLong);
        if (this.mProgramsToLoadData.size() >= 100) {
            loadProgramData();
            return;
        }
        if (this.mProgramsDataLoadTimer == null) {
            this.mProgramsDataLoadTimer = new ExtendableTimer();
            this.mProgramsDataLoadTimer.setTimeout(1000L);
            this.mProgramsDataLoadTimer.setMaximumTimeout(5000L);
            this.mProgramsDataLoadTimer.setId(-4000L);
            this.mProgramsDataLoadTimer.setListener(getTimerListener());
        }
        this.mProgramsDataLoadTimer.start();
    }

    void register() {
        if (this.mRegistered) {
            return;
        }
        ContentResolver localContentResolver = this.mContext.getContentResolver();
        localContentResolver.registerContentObserver(TvContract.Channels.CONTENT_URI, true, this.mContentObserver);
        // todo localContentResolver.registerContentObserver(TvContract.PreviewPrograms.CONTENT_URI, true, this.mContentObserver);
        //localContentResolver.registerContentObserver(TvContract.WatchNextPrograms.CONTENT_URI, true, this.mContentObserver);
        this.mRegistered = true;
    }

    void unregister() {
        if (!this.mRegistered) {
            return;
        }
        this.mContext.getContentResolver().unregisterContentObserver(this.mContentObserver);
        this.mRegistered = false;
    }

    private class BackgroundTaskCallbacks
            implements DataLoadingBackgroundTask.Callbacks {
        private BackgroundTaskCallbacks() {
        }

        @MainThread
        public void onTaskCancelled(DataLoadingBackgroundTask paramDataLoadingBackgroundTask) {
        }

        @MainThread
        public void onTaskCompleted(DataLoadingBackgroundTask paramDataLoadingBackgroundTask) {
            Cursor localCursor = paramDataLoadingBackgroundTask.getResult();
            if (localCursor == null) {
                Log.e("DataSourceObserver", "Error loading program data with task: " + paramDataLoadingBackgroundTask);
            } else {
                while (localCursor.moveToNext()) {
                    long l1 = localCursor.getLong(0);
                    long l2 = localCursor.getLong(1);
                    Long val = DataSourceObserver.this.mCallbacks.findCachedChannelId(l1);
                    if ((val == null) || (val != l2)) {
                        DataSourceObserver.this.invalidateChannelProgramsIfCached(l2);
                    }
                }
            }
        }

        @MainThread
        public void onTaskFailed(DataLoadingBackgroundTask paramDataLoadingBackgroundTask, Throwable paramThrowable) {
            Log.e("DataSourceObserver", "onTaskFailed: " + paramDataLoadingBackgroundTask + ", ex: " + paramThrowable);
        }

        @WorkerThread
        public void onTaskPostProcess(DataLoadingBackgroundTask paramDataLoadingBackgroundTask) {
        }
    }

    static abstract interface Callbacks {
        public abstract boolean areProgramsCached(long paramLong);

        public abstract Long findCachedChannelId(long paramLong);

        public abstract void invalidateAllChannels();

        public abstract void invalidateAllPrograms();

        public abstract void invalidateChannelLogo(long paramLong);

        public abstract void invalidateChannelPrograms(long paramLong);

        public abstract void invalidateWatchNextPrograms();
    }

    private class ExtendableTimerListener
            implements ExtendableTimer.Listener {
        private ExtendableTimerListener() {
        }

        public void onTimerFired(ExtendableTimer paramExtendableTimer) {
            long l = paramExtendableTimer.getId();
            if (l == -1000L) {
                DataSourceObserver.this.mCallbacks.invalidateAllChannels();
                return;
            }
            if (l == -2000L) {
                DataSourceObserver.this.mCallbacks.invalidateAllPrograms();
                return;
            }
            if (l == -3000L) {
                DataSourceObserver.this.mCallbacks.invalidateWatchNextPrograms();
                return;
            }
            if (l == -4000L) {
                DataSourceObserver.this.loadProgramData();
                return;
            }
            if (DataSourceObserver.this.mChannelProgramsInvalidationTimers.containsKey(Long.valueOf(l))) {
                DataSourceObserver.this.mChannelProgramsInvalidationTimers.remove(Long.valueOf(l));
                paramExtendableTimer.recycle();
                DataSourceObserver.this.mCallbacks.invalidateChannelPrograms(l);
                return;
            }
            Log.w("DataSourceObserver", "Unknown timer ID: " + l);
        }
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/data/DataSourceObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */