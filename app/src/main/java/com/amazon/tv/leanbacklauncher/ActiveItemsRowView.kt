package com.amazon.tv.leanbacklauncher

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.OnHierarchyChangeListener
import androidx.leanback.widget.HorizontalGridView
import com.amazon.tv.firetv.leanbacklauncher.apps.AppCategory
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.getAppsColumns
import com.amazon.tv.leanbacklauncher.animation.ParticipatesInScrollAnimation
import com.amazon.tv.leanbacklauncher.animation.ViewDimmer
import com.amazon.tv.leanbacklauncher.animation.ViewDimmer.DimState
import com.amazon.tv.leanbacklauncher.apps.AppsAdapter
import com.amazon.tv.leanbacklauncher.capabilities.LauncherConfiguration
import kotlin.math.abs

open class ActiveItemsRowView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : HorizontalGridView(context, attrs, defStyle), OnHierarchyChangeListener {
    private val TAG =
        if (BuildConfig.DEBUG) ("*" + javaClass.simpleName).take(21) else javaClass.simpleName
    private val mCardElevationSupported: Boolean
    private var mCardSpacing = 0
    private var mChangeObserver: AdapterDataObserver
    protected var mDimState: DimState
    private var mIsAdjustable = false
    var aNumRows = 0
        private set
    private var mRowHeight = 0

    init {
        mChangeObserver = object : AdapterDataObserver() {
            override fun onChanged() {
//                if (BuildConfig.DEBUG) Log.d(TAG, "onChanged() aNumRows: $aNumRows")
                // FIXME: this@ActiveItemsRowView.adjustNumRows()
                this@ActiveItemsRowView.adjustNumRows("other")
                val adapter = this@ActiveItemsRowView.adapter
                if (adapter is AppsAdapter && adapter.takeItemsHaveBeenSorted()) {
                    this@ActiveItemsRowView.selectedPosition = 0
                    if (BuildConfig.DEBUG) Log.d(TAG, "onChanged() setSelectedPosition(0)")
                }
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
//                if (BuildConfig.DEBUG) Log.d(TAG, "ActiveItemsRowView: onItemRangeInserted(positionStart " + positionStart + ", itemCount " + itemCount + ")");
                this@ActiveItemsRowView.adjustNumRows()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
//                if (BuildConfig.DEBUG) Log.d(TAG, "ActiveItemsRowView: onItemRangeRemoved(positionStart " + positionStart + ", itemCount " + itemCount + ")");
                this@ActiveItemsRowView.adjustNumRows()
            }
        }
        isChildrenDrawingOrderEnabled = true
        setAnimateChildLayout(true)
        mDimState = DimState.INACTIVE
        mCardElevationSupported = LauncherConfiguration.getInstance().isCardElevationEnabled
    }

    override fun hasOverlappingRendering(): Boolean {
        return hasFocus() && super.hasOverlappingRendering()
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        getAdapter()?.let {
            it.unregisterAdapterDataObserver(mChangeObserver)
        }
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(mChangeObserver)
    }

    override fun onChildAttachedToWindow(child: View) {
        super.onChildAttachedToWindow(child)
        child.isActivated = ViewDimmer.dimStateToActivated(mDimState)
        if (child is DimmableItem) {
            (child as DimmableItem).setDimState(mDimState, false)
        }
        if (mCardElevationSupported) {
            child.z = resources.getDimensionPixelOffset(R.dimen.unselected_item_z).toFloat()
        }
    }

    override fun setActivated(activated: Boolean) {
        if (ViewDimmer.dimStateToActivated(mDimState) != activated) {
            mDimState = ViewDimmer.activatedToDimState(activated)
            val count = childCount
            for (i in 0 until count) {
                val view = getChildAt(i)
                if (view is DimmableItem) {
                    (view as DimmableItem).setDimState(mDimState, false)
                }
            }
        }
        super.setActivated(activated)
    }

    val isRowActive: Boolean
        get() = ViewDimmer.dimStateToActivated(mDimState)

    fun setIsNumRowsAdjustable(isAdjustable: Boolean) {
        mIsAdjustable = isAdjustable
        setOnHierarchyChangeListener(if (isAdjustable) this else null)
    }

    // called on HomeScreenAdaper initAppRow
    fun adjustNumRows(numRows: Int, cardSpacing: Int, rowHeight: Int) {
        if (mIsAdjustable && this.aNumRows != numRows) {
            if (BuildConfig.DEBUG) Log.w(
                TAG,
                "adjustNumRows[$numRows], mIsAdjustable: $mIsAdjustable, numRows: $numRows, aNumRows: ${this@ActiveItemsRowView.aNumRows}"
            )
            this.aNumRows = numRows
            mCardSpacing = cardSpacing
            mRowHeight = rowHeight
            post {
                val lp = this@ActiveItemsRowView.layoutParams
                lp.height =
                    this@ActiveItemsRowView.aNumRows * mRowHeight + (this@ActiveItemsRowView.aNumRows - 1) * mCardSpacing + (this@ActiveItemsRowView.paddingTop + this@ActiveItemsRowView.paddingBottom)
                if (BuildConfig.DEBUG) Log.w(
                    TAG,
                    "new height: ${this@ActiveItemsRowView.layoutParams.height}"
                )
                setNumRows(this@ActiveItemsRowView.aNumRows)
                if (BuildConfig.DEBUG) Log.w(TAG, "call setNumRows($aNumRows)")
                this@ActiveItemsRowView.setRowHeight(mRowHeight)
                if (BuildConfig.DEBUG) Log.w(TAG, "set RowHeight($mRowHeight)")
            }
        }
    }

    fun adjustNumRows(category: String) {
        // calculate number of rows based on maxApps:
        // always fill a least one full row of maxApps
        val curApps = adapter!!.itemCount
        val maxApps = getAppsColumns(context)
        var maxRows: Int // = resources.getInteger(R.integer.max_num_banner_rows)
        var base = abs(curApps / maxApps)
        val lost = (maxApps * (base + 1)) - curApps
        if (lost < base + 1) base += 1
        // FIXME: rework this category mess (what about FAVORITES?)
        val appTypes = listOf(AppCategory.fromName(category)) // get this real somehow
        val userMax: Int = when {
            appTypes.contains(AppCategory.OTHER) -> RowPreferences.getRowMax(AppCategory.OTHER, context)
            appTypes.contains(AppCategory.VIDEO) -> RowPreferences.getRowMax(AppCategory.VIDEO, context)
            appTypes.contains(AppCategory.MUSIC) -> RowPreferences.getRowMax(AppCategory.MUSIC, context)
            appTypes.contains(AppCategory.GAME) -> RowPreferences.getRowMax(AppCategory.GAME, context)
            else -> context.resources.getInteger(R.integer.max_num_banner_rows)
        }
        // numRows
        maxRows = if (base > 0) base.coerceAtMost(userMax) else resources.getInteger(R.integer.min_num_banner_rows)

        if (BuildConfig.DEBUG) Log.w(
            TAG,
            "adjustNumRows($maxRows), items:$curApps columns:$maxApps waste:$lost user max:$userMax curRows:${this.aNumRows}"
        )
        // numRows
        adjustNumRows(maxRows, mCardSpacing, mRowHeight)
    }

    // called on onChanged / onChildViewAdded / onChildViewRemoved
    private fun adjustNumRows() {
        val integer: Int = if (aNumRows > 0) aNumRows else {
            if (adapter!!.itemCount > getAppsColumns(context)) {
                resources.getInteger(R.integer.max_num_banner_rows)
            } else {
                resources.getInteger(R.integer.min_num_banner_rows)
            }
        }
        // apply
        adjustNumRows(integer, mCardSpacing, mRowHeight)
    }

    override fun childHasTransientStateChanged(child: View, hasTransientState: Boolean) {}

    override fun onChildViewAdded(parent: View, child: View) {
        // FIXME adjustNumRows()
        adjustNumRows("other")
    }

    override fun onChildViewRemoved(parent: View, child: View) {
        // FIXME adjustNumRows()
        adjustNumRows("other")
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        enableAnimations(this)
    }

    private fun enableAnimations(view: View) {
        if (view is ParticipatesInScrollAnimation) {
            (view as ParticipatesInScrollAnimation).setAnimationsEnabled(true)
        }
        if (view is ViewGroup) {
            val count = view.childCount
            for (i in 0 until count) {
                enableAnimations(view.getChildAt(i))
            }
        }
    }

    override fun focusSearch(focused: View, direction: Int): View {
        if (direction == 17 || direction == 66) {
            enableAnimations(this)
        }
        return super.focusSearch(focused, direction)
    }

}