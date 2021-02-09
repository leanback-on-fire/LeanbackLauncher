package com.amazon.tv.leanbacklauncher

import android.Manifest
import android.annotation.TargetApi
import android.app.*
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.content.*
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.media.tv.TvContract
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.OnHierarchyChangeListener
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.accessibility.AccessibilityManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.widget.OnChildViewHolderSelectedListener
import androidx.leanback.widget.VerticalGridView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.amazon.tv.firetv.leanbacklauncher.apps.AppInfoActivity
import com.amazon.tv.firetv.tvrecommendations.NotificationListenerMonitor
import com.amazon.tv.leanbacklauncher.animation.*
import com.amazon.tv.leanbacklauncher.animation.AnimatorLifecycle.OnAnimationFinishedListener
import com.amazon.tv.leanbacklauncher.animation.EditModeMassFadeAnimator.EditMode
import com.amazon.tv.leanbacklauncher.apps.AppsManager.Companion.getInstance
import com.amazon.tv.leanbacklauncher.apps.BannerView
import com.amazon.tv.leanbacklauncher.apps.OnEditModeChangedListener
import com.amazon.tv.leanbacklauncher.clock.ClockView
import com.amazon.tv.leanbacklauncher.logging.LeanbackLauncherEventLogger
import com.amazon.tv.leanbacklauncher.notifications.HomeScreenView
import com.amazon.tv.leanbacklauncher.notifications.NotificationCardView
import com.amazon.tv.leanbacklauncher.notifications.NotificationRowView
import com.amazon.tv.leanbacklauncher.notifications.NotificationRowView.NotificationRowListener
import com.amazon.tv.leanbacklauncher.notifications.NotificationsAdapter
import com.amazon.tv.leanbacklauncher.util.Partner
import com.amazon.tv.leanbacklauncher.util.TvSearchIconLoader
import com.amazon.tv.leanbacklauncher.util.TvSearchSuggestionsLoader
import com.amazon.tv.leanbacklauncher.util.Util
import com.amazon.tv.leanbacklauncher.wallpaper.LauncherWallpaper
import com.amazon.tv.leanbacklauncher.wallpaper.WallpaperInstaller
import com.amazon.tv.leanbacklauncher.widget.EditModeView
import com.amazon.tv.leanbacklauncher.widget.EditModeView.OnEditModeUninstallPressedListener
import java.io.FileDescriptor
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*

class MainActivity : Activity(), OnEditModeChangedListener, OnEditModeUninstallPressedListener {
    private var mAccessibilityManager: AccessibilityManager? = null
    var isInEditMode = false
        private set
    private var mAppWidgetHost: AppWidgetHost? = null
    private var mAppWidgetHostView: AppWidgetHostView? = null
    private var mAppWidgetManager: AppWidgetManager? = null
    private var mContentResolver: ContentResolver? = null
    private var mDelayFirstRecommendationsVisible = true
    private val mEditModeAnimation = AnimatorLifecycle()
    var editModeView: EditModeView? = null
        private set
    private var mEventLogger: LeanbackLauncherEventLogger? = null
    private var mFadeDismissAndSummonAnimations = false
    private val mHandler: Handler = MainActivityMessageHandler(this)

    private class MainActivityMessageHandler constructor(private val activity: MainActivity) : Handler() {
        override fun handleMessage(msg: Message) {
            var z = true
            when (msg.what) {
                1, 2 -> {
                    val mainActivity = activity
                    if (msg.what != 1) {
                        z = false
                    }
                    mainActivity.mIsIdle = z
                    var i = 0
                    while (i < activity.mIdleListeners.size) {
                        activity.mIdleListeners[i].onIdleStateChange(activity.mIsIdle)
                        i++
                    }
                    return
                }
                3 -> {
                    if (activity.mResetAfterIdleEnabled) {
                        activity.mKeepUiReset = true
                        activity.resetLauncherState(true)
                        return
                    }
                    return
                }
                4 -> {
                    activity.onNotificationRowStateUpdate(msg.arg1)
                    return
                }
                5 -> {
                    activity.homeAdapter!!.onUiVisible()
                    return
                }
                6 -> {
                    activity.addWidget(true)
                    return
                }
                7 -> {
                    activity.checkLaunchPointPositions()
                    return
                }
                else -> {
                }
            }
        }
    }

    var homeAdapter: HomeScreenAdapter? = null
        private set
    private var mHomeScreenView: HomeScreenView? = null
    private val mIdleListeners = ArrayList<IdleListener>()
    private var mIdlePeriod = 0
    private var mIsIdle = false
    private var mKeepUiReset = false
    private val mLaunchAnimation = AnimatorLifecycle()
    private var mList: VerticalGridView? = null
    private val mMoveTaskToBack = Runnable {
        if (!moveTaskToBack(true)) {
            mLaunchAnimation.reset()
        }
    }
    private val mNotifListener: NotificationRowListener = object : NotificationRowListener {
        private var mHandler: Handler? = null
        private val mSelectFirstRecommendationRunnable = Runnable {
            if (mNotificationsView!!.adapter != null && mNotificationsView!!.adapter!!.itemCount > 0) {
                mNotificationsView!!.setSelectedPositionSmooth(0)
            }
        }

        override fun onBackgroundImageChanged(imageUri: String?, signature: String?) {
            wallpaperView?.onBackgroundImageChanged(imageUri, signature)
        }

        override fun onSelectedRecommendationChanged(position: Int) {
            if (mKeepUiReset && !mAccessibilityManager!!.isEnabled && position > 0) {
                if (this.mHandler == null) {
                    this.mHandler = Handler()
                }
                this.mHandler!!.post(mSelectFirstRecommendationRunnable)
            }
        }
    }

    private var mNotificationsView: NotificationRowView? = null
    var mPackageReplacedReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val packageName = if (intent != null) intent.data else null
            if (packageName != null && packageName.toString().contains(context.packageName + ".recommendations")) {
                Log.d(TAG, "Recommendations Service updated, reconnecting")
                if (homeAdapter != null) {
                    homeAdapter!!.onReconnectToRecommendationsService()
                }
            }
        }
    }
    var mHomeRefreshReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getBooleanExtra("RefreshHome", false)) {
                if (BuildConfig.DEBUG) Log.d(TAG, "KILL HOME")
                finish()
            }
        }
    }
    private val mPauseAnimation = AnimatorLifecycle()
    private var mRecommendationsAdapter: NotificationsAdapter? = null
    private val mRefreshHomeAdapter = Runnable {
        if (homeAdapter != null) {
            homeAdapter!!.refreshAdapterData()
        }
    }
    private var mResetAfterIdleEnabled = false
    private var mResetPeriod = 0
    private var mScrollManager: HomeScrollManager? = null
    private val mSearchIconCallbacks: LoaderManager.LoaderCallbacks<Drawable> = object : LoaderManager.LoaderCallbacks<Drawable> {
        override fun onCreateLoader(id: Int, args: Bundle): Loader<Drawable> {
            return TvSearchIconLoader(this@MainActivity.applicationContext)
        }

        override fun onLoadFinished(loader: Loader<Drawable>, data: Drawable) {
            homeAdapter!!.onSearchIconUpdate(data)
        }

        override fun onLoaderReset(loader: Loader<Drawable>) {
            homeAdapter!!.onSearchIconUpdate(null)
        }
    }
    private val mSearchSuggestionsCallbacks: LoaderManager.LoaderCallbacks<Array<String>> = object : LoaderManager.LoaderCallbacks<Array<String>> {
        override fun onCreateLoader(id: Int, args: Bundle): Loader<Array<String>> {
            return TvSearchSuggestionsLoader(this@MainActivity.applicationContext)
        }

        override fun onLoadFinished(loader: Loader<Array<String>>, data: Array<String>) {
            homeAdapter!!.onSuggestionsUpdate(data)
        }

        override fun onLoaderReset(loader: Loader<Array<String>>) {
            homeAdapter!!.onSuggestionsUpdate(null)
        }
    }
    private var mShyMode = false
    private var mStartingEditMode = false
    private var mUninstallRequested = false
    private var mUserInteracted = false
    var wallpaperView: LauncherWallpaper? = null
        private set

    interface IdleListener {
        fun onIdleStateChange(z: Boolean)
        fun onVisibilityChange(z: Boolean)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val z: Boolean
        super.onCreate(savedInstanceState)
        mContentResolver = contentResolver
        if (mRecommendationsAdapter == null) {
            mRecommendationsAdapter = NotificationsAdapter(this)
        }
        val appContext = applicationContext
        setContentView(R.layout.activity_main)
        if (Partner.get(this).showLiveTvOnStartUp() && checkFirstRunAfterBoot()) {
            val tvIntent = Intent("android.intent.action.VIEW", TvContract.buildChannelUri(0))
            tvIntent.putExtra("com.google.android.leanbacklauncher.extra.TV_APP_ON_BOOT", true)
            if (packageManager.queryIntentActivities(tvIntent, 1).size > 0) {
                startActivity(tvIntent)
                finish()
            }
        }
        //android O fix bug orientation
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        //overlay permissions request on M+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName"))
                try {
                    startActivityForResult(intent, 0)
                } catch (e: Exception) {
                }
            }
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        MY_PERMISSIONS_REQUEST_LOCATION)
            } // TODO: onRequestPermissionsResult
        }
        mAccessibilityManager = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        editModeView = findViewById(R.id.edit_mode_view)
        editModeView?.setUninstallListener(this)
        wallpaperView = findViewById(R.id.background_container)
        mAppWidgetManager = AppWidgetManager.getInstance(appContext)
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_APP_WIDGETS))
            mAppWidgetHost = AppWidgetHost(this, 123)
        mList = findViewById(R.id.main_list_view)
        mList?.let { mlv ->
            mlv.setHasFixedSize(true)
            mlv.windowAlignment = 1
            mlv.windowAlignmentOffset = resources.getDimensionPixelOffset(R.dimen.home_screen_selected_row_alignment)
            mlv.windowAlignmentOffsetPercent = -1.0f
            mlv.itemAlignmentOffset = 0
            mlv.itemAlignmentOffsetPercent = -1.0f
            mScrollManager = HomeScrollManager(this, mlv)
            mScrollManager!!.addHomeScrollListener(wallpaperView!!)

            homeAdapter = HomeScreenAdapter(this, mScrollManager, mRecommendationsAdapter, editModeView)
            homeAdapter!!.setOnEditModeChangedListener(this)
            mlv.setItemViewCacheSize(homeAdapter!!.itemCount)
            mlv.adapter = homeAdapter
            mlv.setOnChildViewHolderSelectedListener(object : OnChildViewHolderSelectedListener() {
                override fun onChildViewHolderSelected(parent: RecyclerView, child: RecyclerView.ViewHolder, position: Int, subposition: Int) {
                    homeAdapter!!.onChildViewHolderSelected(parent, child, position)
                }
            })
            mlv.setAnimateChildLayout(false)
            val notifIndex = homeAdapter!!.getRowIndex(1) // RowType.NOTIFICATIONS
            if (notifIndex != -1) {
                mlv.selectedPosition = notifIndex
            }
            val recAdapter = homeAdapter!!.recommendationsAdapter
            addIdleListener(recAdapter)
            mlv.setOnHierarchyChangeListener(object : OnHierarchyChangeListener {
                override fun onChildViewAdded(parent: View, child: View) {
                    var tag = 0
                    if (child.tag is Int) {
                        tag = child.tag as Int
                    }
                    when (tag) {
                        0 -> {
                            if (child is SearchOrbView) {
                                child.setLaunchListener { setShyMode(true, true) }
                            }
                            addWidget(false)
                        }
                        1, 2 -> {
                            mHomeScreenView = child.findViewById(R.id.home_screen_messaging)
                            mHomeScreenView?.let {
                                val homeScreenMessaging = it.homeScreenMessaging
                                if (tag == 1) {
                                    recAdapter?.setNotificationRowViewFlipper(homeScreenMessaging)
                                    mNotificationsView = it.notificationRow
                                    mNotificationsView?.setListener(mNotifListener)
                                }
                                homeScreenMessaging.setListener { state ->
                                    mHandler.sendMessageDelayed(mHandler.obtainMessage(4, state, 0), 500)
                                    if (state == 0 && mDelayFirstRecommendationsVisible) {
                                        mDelayFirstRecommendationsVisible = false
                                        mHandler.sendEmptyMessageDelayed(5, 1500)
                                    }
                                }
                            }
                        }
                    }
                    if (child is IdleListener && !mIdleListeners.contains(child)) {
                        addIdleListener(child as IdleListener)
                    }
                }

                override fun onChildViewRemoved(parent: View, child: View) {}
            })
            mlv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    mScrollManager!!.onScrolled(dy, currentScrollPos)
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    mScrollManager!!.onScrollStateChanged(newState)
                }
            })
        }
        mShyMode = true
        z = !mShyMode
        setShyMode(z, true)
        if (BuildConfig.DEBUG) Log.d(TAG, "OnCreate setShyMode($z, true), init mShyMode=$mShyMode")
        mIdlePeriod = resources.getInteger(R.integer.idle_period)
        mResetPeriod = resources.getInteger(R.integer.reset_period)
        mFadeDismissAndSummonAnimations = resources.getBoolean(R.bool.app_launch_animation_fade)
        mKeepUiReset = true
        homeAdapter!!.onInitUi()
        mEventLogger = LeanbackLauncherEventLogger.getInstance(appContext)
        val filter = IntentFilter()
        filter.addAction("android.intent.action.PACKAGE_REPLACED")
        filter.addAction("android.intent.action.PACKAGE_ADDED")
        filter.addDataScheme("package")
        registerReceiver(mPackageReplacedReceiver, filter)
        // regiser RefreshHome broadcast
        val filterHome = IntentFilter(this.javaClass.name) // ACTION com.amazon.tv.leanbacklauncher.MainActivity
        registerReceiver(mHomeRefreshReceiver, filterHome)
//        loaderManager.initLoader(0, null, mSearchIconCallbacks)
//        loaderManager.initLoader(1, null, mSearchSuggestionsCallbacks)
        // start notification listener
        val pref = PreferenceManager.getDefaultSharedPreferences(appContext)
        if (pref.getBoolean(appContext.getString(R.string.pref_enable_recommendations_row), false))
            startService(Intent(this, NotificationListenerMonitor::class.java))
    }

    public override fun onDestroy() {
        mHandler.removeMessages(3)
        super.onDestroy()
        if (homeAdapter != null) {
            homeAdapter!!.onStopUi()
            homeAdapter!!.unregisterReceivers()
        }
        getInstance(applicationContext)!!.onDestroy()
        unregisterReceiver(mPackageReplacedReceiver)
        unregisterReceiver(mHomeRefreshReceiver)
    }

    override fun onUserInteraction() {
        mHandler.removeMessages(3)
        mKeepUiReset = false
        if (hasWindowFocus()) {
            mHandler.removeMessages(1)
            mUserInteracted = true
            if (mIsIdle) {
                mHandler.sendEmptyMessage(2)
            }
            mHandler.sendEmptyMessageDelayed(1, mIdlePeriod.toLong())
        }
        mHandler.sendEmptyMessageDelayed(3, mResetPeriod.toLong())
    }

    private fun addIdleListener(listener: IdleListener) {
        mIdleListeners.add(listener)
        listener.onVisibilityChange(true)
        listener.onIdleStateChange(mIsIdle)
    }

    override fun onBackPressed() {
        if (isInEditMode) {
            editModeView!!.onBackPressed()
        } else if (mLaunchAnimation.isRunning) {
            mLaunchAnimation.cancel()
        } else if (mLaunchAnimation.isPrimed) {
            mLaunchAnimation.reset()
        } else {
            if (mLaunchAnimation.isFinished) {
                mLaunchAnimation.reset()
            }
            dismissLauncher()
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun onBackgroundVisibleBehindChanged(visible: Boolean) {
        setShyMode(!visible, true)
    }

    override fun onEditModeChanged(editMode: Boolean) {
        if (isInEditMode == editMode) {
            return
        }
        if (mAccessibilityManager!!.isEnabled) {
            setEditMode(editMode, false)
        } else {
            setEditMode(editMode, true)
        }
    }

    override fun onUninstallPressed(packageName: String) {
        if (packageName != null && !mUninstallRequested) {
            mUninstallRequested = true
            val uninstallIntent = Intent("android.intent.action.UNINSTALL_PACKAGE", Uri.parse("package:$packageName"))
            uninstallIntent.putExtra("android.intent.extra.RETURN_RESULT", true)
            startActivityForResult(uninstallIntent, 321)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 321 && resultCode != 0) {
            if (resultCode == -1) {
                editModeView!!.uninstallComplete()
            } else if (resultCode == 1) {
                editModeView!!.uninstallFailure()
            }
        }
    }

    private fun setShyMode(shy: Boolean, changeWallpaper: Boolean): Boolean {
        var changed = false
        if (mShyMode != shy) {
            mShyMode = shy
            changed = true
            if (mShyMode) {
                if (BuildConfig.DEBUG) Log.d(TAG, "setShyMode: convertFromTranslucent, mShyMode=$mShyMode")
                convertFromTranslucent()
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    if (BuildConfig.DEBUG) Log.d(TAG, "setShyMode: convertToTranslucent, mShyMode=$mShyMode")
                    convertToTranslucent() // convertToTranslucent(null, null);
                }
            }
        }
        if (changeWallpaper && wallpaperView!!.shynessMode != shy) {
            wallpaperView!!.shynessMode = mShyMode
            if (mShyMode && mNotificationsView != null) {
                mNotificationsView!!.refreshSelectedBackground()
            }
        }
        return changed
    }

    private fun convertFromTranslucent() {
//        if (this@MainActivity.isTaskRoot) return // Avoid Android 9.x back to Home bug
        try {
            val convertFromTranslucent = Activity::class.java.getDeclaredMethod("convertFromTranslucent")
            convertFromTranslucent.isAccessible = true
            convertFromTranslucent.invoke(this@MainActivity)
        } catch (ignored: Throwable) {
        }
    }

    private fun convertToTranslucent() {
        try {
            var translucentConversionListenerClazz: Class<*>? = null
            for (clazz in Activity::class.java.declaredClasses) {
                if (clazz.simpleName.contains("TranslucentConversionListener")) {
                    translucentConversionListenerClazz = clazz
                }
            }
            val convertToTranslucent = Activity::class.java.getDeclaredMethod("convertToTranslucent", translucentConversionListenerClazz, ActivityOptions::class.java)
            convertToTranslucent.isAccessible = true
            convertToTranslucent.invoke(this@MainActivity, null, null)
        } catch (ignored: Throwable) {
        }
    }

    private fun dismissLauncher(): Boolean {
        if (mShyMode) {
            return false
        }
        mLaunchAnimation.init(LauncherDismissAnimator(mList, mFadeDismissAndSummonAnimations, homeAdapter!!.rowHeaders), mMoveTaskToBack, 0.toByte())
        mLaunchAnimation.start()
        return true
    }

    public override fun onNewIntent(intent: Intent) {
        var exitingEditMode = false
        if (isInEditMode) {
            if (Util.isInTouchExploration(applicationContext)) {
                setTitle(R.string.app_label)
            }
            setEditMode(false, true)
            exitingEditMode = true
        }
        if (mLaunchAnimation.isRunning) {
            mLaunchAnimation.cancel()
            return
        }
        if (mLaunchAnimation.isPrimed) {
            mLaunchAnimation.reset()
        }
        if (mLaunchAnimation.isFinished) {
            mLaunchAnimation.reset()
        }
        if (!exitingEditMode) {
            if (intent.extras != null) {
                if (intent.extras!!.getBoolean("extra_start_customize_apps")) {
                    startEditMode(3)
                } else if (intent.extras!!.getBoolean("extra_start_customize_games")) {
                    startEditMode(4)
                }
            }
            if (!mStartingEditMode) {
                if (!hasWindowFocus() || intent.getBooleanExtra("com.android.systemui.recents.tv.RecentsTvActivity.RECENTS_HOME_INTENT_EXTRA", false)) {
                    if (!mLaunchAnimation.isScheduled) {
                        resetLauncherState(false)
                        mLaunchAnimation
                                .init(MassSlideAnimator.Builder(mList)
                                        .setDirection(MassSlideAnimator.Direction.SLIDE_IN)
                                        .setFade(mFadeDismissAndSummonAnimations)
                                        .build(), mRefreshHomeAdapter, 32.toByte())
                    }
                } else if (!dismissLauncher()) {
                    resetLauncherState(true)
                }
            }
        } else if (!mLaunchAnimation.isInitialized && !mLaunchAnimation.isScheduled) {
            resetLauncherState(false)
            mLaunchAnimation.init(MassSlideAnimator.Builder(mList).setDirection(MassSlideAnimator.Direction.SLIDE_IN).setFade(mFadeDismissAndSummonAnimations).build(), mRefreshHomeAdapter, 32.toByte())
        }
    }

    protected fun startEditMode(rowType: Int) {
        if (Util.isInTouchExploration(applicationContext)) {
            setTitle(if (rowType == 3) R.string.title_app_edit_mode else R.string.title_game_edit_mode)
        }
        mStartingEditMode = true
        homeAdapter!!.resetRowPositions(false)
        mLaunchAnimation.cancel()
        mList!!.selectedPosition = homeAdapter!!.getRowIndex(rowType)
        homeAdapter!!.prepareEditMode(rowType)
    }

    private fun resetLauncherState(smooth: Boolean) {
        if (BuildConfig.DEBUG) Log.d("resetLauncherState", "smooth: $smooth")
        mScrollManager!!.onScrolled(0, 0)
        mUserInteracted = false
        homeAdapter!!.resetRowPositions(smooth)
        if (isInEditMode) {
            val z: Boolean
            z = smooth
            setEditMode(false, z)
        }
        var notifIndex = homeAdapter!!.getRowIndex(1)
        if (mList!!.adapter != null) {
            notifIndex = Math.min(mList!!.adapter!!.itemCount - 1, notifIndex)
        }
        if (!(notifIndex == -1 || mList!!.selectedPosition == notifIndex)) {
            if (smooth) {
                mList!!.setSelectedPositionSmooth(notifIndex)
            } else {
                mList!!.selectedPosition = notifIndex
                val focusedChild = mList!!.focusedChild
                val focusedPosition = mList!!.getChildAdapterPosition(focusedChild!!)
                if (!(focusedChild == null || focusedPosition == notifIndex)) {
                    focusedChild.clearFocus()
                }
            }
            if (!(mShyMode || mNotificationsView == null)) {
                mNotificationsView!!.setIgnoreNextActivateBackgroundChange()
            }
        } else { // focus on 1st Apps cat || Search (0)
            val ar = intArrayOf(0) // 0, 4, 8, 9, 7 - SEARCH, GAMES, MUSIC, VIDEO, FAVORITES as in buildRowList()
            var i: Int
            var x: Int
            i = 0
            while (i < ar.size) {
                x = ar[i]
                val appIndex = homeAdapter!!.getRowIndex(x)
                if (!(appIndex == -1 || mList!!.selectedPosition == appIndex)) {
                    if (BuildConfig.DEBUG) Log.d("resetLauncherState", "set focus to $appIndex")
                    //if (smooth) {
                    mList!!.setSelectedPositionSmooth(appIndex)
                    //} else {
                    mList!!.selectedPosition = appIndex
                    //}
                    //this.mList.getChildAt(0).requestFocus();
                }
                i++
            }
        }
        mLaunchAnimation.cancel()
    }

    private val isBackgroundVisibleBehind: Boolean
        private get() {
            try {
                val isBackgroundVisibleBehind = Activity::class.java.getDeclaredMethod("isBackgroundVisibleBehind")
                isBackgroundVisibleBehind.isAccessible = true
                val result = isBackgroundVisibleBehind.invoke(this@MainActivity) as Boolean
                return result
            } catch (ignored: Throwable) {
            }
            return false
        }

    override fun onStart() {
        var z = true
        super.onStart()
        mResetAfterIdleEnabled = false
        mAppWidgetHost?.startListening()
        if (isBackgroundVisibleBehind) {
            if (BuildConfig.DEBUG) Log.d(TAG, "onStart: BackgroundVisibleBehind")
            z = false
        }
        setShyMode(z, true)
        if (BuildConfig.DEBUG) Log.d(TAG, "onStart: setShyMode($z, true)")
        wallpaperView!!.resetBackground()
        homeAdapter!!.refreshAdapterData()
        if (mKeepUiReset) {
            resetLauncherState(false)
        }
        if (!mStartingEditMode) {
            if (!mLaunchAnimation.isInitialized) {
                mLaunchAnimation.init(LauncherReturnAnimator(mList, mLaunchAnimation.lastKnownEpicenter, homeAdapter!!.rowHeaders, mHomeScreenView), mRefreshHomeAdapter, 32.toByte())
            }
            mLaunchAnimation.schedule<LauncherReturnAnimator>()
        }
        mStartingEditMode = false
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {}

    override fun onSaveInstanceState(savedInstanceState: Bundle) {}

    override fun onResume() {
        var forceResort = true
        var z = true
        super.onResume()
        if (isBackgroundVisibleBehind) {
            if (BuildConfig.DEBUG) Log.d(TAG, "onResume: BackgroundVisibleBehind")
            z = false
        }
        val shyChanged = setShyMode(z, true)
        if (BuildConfig.DEBUG) Log.d(TAG, "onResume: setShyMode($z, true) shyChanged = $shyChanged")
        if (!getInstance(applicationContext)!!.checkIfResortingIsNeeded() || isInEditMode) {
            forceResort = false
        }
        homeAdapter!!.sortRowsIfNeeded(forceResort)
        WallpaperInstaller.getInstance(this).installWallpaperIfNeeded()
        wallpaperView!!.shynessMode = mShyMode
        if (shyChanged) {
            wallpaperView!!.resetBackground()
        }
        if (mShyMode && mNotificationsView != null) {
            mNotificationsView!!.refreshSelectedBackground()
        }
        if (!mHandler.hasMessages(6)) {
            mHandler.sendEmptyMessage(6)
        }
        if (homeAdapter != null) {
            homeAdapter!!.animateSearchIn()
        }
        for (i in mIdleListeners.indices) {
            mIdleListeners[i].onVisibilityChange(true)
        }
        mResetAfterIdleEnabled = true
        mHandler.removeMessages(3)
        mHandler.removeMessages(1)
        if (mIsIdle) {
            mHandler.sendEmptyMessage(2)
        } else {
            mHandler.sendEmptyMessageDelayed(1, mIdlePeriod.toLong())
        }
        mHandler.sendEmptyMessageDelayed(3, mResetPeriod.toLong())
        mHandler.sendEmptyMessageDelayed(7, 2000)
        if (mLaunchAnimation.isFinished) {
            mLaunchAnimation.reset()
        }
        if (mLaunchAnimation.isInitialized) {
            mLaunchAnimation.reset()
        }
        if (mLaunchAnimation.isScheduled) {
            primeAnimationAfterLayout()
        }
        mPauseAnimation.reset()
        if (isInEditMode) {
            mEditModeAnimation.init(EditModeMassFadeAnimator(this, EditMode.ENTER), null, 0.toByte())
            mEditModeAnimation.start()
        }
        mUninstallRequested = false
        overridePendingTransition(R.anim.home_fade_in_top, R.anim.home_fade_out_bottom)
        if (!(homeAdapter == null || homeAdapter!!.isUiVisible || mDelayFirstRecommendationsVisible)) {
            homeAdapter!!.onUiVisible()
        }
    }

    override fun onEnterAnimationComplete() {
        if (mLaunchAnimation.isScheduled || mLaunchAnimation.isPrimed) {
            mLaunchAnimation.start()
        }
    }

    override fun onPause() {
        super.onPause()
        mResetAfterIdleEnabled = false
        mLaunchAnimation.cancel()
        mHandler.removeMessages(1)
        mHandler.removeMessages(6)
        mHandler.removeMessages(7)
        for (i in mIdleListeners.indices) {
            mIdleListeners[i].onVisibilityChange(false)
        }
        if (isInEditMode) {
            mEditModeAnimation.init(EditModeMassFadeAnimator(this, EditMode.EXIT), null, 0.toByte())
            mEditModeAnimation.start()
        }
        mPauseAnimation.init(LauncherPauseAnimator(mList), null, 0.toByte())
        mPauseAnimation.start()
        if (homeAdapter != null && homeAdapter!!.isUiVisible) {
            homeAdapter!!.onUiInvisible()
            mDelayFirstRecommendationsVisible = false
        }
    }

    override fun onStop() {
        mResetAfterIdleEnabled = true
        mAppWidgetHost?.stopListening()
        mHandler.removeCallbacksAndMessages(null)
        mHandler.sendEmptyMessageDelayed(3, mResetPeriod.toLong())
        if (isInEditMode) {
            setEditMode(false, false)
        }
        setShyMode(false, false)
        if (BuildConfig.DEBUG) Log.d(TAG, "onStop: setShyMode(false, false)")
        homeAdapter!!.sortRowsIfNeeded(false)
        mLaunchAnimation.reset()
        super.onStop()
    }

    override fun dump(prefix: String, fd: FileDescriptor?, writer: PrintWriter, args: Array<String>?) {
        super.dump(prefix, fd, writer, args)
        getInstance(applicationContext)!!.dump(prefix, writer)
        mLaunchAnimation.dump(prefix, writer, mList)
        homeAdapter!!.dump(prefix, writer)
    }

    private val currentScrollPos: Int
        private get() {
            var pos = 0
            var topView = -1
            var i = 0
            while (i < mList!!.childCount) {
                val v = mList!!.getChildAt(i)
                if (v == null || v.top > 0) {
                    i++
                } else {
                    topView = mList!!.getChildAdapterPosition(v)
                    if (v.measuredHeight > 0) {
                        pos = (homeAdapter!!.getScrollOffset(topView).toFloat() * (Math.abs(v.top).toFloat() / v.measuredHeight.toFloat()) * -1.0f).toInt()
                    }
                    topView--
                    while (topView >= 0) {
                        pos -= homeAdapter!!.getScrollOffset(topView)
                        topView--
                    }
                    return pos
                }
            }
            topView--
            while (topView >= 0) {
                pos -= homeAdapter!!.getScrollOffset(topView)
                topView--
            }
            return pos
        }

    private fun onNotificationRowStateUpdate(state: Int) {
        Log.d("*****", "onNotificationRowStateUpdate(" + state + ") selected postion: " + mList!!.selectedPosition)
        if (state == 1 || state == 2) {
            if (!mUserInteracted) {
                val searchIndex = homeAdapter!!.getRowIndex(0)
                if (searchIndex != -1) {
                    // focus on Search
                    mList!!.selectedPosition = searchIndex
                    mList!!.getChildAt(searchIndex).requestFocus()
                }
            }
        } else if (state == 0 && mList!!.selectedPosition <= homeAdapter!!.getRowIndex(1) && mNotificationsView!!.childCount > 0) {
            // focus on Recomendations
            mNotificationsView!!.selectedPosition = 0
            mNotificationsView!!.getChildAt(0).requestFocus()
        }
    }

    override fun onSearchRequested(): Boolean {
        setShyMode(true, true)
        return super.onSearchRequested()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (mLaunchAnimation.isPrimed || mLaunchAnimation.isRunning || mEditModeAnimation.isPrimed || mEditModeAnimation.isRunning) {
            when (keyCode) {
                KeyEvent.KEYCODE_HOME, KeyEvent.KEYCODE_BACK -> super.onKeyDown(keyCode, event)
                else -> true
            }
        } else if (mShyMode || !isMediaKey(event.keyCode)) {
            super.onKeyDown(keyCode, event)
        } else {
            true
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_INFO) {
            val selectItem = mList!!.focusedChild
            if (selectItem is ActiveFrame) {
                val v = selectItem.mRow
                val child = v?.focusedChild // TODO
                if (child is BannerView) {
                    val holder = child.viewHolder
                    if (holder != null) { // holder == null when the holder is an input
                        val pkg = holder.packageName
                        val intent = Intent(this, AppInfoActivity::class.java)
                        val bundle = Bundle()
                        bundle.putString("pkg", pkg)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                }
            }
            return true
        }
        return if (mShyMode || !isMediaKey(event.keyCode)) {
            super.onKeyUp(keyCode, event)
        } else when (keyCode) {
            KeyEvent.KEYCODE_HEADSETHOOK, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, KeyEvent.KEYCODE_MEDIA_STOP, KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                setShyMode(true, true)
                true
            }
            else -> true
        }
    }

    private fun addWidget(refresh: Boolean) {
        val wrapper: ViewGroup? = findViewById<View>(R.id.widget_wrapper) as? LinearLayout?
        wrapper?.let { ww ->
            if (refresh || mAppWidgetHostView == null) {
                ww.removeAllViews()
                var success = false
                var appWidgetId = Util.getWidgetId(this)
                val appWidgetComp = Partner.get(this).widgetComponentName
                if (appWidgetComp != null) {
                    for (appWidgetInfo in mAppWidgetManager!!.installedProviders) {
                        if (appWidgetComp == appWidgetInfo.provider) {
                            success = appWidgetId != 0
                            if (success && appWidgetComp != Util.getWidgetComponentName(this)) {
                                clearWidget(appWidgetId)
                                success = false
                            }
                            if (!success) {
                                val width = resources.getDimension(R.dimen.widget_width).toInt()
                                val height = resources.getDimension(R.dimen.widget_height).toInt()
                                val options = Bundle()
                                options.putInt("appWidgetMinWidth", width)
                                options.putInt("appWidgetMaxWidth", width)
                                options.putInt("appWidgetMinHeight", height)
                                options.putInt("appWidgetMaxHeight", height)
                                appWidgetId = mAppWidgetHost!!.allocateAppWidgetId()
                                success = mAppWidgetManager!!.bindAppWidgetIdIfAllowed(appWidgetId, appWidgetInfo.provider, options)
                            }
                            if (success) {
                                mAppWidgetHostView = mAppWidgetHost!!.createView(this, appWidgetId, appWidgetInfo)
                                mAppWidgetHostView?.setAppWidget(appWidgetId, appWidgetInfo)
                                ww.addView(mAppWidgetHostView)
                                Util.setWidget(this, appWidgetId, appWidgetInfo.provider)
                            }
                        }
                    }
                }
                if (!success) {
                    clearWidget(appWidgetId)
                    ww.addView(LayoutInflater.from(this).inflate(R.layout.clock, ww, false))
                    val typeface = ResourcesCompat.getFont(this, R.font.sfuidisplay_thin)
                    val clockview: TextView = findViewById<View>(R.id.clock) as ClockView
                    if (clockview != null && typeface != null) clockview.typeface = typeface
                    return
                }
                return
            }
            val parent = mAppWidgetHostView!!.parent as ViewGroup
            if (parent !== ww) {
                parent.removeView(mAppWidgetHostView)
                ww.removeAllViews()
                ww.addView(mAppWidgetHostView)
            }
        }
    }

    private fun clearWidget(appWidgetId: Int) {
        if (appWidgetId != 0) {
            mAppWidgetHost?.deleteAppWidgetId(appWidgetId)
        }
        Util.clearWidget(this)
    }

    val editModeWallpaper: View
        get() = findViewById(R.id.edit_mode_background)

    private fun setEditMode(editMode: Boolean, useAnimation: Boolean) {
        var f = 1.0f
        mEditModeAnimation.reset()
        if (useAnimation) {
            mEditModeAnimation.init(EditModeMassFadeAnimator(this, if (editMode) EditMode.ENTER else EditMode.EXIT), null, 0.toByte())
            mEditModeAnimation.start()
        } else {
            val f2: Float
            val launcherWallpaper = wallpaperView
            f2 = if (editMode) {
                0.0f
            } else {
                1.0f
            }
            launcherWallpaper!!.alpha = f2
            val editModeWallpaper = editModeWallpaper
            if (!editMode) {
                f = 0.0f
            }
            editModeWallpaper.alpha = f
            editModeWallpaper.visibility = View.VISIBLE
            homeAdapter!!.setRowAlphas(if (editMode) 0 else 1)
        }
        if (!editMode && isInEditMode) {
            for (i in 0 until mList!!.childCount) {
                val activeFrame = mList!!.getChildAt(i)
                if (activeFrame is ActiveFrame) {
                    for (j in 0 until activeFrame.childCount) {
                        val activeItemsRow = activeFrame.getChildAt(j)
                        if (activeItemsRow is EditableAppsRowView) {
                            activeItemsRow.editMode = false
                        }
                    }
                }
            }
        }
        isInEditMode = editMode
    }

    private fun checkFirstRunAfterBoot(): Boolean {
        val dummyIntent = Intent("android.intent.category.LEANBACK_LAUNCHER")
        dummyIntent.setClass(this, DummyActivity::class.java)
        val firstRun = PendingIntent.getActivity(this, 0, dummyIntent, 536870912) == null
        if (firstRun) {
            (getSystemService(ALARM_SERVICE) as AlarmManager)[AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 864000000000L] = PendingIntent.getActivity(this, 0, dummyIntent, 0)
        }
        return firstRun
    }

    fun beginLaunchAnimation(view: View, translucent: Boolean, color: Int, onCompleteCallback: Runnable) {
        if (!mLaunchAnimation.isPrimed && !mLaunchAnimation.isRunning && !mLaunchAnimation.isFinished) {
            getBoundsOnScreen(view, mLaunchAnimation.lastKnownEpicenter)
            if (translucent) {
                onCompleteCallback.run()
                return
            }
            val animation: ForwardingAnimatorSet
            animation = if (view is NotificationCardView) {
                NotificationLaunchAnimator(mList, view, mLaunchAnimation.lastKnownEpicenter, findViewById<View>(R.id.click_circle_layer) as ImageView, color, homeAdapter!!.rowHeaders, mHomeScreenView)
            } else {
                LauncherLaunchAnimator(mList, view, mLaunchAnimation.lastKnownEpicenter, findViewById<View>(R.id.click_circle_layer) as ImageView, color, homeAdapter!!.rowHeaders, mHomeScreenView)
            }
            mLaunchAnimation.init(animation, onCompleteCallback, 0.toByte())
            mLaunchAnimation.start()
        }
    }

    val isLaunchAnimationInProgress: Boolean
        get() = mLaunchAnimation.isPrimed || mLaunchAnimation.isRunning
    val isEditAnimationInProgress: Boolean
        get() = mEditModeAnimation.isPrimed || mEditModeAnimation.isRunning

    fun includeInLaunchAnimation(target: View?) {
        mLaunchAnimation.include(target)
    }

    fun includeInEditAnimation(target: View?) {
        mEditModeAnimation.include(target)
    }

    fun excludeFromLaunchAnimation(target: View?) {
        mLaunchAnimation.exclude(target)
    }

    fun excludeFromEditAnimation(target: View?) {
        mEditModeAnimation.exclude(target)
    }

    fun setOnLaunchAnimationFinishedListener(l: OnAnimationFinishedListener?) {
        mLaunchAnimation.setOnAnimationFinishedListener(l)
    }

    private fun primeAnimationAfterLayout() {
        mList!!.rootView.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mList!!.rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                if (mLaunchAnimation.isScheduled) {
                    mLaunchAnimation.prime()
                }
            }
        })
        mList!!.requestLayout()
    }

    private fun checkLaunchPointPositions() {
        if (!mLaunchAnimation.isRunning && checkViewHierarchy(mList)) {
            val buf = StringWriter()
            buf.append("Caught partially animated state; resetting...\n")
            mLaunchAnimation.dump("", PrintWriter(buf), mList)
            Log.w("Animations", buf.toString())
            mLaunchAnimation.reset()
        }
    }

    private fun checkViewHierarchy(view: View?): Boolean {
        if (view is ParticipatesInLaunchAnimation && view.translationY != 0.0f) {
            return true
        }
        if (view is ViewGroup) {
            val group = view
            val n = group.childCount
            for (i in 0 until n) {
                if (checkViewHierarchy(group.getChildAt(i))) {
                    return true
                }
            }
        }
        return false
    }

    companion object {
        const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val TAG = "MainActivity"
        fun isMediaKey(keyCode: Int): Boolean {
            return when (keyCode) {
                KeyEvent.KEYCODE_HEADSETHOOK, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, KeyEvent.KEYCODE_MEDIA_STOP, KeyEvent.KEYCODE_MEDIA_NEXT, KeyEvent.KEYCODE_MEDIA_PREVIOUS, KeyEvent.KEYCODE_MEDIA_REWIND, KeyEvent.KEYCODE_MEDIA_FAST_FORWARD, KeyEvent.KEYCODE_MUTE, KeyEvent.KEYCODE_MEDIA_PLAY, KeyEvent.KEYCODE_MEDIA_PAUSE, KeyEvent.KEYCODE_MEDIA_RECORD -> true
                else -> false
            }
        }

        private fun getBoundsOnScreen(v: View, epicenter: Rect) {
            val location = IntArray(2)
            v.getLocationOnScreen(location)
            epicenter.left = location[0]
            epicenter.top = location[1]
            epicenter.right = epicenter.left + Math.round(v.width.toFloat() * v.scaleX)
            epicenter.bottom = epicenter.top + Math.round(v.height.toFloat() * v.scaleY)
        }
    }
}