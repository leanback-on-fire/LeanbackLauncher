package com.amazon.tv.leanbacklauncher.notifications

import android.app.PendingIntent
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amazon.tv.leanbacklauncher.BuildConfig
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.apps.AppsAdapter.AppBannerViewHolder
import com.amazon.tv.leanbacklauncher.core.LaunchException
import com.amazon.tv.leanbacklauncher.notifications.PartnerAdapter.PartnerBannerViewHolder
import com.amazon.tv.leanbacklauncher.util.Util.startActivity
import com.amazon.tv.tvrecommendations.TvRecommendation

class PartnerAdapter(context: Context, private val mListener: BlacklistListener?) :
    NotificationsServiceAdapter<PartnerBannerViewHolder?>(context, 15000, 60000) {
    class PartnerBannerViewHolder(v: View?) : AppBannerViewHolder(v!!, null) {
        private var mIntent: PendingIntent? = null
        private val TAG by lazy { if (BuildConfig.DEBUG) ("[*]" + javaClass.simpleName).take(21) else javaClass.simpleName }

        fun init(
            title: CharSequence?,
            banner: Drawable?,
            intent: PendingIntent?,
            launchColor: Int
        ) {
            super.init(title, banner, launchColor)
            mIntent = intent
        }

        override fun performLaunch() {
            if (mIntent != null) {
                try {
                    startActivity(mContext, mIntent!!)
                } catch (e: Exception) {
                    e.message?.let { Log.e(TAG, "Could not launch partner intent, $it ") }
                }
            } else {
                throw LaunchException("No partner intent to launch: $packageName")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartnerBannerViewHolder {
        return PartnerBannerViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.app_banner, parent, false)
        )
    }

    override fun onBindViewHolder(appHolder: PartnerBannerViewHolder, position: Int) {
        if (position < itemCount) {
            val recommendation = getRecommendation(position)
            recommendation?.let {
                appHolder.init(
                    recommendation.title,
                    BitmapDrawable(mContext.resources, recommendation.contentImage),
                    recommendation.contentIntent,
                    recommendation.color
                )
            }
        }
    }

    override val isPartnerClient: Boolean
        get() = true

    override fun onNewRecommendation(rec: TvRecommendation) {
        val pkgName = rec.replacedPackageName
        if (pkgName.isNotEmpty() && mListener != null) {
            mListener.onPackageBlacklisted(pkgName)
        }
    }

    override fun onRecommendationRemoved(rec: TvRecommendation?) {
        val pkgName = rec?.replacedPackageName
        if (!pkgName.isNullOrEmpty() && mListener != null) {
            mListener.onPackageUnblacklisted(pkgName)
        }
    }
}