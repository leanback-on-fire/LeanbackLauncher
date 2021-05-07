package com.amazon.tv.leanbacklauncher.notifications

import android.text.TextUtils
import com.amazon.tv.tvrecommendations.TvRecommendation

internal object NotificationUtils {
    @JvmStatic
    fun equals(left: TvRecommendation?, right: TvRecommendation?): Boolean {
        return if (left == null || right == null) {
            left == right
        } else left.key.equals(right.key)
    }
}