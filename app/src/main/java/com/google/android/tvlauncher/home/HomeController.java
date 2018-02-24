package com.google.android.tvlauncher.home;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
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
import com.google.android.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnBackNotHandledListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnBackPressedListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnHomeNotHandledListener;
import com.google.android.tvlauncher.BackHomeControllerListeners.OnHomePressedListener;
import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEventParameters;
import com.google.android.tvlauncher.analytics.LogEvents;
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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class HomeController extends Adapter<HomeController.HomeRowViewHolder> implements OnActionListener, FacetProviderAdapter, OnBackPressedListener, OnBackNotHandledListener, OnHomePressedListener, OnHomeNotHandledListener, AccessibilityStateChangeListener {
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
    private final HomeChannelsObserver mChannelsObserver = new HomeChannelsObserver() {
        public void onChannelsChange() {
            int count = HomeController.this.mDataManager.getHomeChannelCount();
            if (count >= 0) {
                HomeController.this.mEventLogger.log(new LogEventParameters(LogEvents.OPEN_HOME).putParameter(LogEvents.PARAMETER_SHOWN_CHANNEL_COUNT, count));
            }
            HomeController.this.notifyDataSetChanged();
        }

        public void onChannelMove(int fromIndex, int toIndex) {
            int fromPosition = HomeController.this.getAdapterPositionForChannelIndex(fromIndex);
            int toPosition = HomeController.this.getAdapterPositionForChannelIndex(toIndex);
            if (HomeController.this.mSelectedPosition == fromPosition) {
                HomeController.this.mSelectedPosition = toPosition;
            }
            int numChannels = HomeController.this.getChannelCount();
            if (fromIndex == 0 || toIndex == 0 || fromIndex == numChannels - 1 || toIndex == numChannels - 1) {
                HomeController.this.notifyItemChanged(fromPosition, HomeController.PAYLOAD_CHANNEL_MOVE_ACTION);
                HomeController.this.notifyItemChanged(toPosition, HomeController.PAYLOAD_CHANNEL_MOVE_ACTION);
            }
            HomeController.this.notifyItemMoved(fromPosition, toPosition);
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
    private final Runnable mNotifyChannelItemMetadataChangedRunnable = new Runnable() {
        public void run() {
            HomeController.this.notifyItemChanged(HomeController.this.mSelectedPosition, HomeController.PAYLOAD_CHANNEL_ITEM_METADATA);
        }
    };
    private final Runnable mNotifySelectionChangedRunnable = new Runnable() {
        public void run() {
            Set<Integer> positionsToUpdate = new HashSet(HomeController.this.mPrevSelectedPositions.size() + 5);
            if (HomeController.this.mState == 0 || HomeController.this.mState == 1) {
                positionsToUpdate.addAll(HomeController.this.mPrevSelectedPositions);
                HomeController.this.mPrevSelectedPositions.clear();
                positionsToUpdate.add(Integer.valueOf(HomeController.this.mSelectedPosition));
                positionsToUpdate.add(Integer.valueOf(HomeController.this.mSelectedPosition - 1));
                positionsToUpdate.add(Integer.valueOf(HomeController.this.mSelectedPosition + 1));
            }
            if (HomeController.this.mState == 0) {
                if (HomeController.this.mSelectedPosition == 0 || HomeController.this.mSelectedPosition == 1) {
                    positionsToUpdate.add(Integer.valueOf(HomeController.this.mSelectedPosition + 2));
                }
                if (HomeController.this.mSelectedPosition == 0) {
                    positionsToUpdate.add(Integer.valueOf(HomeController.this.mSelectedPosition + 3));
                }
            }
            int itemCount = HomeController.this.getItemCount();
            for (Integer position : positionsToUpdate) {
                if (position.intValue() >= 0 && position.intValue() < itemCount) {
                    HomeController.this.notifyItemChanged(position.intValue(), HomeController.PAYLOAD_STATE);
                }
            }
        }
    };
    private OnBackNotHandledListener mOnBackNotHandledListener;
    private OnProgramSelectedListener mOnProgramSelectedListener = new OnProgramSelectedListener() {
        public void onProgramSelected(Program program, ProgramController programController) {
            HomeController.this.mLastSelectedProgramController = programController;
            if (HomeController.this.mBackgroundController != null) {
                HomeController.this.mHandler.removeCallbacks(HomeController.this.mUpdateBackgroundForProgramAfterDelayRunnable);
                HomeController.this.mHandler.postDelayed(HomeController.this.mUpdateBackgroundForProgramAfterDelayRunnable, 2000);
            }
            HomeController.this.mHandler.removeCallbacks(HomeController.this.mNotifyChannelItemMetadataChangedRunnable);
            HomeController.this.mHandler.post(HomeController.this.mNotifyChannelItemMetadataChangedRunnable);
        }
    };
    private HashSet<Integer> mPrevSelectedPositions = new HashSet();
    private Runnable mRefreshWatchNextOffset = new Runnable() {
        public void run() {
            HomeController.this.mDataManager.refreshWatchNextOffset();
            HomeController.this.mWatchNextHandler.postDelayed(HomeController.this.mRefreshWatchNextOffset, ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS);
        }
    };
    private Set<String> mRemoveProgramPackagesBlacklist;
    private Runnable mRequeryWatchNext = new Runnable() {
        public void run() {
            HomeController.this.mDataManager.loadWatchNextProgramData();
            HomeController.this.mWatchNextHandler.postDelayed(HomeController.this.mRequeryWatchNext, 600000);
        }
    };
    private Runnable mSelectFirstAppRunnable = new Runnable() {
        public void run() {
            if (HomeController.this.mSelectFirstAppWhenRowSelected && HomeController.this.mSelectedPosition == 1) {
                HomeRow appsRow = HomeController.this.getHomeRow(1);
                if (appsRow instanceof AppRowController) {
                    ((AppRowController) appsRow).setSelectedItemPosition(0);
                }
            }
            HomeController.this.mSelectFirstAppWhenRowSelected = false;
        }
    };
    private boolean mSelectFirstAppWhenRowSelected;
    private int mSelectedPosition = 1;
    private boolean mStarted;
    private int mState = 0;
    private final Runnable mUpdateBackgroundForProgramAfterDelayRunnable = new Runnable() {
        public void run() {
            if (HomeController.this.mBackgroundController != null && HomeController.this.mLastSelectedProgramController != null && HomeController.this.mLastSelectedProgramController.isViewFocused() && HomeController.this.mLastSelectedProgramController.getPreviewImagePalette() != null) {
                HomeController.this.mBackgroundController.updateBackground(HomeController.this.mLastSelectedProgramController.getPreviewImagePalette());
            }
        }
    };
    private final Runnable mUpdateBackgroundForTopRowsAfterDelayRunnable = new Runnable() {
        public void run() {
            if (HomeController.this.mBackgroundController != null && HomeController.this.mSelectedPosition <= 1) {
                HomeController.this.mBackgroundController.enterDarkMode();
            }
        }
    };
    private Handler mWatchNextHandler = new Handler();
    private OnSharedPreferenceChangeListener mWatchNextPrefListener = new OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (TvDataManager.SHOW_WATCH_NEXT_ROW_KEY.equals(key)) {
                boolean watchNextVisible = sharedPreferences.getBoolean(TvDataManager.SHOW_WATCH_NEXT_ROW_KEY, true);
                if (HomeController.this.mWatchNextVisible != watchNextVisible) {
                    HomeController.this.mWatchNextVisible = watchNextVisible;
                    if (HomeController.this.mWatchNextVisible) {
                        HomeController.this.notifyItemInserted(2);
                    } else {
                        HomeController.this.notifyItemRemoved(2);
                    }
                }
            }
        }
    };
    private boolean mWatchNextVisible;
    private int mZoomedOutChannelKeyline;

    @Retention(RetentionPolicy.SOURCE)
    @interface RowType {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    class HomeRowViewHolder extends ViewHolder implements OnHomeStateChangeListener, OnHomeRowSelectedListener, OnHomeRowRemovedListener {
        HomeRow homeRow;

        HomeRowViewHolder(HomeRow homeRow) {
            super(homeRow.getView());
            this.homeRow = homeRow;
            homeRow.setOnHomeStateChangeListener(this);
            homeRow.setOnHomeRowSelectedListener(this);
            homeRow.setOnHomeRowRemovedListener(this);
        }

        public void onHomeStateChange(int state) {
            HomeController.this.setState(state);
        }

        public void onHomeRowSelected(HomeRow homeRow) {
            HomeController.this.setSelectedPosition(getAdapterPosition());
        }

        public void onHomeRowRemoved(HomeRow homeRow) {
            HomeController.this.onItemRemoved(getAdapterPosition());
        }
    }

    private static String stateToString(int state) {
        String stateString;
        switch (state) {
            case 0:
                stateString = "STATE_DEFAULT";
                break;
            case 1:
                stateString = "STATE_ZOOMED_OUT";
                break;
            case 2:
                stateString = "STATE_CHANNEL_ACTIONS";
                break;
            case 3:
                stateString = "STATE_MOVE_CHANNEL";
                break;
            default:
                stateString = "STATE_UNKNOWN";
                break;
        }
        return stateString + " (" + state + ")";
    }

    HomeController(Context context, EventLogger eventLogger) {
        this.mContext = context;
        this.mEventLogger = eventLogger;
        this.mDataManager = TvDataManager.getInstance(this.mContext);
        this.mDataManager.registerHomeChannelsObserver(this.mChannelsObserver);
        ItemAlignmentDef def = new ItemAlignmentDef();
        def.setItemAlignmentViewId(R.id.button);
        final ItemAlignmentFacet facet = new ItemAlignmentFacet();
        facet.setAlignmentDefs(new ItemAlignmentDef[]{def});
        this.mConfigureChannelsRowAlignmentFacetProvider = new FacetProvider() {
            public Object getFacet(Class<?> cls) {
                return facet;
            }
        };
        this.mDefaultChannelKeyline = this.mContext.getResources().getDimensionPixelSize(R.dimen.channel_items_list_default_keyline);
        this.mZoomedOutChannelKeyline = this.mContext.getResources().getDimensionPixelSize(R.dimen.channel_items_list_zoomed_out_keyline);
        setHasStableIds(true);
        registerChannelsObserverAndLoadDataIfNeeded();
        this.mStarted = true;
        SharedPreferences preferences = context.getSharedPreferences(TvDataManager.WATCH_NEXT_PREF_FILE_NAME, 0);
        this.mWatchNextVisible = preferences.getBoolean(TvDataManager.SHOW_WATCH_NEXT_ROW_KEY, true);
        preferences.registerOnSharedPreferenceChangeListener(this.mWatchNextPrefListener);
        PaletteUtil.registerGlidePaletteTranscoder(this.mContext);
        this.mAddToWatchNextPackagesBlacklist = GservicesUtils.retrievePackagesBlacklistedForWatchNext(this.mContext.getContentResolver());
        this.mRemoveProgramPackagesBlacklist = GservicesUtils.retrievePackagesBlacklistedForProgramRemoval(this.mContext.getContentResolver());
    }

    public void setList(VerticalGridView list) {
        this.mList = list;
        this.mList.getLayoutManager().setItemPrefetchEnabled(false);
        this.mList.setItemAlignmentViewId(R.id.items_list);
        this.mList.setWindowAlignment(1);
        this.mList.setWindowAlignmentOffsetPercent(0.0f);
        this.mList.setWindowAlignmentOffset(this.mDefaultChannelKeyline);
        this.mList.setSelectedPosition(this.mSelectedPosition);
        this.mList.setItemAnimator(null);
    }

    void setBackgroundController(HomeBackgroundController backgroundController) {
        this.mBackgroundController = backgroundController;
    }

    void setOnBackNotHandledListener(OnBackNotHandledListener onBackNotHandledListener) {
        this.mOnBackNotHandledListener = onBackNotHandledListener;
    }

    private void registerChannelsObserverAndLoadDataIfNeeded() {
        this.mDataManager.registerHomeChannelsObserver(this.mChannelsObserver);
        if (!this.mDataManager.isHomeChannelDataLoaded() || this.mDataManager.isHomeChannelDataStale()) {
            this.mDataManager.loadHomeChannelData();
        }
    }

    private int getChannelCount() {
        if (this.mDataManager.isHomeChannelDataLoaded()) {
            return this.mDataManager.getHomeChannelCount();
        }
        return 0;
    }

    void onStart() {
        // ActiveMediaSessionManager.getInstance(this.mContext).start();
        for (WatchNextRowController controller : this.mActiveWatchNextRowControllers) {
            controller.onStart();
        }
        for (ChannelRowController controller2 : this.mActiveChannelRowControllers) {
            controller2.onStart();
        }
        AccessibilityManager manager = (AccessibilityManager) this.mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (manager != null) {
            manager.addAccessibilityStateChangeListener(this);
        }
        if (!this.mStarted) {
            registerChannelsObserverAndLoadDataIfNeeded();
            this.mStarted = true;
        }
    }

    void onStop() {
        // ActiveMediaSessionManager.getInstance(this.mContext).stop();
        this.mDataManager.unregisterHomeChannelsObserver(this.mChannelsObserver);
        this.mDataManager.pruneChannelProgramsCache();
        for (WatchNextRowController controller : this.mActiveWatchNextRowControllers) {
            controller.onStop();
        }
        for (ChannelRowController controller2 : this.mActiveChannelRowControllers) {
            controller2.onStop();
        }
        AccessibilityManager manager = (AccessibilityManager) this.mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (manager != null) {
            manager.removeAccessibilityStateChangeListener(this);
        }
        this.mStarted = false;
    }

    void onResume() {
        this.mWatchNextHandler.postDelayed(this.mRefreshWatchNextOffset, ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS);
        this.mWatchNextHandler.postDelayed(this.mRequeryWatchNext, 600000);
        if (this.mDataManager.isHomeChannelDataLoaded()) {
            this.mEventLogger.log(new LogEventParameters(LogEvents.OPEN_HOME).putParameter(LogEvents.PARAMETER_SHOWN_CHANNEL_COUNT, this.mDataManager.getHomeChannelCount()));
        }
    }

    void onPause() {
        if (this.mLastSelectedProgramController != null) {
            this.mLastSelectedProgramController.stopPreviewVideo(true);
        }
        this.mWatchNextHandler.removeCallbacks(this.mRefreshWatchNextOffset);
        this.mWatchNextHandler.removeCallbacks(this.mRequeryWatchNext);
        this.mLastPausedTime = SystemClock.elapsedRealtime();
    }

    @Nullable
    private HomeRow getHomeRow(int position) {
        HomeRowViewHolder holder = (HomeRowViewHolder) this.mList.findViewHolderForAdapterPosition(position);
        return holder != null ? holder.homeRow : null;
    }

    @Nullable
    private HomeRow getSelectedHomeRow() {
        return getHomeRow(this.mSelectedPosition);
    }

    private void scrollToAppsRow() {
        this.mSelectFirstAppWhenRowSelected = true;
        if (this.mList.getSelectedPosition() > 6) {
            this.mList.setSelectedPosition(1);
        } else {
            this.mList.setSelectedPositionSmooth(1);
        }
    }

    public void onAccessibilityStateChanged(boolean enabled) {
        notifyItemChanged(0);
    }

    public void onBackPressed(Context c) {
        HomeRow homeRow = getSelectedHomeRow();
        if (homeRow instanceof OnBackPressedListener) {
            ((OnBackPressedListener) homeRow).onBackPressed(c);
        } else {
            onBackNotHandled(c);
        }
    }

    public void onBackNotHandled(Context c) {
        if (this.mList.getSelectedPosition() != 1 && (this.mState == 0 || this.mState == 1)) {
            scrollToAppsRow();
        } else if (this.mOnBackNotHandledListener != null) {
            this.mOnBackNotHandledListener.onBackNotHandled(c);
        }
    }

    public void onHomePressed(Context c) {
        if (SystemClock.elapsedRealtime() - this.mLastPausedTime <= 200) {
            HomeRow homeRow = getSelectedHomeRow();
            if (homeRow instanceof OnHomePressedListener) {
                ((OnHomePressedListener) homeRow).onHomePressed(c);
            } else {
                onHomeNotHandled(c);
            }
        }
    }

    public void onHomeNotHandled(Context c) {
        if (this.mList.getSelectedPosition() == 1) {
            return;
        }
        if (this.mState == 0 || this.mState == 1) {
            scrollToAppsRow();
        }
    }

    void updateNotifications(Cursor cursor) {
        this.mNotifTrayCursor = cursor;
        for (NotificationsTrayAdapter adapter : this.mNotificationsTrayAdapters) {
            adapter.changeCursor(cursor);
        }
        notifyItemChanged(0);
    }

    void updatePanelNotifsCount(Cursor cursor) {
        this.mNotifCountCursor = cursor;
        notifyItemChanged(0);
    }

    void setChannelLogoRequestManager(RequestManager requestManager) {
        this.mChannelLogoRequestManager = requestManager;
        this.mChannelLogoRequestManager.setDefaultRequestOptions((RequestOptions) new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE));
    }

    private int getNumberOfRowsAboveChannels() {
        return this.mWatchNextVisible ? 3 : 2;
    }

    private void setState(int state) {
        if (Util.isAccessibilityEnabled(this.mContext)) {
            switch (state) {
                case 1:
                case 2:
                case 3:
                    state = 0;
                    break;
            }
        }
        if (this.mState != state) {
            switch (this.mState) {
                case 1:
                    this.mEventLogger.log(new UserActionEvent(LogEvents.EXIT_ZOOMED_OUT_MODE));
                    break;
                case 2:
                    this.mEventLogger.log(new UserActionEvent(LogEvents.EXIT_CHANNEL_ACTIONS_MODE));
                    break;
                case 3:
                    this.mEventLogger.log(new UserActionEvent(LogEvents.EXIT_MOVE_CHANNEL_MODE));
                    break;
            }
            this.mState = state;
            switch (this.mState) {
                case 0:
                    this.mList.setWindowAlignmentOffset(this.mDefaultChannelKeyline);
                    break;
                case 1:
                    this.mList.setWindowAlignmentOffset(this.mZoomedOutChannelKeyline);
                    this.mEventLogger.log(new UserActionEvent(LogEvents.ENTER_ZOOMED_OUT_MODE));
                    break;
                case 2:
                    this.mList.setWindowAlignmentOffset(this.mZoomedOutChannelKeyline);
                    this.mEventLogger.log(new UserActionEvent(LogEvents.ENTER_CHANNEL_ACTIONS_MODE));
                    break;
                case 3:
                    this.mList.setWindowAlignmentOffset(this.mZoomedOutChannelKeyline);
                    this.mEventLogger.log(new UserActionEvent(LogEvents.ENTER_MOVE_CHANNEL_MODE));
                    break;
            }
            notifyItemRangeChanged(0, getItemCount(), PAYLOAD_STATE);
        }
    }

    private void setSelectedPosition(int selectedPosition) {
        if (this.mSelectedPosition != selectedPosition) {
            if (this.mState == 0 || this.mState == 1) {
                this.mPrevSelectedPositions.add(Integer.valueOf(this.mSelectedPosition));
                this.mHandler.post(this.mNotifySelectionChangedRunnable);
            }
            this.mSelectedPosition = selectedPosition;
            if (this.mSelectedPosition > 1 || this.mBackgroundController == null) {
                this.mHandler.removeCallbacks(this.mUpdateBackgroundForTopRowsAfterDelayRunnable);
            } else {
                this.mHandler.postDelayed(this.mUpdateBackgroundForTopRowsAfterDelayRunnable, 2000);
            }
        }
        if (!this.mSelectFirstAppWhenRowSelected || selectedPosition != 1) {
            return;
        }
        if (Util.isAccessibilityEnabled(this.mContext)) {
            this.mHandler.postDelayed(this.mSelectFirstAppRunnable, 50);
        } else {
            this.mSelectFirstAppRunnable.run();
        }
    }

    private void onItemRemoved(int position) {
        if (this.mWatchNextVisible && position == 2) {
            this.mWatchNextVisible = false;
            this.mContext.getSharedPreferences(TvDataManager.WATCH_NEXT_PREF_FILE_NAME, 0).edit().putBoolean(TvDataManager.SHOW_WATCH_NEXT_ROW_KEY, false).apply();
        }
        if (position == getItemCount() - 1) {
            this.mSelectedPosition = position - 1;
        }
        if (!Util.isAccessibilityEnabled(this.mContext)) {
            this.mState = 1;
        }
        notifyDataSetChanged();
    }

    private int getAdapterPositionForChannelIndex(int channelIndex) {
        return getNumberOfRowsAboveChannels() + channelIndex;
    }

    private int getChannelIndexForAdapterPosition(int position) {
        return position - getNumberOfRowsAboveChannels();
    }

    public int getItemCount() {
        return (getChannelCount() + getNumberOfRowsAboveChannels()) + 1;
    }

    public long getItemId(int position) {
        switch (getItemViewType(position)) {
            case 0:
                return -2;
            case 1:
                return -3;
            case 2:
                return -4;
            case 3:
                return this.mDataManager.getHomeChannel(getChannelIndexForAdapterPosition(position)).getId();
            case 4:
                return -5;
            default:
                return super.getItemId(position);
        }
    }

    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return 4;
        }
        if (position == 0) {
            return 0;
        }
        if (position == 1) {
            return 1;
        }
        if (position == 2 && this.mWatchNextVisible) {
            return 2;
        }
        return 3;
    }

    public HomeRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == 0) {
            HomeTopRowView v = (HomeTopRowView) inflater.inflate(R.layout.home_top_row, parent, false);
            v.setOnActionListener(this);
            NotificationsPanelController controller = new NotificationsPanelController(parent.getContext(), this.mEventLogger);
            v.setNotificationsTrayAdapter(new NotificationsTrayAdapter(parent.getContext(), this.mEventLogger, this.mNotifTrayCursor));
            v.setNotificationsPanelController(controller);
            return new HomeRowViewHolder(v);
        } else if (viewType == 1) {
            View v2 = inflater.inflate(R.layout.home_channel_row, parent, false);
            v2.setId(R.id.apps_row);
            AppRowController controller2 = new AppRowController((ChannelView) v2, this.mEventLogger);
            controller2.setOnBackNotHandledListener(this);
            controller2.setOnHomeNotHandledListener(this);
            return new HomeRowViewHolder(controller2);
        } else if (viewType == 2) {
            WatchNextRowController controller3 = new WatchNextRowController((ChannelView) inflater.inflate(R.layout.home_channel_row, parent, false), this.mEventLogger);
            controller3.setOnProgramSelectedListener(this.mOnProgramSelectedListener);
            controller3.setOnBackNotHandledListener(this);
            controller3.setOnHomeNotHandledListener(this);
            return new HomeRowViewHolder(controller3);
        } else if (viewType == 4) {
            return new HomeRowViewHolder(new ConfigureChannelsRowController(inflater.inflate(R.layout.home_configure_channels_row, parent, false)));
        } else {
            ChannelRowController controller4 = new ChannelRowController((ChannelView) inflater.inflate(R.layout.home_channel_row, parent, false), this.mChannelLogoRequestManager, this.mEventLogger);
            controller4.setOnProgramSelectedListener(this.mOnProgramSelectedListener);
            controller4.setOnBackNotHandledListener(this);
            controller4.setOnHomeNotHandledListener(this);
            return new HomeRowViewHolder(controller4);
        }
    }

    public void onBindViewHolder(HomeRowViewHolder holder, int position, List<Object> payloads) {
        boolean z = true;
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
            return;
        }
        int rowType = getItemViewType(position);


        if (payloads.contains(PAYLOAD_STATE)) {
            if (rowType == 2 && holder.homeRow instanceof WatchNextRowController) {
                WatchNextRowController controller = (WatchNextRowController) holder.homeRow;

                if (payloads.contains(PAYLOAD_CHANNEL_ITEM_METADATA)) {
                    controller.bindItemMetadata();
                }
                controller.setState(getChannelState(position));
                this.mActiveWatchNextRowControllers.add(controller);

            } else if (rowType == 3 && holder.homeRow instanceof ChannelRowController) {
                ChannelRowController controller = (ChannelRowController) holder.homeRow;

                if (payloads.contains(PAYLOAD_CHANNEL_ITEM_METADATA)) {
                    controller.bindItemMetadata();
                }

                controller.setState(getChannelState(position));
                this.mActiveChannelRowControllers.add(controller);
            } else if (rowType == 0) {
                HomeTopRowView v = (HomeTopRowView) holder.homeRow.getView();
                boolean z2 = this.mState == 1;
                if (this.mSelectedPosition != position) {
                    z = false;
                }
                v.setState(z2, z);
            } else {
                onBindViewHolder(holder, position);
            }
        }
        if (payloads.contains(PAYLOAD_CHANNEL_MOVE_ACTION) && rowType == 3 && holder.homeRow instanceof ChannelRowController) {
            ChannelRowController controller = (ChannelRowController) holder.homeRow;

            controller.bindChannelMoveAction();
        }
    }

    public void onBindViewHolder(HomeRowViewHolder viewHolder, int position) {
        boolean z = true;
        int rowType = getItemViewType(position);
        if (rowType == ROW_TYPE_WATCH_NEXT_POSITION && viewHolder.homeRow instanceof WatchNextRowController) {
            WatchNextRowController controller = (WatchNextRowController) viewHolder.homeRow;
            controller.bind(getChannelState(position));
            this.mActiveWatchNextRowControllers.add(controller);
        } else if (rowType == ROW_TYPE_APPS && viewHolder.homeRow instanceof AppRowController) {
            AppRowController controller = (AppRowController) viewHolder.homeRow;
            controller.bind(getChannelState(position));
        } else if (rowType == ROW_TYPE_CONFIGURE_CHANNELS && viewHolder.homeRow instanceof ConfigureChannelsRowController) {
            ConfigureChannelsRowController controller = (ConfigureChannelsRowController) viewHolder.homeRow;
            controller.bind(this.mState);
        } else if (rowType == ROW_TYPE_CHANNEL && viewHolder.homeRow instanceof ChannelRowController) {
            onBindChannel((ChannelRowController) viewHolder.homeRow, getChannelIndexForAdapterPosition(position), position);
        } else if (rowType == 0) {
            HomeTopRowView v = (HomeTopRowView) viewHolder.homeRow.getView();
            this.mNotificationsTrayAdapters.add(v.getNotificationsTrayAdapter());
            this.mNotificationsPanelControllers.add(v.getNotificationsPanelController());
            v.getNotificationsTrayAdapter().changeCursor(this.mNotifTrayCursor);
            v.getNotificationsPanelController().updateNotificationsCount(this.mNotifCountCursor);
            v.updateNotificationsTrayVisibility();
            boolean z2 = this.mState == 1;
            if (this.mSelectedPosition != position) {
                z = false;
            }
            v.setState(z2, z);
        }
    }

    private void onBindChannel(ChannelRowController controller, int channelIndex, int position) {
        boolean z;
        boolean z2 = true;
        HomeChannel channel = this.mDataManager.getHomeChannel(channelIndex);
        int channelState = getChannelState(position);
        if (this.mAddToWatchNextPackagesBlacklist.contains(channel.getPackageName())) {
            z = false;
        } else {
            z = true;
        }
        if (this.mRemoveProgramPackagesBlacklist.contains(channel.getPackageName())) {
            z2 = false;
        }
        controller.bindChannel(channel, channelState, z, z2);
        this.mActiveChannelRowControllers.add(controller);
    }

    private int getChannelState(int position) {
        boolean rowSelected = this.mSelectedPosition == position;
        switch (this.mState) {
            case 0:
                if (rowSelected) {
                    return 0;
                }
                if (position == this.mSelectedPosition - 1) {
                    return 2;
                }
                if (position != this.mSelectedPosition + 1 || position <= 2) {
                    return 1;
                }
                return 3;
            case 1:
                return rowSelected ? 4 : 5;
            case 2:
                return rowSelected ? 6 : 7;
            case 3:
                return rowSelected ? 8 : 9;
            default:
                return 1;
        }
    }

    public void onViewRecycled(HomeRowViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.homeRow instanceof ChannelRowController) {
            ChannelRowController channelRowController = (ChannelRowController) holder.homeRow;
            channelRowController.recycle();
            this.mActiveChannelRowControllers.remove(channelRowController);
        } else if (holder.homeRow instanceof WatchNextRowController) {
            WatchNextRowController watchNextRowController = (WatchNextRowController) holder.homeRow;
            watchNextRowController.recycle();
            this.mActiveWatchNextRowControllers.remove(watchNextRowController);
        } else if (holder.homeRow instanceof HomeTopRowView) {
            HomeTopRowView homeTopRowView = (HomeTopRowView) holder.homeRow;
            NotificationsPanelController controller = homeTopRowView.getNotificationsPanelController();
            NotificationsTrayAdapter adapter = homeTopRowView.getNotificationsTrayAdapter();
            adapter.changeCursor(null);
            this.mNotificationsPanelControllers.remove(controller);
            this.mNotificationsTrayAdapters.remove(adapter);
        }
    }

    public FacetProvider getFacetProvider(int type) {
        if (type == 4) {
            return this.mConfigureChannelsRowAlignmentFacetProvider;
        }
        return null;
    }

    public void onShowInputs() {
        try {
            this.mContext.startActivity(new Intent(INPUTS_PANEL_INTENT_ACTION));
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "InputsPanelActivity not found :  " + e);
        }
    }

    public void onStartSettings() {
        this.mEventLogger.log(new UserActionEvent(LogEvents.START_SETTINGS));
        try {
            this.mContext.startActivity(new Intent("android.settings.SETTINGS"));
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Exception starting settings", e);
            Toast.makeText(this.mContext, this.mContext.getString(R.string.app_unavailable), Toast.LENGTH_SHORT).show();
        }
    }
}
