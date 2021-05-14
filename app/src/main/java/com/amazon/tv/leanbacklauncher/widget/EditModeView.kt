package com.amazon.tv.leanbacklauncher.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.amazon.tv.leanbacklauncher.EditableAppsRowView
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.animation.EditModeUninstallAnimationHolder
import com.amazon.tv.leanbacklauncher.animation.ViewFocusAnimator
import com.amazon.tv.leanbacklauncher.apps.BannerSelectedChangedListener
import com.amazon.tv.leanbacklauncher.apps.BannerView
import com.amazon.tv.leanbacklauncher.apps.OnEditModeChangedListener
import com.amazon.tv.leanbacklauncher.graphics.ClipCircleDrawable
import com.amazon.tv.leanbacklauncher.util.Util.isConfirmKey
import java.util.*

class EditModeView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle), View.OnClickListener, BannerSelectedChangedListener,
    OnEditModeChangedListener {
    private val mActionListeners: ArrayList<EditModeViewActionListener?> =
        ArrayList<EditModeViewActionListener?>()
    private var mCurSelectedBanner: BannerView? = null
    var finishButton: Button? = null
        private set
    private var mUninstallAnimation: EditModeUninstallAnimationHolder? = null
    var uninstallApp: ImageView? = null
        private set
    var uninstallCircle: ImageView? = null
        private set
    var uninstallIcon: ImageView? = null
        private set
    var uninstallIconCircle: ImageView? = null
        private set
    private var mUninstallListener: OnEditModeUninstallPressedListener? = null
    var uninstallText: TextView? = null
        private set

    interface OnEditModeUninstallPressedListener {
        fun onUninstallPressed(str: String?)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        uninstallCircle = findViewById(R.id.uninstall_area_circle)
        uninstallIconCircle = findViewById(R.id.uninstall_icon_circle)
        uninstallIcon = findViewById(R.id.uninstall_icon)
        uninstallText = findViewById(R.id.uninstall_text)
        finishButton = findViewById(R.id.finish_button)
        uninstallApp = findViewById(R.id.uninstall_app_banner)
        uninstallApp?.let {
            val mFocusAnimator = ViewFocusAnimator(it)
            mFocusAnimator.setFocusImmediate(true)
        }
        val zDeltaIcon = resources.getDimensionPixelOffset(R.dimen.edit_uninstall_icon_z)
            .toFloat()
        uninstallApp?.z = resources.getDimensionPixelOffset(R.dimen.edit_app_banner_z).toFloat()
        uninstallIconCircle?.z = zDeltaIcon
        uninstallIcon?.z = zDeltaIcon
        uninstallApp?.clipToOutline = true
        finishButton?.setOnClickListener(this)
        uninstallText?.importantForAccessibility = 2
        setUninstallCircleLayout()
        setUninstallIconCircleLayout()
        setUninstallTextLayout()
        mUninstallAnimation = EditModeUninstallAnimationHolder(this)
    }

    override fun onEditModeChanged(z: Boolean) {
        var i = View.VISIBLE
        if (!z) {
            setBannerUninstallMode(false)
            if (hasFocus()) {
                notifyOnExitEditModeTriggered()
            }
        }
        if (!z) {
            i = View.GONE
        }
        visibility = i
        alpha = if (z) 1.0f else 0.0f
    }

    override fun onSelectedChanged(bannerView: BannerView, z: Boolean) {
        mCurSelectedBanner = if (z) {
            bannerView
        } else {
            null
        }
        var imageView = uninstallIconCircle
        var i: Int = if (z) {
            View.VISIBLE
        } else {
            View.GONE
        }
        imageView?.visibility = i

        val textView = uninstallText
        i = if (z) {
            View.VISIBLE
        } else {
            View.GONE
        }
        textView?.visibility = i

        imageView = uninstallIcon
        i = if (z) {
            View.VISIBLE
        } else {
            View.GONE
        }
        imageView?.visibility = i

        imageView = uninstallCircle
        i = if (z) {
            View.VISIBLE
        } else {
            View.GONE
        }
        imageView?.visibility = i
//        i = View.GONE
//        val button = finishButton
//        if (!z) {
//            i = View.VISIBLE
//        }
//        button?.visibility = i // useless DONE
    }

    fun clearUninstallAndFinishLayers() {
        uninstallIconCircle?.visibility = GONE
        uninstallText?.visibility = GONE
        uninstallIcon?.visibility = GONE
        uninstallCircle?.visibility = GONE
        finishButton?.visibility = GONE
    }

    fun addActionListener(listener: EditModeViewActionListener?) {
        mActionListeners.add(listener)
    }

    fun removeActionListener(listener: EditModeViewActionListener?) {
        mActionListeners.remove(listener)
    }

    fun requestUninstallIconFocus(curView: BannerView?, activeItems: EditableAppsRowView?) {
        uninstallIcon?.requestFocus()
        setBannerUninstallModeWithAnimation(true, curView, activeItems)
    }

    private fun setBannerUninstallMode(uninstallMode: Boolean) {
        mUninstallAnimation?.setViewsToExitState()
        setUninstallCircleLayout()
        setUninstallIconCircleLayout()
        setUninstallTextLayout()
        uninstallApp?.visibility =
            if (uninstallMode) View.VISIBLE else View.GONE
    }
    // FIXME
    fun setBannerUninstallModeWithAnimation(
        uninstallMode: Boolean,
        curView: BannerView?,
        activeItems: EditableAppsRowView?
    ) {
        if (uninstallMode) {
            mUninstallAnimation?.startAnimation(
                EditModeUninstallAnimationHolder.EditModeUninstallState.ENTER,
                curView,
                activeItems
            )
        } else {
            mUninstallAnimation?.startAnimation(
                EditModeUninstallAnimationHolder.EditModeUninstallState.EXIT,
                curView,
                activeItems
            )
        }
    }

    fun uninstallComplete() {
        setBannerUninstallMode(false)
        notifyUninstallComplete()
    }

    fun uninstallFailure() {
        setBannerUninstallMode(false)
        notifyUninstallFailure()
    }

    fun setBannerDrawable(drawable: Drawable?) {
        uninstallApp?.setImageDrawable(drawable)
    }

    fun setUninstallListener(listener: OnEditModeUninstallPressedListener?) {
        mUninstallListener = listener
    }

    private fun notifyOnExitEditModeTriggered() {
        for (mActionListener in mActionListeners) {
            (mActionListener as EditModeViewActionListener).onEditModeExitTriggered()
        }
    }

    private fun notifyOnFocusLeavingEditMode(from: Int) {
        for (mActionListener in mActionListeners) {
            (mActionListener as EditModeViewActionListener).onFocusLeavingEditModeLayer(from)
        }
    }

    private fun notifyUninstallComplete() {
        for (mActionListener in mActionListeners) {
            (mActionListener as EditModeViewActionListener).onUninstallComplete()
        }
    }

    private fun notifyUninstallFailure() {
        for (mActionListener in mActionListeners) {
            (mActionListener as EditModeViewActionListener).onUninstallFailure()
        }
    }

    private fun notifyPrepForUninstall(): String {
        var packageUninstalling = ""
        for (mActionListener in mActionListeners) {
            val result = (mActionListener as EditModeViewActionListener).onPrepForUninstall()
            if (!(result == null || result.isEmpty())) {
                packageUninstalling = result
            }
        }
        return packageUninstalling
    }

    fun onBackPressed() {
        mCurSelectedBanner?.let {
            it.notifyEditModeManager(false)
            it.isSelected = false
            return
        }
        notifyOnExitEditModeTriggered()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val action = event.action
        val keyCode = event.keyCode
        return if (action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP && finishButton!!.isFocused) {
                notifyOnFocusLeavingEditMode(0)
                true
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP && uninstallIcon!!.isFocused || keyCode == KeyEvent.KEYCODE_BACK && uninstallIcon!!.isFocused) {
                notifyOnFocusLeavingEditMode(1)
                true
            } else if ((isConfirmKey(keyCode) || keyCode == KeyEvent.KEYCODE_BACK) && finishButton!!.isFocused) {
                notifyOnExitEditModeTriggered()
                true
            } else if (!isConfirmKey(keyCode) || !uninstallIcon!!.isFocused) {
                true
            } else {
                mUninstallListener?.onUninstallPressed(notifyPrepForUninstall())
                true
            }
        } else if (action != KeyEvent.ACTION_UP) {
            super.dispatchKeyEvent(event)
        } else {
            true
        }
    }

    private fun setUninstallCircleLayout() {
        val circlelp = LayoutParams(
            resources.getDimensionPixelSize(R.dimen.edit_uninstall_area_circle_width),
            resources.getDimensionPixelOffset(R.dimen.edit_uninstall_area_circle_height)
        )
        circlelp.addRule(12)
        circlelp.addRule(13)
        uninstallCircle?.setImageDrawable(
            ClipCircleDrawable(
                ResourcesCompat.getColor(
                    resources,
                    R.color.edit_uninstall_area_color,
                    null
                )
            )
        )
        uninstallCircle?.layoutParams = circlelp
    }

    private fun setUninstallIconCircleLayout() {
        val iconCirclelp = LayoutParams(
            resources.getDimensionPixelSize(R.dimen.edit_uninstall_icon_circle_focused_size),
            resources.getDimensionPixelSize(R.dimen.edit_uninstall_icon_circle_focused_size)
        )
        iconCirclelp.setMargins(
            0,
            0,
            0,
            resources.getDimensionPixelSize(R.dimen.edit_uninstall_icon_circle_focused_bottom_margin)
        )
        iconCirclelp.addRule(2, R.id.uninstall_text)
        iconCirclelp.addRule(13)
        uninstallIconCircle?.setImageDrawable(
            ClipCircleDrawable(
                ResourcesCompat.getColor(
                    resources,
                    R.color.edit_uninstall_circle_color,
                    null
                )
            )
        )
        uninstallIconCircle?.layoutParams = iconCirclelp
    }

    private fun setUninstallTextLayout() {
        val textlp = LayoutParams(-2, -2)
        textlp.setMargins(
            0,
            0,
            0,
            resources.getDimensionPixelOffset(R.dimen.edit_uninstall_text_focused_bottom_margin)
        )
        textlp.addRule(14)
        textlp.addRule(12)
        uninstallText?.layoutParams = textlp
    }

    override fun onClick(v: View) {
        if (v === finishButton) {
            notifyOnExitEditModeTriggered()
        }
    }
}