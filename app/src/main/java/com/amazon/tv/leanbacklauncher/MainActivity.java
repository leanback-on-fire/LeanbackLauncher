package com.amazon.tv.leanbacklauncher;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.PendingIntent;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.Rect;
import android.media.tv.TvContract;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v17.leanback.widget.OnChildViewHolderSelectedListener;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.amazon.tv.leanbacklauncher.animation.AnimatorLifecycle;
import com.amazon.tv.leanbacklauncher.animation.AnimatorLifecycle.OnAnimationFinishedListener;
import com.amazon.tv.leanbacklauncher.animation.EditModeMassFadeAnimator;
import com.amazon.tv.leanbacklauncher.animation.EditModeMassFadeAnimator.EditMode;
import com.amazon.tv.leanbacklauncher.animation.ForwardingAnimatorSet;
import com.amazon.tv.leanbacklauncher.animation.LauncherDismissAnimator;
import com.amazon.tv.leanbacklauncher.animation.LauncherLaunchAnimator;
import com.amazon.tv.leanbacklauncher.animation.LauncherPauseAnimator;
import com.amazon.tv.leanbacklauncher.animation.LauncherReturnAnimator;
import com.amazon.tv.leanbacklauncher.animation.MassSlideAnimator;
import com.amazon.tv.leanbacklauncher.animation.NotificationLaunchAnimator;
import com.amazon.tv.leanbacklauncher.animation.ParticipatesInLaunchAnimation;
import com.amazon.tv.leanbacklauncher.apps.AppsAdapter;
import com.amazon.tv.leanbacklauncher.apps.AppsManager;
import com.amazon.tv.leanbacklauncher.apps.BannerView;
import com.amazon.tv.leanbacklauncher.apps.OnEditModeChangedListener;
import com.amazon.tv.leanbacklauncher.BuildConfig;
import com.amazon.tv.leanbacklauncher.logging.LeanbackLauncherEventLogger;
import com.amazon.tv.leanbacklauncher.notifications.HomeScreenMessaging;
import com.amazon.tv.leanbacklauncher.notifications.HomeScreenMessaging.ChangeListener;
import com.amazon.tv.leanbacklauncher.notifications.HomeScreenView;
import com.amazon.tv.leanbacklauncher.notifications.NotificationCardView;
import com.amazon.tv.leanbacklauncher.notifications.NotificationRowView;
import com.amazon.tv.leanbacklauncher.notifications.NotificationRowView.NotificationRowListener;
import com.amazon.tv.leanbacklauncher.notifications.NotificationsAdapter;
import com.amazon.tv.leanbacklauncher.trace.AppTrace;
import com.amazon.tv.leanbacklauncher.util.Partner;
import com.amazon.tv.leanbacklauncher.util.TvSearchIconLoader;
import com.amazon.tv.leanbacklauncher.util.TvSearchSuggestionsLoader;
import com.amazon.tv.leanbacklauncher.util.Util;
import com.amazon.tv.leanbacklauncher.wallpaper.LauncherWallpaper;
import com.amazon.tv.leanbacklauncher.wallpaper.WallpaperInstaller;
import com.amazon.tv.leanbacklauncher.widget.EditModeView;
import com.amazon.tv.leanbacklauncher.widget.EditModeView.OnEditModeUninstallPressedListener;
import com.amazon.tv.firetv.leanbacklauncher.apps.AppInfoActivity;
import com.amazon.tv.firetv.tvrecommendations.NotificationListenerMonitor;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Class;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class MainActivity extends Activity implements OnEditModeChangedListener, OnEditModeUninstallPressedListener {
    private AccessibilityManager mAccessibilityManager;
    private boolean mAppEditMode;
    private AppWidgetHost mAppWidgetHost;
    private AppWidgetHostView mAppWidgetHostView;
    private AppWidgetManager mAppWidgetManager;
    private ContentResolver mContentResolver;
    private boolean mDelayFirstRecommendationsVisible = true;
    private AnimatorLifecycle mEditModeAnimation = new AnimatorLifecycle();
    private EditModeView mEditModeView;
    private LeanbackLauncherEventLogger mEventLogger;
    private boolean mFadeDismissAndSummonAnimations;
    private Handler mHandler = new MainActivityMessageHandler(this);

    private static String TAG = "LeanbackLauncher";

    private static class MainActivityMessageHandler extends Handler {
        private MainActivity activity;

        private MainActivityMessageHandler(MainActivity activity) {
            this.activity = activity;
        }

        public void handleMessage(Message msg) {
            boolean z = true;
            switch (msg.what) {
                case 1:
                case 2:
                    MainActivity mainActivity = activity;
                    if (msg.what != 1) {
                        z = false;
                    }
                    mainActivity.mIsIdle = z;
                    for (int i = 0; i < activity.mIdleListeners.size(); i++) {
                        activity.mIdleListeners.get(i).onIdleStateChange(activity.mIsIdle);
                    }
                    return;
                case 3:
                    if (activity.mResetAfterIdleEnabled) {
                        activity.mKeepUiReset = true;
                        activity.resetLauncherState(true);
                        return;
                    }
                    return;
                case 4:
                    activity.onNotificationRowStateUpdate(msg.arg1);
                    return;
                case 5:
                    activity.mHomeAdapter.onUiVisible();
                    return;
                case 6:
                    activity.addWidget(true);
                    return;
                case 7:
                    activity.checkLaunchPointPositions();
                    return;
                default:
            }
        }
    }

    private HomeScreenAdapter mHomeAdapter;
    private HomeScreenView mHomeScreenView;
    private ArrayList<IdleListener> mIdleListeners = new ArrayList<>();
    private int mIdlePeriod;
    private boolean mIsIdle;
    private boolean mKeepUiReset;
    private AnimatorLifecycle mLaunchAnimation = new AnimatorLifecycle();
    private VerticalGridView mList;
    private final Runnable mMoveTaskToBack = new Runnable() {
        public void run() {
            if (!MainActivity.this.moveTaskToBack(true)) {
                MainActivity.this.mLaunchAnimation.reset();
            }
        }
    };
    private NotificationRowListener mNotifListener = new NotificationRowListener() {
        private Handler mHandler;
        private Runnable mSelectFirstRecommendationRunnable = new Runnable() {
            public void run() {
                if (MainActivity.this.mNotificationsView.getAdapter() != null && MainActivity.this.mNotificationsView.getAdapter().getItemCount() > 0) {
                    MainActivity.this.mNotificationsView.setSelectedPositionSmooth(0);
                }
            }
        };

        public void onBackgroundImageChanged(String imageUri, String signature) {
            if (MainActivity.this.mWallpaper != null) {
                MainActivity.this.mWallpaper.onBackgroundImageChanged(imageUri, signature);
            }
        }

        public void onSelectedRecommendationChanged(int position) {
            if (MainActivity.this.mKeepUiReset && !MainActivity.this.mAccessibilityManager.isEnabled() && position > 0) {
                if (this.mHandler == null) {
                    this.mHandler = new Handler();
                }
                this.mHandler.post(this.mSelectFirstRecommendationRunnable);
            }
        }
    };
    private NotificationRowView mNotificationsView;
    BroadcastReceiver mPackageReplacedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Uri packageName = intent != null ? intent.getData() : null;
            if (packageName != null && packageName.toString().contains(context.getPackageName() + ".recommendations")) {
                Log.d(TAG, "Recommendations Service updated, reconnecting");
                if (MainActivity.this.mHomeAdapter != null) {
                    MainActivity.this.mHomeAdapter.onReconnectToRecommendationsService();
                }
            }
        }
    };
    BroadcastReceiver mHomeRefreshReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
        	if (intent.getBooleanExtra("RefreshHome", false)) {
                if (BuildConfig.DEBUG) Log.d(TAG, "KILL HOME");
                MainActivity.this.finish();
            }
        }
    };
    private AnimatorLifecycle mPauseAnimation = new AnimatorLifecycle();
    private NotificationsAdapter mRecommendationsAdapter;
    private final Runnable mRefreshHomeAdapter = new Runnable() {
        public void run() {
            if (MainActivity.this.mHomeAdapter != null) {
                MainActivity.this.mHomeAdapter.refreshAdapterData();
            }
        }
    };
    private boolean mResetAfterIdleEnabled;
    private int mResetPeriod;
    private HomeScrollManager mScrollManager;
    private LoaderCallbacks<Drawable> mSearchIconCallbacks = new LoaderCallbacks<Drawable>() {
        public Loader<Drawable> onCreateLoader(int id, Bundle args) {
            return new TvSearchIconLoader(MainActivity.this.getApplicationContext());
        }

        public void onLoadFinished(Loader<Drawable> loader, Drawable data) {
            MainActivity.this.mHomeAdapter.onSearchIconUpdate(data);
        }

        public void onLoaderReset(Loader<Drawable> loader) {
            MainActivity.this.mHomeAdapter.onSearchIconUpdate(null);
        }
    };
    private LoaderCallbacks<String[]> mSearchSuggestionsCallbacks = new LoaderCallbacks<String[]>() {
        public Loader<String[]> onCreateLoader(int id, Bundle args) {
            return new TvSearchSuggestionsLoader(MainActivity.this.getApplicationContext());
        }

        public void onLoadFinished(Loader<String[]> loader, String[] data) {
            MainActivity.this.mHomeAdapter.onSuggestionsUpdate(data);
        }

        public void onLoaderReset(Loader<String[]> loader) {
            MainActivity.this.mHomeAdapter.onSuggestionsUpdate(null);
        }
    };
    private boolean mShyMode;
    private boolean mStartingEditMode;
    private boolean mUninstallRequested;
    private boolean mUserInteracted = false;
    private LauncherWallpaper mWallpaper;

    public interface IdleListener {
        void onIdleStateChange(boolean z);

        void onVisibilityChange(boolean z);
    }

    protected void onCreate(Bundle savedInstanceState) {
        AppTrace.beginSection("onCreate");
        try {
            boolean z;
            super.onCreate(savedInstanceState);
            this.mContentResolver = getContentResolver();
            if (this.mRecommendationsAdapter == null) {
                this.mRecommendationsAdapter = new NotificationsAdapter(this);
            }
            Context appContext = getApplicationContext();
            setContentView(R.layout.activity_main);
            if (Partner.get(this).showLiveTvOnStartUp() && checkFirstRunAfterBoot()) {
                Intent tvIntent = new Intent("android.intent.action.VIEW", TvContract.buildChannelUri(0));
                tvIntent.putExtra("com.google.android.leanbacklauncher.extra.TV_APP_ON_BOOT", true);
                if (getPackageManager().queryIntentActivities(tvIntent, 1).size() > 0) {
                    startActivity(tvIntent);
                    finish();
                }
            }
			//android O fix bug orientation
			if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
            this.mAccessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
            this.mEditModeView = (EditModeView) findViewById(R.id.edit_mode_view);
            this.mEditModeView.setUninstallListener(this);
            this.mWallpaper = (LauncherWallpaper) findViewById(R.id.background_container);
            this.mAppWidgetManager = AppWidgetManager.getInstance(appContext);
            this.mAppWidgetHost = new AppWidgetHost(this, 123);
            this.mList = (VerticalGridView) findViewById(R.id.main_list_view);
            this.mList.setHasFixedSize(true);
            this.mList.setWindowAlignment(1);
            this.mList.setWindowAlignmentOffset(getResources().getDimensionPixelOffset(R.dimen.home_screen_selected_row_alignment));
            this.mList.setWindowAlignmentOffsetPercent(-1.0f);
            this.mList.setItemAlignmentOffset(0);
            this.mList.setItemAlignmentOffsetPercent(-1.0f);
            this.mScrollManager = new HomeScrollManager(this, this.mList);
            this.mScrollManager.addHomeScrollListener(this.mWallpaper);
            this.mHomeAdapter = new HomeScreenAdapter(this, this.mScrollManager, this.mRecommendationsAdapter, this.mEditModeView);
            this.mHomeAdapter.setOnEditModeChangedListener(this);
            this.mList.setItemViewCacheSize(this.mHomeAdapter.getItemCount());
            this.mList.setAdapter(this.mHomeAdapter);
            this.mList.setOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() {
                public void onChildViewHolderSelected(RecyclerView parent, ViewHolder child, int position, int subposition) {
                    MainActivity.this.mHomeAdapter.onChildViewHolderSelected(parent, child, position);
                }
            });
            this.mList.setAnimateChildLayout(false);
            int notifIndex = this.mHomeAdapter.getRowIndex(1);
            if (notifIndex != -1) {
                this.mList.setSelectedPosition(notifIndex);
            }
            final NotificationsAdapter recAdapter = this.mHomeAdapter.getRecommendationsAdapter();
            addIdleListener(recAdapter);
            this.mList.setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
                public void onChildViewAdded(View parent, View child) {
                    int tag = 0;
                    if (child.getTag() instanceof Integer) {
                        tag = (Integer) child.getTag();
                    }
                    switch (tag) {
                        case 0:
                            if (child instanceof SearchOrbView) {
                                ((SearchOrbView) child).setLaunchListener(new SearchOrbView.SearchLaunchListener() {
                                    public void onSearchLaunched() {
                                        MainActivity.this.setShyMode(true, true);
                                    }
                                });
                            }
                            MainActivity.this.addWidget(false);
                            break;
                        case 1:
                        case 2:
                            MainActivity.this.mHomeScreenView = (HomeScreenView) child.findViewById(R.id.home_screen_messaging);
                            if (MainActivity.this.mHomeScreenView != null) {
                                HomeScreenMessaging homeScreenMessaging = MainActivity.this.mHomeScreenView.getHomeScreenMessaging();
                                if (tag == 1) {
                                    recAdapter.setNotificationRowViewFlipper(homeScreenMessaging);
                                    MainActivity.this.mNotificationsView = MainActivity.this.mHomeScreenView.getNotificationRow();
                                    if (MainActivity.this.mNotificationsView != null) {
                                        MainActivity.this.mNotificationsView.setListener(MainActivity.this.mNotifListener);
                                    }
                                }
                                homeScreenMessaging.setListener(new ChangeListener() {
                                    public void onStateChanged(int state) {
                                        MainActivity.this.mHandler.sendMessageDelayed(MainActivity.this.mHandler.obtainMessage(4, state, 0), 500);
                                        if (state == 0 && MainActivity.this.mDelayFirstRecommendationsVisible) {
                                            MainActivity.this.mDelayFirstRecommendationsVisible = false;
                                            MainActivity.this.mHandler.sendEmptyMessageDelayed(5, 1500);
                                        }
                                    }
                                });
                                break;
                            }
                            break;
                    }
                    if ((child instanceof IdleListener) && !MainActivity.this.mIdleListeners.contains(child)) {
                        MainActivity.this.addIdleListener((IdleListener) child);
                    }
                }

                public void onChildViewRemoved(View parent, View child) {
                }
            });
            this.mList.addOnScrollListener(new OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    MainActivity.this.mScrollManager.onScrolled(dy, MainActivity.this.getCurrentScrollPos());
                }

                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    MainActivity.this.mScrollManager.onScrollStateChanged(newState);
                }
            });
            this.mShyMode = true;
            if (this.mShyMode) {
                z = false;
            } else {
                z = true;
            }
            setShyMode(z, true);
            this.mIdlePeriod = getResources().getInteger(R.integer.idle_period);
            this.mResetPeriod = getResources().getInteger(R.integer.reset_period);
            this.mFadeDismissAndSummonAnimations = getResources().getBoolean(R.bool.app_launch_animation_fade);
            this.mKeepUiReset = true;
            this.mHomeAdapter.onInitUi();
            this.mEventLogger = LeanbackLauncherEventLogger.getInstance(appContext);
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.PACKAGE_REPLACED");
            filter.addAction("android.intent.action.PACKAGE_ADDED");
            filter.addDataScheme("package");
            registerReceiver(this.mPackageReplacedReceiver, filter);
			// RefreshHome BC
            // IntentFilter filterHome = new IntentFilter("com.amazon.tv.leanbacklauncher.MainActivity");
            IntentFilter filterHome = new IntentFilter(this.getClass().getName()); // ACTION
            registerReceiver(this.mHomeRefreshReceiver, filterHome);
            getLoaderManager().initLoader(0, null, this.mSearchIconCallbacks);
            getLoaderManager().initLoader(1, null, this.mSearchSuggestionsCallbacks);
        } finally {
            AppTrace.endSection();
        }
        // start notification listener
        Context appContext = getApplicationContext();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(appContext);
        if (pref.getBoolean(appContext.getString(R.string.pref_enable_recommendations_row), false))
        	startService(new Intent(this, NotificationListenerMonitor.class));
    }

    public void onDestroy() {
        this.mHandler.removeMessages(3);
        super.onDestroy();
        if (this.mHomeAdapter != null) {
            this.mHomeAdapter.onStopUi();
            this.mHomeAdapter.unregisterReceivers();
        }
        AppsManager.getInstance(getApplicationContext()).onDestroy();
        unregisterReceiver(this.mPackageReplacedReceiver);
        unregisterReceiver(this.mHomeRefreshReceiver);
    }

    public void onUserInteraction() {
        this.mHandler.removeMessages(3);
        this.mKeepUiReset = false;
        if (hasWindowFocus()) {
            this.mHandler.removeMessages(1);
            this.mUserInteracted = true;
            if (this.mIsIdle) {
                this.mHandler.sendEmptyMessage(2);
            }
            this.mHandler.sendEmptyMessageDelayed(1, (long) this.mIdlePeriod);
        }
        this.mHandler.sendEmptyMessageDelayed(3, (long) this.mResetPeriod);
    }

    private void addIdleListener(IdleListener listener) {
        this.mIdleListeners.add(listener);
        listener.onVisibilityChange(true);
        listener.onIdleStateChange(this.mIsIdle);
    }

    public void onBackPressed() {
        if (this.mAppEditMode) {
            getEditModeView().onBackPressed();
        } else if (this.mLaunchAnimation.isRunning()) {
            this.mLaunchAnimation.cancel();
        } else if (this.mLaunchAnimation.isPrimed()) {
            this.mLaunchAnimation.reset();
        } else {
            if (this.mLaunchAnimation.isFinished()) {
                this.mLaunchAnimation.reset();
            }
            dismissLauncher();
        }
    }

    public void onBackgroundVisibleBehindChanged(boolean visible) {
        setShyMode(!visible, true);
    }

    public void onEditModeChanged(boolean editMode) {
        if (this.mAppEditMode == editMode) {
            return;
        }
        if (this.mAccessibilityManager.isEnabled()) {
            setEditMode(editMode, false);
        } else {
            setEditMode(editMode, true);
        }
    }

    public void onUninstallPressed(String packageName) {
        if (packageName != null && !this.mUninstallRequested) {
            this.mUninstallRequested = true;
            Intent uninstallIntent = new Intent("android.intent.action.UNINSTALL_PACKAGE", Uri.parse("package:" + packageName));
            uninstallIntent.putExtra("android.intent.extra.RETURN_RESULT", true);
            startActivityForResult(uninstallIntent, 321);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 321 && resultCode != 0) {
            if (resultCode == -1) {
                this.mEditModeView.uninstallComplete();
            } else if (resultCode == 1) {
                this.mEditModeView.uninstallFailure();
            }
        }
    }

    private boolean setShyMode(boolean shy, boolean changeWallpaper) {
        boolean changed = false;
        if (this.mShyMode != shy) {
            this.mShyMode = shy;
            changed = true;
            if (this.mShyMode) {
            	 if (BuildConfig.DEBUG) Log.d(TAG, "setShyMode: convertFromTranslucent");
                convertFromTranslucent();
            } else {
                 if (BuildConfig.DEBUG) Log.d(TAG, "setShyMode: convertToTranslucent");
                // convertToTranslucent(null, null);
                convertToTranslucent();
            }
        }
        if (changeWallpaper && this.mWallpaper.getShynessMode() != shy) {
            this.mWallpaper.setShynessMode(this.mShyMode);
            if (this.mShyMode && this.mNotificationsView != null) {
                this.mNotificationsView.refreshSelectedBackground();
            }
        }
        return changed;
    }

	private void convertFromTranslucent() {
		if (MainActivity.this.isTaskRoot()) return;
		try {
			Method convertFromTranslucent = Activity.class.getDeclaredMethod("convertFromTranslucent");
			convertFromTranslucent.setAccessible(true);
			convertFromTranslucent.invoke(MainActivity.this);
		} catch (Throwable ignored) {
		}
	}

	private void convertToTranslucent() {
		try {
			Class<?> translucentConversionListenerClazz = null;
			for (Class<?> clazz : Activity.class.getDeclaredClasses()) {
				if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
					translucentConversionListenerClazz = clazz;
				}
			}
			Method convertToTranslucent = Activity.class.getDeclaredMethod("convertToTranslucent", new
					Class[]{translucentConversionListenerClazz, ActivityOptions.class});
			convertToTranslucent.setAccessible(true);
			convertToTranslucent.invoke(MainActivity.this, new Object[]{null, null});
		} catch (Throwable ignored) {
		}
	}

    private boolean dismissLauncher() {
        if (this.mShyMode) {
            return false;
        }
        this.mLaunchAnimation.init(new LauncherDismissAnimator(this.mList, this.mFadeDismissAndSummonAnimations, this.mHomeAdapter.getRowHeaders()), this.mMoveTaskToBack, (byte) 0);
        this.mLaunchAnimation.start();
        return true;
    }

    public void onNewIntent(Intent intent) {
        boolean exitingEditMode = false;
        if (this.mAppEditMode) {
            if (Util.isInTouchExploration(getApplicationContext())) {
                setTitle(R.string.app_label);
            }
            setEditMode(false, true);
            exitingEditMode = true;
        }
        if (this.mLaunchAnimation.isRunning()) {
            this.mLaunchAnimation.cancel();
            return;
        }
        if (this.mLaunchAnimation.isPrimed()) {
            this.mLaunchAnimation.reset();
        }
        if (this.mLaunchAnimation.isFinished()) {
            this.mLaunchAnimation.reset();
        }
        if (!exitingEditMode) {
            if (intent.getExtras() != null) {
                if (intent.getExtras().getBoolean("extra_start_customize_apps")) {
                    startEditMode(3);
                } else if (intent.getExtras().getBoolean("extra_start_customize_games")) {
                    startEditMode(4);
                }
            }
            if (!this.mStartingEditMode) {
                if (!hasWindowFocus() || intent.getBooleanExtra("com.android.systemui.recents.tv.RecentsTvActivity.RECENTS_HOME_INTENT_EXTRA", false)) {
                    if (!this.mLaunchAnimation.isScheduled()) {
                        resetLauncherState(false);
                        this.mLaunchAnimation.init(new MassSlideAnimator.Builder(this.mList).setDirection(MassSlideAnimator.Direction.SLIDE_IN).setFade(this.mFadeDismissAndSummonAnimations).build(), this.mRefreshHomeAdapter, (byte) 32);
                    }
                } else if (!dismissLauncher()) {
                    resetLauncherState(true);
                }
            }
        } else if (!this.mLaunchAnimation.isInitialized() && !this.mLaunchAnimation.isScheduled()) {
            resetLauncherState(false);
            this.mLaunchAnimation.init(new MassSlideAnimator.Builder(this.mList).setDirection(MassSlideAnimator.Direction.SLIDE_IN).setFade(this.mFadeDismissAndSummonAnimations).build(), this.mRefreshHomeAdapter, (byte) 32);
        }
    }

    protected void startEditMode(int rowType) {
        if (Util.isInTouchExploration(getApplicationContext())) {
            setTitle(rowType == 3 ? R.string.title_app_edit_mode : R.string.title_game_edit_mode);
        }
        this.mStartingEditMode = true;
        this.mHomeAdapter.resetRowPositions(false);
        this.mLaunchAnimation.cancel();
        this.mList.setSelectedPosition(this.mHomeAdapter.getRowIndex(rowType));
        this.mHomeAdapter.prepareEditMode(rowType);
    }

    private void resetLauncherState(boolean smooth) {
        this.mScrollManager.onScrolled(0, 0);
        this.mUserInteracted = false;
        this.mHomeAdapter.resetRowPositions(smooth);
        if (this.mAppEditMode) {
            boolean z;
            if (smooth) {
                z = true;
            } else {
                z = false;
            }
            setEditMode(false, z);
        }
        int notifIndex = this.mHomeAdapter.getRowIndex(1);
        if (this.mList.getAdapter() != null) {
            notifIndex = Math.min(this.mList.getAdapter().getItemCount() - 1, notifIndex);
        }
        if (!(notifIndex == -1 || this.mList.getSelectedPosition() == notifIndex)) {
            if (smooth) {
                this.mList.setSelectedPositionSmooth(notifIndex);
            } else {
                this.mList.setSelectedPosition(notifIndex);
                View focusedChild = this.mList.getFocusedChild();
                int focusedPosition = this.mList.getChildAdapterPosition(focusedChild);
                if (!(focusedChild == null || focusedPosition == notifIndex)) {
                    focusedChild.clearFocus();
                }
            }
            if (!(this.mShyMode || this.mNotificationsView == null)) {
                this.mNotificationsView.setIgnoreNextActivateBackgroundChange();
            }
        }
        this.mLaunchAnimation.cancel();
    }

	private boolean isBackgroundVisibleBehind() {
		try {
			Method isBackgroundVisibleBehind = Activity.class.getDeclaredMethod("isBackgroundVisibleBehind");
			isBackgroundVisibleBehind.setAccessible(true);
			//Object result = isBackgroundVisibleBehind.invoke(MainActivity.this);
			//if (Boolean.TRUE.equals(result))
			//	return true;
			Boolean result = (Boolean)isBackgroundVisibleBehind.invoke(MainActivity.this);
			return result.booleanValue();
		} catch (Throwable ignored) {
		}
		return false;
	}

    protected void onStart() {
        boolean z = true;
        AppTrace.beginSection("onStart");
        try {
            super.onStart();
            this.mResetAfterIdleEnabled = false;
            if (this.mAppWidgetHost != null) {
                this.mAppWidgetHost.startListening();
            }
			if (isBackgroundVisibleBehind()) {
				if (BuildConfig.DEBUG) Log.d(TAG, "onStart: BackgroundVisibleBehind");
				z = false;
			}
            setShyMode(z, true);
            this.mWallpaper.resetBackground();
            this.mHomeAdapter.refreshAdapterData();
            if (this.mKeepUiReset) {
                resetLauncherState(false);
            }
            if (!this.mStartingEditMode) {
                if (!this.mLaunchAnimation.isInitialized()) {
                    this.mLaunchAnimation.init(new LauncherReturnAnimator(this.mList, this.mLaunchAnimation.lastKnownEpicenter, this.mHomeAdapter.getRowHeaders(), this.mHomeScreenView), this.mRefreshHomeAdapter, (byte) 32);
                }
                this.mLaunchAnimation.schedule();
            }
            this.mStartingEditMode = false;

            AppTrace.endSection();
        } catch (Throwable th) {
            AppTrace.endSection();
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
    }

    protected void onResume() {
        boolean forceResort = true;
        AppTrace.beginSection("onResume");
        try {
            boolean z = true;
            super.onResume();
			if (isBackgroundVisibleBehind()) {
				if (BuildConfig.DEBUG) Log.d(TAG, "onResume: BackgroundVisibleBehind");
				z = false;
			}
            boolean shyChanged = setShyMode(z, true);
            if (!AppsManager.getInstance(getApplicationContext()).checkIfResortingIsNeeded() || this.mAppEditMode) {
                forceResort = false;
            }
            this.mHomeAdapter.sortRowsIfNeeded(forceResort);
            WallpaperInstaller.getInstance(this).installWallpaperIfNeeded();
            this.mWallpaper.setShynessMode(this.mShyMode);
            if (shyChanged) {
                this.mWallpaper.resetBackground();
            }
            if (this.mShyMode && this.mNotificationsView != null) {
                this.mNotificationsView.refreshSelectedBackground();
            }
            if (!this.mHandler.hasMessages(6)) {
                this.mHandler.sendEmptyMessage(6);
            }
            if (this.mHomeAdapter != null) {
                this.mHomeAdapter.animateSearchIn();
            }
            for (int i = 0; i < this.mIdleListeners.size(); i++) {
                ((IdleListener) this.mIdleListeners.get(i)).onVisibilityChange(true);
            }
            this.mResetAfterIdleEnabled = true;
            this.mHandler.removeMessages(3);
            this.mHandler.removeMessages(1);
            if (this.mIsIdle) {
                this.mHandler.sendEmptyMessage(2);
            } else {
                this.mHandler.sendEmptyMessageDelayed(1, (long) this.mIdlePeriod);
            }
            this.mHandler.sendEmptyMessageDelayed(3, (long) this.mResetPeriod);
            this.mHandler.sendEmptyMessageDelayed(7, 2000);
            if (this.mLaunchAnimation.isFinished()) {
                this.mLaunchAnimation.reset();
            }
            if (this.mLaunchAnimation.isInitialized()) {
                this.mLaunchAnimation.reset();
            }
            if (this.mLaunchAnimation.isScheduled()) {
                primeAnimationAfterLayout();
            }
            this.mPauseAnimation.reset();
            if (this.mAppEditMode) {
                this.mEditModeAnimation.init(new EditModeMassFadeAnimator(this, EditMode.ENTER), null, (byte) 0);
                this.mEditModeAnimation.start();
            }
            this.mUninstallRequested = false;
            overridePendingTransition(R.anim.home_fade_in_top, R.anim.home_fade_out_bottom);
            if (!(this.mHomeAdapter == null || this.mHomeAdapter.isUiVisible() || this.mDelayFirstRecommendationsVisible)) {
                this.mHomeAdapter.onUiVisible();
            }
            AppTrace.endSection();
        } catch (Throwable th) {
            AppTrace.endSection();
        }
    }

    public void onEnterAnimationComplete() {
        if (this.mLaunchAnimation.isScheduled() || this.mLaunchAnimation.isPrimed()) {
            this.mLaunchAnimation.start();
        }
    }

    protected void onPause() {
        AppTrace.beginSection("onPause");
        try {
            super.onPause();
            this.mResetAfterIdleEnabled = false;
            this.mLaunchAnimation.cancel();
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(6);
            this.mHandler.removeMessages(7);
            for (int i = 0; i < this.mIdleListeners.size(); i++) {
                this.mIdleListeners.get(i).onVisibilityChange(false);
            }
            if (this.mAppEditMode) {
                this.mEditModeAnimation.init(new EditModeMassFadeAnimator(this, EditMode.EXIT), null, (byte) 0);
                this.mEditModeAnimation.start();
            }
            this.mPauseAnimation.init(new LauncherPauseAnimator(this.mList), null, (byte) 0);
            this.mPauseAnimation.start();
            if (this.mHomeAdapter != null && this.mHomeAdapter.isUiVisible()) {
                this.mHomeAdapter.onUiInvisible();
                this.mDelayFirstRecommendationsVisible = false;
            }
            AppTrace.endSection();
        } catch (Throwable th) {
            AppTrace.endSection();
        }
    }

    protected void onStop() {
        AppTrace.beginSection("onStop");
        try {
            this.mResetAfterIdleEnabled = true;
            if (this.mAppWidgetHost != null) {
                this.mAppWidgetHost.stopListening();
            }
            this.mHandler.removeCallbacksAndMessages(null);
            this.mHandler.sendEmptyMessageDelayed(3, (long) this.mResetPeriod);
            if (this.mAppEditMode) {
                setEditMode(false, false);
            }
            setShyMode(false, false);
            this.mHomeAdapter.sortRowsIfNeeded(false);
            this.mLaunchAnimation.reset();

            AppTrace.endSection();
        } catch (Throwable th) {
            AppTrace.endSection();
        }
        super.onStop();
    }

    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        AppsManager.getInstance(getApplicationContext()).dump(prefix, writer);
        this.mLaunchAnimation.dump(prefix, writer, this.mList);
        this.mHomeAdapter.dump(prefix, writer);
    }

    private int getCurrentScrollPos() {
        int pos = 0;
        int topView = -1;
        int i = 0;
        while (i < this.mList.getChildCount()) {
            View v = this.mList.getChildAt(i);
            if (v == null || v.getTop() > 0) {
                i++;
            } else {
                topView = this.mList.getChildAdapterPosition(v);
                if (v.getMeasuredHeight() > 0) {
                    pos = (int) ((((float) this.mHomeAdapter.getScrollOffset(topView)) * (((float) Math.abs(v.getTop())) / ((float) v.getMeasuredHeight()))) * -1.0f);
                }
                for (topView--; topView >= 0; topView--) {
                    pos -= this.mHomeAdapter.getScrollOffset(topView);
                }
                return pos;
            }
        }
        for (topView--; topView >= 0; topView--) {
            pos -= this.mHomeAdapter.getScrollOffset(topView);
        }
        return pos;
    }

    private void onNotificationRowStateUpdate(int state) {
        if (state == 1 || state == 2) {
            if (!this.mUserInteracted) {
                int searchIndex = this.mHomeAdapter.getRowIndex(0);
                if (searchIndex != -1) {
                    this.mList.setSelectedPosition(searchIndex);
                    this.mList.getChildAt(searchIndex).requestFocus();
                }
            }
        } else if (state == 0 && this.mList.getSelectedPosition() <= this.mHomeAdapter.getRowIndex(1) && this.mNotificationsView.getChildCount() > 0) {
            this.mNotificationsView.setSelectedPosition(0);
            this.mNotificationsView.getChildAt(0).requestFocus();
        }
    }

    public boolean onSearchRequested() {
        setShyMode(true, true);
        return super.onSearchRequested();
    }

    public static boolean isMediaKey(int keyCode) {
        switch (keyCode) {
            case 79:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 126:
            case 127:
            case 130:
                return true;
            default:
                return false;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (this.mLaunchAnimation.isPrimed() || this.mLaunchAnimation.isRunning() || this.mEditModeAnimation.isPrimed() || this.mEditModeAnimation.isRunning()) {
            switch (keyCode) {
                case 3: // KEYCODE_HOME
                case 4: // KEYCODE_BACK
                    return super.onKeyDown(keyCode, event);
                default:
                    return true;
            }
        } else if (this.mShyMode || !isMediaKey(event.getKeyCode())) {
            return super.onKeyDown(keyCode, event);
        } else {
            return true;
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_INFO) {
            View selectItem = mList.getFocusedChild();

            if (selectItem instanceof ActiveFrame) {
                ActiveItemsRowView v = ((ActiveFrame) selectItem).mRow;
                View child = v.getFocusedChild(); // todo

                if (child instanceof BannerView) {
                    BannerView banner = (BannerView) child;
                    AppsAdapter.AppViewHolder holder = banner.getViewHolder();

                    if (holder != null) { // holder == null when the holder is an input
                        String pkg = holder.getPackageName();

                        Intent intent = new Intent(this, AppInfoActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putString("pkg", pkg);
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                }
            }

            return true;
        }


        if (this.mShyMode || !isMediaKey(event.getKeyCode())) {
            return super.onKeyUp(keyCode, event);
        }
        switch (keyCode) {
            case 79:  // KEYCODE_HEADSETHOOK
            case 85:  // KEYCODE_MEDIA_PLAY_PAUSE
            case 86:  // KEYCODE_MEDIA_STOP
            case 127: // KEYCODE_MEDIA_PAUSE
                setShyMode(true, true);
                return true;
            default:
                return true;
        }
    }

    private void addWidget(boolean refresh) {
        ViewGroup wrapper = (LinearLayout) findViewById(R.id.widget_wrapper);
        if (wrapper != null) {
            if (refresh || this.mAppWidgetHostView == null) {
                wrapper.removeAllViews();
                boolean success = false;
                int appWidgetId = Util.getWidgetId(this);
                ComponentName appWidgetComp = Partner.get(this).getWidgetComponentName();
                if (appWidgetComp != null) {
                    for (AppWidgetProviderInfo appWidgetInfo : this.mAppWidgetManager.getInstalledProviders()) {
                        if (appWidgetComp.equals(appWidgetInfo.provider)) {
                            if (appWidgetId != 0) {
                                success = true;
                            } else {
                                success = false;
                            }
                            if (success && !appWidgetComp.equals(Util.getWidgetComponentName(this))) {
                                clearWidget(appWidgetId);
                                success = false;
                            }
                            if (!success) {
                                int width = (int) getResources().getDimension(R.dimen.widget_width);
                                int height = (int) getResources().getDimension(R.dimen.widget_height);
                                Bundle options = new Bundle();
                                options.putInt("appWidgetMinWidth", width);
                                options.putInt("appWidgetMaxWidth", width);
                                options.putInt("appWidgetMinHeight", height);
                                options.putInt("appWidgetMaxHeight", height);
                                appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
                                success = this.mAppWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, appWidgetInfo.provider, options);
                            }
                            if (success) {
                                this.mAppWidgetHostView = this.mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
                                this.mAppWidgetHostView.setAppWidget(appWidgetId, appWidgetInfo);
                                wrapper.addView(this.mAppWidgetHostView);
                                Util.setWidget(this, appWidgetId, appWidgetInfo.provider);
                            }
                        }
                    }
                }
                if (!success) {
                    clearWidget(appWidgetId);
                    wrapper.addView(LayoutInflater.from(this).inflate(R.layout.clock, wrapper, false));
                    return;
                }
                return;
            }
            ViewGroup parent = (ViewGroup) this.mAppWidgetHostView.getParent();
            if (parent != wrapper) {
                if (parent != null) {
                    parent.removeView(this.mAppWidgetHostView);
                }
                wrapper.removeAllViews();
                wrapper.addView(this.mAppWidgetHostView);
            }
        }
    }

    private void clearWidget(int appWidgetId) {
        if (appWidgetId != 0) {
            this.mAppWidgetHost.deleteAppWidgetId(appWidgetId);
        }
        Util.clearWidget(this);
    }

    public LauncherWallpaper getWallpaperView() {
        return this.mWallpaper;
    }

    public EditModeView getEditModeView() {
        return this.mEditModeView;
    }

    public View getEditModeWallpaper() {
        return findViewById(R.id.edit_mode_background);
    }

    public boolean isInEditMode() {
        return this.mAppEditMode;
    }

    private void setEditMode(boolean editMode, boolean useAnimation) {
        float f = 1.0f;
        this.mEditModeAnimation.reset();
        if (useAnimation) {
            this.mEditModeAnimation.init(new EditModeMassFadeAnimator(this, editMode ? EditMode.ENTER : EditMode.EXIT), null, (byte) 0);
            this.mEditModeAnimation.start();
        } else {
            float f2;
            LauncherWallpaper launcherWallpaper = this.mWallpaper;
            if (editMode) {
                f2 = 0.0f;
            } else {
                f2 = 1.0f;
            }
            launcherWallpaper.setAlpha(f2);
            View editModeWallpaper = getEditModeWallpaper();
            if (!editMode) {
                f = 0.0f;
            }
            editModeWallpaper.setAlpha(f);
            getEditModeWallpaper().setVisibility(0);
            this.mHomeAdapter.setRowAlphas(editMode ? 0 : 1);
        }
        if (!editMode && this.mAppEditMode) {
            for (int i = 0; i < this.mList.getChildCount(); i++) {
                View activeFrame = this.mList.getChildAt(i);
                if (activeFrame instanceof ActiveFrame) {
                    for (int j = 0; j < ((ActiveFrame) activeFrame).getChildCount(); j++) {
                        View activeItemsRow = ((ActiveFrame) activeFrame).getChildAt(j);
                        if (activeItemsRow instanceof EditableAppsRowView) {
                            ((EditableAppsRowView) activeItemsRow).setEditMode(false);
                        }
                    }
                }
            }
        }
        this.mAppEditMode = editMode;
    }

    public HomeScreenAdapter getHomeAdapter() {
        return this.mHomeAdapter;
    }

    private boolean checkFirstRunAfterBoot() {
        Intent dummyIntent = new Intent("android.intent.category.LEANBACK_LAUNCHER");
        dummyIntent.setClass(this, DummyActivity.class);

        boolean firstRun = PendingIntent.getActivity(this, 0, dummyIntent, 536870912) == null;

        if (firstRun) {
            ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).set(2, SystemClock.elapsedRealtime() + 864000000000L, PendingIntent.getActivity(this, 0, dummyIntent, 0));
        }

        return firstRun;
    }

    public void beginLaunchAnimation(View view, boolean translucent, int color, Runnable onCompleteCallback) {
        if (!this.mLaunchAnimation.isPrimed() && !this.mLaunchAnimation.isRunning() && !this.mLaunchAnimation.isFinished()) {
            getBoundsOnScreen(view, this.mLaunchAnimation.lastKnownEpicenter);
            if (translucent) {
                onCompleteCallback.run();
                return;
            }
            ForwardingAnimatorSet animation;
            if (view instanceof NotificationCardView) {
                animation = new NotificationLaunchAnimator(this.mList, (NotificationCardView) view, this.mLaunchAnimation.lastKnownEpicenter, (ImageView) findViewById(R.id.click_circle_layer), color, this.mHomeAdapter.getRowHeaders(), this.mHomeScreenView);
            } else {
                animation = new LauncherLaunchAnimator(this.mList, view, this.mLaunchAnimation.lastKnownEpicenter, (ImageView) findViewById(R.id.click_circle_layer), color, this.mHomeAdapter.getRowHeaders(), this.mHomeScreenView);
            }
            this.mLaunchAnimation.init(animation, onCompleteCallback, (byte) 0);
            this.mLaunchAnimation.start();
        }
    }

    public boolean isLaunchAnimationInProgress() {
        return this.mLaunchAnimation.isPrimed() || this.mLaunchAnimation.isRunning();
    }

    public boolean isEditAnimationInProgress() {
        return this.mEditModeAnimation.isPrimed() || this.mEditModeAnimation.isRunning();
    }

    public void includeInLaunchAnimation(View target) {
        this.mLaunchAnimation.include(target);
    }

    public void includeInEditAnimation(View target) {
        this.mEditModeAnimation.include(target);
    }

    public void excludeFromLaunchAnimation(View target) {
        this.mLaunchAnimation.exclude(target);
    }

    public void excludeFromEditAnimation(View target) {
        this.mEditModeAnimation.exclude(target);
    }

    public void setOnLaunchAnimationFinishedListener(OnAnimationFinishedListener l) {
        this.mLaunchAnimation.setOnAnimationFinishedListener(l);
    }

    private void primeAnimationAfterLayout() {
        this.mList.getRootView().getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                MainActivity.this.mList.getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (MainActivity.this.mLaunchAnimation.isScheduled()) {
                    MainActivity.this.mLaunchAnimation.prime();
                }
            }
        });
        this.mList.requestLayout();
    }

    private static void getBoundsOnScreen(View v, Rect epicenter) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        epicenter.left = location[0];
        epicenter.top = location[1];
        epicenter.right = epicenter.left + Math.round(((float) v.getWidth()) * v.getScaleX());
        epicenter.bottom = epicenter.top + Math.round(((float) v.getHeight()) * v.getScaleY());
    }

    private void checkLaunchPointPositions() {
        if (!this.mLaunchAnimation.isRunning() && checkViewHierarchy(this.mList)) {
            StringWriter buf = new StringWriter();
            buf.append("Caught partially animated state; resetting...\n");
            this.mLaunchAnimation.dump("", new PrintWriter(buf), this.mList);
            Log.w("Animations", buf.toString());
            this.mLaunchAnimation.reset();
        }
    }

    private boolean checkViewHierarchy(View view) {
        if ((view instanceof ParticipatesInLaunchAnimation) && view.getTranslationY() != 0.0f) {
            return true;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int n = group.getChildCount();
            for (int i = 0; i < n; i++) {
                if (checkViewHierarchy(group.getChildAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }
}
