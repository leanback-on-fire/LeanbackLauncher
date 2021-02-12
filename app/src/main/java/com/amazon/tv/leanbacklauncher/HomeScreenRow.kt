package com.amazon.tv.leanbacklauncher

import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.amazon.tv.firetv.leanbacklauncher.apps.RowType

class HomeScreenRow internal constructor(val type: RowType, val position: Int, private val mHideIfEmpty: Boolean) : AdapterDataObserver() {
    private var mAdapter: RecyclerView.Adapter<*>? = null
    var fontName: String? = null
        private set
    private var mHasHeader = false
    var icon: Drawable? = null
        private set
    private var mListener: RowChangeListener? = null
    var rowScrollOffset = 0
        private set
    var rowView: View? = null
    var title: String? = null
        private set
    private var mVisible = isVisible

    interface RowChangeListener {
        fun onRowVisibilityChanged(i: Int, z: Boolean)
    }

    fun setHeaderInfo(hasHeader: Boolean, title: String?, icon: Drawable?, fontName: String?) {
        mHasHeader = hasHeader
        if (hasHeader) {
            this.title = title
            this.fontName = fontName
            this.icon = icon
        } else {
            this.title = null
        }
    }

    fun hasHeader(): Boolean {
        return mHasHeader
    }

    var adapter: RecyclerView.Adapter<*>?
        get() = mAdapter
        set(adapter) {
            if (adapter != null) {
                if (mHideIfEmpty && mAdapter != null) {
                    mAdapter!!.unregisterAdapterDataObserver(this)
                }
                mAdapter = adapter
                mVisible = isVisible
                if (mHideIfEmpty && mAdapter != null) {
                    mAdapter!!.registerAdapterDataObserver(this)
                }
            }
        }

    fun setViewScrollOffset(size: Int) {
        rowScrollOffset = size
    }

    fun setChangeListener(listener: RowChangeListener?) {
        mListener = listener
    }

    val isVisible: Boolean
        get() = !mHideIfEmpty || mAdapter != null && mAdapter!!.itemCount > 0

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        onChanged()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        onChanged()
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        onChanged()
    }

    override fun onChanged() {
        if (mVisible != isVisible) {
            mVisible = !mVisible
            if (mListener != null) {
                mListener!!.onRowVisibilityChanged(position, mVisible)
            }
        }
    }
}