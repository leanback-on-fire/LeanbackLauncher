package com.google.android.tvlauncher.home;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.FacetProvider;
import android.support.v17.leanback.widget.FacetProviderAdapter;
import android.support.v17.leanback.widget.ItemAlignmentFacet;
import android.support.v17.leanback.widget.ItemAlignmentFacet.ItemAlignmentDef;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener;
import android.widget.Toast;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnBackNotHandledListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnBackPressedListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnHomeNotHandledListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnHomePressedListener;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEventParameters;
import com.google.android.tvlauncher.analytics.UserActionEvent;
import com.google.android.tvlauncher.data.HomeChannelsObserver;
import com.google.android.tvlauncher.data.TvDataManager;
import com.google.android.tvlauncher.model.HomeChannel;
import com.google.android.tvlauncher.model.Program;
import com.google.android.tvlauncher.notifications.NotificationsPanelController;
import com.google.android.tvlauncher.notifications.NotificationsTrayAdapter;
import com.google.android.tvlauncher.util.GservicesUtils;
import com.google.android.tvlauncher.util.Util;
import com.google.android.tvlauncher.util.palette.PaletteUtil;
import com.google.android.tvlauncher.view.HomeTopRowView;
import com.google.android.tvlauncher.view.HomeTopRowView.OnActionListener;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class HomeController
  extends RecyclerView.Adapter<HomeRowViewHolder>
  implements HomeTopRowView.OnActionListener, FacetProviderAdapter, BackHomeControllerListeners.OnBackPressedListener, BackHomeControllerListeners.OnBackNotHandledListener, BackHomeControllerListeners.OnHomePressedListener, BackHomeControllerListeners.OnHomeNotHandledListener, AccessibilityManager.AccessibilityStateChangeListener
{
  private static final int ACCESSIBILITY_APP_SELECT_DELAY_MILLIS = 50;
  private static final int BACKGROUND_UPDATE_DELAY_MILLIS = 2000;
  private static final boolean DEBUG = false;
  private static final String INPUTS_PANEL_INTENT_ACTION = "com.google.android.tvlauncher.INPUTS";
  private static final int MAX_SMOOTH_SCROLL_DISTANCE = 6;
  private static final int NUMBER_OF_ROWS_ABOVE_CHANNELS_WITHOUT_WATCH_NEXT = 2;
  private static final int NUMBER_OF_ROWS_ABOVE_CHANNELS_WITH_WATCH_NEXT = 3;
  private static final int NUMBER_OF_ROWS_BELOW_CHANNELS = 1;
  private static final String PAYLOAD_CHANNEL_ITEM_METADATA = "PAYLOAD_CHANNEL_ITEM_METADATA";
  private static final String PAYLOAD_CHANNEL_MOVE_ACTION = "PAYLOAD_CHANNEL_MOVE_ACTION";
  private static final String PAYLOAD_STATE = "PAYLOAD_STATE";
  private static final int REFRESH_WATCH_NEXT_OFFSET_INTERVAL_MILLIS = 60000;
  private static final int ROW_TYPE_APPS = 1;
  private static final int ROW_TYPE_APPS_POSITION = 1;
  private static final int ROW_TYPE_CHANNEL = 3;
  private static final int ROW_TYPE_CONFIGURE_CHANNELS = 4;
  private static final int ROW_TYPE_TOP = 0;
  private static final int ROW_TYPE_TOP_POSITION = 0;
  private static final int ROW_TYPE_WATCH_NEXT = 2;
  private static final int ROW_TYPE_WATCH_NEXT_POSITION = 2;
  static final int STATE_CHANNEL_ACTIONS = 2;
  static final int STATE_DEFAULT = 0;
  static final int STATE_MOVE_CHANNEL = 3;
  static final int STATE_ZOOMED_OUT = 1;
  private static final String TAG = "HomeController";
  private static final int THRESHOLD_HOME_PRESS_PAUSE_INTERVAL_MILLIS = 200;
  private Set<ChannelRowController> mActiveChannelRowControllers = new HashSet();
  private Set<WatchNextRowController> mActiveWatchNextRowControllers = new HashSet();
  private Set<String> mAddToWatchNextPackagesBlacklist;
  private HomeBackgroundController mBackgroundController;
  private RequestManager mChannelLogoRequestManager;
  private final HomeChannelsObserver mChannelsObserver = new HomeChannelsObserver()
  {
    public void onChannelMove(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      int i = HomeController.this.getAdapterPositionForChannelIndex(paramAnonymousInt1);
      int j = HomeController.this.getAdapterPositionForChannelIndex(paramAnonymousInt2);
      if (HomeController.this.mSelectedPosition == i) {
        HomeController.access$602(HomeController.this, j);
      }
      int k = HomeController.this.getChannelCount();
      if ((paramAnonymousInt1 == 0) || (paramAnonymousInt2 == 0) || (paramAnonymousInt1 == k - 1) || (paramAnonymousInt2 == k - 1))
      {
        HomeController.this.notifyItemChanged(i, "PAYLOAD_CHANNEL_MOVE_ACTION");
        HomeController.this.notifyItemChanged(j, "PAYLOAD_CHANNEL_MOVE_ACTION");
      }
      HomeController.this.notifyItemMoved(i, j);
    }
    
    public void onChannelsChange()
    {
      int i = HomeController.this.mDataManager.getHomeChannelCount();
      if (i >= 0) {
        HomeController.this.mEventLogger.log(new LogEventParameters("open_home").putParameter("shown_channel_count", i));
      }
      HomeController.this.notifyDataSetChanged();
    }
  };
  private FacetProvider mConfigureChannelsRowAlignmentFacetProvider;
  private final Context mContext;
  private final TvDataManager mDataManager;
  private int mDefaultChannelKeyline;
  private final EventLogger mEventLogger;
  private Handler mHandler = new Handler();
  private long mLastPausedTime;
  private ProgramController mLastSelectedProgramController;
  private VerticalGridView mList;
  private Cursor mNotifCountCursor = null;
  private Cursor mNotifTrayCursor = null;
  private Set<NotificationsPanelController> mNotificationsPanelControllers = new HashSet();
  private Set<NotificationsTrayAdapter> mNotificationsTrayAdapters = new HashSet();
  private final Runnable mNotifyChannelItemMetadataChangedRunnable = new Runnable()
  {
    public void run()
    {
      HomeController.this.notifyItemChanged(HomeController.this.mSelectedPosition, "PAYLOAD_CHANNEL_ITEM_METADATA");
    }
  };
  private final Runnable mNotifySelectionChangedRunnable = new Runnable()
  {
    public void run()
    {
      Object localObject = new HashSet(HomeController.this.mPrevSelectedPositions.size() + 5);
      if ((HomeController.this.mState == 0) || (HomeController.this.mState == 1))
      {
        ((Set)localObject).addAll(HomeController.this.mPrevSelectedPositions);
        HomeController.this.mPrevSelectedPositions.clear();
        ((Set)localObject).add(Integer.valueOf(HomeController.this.mSelectedPosition));
        ((Set)localObject).add(Integer.valueOf(HomeController.this.mSelectedPosition - 1));
        ((Set)localObject).add(Integer.valueOf(HomeController.this.mSelectedPosition + 1));
      }
      if (HomeController.this.mState == 0)
      {
        if ((HomeController.this.mSelectedPosition == 0) || (HomeController.this.mSelectedPosition == 1)) {
          ((Set)localObject).add(Integer.valueOf(HomeController.this.mSelectedPosition + 2));
        }
        if (HomeController.this.mSelectedPosition == 0) {
          ((Set)localObject).add(Integer.valueOf(HomeController.this.mSelectedPosition + 3));
        }
      }
      int i = HomeController.this.getItemCount();
      localObject = ((Set)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        Integer localInteger = (Integer)((Iterator)localObject).next();
        if ((localInteger.intValue() >= 0) && (localInteger.intValue() < i)) {
          HomeController.this.notifyItemChanged(localInteger.intValue(), "PAYLOAD_STATE");
        }
      }
    }
  };
  private BackHomeControllerListeners.OnBackNotHandledListener mOnBackNotHandledListener;
  private OnProgramSelectedListener mOnProgramSelectedListener = new OnProgramSelectedListener()
  {
    public void onProgramSelected(Program paramAnonymousProgram, ProgramController paramAnonymousProgramController)
    {
      HomeController.access$1102(HomeController.this, paramAnonymousProgramController);
      if (HomeController.this.mBackgroundController != null)
      {
        HomeController.this.mHandler.removeCallbacks(HomeController.this.mUpdateBackgroundForProgramAfterDelayRunnable);
        HomeController.this.mHandler.postDelayed(HomeController.this.mUpdateBackgroundForProgramAfterDelayRunnable, 2000L);
      }
      HomeController.this.mHandler.removeCallbacks(HomeController.this.mNotifyChannelItemMetadataChangedRunnable);
      HomeController.this.mHandler.post(HomeController.this.mNotifyChannelItemMetadataChangedRunnable);
    }
  };
  private HashSet<Integer> mPrevSelectedPositions = new HashSet();
  private Runnable mRefreshWatchNextOffset = new Runnable()
  {
    public void run()
    {
      HomeController.this.mDataManager.refreshWatchNextOffset();
      HomeController.this.mWatchNextHandler.postDelayed(HomeController.this.mRefreshWatchNextOffset, 60000L);
    }
  };
  private Set<String> mRemoveProgramPackagesBlacklist;
  private Runnable mRequeryWatchNext = new Runnable()
  {
    public void run()
    {
      HomeController.this.mDataManager.loadWatchNextProgramData();
      HomeController.this.mWatchNextHandler.postDelayed(HomeController.this.mRequeryWatchNext, 600000L);
    }
  };
  private Runnable mSelectFirstAppRunnable = new Runnable()
  {
    public void run()
    {
      if ((HomeController.this.mSelectFirstAppWhenRowSelected) && (HomeController.this.mSelectedPosition == 1))
      {
        HomeRow localHomeRow = HomeController.this.getHomeRow(1);
        if ((localHomeRow instanceof AppRowController)) {
          ((AppRowController)localHomeRow).setSelectedItemPosition(0);
        }
      }
      HomeController.access$1202(HomeController.this, false);
    }
  };
  private boolean mSelectFirstAppWhenRowSelected;
  private int mSelectedPosition = 1;
  private boolean mStarted;
  private int mState = 0;
  private final Runnable mUpdateBackgroundForProgramAfterDelayRunnable = new Runnable()
  {
    public void run()
    {
      if ((HomeController.this.mBackgroundController != null) && (HomeController.this.mLastSelectedProgramController != null) && (HomeController.this.mLastSelectedProgramController.isViewFocused()) && (HomeController.this.mLastSelectedProgramController.getPreviewImagePalette() != null)) {
        HomeController.this.mBackgroundController.updateBackground(HomeController.this.mLastSelectedProgramController.getPreviewImagePalette());
      }
    }
  };
  private final Runnable mUpdateBackgroundForTopRowsAfterDelayRunnable = new Runnable()
  {
    public void run()
    {
      if ((HomeController.this.mBackgroundController != null) && (HomeController.this.mSelectedPosition <= 1)) {
        HomeController.this.mBackgroundController.enterDarkMode();
      }
    }
  };
  private Handler mWatchNextHandler = new Handler();
  private SharedPreferences.OnSharedPreferenceChangeListener mWatchNextPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener()
  {
    public void onSharedPreferenceChanged(SharedPreferences paramAnonymousSharedPreferences, String paramAnonymousString)
    {
      if ("show_watch_next_row_key".equals(paramAnonymousString))
      {
        boolean bool = paramAnonymousSharedPreferences.getBoolean("show_watch_next_row_key", true);
        if (HomeController.this.mWatchNextVisible != bool)
        {
          HomeController.access$1402(HomeController.this, bool);
          if (!HomeController.this.mWatchNextVisible) {
            break label58;
          }
          HomeController.this.notifyItemInserted(2);
        }
      }
      return;
      label58:
      HomeController.this.notifyItemRemoved(2);
    }
  };
  private boolean mWatchNextVisible;
  private int mZoomedOutChannelKeyline;
  
  HomeController(Context paramContext, EventLogger paramEventLogger)
  {
    this.mContext = paramContext;
    this.mEventLogger = paramEventLogger;
    this.mDataManager = TvDataManager.getInstance(this.mContext);
    this.mDataManager.registerHomeChannelsObserver(this.mChannelsObserver);
    paramEventLogger = new ItemAlignmentFacet.ItemAlignmentDef();
    paramEventLogger.setItemAlignmentViewId(2131951826);
    final ItemAlignmentFacet localItemAlignmentFacet = new ItemAlignmentFacet();
    localItemAlignmentFacet.setAlignmentDefs(new ItemAlignmentFacet.ItemAlignmentDef[] { paramEventLogger });
    this.mConfigureChannelsRowAlignmentFacetProvider = new FacetProvider()
    {
      public Object getFacet(Class<?> paramAnonymousClass)
      {
        return localItemAlignmentFacet;
      }
    };
    this.mDefaultChannelKeyline = this.mContext.getResources().getDimensionPixelSize(2131558518);
    this.mZoomedOutChannelKeyline = this.mContext.getResources().getDimensionPixelSize(2131558522);
    setHasStableIds(true);
    registerChannelsObserverAndLoadDataIfNeeded();
    this.mStarted = true;
    paramContext = paramContext.getSharedPreferences("com.google.android.tvlauncher.data.TvDataManager.WATCH_NEXT_PREF_FILE_NAME", 0);
    this.mWatchNextVisible = paramContext.getBoolean("show_watch_next_row_key", true);
    paramContext.registerOnSharedPreferenceChangeListener(this.mWatchNextPrefListener);
    PaletteUtil.registerGlidePaletteTranscoder(this.mContext);
    this.mAddToWatchNextPackagesBlacklist = GservicesUtils.retrievePackagesBlacklistedForWatchNext(this.mContext.getContentResolver());
    this.mRemoveProgramPackagesBlacklist = GservicesUtils.retrievePackagesBlacklistedForProgramRemoval(this.mContext.getContentResolver());
  }
  
  private int getAdapterPositionForChannelIndex(int paramInt)
  {
    return getNumberOfRowsAboveChannels() + paramInt;
  }
  
  private int getChannelCount()
  {
    if (this.mDataManager.isHomeChannelDataLoaded()) {
      return this.mDataManager.getHomeChannelCount();
    }
    return 0;
  }
  
  private int getChannelIndexForAdapterPosition(int paramInt)
  {
    return paramInt - getNumberOfRowsAboveChannels();
  }
  
  private int getChannelState(int paramInt)
  {
    if (this.mSelectedPosition == paramInt) {}
    for (int i = 1;; i = 0) {
      switch (this.mState)
      {
      default: 
        return 1;
      }
    }
    if (i != 0) {
      return 4;
    }
    return 5;
    if (i != 0) {
      return 6;
    }
    return 7;
    if (i != 0) {
      return 8;
    }
    return 9;
    if (i != 0) {
      return 0;
    }
    if (paramInt == this.mSelectedPosition - 1) {
      return 2;
    }
    if ((paramInt == this.mSelectedPosition + 1) && (paramInt > 2)) {
      return 3;
    }
    return 1;
  }
  
  @Nullable
  private HomeRow getHomeRow(int paramInt)
  {
    HomeRowViewHolder localHomeRowViewHolder = (HomeRowViewHolder)this.mList.findViewHolderForAdapterPosition(paramInt);
    if (localHomeRowViewHolder != null) {
      return localHomeRowViewHolder.homeRow;
    }
    return null;
  }
  
  private int getNumberOfRowsAboveChannels()
  {
    if (this.mWatchNextVisible) {
      return 3;
    }
    return 2;
  }
  
  @Nullable
  private HomeRow getSelectedHomeRow()
  {
    return getHomeRow(this.mSelectedPosition);
  }
  
  private void onBindChannel(ChannelRowController paramChannelRowController, int paramInt1, int paramInt2)
  {
    boolean bool2 = true;
    HomeChannel localHomeChannel = this.mDataManager.getHomeChannel(paramInt1);
    paramInt1 = getChannelState(paramInt2);
    boolean bool1;
    if (!this.mAddToWatchNextPackagesBlacklist.contains(localHomeChannel.getPackageName()))
    {
      bool1 = true;
      if (this.mRemoveProgramPackagesBlacklist.contains(localHomeChannel.getPackageName())) {
        break label85;
      }
    }
    for (;;)
    {
      paramChannelRowController.bindChannel(localHomeChannel, paramInt1, bool1, bool2);
      this.mActiveChannelRowControllers.add(paramChannelRowController);
      return;
      bool1 = false;
      break;
      label85:
      bool2 = false;
    }
  }
  
  private void onItemRemoved(int paramInt)
  {
    if ((this.mWatchNextVisible) && (paramInt == 2))
    {
      this.mWatchNextVisible = false;
      this.mContext.getSharedPreferences("com.google.android.tvlauncher.data.TvDataManager.WATCH_NEXT_PREF_FILE_NAME", 0).edit().putBoolean("show_watch_next_row_key", false).apply();
    }
    if (paramInt == getItemCount() - 1) {
      this.mSelectedPosition = (paramInt - 1);
    }
    if (!Util.isAccessibilityEnabled(this.mContext)) {
      this.mState = 1;
    }
    notifyDataSetChanged();
  }
  
  private void registerChannelsObserverAndLoadDataIfNeeded()
  {
    this.mDataManager.registerHomeChannelsObserver(this.mChannelsObserver);
    if ((!this.mDataManager.isHomeChannelDataLoaded()) || (this.mDataManager.isHomeChannelDataStale())) {
      this.mDataManager.loadHomeChannelData();
    }
  }
  
  private void scrollToAppsRow()
  {
    this.mSelectFirstAppWhenRowSelected = true;
    if (this.mList.getSelectedPosition() > 6)
    {
      this.mList.setSelectedPosition(1);
      return;
    }
    this.mList.setSelectedPositionSmooth(1);
  }
  
  private void setSelectedPosition(int paramInt)
  {
    if (this.mSelectedPosition != paramInt)
    {
      if ((this.mState == 0) || (this.mState == 1))
      {
        this.mPrevSelectedPositions.add(Integer.valueOf(this.mSelectedPosition));
        this.mHandler.post(this.mNotifySelectionChangedRunnable);
      }
      this.mSelectedPosition = paramInt;
      if ((this.mSelectedPosition > 1) || (this.mBackgroundController == null)) {
        break label123;
      }
      this.mHandler.postDelayed(this.mUpdateBackgroundForTopRowsAfterDelayRunnable, 2000L);
    }
    for (;;)
    {
      if ((this.mSelectFirstAppWhenRowSelected) && (paramInt == 1))
      {
        if (!Util.isAccessibilityEnabled(this.mContext)) {
          break;
        }
        this.mHandler.postDelayed(this.mSelectFirstAppRunnable, 50L);
      }
      return;
      label123:
      this.mHandler.removeCallbacks(this.mUpdateBackgroundForTopRowsAfterDelayRunnable);
    }
    this.mSelectFirstAppRunnable.run();
  }
  
  private void setState(int paramInt)
  {
    int i = paramInt;
    if (Util.isAccessibilityEnabled(this.mContext)) {}
    switch (paramInt)
    {
    default: 
      i = paramInt;
      if (this.mState != i) {
        switch (this.mState)
        {
        case 0: 
        default: 
          label84:
          this.mState = i;
          switch (this.mState)
          {
          }
          break;
        }
      }
      break;
    }
    for (;;)
    {
      notifyItemRangeChanged(0, getItemCount(), "PAYLOAD_STATE");
      return;
      i = 0;
      break;
      this.mEventLogger.log(new UserActionEvent("exit_zoomed_out_mode"));
      break label84;
      this.mEventLogger.log(new UserActionEvent("exit_channel_actions_mode"));
      break label84;
      this.mEventLogger.log(new UserActionEvent("exit_move_channel_mode"));
      break label84;
      this.mList.setWindowAlignmentOffset(this.mDefaultChannelKeyline);
      continue;
      this.mList.setWindowAlignmentOffset(this.mZoomedOutChannelKeyline);
      this.mEventLogger.log(new UserActionEvent("enter_zoomed_out_mode"));
      continue;
      this.mList.setWindowAlignmentOffset(this.mZoomedOutChannelKeyline);
      this.mEventLogger.log(new UserActionEvent("enter_channel_actions_mode"));
      continue;
      this.mList.setWindowAlignmentOffset(this.mZoomedOutChannelKeyline);
      this.mEventLogger.log(new UserActionEvent("enter_move_channel_mode"));
    }
  }
  
  private static String stateToString(int paramInt)
  {
    String str;
    switch (paramInt)
    {
    default: 
      str = "STATE_UNKNOWN";
    }
    for (;;)
    {
      return str + " (" + paramInt + ")";
      str = "STATE_DEFAULT";
      continue;
      str = "STATE_ZOOMED_OUT";
      continue;
      str = "STATE_CHANNEL_ACTIONS";
      continue;
      str = "STATE_MOVE_CHANNEL";
    }
  }
  
  public FacetProvider getFacetProvider(int paramInt)
  {
    if (paramInt == 4) {
      return this.mConfigureChannelsRowAlignmentFacetProvider;
    }
    return null;
  }
  
  public int getItemCount()
  {
    return getChannelCount() + getNumberOfRowsAboveChannels() + 1;
  }
  
  public long getItemId(int paramInt)
  {
    switch (getItemViewType(paramInt))
    {
    default: 
      return super.getItemId(paramInt);
    case 0: 
      return -2L;
    case 1: 
      return -3L;
    case 2: 
      return -4L;
    case 4: 
      return -5L;
    }
    paramInt = getChannelIndexForAdapterPosition(paramInt);
    return this.mDataManager.getHomeChannel(paramInt).getId();
  }
  
  public int getItemViewType(int paramInt)
  {
    int i = 1;
    if (paramInt == getItemCount() - 1) {
      i = 4;
    }
    do
    {
      return i;
      if (paramInt == 0) {
        return 0;
      }
    } while (paramInt == 1);
    if ((paramInt == 2) && (this.mWatchNextVisible)) {
      return 2;
    }
    return 3;
  }
  
  public void onAccessibilityStateChanged(boolean paramBoolean)
  {
    notifyItemChanged(0);
  }
  
  public void onBackNotHandled(Context paramContext)
  {
    if ((this.mList.getSelectedPosition() != 1) && ((this.mState == 0) || (this.mState == 1))) {
      scrollToAppsRow();
    }
    while (this.mOnBackNotHandledListener == null) {
      return;
    }
    this.mOnBackNotHandledListener.onBackNotHandled(paramContext);
  }
  
  public void onBackPressed(Context paramContext)
  {
    HomeRow localHomeRow = getSelectedHomeRow();
    if ((localHomeRow instanceof BackHomeControllerListeners.OnBackPressedListener))
    {
      ((BackHomeControllerListeners.OnBackPressedListener)localHomeRow).onBackPressed(paramContext);
      return;
    }
    onBackNotHandled(paramContext);
  }
  
  public void onBindViewHolder(HomeRowViewHolder paramHomeRowViewHolder, int paramInt)
  {
    boolean bool2 = true;
    int i = getItemViewType(paramInt);
    if (i == 2)
    {
      paramHomeRowViewHolder = (WatchNextRowController)paramHomeRowViewHolder.homeRow;
      paramHomeRowViewHolder.bind(getChannelState(paramInt));
      this.mActiveWatchNextRowControllers.add(paramHomeRowViewHolder);
    }
    do
    {
      return;
      if (i == 1)
      {
        ((AppRowController)paramHomeRowViewHolder.homeRow).bind(getChannelState(paramInt));
        return;
      }
      if (i == 4)
      {
        ((ConfigureChannelsRowController)paramHomeRowViewHolder.homeRow).bind(this.mState);
        return;
      }
      if (i == 3)
      {
        onBindChannel((ChannelRowController)paramHomeRowViewHolder.homeRow, getChannelIndexForAdapterPosition(paramInt), paramInt);
        return;
      }
    } while (i != 0);
    paramHomeRowViewHolder = (HomeTopRowView)paramHomeRowViewHolder.homeRow.getView();
    this.mNotificationsTrayAdapters.add(paramHomeRowViewHolder.getNotificationsTrayAdapter());
    this.mNotificationsPanelControllers.add(paramHomeRowViewHolder.getNotificationsPanelController());
    paramHomeRowViewHolder.getNotificationsTrayAdapter().changeCursor(this.mNotifTrayCursor);
    paramHomeRowViewHolder.getNotificationsPanelController().updateNotificationsCount(this.mNotifCountCursor);
    paramHomeRowViewHolder.updateNotificationsTrayVisibility();
    boolean bool1;
    if (this.mState == 1)
    {
      bool1 = true;
      if (this.mSelectedPosition != paramInt) {
        break label212;
      }
    }
    for (;;)
    {
      paramHomeRowViewHolder.setState(bool1, bool2);
      return;
      bool1 = false;
      break;
      label212:
      bool2 = false;
    }
  }
  
  public void onBindViewHolder(HomeRowViewHolder paramHomeRowViewHolder, int paramInt, List<Object> paramList)
  {
    boolean bool2 = true;
    if (paramList.isEmpty()) {
      onBindViewHolder(paramHomeRowViewHolder, paramInt);
    }
    label148:
    label218:
    label244:
    label257:
    for (;;)
    {
      return;
      int i = getItemViewType(paramInt);
      Object localObject;
      if (paramList.contains("PAYLOAD_CHANNEL_ITEM_METADATA"))
      {
        if (i == 3) {
          ((ChannelRowController)paramHomeRowViewHolder.homeRow).bindItemMetadata();
        }
      }
      else if (paramList.contains("PAYLOAD_STATE"))
      {
        if (i != 2) {
          break label148;
        }
        localObject = (WatchNextRowController)paramHomeRowViewHolder.homeRow;
        ((WatchNextRowController)localObject).setState(getChannelState(paramInt));
        this.mActiveWatchNextRowControllers.add(localObject);
      }
      for (;;)
      {
        if ((!paramList.contains("PAYLOAD_CHANNEL_MOVE_ACTION")) || (i != 3)) {
          break label257;
        }
        ((ChannelRowController)paramHomeRowViewHolder.homeRow).bindChannelMoveAction();
        return;
        if (i != 2) {
          break;
        }
        ((WatchNextRowController)paramHomeRowViewHolder.homeRow).bindItemMetadata();
        break;
        if (i == 3)
        {
          localObject = (ChannelRowController)paramHomeRowViewHolder.homeRow;
          ((ChannelRowController)localObject).setState(getChannelState(paramInt));
          this.mActiveChannelRowControllers.add(localObject);
        }
        else
        {
          if (i == 0)
          {
            localObject = (HomeTopRowView)paramHomeRowViewHolder.homeRow.getView();
            boolean bool1;
            if (this.mState == 1)
            {
              bool1 = true;
              if (this.mSelectedPosition != paramInt) {
                break label244;
              }
            }
            for (;;)
            {
              ((HomeTopRowView)localObject).setState(bool1, bool2);
              break;
              bool1 = false;
              break label218;
              bool2 = false;
            }
          }
          onBindViewHolder(paramHomeRowViewHolder, paramInt);
        }
      }
    }
  }
  
  public HomeRowViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    Object localObject = LayoutInflater.from(paramViewGroup.getContext());
    if (paramInt == 0)
    {
      localObject = (HomeTopRowView)((LayoutInflater)localObject).inflate(2130968624, paramViewGroup, false);
      ((HomeTopRowView)localObject).setOnActionListener(this);
      NotificationsPanelController localNotificationsPanelController = new NotificationsPanelController(paramViewGroup.getContext(), this.mEventLogger);
      ((HomeTopRowView)localObject).setNotificationsTrayAdapter(new NotificationsTrayAdapter(paramViewGroup.getContext(), this.mEventLogger, this.mNotifTrayCursor));
      ((HomeTopRowView)localObject).setNotificationsPanelController(localNotificationsPanelController);
      return new HomeRowViewHolder((HomeRow)localObject);
    }
    if (paramInt == 1)
    {
      paramViewGroup = ((LayoutInflater)localObject).inflate(2130968621, paramViewGroup, false);
      paramViewGroup.setId(2131951620);
      paramViewGroup = new AppRowController((ChannelView)paramViewGroup, this.mEventLogger);
      paramViewGroup.setOnBackNotHandledListener(this);
      paramViewGroup.setOnHomeNotHandledListener(this);
      return new HomeRowViewHolder(paramViewGroup);
    }
    if (paramInt == 2)
    {
      paramViewGroup = new WatchNextRowController((ChannelView)((LayoutInflater)localObject).inflate(2130968621, paramViewGroup, false), this.mEventLogger);
      paramViewGroup.setOnProgramSelectedListener(this.mOnProgramSelectedListener);
      paramViewGroup.setOnBackNotHandledListener(this);
      paramViewGroup.setOnHomeNotHandledListener(this);
      return new HomeRowViewHolder(paramViewGroup);
    }
    if (paramInt == 4) {
      return new HomeRowViewHolder(new ConfigureChannelsRowController(((LayoutInflater)localObject).inflate(2130968622, paramViewGroup, false)));
    }
    paramViewGroup = new ChannelRowController((ChannelView)((LayoutInflater)localObject).inflate(2130968621, paramViewGroup, false), this.mChannelLogoRequestManager, this.mEventLogger);
    paramViewGroup.setOnProgramSelectedListener(this.mOnProgramSelectedListener);
    paramViewGroup.setOnBackNotHandledListener(this);
    paramViewGroup.setOnHomeNotHandledListener(this);
    return new HomeRowViewHolder(paramViewGroup);
  }
  
  public void onHomeNotHandled(Context paramContext)
  {
    if ((this.mList.getSelectedPosition() != 1) && ((this.mState == 0) || (this.mState == 1))) {
      scrollToAppsRow();
    }
  }
  
  public void onHomePressed(Context paramContext)
  {
    if (SystemClock.elapsedRealtime() - this.mLastPausedTime > 200L) {
      return;
    }
    HomeRow localHomeRow = getSelectedHomeRow();
    if ((localHomeRow instanceof BackHomeControllerListeners.OnHomePressedListener))
    {
      ((BackHomeControllerListeners.OnHomePressedListener)localHomeRow).onHomePressed(paramContext);
      return;
    }
    onHomeNotHandled(paramContext);
  }
  
  void onPause()
  {
    if (this.mLastSelectedProgramController != null) {
      this.mLastSelectedProgramController.stopPreviewVideo(true);
    }
    this.mWatchNextHandler.removeCallbacks(this.mRefreshWatchNextOffset);
    this.mWatchNextHandler.removeCallbacks(this.mRequeryWatchNext);
    this.mLastPausedTime = SystemClock.elapsedRealtime();
  }
  
  void onResume()
  {
    this.mWatchNextHandler.postDelayed(this.mRefreshWatchNextOffset, 60000L);
    this.mWatchNextHandler.postDelayed(this.mRequeryWatchNext, 600000L);
    if (this.mDataManager.isHomeChannelDataLoaded()) {
      this.mEventLogger.log(new LogEventParameters("open_home").putParameter("shown_channel_count", this.mDataManager.getHomeChannelCount()));
    }
  }
  
  public void onShowInputs()
  {
    try
    {
      Intent localIntent = new Intent("com.google.android.tvlauncher.INPUTS");
      this.mContext.startActivity(localIntent);
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Log.e("HomeController", "InputsPanelActivity not found :  " + localActivityNotFoundException);
    }
  }
  
  void onStart()
  {
    ActiveMediaSessionManager.getInstance(this.mContext).start();
    Object localObject = this.mActiveWatchNextRowControllers.iterator();
    while (((Iterator)localObject).hasNext()) {
      ((WatchNextRowController)((Iterator)localObject).next()).onStart();
    }
    localObject = this.mActiveChannelRowControllers.iterator();
    while (((Iterator)localObject).hasNext()) {
      ((ChannelRowController)((Iterator)localObject).next()).onStart();
    }
    localObject = (AccessibilityManager)this.mContext.getSystemService("accessibility");
    if (localObject != null) {
      ((AccessibilityManager)localObject).addAccessibilityStateChangeListener(this);
    }
    if (!this.mStarted)
    {
      registerChannelsObserverAndLoadDataIfNeeded();
      this.mStarted = true;
    }
  }
  
  public void onStartSettings()
  {
    this.mEventLogger.log(new UserActionEvent("start_settings"));
    try
    {
      this.mContext.startActivity(new Intent("android.settings.SETTINGS"));
      return;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Log.e("HomeController", "Exception starting settings", localActivityNotFoundException);
      Toast.makeText(this.mContext, this.mContext.getString(2131492896), 0).show();
    }
  }
  
  void onStop()
  {
    ActiveMediaSessionManager.getInstance(this.mContext).stop();
    this.mDataManager.unregisterHomeChannelsObserver(this.mChannelsObserver);
    this.mDataManager.pruneChannelProgramsCache();
    Object localObject = this.mActiveWatchNextRowControllers.iterator();
    while (((Iterator)localObject).hasNext()) {
      ((WatchNextRowController)((Iterator)localObject).next()).onStop();
    }
    localObject = this.mActiveChannelRowControllers.iterator();
    while (((Iterator)localObject).hasNext()) {
      ((ChannelRowController)((Iterator)localObject).next()).onStop();
    }
    localObject = (AccessibilityManager)this.mContext.getSystemService("accessibility");
    if (localObject != null) {
      ((AccessibilityManager)localObject).removeAccessibilityStateChangeListener(this);
    }
    this.mStarted = false;
  }
  
  public void onViewRecycled(HomeRowViewHolder paramHomeRowViewHolder)
  {
    super.onViewRecycled(paramHomeRowViewHolder);
    if ((paramHomeRowViewHolder.homeRow instanceof ChannelRowController))
    {
      paramHomeRowViewHolder = (ChannelRowController)paramHomeRowViewHolder.homeRow;
      paramHomeRowViewHolder.recycle();
      this.mActiveChannelRowControllers.remove(paramHomeRowViewHolder);
    }
    do
    {
      return;
      if ((paramHomeRowViewHolder.homeRow instanceof WatchNextRowController))
      {
        paramHomeRowViewHolder = (WatchNextRowController)paramHomeRowViewHolder.homeRow;
        paramHomeRowViewHolder.recycle();
        this.mActiveWatchNextRowControllers.remove(paramHomeRowViewHolder);
        return;
      }
    } while (!(paramHomeRowViewHolder.homeRow instanceof HomeTopRowView));
    Object localObject = (HomeTopRowView)paramHomeRowViewHolder.homeRow;
    paramHomeRowViewHolder = ((HomeTopRowView)localObject).getNotificationsPanelController();
    localObject = ((HomeTopRowView)localObject).getNotificationsTrayAdapter();
    ((NotificationsTrayAdapter)localObject).changeCursor(null);
    this.mNotificationsPanelControllers.remove(paramHomeRowViewHolder);
    this.mNotificationsTrayAdapters.remove(localObject);
  }
  
  void setBackgroundController(HomeBackgroundController paramHomeBackgroundController)
  {
    this.mBackgroundController = paramHomeBackgroundController;
  }
  
  void setChannelLogoRequestManager(RequestManager paramRequestManager)
  {
    this.mChannelLogoRequestManager = paramRequestManager;
    this.mChannelLogoRequestManager.setDefaultRequestOptions((RequestOptions)new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE));
  }
  
  public void setList(VerticalGridView paramVerticalGridView)
  {
    this.mList = paramVerticalGridView;
    this.mList.getLayoutManager().setItemPrefetchEnabled(false);
    this.mList.setItemAlignmentViewId(2131951810);
    this.mList.setWindowAlignment(1);
    this.mList.setWindowAlignmentOffsetPercent(0.0F);
    this.mList.setWindowAlignmentOffset(this.mDefaultChannelKeyline);
    this.mList.setSelectedPosition(this.mSelectedPosition);
    this.mList.setItemAnimator(null);
  }
  
  void setOnBackNotHandledListener(BackHomeControllerListeners.OnBackNotHandledListener paramOnBackNotHandledListener)
  {
    this.mOnBackNotHandledListener = paramOnBackNotHandledListener;
  }
  
  void updateNotifications(Cursor paramCursor)
  {
    this.mNotifTrayCursor = paramCursor;
    Iterator localIterator = this.mNotificationsTrayAdapters.iterator();
    while (localIterator.hasNext()) {
      ((NotificationsTrayAdapter)localIterator.next()).changeCursor(paramCursor);
    }
    notifyItemChanged(0);
  }
  
  void updatePanelNotifsCount(Cursor paramCursor)
  {
    this.mNotifCountCursor = paramCursor;
    notifyItemChanged(0);
  }
  
  class HomeRowViewHolder
    extends RecyclerView.ViewHolder
    implements OnHomeStateChangeListener, OnHomeRowSelectedListener, OnHomeRowRemovedListener
  {
    HomeRow homeRow;
    
    HomeRowViewHolder(HomeRow paramHomeRow)
    {
      super();
      this.homeRow = paramHomeRow;
      paramHomeRow.setOnHomeStateChangeListener(this);
      paramHomeRow.setOnHomeRowSelectedListener(this);
      paramHomeRow.setOnHomeRowRemovedListener(this);
    }
    
    public void onHomeRowRemoved(HomeRow paramHomeRow)
    {
      HomeController.this.onItemRemoved(getAdapterPosition());
    }
    
    public void onHomeRowSelected(HomeRow paramHomeRow)
    {
      int i = getAdapterPosition();
      HomeController.this.setSelectedPosition(i);
    }
    
    public void onHomeStateChange(int paramInt)
    {
      HomeController.this.setState(paramInt);
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  static @interface RowType {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface State {}
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/HomeController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */