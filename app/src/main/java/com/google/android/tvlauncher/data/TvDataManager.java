package com.google.android.tvlauncher.data;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.media.tv.TvContract.Channels;
import android.media.tv.TvContract.PreviewPrograms;
import android.media.tv.TvContract.WatchNextPrograms;
import android.media.tv.TvInputManager;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.UserActionEvent;
import com.google.android.tvlauncher.model.Channel;
import com.google.android.tvlauncher.model.ChannelPackage;
import com.google.android.tvlauncher.model.HomeChannel;
import com.google.android.tvlauncher.model.Program;
import com.google.android.tvlauncher.util.Partner;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
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

public class TvDataManager
  implements DataLoadingBackgroundTask.Callbacks
{
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
  private final Comparator<HomeChannel> mHomeChannelsComparator = new Comparator()
  {
    public int compare(@NonNull HomeChannel paramAnonymousHomeChannel1, @NonNull HomeChannel paramAnonymousHomeChannel2)
    {
      if (paramAnonymousHomeChannel1 == paramAnonymousHomeChannel2) {}
      Integer localInteger1;
      do
      {
        return 0;
        localInteger1 = TvDataManager.this.mChannelOrderManager.getChannelPosition(paramAnonymousHomeChannel1.getId());
        Integer localInteger2 = TvDataManager.this.mChannelOrderManager.getChannelPosition(paramAnonymousHomeChannel2.getId());
        if ((localInteger1 != null) && (localInteger2 != null)) {
          return Integer.compare(localInteger1.intValue(), localInteger2.intValue());
        }
        if ((localInteger1 != null) || (localInteger2 != null)) {
          break;
        }
      } while ((paramAnonymousHomeChannel1.getDisplayName() == null) && (paramAnonymousHomeChannel2.getDisplayName() == null));
      if (paramAnonymousHomeChannel1.getDisplayName() == null) {
        return 1;
      }
      if (paramAnonymousHomeChannel2.getDisplayName() == null) {
        return -1;
      }
      return paramAnonymousHomeChannel1.getDisplayName().compareToIgnoreCase(paramAnonymousHomeChannel2.getDisplayName());
      if (localInteger1 == null) {
        return 1;
      }
      return -1;
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
  private final SharedPreferences.OnSharedPreferenceChangeListener mWatchNextPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener()
  {
    public void onSharedPreferenceChanged(SharedPreferences paramAnonymousSharedPreferences, String paramAnonymousString)
    {
      if (paramAnonymousString.startsWith("watch_next_package_key_prefix"))
      {
        TvDataManager.access$102(TvDataManager.this, TvDataManager.this.buildWatchNextSelection(paramAnonymousSharedPreferences));
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
  
  private TvDataManager(Context paramContext)
  {
    this.mContext = paramContext.getApplicationContext();
    this.mDataSourceObserver = new DataSourceObserver(this.mContext, new DataSourceObserverCallbacks(null));
    paramContext = paramContext.getSharedPreferences("com.google.android.tvlauncher.data.TvDataManager.WATCH_NEXT_PREF_FILE_NAME", 0);
    paramContext.registerOnSharedPreferenceChangeListener(this.mWatchNextPrefListener);
    this.mWatchNextSelection = buildWatchNextSelection(paramContext);
    loadAllWatchNextProgramDataIntoCache();
  }
  
  public static void addProgramToWatchlist(Context paramContext, final long paramLong, String paramString)
  {
    new AsyncTask()
    {
      protected Void doInBackground(Object... paramAnonymousVarArgs)
      {
        paramAnonymousVarArgs = this.val$context.getContentResolver().query(TvContract.buildPreviewProgramUri(paramLong), null, null, null, null);
        if (paramAnonymousVarArgs != null) {}
        try
        {
          if (paramAnonymousVarArgs.moveToFirst())
          {
            ContentValues localContentValues = new ContentValues();
            localContentValues.put("package_name", this.val$packageName);
            localContentValues.put("watch_next_type", Integer.valueOf(3));
            localContentValues.put("last_engagement_time_utc_millis", Long.valueOf(System.currentTimeMillis()));
            localContentValues.put("title", paramAnonymousVarArgs.getString(paramAnonymousVarArgs.getColumnIndex("title")));
            localContentValues.put("season_display_number", paramAnonymousVarArgs.getString(paramAnonymousVarArgs.getColumnIndex("season_display_number")));
            localContentValues.put("season_title", paramAnonymousVarArgs.getString(paramAnonymousVarArgs.getColumnIndex("season_title")));
            localContentValues.put("episode_display_number", paramAnonymousVarArgs.getString(paramAnonymousVarArgs.getColumnIndex("episode_display_number")));
            localContentValues.put("episode_title", paramAnonymousVarArgs.getString(paramAnonymousVarArgs.getColumnIndex("episode_title")));
            localContentValues.put("canonical_genre", paramAnonymousVarArgs.getString(paramAnonymousVarArgs.getColumnIndex("canonical_genre")));
            localContentValues.put("short_description", paramAnonymousVarArgs.getString(paramAnonymousVarArgs.getColumnIndex("short_description")));
            localContentValues.put("long_description", paramAnonymousVarArgs.getString(paramAnonymousVarArgs.getColumnIndex("long_description")));
            localContentValues.put("video_width", Integer.valueOf(paramAnonymousVarArgs.getInt(paramAnonymousVarArgs.getColumnIndex("video_width"))));
            localContentValues.put("video_height", Integer.valueOf(paramAnonymousVarArgs.getInt(paramAnonymousVarArgs.getColumnIndex("video_height"))));
            localContentValues.put("audio_language", paramAnonymousVarArgs.getString(paramAnonymousVarArgs.getColumnIndex("audio_language")));
            localContentValues.put("content_rating", paramAnonymousVarArgs.getString(paramAnonymousVarArgs.getColumnIndex("content_rating")));
            localContentValues.put("poster_art_uri", paramAnonymousVarArgs.getString(paramAnonymousVarArgs.getColumnIndex("poster_art_uri")));
            localContentValues.put("thumbnail_uri", paramAnonymousVarArgs.getString(paramAnonymousVarArgs.getColumnIndex("thumbnail_uri")));
            localContentValues.put("searchable", Integer.valueOf(paramAnonymousVarArgs.getInt(paramAnonymousVarArgs.getColumnIndex("searchable"))));
            localContentValues.put("internal_provider_data", paramAnonymousVarArgs.getBlob(paramAnonymousVarArgs.getColumnIndex("internal_provider_data")));
            localContentValues.put("internal_provider_flag1", Integer.valueOf(paramAnonymousVarArgs.getInt(paramAnonymousVarArgs.getColumnIndex("internal_provider_flag1"))));
            localContentValues.put("internal_provider_flag2", Integer.valueOf(paramAnonymousVarArgs.getInt(paramAnonymousVarArgs.getColumnIndex("internal_provider_flag2"))));
            localContentValues.put("internal_provider_flag3", Integer.valueOf(paramAnonymousVarArgs.getInt(paramAnonymousVarArgs.getColumnIndex("internal_provider_flag3"))));
            localContentValues.put("internal_provider_flag4", Integer.valueOf(paramAnonymousVarArgs.getInt(paramAnonymousVarArgs.getColumnIndex("internal_provider_flag4"))));
            localContentValues.put("version_number", Integer.valueOf(paramAnonymousVarArgs.getInt(paramAnonymousVarArgs.getColumnIndex("version_number"))));
            localContentValues.put("review_rating_style", Integer.valueOf(paramAnonymousVarArgs.getInt(paramAnonymousVarArgs.getColumnIndex("review_rating_style"))));
            localContentValues.put("review_rating", paramAnonymousVarArgs.getString(paramAnonymousVarArgs.getColumnIndex("review_rating")));
            localContentValues.put("type", Integer.valueOf(paramAnonymousVarArgs.getInt(paramAnonymousVarArgs.getColumnIndex("type"))));
            localContentValues.put("poster_art_aspect_ratio", Integer.valueOf(paramAnonymousVarArgs.getInt(paramAnonymousVarArgs.getColumnIndex("poster_art_aspect_ratio"))));
            localContentValues.put("poster_thumbnail_aspect_ratio", Integer.valueOf(paramAnonymousVarArgs.getInt(paramAnonymousVarArgs.getColumnIndex("poster_thumbnail_aspect_ratio"))));
            localContentValues.put("logo_uri", paramAnonymousVarArgs.getString(paramAnonymousVarArgs.getColumnIndex("logo_uri")));
            localContentValues.put("availability", Integer.valueOf(paramAnonymousVarArgs.getInt(paramAnonymousVarArgs.getColumnIndex("availability"))));
            localContentValues.put("starting_price", paramAnonymousVarArgs.getString(paramAnonymousVarArgs.getColumnIndex("starting_price")));
            localContentValues.put("offer_price", paramAnonymousVarArgs.getString(paramAnonymousVarArgs.getColumnIndex("offer_price")));
            localContentValues.put("release_date", paramAnonymousVarArgs.getString(paramAnonymousVarArgs.getColumnIndex("release_date")));
            localContentValues.put("item_count", Integer.valueOf(paramAnonymousVarArgs.getInt(paramAnonymousVarArgs.getColumnIndex("item_count"))));
            localContentValues.put("live", Integer.valueOf(paramAnonymousVarArgs.getInt(paramAnonymousVarArgs.getColumnIndex("live"))));
            localContentValues.put("internal_provider_id", paramAnonymousVarArgs.getString(paramAnonymousVarArgs.getColumnIndex("internal_provider_id")));
            localContentValues.put("preview_video_uri", paramAnonymousVarArgs.getString(paramAnonymousVarArgs.getColumnIndex("preview_video_uri")));
            localContentValues.put("last_playback_position_millis", Integer.valueOf(paramAnonymousVarArgs.getInt(paramAnonymousVarArgs.getColumnIndex("last_playback_position_millis"))));
            localContentValues.put("duration_millis", Integer.valueOf(paramAnonymousVarArgs.getInt(paramAnonymousVarArgs.getColumnIndex("duration_millis"))));
            localContentValues.put("intent_uri", paramAnonymousVarArgs.getString(paramAnonymousVarArgs.getColumnIndex("intent_uri")));
            localContentValues.put("transient", Integer.valueOf(paramAnonymousVarArgs.getInt(paramAnonymousVarArgs.getColumnIndex("transient"))));
            localContentValues.put("interaction_type", Integer.valueOf(paramAnonymousVarArgs.getInt(paramAnonymousVarArgs.getColumnIndex("interaction_type"))));
            localContentValues.put("interaction_count", Long.valueOf(paramAnonymousVarArgs.getLong(paramAnonymousVarArgs.getColumnIndex("interaction_count"))));
            localContentValues.put("author", paramAnonymousVarArgs.getString(paramAnonymousVarArgs.getColumnIndex("author")));
            localContentValues.put("browsable", Integer.valueOf(paramAnonymousVarArgs.getInt(paramAnonymousVarArgs.getColumnIndex("browsable"))));
            localContentValues.put("content_id", paramAnonymousVarArgs.getString(paramAnonymousVarArgs.getColumnIndex("content_id")));
            long l = ContentUris.parseId(this.val$context.getContentResolver().insert(TvContract.WatchNextPrograms.CONTENT_URI, localContentValues));
            ((TvInputManager)this.val$context.getSystemService("tv_input")).notifyPreviewProgramAddedToWatchNext(this.val$packageName, paramLong, l);
          }
          return null;
        }
        finally
        {
          paramAnonymousVarArgs.close();
        }
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[0]);
  }
  
  private static <K, V> void addToMultimap(Map<K, List<V>> paramMap, K paramK, V paramV)
  {
    Object localObject = (List)paramMap.get(paramK);
    if (localObject == null)
    {
      localObject = new LinkedList();
      ((List)localObject).add(paramV);
      paramMap.put(paramK, localObject);
    }
    while (((List)localObject).contains(paramV)) {
      return;
    }
    ((List)localObject).add(paramV);
  }
  
  private String buildWatchNextSelection(SharedPreferences paramSharedPreferences)
  {
    StringBuilder localStringBuilder = new StringBuilder(" NOT IN (");
    int i = 0;
    paramSharedPreferences = paramSharedPreferences.getAll().keySet().iterator();
    while (paramSharedPreferences.hasNext())
    {
      String str = (String)paramSharedPreferences.next();
      if (str.startsWith("watch_next_package_key_prefix"))
      {
        localStringBuilder.append("'").append(str.substring("watch_next_package_key_prefix".length())).append("',");
        i = 1;
      }
    }
    if (i != 0)
    {
      localStringBuilder.setLength(localStringBuilder.length() - 1);
      localStringBuilder.append(")");
      paramSharedPreferences = localStringBuilder.toString();
      return "browsable=1 AND last_engagement_time_utc_millis<=? AND package_name" + paramSharedPreferences;
    }
    return "browsable=1 AND last_engagement_time_utc_millis<=?";
  }
  
  private void cleanupAfterChannelProgramsRemovalFromCache(ProgramsDataBuffer paramProgramsDataBuffer, Long paramLong)
  {
    if (paramProgramsDataBuffer != null) {
      paramProgramsDataBuffer.release();
    }
    this.mStaleProgramsChannels.remove(paramLong);
    this.mCachedProgramsChannelOrder.remove(paramLong);
  }
  
  private void clearPackageChannelsCacheAndCancelTasksIfNoObservers()
  {
    if ((this.mPackagesWithChannelsObservers.size() == 0) && (this.mPackageChannelsObservers.size() == 0))
    {
      if (this.mAllPreviewChannelsBackgroundTask != null)
      {
        this.mAllPreviewChannelsBackgroundTask.cancel();
        this.mAllPreviewChannelsBackgroundTask = null;
      }
      this.mPackagesWithChannels = null;
      this.mPackageChannels = null;
    }
  }
  
  public static TvDataManager getInstance(Context paramContext)
  {
    if (sInstance == null) {
      sInstance = new TvDataManager(paramContext);
    }
    return sInstance;
  }
  
  private void loadAllPreviewChannelsData()
  {
    if (this.mAllPreviewChannelsBackgroundTask != null) {
      return;
    }
    DataLoadingBackgroundTask localDataLoadingBackgroundTask = DataLoadingBackgroundTask.obtain(this.mContext).setUri(TvContract.Channels.CONTENT_URI).setProjection(Channel.PROJECTION).setSelection("type='TYPE_PREVIEW'").setTag(2L).setCallbacks(this);
    startTrackingTask(localDataLoadingBackgroundTask);
    localDataLoadingBackgroundTask.execute();
  }
  
  private void loadAllWatchNextProgramDataIntoCache()
  {
    if (this.mWatchNextCacheBackgroundTask != null) {
      return;
    }
    DataLoadingBackgroundTask localDataLoadingBackgroundTask = DataLoadingBackgroundTask.obtain(this.mContext).setUri(TvContract.WatchNextPrograms.CONTENT_URI).setProjection(Program.WATCH_NEXT_PROJECTION).setSelection("browsable=1").setTag(5L).setCallbacks(this);
    startTrackingTask(localDataLoadingBackgroundTask);
    localDataLoadingBackgroundTask.execute();
  }
  
  private void loadChannelProgramData(long paramLong)
  {
    if (this.mChannelProgramsBackgroundTasks.containsKey(Long.valueOf(paramLong))) {
      return;
    }
    if (!this.mChannelPrograms.containsKey(Long.valueOf(paramLong))) {
      removeOneChannelProgramsIfCacheTooBig();
    }
    DataLoadingBackgroundTask localDataLoadingBackgroundTask = DataLoadingBackgroundTask.obtain(this.mContext).setUri(TvContract.PreviewPrograms.CONTENT_URI).setProjection(Program.PROJECTION).setSelection("channel_id=? AND browsable=1").setSelectionArg(String.valueOf(paramLong)).setSortOrder("weight DESC").setExtraParam(Long.valueOf(paramLong)).setTag(1L).setCallbacks(this);
    startTrackingTask(localDataLoadingBackgroundTask);
    localDataLoadingBackgroundTask.execute();
  }
  
  private void loadHomeChannelDataInternal()
  {
    if (this.mHomeChannelsBackgroundTask != null) {
      return;
    }
    DataLoadingBackgroundTask localDataLoadingBackgroundTask = DataLoadingBackgroundTask.obtain(this.mContext).setUri(TvContract.Channels.CONTENT_URI).setProjection(HomeChannel.PROJECTION).setSelection("browsable=1 AND type='TYPE_PREVIEW'").setTag(0L).setCallbacks(this);
    startTrackingTask(localDataLoadingBackgroundTask);
    localDataLoadingBackgroundTask.execute();
  }
  
  private void loadPromoChannelData()
  {
    if (this.mPromoChannelBackgroundTask != null) {
      return;
    }
    if (Partner.get(this.mContext).getAppsPromotionRowPackage() == null)
    {
      Log.e("TvDataManager", "Promotion row package is not configured");
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvDataManager.this.setPromoChannelAndNotify(null);
        }
      });
      return;
    }
    DataLoadingBackgroundTask localDataLoadingBackgroundTask = DataLoadingBackgroundTask.obtain(this.mContext).setUri(TvContract.Channels.CONTENT_URI).setProjection(Channel.PROJECTION).setSelection("package_name=?").setSelectionArg(Partner.get(this.mContext).getAppsPromotionRowPackage()).setTag(3L).setCallbacks(this);
    startTrackingTask(localDataLoadingBackgroundTask);
    localDataLoadingBackgroundTask.execute();
  }
  
  private void loadWatchNextProgramDataInternal()
  {
    if (this.mWatchNextProgramsBackgroundTask != null) {
      return;
    }
    DataLoadingBackgroundTask localDataLoadingBackgroundTask = DataLoadingBackgroundTask.obtain(this.mContext).setUri(TvContract.WatchNextPrograms.CONTENT_URI).setProjection(Program.WATCH_NEXT_PROJECTION).setSelection(this.mWatchNextSelection).setSelectionArg(String.valueOf(System.currentTimeMillis() + 600000L)).setSortOrder("last_engagement_time_utc_millis DESC LIMIT 1000").setTag(4L).setCallbacks(this);
    startTrackingTask(localDataLoadingBackgroundTask);
    localDataLoadingBackgroundTask.execute();
  }
  
  private void logCachesState()
  {
    StringBuilder localStringBuilder = new StringBuilder().append("Cache: \nmHomeChannels=");
    if (this.mHomeChannels != null)
    {
      localObject = Integer.valueOf(this.mHomeChannels.size());
      localStringBuilder = localStringBuilder.append(localObject).append("\nmChannelPrograms=");
      if (this.mChannelPrograms == null) {
        break label208;
      }
      localObject = Integer.valueOf(this.mChannelPrograms.size());
      label66:
      localStringBuilder = localStringBuilder.append(localObject).append("\nmPackagesWithChannels=");
      if (this.mPackagesWithChannels == null) {
        break label215;
      }
      localObject = Integer.valueOf(this.mPackagesWithChannels.size());
      label98:
      localStringBuilder = localStringBuilder.append(localObject).append("\nmPackageChannels=");
      if (this.mPackageChannels == null) {
        break label222;
      }
      localObject = Integer.valueOf(this.mPackageChannels.size());
      label130:
      localStringBuilder = localStringBuilder.append(localObject).append("\nmCachedProgramsChannelOrder: ").append(this.mCachedProgramsChannelOrder).append("\nmStaleProgramsChannels: ").append(this.mStaleProgramsChannels).append("\nmWatchNextPrograms: ");
      if (this.mWatchNextPrograms == null) {
        break label229;
      }
    }
    label208:
    label215:
    label222:
    label229:
    for (Object localObject = Integer.valueOf(this.mWatchNextPrograms.getCount());; localObject = "null")
    {
      Log.d("TvDataManager", localObject);
      return;
      localObject = "null";
      break;
      localObject = "null";
      break label66;
      localObject = "null";
      break label98;
      localObject = "null";
      break label130;
    }
  }
  
  private void logObserversState()
  {
    int j = this.mChannelProgramsObservers.size();
    int i = 0;
    Iterator localIterator = this.mChannelProgramsObservers.values().iterator();
    while (localIterator.hasNext()) {
      i += ((List)localIterator.next()).size();
    }
    Log.d("TvDataManager", "Observers: \nmHomeChannelsObservers.size()=" + this.mHomeChannelsObservers.size() + "\nmChannelProgramsObservers.size()=" + j + " (total: " + i + ")" + "\nmPackagesWithChannelsObservers.size()=" + this.mPackagesWithChannelsObservers.size() + "\nmPackageChannelsObservers.size()=" + this.mPackageChannelsObservers.size() + "\nmPromoChannelObservers.size()=" + this.mPromoChannelObservers.size() + "\nmWatchNextProgramsObservers.size()=" + this.mWatchNextProgramsObservers.size());
  }
  
  private void notifyChannelProgramsChange(long paramLong)
  {
    Object localObject = (List)this.mChannelProgramsObservers.get(Long.valueOf(paramLong));
    if (localObject != null)
    {
      localObject = ((List)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        ((ChannelProgramsObserver)((Iterator)localObject).next()).onProgramsChange(paramLong);
      }
    }
  }
  
  private void notifyHomeChannelsChange()
  {
    Iterator localIterator = this.mHomeChannelsObservers.iterator();
    while (localIterator.hasNext()) {
      ((HomeChannelsObserver)localIterator.next()).onChannelsChange();
    }
  }
  
  private void notifyPackageChannelsChange()
  {
    Iterator localIterator = this.mPackageChannelsObservers.iterator();
    while (localIterator.hasNext()) {
      ((PackageChannelsObserver)localIterator.next()).onChannelsChange();
    }
  }
  
  private void notifyPackagesWithChannelsChange()
  {
    Iterator localIterator = this.mPackagesWithChannelsObservers.iterator();
    while (localIterator.hasNext()) {
      ((PackagesWithChannelsObserver)localIterator.next()).onPackagesChange();
    }
  }
  
  private void notifyPromoChannelChange()
  {
    Iterator localIterator = this.mPromoChannelObservers.iterator();
    while (localIterator.hasNext()) {
      ((PromoChannelObserver)localIterator.next()).onChannelChange();
    }
  }
  
  private void notifyWatchNextProgramsChange()
  {
    Iterator localIterator = this.mWatchNextProgramsObservers.iterator();
    while (localIterator.hasNext()) {
      ((WatchNextProgramsObserver)localIterator.next()).onProgramsChange();
    }
  }
  
  private void registerDataSourceObserver()
  {
    this.mDataSourceObserver.register();
  }
  
  private static <K, V> void removeFromMultimap(Map<K, List<V>> paramMap, K paramK, V paramV)
  {
    List localList = (List)paramMap.get(paramK);
    if (localList == null) {}
    do
    {
      return;
      localList.remove(paramV);
    } while (localList.size() != 0);
    paramMap.remove(paramK);
  }
  
  private void removeOneChannelProgramsIfCacheTooBig()
  {
    if (this.mChannelPrograms.size() >= 15)
    {
      Iterator localIterator = this.mCachedProgramsChannelOrder.iterator();
      while (localIterator.hasNext())
      {
        Long localLong = (Long)localIterator.next();
        if (!this.mChannelProgramsObservers.containsKey(localLong)) {
          cleanupAfterChannelProgramsRemovalFromCache((ProgramsDataBuffer)this.mChannelPrograms.remove(localLong), localLong);
        }
      }
    }
  }
  
  public static void removePreviewProgram(Context paramContext, final long paramLong, String paramString)
  {
    new AsyncTask()
    {
      protected Void doInBackground(Object... paramAnonymousVarArgs)
      {
        paramAnonymousVarArgs = new ContentValues();
        paramAnonymousVarArgs.put("browsable", Integer.valueOf(0));
        if (this.val$context.getContentResolver().update(TvContract.buildPreviewProgramUri(paramLong), paramAnonymousVarArgs, null, null) == 1) {
          ((TvInputManager)this.val$context.getSystemService("tv_input")).notifyPreviewProgramBrowsableDisabled(this.val$packageName, paramLong);
        }
        return null;
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[0]);
  }
  
  public static void removeProgramFromWatchlist(Context paramContext, long paramLong, final EventLogger paramEventLogger)
  {
    new AsyncTask()
    {
      protected Void doInBackground(Object... paramAnonymousVarArgs)
      {
        Uri localUri = TvContract.buildWatchNextProgramUri(this.val$programId);
        paramAnonymousVarArgs = paramEventLogger.getContentResolver().query(localUri, new String[] { "package_name" }, null, null, null);
        if (paramAnonymousVarArgs != null) {}
        try
        {
          if (paramAnonymousVarArgs.moveToFirst())
          {
            String str = paramAnonymousVarArgs.getString(0);
            ContentValues localContentValues = new ContentValues();
            localContentValues.put("browsable", Integer.valueOf(0));
            if (paramEventLogger.getContentResolver().update(localUri, localContentValues, null, null) == 1) {
              ((TvInputManager)paramEventLogger.getSystemService("tv_input")).notifyWatchNextProgramBrowsableDisabled(str, this.val$programId);
            }
            this.val$eventLogger.log(new UserActionEvent("remove_program_from_watch_next").putParameter("package_name", str));
          }
          return null;
        }
        finally
        {
          paramAnonymousVarArgs.close();
        }
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[0]);
  }
  
  private void setPromoChannelAndNotify(Channel paramChannel)
  {
    this.mPromoChannel = paramChannel;
    this.mPromoChannelLoaded = true;
    notifyPromoChannelChange();
  }
  
  private void startTrackingTask(DataLoadingBackgroundTask paramDataLoadingBackgroundTask)
  {
    if (paramDataLoadingBackgroundTask.getTag() == 0L) {
      this.mHomeChannelsBackgroundTask = paramDataLoadingBackgroundTask;
    }
    do
    {
      return;
      if (paramDataLoadingBackgroundTask.getTag() == 1L)
      {
        this.mChannelProgramsBackgroundTasks.put((Long)paramDataLoadingBackgroundTask.getExtraParam(), paramDataLoadingBackgroundTask);
        return;
      }
      if (paramDataLoadingBackgroundTask.getTag() == 2L)
      {
        this.mAllPreviewChannelsBackgroundTask = paramDataLoadingBackgroundTask;
        return;
      }
      if (paramDataLoadingBackgroundTask.getTag() == 3L)
      {
        this.mPromoChannelBackgroundTask = paramDataLoadingBackgroundTask;
        return;
      }
      if (paramDataLoadingBackgroundTask.getTag() == 4L)
      {
        this.mWatchNextProgramsBackgroundTask = paramDataLoadingBackgroundTask;
        return;
      }
    } while (paramDataLoadingBackgroundTask.getTag() != 5L);
    this.mWatchNextCacheBackgroundTask = paramDataLoadingBackgroundTask;
  }
  
  private void stopTrackingTask(DataLoadingBackgroundTask paramDataLoadingBackgroundTask)
  {
    if (paramDataLoadingBackgroundTask.getTag() == 0L) {
      this.mHomeChannelsBackgroundTask = null;
    }
    do
    {
      return;
      if (paramDataLoadingBackgroundTask.getTag() == 1L)
      {
        this.mChannelProgramsBackgroundTasks.remove((Long)paramDataLoadingBackgroundTask.getExtraParam());
        return;
      }
      if (paramDataLoadingBackgroundTask.getTag() == 2L)
      {
        this.mAllPreviewChannelsBackgroundTask = null;
        return;
      }
      if (paramDataLoadingBackgroundTask.getTag() == 3L)
      {
        this.mPromoChannelBackgroundTask = null;
        return;
      }
      if (paramDataLoadingBackgroundTask.getTag() == 4L)
      {
        this.mWatchNextProgramsBackgroundTask = null;
        return;
      }
    } while (paramDataLoadingBackgroundTask.getTag() != 5L);
    this.mWatchNextCacheBackgroundTask = null;
  }
  
  private void unregisterDataSourceObserverIfNoObservers()
  {
    if ((this.mHomeChannelsObservers.size() == 0) && (this.mChannelProgramsObservers.size() == 0) && (this.mPackagesWithChannelsObservers.size() == 0) && (this.mPackageChannelsObservers.size() == 0) && (this.mPromoChannelObservers.size() == 0) && (this.mWatchNextProgramsObservers.size() == 0))
    {
      this.mHomeChannelsStale = true;
      this.mChannelLogoUris.clear();
      Iterator localIterator = this.mChannelPrograms.keySet().iterator();
      while (localIterator.hasNext())
      {
        Long localLong = (Long)localIterator.next();
        this.mStaleProgramsChannels.add(localLong);
      }
      this.mWatchNextProgramsStale = true;
      this.mDataSourceObserver.unregister();
    }
  }
  
  private void updateWatchNextCache(Cursor paramCursor)
  {
    this.mWatchNextProgramsCache.clear();
    if ((paramCursor != null) && (paramCursor.getCount() > 0))
    {
      paramCursor.moveToFirst();
      do
      {
        addProgramToWatchNextCache(paramCursor.getString(27), paramCursor.getString(33));
      } while (paramCursor.moveToNext());
    }
  }
  
  public void addProgramToWatchNextCache(String paramString1, String paramString2)
  {
    if ((paramString1 != null) && (paramString2 != null)) {
      this.mWatchNextProgramsCache.add(paramString1.concat(paramString2));
    }
  }
  
  public Uri getChannelLogoUri(Long paramLong)
  {
    Uri localUri2 = (Uri)this.mChannelLogoUris.get(paramLong);
    Uri localUri1 = localUri2;
    if (localUri2 == null)
    {
      localUri1 = TvContract.buildChannelLogoUri(paramLong.longValue()).buildUpon().appendQueryParameter("t", String.valueOf(System.currentTimeMillis())).build();
      this.mChannelLogoUris.put(paramLong, localUri1);
    }
    return localUri1;
  }
  
  public ChannelOrderManager getChannelOrderManager()
  {
    if (!isHomeChannelDataLoaded()) {
      throw new IllegalStateException("Home channel data not loaded yet");
    }
    return this.mChannelOrderManager;
  }
  
  public HomeChannel getHomeChannel(int paramInt)
  {
    return (HomeChannel)this.mHomeChannels.get(paramInt);
  }
  
  public int getHomeChannelCount()
  {
    if (this.mHomeChannels != null) {
      return this.mHomeChannels.size();
    }
    return -1;
  }
  
  public List<Channel> getPackageChannels(String paramString)
  {
    return (List)this.mPackageChannels.get(paramString);
  }
  
  public List<ChannelPackage> getPackagesWithChannels()
  {
    return this.mPackagesWithChannels;
  }
  
  public Program getProgram(long paramLong, int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Position must be positive");
    }
    ProgramsDataBuffer localProgramsDataBuffer = (ProgramsDataBuffer)this.mChannelPrograms.get(Long.valueOf(paramLong));
    if ((localProgramsDataBuffer != null) && (paramInt >= localProgramsDataBuffer.getCount())) {
      throw new IllegalArgumentException("Position [" + paramInt + "] is out of bounds [0, " + (localProgramsDataBuffer.getCount() - 1) + "]");
    }
    if (localProgramsDataBuffer != null) {
      return localProgramsDataBuffer.get(paramInt);
    }
    return null;
  }
  
  public int getProgramCount(long paramLong)
  {
    if (this.mChannelPrograms.containsKey(Long.valueOf(paramLong))) {
      return ((ProgramsDataBuffer)this.mChannelPrograms.get(Long.valueOf(paramLong))).getCount();
    }
    return -1;
  }
  
  public Channel getPromoChannel()
  {
    return this.mPromoChannel;
  }
  
  public Program getWatchNextProgram(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Position must be positive");
    }
    if ((this.mWatchNextPrograms != null) && (paramInt >= this.mWatchNextPrograms.getCount())) {
      throw new IllegalArgumentException("Position [" + paramInt + "] is out of bounds [0, " + (this.mWatchNextPrograms.getCount() - 1) + "]");
    }
    if (this.mWatchNextPrograms != null) {
      return this.mWatchNextPrograms.get(paramInt);
    }
    return null;
  }
  
  public int getWatchNextProgramsCount()
  {
    if (this.mWatchNextPrograms != null) {
      return this.mWatchNextPrograms.getCount();
    }
    return 0;
  }
  
  public boolean isHomeChannelDataLoaded()
  {
    return this.mHomeChannels != null;
  }
  
  public boolean isHomeChannelDataStale()
  {
    return this.mHomeChannelsStale;
  }
  
  public boolean isInWatchNext(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString2 == null)) {
      return false;
    }
    paramString1 = paramString1.concat(paramString2);
    return this.mWatchNextProgramsCache.contains(paramString1);
  }
  
  public boolean isPackageChannelDataLoaded(String paramString)
  {
    return (this.mPackageChannels != null) && (this.mPackageChannels.containsKey(paramString));
  }
  
  public boolean isPackagesWithChannelsDataLoaded()
  {
    return this.mPackagesWithChannels != null;
  }
  
  public boolean isProgramDataLoaded(long paramLong)
  {
    return this.mChannelPrograms.containsKey(Long.valueOf(paramLong));
  }
  
  public boolean isProgramDataStale(long paramLong)
  {
    return this.mStaleProgramsChannels.contains(Long.valueOf(paramLong));
  }
  
  public boolean isPromoChannelLoaded()
  {
    return this.mPromoChannelLoaded;
  }
  
  public boolean isWatchNextProgramsDataLoaded()
  {
    return this.mWatchNextPrograms != null;
  }
  
  public boolean isWatchNextProgramsDataStale()
  {
    return this.mWatchNextProgramsStale;
  }
  
  public void loadHomeChannelData()
  {
    loadHomeChannelDataInternal();
  }
  
  public void loadPackageChannelsData(String paramString)
  {
    loadAllPreviewChannelsData();
  }
  
  public void loadPackagesWithChannelsData()
  {
    loadAllPreviewChannelsData();
  }
  
  public void loadProgramData(long paramLong)
  {
    loadChannelProgramData(paramLong);
  }
  
  public void loadPromoChannel()
  {
    loadPromoChannelData();
  }
  
  public void loadWatchNextProgramData()
  {
    loadWatchNextProgramDataInternal();
  }
  
  @MainThread
  public void onTaskCancelled(DataLoadingBackgroundTask paramDataLoadingBackgroundTask)
  {
    stopTrackingTask(paramDataLoadingBackgroundTask);
  }
  
  @MainThread
  public void onTaskCompleted(DataLoadingBackgroundTask paramDataLoadingBackgroundTask)
  {
    if (paramDataLoadingBackgroundTask.getTag() == 0L)
    {
      if (this.mChannelOrderManager == null)
      {
        this.mChannelOrderManager = new ChannelOrderManager(this.mContext);
        this.mChannelOrderManager.setChannelsObservers(this.mHomeChannelsObservers);
      }
      this.mHomeChannels = ((List)paramDataLoadingBackgroundTask.getExtraResult());
      this.mHomeChannelsStale = false;
      Collections.sort(this.mHomeChannels, this.mHomeChannelsComparator);
      this.mChannelOrderManager.setChannels(this.mHomeChannels);
      this.mChannelOrderManager.refreshChannelPositions();
      notifyHomeChannelsChange();
    }
    for (;;)
    {
      stopTrackingTask(paramDataLoadingBackgroundTask);
      return;
      Object localObject;
      if (paramDataLoadingBackgroundTask.getTag() == 1L)
      {
        if (paramDataLoadingBackgroundTask.getResult() != null)
        {
          localObject = (Long)paramDataLoadingBackgroundTask.getExtraParam();
          cleanupAfterChannelProgramsRemovalFromCache((ProgramsDataBuffer)this.mChannelPrograms.remove(localObject), (Long)localObject);
          this.mChannelPrograms.put(localObject, new ProgramsDataBuffer(paramDataLoadingBackgroundTask.getResult()));
          this.mCachedProgramsChannelOrder.add(localObject);
          notifyChannelProgramsChange(((Long)localObject).longValue());
        }
      }
      else if (paramDataLoadingBackgroundTask.getTag() == 2L)
      {
        localObject = (Object[])paramDataLoadingBackgroundTask.getExtraResult();
        this.mPackagesWithChannels = ((List)localObject[0]);
        this.mPackageChannels = ((Map)localObject[1]);
        notifyPackagesWithChannelsChange();
        notifyPackageChannelsChange();
      }
      else if (paramDataLoadingBackgroundTask.getTag() == 3L)
      {
        setPromoChannelAndNotify((Channel)paramDataLoadingBackgroundTask.getExtraResult());
      }
      else if (paramDataLoadingBackgroundTask.getTag() == 4L)
      {
        if (paramDataLoadingBackgroundTask.getResult() != null)
        {
          if (this.mWatchNextPrograms != null) {
            this.mWatchNextPrograms.release();
          }
          this.mWatchNextPrograms = new WatchNextDataBuffer(paramDataLoadingBackgroundTask.getResult());
          this.mWatchNextProgramsStale = false;
          notifyWatchNextProgramsChange();
        }
        else
        {
          Log.e("TvDataManager", "error loading watch next programs, cursor is null");
        }
      }
      else if (paramDataLoadingBackgroundTask.getTag() == 5L)
      {
        localObject = paramDataLoadingBackgroundTask.getResult();
        if (localObject != null) {
          updateWatchNextCache((Cursor)localObject);
        } else {
          Log.e("TvDataManager", "error loading watch next data into cache, cursor is null");
        }
      }
    }
  }
  
  @MainThread
  public void onTaskFailed(DataLoadingBackgroundTask paramDataLoadingBackgroundTask, Throwable paramThrowable)
  {
    Log.e("TvDataManager", "onTaskFailed: " + paramDataLoadingBackgroundTask + ", throwable: " + paramThrowable);
    stopTrackingTask(paramDataLoadingBackgroundTask);
  }
  
  @WorkerThread
  public void onTaskPostProcess(DataLoadingBackgroundTask paramDataLoadingBackgroundTask)
  {
    if (paramDataLoadingBackgroundTask.getTag() == 0L)
    {
      localObject2 = paramDataLoadingBackgroundTask.getResult();
      if (localObject2 != null)
      {
        localObject1 = new ArrayList(((Cursor)localObject2).getCount());
        while (((Cursor)localObject2).moveToNext()) {
          ((List)localObject1).add(HomeChannel.fromCursor((Cursor)localObject2));
        }
        ((Cursor)localObject2).close();
        paramDataLoadingBackgroundTask.setExtraResult(localObject1);
      }
    }
    label144:
    label180:
    do
    {
      for (;;)
      {
        return;
        localObject1 = Collections.emptyList();
        Log.e("TvDataManager", "error loading home channels, cursor is null");
        break;
        if (paramDataLoadingBackgroundTask.getTag() != 1L) {
          break label180;
        }
        localObject1 = paramDataLoadingBackgroundTask.getResult();
        if (localObject1 == null) {
          break label144;
        }
        while (((Cursor)localObject1).moveToNext()) {
          this.mProgramChannelIds.put(Long.valueOf(((Cursor)localObject1).getLong(0)), Long.valueOf(((Cursor)localObject1).getLong(1)));
        }
      }
      Log.e("TvDataManager", "error loading programs for channel " + paramDataLoadingBackgroundTask.getExtraParam() + ", cursor is null");
      return;
      if (paramDataLoadingBackgroundTask.getTag() == 2L)
      {
        localObject1 = paramDataLoadingBackgroundTask.getResult();
        if (localObject1 != null)
        {
          localObject3 = new ArrayList(((Cursor)localObject1).getCount());
          HashMap localHashMap = new HashMap(((Cursor)localObject1).getCount());
          while (((Cursor)localObject1).moveToNext())
          {
            localObject2 = Channel.fromCursor((Cursor)localObject1);
            addToMultimap(localHashMap, ((Channel)localObject2).getPackageName(), localObject2);
          }
          ((Cursor)localObject1).close();
          Iterator localIterator = localHashMap.entrySet().iterator();
          for (;;)
          {
            localObject2 = localHashMap;
            localObject1 = localObject3;
            if (!localIterator.hasNext()) {
              break;
            }
            localObject1 = (Map.Entry)localIterator.next();
            ((List)localObject3).add(new ChannelPackage((String)((Map.Entry)localObject1).getKey(), ((List)((Map.Entry)localObject1).getValue()).size()));
          }
        }
        localObject1 = Collections.emptyList();
        localObject2 = Collections.emptyMap();
        Log.e("TvDataManager", "error loading all preview channels, cursor is null");
        paramDataLoadingBackgroundTask.setExtraResult(new Object[] { localObject1, localObject2 });
        return;
      }
    } while (paramDataLoadingBackgroundTask.getTag() != 3L);
    Object localObject2 = null;
    Object localObject1 = null;
    Object localObject3 = paramDataLoadingBackgroundTask.getResult();
    if (localObject3 != null)
    {
      if (((Cursor)localObject3).moveToFirst()) {
        localObject1 = Channel.fromCursor((Cursor)localObject3);
      }
      ((Cursor)localObject3).close();
    }
    for (;;)
    {
      paramDataLoadingBackgroundTask.setExtraResult(localObject1);
      return;
      Log.e("TvDataManager", "error loading promo channel, cursor is null");
      localObject1 = localObject2;
    }
  }
  
  public void pruneChannelProgramsCache()
  {
    Iterator localIterator = this.mChannelPrograms.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Long localLong = (Long)localEntry.getKey();
      if (!this.mChannelProgramsObservers.containsKey(localLong))
      {
        localIterator.remove();
        cleanupAfterChannelProgramsRemovalFromCache((ProgramsDataBuffer)localEntry.getValue(), localLong);
      }
    }
  }
  
  public void refreshWatchNextOffset()
  {
    if ((this.mWatchNextPrograms != null) && (this.mWatchNextPrograms.refresh())) {
      notifyWatchNextProgramsChange();
    }
  }
  
  public void registerChannelProgramsObserver(long paramLong, ChannelProgramsObserver paramChannelProgramsObserver)
  {
    addToMultimap(this.mChannelProgramsObservers, Long.valueOf(paramLong), paramChannelProgramsObserver);
    registerDataSourceObserver();
  }
  
  public void registerHomeChannelsObserver(HomeChannelsObserver paramHomeChannelsObserver)
  {
    if (!this.mHomeChannelsObservers.contains(paramHomeChannelsObserver)) {
      this.mHomeChannelsObservers.add(paramHomeChannelsObserver);
    }
    registerDataSourceObserver();
  }
  
  public void registerPackageChannelsObserver(PackageChannelsObserver paramPackageChannelsObserver)
  {
    if (!this.mPackageChannelsObservers.contains(paramPackageChannelsObserver)) {
      this.mPackageChannelsObservers.add(paramPackageChannelsObserver);
    }
    registerDataSourceObserver();
  }
  
  public void registerPackagesWithChannelsObserver(PackagesWithChannelsObserver paramPackagesWithChannelsObserver)
  {
    if (!this.mPackagesWithChannelsObservers.contains(paramPackagesWithChannelsObserver)) {
      this.mPackagesWithChannelsObservers.add(paramPackagesWithChannelsObserver);
    }
    registerDataSourceObserver();
  }
  
  public void registerPromoChannelObserver(PromoChannelObserver paramPromoChannelObserver)
  {
    if (!this.mPromoChannelObservers.contains(paramPromoChannelObserver)) {
      this.mPromoChannelObservers.add(paramPromoChannelObserver);
    }
    registerDataSourceObserver();
  }
  
  public void registerWatchNextProgramsObserver(WatchNextProgramsObserver paramWatchNextProgramsObserver)
  {
    if (!this.mWatchNextProgramsObservers.contains(paramWatchNextProgramsObserver)) {
      this.mWatchNextProgramsObservers.add(paramWatchNextProgramsObserver);
    }
    registerDataSourceObserver();
  }
  
  public void reloadWatchNextCache()
  {
    loadAllWatchNextProgramDataIntoCache();
  }
  
  public void removeHomeChannel(long paramLong)
  {
    Integer localInteger = this.mChannelOrderManager.getChannelPosition(paramLong);
    if (localInteger != null)
    {
      this.mHomeChannels.remove(localInteger.intValue());
      this.mChannelOrderManager.refreshChannelPositions();
    }
    setChannelBrowsable(paramLong, false);
  }
  
  public void removeProgramFromWatchNextCache(String paramString1, String paramString2)
  {
    if ((paramString1 != null) && (paramString2 != null)) {
      this.mWatchNextProgramsCache.remove(paramString1.concat(paramString2));
    }
  }
  
  public void setChannelBrowsable(final long paramLong, final boolean paramBoolean)
  {
    new AsyncTask()
    {
      protected Void doInBackground(Object... paramAnonymousVarArgs)
      {
        paramAnonymousVarArgs = new ContentValues();
        paramAnonymousVarArgs.put("browsable", Boolean.valueOf(paramBoolean));
        TvDataManager.this.mContext.getContentResolver().update(TvContract.buildChannelUri(paramLong), paramAnonymousVarArgs, null, null);
        return null;
      }
    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[] { Boolean.valueOf(paramBoolean), Long.valueOf(paramLong) });
  }
  
  public void unregisterChannelProgramsObserver(long paramLong, ChannelProgramsObserver paramChannelProgramsObserver)
  {
    DataLoadingBackgroundTask localDataLoadingBackgroundTask = (DataLoadingBackgroundTask)this.mChannelProgramsBackgroundTasks.remove(Long.valueOf(paramLong));
    if (localDataLoadingBackgroundTask != null) {
      localDataLoadingBackgroundTask.cancel();
    }
    removeFromMultimap(this.mChannelProgramsObservers, Long.valueOf(paramLong), paramChannelProgramsObserver);
    unregisterDataSourceObserverIfNoObservers();
  }
  
  public void unregisterHomeChannelsObserver(HomeChannelsObserver paramHomeChannelsObserver)
  {
    if (this.mHomeChannelsBackgroundTask != null)
    {
      this.mHomeChannelsBackgroundTask.cancel();
      this.mHomeChannelsBackgroundTask = null;
    }
    this.mHomeChannelsObservers.remove(paramHomeChannelsObserver);
    unregisterDataSourceObserverIfNoObservers();
  }
  
  public void unregisterPackageChannelsObserver(PackageChannelsObserver paramPackageChannelsObserver)
  {
    this.mPackageChannelsObservers.remove(paramPackageChannelsObserver);
    clearPackageChannelsCacheAndCancelTasksIfNoObservers();
    unregisterDataSourceObserverIfNoObservers();
  }
  
  public void unregisterPackagesWithChannelsObserver(PackagesWithChannelsObserver paramPackagesWithChannelsObserver)
  {
    this.mPackagesWithChannelsObservers.remove(paramPackagesWithChannelsObserver);
    clearPackageChannelsCacheAndCancelTasksIfNoObservers();
    unregisterDataSourceObserverIfNoObservers();
  }
  
  public void unregisterPromoChannelObserver(PromoChannelObserver paramPromoChannelObserver)
  {
    if (this.mPromoChannelBackgroundTask != null)
    {
      this.mPromoChannelBackgroundTask.cancel();
      this.mPromoChannelBackgroundTask = null;
    }
    this.mPromoChannelObservers.remove(paramPromoChannelObserver);
    if (this.mPromoChannelObservers.size() == 0)
    {
      this.mPromoChannel = null;
      this.mPromoChannelLoaded = false;
    }
    unregisterDataSourceObserverIfNoObservers();
  }
  
  public void unregisterWatchNextProgramsObserver(WatchNextProgramsObserver paramWatchNextProgramsObserver)
  {
    if (this.mWatchNextProgramsBackgroundTask != null)
    {
      this.mWatchNextProgramsBackgroundTask.cancel();
      this.mWatchNextProgramsBackgroundTask = null;
    }
    this.mWatchNextProgramsObservers.remove(paramWatchNextProgramsObserver);
    unregisterDataSourceObserverIfNoObservers();
  }
  
  private class DataSourceObserverCallbacks
    implements DataSourceObserver.Callbacks
  {
    private DataSourceObserverCallbacks() {}
    
    private void reloadProgramsOrMarkStale(Long paramLong)
    {
      if (TvDataManager.this.mChannelProgramsObservers.containsKey(paramLong))
      {
        TvDataManager.this.loadChannelProgramData(paramLong.longValue());
        return;
      }
      TvDataManager.this.mStaleProgramsChannels.add(paramLong);
    }
    
    public boolean areProgramsCached(long paramLong)
    {
      return TvDataManager.this.mChannelPrograms.containsKey(Long.valueOf(paramLong));
    }
    
    public Long findCachedChannelId(long paramLong)
    {
      return (Long)TvDataManager.this.mProgramChannelIds.get(Long.valueOf(paramLong));
    }
    
    public void invalidateAllChannels()
    {
      if (TvDataManager.this.mHomeChannelsObservers.size() > 0) {
        TvDataManager.this.loadHomeChannelDataInternal();
      }
      for (;;)
      {
        if ((TvDataManager.this.mPackagesWithChannelsObservers.size() > 0) || (TvDataManager.this.mPackageChannelsObservers.size() > 0)) {
          TvDataManager.this.loadAllPreviewChannelsData();
        }
        if ((TvDataManager.this.mPromoChannelObservers.size() > 0) && (Partner.get(TvDataManager.this.mContext).getAppsPromotionRowPackage() != null)) {
          TvDataManager.this.loadPromoChannelData();
        }
        return;
        TvDataManager.access$802(TvDataManager.this, true);
      }
    }
    
    public void invalidateAllPrograms()
    {
      Iterator localIterator = TvDataManager.this.mChannelPrograms.keySet().iterator();
      while (localIterator.hasNext()) {
        reloadProgramsOrMarkStale((Long)localIterator.next());
      }
    }
    
    public void invalidateChannelLogo(long paramLong)
    {
      TvDataManager.this.mChannelLogoUris.remove(Long.valueOf(paramLong));
    }
    
    public void invalidateChannelPrograms(long paramLong)
    {
      if (TvDataManager.this.mChannelPrograms.containsKey(Long.valueOf(paramLong))) {
        reloadProgramsOrMarkStale(Long.valueOf(paramLong));
      }
    }
    
    public void invalidateWatchNextPrograms()
    {
      if (TvDataManager.this.mWatchNextProgramsObservers.size() > 0)
      {
        TvDataManager.this.loadWatchNextProgramDataInternal();
        return;
      }
      TvDataManager.access$2102(TvDataManager.this, true);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/data/TvDataManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */