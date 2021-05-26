package com.amazon.tv.leanbacklauncher.recommendations

import android.content.Context
import android.content.Intent
import androidx.annotation.Keep
import com.amazon.tv.tvrecommendations.RecommendationsClient

abstract class SwitchingRecommendationsClient(private val mContext: Context?) :
    RecommendationsClient(
        mContext
    ) {
    private val mDelegate: Delegate? = null

    interface Delegate {
        val serviceIntent: Intent?
        fun release()
    }

    override fun getServiceIntent(): Intent {
        var serviceIntent: Intent? = null
        mDelegate?.let {
            serviceIntent = mDelegate.serviceIntent
        }
        return serviceIntent ?: Intent(this.mContext, RecommendationsService::class.java)
    }

    @Keep
    fun reconnect() {
        disconnect()
        connect()
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        mDelegate?.release()
        //super.finalize()
    }
}