package com.amazon.tv.leanbacklauncher.apps

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences
import com.amazon.tv.leanbacklauncher.DimmableItem
import com.amazon.tv.leanbacklauncher.EditableAppsRowView
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.RoundedRectOutlineProvider
import com.amazon.tv.leanbacklauncher.animation.AppViewFocusAnimator
import com.amazon.tv.leanbacklauncher.animation.ParticipatesInLaunchAnimation
import com.amazon.tv.leanbacklauncher.animation.ParticipatesInScrollAnimation
import com.amazon.tv.leanbacklauncher.animation.ViewDimmer
import com.amazon.tv.leanbacklauncher.animation.ViewDimmer.DimState
import com.amazon.tv.leanbacklauncher.apps.AppsAdapter.AppViewHolder
import com.amazon.tv.leanbacklauncher.widget.EditModeManager
import java.util.*

class BannerView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context!!, attrs, defStyle), OnLongClickListener, DimmableItem, ParticipatesInLaunchAnimation, ParticipatesInScrollAnimation, OnEditModeChangedListener {
    private var sOutline // was static
            : RoundedRectOutlineProvider? = null
    private var mAppBanner: View? = null
    var viewDimmer: ViewDimmer? = null
        private set
    private var mEditFocusFrame: ImageView? = null
    private var mFocusFrame: ImageView? = null
    private var mEditListener: OnEditModeChangedListener? = null
    private var mEditMode = false
    private val mEditModeManager: EditModeManager
    private val mFocusAnimator: AppViewFocusAnimator
    private var mInstallStateOverlay: View? = null
    private var mLastFocusedBanner: BannerView? = null
    private var mLeavingEditMode = false
    private val mSelectedListeners: ArrayList<BannerSelectedChangedListener?> = ArrayList<BannerSelectedChangedListener?>()

    // TODO
    var viewHolder: AppViewHolder? = null
    override fun onFinishInflate() {
        super.onFinishInflate()
        val ctx = context
        val width = resources.getDimensionPixelSize(R.dimen.banner_width)
        val height = resources.getDimensionPixelSize(R.dimen.banner_height)
        val size = RowPreferences.getBannersSize(ctx) // 50 - 200
        this.layoutParams.height = height * size / 100 // px
        this.layoutParams.width = width * size / 100 // px
        requestLayout() // set new BannerView dimensions
        viewDimmer = ViewDimmer(this)
        mAppBanner = findViewById(R.id.app_banner)
        mInstallStateOverlay = findViewById(R.id.install_state_overlay)
        if (mAppBanner is ImageView) {
            mAppBanner?.setOutlineProvider(sOutline)
            mAppBanner?.setClipToOutline(true)
            viewDimmer?.addDimTarget(mAppBanner as ImageView?)
        } else {
            if (mAppBanner is LinearLayout) {
                mAppBanner?.setOutlineProvider(sOutline)
                mAppBanner?.setClipToOutline(true)
            }
            val inputBannerView = findViewById<View>(R.id.input_banner)
            inputBannerView.let {
                it.outlineProvider = sOutline
                it.clipToOutline = true
            }
            var bannerView = findViewById<View>(R.id.banner_icon)
            if (bannerView is ImageView) {
                viewDimmer?.addDimTarget(bannerView)
            }
            var bannerLabel = findViewById<View>(R.id.banner_label)
            if (bannerLabel is TextView) {
                viewDimmer?.addDimTarget(bannerLabel)
            }
            bannerView = findViewById(R.id.input_image)
            if (bannerView is ImageView) {
                bannerView.setOutlineProvider(sOutline)
                bannerView.setClipToOutline(true)
                viewDimmer?.addDimTarget(bannerView)
            }
            bannerLabel = findViewById(R.id.input_label)
            if (bannerLabel is TextView) {
                viewDimmer?.addDimTarget(bannerLabel)
            }
            if (background != null) {
                viewDimmer?.addDimTarget(background)
            }
        }
        viewDimmer?.setDimLevelImmediate()
        val radius = RowPreferences.getCorners(ctx).toFloat() // (float) getResources().getDimensionPixelOffset(R.dimen.banner_corner_radius);
        var stroke = 2
        var color = resources.getColor(R.color.edit_selection_indicator_color)
        var gd: GradientDrawable? = null
        // edit focus frame (edit_frame_width : edit_frame_height)
        val efv = findViewById<View>(R.id.edit_focused_frame)
        if (efv is ImageView) {
            mEditFocusFrame = efv
            efv.getLayoutParams().width = resources.getDimensionPixelSize(R.dimen.edit_frame_width) * size / 100
            efv.getLayoutParams().height = resources.getDimensionPixelSize(R.dimen.edit_frame_height) * size / 100
            efv.requestLayout() // set new edit focus frame dimensions
            gd = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT))
            gd.shape = GradientDrawable.RECTANGLE
            gd.setStroke(stroke, color) // fixed width
            gd.cornerRadius = radius + (resources.getDimensionPixelSize(R.dimen.edit_frame_width) - width) * size / 100 / 2
            mEditFocusFrame!!.setImageDrawable(gd) // set new edit frame drawable
        }
        // focus frame (banner_frame_width : banner_frame_height)
        val ffv = findViewById<View>(R.id.banner_focused_frame)
        if (ffv is ImageView) {
            mFocusFrame = ffv
            stroke = RowPreferences.getFrameWidth(ctx) // (int) getResources().getDimensionPixelSize(R.dimen.banner_frame_stroke);
            color = RowPreferences.getFrameColor(ctx) // (int) getResources().getColor(R.color.banner_focus_frame_color);
            ffv.getLayoutParams().width = (width + 2 * stroke - radius.toInt() / 2) * size / 100 // px
            ffv.getLayoutParams().height = (height + 2 * stroke - radius.toInt() / 2) * size / 100 // px
            ffv.requestLayout() // set new focus frame dimensions
            gd = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT))
            gd.shape = GradientDrawable.RECTANGLE
            gd.setStroke(stroke * size / 100, color) // setStroke(10, Color.BLACK);
            gd.cornerRadius = radius // setCornerRadius(10f);
            mFocusFrame?.setImageDrawable(gd) // set new focus frame drawable
        }
    }

    fun setTextViewColor(textView: TextView?, color: Int) {
            viewDimmer?.setTargetTextColor(textView, color)
    }

    var isEditMode: Boolean
        get() = mEditMode
        set(editMode) {
            if (isEditable) {
                mEditMode = editMode
            }
            if (!editMode) {
                mLastFocusedBanner = null
            }
        }

    private fun setEditMode() {
            mEditListener?.onEditModeChanged(mEditMode)
    }

    fun addSelectedListener(listener: BannerSelectedChangedListener?) {
        mSelectedListeners.add(listener)
    }

    fun clearBannerForRecycle() {
        clearFocus()
        mEditFocusFrame!!.visibility = GONE
    }

    fun removeSelectedListener(listener: BannerSelectedChangedListener?) {
        mSelectedListeners.remove(listener)
    }

    fun setOnEditModeChangedListener(listener: OnEditModeChangedListener?) {
        mEditListener = listener
    }

    fun setFocusedFrameState() {
        if (!isEditable) {
            return
        }
        if (mEditMode && hasFocus()) {
            viewDimmer?.setDimState(DimState.ACTIVE, false)
            if (isSelected) {
                mEditFocusFrame!!.visibility = GONE
                return
            }
            mEditFocusFrame!!.visibility = VISIBLE // 0 - VISIBLE. 8 - GONE
            post { requestLayout() }
            return
        }
        mEditFocusFrame!!.visibility = GONE
    }

    override fun onEditModeChanged(editMode: Boolean) {
        if (isEditable) {
            mEditMode = editMode
            if (hasFocus()) {
                if (!editMode) {
                    mLeavingEditMode = true
                }
                isSelected = editMode
                setFocusedFrameState()
                mFocusAnimator.onEditModeChanged(this, editMode)
            }
        }
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        if (mEditMode && gainFocus && isEditModeSelected) {
            isSelected = true
            sendAccessibilityEvent(8)
        }
        setFocusedFrameState()
        // focus outline
        mFocusFrame?.let {
            if (hasFocus()) it.visibility = VISIBLE else it.visibility = GONE
        }
    }
    // FIXME: WTF?
//    override fun requestFocus(direction: Int, previous: Rect): Boolean {
//        if (mLastFocusedBanner == null || mLastFocusedBanner === this) {
//                return super.requestFocus(direction, previous)
//        }
//        mLastFocusedBanner!!.requestFocus()
//        return false
//    }

    override fun sendAccessibilityEvent(eventType: Int) {
        if (!mEditMode || isSelected || mEditModeManager.selectedComponentName == null) {
            super.sendAccessibilityEvent(eventType)
        }
    }

    val isEditable: Boolean
        get() {
            val parent = parent
            return if (parent is EditableAppsRowView) {
                parent.isRowEditable
            } else false
        }

    fun onClickInEditMode() {
        var z = false
        if (isEditable) {
            notifyEditModeManager(false)
            if (!isSelected) {
                z = true
            }
            isSelected = z
        }
    }

    override fun onLongClick(v: View): Boolean {
        return if (isEditable && !mEditMode) {
            mEditMode = true
            isSelected = true
            setEditMode()
            true
        } else if (!isEditable || !mEditMode) {
            false
        } else {
            onClickInEditMode()
            true
        }
    }

    override fun setSelected(selected: Boolean) {
        if (mEditMode || mLeavingEditMode) {
            if (selected != isSelected) {
                super.setSelected(selected)
                setFocusedFrameState()
                if (!(mSelectedListeners.isEmpty() || mLeavingEditMode)) {
                    val it: Iterator<*> = mSelectedListeners.iterator()
                    while (it.hasNext()) {
                        (it.next() as BannerSelectedChangedListener).onSelectedChanged(this, selected)
                    }
                }
                if (selected) {
                    notifyEditModeManager(selected)
                }
                if (Log.isLoggable("LauncherEditMode", Log.DEBUG)) {
                    Log.d("LauncherEditMode", "BannerView selected is now: $isSelected")
                }
            }
            mLeavingEditMode = false
        }
    }

    private val isEditModeSelected: Boolean
        get() = TextUtils.equals(viewHolder!!.componentName, mEditModeManager.selectedComponentName)

    fun notifyEditModeManager(selected: Boolean) {
        if (selected) {
            mEditModeManager.selectedComponentName = viewHolder!!.componentName
        } else {
            mEditModeManager.selectedComponentName = null
        }
    }

    override fun setDimState(dimState: DimState, immediate: Boolean) {
        viewDimmer?.setDimState(dimState, immediate)
    }

    fun setLastFocusedBanner(view: BannerView?) {
        mLastFocusedBanner = view
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        clearAnimation()
        viewDimmer?.setDimLevelImmediate()
        mFocusAnimator.setFocusImmediate(hasFocus())
        setAnimationsEnabled(true)
    }

    override fun setZ(z: Float) {
        if (mAppBanner is ImageView || mAppBanner is LinearLayout) {
            mAppBanner?.z = z
            mInstallStateOverlay!!.z = z
            return
        }
        super.setZ(z)
    }

    override fun setAnimationsEnabled(enabled: Boolean) {
        mFocusAnimator.setEnabled(enabled)
        viewDimmer?.setAnimationEnabled(enabled)
    }

    init {
        mFocusAnimator = AppViewFocusAnimator(this)
        mSelectedListeners.add(mFocusAnimator)
        mEditModeManager = EditModeManager.getInstance()
        isSelected = false
        setOnLongClickListener(this)
        if (sOutline == null) {
            //sOutline = RoundedRectOutlineProvider((float) getResources().getDimensionPixelOffset(R.dimen.banner_corner_radius))
            sOutline = RoundedRectOutlineProvider(RowPreferences.getCorners(context!!).toFloat())
        }
    }
}