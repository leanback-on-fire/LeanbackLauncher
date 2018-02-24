package com.google.android.tvlauncher.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.media.tv.TvContract.Channels;
import android.media.tv.TvContract.PreviewPrograms;
import android.media.tv.TvContract.WatchNextPrograms;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEvents;
import com.google.android.tvlauncher.analytics.UserActionEvent;
import com.google.android.tvlauncher.model.Channel;
import com.google.android.tvlauncher.model.ChannelPackage;
import com.google.android.tvlauncher.model.HomeChannel;
import com.google.android.tvlauncher.model.Program;
import com.google.android.tvlauncher.notifications.NotificationsContract;
import com.google.android.tvlauncher.util.LauncherSharedConstants;
import com.google.android.tvlauncher.util.Partner;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

public class TvDataManager implements DataLoadingBackgroundTask.Callbacks {
    private static final String ALL_PREVIEW_CHANNELS_SELECTION = "type='TYPE_PREVIEW'";
    private static final boolean DEBUG = false;
    public static final String ENABLE_PREVIEW_AUDIO_KEY = "enable_preview_audio_key";
    private static final String HOME_CHANNELS_SELECTION = "browsable=1 AND type='TYPE_PREVIEW'";
    private static final int MAX_PROGRAMS_CHANNELS_CACHED = 15;
    private static final int MAX_WATCH_NEXT_PROGRAMS = 1000;
    public static final String PREVIEW_VIDEO_PREF_FILE_NAME = "com.google.android.tvlauncher.data.TvDataManager.PREVIEW_VIDEO_PREF_FILE_NAME";
    private static final String PROGRAMS_BY_CHANNEL_ID_SELECTION = "channel_id=? AND browsable=1";
    private static final String PROMO_CHANNEL_SELECTION = "package_name=?";
    public static final String SHOW_PREVIEW_VIDEO_KEY = "show_preview_video_key";
    public static final String SHOW_WATCH_NEXT_ROW_KEY = "show_watch_next_row_key";
    private static final String TAG = "TvDataManager";
    private static final int TASK_ALL_PREVIEW_CHANNELS = 2;
    private static final int TASK_CHANNEL_PROGRAMS = 1;
    private static final int TASK_HOME_CHANNELS = 0;
    private static final int TASK_PROMO_CHANNEL = 3;
    private static final int TASK_WATCH_NEXT_CACHE = 5;
    private static final int TASK_WATCH_NEXT_PROGRAMS = 4;
    public static final String WATCH_NEXT_PACKAGE_KEY_PREFIX = "watch_next_package_key_prefix";
    public static final String WATCH_NEXT_PREF_FILE_NAME = "com.google.android.tvlauncher.data.TvDataManager.WATCH_NEXT_PREF_FILE_NAME";
    public static final int WATCH_NEXT_REQUERY_INTERVAL_MILLIS = 600000;
    private static final String WATCH_NEXT_SELECTION = "browsable=1 AND last_engagement_time_utc_millis<=?";
    @SuppressLint({"StaticFieldLeak"})
    private static TvDataManager sInstance;
    private DataLoadingBackgroundTask mAllPreviewChannelsBackgroundTask;
    private Queue<Long> mCachedProgramsChannelOrder = new ArrayDeque();
    @SuppressLint({"UseSparseArrays"})
    private Map<Long, Uri> mChannelLogoUris = new HashMap();
    private ChannelOrderManager mChannelOrderManager;
    @SuppressLint({"UseSparseArrays"})
    private Map<Long, ProgramsDataBuffer> mChannelPrograms = new HashMap();
    @SuppressLint({"UseSparseArrays"})
    private Map<Long, DataLoadingBackgroundTask> mChannelProgramsBackgroundTasks = new HashMap();
    @SuppressLint({"UseSparseArrays"})
    private Map<Long, List<ChannelProgramsObserver>> mChannelProgramsObservers = new HashMap();
    private final Context mContext;
    private final DataSourceObserver mDataSourceObserver;
    private final Handler mHandler = new Handler();
    private List<HomeChannel> mHomeChannels;
    private DataLoadingBackgroundTask mHomeChannelsBackgroundTask;
    private final Comparator<HomeChannel> mHomeChannelsComparator = new Comparator<HomeChannel>() {
        public int compare(@NonNull HomeChannel channelLeft, @NonNull HomeChannel channelRight) {
            if (channelLeft == channelRight) {
                return 0;
            }
            Integer positionLeft = TvDataManager.this.mChannelOrderManager.getChannelPosition(channelLeft.getId());
            Integer positionRight = TvDataManager.this.mChannelOrderManager.getChannelPosition(channelRight.getId());
            if (positionLeft != null && positionRight != null) {
                return Integer.compare(positionLeft.intValue(), positionRight.intValue());
            }
            if (positionLeft == null && positionRight == null) {
                if (channelLeft.getDisplayName() == null && channelRight.getDisplayName() == null) {
                    return 0;
                }
                if (channelLeft.getDisplayName() == null) {
                    return 1;
                }
                if (channelRight.getDisplayName() == null) {
                    return -1;
                }
                return channelLeft.getDisplayName().compareToIgnoreCase(channelRight.getDisplayName());
            } else if (positionLeft == null) {
                return 1;
            } else {
                return -1;
            }
        }
    };
    private List<HomeChannelsObserver> mHomeChannelsObservers = new LinkedList();
    private boolean mHomeChannelsStale = true;
    private Map<String, List<Channel>> mPackageChannels;
    private List<PackageChannelsObserver> mPackageChannelsObservers = new LinkedList();
    private List<ChannelPackage> mPackagesWithChannels;
    private List<PackagesWithChannelsObserver> mPackagesWithChannelsObservers = new LinkedList();
    @SuppressLint({"UseSparseArrays"})
    private Map<Long, Long> mProgramChannelIds = Collections.synchronizedMap(new HashMap());
    private Channel mPromoChannel;
    private DataLoadingBackgroundTask mPromoChannelBackgroundTask;
    private boolean mPromoChannelLoaded;
    private List<PromoChannelObserver> mPromoChannelObservers = new LinkedList();
    private Set<Long> mStaleProgramsChannels = new HashSet();
    private DataLoadingBackgroundTask mWatchNextCacheBackgroundTask;
    private final OnSharedPreferenceChangeListener mWatchNextPrefListener = new OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.startsWith(TvDataManager.WATCH_NEXT_PACKAGE_KEY_PREFIX)) {
                TvDataManager.this.mWatchNextSelection = TvDataManager.this.buildWatchNextSelection(sharedPreferences);
                TvDataManager.this.loadWatchNextProgramData();
            }
        }
    };
    private WatchNextDataBuffer mWatchNextPrograms;
    private DataLoadingBackgroundTask mWatchNextProgramsBackgroundTask;
    private Set<String> mWatchNextProgramsCache = new HashSet();
    private List<WatchNextProgramsObserver> mWatchNextProgramsObservers = new LinkedList();
    private boolean mWatchNextProgramsStale = true;
    private String mWatchNextSelection;

    private class DataSourceObserverCallbacks implements DataSourceObserver.Callbacks {
        private DataSourceObserverCallbacks() {
        }

        public void invalidateAllChannels() {
            if (TvDataManager.this.mHomeChannelsObservers.size() > 0) {
                TvDataManager.this.loadHomeChannelDataInternal();
            } else {
                TvDataManager.this.mHomeChannelsStale = true;
            }
            if (TvDataManager.this.mPackagesWithChannelsObservers.size() > 0 || TvDataManager.this.mPackageChannelsObservers.size() > 0) {
                TvDataManager.this.loadAllPreviewChannelsData();
            }
            if (TvDataManager.this.mPromoChannelObservers.size() > 0 && Partner.get(TvDataManager.this.mContext).getAppsPromotionRowPackage() != null) {
                TvDataManager.this.loadPromoChannelData();
            }
        }

        public void invalidateChannelLogo(long channelId) {
            TvDataManager.this.mChannelLogoUris.remove(Long.valueOf(channelId));
        }

        private void reloadProgramsOrMarkStale(Long channelId) {
            if (TvDataManager.this.mChannelProgramsObservers.containsKey(channelId)) {
                TvDataManager.this.loadChannelProgramData(channelId.longValue());
            } else {
                TvDataManager.this.mStaleProgramsChannels.add(channelId);
            }
        }

        public void invalidateAllPrograms() {
            for (Long channelId : TvDataManager.this.mChannelPrograms.keySet()) {
                reloadProgramsOrMarkStale(channelId);
            }
        }

        public void invalidateChannelPrograms(long channelId) {
            if (TvDataManager.this.mChannelPrograms.containsKey(Long.valueOf(channelId))) {
                reloadProgramsOrMarkStale(Long.valueOf(channelId));
            }
        }

        public void invalidateWatchNextPrograms() {
            if (TvDataManager.this.mWatchNextProgramsObservers.size() > 0) {
                TvDataManager.this.loadWatchNextProgramDataInternal();
            } else {
                TvDataManager.this.mWatchNextProgramsStale = true;
            }
        }

        public Long findCachedChannelId(long programId) {
            return (Long) TvDataManager.this.mProgramChannelIds.get(Long.valueOf(programId));
        }

        public boolean areProgramsCached(long channelId) {
            return TvDataManager.this.mChannelPrograms.containsKey(Long.valueOf(channelId));
        }
    }

    public static TvDataManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TvDataManager(context);
        }
        return sInstance;
    }

    private TvDataManager(Context context) {
        this.mContext = context.getApplicationContext();
        this.mDataSourceObserver = new DataSourceObserver(this.mContext, new DataSourceObserverCallbacks());
        SharedPreferences preferences = context.getSharedPreferences(WATCH_NEXT_PREF_FILE_NAME, 0);
        preferences.registerOnSharedPreferenceChangeListener(this.mWatchNextPrefListener);
        this.mWatchNextSelection = buildWatchNextSelection(preferences);
        loadAllWatchNextProgramDataIntoCache();
    }

    private void logObserversState() {
        int channelsObserved = this.mChannelProgramsObservers.size();
        int channelObservers = 0;
        for (List<ChannelProgramsObserver> observers : this.mChannelProgramsObservers.values()) {
            channelObservers += observers.size();
        }
        Log.d(TAG, "Observers: \nmHomeChannelsObservers.size()=" + this.mHomeChannelsObservers.size() + "\nmChannelProgramsObservers.size()=" + channelsObserved + " (total: " + channelObservers + ")" + "\nmPackagesWithChannelsObservers.size()=" + this.mPackagesWithChannelsObservers.size() + "\nmPackageChannelsObservers.size()=" + this.mPackageChannelsObservers.size() + "\nmPromoChannelObservers.size()=" + this.mPromoChannelObservers.size() + "\nmWatchNextProgramsObservers.size()=" + this.mWatchNextProgramsObservers.size());
    }

    private void logCachesState() {
        Object valueOf;
        String str = TAG;
        StringBuilder append = new StringBuilder().append("Cache: \nmHomeChannels=").append(this.mHomeChannels != null ? Integer.valueOf(this.mHomeChannels.size()) : "null").append("\nmChannelPrograms=").append(this.mChannelPrograms != null ? Integer.valueOf(this.mChannelPrograms.size()) : "null").append("\nmPackagesWithChannels=").append(this.mPackagesWithChannels != null ? Integer.valueOf(this.mPackagesWithChannels.size()) : "null").append("\nmPackageChannels=").append(this.mPackageChannels != null ? Integer.valueOf(this.mPackageChannels.size()) : "null").append("\nmCachedProgramsChannelOrder: ").append(this.mCachedProgramsChannelOrder).append("\nmStaleProgramsChannels: ").append(this.mStaleProgramsChannels).append("\nmWatchNextPrograms: ");
        if (this.mWatchNextPrograms != null) {
            valueOf = Integer.valueOf(this.mWatchNextPrograms.getCount());
        } else {
            valueOf = "null";
        }
        Log.d(str, append.append(valueOf).toString());
    }

    private void registerDataSourceObserver() {
        this.mDataSourceObserver.register();
    }

    private void unregisterDataSourceObserverIfNoObservers() {
        if (this.mHomeChannelsObservers.size() == 0 && this.mChannelProgramsObservers.size() == 0 && this.mPackagesWithChannelsObservers.size() == 0 && this.mPackageChannelsObservers.size() == 0 && this.mPromoChannelObservers.size() == 0 && this.mWatchNextProgramsObservers.size() == 0) {
            this.mHomeChannelsStale = true;
            this.mChannelLogoUris.clear();
            for (Long channelId : this.mChannelPrograms.keySet()) {
                this.mStaleProgramsChannels.add(channelId);
            }
            this.mWatchNextProgramsStale = true;
            this.mDataSourceObserver.unregister();
        }
    }

    public void registerHomeChannelsObserver(HomeChannelsObserver observer) {
        if (!this.mHomeChannelsObservers.contains(observer)) {
            this.mHomeChannelsObservers.add(observer);
        }
        registerDataSourceObserver();
    }

    public void unregisterHomeChannelsObserver(HomeChannelsObserver observer) {
        if (this.mHomeChannelsBackgroundTask != null) {
            this.mHomeChannelsBackgroundTask.cancel();
            this.mHomeChannelsBackgroundTask = null;
        }
        this.mHomeChannelsObservers.remove(observer);
        unregisterDataSourceObserverIfNoObservers();
    }

    public void registerChannelProgramsObserver(long channelId, ChannelProgramsObserver observer) {
        addToMultimap(this.mChannelProgramsObservers, Long.valueOf(channelId), observer);
        registerDataSourceObserver();
    }

    public void unregisterChannelProgramsObserver(long channelId, ChannelProgramsObserver observer) {
        DataLoadingBackgroundTask task = (DataLoadingBackgroundTask) this.mChannelProgramsBackgroundTasks.remove(Long.valueOf(channelId));
        if (task != null) {
            task.cancel();
        }
        removeFromMultimap(this.mChannelProgramsObservers, Long.valueOf(channelId), observer);
        unregisterDataSourceObserverIfNoObservers();
    }

    public void registerPackagesWithChannelsObserver(PackagesWithChannelsObserver observer) {
        if (!this.mPackagesWithChannelsObservers.contains(observer)) {
            this.mPackagesWithChannelsObservers.add(observer);
        }
        registerDataSourceObserver();
    }

    public void unregisterPackagesWithChannelsObserver(PackagesWithChannelsObserver observer) {
        this.mPackagesWithChannelsObservers.remove(observer);
        clearPackageChannelsCacheAndCancelTasksIfNoObservers();
        unregisterDataSourceObserverIfNoObservers();
    }

    public void registerPackageChannelsObserver(PackageChannelsObserver observer) {
        if (!this.mPackageChannelsObservers.contains(observer)) {
            this.mPackageChannelsObservers.add(observer);
        }
        registerDataSourceObserver();
    }

    public void unregisterPackageChannelsObserver(PackageChannelsObserver observer) {
        this.mPackageChannelsObservers.remove(observer);
        clearPackageChannelsCacheAndCancelTasksIfNoObservers();
        unregisterDataSourceObserverIfNoObservers();
    }

    private void clearPackageChannelsCacheAndCancelTasksIfNoObservers() {
        if (this.mPackagesWithChannelsObservers.size() == 0 && this.mPackageChannelsObservers.size() == 0) {
            if (this.mAllPreviewChannelsBackgroundTask != null) {
                this.mAllPreviewChannelsBackgroundTask.cancel();
                this.mAllPreviewChannelsBackgroundTask = null;
            }
            this.mPackagesWithChannels = null;
            this.mPackageChannels = null;
        }
    }

    public void registerPromoChannelObserver(PromoChannelObserver observer) {
        if (!this.mPromoChannelObservers.contains(observer)) {
            this.mPromoChannelObservers.add(observer);
        }
        registerDataSourceObserver();
    }

    public void unregisterPromoChannelObserver(PromoChannelObserver observer) {
        if (this.mPromoChannelBackgroundTask != null) {
            this.mPromoChannelBackgroundTask.cancel();
            this.mPromoChannelBackgroundTask = null;
        }
        this.mPromoChannelObservers.remove(observer);
        if (this.mPromoChannelObservers.size() == 0) {
            this.mPromoChannel = null;
            this.mPromoChannelLoaded = false;
        }
        unregisterDataSourceObserverIfNoObservers();
    }

    public void registerWatchNextProgramsObserver(WatchNextProgramsObserver observer) {
        if (!this.mWatchNextProgramsObservers.contains(observer)) {
            this.mWatchNextProgramsObservers.add(observer);
        }
        registerDataSourceObserver();
    }

    public void unregisterWatchNextProgramsObserver(WatchNextProgramsObserver observer) {
        if (this.mWatchNextProgramsBackgroundTask != null) {
            this.mWatchNextProgramsBackgroundTask.cancel();
            this.mWatchNextProgramsBackgroundTask = null;
        }
        this.mWatchNextProgramsObservers.remove(observer);
        unregisterDataSourceObserverIfNoObservers();
    }

    public boolean isInWatchNext(String contentId, String packageName) {
        if (contentId == null || packageName == null) {
            return false;
        }
        return this.mWatchNextProgramsCache.contains(contentId.concat(packageName));
    }

    private void updateWatchNextCache(Cursor cursor) {
        this.mWatchNextProgramsCache.clear();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                addProgramToWatchNextCache(cursor.getString(27), cursor.getString(33));
            } while (cursor.moveToNext());
        }
    }

    public void refreshWatchNextOffset() {
        if (this.mWatchNextPrograms != null && this.mWatchNextPrograms.refresh()) {
            notifyWatchNextProgramsChange();
        }
    }

    public void reloadWatchNextCache() {
        loadAllWatchNextProgramDataIntoCache();
    }

    public void removeProgramFromWatchNextCache(String contentId, String packageName) {
        if (contentId != null && packageName != null) {
            this.mWatchNextProgramsCache.remove(contentId.concat(packageName));
        }
    }

    public void addProgramToWatchNextCache(String contentId, String packageName) {
        if (contentId != null && packageName != null) {
            this.mWatchNextProgramsCache.add(contentId.concat(packageName));
        }
    }

    private static <K, V> void addToMultimap(Map<K, List<V>> multimap, K key, V item) {
        List<V> list = (List) multimap.get(key);
        if (list == null) {
            list = new LinkedList();
            list.add(item);
            multimap.put(key, list);
        } else if (!list.contains(item)) {
            list.add(item);
        }
    }

    private static <K, V> void removeFromMultimap(Map<K, List<V>> multimap, K key, V item) {
        List<V> list = (List) multimap.get(key);
        if (list != null) {
            list.remove(item);
            if (list.size() == 0) {
                multimap.remove(key);
            }
        }
    }

    private void notifyHomeChannelsChange() {
        for (HomeChannelsObserver observer : this.mHomeChannelsObservers) {
            observer.onChannelsChange();
        }
    }

    private void notifyChannelProgramsChange(long channelId) {
        List<ChannelProgramsObserver> observers = (List) this.mChannelProgramsObservers.get(Long.valueOf(channelId));
        if (observers != null) {
            for (ChannelProgramsObserver observer : observers) {
                observer.onProgramsChange(channelId);
            }
        }
    }

    private void notifyPackagesWithChannelsChange() {
        for (PackagesWithChannelsObserver observer : this.mPackagesWithChannelsObservers) {
            observer.onPackagesChange();
        }
    }

    private void notifyPackageChannelsChange() {
        for (PackageChannelsObserver observer : this.mPackageChannelsObservers) {
            observer.onChannelsChange();
        }
    }

    private void notifyPromoChannelChange() {
        for (PromoChannelObserver observer : this.mPromoChannelObservers) {
            observer.onChannelChange();
        }
    }

    private void notifyWatchNextProgramsChange() {
        for (WatchNextProgramsObserver observer : this.mWatchNextProgramsObservers) {
            observer.onProgramsChange();
        }
    }

    public boolean isHomeChannelDataLoaded() {
        return this.mHomeChannels != null;
    }

    public boolean isHomeChannelDataStale() {
        return this.mHomeChannelsStale;
    }

    public int getHomeChannelCount() {
        return this.mHomeChannels != null ? this.mHomeChannels.size() : -1;
    }

    public HomeChannel getHomeChannel(int position) {
        return (HomeChannel) this.mHomeChannels.get(position);
    }

    public void loadHomeChannelData() {
        loadHomeChannelDataInternal();
    }

    public void removeHomeChannel(long channelId) {
        Integer position = this.mChannelOrderManager.getChannelPosition(channelId);
        if (position != null) {
            this.mHomeChannels.remove(position.intValue());
            this.mChannelOrderManager.refreshChannelPositions();
        }
        setChannelBrowsable(channelId, false);
    }

    public Uri getChannelLogoUri(Long channelId) {
        Uri uri = (Uri) this.mChannelLogoUris.get(channelId);
        if (uri != null) {
            return uri;
        }
        uri = TvContract.buildChannelLogoUri(channelId.longValue()).buildUpon().appendQueryParameter("t", String.valueOf(System.currentTimeMillis())).build();
        this.mChannelLogoUris.put(channelId, uri);
        return uri;
    }

    public boolean isProgramDataLoaded(long channelId) {
        return this.mChannelPrograms.containsKey(Long.valueOf(channelId));
    }

    public boolean isProgramDataStale(long channelId) {
        return this.mStaleProgramsChannels.contains(Long.valueOf(channelId));
    }

    public int getProgramCount(long channelId) {
        return this.mChannelPrograms.containsKey(Long.valueOf(channelId)) ? ((ProgramsDataBuffer) this.mChannelPrograms.get(Long.valueOf(channelId))).getCount() : -1;
    }

    public Program getProgram(long channelId, int position) {
        if (position < 0) {
            throw new IllegalArgumentException("Position must be positive");
        }
        ProgramsDataBuffer programs = (ProgramsDataBuffer) this.mChannelPrograms.get(Long.valueOf(channelId));
        if (programs == null || position < programs.getCount()) {
            return programs != null ? programs.get(position) : null;
        } else {
            throw new IllegalArgumentException("Position [" + position + "] is out of bounds [0, " + (programs.getCount() - 1) + "]");
        }
    }

    public void loadProgramData(long channelId) {
        loadChannelProgramData(channelId);
    }

    public void pruneChannelProgramsCache() {
        Iterator<Entry<Long, ProgramsDataBuffer>> it = this.mChannelPrograms.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Long, ProgramsDataBuffer> entry = (Entry) it.next();
            Long channelId = (Long) entry.getKey();
            if (!this.mChannelProgramsObservers.containsKey(channelId)) {
                it.remove();
                cleanupAfterChannelProgramsRemovalFromCache((ProgramsDataBuffer) entry.getValue(), channelId);
            }
        }
    }

    private void cleanupAfterChannelProgramsRemovalFromCache(ProgramsDataBuffer buffer, Long channelId) {
        if (buffer != null) {
            buffer.release();
        }
        this.mStaleProgramsChannels.remove(channelId);
        this.mCachedProgramsChannelOrder.remove(channelId);
    }

    public boolean isPackagesWithChannelsDataLoaded() {
        return this.mPackagesWithChannels != null;
    }

    public List<ChannelPackage> getPackagesWithChannels() {
        return this.mPackagesWithChannels;
    }

    public void loadPackagesWithChannelsData() {
        loadAllPreviewChannelsData();
    }

    public boolean isPackageChannelDataLoaded(String packageName) {
        return this.mPackageChannels != null && this.mPackageChannels.containsKey(packageName);
    }

    public List<Channel> getPackageChannels(String packageName) {
        return (List) this.mPackageChannels.get(packageName);
    }

    public void loadPackageChannelsData(String packageName) {
        loadAllPreviewChannelsData();
    }

    public boolean isPromoChannelLoaded() {
        return this.mPromoChannelLoaded;
    }

    public Channel getPromoChannel() {
        return this.mPromoChannel;
    }

    public void loadPromoChannel() {
        loadPromoChannelData();
    }

    public boolean isWatchNextProgramsDataLoaded() {
        return this.mWatchNextPrograms != null;
    }

    public boolean isWatchNextProgramsDataStale() {
        return this.mWatchNextProgramsStale;
    }

    public Program getWatchNextProgram(int position) {
        if (position < 0) {
            throw new IllegalArgumentException("Position must be positive");
        } else if (this.mWatchNextPrograms == null || position < this.mWatchNextPrograms.getCount()) {
            return this.mWatchNextPrograms != null ? this.mWatchNextPrograms.get(position) : null;
        } else {
            throw new IllegalArgumentException("Position [" + position + "] is out of bounds [0, " + (this.mWatchNextPrograms.getCount() - 1) + "]");
        }
    }

    public int getWatchNextProgramsCount() {
        return this.mWatchNextPrograms != null ? this.mWatchNextPrograms.getCount() : 0;
    }

    public void loadWatchNextProgramData() {
        loadWatchNextProgramDataInternal();
    }

    private void loadHomeChannelDataInternal() {
        if (this.mHomeChannelsBackgroundTask == null) {
            DataLoadingBackgroundTask task = DataLoadingBackgroundTask.obtain(this.mContext).setUri(Channels.CONTENT_URI).setProjection(HomeChannel.PROJECTION).setSelection(HOME_CHANNELS_SELECTION).setTag(0).setCallbacks(this);
            startTrackingTask(task);
            task.execute();
        }
    }

    private void removeOneChannelProgramsIfCacheTooBig() {
        if (this.mChannelPrograms.size() >= 15) {
            for (Long cachedChannelId : this.mCachedProgramsChannelOrder) {
                if (!this.mChannelProgramsObservers.containsKey(cachedChannelId)) {
                    cleanupAfterChannelProgramsRemovalFromCache((ProgramsDataBuffer) this.mChannelPrograms.remove(cachedChannelId), cachedChannelId);
                    return;
                }
            }
        }
    }

    private void loadChannelProgramData(long channelId) {
        if (!this.mChannelProgramsBackgroundTasks.containsKey(Long.valueOf(channelId))) {
            if (!this.mChannelPrograms.containsKey(Long.valueOf(channelId))) {
                removeOneChannelProgramsIfCacheTooBig();
            }
            DataLoadingBackgroundTask task = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                task = DataLoadingBackgroundTask.obtain(this.mContext).setUri(PreviewPrograms.CONTENT_URI).setProjection(Program.PROJECTION).setSelection(PROGRAMS_BY_CHANNEL_ID_SELECTION).setSelectionArg(String.valueOf(channelId)).setSortOrder("weight DESC").setExtraParam(Long.valueOf(channelId)).setTag(1).setCallbacks(this);
                startTrackingTask(task);
                task.execute();
            }

        }
    }

    private void loadAllPreviewChannelsData() {
        if (this.mAllPreviewChannelsBackgroundTask == null) {
            DataLoadingBackgroundTask task = DataLoadingBackgroundTask.obtain(this.mContext).setUri(Channels.CONTENT_URI).setProjection(Channel.PROJECTION).setSelection(ALL_PREVIEW_CHANNELS_SELECTION).setTag(2).setCallbacks(this);
            startTrackingTask(task);
            task.execute();
        }
    }

    private void loadPromoChannelData() {
        if (this.mPromoChannelBackgroundTask == null) {
            if (Partner.get(this.mContext).getAppsPromotionRowPackage() == null) {
                Log.e(TAG, "Promotion row package is not configured");
                this.mHandler.post(new Runnable() {
                    public void run() {
                        TvDataManager.this.setPromoChannelAndNotify(null);
                    }
                });
                return;
            }
            DataLoadingBackgroundTask task = DataLoadingBackgroundTask.obtain(this.mContext).setUri(Channels.CONTENT_URI).setProjection(Channel.PROJECTION).setSelection(PROMO_CHANNEL_SELECTION).setSelectionArg(Partner.get(this.mContext).getAppsPromotionRowPackage()).setTag(3).setCallbacks(this);
            startTrackingTask(task);
            task.execute();
        }
    }

    private void loadWatchNextProgramDataInternal() {
        if (this.mWatchNextProgramsBackgroundTask == null) {
            // DataLoadingBackgroundTask task = DataLoadingBackgroundTask.obtain(this.mContext).setUri(WatchNextPrograms.CONTENT_URI).setProjection(Program.WATCH_NEXT_PROJECTION).setSelection(this.mWatchNextSelection).setSelectionArg(String.valueOf(System.currentTimeMillis() + 600000)).setSortOrder("last_engagement_time_utc_millis DESC LIMIT 1000").setTag(4).setCallbacks(this);
            //  startTrackingTask(task);
            //  task.execute();
        }
    }

    private void loadAllWatchNextProgramDataIntoCache() {
        if (this.mWatchNextCacheBackgroundTask == null) {
            //   DataLoadingBackgroundTask task = DataLoadingBackgroundTask.obtain(this.mContext).setUri(WatchNextPrograms.CONTENT_URI).setProjection(Program.WATCH_NEXT_PROJECTION).setSelection("browsable=1").setTag(5).setCallbacks(this);
            //   startTrackingTask(task);
            //  task.execute();
        }
    }

    private String buildWatchNextSelection(SharedPreferences prefs) {
        StringBuilder sb = new StringBuilder(" NOT IN (");
        boolean hasPackages = false;
        for (String key : prefs.getAll().keySet()) {
            if (key.startsWith(WATCH_NEXT_PACKAGE_KEY_PREFIX)) {
                sb.append("'").append(key.substring(WATCH_NEXT_PACKAGE_KEY_PREFIX.length())).append("',");
                hasPackages = true;
            }
        }
        if (!hasPackages) {
            return WATCH_NEXT_SELECTION;
        }
        sb.setLength(sb.length() - 1);
        sb.append(")");
        return "browsable=1 AND last_engagement_time_utc_millis<=? AND package_name" + sb.toString();
    }

    private void startTrackingTask(DataLoadingBackgroundTask task) {
        if (task.getTag() == 0) {
            this.mHomeChannelsBackgroundTask = task;
        } else if (task.getTag() == 1) {
            this.mChannelProgramsBackgroundTasks.put((Long) task.getExtraParam(), task);
        } else if (task.getTag() == 2) {
            this.mAllPreviewChannelsBackgroundTask = task;
        } else if (task.getTag() == 3) {
            this.mPromoChannelBackgroundTask = task;
        } else if (task.getTag() == 4) {
            this.mWatchNextProgramsBackgroundTask = task;
        } else if (task.getTag() == 5) {
            this.mWatchNextCacheBackgroundTask = task;
        }
    }

    private void stopTrackingTask(DataLoadingBackgroundTask task) {
        if (task.getTag() == 0) {
            this.mHomeChannelsBackgroundTask = null;
        } else if (task.getTag() == 1) {
            this.mChannelProgramsBackgroundTasks.remove((Long) task.getExtraParam());
        } else if (task.getTag() == 2) {
            this.mAllPreviewChannelsBackgroundTask = null;
        } else if (task.getTag() == 3) {
            this.mPromoChannelBackgroundTask = null;
        } else if (task.getTag() == 4) {
            this.mWatchNextProgramsBackgroundTask = null;
        } else if (task.getTag() == 5) {
            this.mWatchNextCacheBackgroundTask = null;
        }
    }

    @WorkerThread
    public void onTaskPostProcess(DataLoadingBackgroundTask task) {
        Cursor cursor;
        if (task.getTag() == 0) {
            List<HomeChannel> channels;
            cursor = task.getResult();
            if (cursor != null) {
                channels = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    channels.add(HomeChannel.fromCursor(cursor));
                }
                cursor.close();
            } else {
                channels = Collections.emptyList();
                Log.e(TAG, "error loading home channels, cursor is null");
            }
            task.setExtraResult(channels);
        } else if (task.getTag() == 1) {
            cursor = task.getResult();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    this.mProgramChannelIds.put(Long.valueOf(cursor.getLong(0)), Long.valueOf(cursor.getLong(1)));
                }
                return;
            }
            Log.e(TAG, "error loading programs for channel " + task.getExtraParam() + ", cursor is null");
        } else if (task.getTag() == 2) {
            List<ChannelPackage> packagesWithChannels;
            Map<String, List<Channel>> packageChannels;
            cursor = task.getResult();
            if (cursor != null) {
                packagesWithChannels = new ArrayList(cursor.getCount());
                packageChannels = new HashMap(cursor.getCount());
                while (cursor.moveToNext()) {
                    Channel channel = Channel.fromCursor(cursor);
                    addToMultimap(packageChannels, channel.getPackageName(), channel);
                }
                cursor.close();
                for (Entry<String, List<Channel>> entry : packageChannels.entrySet()) {
                    packagesWithChannels.add(new ChannelPackage((String) entry.getKey(), ((List) entry.getValue()).size()));
                }
            } else {
                packagesWithChannels = Collections.emptyList();
                packageChannels = Collections.emptyMap();
                Log.e(TAG, "error loading all preview channels, cursor is null");
            }
            task.setExtraResult(new Object[]{packagesWithChannels, packageChannels});
        } else if (task.getTag() == 3) {
            Channel channel = null;
            cursor = task.getResult();
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    channel = Channel.fromCursor(cursor);
                }
                cursor.close();
            } else {
                Log.e(TAG, "error loading promo channel, cursor is null");
            }
            task.setExtraResult(channel);
        }
    }

    @MainThread
    public void onTaskCompleted(DataLoadingBackgroundTask task) {
        if (task.getTag() == 0) {
            if (this.mChannelOrderManager == null) {
                this.mChannelOrderManager = new ChannelOrderManager(this.mContext);
                this.mChannelOrderManager.setChannelsObservers(this.mHomeChannelsObservers);
            }
            this.mHomeChannels = (List) task.getExtraResult();
            this.mHomeChannelsStale = false;
            Collections.sort(this.mHomeChannels, this.mHomeChannelsComparator);
            this.mChannelOrderManager.setChannels(this.mHomeChannels);
            this.mChannelOrderManager.refreshChannelPositions();
            notifyHomeChannelsChange();
        } else if (task.getTag() == 1) {
            if (task.getResult() != null) {
                Long channelId = (Long) task.getExtraParam();
                cleanupAfterChannelProgramsRemovalFromCache((ProgramsDataBuffer) this.mChannelPrograms.remove(channelId), channelId);
                this.mChannelPrograms.put(channelId, new ProgramsDataBuffer(task.getResult()));
                this.mCachedProgramsChannelOrder.add(channelId);
                notifyChannelProgramsChange(channelId.longValue());
            }
        } else if (task.getTag() == 2) {
            Object[] results = (Object[]) task.getExtraResult();
            this.mPackagesWithChannels = (List) results[0];
            this.mPackageChannels = (Map) results[1];
            notifyPackagesWithChannelsChange();
            notifyPackageChannelsChange();
        } else if (task.getTag() == 3) {
            setPromoChannelAndNotify((Channel) task.getExtraResult());
        } else if (task.getTag() == 4) {
            if (task.getResult() != null) {
                if (this.mWatchNextPrograms != null) {
                    this.mWatchNextPrograms.release();
                }
                this.mWatchNextPrograms = new WatchNextDataBuffer(task.getResult());
                this.mWatchNextProgramsStale = false;
                notifyWatchNextProgramsChange();
            } else {
                Log.e(TAG, "error loading watch next programs, cursor is null");
            }
        } else if (task.getTag() == 5) {
            Cursor cursor = task.getResult();
            if (cursor != null) {
                updateWatchNextCache(cursor);
            } else {
                Log.e(TAG, "error loading watch next data into cache, cursor is null");
            }
        }
        stopTrackingTask(task);
    }

    private void setPromoChannelAndNotify(Channel promoChannel) {
        this.mPromoChannel = promoChannel;
        this.mPromoChannelLoaded = true;
        notifyPromoChannelChange();
    }

    @MainThread
    public void onTaskCancelled(DataLoadingBackgroundTask task) {
        stopTrackingTask(task);
    }

    @MainThread
    public void onTaskFailed(DataLoadingBackgroundTask task, Throwable throwable) {
        Log.e(TAG, "onTaskFailed: " + task + ", throwable: " + throwable);
        stopTrackingTask(task);
    }

    public void setChannelBrowsable(final long channelId, final boolean browsable) {
        new AsyncTask<Object, Void, Void>() {
            protected Void doInBackground(Object... params) {
                ContentValues values = new ContentValues();
                values.put("browsable", Boolean.valueOf(browsable));
                TvDataManager.this.mContext.getContentResolver().update(TvContract.buildChannelUri(channelId), values, null, null);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[]{Boolean.valueOf(browsable), Long.valueOf(channelId)});
    }

    public static void removePreviewProgram(final Context context, final long programId, final String packageName) {
       /* new AsyncTask<Object, Void, Void>() {
            protected Void doInBackground(Object... params) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("browsable", Integer.valueOf(0));
                if (context.getContentResolver().update(TvContract.buildPreviewProgramUri(programId), contentValues, null, null) == 1) {
                    ((TvInputManager) context.getSystemService("tv_input")).notifyPreviewProgramBrowsableDisabled(packageName, programId);
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[0]);
    */
    }

    public static void removeProgramFromWatchlist(final Context context, final long programId, final EventLogger eventLogger) {
        new AsyncTask<Object, Void, Void>() {
            protected Void doInBackground(Object... params) {
                Uri watchNextProgramUri = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    watchNextProgramUri = TvContract.buildWatchNextProgramUri(programId);
                }
                Cursor cursor = context.getContentResolver().query(watchNextProgramUri, new String[]{"package_name"}, null, null, null);
                if (cursor != null) {
                    try {
                        if (cursor.moveToFirst()) {
                            String packageName = cursor.getString(0);
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("browsable", Integer.valueOf(0));
                            if (context.getContentResolver().update(watchNextProgramUri, contentValues, null, null) == 1) {
                                // todo ((TvInputManager) context.getSystemService(Context.TV_INPUT_SERVICE)).notifyWatchNextProgramBrowsableDisabled(packageName, programId);
                            }
                            eventLogger.log(new UserActionEvent(LogEvents.REMOVE_PROGRAM_FROM_WATCH_NEXT).putParameter("package_name", packageName));
                        }
                    } catch (Throwable th) {
                        cursor.close();
                    }
                }
                cursor.close();
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[0]);
    }

    @SuppressLint("StaticFieldLeak")
    public static void addProgramToWatchlist(final Context context, final long programId, final String packageName) {
        new AsyncTask<Object, Void, Void>() {
            protected Void doInBackground(Object... params) {
                Cursor cursor = null;//context.getContentResolver().query(TvContract.buildPreviewProgramUri(programId), null, null, null, null);
                if (cursor != null) {
                    try {
                        if (cursor.moveToFirst()) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("package_name", packageName);
                            contentValues.put("watch_next_type", Integer.valueOf(3));
                            contentValues.put("last_engagement_time_utc_millis", Long.valueOf(System.currentTimeMillis()));
                            contentValues.put(NotificationsContract.COLUMN_NOTIF_TITLE, cursor.getString(cursor.getColumnIndex(NotificationsContract.COLUMN_NOTIF_TITLE)));
                            contentValues.put("season_display_number", cursor.getString(cursor.getColumnIndex("season_display_number")));
                            contentValues.put("season_title", cursor.getString(cursor.getColumnIndex("season_title")));
                            contentValues.put("episode_display_number", cursor.getString(cursor.getColumnIndex("episode_display_number")));
                            contentValues.put("episode_title", cursor.getString(cursor.getColumnIndex("episode_title")));
                            contentValues.put("canonical_genre", cursor.getString(cursor.getColumnIndex("canonical_genre")));
                            contentValues.put("short_description", cursor.getString(cursor.getColumnIndex("short_description")));
                            contentValues.put("long_description", cursor.getString(cursor.getColumnIndex("long_description")));
                            contentValues.put("video_width", Integer.valueOf(cursor.getInt(cursor.getColumnIndex("video_width"))));
                            contentValues.put("video_height", Integer.valueOf(cursor.getInt(cursor.getColumnIndex("video_height"))));
                            contentValues.put("audio_language", cursor.getString(cursor.getColumnIndex("audio_language")));
                            contentValues.put("content_rating", cursor.getString(cursor.getColumnIndex("content_rating")));
                            contentValues.put("poster_art_uri", cursor.getString(cursor.getColumnIndex("poster_art_uri")));
                            contentValues.put("thumbnail_uri", cursor.getString(cursor.getColumnIndex("thumbnail_uri")));
                            contentValues.put("searchable", Integer.valueOf(cursor.getInt(cursor.getColumnIndex("searchable"))));
                            contentValues.put(LauncherSharedConstants.LEGACY_PACKAGE_NAME_COLUMN, cursor.getBlob(cursor.getColumnIndex(LauncherSharedConstants.LEGACY_PACKAGE_NAME_COLUMN)));
                            contentValues.put("internal_provider_flag1", Integer.valueOf(cursor.getInt(cursor.getColumnIndex("internal_provider_flag1"))));
                            contentValues.put("internal_provider_flag2", Integer.valueOf(cursor.getInt(cursor.getColumnIndex("internal_provider_flag2"))));
                            contentValues.put("internal_provider_flag3", Integer.valueOf(cursor.getInt(cursor.getColumnIndex("internal_provider_flag3"))));
                            contentValues.put("internal_provider_flag4", Integer.valueOf(cursor.getInt(cursor.getColumnIndex("internal_provider_flag4"))));
                            contentValues.put("version_number", Integer.valueOf(cursor.getInt(cursor.getColumnIndex("version_number"))));
                            contentValues.put("review_rating_style", Integer.valueOf(cursor.getInt(cursor.getColumnIndex("review_rating_style"))));
                            contentValues.put("review_rating", cursor.getString(cursor.getColumnIndex("review_rating")));
                            contentValues.put("type", Integer.valueOf(cursor.getInt(cursor.getColumnIndex("type"))));
                            contentValues.put("poster_art_aspect_ratio", Integer.valueOf(cursor.getInt(cursor.getColumnIndex("poster_art_aspect_ratio"))));
                            contentValues.put("poster_thumbnail_aspect_ratio", Integer.valueOf(cursor.getInt(cursor.getColumnIndex("poster_thumbnail_aspect_ratio"))));
                            contentValues.put("logo_uri", cursor.getString(cursor.getColumnIndex("logo_uri")));
                            contentValues.put("availability", Integer.valueOf(cursor.getInt(cursor.getColumnIndex("availability"))));
                            contentValues.put("starting_price", cursor.getString(cursor.getColumnIndex("starting_price")));
                            contentValues.put("offer_price", cursor.getString(cursor.getColumnIndex("offer_price")));
                            contentValues.put("release_date", cursor.getString(cursor.getColumnIndex("release_date")));
                            contentValues.put("item_count", Integer.valueOf(cursor.getInt(cursor.getColumnIndex("item_count"))));
                            contentValues.put("live", Integer.valueOf(cursor.getInt(cursor.getColumnIndex("live"))));
                            contentValues.put("internal_provider_id", cursor.getString(cursor.getColumnIndex("internal_provider_id")));
                            contentValues.put("preview_video_uri", cursor.getString(cursor.getColumnIndex("preview_video_uri")));
                            contentValues.put("last_playback_position_millis", Integer.valueOf(cursor.getInt(cursor.getColumnIndex("last_playback_position_millis"))));
                            contentValues.put("duration_millis", Integer.valueOf(cursor.getInt(cursor.getColumnIndex("duration_millis"))));
                            contentValues.put("intent_uri", cursor.getString(cursor.getColumnIndex("intent_uri")));
                            contentValues.put("transient", Integer.valueOf(cursor.getInt(cursor.getColumnIndex("transient"))));
                            contentValues.put("interaction_type", Integer.valueOf(cursor.getInt(cursor.getColumnIndex("interaction_type"))));
                            contentValues.put("interaction_count", Long.valueOf(cursor.getLong(cursor.getColumnIndex("interaction_count"))));
                            contentValues.put("author", cursor.getString(cursor.getColumnIndex("author")));
                            contentValues.put("browsable", Integer.valueOf(cursor.getInt(cursor.getColumnIndex("browsable"))));
                            contentValues.put("content_id", cursor.getString(cursor.getColumnIndex("content_id")));
                            // ((TvInputManager) context.getSystemService("tv_input")).notifyPreviewProgramAddedToWatchNext(packageName, programId, ContentUris.parseId(context.getContentResolver().insert(WatchNextPrograms.CONTENT_URI, contentValues)));
                        }
                    } catch (Throwable th) {
                        cursor.close();
                    }
                }
                cursor.close();
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[0]);
    }

    public ChannelOrderManager getChannelOrderManager() {
        if (isHomeChannelDataLoaded()) {
            return this.mChannelOrderManager;
        }
        throw new IllegalStateException("Home channel data not loaded yet");
    }
}
