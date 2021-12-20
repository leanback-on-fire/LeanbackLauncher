package com.amazon.tv.leanbacklauncher.notifications

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.leanback.widget.OnChildViewHolderSelectedListener
import androidx.recyclerview.widget.RecyclerView
import com.amazon.tv.leanbacklauncher.ActiveItemsRowView

class NotificationRowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ActiveItemsRowView(context, attrs, defStyle) {
    private var mIgnoreActivateForBckChange = false
    private var mLastReportedBackground: String? = null
    private var mNotificationListener: NotificationRowListener? = null

    interface NotificationRowListener {
        fun onBackgroundImageChanged(str: String?, str2: String?)
        fun onSelectedRecommendationChanged(i: Int)
    }

    fun setListener(listener: NotificationRowListener?) {
        mNotificationListener = listener
    }

    fun refreshSelectedBackground() {
        updateLauncherBackground(1)
    }

    fun setIgnoreNextActivateBackgroundChange() {
        if (!isRowActive) {
            mIgnoreActivateForBckChange = true
        }
    }

    override fun setActivated(activated: Boolean) {
        val oldActivated = isRowActive
        super.setActivated(activated)
        if (oldActivated == activated) {
            return
        }
        if (mIgnoreActivateForBckChange) {
            updateLauncherBackground(2)
            mIgnoreActivateForBckChange = false
            return
        }
        updateLauncherBackground(0)
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == 0 && mLastReportedBackground == null) {
            updateLauncherBackground(1)
        }
    }

    override fun requestChildFocus(child: View?, focused: View?) {
        if (!isRowActive && child is NotificationCardView) {
            child.isSelectedAnimationDelayed = false
        }
        super.requestChildFocus(child, focused)
    }

    private fun updateLauncherBackground(type: Int) {
        var backgroundUri: String? = null
        var signature: String? = null
        if (visibility == View.VISIBLE || mNotificationListener == null) {
            val rowActive = isRowActive
            if (mNotificationListener != null && (rowActive || type == 1) && adapter != null && adapter!!.itemCount > 0) {
                val holder = findViewHolderForLayoutPosition(selectedPosition)
                if (holder != null) {
                    val child = holder.itemView
                    if (child is RecView) {
                        backgroundUri = child.wallpaperUri
                        signature = child.signature
                    }
                }
            }
            if (type == 2) {
                mLastReportedBackground = backgroundUri
                return
            } else if (type == 1 || mLastReportedBackground != backgroundUri) {
                mLastReportedBackground = backgroundUri
                if ((rowActive || mLastReportedBackground != null) && mNotificationListener != null) {
                    mNotificationListener?.onBackgroundImageChanged(mLastReportedBackground, signature)
                    return
                }
                return
            } else if (mLastReportedBackground == null && backgroundUri == null && rowActive && mNotificationListener != null) {
                mNotificationListener?.onBackgroundImageChanged(null, signature)
                return
            } else {
                return
            }
        }
        mLastReportedBackground = null
        mNotificationListener?.onBackgroundImageChanged(null, null)
    }

    init {
        setOnChildViewHolderSelectedListener(object : OnChildViewHolderSelectedListener() {
            override fun onChildViewHolderSelected(
                parent: RecyclerView,
                child: ViewHolder?,
                position: Int,
                subposition: Int
            ) {
                updateLauncherBackground(0)
                mNotificationListener?.onSelectedRecommendationChanged(position)
            }
        })
    }
}