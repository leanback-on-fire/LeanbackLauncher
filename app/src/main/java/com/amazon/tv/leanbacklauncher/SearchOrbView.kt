package com.amazon.tv.leanbacklauncher

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnFocusChangeListener
import android.view.accessibility.AccessibilityManager
import android.widget.FrameLayout
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.widget.SearchOrbView
import com.amazon.tv.leanbacklauncher.MainActivity.IdleListener
import com.amazon.tv.leanbacklauncher.apps.AppsManager.SearchPackageChangeListener
import com.amazon.tv.leanbacklauncher.util.Partner
import com.amazon.tv.leanbacklauncher.util.Util.isConfirmKey
import com.amazon.tv.leanbacklauncher.util.Util.playErrorSound
import com.amazon.tv.leanbacklauncher.util.Util.searchIntent
import com.amazon.tv.leanbacklauncher.util.Util.startSearchActivitySafely
import java.util.*

@Suppress("DEPRECATION")
class SearchOrbView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs),
    IdleListener, SearchPackageChangeListener {
    private var mAssistantIcon: Drawable? = null
    private var mClickDeviceId = -1
    private var mColorBright = 0
    private var mColorDim = 0
    private val mContext: Context
    private var mCurrentIndex = 0
    private val mDefaultColorMicIcon: Drawable?
    private val mDefaultTextToShow: Array<String>
    private var mEatDpadCenterKeyDown = false
    private val mFocusedColor: Int
    private val mFocusedKeyboardText: String
    private val mFocusedMicText: String?
    private val mFocusedText: String?
    private val mHandler = Handler()
    private val mIdleTextFlipDelay: Int
    private val mIsHintFlippingAllowed: Boolean
    private var mKeyboardContainer: FrameLayout? = null
    private var mKeyboardFocusedIcon: Drawable? = null
    private val mKeyboardOrbAnimationDuration: Int
    private var mKeyboardOrbProgress = 0.0f
    private var mKeyboardOrbView: SearchOrbView? = null
    private var mKeyboardUnfocusedIcon: Drawable? = null
    private val mLaunchFadeDuration: Int
    private var mListener: SearchLaunchListener? = null
    private var mMicOrbView: SearchOrbView? = null
    private val mMicUnfocusedIcon: Drawable?
    private var mOrbAnimation: ObjectAnimator? = null
    private val mSearchHintText: String?
    private val mSearchIntent = searchIntent
    private val mSearchOrbsSpacing: Int
    private var mSwitchRunnable: Runnable? = null
    private var mSwitcher: TextSwitcher? = null
    private var mTextToShow: Array<String> = emptyArray()
    private val mUnfocusedColor: Int
    private var mWahlbergUx: Boolean
    private var mWidgetView: View? = null
    private val kantnissPackageID = "com.google.android.katniss"

    interface SearchLaunchListener {
        fun onSearchLaunched()
    }

    init {
        var z = false
        mContext = context
        val res = context.resources
        mDefaultTextToShow = res.getStringArray(R.array.search_orb_text_to_show)
        mIdleTextFlipDelay = res.getInteger(R.integer.search_orb_idle_hint_flip_delay)
        mLaunchFadeDuration = res.getInteger(R.integer.search_orb_text_fade_duration)
        mSearchHintText = fixItalics(context.getString(R.string.search_hint_text))
        mFocusedText = fixItalics(context.getString(R.string.focused_search_hint_text))
        mFocusedMicText = fixItalics(context.getString(R.string.focused_search_mic_hint_text))
        mFocusedKeyboardText = context.getString(R.string.focused_search_keyboard_hint_text)
        mFocusedColor = ContextCompat.getColor(mContext, R.color.search_orb_focused_hint_color)
        mUnfocusedColor = ContextCompat.getColor(mContext, R.color.search_orb_unfocused_hint_color)
        if (res.getBoolean(R.bool.is_hint_flipping_allowed) && isKatnissPackagePresent) {
            z = true
        }
        mIsHintFlippingAllowed = z
        mWahlbergUx = useWahlbergUx()
        mSearchOrbsSpacing = res.getDimensionPixelSize(R.dimen.search_orbs_spacing)
        mKeyboardOrbAnimationDuration = res.getInteger(R.integer.lb_search_orb_scale_duration_ms)
        mDefaultColorMicIcon = ResourcesCompat.getDrawable(res, R.drawable.ic_mic_color, null)
        mMicUnfocusedIcon = ResourcesCompat.getDrawable(res, R.drawable.ic_mic_grey, null)
    }

    private fun useWahlbergUx(): Boolean {
        try {
            val searchResources =
                mContext.packageManager.getResourcesForApplication(kantnissPackageID)
            var resId = 0
            if (searchResources != null) {
                resId = searchResources.getIdentifier(
                    "katniss_uses_new_google_logo",
                    "bool",
                    kantnissPackageID
                )
            }
            if (resId != 0) {
                return searchResources.getBoolean(resId)
            }
            // FIXME: versionCode deprecated in API28, only Katniss 3.13+
            if (isKatnissPackagePresent) {
                val vc =
                    mContext.packageManager.getPackageInfo(
                        kantnissPackageID,
                        0
                    ).versionCode // 11000272
                if (vc > 11000000) {
                    return true
                }
            }
        } catch (_: PackageManager.NameNotFoundException) {
        }
        return false
    }

    val isKatnissPackagePresent: Boolean
        get() {
            val enabled = try {
                mContext.packageManager.getApplicationInfo(kantnissPackageID, 0).enabled
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
            return enabled
        }

    val isAssistPackagePresent: Boolean
        get() {
            var assistIntent = mSearchIntent
            try {
                val packages = mContext.packageManager.queryIntentActivities(assistIntent, 0)
                val pkg = packages.first().activityInfo.packageName
                return pkg.isNotEmpty()
            } catch (e: Exception) {
                assistIntent = Intent(Intent.ACTION_VOICE_COMMAND)
                try {
                    val packages = mContext.packageManager.queryIntentActivities(assistIntent, 0)
                    val pkg = packages.first().activityInfo.packageName
                    return pkg.isNotEmpty()
                } catch (_: Exception) {
                }
            }
            return false
        }

    public override fun onFinishInflate() {
        super.onFinishInflate()
        mWidgetView = findViewById(R.id.widget_wrapper)
        mMicOrbView = findViewById(R.id.mic_orb)
        mMicOrbView?.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            setSearchState(false)
            if (mAssistantIcon != null) {
                mMicOrbView?.orbIcon = if (hasFocus) mDefaultColorMicIcon else mAssistantIcon
            }
        }
        initializeSearchOrbs()
        initTextSwitcher(context)
    }

    override fun onSearchPackageChanged() {
        if (isKatnissPackagePresent || isAssistPackagePresent) {
            showSearch()
        } else {
            hideSearch()
        }
        if (useWahlbergUx() != mWahlbergUx) {
            mWahlbergUx = useWahlbergUx()
            initializeSearchOrbs()
            setSearchState(false)
        }
    }

    val searchPackageName: String
        get() {
            if (isKatnissPackagePresent)
                kantnissPackageID
            else {
                var assistIntent = mSearchIntent
                try {
                    val packages = mContext.packageManager.queryIntentActivities(assistIntent, 0)
                    return packages.first().activityInfo.packageName
                } catch (e: Exception) {
                    assistIntent = Intent(Intent.ACTION_VOICE_COMMAND)
                    try {
                        val packages =
                            mContext.packageManager.queryIntentActivities(assistIntent, 0)
                        return packages.first().activityInfo.packageName
                    } catch (_: Exception) {
                    }
                }
            }
            return kantnissPackageID
        }

    private fun initializeSearchOrbs() {
        if (mOrbAnimation != null && (mOrbAnimation!!.isRunning || mOrbAnimation!!.isStarted)) {
            mOrbAnimation?.cancel()
        }
        mOrbAnimation = null
        if (mWahlbergUx) {
            mKeyboardOrbView = findViewById(R.id.keyboard_orb)
            mKeyboardContainer = findViewById(R.id.keyboard_orb_container)
            mKeyboardFocusedIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_keyboard_blue)
            mKeyboardUnfocusedIcon =
                ContextCompat.getDrawable(mContext, R.drawable.ic_keyboard_grey)
            mColorBright = ContextCompat.getColor(mContext, R.color.search_orb_bg_bright_color)
            mColorDim = ContextCompat.getColor(mContext, R.color.search_orb_bg_dim_color)
            mKeyboardOrbView?.orbIcon = mKeyboardUnfocusedIcon
            mKeyboardOrbView?.enableOrbColorAnimation(false)
            mKeyboardOrbView?.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
                setSearchState(false)
                val keyboardOrbView = v as SearchOrbView
                keyboardOrbView.orbIcon =
                    if (hasFocus) mKeyboardFocusedIcon else mKeyboardUnfocusedIcon
                keyboardOrbView.orbColor = if (hasFocus) mColorBright else mColorDim
                when {
                    hasFocus -> {
                        mMicOrbView?.orbIcon = mMicUnfocusedIcon
                    }
                    mAssistantIcon != null -> {
                        mMicOrbView?.orbIcon = mAssistantIcon
                    }
                    else -> {
                        mMicOrbView?.orbIcon = mDefaultColorMicIcon
                    }
                }
            }
            mOrbAnimation = ObjectAnimator.ofFloat(this, "keyboardOrbProgress", 0.0f)
            mOrbAnimation?.duration = mKeyboardOrbAnimationDuration.toLong()
        } else {
            mKeyboardFocusedIcon = null
            mKeyboardUnfocusedIcon = null
            mKeyboardOrbView = null
            mKeyboardContainer = null
        }
        val partnerSearchIcon = Partner.get(mContext).customSearchIcon
        when {
            mAssistantIcon != null -> {
                mMicOrbView?.orbIcon = mAssistantIcon
                mMicOrbView?.orbColor = mColorBright
                mMicOrbView?.enableOrbColorAnimation(false)
            }
            partnerSearchIcon != null -> {
                mMicOrbView?.orbIcon = partnerSearchIcon
            }
            mWahlbergUx -> {
                mMicOrbView?.orbColor = mColorBright
                mMicOrbView?.orbIcon = mDefaultColorMicIcon
                mMicOrbView?.enableOrbColorAnimation(false)
            }
            else -> {
                mMicOrbView?.orbColors = SearchOrbView.Colors(
                    ContextCompat.getColor(
                        mContext,
                        R.color.search_orb_bg_color_old
                    ), ContextCompat.getColor(mContext, R.color.search_orb_bg_bright_color_old)
                )
                mMicOrbView?.orbIcon =
                    ContextCompat.getDrawable(mContext, R.drawable.ic_search_mic_out_normal)
                mMicOrbView?.enableOrbColorAnimation(true)
            }
        }
        viewTreeObserver.addOnGlobalLayoutListener { setKeyboardOrbProgress(mKeyboardOrbProgress) }
    }

    private fun focusIsOnSearchView(): Boolean {
        return mMicOrbView!!.hasFocus() || mKeyboardOrbView != null && mKeyboardOrbView!!.hasFocus()
    }

    private fun setSearchState(isReset: Boolean) {
        val isKeyboard: Boolean
        mHandler.removeCallbacks(mSwitchRunnable!!)
        val focused = focusIsOnSearchView()
        val old = mCurrentIndex
        isKeyboard = mWahlbergUx && focused && !mMicOrbView!!.hasFocus()
        val i = if (focused) if (!isKeyboard) -2 else -3 else -1
        mCurrentIndex = i
        if (old != mCurrentIndex) {
            val useFade: Boolean = old != -1 && mCurrentIndex != -1
            val z: Boolean = !isReset
            configSwitcher(z, focused, if (useFade) 2 else 1)
            if (mWahlbergUx) {
                mSwitcher?.setText(fixItalics(getHintText(focused, isKeyboard)))
            } else {
                mSwitcher?.setText(fixItalics(getHintText(focused, false)))
            }
        }
        if (mKeyboardOrbView != null) {
            animateKeyboardOrb(focused)
        }
    }

    private fun getHintText(focused: Boolean, isKeyboard: Boolean): String? {
        return if (!mWahlbergUx) {
            if (focused) mFocusedText else mSearchHintText
        } else {
            if (focused) {
                if (isKeyboard) mFocusedKeyboardText else mFocusedMicText
            } else {
                mSearchHintText
            }
        }
    }

    private fun initTextSwitcher(context: Context) {
        mSwitcher = findViewById(R.id.text_switcher)
        mSwitcher?.animateFirstView = false
        mSwitcher?.setFactory(object : ViewSwitcher.ViewFactory {
            var inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            override fun makeView(): View {
                return inflater.inflate(R.layout.search_orb_text_hint, this@SearchOrbView, false)
            }
        })
        mSwitchRunnable = object : Runnable {
            override fun run() {
                val old = mCurrentIndex
                mCurrentIndex = Random().nextInt(mTextToShow.size)
                if (old == mCurrentIndex) {
                    mCurrentIndex = (mCurrentIndex + 1) % mTextToShow.size
                }
                configSwitcher(switchViews = true, focused = false, animType = 0)
                mSwitcher?.setText(fixItalics(mTextToShow[mCurrentIndex]))
                mHandler.postDelayed(this, mIdleTextFlipDelay.toLong())
            }
        }
        reset()
    }

    fun fixItalics(text: String?): String? {
        if (text == null) {
            return null
        }
        val builder = StringBuilder(text)
        if (layoutDirection == LAYOUT_DIRECTION_RTL) {
            builder.insert(0, " ")
        } else {
            builder.append(" ")
        }
        return builder.toString()
    }

    private fun configSwitcher(switchViews: Boolean, focused: Boolean, animType: Int) {
        val inAnim: Int
        val outAnim: Int
        val v = if (switchViews) mSwitcher!!.nextView else mSwitcher!!.currentView
        if (v is TextView) {
            v.setTextColor(if (focused) mFocusedColor else mUnfocusedColor)
            v.setTypeface(null, Typeface.ITALIC)
        }
        when (animType) {
            1 -> {
                inAnim = R.anim.slide_in_left
                outAnim = R.anim.slide_out_right
            }
            0 -> {
                inAnim = R.anim.slide_in_bottom
                outAnim = R.anim.slide_out_top
            }
            else -> {
                inAnim = R.anim.fade_in
                outAnim = R.anim.fade_out
            }
        }
        mSwitcher?.setInAnimation(mContext, inAnim)
        mSwitcher?.setOutAnimation(mContext, outAnim)
    }

    fun reset() {
        mHandler.removeCallbacks(mSwitchRunnable!!)
        mSwitcher?.reset()
        mCurrentIndex = 0
        setSearchState(true)
    }

    override fun onIdleStateChange(isIdle: Boolean) {
        if (mIsHintFlippingAllowed) {
            mHandler.removeCallbacks(mSwitchRunnable!!)
            if (isIdle && isAttachedToWindow && isOnScreen && !mMicOrbView!!.hasFocus()) {
                mHandler.post(mSwitchRunnable!!)
            }
        }
    }

    private val isOnScreen: Boolean
        get() {
            val rect = Rect()
            return getGlobalVisibleRect(rect) && height == rect.height() && width == rect.width()
        }

    override fun onVisibilityChange(isVisible: Boolean) {
        if (!isVisible) {
            reset()
        } else if (mKeyboardOrbView != null && mKeyboardOrbView!!.hasFocus()) {
            mMicOrbView?.requestFocus()
        }
        val searchOrbView = mMicOrbView
        val z = isVisible && mMicOrbView!!.hasFocus() && !mWahlbergUx
        searchOrbView?.enableOrbColorAnimation(z)
    }

    private fun setVisible(visible: Boolean) {
        animateVisibility(mWidgetView, visible)
        animateVisibility(mSwitcher, visible)
    }

    fun hideSearch() {
        mKeyboardOrbView?.visibility = View.INVISIBLE
        mMicOrbView?.visibility = View.GONE
        mSwitcher?.visibility = View.GONE
    }

    fun showSearch() {
        mKeyboardOrbView?.visibility = View.VISIBLE
        mMicOrbView?.visibility = View.VISIBLE
        mSwitcher?.visibility = View.VISIBLE
    }

    private fun animateVisibility(view: View?, visible: Boolean) {
        view!!.clearAnimation()
        val anim = view.animate().alpha(if (visible) 1.0f else 0.0f)
            .setDuration(mLaunchFadeDuration.toLong())
        if (!(!mWahlbergUx || mKeyboardOrbView == null || visible)) {
            anim.setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    if (mKeyboardOrbView != null && mKeyboardOrbView!!.hasFocus()) {
                        mMicOrbView?.requestFocus()
                    }
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
        anim.start()
    }

    private fun animateKeyboardOrb(visible: Boolean) {
        mKeyboardOrbView?.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        if (mOrbAnimation != null) {
            if (mOrbAnimation!!.isStarted) {
                mOrbAnimation?.cancel()
            }
            if (mKeyboardOrbProgress != (if (visible) 1.0f else 0.0f)) {
                mOrbAnimation?.setFloatValues(mKeyboardOrbProgress, if (visible) 1.0f else 0.0f)
                mOrbAnimation?.start()
            }
        }
    }

    private fun setKeyboardOrbProgress(progress: Float) {
        var i = View.VISIBLE
        var d = 1
        val focusable: Boolean = progress.toDouble() == 1.0
        val visible: Boolean = progress > 0.0f
        mKeyboardOrbProgress = progress
        if (mKeyboardOrbView != null) {
            mKeyboardOrbView?.isFocusable = focusable
            mKeyboardOrbView?.alpha = progress
            val frameLayout = mKeyboardContainer
            if (!visible) {
                i = View.INVISIBLE
            }
            frameLayout?.visibility = i
            mKeyboardContainer?.scaleX = progress
            mKeyboardContainer?.scaleY = progress
            val orbWidth = mKeyboardOrbView?.measuredWidth ?: 0
            if (layoutDirection != View.LAYOUT_DIRECTION_RTL) {
                d = -1
            }
            val offset = (d * (mSearchOrbsSpacing + orbWidth)).toFloat() * (1.0f - progress)
            mKeyboardOrbView?.translationX = offset / progress
            mSwitcher?.translationX = offset
        }
    }

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        reset()
    }

    public override fun onAttachedToWindow() {
        var isTouchExplorationEnabled = true
        super.onAttachedToWindow()
        setVisible(true)
        val am = mContext.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        if (!(am.isEnabled && am.isTouchExplorationEnabled)) {
            isTouchExplorationEnabled = false
        }
        val finalIsTouchExplorationEnabled = isTouchExplorationEnabled // TODO: check Accessibility
        //val finalIsTouchExplorationEnabled = false
        val listener = OnClickListener {
            val success: Boolean
            val iskeyboardSearch: Boolean =
                mKeyboardOrbView != null && mKeyboardOrbView!!.hasFocus()
            success = if (!finalIsTouchExplorationEnabled) {
                startSearchActivitySafely(
                    mContext,
                    mSearchIntent,
                    mClickDeviceId,
                    iskeyboardSearch
                ) && mListener != null
            } else startSearchActivitySafely(
                mContext,
                mSearchIntent,
                iskeyboardSearch
            ) && mListener != null
            if (success) {
                animateOut()
                mListener?.onSearchLaunched()
            }
        }
        mMicOrbView?.setOnClickListener(listener)
        mKeyboardOrbView?.setOnClickListener(listener)

    }

    fun setLaunchListener(listener: SearchLaunchListener?) {
        mListener = listener
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val action = event.action
        if (isConfirmKey(event.keyCode)) {
            if (event.isLongPress) {
                mEatDpadCenterKeyDown = true
                playErrorSound(context)
                return true
            } else if (action == 1) {
                if (mEatDpadCenterKeyDown) {
                    mEatDpadCenterKeyDown = false
                    return true
                }
                mClickDeviceId = event.deviceId
            }
        }
        return super.dispatchKeyEvent(event)
    }

    fun animateIn() {
        setVisible(true)
    }

    private fun animateOut() {
        setVisible(false)
        reset()
    }

    override fun setActivated(active: Boolean) {}

    fun updateSearchSuggestions(suggestions: Array<String>?) {
        mCurrentIndex = 0
        mTextToShow = if (suggestions == null || suggestions.isEmpty()) {
            mDefaultTextToShow
        } else {
            suggestions
        }
    }

    fun updateAssistantIcon(icon: Drawable?) {
        mAssistantIcon = icon
        initializeSearchOrbs()
    }

}