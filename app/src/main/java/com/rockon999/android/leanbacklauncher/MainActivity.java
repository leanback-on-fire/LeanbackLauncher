package com.rockon999.android.leanbacklauncher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rockon999.android.leanbacklauncher.adapter.AllAppGridAdapter;
import com.rockon999.android.leanbacklauncher.animation.AnimatorLifecycle;
import com.rockon999.android.leanbacklauncher.animation.EditModeMassFadeAnimator;
import com.rockon999.android.leanbacklauncher.animation.ForwardingAnimatorSet;
import com.rockon999.android.leanbacklauncher.animation.LauncherDismissAnimator;
import com.rockon999.android.leanbacklauncher.animation.LauncherLaunchAnimator;
import com.rockon999.android.leanbacklauncher.animation.LauncherPauseAnimator;
import com.rockon999.android.leanbacklauncher.animation.LauncherReturnAnimator;
import com.rockon999.android.leanbacklauncher.animation.MassSlideAnimator;
import com.rockon999.android.leanbacklauncher.animation.MassSlideAnimator.Direction;
import com.rockon999.android.leanbacklauncher.animation.ParticipatesInLaunchAnimation;
import com.rockon999.android.leanbacklauncher.apps.AppsAdapter;
import com.rockon999.android.leanbacklauncher.apps.AppsRanker;
import com.rockon999.android.leanbacklauncher.apps.BannerView;
import com.rockon999.android.leanbacklauncher.apps.LaunchPoint;
import com.rockon999.android.leanbacklauncher.apps.LaunchPointListGenerator;
import com.rockon999.android.leanbacklauncher.apps.OnEditModeChangedListener;
import com.rockon999.android.leanbacklauncher.apps.notifications.NotificationListenerMonitor;
import com.rockon999.android.leanbacklauncher.data.ConstData;
import com.rockon999.android.leanbacklauncher.logging.LeanbackLauncherEventLogger;
import com.rockon999.android.leanbacklauncher.modle.db.AppInfoService;
import com.rockon999.android.leanbacklauncher.recline.util.DrawableDownloader;
import com.rockon999.android.leanbacklauncher.settings.AppInfoActivity;
import com.rockon999.android.leanbacklauncher.util.ReflectUtils;
import com.rockon999.android.leanbacklauncher.util.Util;
import com.rockon999.android.leanbacklauncher.wallpaper.LauncherWallpaper;
import com.rockon999.android.leanbacklauncher.widget.AllAppGridView;
import com.rockon999.android.leanbacklauncher.widget.EditModeView;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements OnEditModeChangedListener, EditModeView.OnEditModeUninstallPressedListener, AdapterView.OnItemClickListener, Runnable {
    private static final String TAG = "MainActivity_LEANBACK";
    private AccessibilityManager mAccessibilityManager;
    private boolean mAppEditMode;
    private AppWidgetHost mAppWidgetHost;
    private AppWidgetHostView mAppWidgetHostView;
    private AppWidgetManager mAppWidgetManager;
    private AppsRanker mAppsRanker;
    private AnimatorLifecycle mEditModeAnimation;
    private EditModeView mEditModeView;
    private LeanbackLauncherEventLogger mEventLogger;
    private boolean mFadeDismissAndSummonAnimations;
    private Handler mHandler;
    private HomeScreenAdapter mHomeAdapter;
    private ArrayList<IdleListener> mIdleListeners;
    private int mIdlePeriod;
    private boolean mIsIdle;
    private boolean mKeepUiReset;
    private AnimatorLifecycle mLaunchAnimation;
    private LaunchPointListGenerator mLaunchPointListGenerator;
    private VerticalGridView mList;
    private final Runnable mMoveTaskToBack;

    BroadcastReceiver mPackageReplacedReceiver;
    private AnimatorLifecycle mPauseAnimation;
    private final Runnable mRefreshHomeAdapter;
    private boolean mResetAfterIdleEnabled;
    private int mResetPeriod;
    private HomeScrollManager mScrollManager;
    private boolean mShyMode;
    private boolean mStartingEditMode;
    private boolean mUserInteracted;
    private LauncherWallpaper mWallpaper;

    private RelativeLayout mLayoutLoading;
    private GridView mAllAppGridView;
    private AllAppGridAdapter mAllAppGridAdapter;
    private List<LaunchPoint> mAllLaunchPoints;
    /**
     * Whether the launcher is being loaded for the first time...
     */
    private boolean mIsFirstLoad = true;

    private int mActivityState;
    private Handler mTimerHandler;

    /* renamed from: MainActivity.1 */
    class C01581 implements Runnable {
        C01581() {
        }

        public void run() {
            if (MainActivity.this.mHomeAdapter != null) {
                MainActivity.this.mHomeAdapter.refreshAdapterData();
            }
        }
    }

    /* renamed from: MainActivity.2 */
    class ResetLaunchAnimation implements Runnable {
        ResetLaunchAnimation() {
        }

        public void run() {
            if (!MainActivity.this.moveTaskToBack(true)) {
                MainActivity.this.mLaunchAnimation.reset();
            }
        }
    }

    /* renamed from: MainActivity.3 */
    class C01603 extends Handler {
        C01603() {
        }

        @SuppressLint("PrivateResource")
        public void handleMessage(Message msg) {
            boolean z = true;
            switch (msg.what) {
                case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                    MainActivity mainActivity = MainActivity.this;
                    if (msg.what != 1) {
                        z = false;
                    }
                    mainActivity.mIsIdle = z;
                    for (int i = 0; i < MainActivity.this.mIdleListeners.size(); i++) {
                        MainActivity.this.mIdleListeners.get(i).onIdleStateChange(MainActivity.this.mIsIdle);
                    }

                    break;
                case android.support.v7.preference.R.styleable.Preference_android_layout /*3*/:
                    if (MainActivity.this.mResetAfterIdleEnabled) {
                        MainActivity.this.mKeepUiReset = true;
                        MainActivity.this.resetLauncherState(true);
                    }
                    break;
                case android.support.v7.preference.R.styleable.Preference_android_title /*4*/:
                    MainActivity.this.onNotificationRowStateUpdate(msg.arg1);
                    break;
                case android.support.v7.preference.R.styleable.Preference_android_key /*6*/:
                    MainActivity.this.addWidget(true);
                    break;
                case android.support.v7.preference.R.styleable.Preference_android_summary /*7*/:
                    MainActivity.this.checkLaunchPointPositions();
                default:
            }
        }
    }


    /* renamed from: MainActivity.5 */
    class PackageUpdateReceiver extends BroadcastReceiver {
        PackageUpdateReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            Uri packageName = null;
            if (intent != null) {
                packageName = intent.getData();
            }
            if (packageName != null && packageName.toString().contains("com.rockon999.android.leanbacklauncher.recommendations")) {
                Log.d("LeanbackLauncher", "Recommendations Service updated, reconnecting");
                if (MainActivity.this.mHomeAdapter != null) {
                    MainActivity.this.mHomeAdapter.onReconnectToRecommendationsService();
                }
            }
        }
    }

    /* renamed from: MainActivity.6 */
    class C01656 implements OnHierarchyChangeListener {

        /* renamed from: MainActivity.6.2 */
        class C01642 implements SearchOrbView.SearchLaunchListener {
            C01642() {
            }

            public void onSearchLaunched() {
                MainActivity.this.setShyMode(true, true);
            }
        }


        @SuppressLint("PrivateResource")
        public void onChildViewAdded(View parent, View child) {
            int tag = 0;
            if (child.getTag() instanceof Integer) {
                tag = (Integer) child.getTag();
            }
            switch (tag) {
                case android.support.v7.preference.R.styleable.Preference_android_icon /*0*/:
                    if (child instanceof SearchOrbView) {
                        ((SearchOrbView) child).setLaunchListener(new C01642());
                    }
                    MainActivity.this.addWidget(false);
                    break;
                case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                case android.support.v7.recyclerview.R.styleable.RecyclerView_layoutManager /*2*/:
                    break;
            }
            if ((child instanceof IdleListener) && !MainActivity.this.mIdleListeners.contains(child)) {
                MainActivity.this.addIdleListener((IdleListener) child);
            }
        }

        public void onChildViewRemoved(View parent, View child) {
        }
    }

    /* renamed from: MainActivity.7 */
    class C01667 extends OnScrollListener {
        C01667() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            MainActivity.this.mScrollManager.onScrolled(dy, MainActivity.this.getCurrentScrollPos());
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            MainActivity.this.mScrollManager.onScrollStateChanged(newState);
        }
    }

    /* renamed from: MainActivity.8 */
    class C01678 implements AnimatorLifecycle.OnAnimationFinishedListener {
        C01678() {
        }

        public void onAnimationFinished() {
            MainActivity.this.mList.setVisibility(View.GONE);
        }
    }

    /* renamed from: MainActivity.9 */
    class C01689 implements OnGlobalLayoutListener {
        C01689() {
        }

        public void onGlobalLayout() {
            MainActivity.this.mList.getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
            if (MainActivity.this.mLaunchAnimation.isScheduled()) {
                MainActivity.this.mLaunchAnimation.prime();
            }
        }
    }

    public interface IdleListener {
        void onIdleStateChange(boolean z);

        void onVisibilityChange(boolean z);
    }

    public MainActivity() {
        this.mRefreshHomeAdapter = new C01581();
        this.mMoveTaskToBack = new ResetLaunchAnimation();
        this.mLaunchAnimation = new AnimatorLifecycle();
        this.mEditModeAnimation = new AnimatorLifecycle();
        this.mPauseAnimation = new AnimatorLifecycle();
        this.mIdleListeners = new ArrayList<>();
        this.mUserInteracted = false;
        this.mHandler = new C01603();

        this.mPackageReplacedReceiver = new PackageUpdateReceiver();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsFirstLoad = true;
        mTimerHandler = new Handler();

        Context appContext = getApplicationContext();
        DrawableDownloader.getInstance(appContext);
        setContentView(R.layout.activity_main);
        this.mAccessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        this.mAppsRanker = AppsRanker.getInstance(this);
        this.mLaunchPointListGenerator = new LaunchPointListGenerator(this);
        this.mLaunchPointListGenerator.addToBlacklist(BuildConfig.APPLICATION_ID, false);
        mLayoutLoading = (RelativeLayout) findViewById(R.id.layout_loading);
        mAllAppGridView = (AllAppGridView) findViewById(R.id.grid_all_app);
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
        this.mHomeAdapter = new HomeScreenAdapter(this, this.mScrollManager, this.mLaunchPointListGenerator, this.mEditModeView, this.mAppsRanker);
        this.mHomeAdapter.setOnEditModeChangedListener(this);
        Log.i(TAG, "onCreate->HomeAdapter->size:" + mHomeAdapter.getItemCount());
        this.mList.setItemViewCacheSize(this.mHomeAdapter.getItemCount());
        this.mList.setAdapter(this.mHomeAdapter);
        this.mList.setOnChildSelectedListener(this.mHomeAdapter);
        this.mList.setAnimateChildLayout(false);
        int notifIndex = this.mHomeAdapter.getRowIndex(1);
        if (notifIndex != -1) {
            this.mList.setSelectedPosition(notifIndex);
        }
        this.mList.setOnHierarchyChangeListener(new C01656());
        this.mList.addOnScrollListener(new C01667());
        this.mShyMode = true;
        setShyMode(!this.mShyMode, true);
        this.mIdlePeriod = getResources().getInteger(R.integer.idle_period);
        this.mResetPeriod = getResources().getInteger(R.integer.reset_period);
        this.mFadeDismissAndSummonAnimations = getResources().getBoolean(R.bool.app_launch_animation_fade);
        this.mKeepUiReset = true;
        this.mHomeAdapter.onInitUi();
        this.mEventLogger = LeanbackLauncherEventLogger.getInstance(appContext);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_REPLACED");
        filter.addDataScheme("package");
        registerReceiver(this.mPackageReplacedReceiver, filter);

        startService(new Intent(this, NotificationListenerMonitor.class));

    }

    public void onDestroy() {
        this.mHandler.removeMessages(3);
        mTimerHandler.removeCallbacks(this);
        super.onDestroy();
        if (this.mHomeAdapter != null) {
            this.mHomeAdapter.onStopUi();
            this.mHomeAdapter.unregisterReceivers();
        }
        if (this.mAppsRanker != null) {
            this.mAppsRanker.unregisterListeners();
        }
        unregisterReceiver(this.mPackageReplacedReceiver);
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
        boolean z;
        z = !visible;
        setShyMode(z, true);
    }

    public void onEditModeChanged(boolean editMode) {
        Log.i(TAG, "onEditModeChanged->editMode:" + editMode);
        Log.i(TAG, "onEditModeChanged->mAccessibilityManager->isEnabled:" + mAccessibilityManager.isEnabled());
        if (this.mAppEditMode == editMode) {
            return;
        }
        setEditMode(editMode, false);
        /*if (this.mAccessibilityManager.isEnabled()) {
            setEditMode(editMode, false);
        } else {
            setEditMode(editMode, true);
        }*/
    }

    public void onUninstallPressed(String packageName) {
        if (packageName != null) {
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
            /*
            if (this.mShyMode) {
                convertFromTranslucent();
            } else {
                convertToTranslucent(null, null);
            }*/
        }
        if (changeWallpaper && this.mWallpaper.getShynessMode() != shy) {
            this.mWallpaper.setShynessMode(this.mShyMode);

        }
        return changed;
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
                        this.mLaunchAnimation.init(new MassSlideAnimator.Builder(this.mList).setDirection(Direction.SLIDE_IN).setFade(this.mFadeDismissAndSummonAnimations).build(), this.mRefreshHomeAdapter, (byte) 32);
                    }
                } else if (!dismissLauncher()) {
                    resetLauncherState(true);
                }
            }
        } else if (!this.mLaunchAnimation.isInitialized() && !this.mLaunchAnimation.isScheduled()) {
            resetLauncherState(false);
            this.mLaunchAnimation.init(new MassSlideAnimator.Builder(this.mList).setDirection(Direction.SLIDE_IN).setFade(this.mFadeDismissAndSummonAnimations).build(), this.mRefreshHomeAdapter, (byte) 32);
        }
    }

    protected void startEditMode(int rowType) {
        if (Util.isInTouchExploration(getApplicationContext())) {
            int i;
            if (rowType == 3) {
                i = R.string.title_app_edit_mode;
            } else {
                i = R.string.title_game_edit_mode;
            }
            setTitle(i);
        }
        this.mStartingEditMode = true;
        this.mHomeAdapter.resetRowPositions(false);
        this.mLaunchAnimation.cancel();
        this.mList.setSelectedPosition(this.mHomeAdapter.getRowIndex(rowType));
        this.mHomeAdapter.prepareEditMode(rowType);
    }

    private void resetLauncherState(boolean smooth) {
        Log.i(TAG, "resetLauncherState");
        this.mScrollManager.onScrolled(0, 0);
        this.mUserInteracted = false;
        this.mHomeAdapter.resetRowPositions(smooth);
        if (this.mAppEditMode) {
            boolean z;
            z = smooth;
            setEditMode(false, z);
        }
        int notifIndex = this.mHomeAdapter.getRowIndex(1);
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

        }
        this.mLaunchAnimation.cancel();
    }

    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        this.mResetAfterIdleEnabled = false;
        if (this.mAppWidgetHost != null) {
            this.mAppWidgetHost.startListening();
        }
        //setShyMode(!isBackgroundVisibleBehind(), true);
        this.mWallpaper.resetBackground();
        /*if (this.mHomeAdapter != null) {
            this.mHomeAdapter.refreshAdapterData();
        }*/
        if (this.mKeepUiReset) {
            resetLauncherState(false);
        }
        if (!this.mStartingEditMode) {
            if (!this.mLaunchAnimation.isInitialized()) {
                this.mLaunchAnimation.init(new LauncherReturnAnimator(this.mList, this.mLaunchAnimation.lastKnownEpicenter, this.mHomeAdapter.getRowHeaders()), this.mRefreshHomeAdapter, (byte) 32);
            }
            this.mLaunchAnimation.schedule();
        }
        this.mStartingEditMode = false;
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    protected void onSaveInstanceState(Bundle savedInstanceState) {
    }

    protected void onResume() {
        super.onResume();
        mActivityState = 1;
        Log.i(TAG, "onResume");
        boolean backgroudVisible = (boolean) ReflectUtils.invokeMethod(getClass().getSuperclass(), this, "isBackgroundVisibleBehind", new Class[]{}, new Object[]{});
        boolean z = !backgroudVisible;
        boolean shyChanged = setShyMode(z, true);
        this.mHomeAdapter.sortRowsIfNeeded(this.mAppsRanker.checkIfResortingIsNeeded());
        //WallpaperInstaller.getInstance(this).installWallpaperIfNeeded();
        this.mWallpaper.setShynessMode(this.mShyMode);
        if (shyChanged) {
            this.mWallpaper.resetBackground();
        }

        if (!this.mHandler.hasMessages(6)) {
            this.mHandler.sendEmptyMessage(6);
        }
        if (this.mHomeAdapter != null) {
            this.mHomeAdapter.animateSearchIn();
        }
        for (int i = 0; i < this.mIdleListeners.size(); i++) {
            Log.i(TAG, "mIdleListeners.get(i)->className:" + mIdleListeners.get(i).getClass().getName());
            this.mIdleListeners.get(i).onVisibilityChange(true);
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
            new EditModeMassFadeAnimator(this, EditModeMassFadeAnimator.EditMode.ENTER).start();
        }
        if (mIsFirstLoad) {
            mList.setVisibility(View.GONE);
            mIsFirstLoad = false;
            Log.i(TAG, "load app item start time:" + System.currentTimeMillis());
            mTimerHandler.post(this);
        } else {
            this.mList.setVisibility(View.VISIBLE);
        }
        overridePendingTransition(R.anim.home_fade_in_top, R.anim.home_fade_out_bottom);
        if (this.mHomeAdapter != null) {
            this.mHomeAdapter.onUiVisible();
        }
    }

    public void onEnterAnimationComplete() {
        if (this.mLaunchAnimation.isScheduled() || this.mLaunchAnimation.isPrimed()) {
            this.mLaunchAnimation.start();
        }
    }

    protected void onPause() {
        super.onPause();
        mActivityState = 0;
        Log.i(TAG, "onPause");
        this.mResetAfterIdleEnabled = false;
        this.mLaunchAnimation.cancel();
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(6);
        this.mHandler.removeMessages(7);
        for (int i = 0; i < this.mIdleListeners.size(); i++) {
            this.mIdleListeners.get(i).onVisibilityChange(false);
        }
        if (this.mAppEditMode) {
            new EditModeMassFadeAnimator(this, EditModeMassFadeAnimator.EditMode.EXIT).start();
        }
        this.mPauseAnimation.init(new LauncherPauseAnimator(this.mList), null, (byte) 0);
        this.mPauseAnimation.setOnAnimationFinishedListener(new C01678());
        this.mPauseAnimation.start();
        if (this.mHomeAdapter != null) {
            this.mHomeAdapter.onUiInvisible();
        }
    }

    protected void onStop() {
        this.mResetAfterIdleEnabled = true;
        if (this.mAppWidgetHost != null) {
            this.mAppWidgetHost.stopListening();
        }
        this.mHandler.removeCallbacksAndMessages(null);
        this.mHandler.sendEmptyMessageDelayed(3, (long) this.mResetPeriod);
        if (this.mAppEditMode) {
            setEditMode(false, false);
        }
        super.onStop();
        setShyMode(false, false);
        this.mHomeAdapter.sortRowsIfNeeded(false);
        this.mLaunchAnimation.reset();
    }

    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        this.mAppsRanker.dump(prefix, writer);
        this.mLaunchAnimation.dump(prefix, writer, this.mList);
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
        Log.i(TAG, "onNotificationRowStateUpdate");
        if (state == 1 || state == 2) {
            if (!this.mUserInteracted) {
                int searchIndex = this.mHomeAdapter.getRowIndex(0);
                if (searchIndex != -1) {
                    this.mList.setSelectedPosition(searchIndex);
                    this.mList.getChildAt(searchIndex).requestFocus();
                }
            }
        }
    }

    public boolean onSearchRequested() {
        setShyMode(true, true);
        return super.onSearchRequested();
    }

    public static final boolean isMediaKey(int keyCode) {
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
        Log.i(TAG, "onKeyDown");
        if (mAllAppGridView.getVisibility() == View.VISIBLE) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                mAllAppGridView.setVisibility(View.GONE);
                int itemCount = mList.getChildCount();
                int selectedPosition = mList.getSelectedPosition();
                ArrayList<HomeScreenRow> visibleRows = mHomeAdapter.getVisRowList();
                for (int i = 0; i < itemCount; ++i) {
                    View rowView = visibleRows.get(i).getRowView();
                    if (rowView instanceof ActiveFrame && i != selectedPosition) {
                        rowView.setVisibility(View.VISIBLE);
                    }
                }
                // Save the database again...
                if (mAllLaunchPoints != null && mAllLaunchPoints.size() > 0) {
                    List<LaunchPoint> selectLaunchPoints = new ArrayList<>();
                    for (LaunchPoint itemLaunchPoint : mAllLaunchPoints) {
                        if (itemLaunchPoint.isFavorited()) {
                            selectLaunchPoints.add(itemLaunchPoint);
                        }
                    }
                    Log.i(TAG, "selectLaunchPoints->size:" + selectLaunchPoints.size());
                    AppInfoService appInfoService = new AppInfoService();
                    appInfoService.deleteByType(ConstData.AppType.FAVORITE);
                    appInfoService.saveByLaunchPoints(selectLaunchPoints, ConstData.AppType.FAVORITE);
                }
                mHomeAdapter.refreshAdapterData();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                int numCols = mAllAppGridView.getNumColumns();
                int selectedPosition = mAllAppGridView.getSelectedItemPosition();
                if (selectedPosition / numCols == 0)
                    return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                int selectedPosition = mAllAppGridView.getSelectedItemPosition();
                if (selectedPosition == mAllAppGridView.getAdapter().getCount() - 1)
                    return true;
            }

        }
        if (this.mLaunchAnimation.isPrimed() || this.mLaunchAnimation.isRunning() || this.mEditModeAnimation.isPrimed() || this.mEditModeAnimation.isRunning()) {
            Log.i(TAG, "onKeyDown 1");
            switch (keyCode) {
                case android.support.v7.preference.R.styleable.Preference_android_layout /*3*/:
                case android.support.v7.preference.R.styleable.Preference_android_title /*4*/:
                    return super.onKeyDown(keyCode, event);
                default:
                    Log.i(TAG, "return true");
                    return true;
            }
        } else if (this.mShyMode || !isMediaKey(event.getKeyCode())) {
            Log.i(TAG, "onKeyDown 2");
            return super.onKeyDown(keyCode, event);
        } else {
            Log.i(TAG, "onKeyDown 3");
            Log.i(TAG, "return true");
            return true;
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && this.mAllAppGridView.getVisibility() != View.VISIBLE) {
            View selectItem = mList.getFocusedChild();

            if (selectItem instanceof ActiveFrame) {
                ActiveItemsRowView v = ((ActiveFrame) selectItem).mRow;
                View child = v.getCurView();

                if (child instanceof BannerView) {
                    BannerView banner = (BannerView) child;
                    AppsAdapter.AppViewHolder holder = banner.getViewHolder();
                    String pkg = holder.getPackageName();

                    Intent intent = new Intent(this, AppInfoActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("pkg", pkg);
                    intent.putExtras(bundle);

                    startActivity(intent);
                }

            }

            return true;
        }

        if (this.mShyMode || !isMediaKey(event.getKeyCode())) {
            return super.onKeyUp(keyCode, event);
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_HEADSETHOOK:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_STOP:
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                setShyMode(true, true);
                break;
        }
        return true;
    }

    private void addWidget(boolean refresh) {
        ViewGroup wrapper = (LinearLayout) findViewById(R.id.widget_wrapper);
        if (wrapper != null) {
            if (refresh || this.mAppWidgetHostView == null) {
                int childCount = wrapper.getChildCount();
                Log.i(TAG, "addWidget->childCount:" + childCount);
                wrapper.removeAllViews();

                int appWidgetId = Util.getWidgetId(this);

                clearWidget(appWidgetId);
                wrapper.addView(LayoutInflater.from(this).inflate(R.layout.clock, null));

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
            EditModeMassFadeAnimator.EditMode editMode2;
            if (editMode) {
                editMode2 = EditModeMassFadeAnimator.EditMode.ENTER;
            } else {
                editMode2 = EditModeMassFadeAnimator.EditMode.EXIT;
            }
            this.mEditModeAnimation.init(new EditModeMassFadeAnimator(this, editMode2), null, (byte) 0);
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
            getEditModeWallpaper().setVisibility(View.VISIBLE);
            this.mHomeAdapter.setRowAlphas(editMode ? 0 : 1);
        }
        if (!editMode && this.mAppEditMode) {
            for (int i = 0; i < this.mList.getChildCount(); i++) {
                View activeFrame = this.mList.getChildAt(i);
                if (activeFrame instanceof ActiveFrame) {
                    for (int j = 0; j < ((ActiveFrame) activeFrame).getChildCount(); j++) {
                        View activeItemsRow = ((ActiveFrame) activeFrame).getChildAt(j);
                        if (activeItemsRow instanceof ActiveItemsRowView) {
                            ((ActiveItemsRowView) activeItemsRow).setEditMode(false);
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
        boolean firstRun;
        Intent dummyIntent = new Intent("android.intent.category.LEANBACK_LAUNCHER");
        dummyIntent.setClass(this, DummyActivity.class);
        firstRun = PendingIntent.getActivity(this, 0, dummyIntent, 536870912) == null;
        if (firstRun) {
            ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 711573504, PendingIntent.getActivity(this, 0, dummyIntent, 0));
        }
        return firstRun;
    }

    public void beginLaunchAnimation(View view, boolean translucent, int color, Runnable onCompleteCallback) {
        if (!this.mLaunchAnimation.isPrimed() && !this.mLaunchAnimation.isRunning() && !this.mLaunchAnimation.isFinished()) {
            getBoundsOnScreen(view, this.mLaunchAnimation.lastKnownEpicenter);
            if (translucent) {
                onCompleteCallback.run();
            } else {
                ForwardingAnimatorSet animation;

                animation = new LauncherLaunchAnimator(this.mList, view, this.mLaunchAnimation.lastKnownEpicenter, (ImageView) findViewById(R.id.click_circle_layer), color, this.mHomeAdapter.getRowHeaders());

                this.mLaunchAnimation.init(animation, onCompleteCallback, (byte) 0);
                this.mLaunchAnimation.start();
            }
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

    public void setOnLaunchAnimationFinishedListener(AnimatorLifecycle.OnAnimationFinishedListener l) {
        this.mLaunchAnimation.setOnAnimationFinishedListener(l);
    }

    private void primeAnimationAfterLayout() {
        this.mList.getRootView().getViewTreeObserver().addOnGlobalLayoutListener(new C01689());
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

    public void preformIconMoreClick() {
        int itemCount = mList.getChildCount();
        int selectedPosition = mList.getSelectedPosition();
        ArrayList<HomeScreenRow> visibleRows = mHomeAdapter.getVisRowList();
        for (int i = 0; i < itemCount; ++i) {
            View rowView = visibleRows.get(i).getRowView();
            if (rowView instanceof ActiveFrame && i != selectedPosition) {
                rowView.setVisibility(View.GONE);

            }
        }
        mAllAppGridView.setVisibility(View.VISIBLE);
        mAllLaunchPoints = mLaunchPointListGenerator.getAllLaunchPoints();
        mAllAppGridAdapter = new AllAppGridAdapter(this, R.layout.adapter_item_allapp, mAllLaunchPoints);
        mAllAppGridView.setAdapter(mAllAppGridAdapter);
        mAllAppGridView.setOnItemClickListener(this);
        mAllAppGridView.setFocusable(true);
        mAllAppGridView.setFocusableInTouchMode(true);
        mAllAppGridView.requestFocus();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LaunchPoint selectItem = mAllAppGridAdapter.getItem(position);

        if (selectItem != null) {
            boolean isRecommend = selectItem.isFavorited();
            selectItem.setFavorite(!isRecommend);
            CheckBox selectBox = (CheckBox) view.findViewById(R.id.check_select);
            selectBox.setChecked(selectItem.isFavorited());
        }
    }

    @Override
    public void run() {
        if (mActivityState == 1) {
            int itemCount = mHomeAdapter.getItemCount();
            Log.i(TAG, "run->itemCount:" + itemCount);
            ArrayList<HomeScreenRow> visibleRows = mHomeAdapter.getVisRowList();
            for (int i = 0; i < itemCount; ++i) {
                Log.i(TAG, "run1");
                RecyclerView.Adapter<?> rowAdapter = visibleRows.get(i).getAdapter();
                if (rowAdapter instanceof AppsAdapter && ((AppsAdapter) rowAdapter).getAppType() == ConstData.AppType.ALL && rowAdapter.getItemCount() > 0) {
                    mLayoutLoading.setVisibility(View.GONE);
                    mList.setVisibility(View.VISIBLE);
                    mTimerHandler.removeCallbacks(this);
                    Log.i(TAG, "load app item end time:" + System.currentTimeMillis());
                    Log.i(TAG, "run2");
                    return;
                }
            }
            mTimerHandler.postDelayed(this, 100);
        } else {
            mTimerHandler.postDelayed(this, 100);
        }
    }
}
