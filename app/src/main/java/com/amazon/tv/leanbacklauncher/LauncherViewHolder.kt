package com.amazon.tv.leanbacklauncher

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.amazon.tv.leanbacklauncher.core.LaunchException
import com.amazon.tv.leanbacklauncher.trace.AppTrace
import com.amazon.tv.leanbacklauncher.trace.AppTrace.TraceTag

abstract class LauncherViewHolder protected constructor(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
    @JvmField
    protected val mContext: Context = v.context
    private var mLaunchColor = 0
    private var mLaunchIntent: Intent? = null
    private var mLaunchTag: TraceTag? = null
    private var mLaunchTranslucent = false

    init {
        v.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        mLaunchTag = AppTrace.beginAsyncSection("LaunchAnimation")
        if (v === itemView) {
            (mContext as MainActivity).beginLaunchAnimation(view = v, translucent = mLaunchTranslucent, color = mLaunchColor) {
                AppTrace.endAsyncSection(mLaunchTag)
                try {
                    performLaunch()
                } catch (e: LaunchException) {
                    Log.e("LauncherViewHolder", "Could not perform launch:", e)
                    Toast.makeText(mContext, R.string.failed_launch, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    protected fun setLaunchTranslucent(launchTranslucent: Boolean) {
        mLaunchTranslucent = launchTranslucent
    }

    protected fun setLaunchColor(launchColor: Int) {
        mLaunchColor = launchColor
    }

    protected fun setLaunchIntent(launchIntent: Intent?) {
        mLaunchIntent = launchIntent
    }

    protected open fun performLaunch() {
        try {
            mContext.startActivity(mLaunchIntent)
            onLaunchSucceeded()
        } catch (t: Throwable) {
            //  LaunchException launchException = new LaunchException("Failed to launch intent: " + this.mLaunchIntent, t);
            t.printStackTrace()
        }
    }

    protected open fun onLaunchSucceeded() {}

}