package com.amazon.tv.leanbacklauncher

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.OnHierarchyChangeListener
import androidx.leanback.widget.HorizontalGridView
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences.getOneRowMaxApps
import com.amazon.tv.leanbacklauncher.animation.ParticipatesInScrollAnimation
import com.amazon.tv.leanbacklauncher.animation.ViewDimmer
import com.amazon.tv.leanbacklauncher.animation.ViewDimmer.DimState
import com.amazon.tv.leanbacklauncher.apps.AppsAdapter
import com.amazon.tv.leanbacklauncher.capabilities.LauncherConfiguration

open class ActiveItemsRowView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyle: Int = 0) : HorizontalGridView(context, attrs, defStyle), OnHierarchyChangeListener {
    private val TAG =
        if (BuildConfig.DEBUG) ("*" + javaClass.simpleName).take(21) else javaClass.simpleName
    private val mCardElevationSupported: Boolean
    private var mCardSpacing = 0
    private var mChangeObserver: AdapterDataObserver
    protected var mDimState: DimState
    private var mIsAdjustable = false
    var numberOfRows = 0
        private set
    private var mRowHeight = 0

    init {
        mChangeObserver = object : AdapterDataObserver() {
            override fun onChanged() {
//                if (BuildConfig.DEBUG) Log.d(TAG, "ActiveItemsRowView: onChanged()");
                this@ActiveItemsRowView.adjustNumRows()
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
        if (getAdapter() != null) {
            getAdapter()!!.unregisterAdapterDataObserver(mChangeObserver)
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

    fun adjustNumRows(numRows: Int, cardSpacing: Int, rowHeight: Int) {
//        if (BuildConfig.DEBUG) Log.w(TAG, "adjustNumRows(numRows:" + numRows + ", cardSpacing:" + cardSpacing + ", rowHeight:" + rowHeight +")");
        if (mIsAdjustable && this.numberOfRows != numRows) {
            this.numberOfRows = numRows
            mCardSpacing = cardSpacing
            mRowHeight = rowHeight
            post {
                this@ActiveItemsRowView.layoutParams.height = this@ActiveItemsRowView.numberOfRows * mRowHeight + (this@ActiveItemsRowView.numberOfRows - 1) * mCardSpacing + (this@ActiveItemsRowView.paddingTop + this@ActiveItemsRowView.paddingBottom)
                // Log.w(TAG, "height: " + ActiveItemsRowView.this.getLayoutParams().height);
                setNumRows(this@ActiveItemsRowView.numberOfRows)
                // Log.w(TAG, "setNumRows: " + ActiveItemsRowView.this.mNumRows);
                this@ActiveItemsRowView.setRowHeight(mRowHeight)
                // Log.w(TAG, "setRowHeight: " + ActiveItemsRowView.this.mRowHeight);
            }
        }
    }

    private fun adjustNumRows() {
        val integer: Int
        integer = if (numberOfRows > 0) numberOfRows else {
            val res = resources
            val ctx = context
            if (adapter!!.itemCount > getOneRowMaxApps(ctx)) {
                res.getInteger(R.integer.max_num_banner_rows)
            } else {
                res.getInteger(R.integer.min_num_banner_rows)
            }
        }
        //        if (BuildConfig.DEBUG) Log.d(TAG, "ActiveItemsRowView: adjustNumRows() to " + integer);
        adjustNumRows(integer, mCardSpacing, mRowHeight)
    }

    override fun childHasTransientStateChanged(child: View, hasTransientState: Boolean) {}
    override fun onChildViewAdded(parent: View, child: View) {
        adjustNumRows()
    }

    override fun onChildViewRemoved(parent: View, child: View) {
        adjustNumRows()
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