package com.amazon.tv.leanbacklauncher

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalFocusChangeListener
import android.view.accessibility.AccessibilityManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.widget.OnChildViewHolderSelectedListener
import androidx.recyclerview.widget.RecyclerView
import com.amazon.tv.firetv.leanbacklauncher.apps.FavoritesAdapter
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.getBannersSize
import com.amazon.tv.leanbacklauncher.animation.ViewDimmer
import com.amazon.tv.leanbacklauncher.apps.*
import com.amazon.tv.leanbacklauncher.util.Util
import com.amazon.tv.leanbacklauncher.widget.EditModeView
import com.amazon.tv.leanbacklauncher.widget.EditModeViewActionListener
import java.util.*

class EditableAppsRowView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ActiveItemsRowView(context, attrs, defStyle), OnGlobalFocusChangeListener,
    BannerSelectedChangedListener, OnEditModeChangedListener, EditModeViewActionListener {
    private val TAG =
        if (BuildConfig.DEBUG) ("*" + javaClass.simpleName).take(21) else javaClass.simpleName
    private var mChangeObserver: AdapterDataObserver
    private var mCurFocused = 0
    private val mEditListeners: ArrayList<OnEditModeChangedListener?>
    private var mEditMode = false
    var isEditModePending = false
    private var mEditModeView: EditModeView? = null
    private var mLastFocused: Int
    private var mSwapping = false

    init {
        mLastFocused = -1
        mEditListeners = ArrayList<OnEditModeChangedListener?>()
        this.mChangeObserver = object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (mCurFocused >= positionStart) mCurFocused += itemCount
                if (mLastFocused >= positionStart) mLastFocused += itemCount
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                if (mCurFocused >= positionStart + itemCount) {
                    mCurFocused -= itemCount
                } else if (mCurFocused >= positionStart) {
                    focusOnNewPosition() // TODO: check EditMode logic
                }
                if (mLastFocused >= positionStart + itemCount) {
                    mLastFocused -= itemCount
                }
            }
        }
        setOnChildViewHolderSelectedListener(object : OnChildViewHolderSelectedListener() {
            override fun onChildViewHolderSelected(
                parent: RecyclerView?,
                holder: ViewHolder?,
                position: Int,
                subposition: Int
            ) {
                this@EditableAppsRowView.onChildViewHolderSelected(position)
            }
        })
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        getAdapter()?.unregisterAdapterDataObserver(this.mChangeObserver)
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(this.mChangeObserver)
    }

    override fun onChildAttachedToWindow(child: View) {
        super.onChildAttachedToWindow(child)
        if (child is BannerView) {
            addEditModeListener(child)
            child.addSelectedListener(mEditModeView)
            child.addSelectedListener(this)
            child.setOnEditModeChangedListener(this)
        }
    }

    override fun onChildDetachedFromWindow(child: View) {
        super.onChildDetachedFromWindow(child)
        if (child is BannerView) {
            removeEditModeListener(child)
            child.removeSelectedListener(mEditModeView)
            child.removeSelectedListener(this)
            child.setOnEditModeChangedListener(null)
        }
    }

    fun addEditModeListener(listener: OnEditModeChangedListener?) {
        mEditListeners.add(listener)
    }

    private fun removeEditModeListener(listener: OnEditModeChangedListener?) {
        mEditListeners.remove(listener)
    }

    fun setEditModeView(editModeView: EditModeView?) {
        mEditModeView?.removeActionListener(this)
        mEditModeView = editModeView
        mEditModeView?.addActionListener(this)
    }

    @set:SuppressLint("RestrictedApi")
    var editMode: Boolean
        get() = mEditMode
        set(editMode) {
            isEditModePending = false
            if (Util.isInTouchExploration(context) && editMode) {
                (context as Activity).setTitle(R.string.title_app_edit_mode)
            }
            if (mEditMode != editMode) {
                mEditMode = editMode
                val dimensionPixelSize: Int = if (editMode) {
                    val size = getBannersSize(context)
                    resources.getDimensionPixelSize(R.dimen.banner_width) * size / 100
                } else {
                    0
                }
                extraLayoutSpace = dimensionPixelSize
                if (!editMode) {
                    viewTreeObserver.removeOnGlobalFocusChangeListener(this)
                    val adapter = adapter as AppsAdapter?
                    adapter?.saveAppOrderSnapshot()
                } else if (isAccessibilityEnabled) {
                    viewTreeObserver.addOnGlobalFocusChangeListener(this)
                }
                if (mEditListeners.isNotEmpty()) {
                    val it: Iterator<*> = mEditListeners.iterator()
                    while (it.hasNext()) {
                        (it.next() as OnEditModeChangedListener).onEditModeChanged(editMode)
                    }
                }
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Edit Mode is now $mEditMode.")
                }
            }
        }

    val isRowEditable: Boolean
        //get() = true // disable EditMode for Favorites
        get() {
            val adapter = adapter
            return adapter !is FavoritesAdapter
        }

    override fun onSelectedChanged(bannerView: BannerView, selected: Boolean) {
        if (isUninstallDisallowed(bannerView) && selected) {
            mEditModeView?.clearUninstallAndFinishLayers()
        } else {
            mEditModeView?.onSelectedChanged(bannerView, selected)
        }
        val childCount = childCount
        refreshSelectedView()
        mDimState = if (selected) ViewDimmer.DimState.EDIT_MODE else ViewDimmer.DimState.ACTIVE
        val curViewHolder = curViewHolderInt
        val itemView = curViewHolder?.itemView
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child !== itemView && child is BannerView) {
                (child as DimmableItem).setDimState(mDimState, false)
            }
        }
    }

    private fun isUninstallDisallowed(v: BannerView?): Boolean {
        if (v == null) {
            return true
        }
        val lp = getViewLaunchPoint(v)
        return lp == null || Util.isSystemApp(
            context,
            getViewPackageName(v)
        ) || !Util.isUninstallAllowed(context) || lp.isInstalling
    }

    override fun onEditModeExitTriggered() {
        val curViewHolder = curViewHolderInt
        if (isRowActive && curViewHolder != null) {
            curViewHolder.itemView.requestFocus()
            setBannerDrawableUninstallState(false)
        }
        if (Util.isInTouchExploration(context)) {
            (context as Activity).setTitle(R.string.app_label)
        }
        editMode = false
    }

    override fun onFocusLeavingEditModeLayer(from: Int) {
        if (isRowActive) {
            val lastFocusedViewHolder = lastFocusedViewHolderInt
            val lastFocusedView = lastFocusedViewHolder?.itemView
            val curFocusedViewHolder = curViewHolderInt
            val curFocusedView = curFocusedViewHolder?.itemView
            if (lastFocusedView is BannerView && curFocusedView is BannerView) {
                lastFocusedView.requestFocus()
                if (mLastFocused != mCurFocused) {
                    focusOnNewPosition()
                    mLastFocused = mCurFocused
                }
                if (from == 1) {
                    lastFocusedView.setSelected(true)
                    mEditModeView?.setBannerUninstallModeWithAnimation(false, lastFocusedView, this)
                }
            } else if (childCount > 0 && mEditMode) {
                focusOnNewPosition()
            }
        }
    }

    override fun onPrepForUninstall(): String {
        val packageName = ""
        val lastFocusedViewHolder = lastFocusedViewHolderInt
        return if (lastFocusedViewHolder == null || adapter !is AppsAdapter) {
            packageName
        } else
            getViewPackageName(lastFocusedViewHolder.itemView)!!
    }

    private fun getViewLaunchPoint(view: View): LaunchPoint? {
        if (adapter is AppsAdapter) {
            val position = getChildAdapterPosition(view)
            val adapter = adapter as AppsAdapter?
            if (!(adapter == null || position == -1)) {
                return adapter.getLaunchPointForPosition(position)
            }
        }
        return null
    }

    private fun getViewPackageName(view: View): String? {
        val lp = getViewLaunchPoint(view)
        return lp?.packageName
    }

    override fun onUninstallComplete() {
        val lastFocusedViewHolder = lastFocusedViewHolderInt
        lastFocusedViewHolder?.let { holder ->
            holder.itemView.isSelected = false
            if (holder.itemView is BannerView) {
                (holder.itemView as BannerView).notifyEditModeManager(false)
            }
            setChildrenLastFocusedBanner(null)
            setBannerDrawableUninstallState(false)
            focusOnNewPosition()
        }
    }

    override fun onUninstallFailure() {
        val lastFocusedViewHolder = lastFocusedViewHolderInt
        lastFocusedViewHolder?.let { holder ->
            holder.itemView.requestFocus()
            setBannerDrawableUninstallState(false)
        }
    }

    private fun setUninstallState() {
        refreshSelectedView()
        val curFocusedViewHolder = curViewHolderInt
        val curView = curFocusedViewHolder?.itemView
        if (curView is BannerView && !isUninstallDisallowed(curView as BannerView?)) {
            mLastFocused = mCurFocused
            mEditModeView?.requestUninstallIconFocus(curView as BannerView?, this)
            mEditModeView?.setBannerDrawable(lastFocusedBannerDrawable)
        }
    }

    override fun onEditModeChanged(z: Boolean) {
        editMode = z
    }

    private fun swapAppOrder(movingBanner: BannerView?, otherBanner: BannerView?) {
        if (BuildConfig.DEBUG) Log.d(
            TAG,
            "swapAppOrder(${getChildAdapterPosition(movingBanner!!)},${
                getChildAdapterPosition(otherBanner!!)
            })"
        )
        (adapter as AppsAdapter?)?.moveLaunchPoint(
            getChildAdapterPosition(movingBanner!!),
            getChildAdapterPosition(otherBanner!!),
            true
        )
    }

    override fun onGlobalFocusChanged(oldFocus: View?, newFocus: View?) {
        if (isRowActive && oldFocus != null && newFocus != null) {
            if (oldFocus !is BannerView || newFocus !is BannerView) {
                val curFocusedViewHolder = curViewHolderInt
                val lastFocusedViewHolder = lastFocusedViewHolderInt
                when {
                    // uninstall icon
                    newFocus == mEditModeView!!.uninstallIcon -> {
                        setUninstallState()
                        setChildrenLastFocusedBanner(if (lastFocusedViewHolder != null) lastFocusedViewHolder.itemView as BannerView else null)
                    }
                    oldFocus == mEditModeView!!.uninstallIcon -> {
                        val editModeView = mEditModeView
                        val bannerView: BannerView? = if (curFocusedViewHolder != null) {
                            curFocusedViewHolder.itemView as BannerView
                        } else {
                            null
                        }
                        editModeView!!.setBannerUninstallModeWithAnimation(false, bannerView, this)
                        setChildrenLastFocusedBanner(null)
                    }
                    // uninstall button
                    newFocus == mEditModeView!!.finishButton -> {
                        setChildrenLastFocusedBanner(if (curFocusedViewHolder != null) curFocusedViewHolder.itemView as BannerView else null)
                    }
                    oldFocus == mEditModeView!!.finishButton -> {
                        setChildrenLastFocusedBanner(null)
                    }
                }
            } else if (oldFocus.isSelected() && !mSwapping) {
                try {
                    mSwapping = true
                    swapAppOrder(oldFocus, newFocus)
                    oldFocus.requestFocus()
                } finally {
                    mSwapping = false
                }
            }
        }
    }

    // TODO: check cast for mEditModeView
    override fun focusSearch(focused: View, direction: Int): View {
        var dir = direction
        if (mEditMode) {
            val position = getChildAdapterPosition(focused)
            val numRows = aNumRows
            if (focused.isSelected) { // moving app in edit mode
                if (itemAnimator!!.isRunning) {
                    return focused
                }
                if (layoutDirection == LAYOUT_DIRECTION_RTL && (dir == FOCUS_LEFT || dir == FOCUS_RIGHT)) {
                    dir = if (dir == FOCUS_LEFT) FOCUS_RIGHT else FOCUS_LEFT
                }
                when (dir) {
                    FOCUS_DOWN -> {
                        if (position % numRows >= numRows - 1 || position >= adapter!!.itemCount - 1) {
                            setUninstallState()
                            return mEditModeView!!.uninstallIcon as View
                        }
                        moveLaunchPoint(position, position + 1)
                        return focused
                    }
                    FOCUS_UP -> {
                        if (position % numRows <= 0) {
                            return focused
                        }
                        moveLaunchPoint(position, position - 1)
                        return focused
                    }
                    FOCUS_RIGHT -> {
                        moveLaunchPoint(position, position + numRows)
                        return focused
                    }
                    FOCUS_LEFT -> {
                        moveLaunchPoint(position, position - numRows)
                        return focused
                    }
                }
            } else if (dir == FOCUS_DOWN && position % numRows == numRows - 1) {
                setLastFocused()
                return mEditModeView!!.finishButton as View
            } else if (dir == FOCUS_DOWN && position == adapter!!.itemCount - 1) {
                return focused
            } else {
                if (dir == FOCUS_UP && position % numRows == 0) {
                    return focused
                }
            }
        }
        return super.focusSearch(focused, dir)
    }

    private val isAccessibilityEnabled: Boolean
        //get() = (context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager).isEnabled
        get() {
            val enabled =
                (context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager).isEnabled
            Log.d(TAG, "Accessibility enabled: $enabled.")
            return enabled
        }

    private fun moveLaunchPoint(fromPosition: Int, toPosition: Int): Boolean {
        return (adapter as AppsAdapter?)!!.moveLaunchPoint(fromPosition, toPosition, true)
    }

    private fun focusOnNewPosition() {
        val curFocusedViewHolder = curViewHolderInt
        if (curFocusedViewHolder == null) {
            mCurFocused = NO_POSITION
            return
        }
        var newFocusPosition = curFocusedViewHolder.layoutPosition + aNumRows
        if (isAccessibilityEnabled || !isRowActive) { // FIXME: no accessibility focus bug
            newFocusPosition = 0
        }
        val children = childCount
        if (newFocusPosition >= children) {
            newFocusPosition = children - 1
        }
        if (newFocusPosition == curFocusedViewHolder.layoutPosition) {
            newFocusPosition--
        }
        if (newFocusPosition < 0 || curFocusedViewHolder.itemView !is BannerView) {
            onEditModeExitTriggered()
            return
        }
        onSelectedChanged(curFocusedViewHolder.itemView as BannerView, false)
        val newFocusView = getChildAt(newFocusPosition)
        newFocusView?.requestFocus()
        newFocusView?.isSelected = false
    }

    private val lastFocusedBannerDrawable: Drawable?
        get() {
            val adapter = adapter
            val lastFocusedViewHolder = lastFocusedViewHolderInt
            if (lastFocusedViewHolder == null || adapter !is AppsAdapter) {
                return null
            }
            val position = lastFocusedViewHolder.bindingAdapterPosition
            if (position != -1) {
                var drawable = adapter.getDrawableFromLaunchPoint(position)
                if (drawable == null) {
                    lastFocusedViewHolder.itemView.isDrawingCacheEnabled = true
                    val tmpBitmap = Bitmap.createBitmap(lastFocusedViewHolder.itemView.drawingCache)
                    lastFocusedViewHolder.itemView.isDrawingCacheEnabled = false
                    drawable = BitmapDrawable(resources, tmpBitmap)
                }
                return drawable
            }
            return null
        }

    fun setBannerDrawableUninstallState(uninstalling: Boolean) {
        val lastFocusedViewHolder = lastFocusedViewHolderInt
        if (lastFocusedViewHolder != null && lastFocusedViewHolder.itemView is BannerView) {
            val itemView = lastFocusedViewHolder.itemView
            val bannerView = itemView.findViewById<View>(R.id.app_banner)
            if (bannerView != null) {
                var drawable: Drawable?
                drawable = if (uninstalling) {
                    ResourcesCompat.getDrawable(resources, R.drawable.dashed_holder, null)
                } else {
                    ResourcesCompat.getDrawable(resources, R.drawable.banner_background, null)
                }
                bannerView.background = drawable
                var i = View.GONE
                if (bannerView is LinearLayout) {
                    val icon = itemView.findViewById<View>(R.id.banner_icon)
                    val vis: Int = if (uninstalling) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
                    icon.visibility = vis
                    val text = itemView.findViewById<View>(R.id.banner_label)
                    if (!uninstalling) {
                        i = View.VISIBLE
                    }
                    text.visibility = i
                }
                if (bannerView is ImageView) {
                    drawable = if (uninstalling) {
                        null
                    } else {
                        lastFocusedBannerDrawable
                    }
                    bannerView.setImageDrawable(drawable)
                }
                if (!uninstalling) {
                    mLastFocused = -1
                }
            }
        }
    }

    private fun setChildrenLastFocusedBanner(view: BannerView?) {
        val children = childCount
        for (i in 0 until children) {
            if (getChildAt(i) is BannerView) {
                (getChildAt(i) as BannerView).setLastFocusedBanner(view)
            }
        }
    }

    private fun setLastFocused() {
        mLastFocused = mCurFocused
    }

    fun onChildViewHolderSelected(position: Int) {
        if (position != mCurFocused) {
            if (mEditMode) {
                mLastFocused = mCurFocused
            }
            mCurFocused = position
        }
        postInvalidateDelayed(50)
    }

    fun refreshSelectedView() {
        mCurFocused = selectedPosition
    }

    val curViewHolder: ViewHolder?
        get() {
            if (mCurFocused == -1) {
                refreshSelectedView()
            }
            return curViewHolderInt
        }

    private val curViewHolderInt: ViewHolder?
        get() = if (mCurFocused != -1) {
            findViewHolderForLayoutPosition(mCurFocused)
        } else null

    private val lastFocusedViewHolderInt: ViewHolder?
        get() = if (mLastFocused != -1) {
            findViewHolderForLayoutPosition(mLastFocused)
        } else null

}