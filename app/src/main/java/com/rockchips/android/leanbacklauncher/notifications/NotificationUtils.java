package com.rockchips.android.leanbacklauncher.notifications;

import android.text.TextUtils;
import com.rockchips.android.leanbacklauncher.tvrecommendations.TvRecommendation;

class NotificationUtils {
    private NotificationUtils() {
    }

    public static boolean equals(TvRecommendation left, TvRecommendation right) {
        boolean z = true;
        if (left != null && right != null) {
            return TextUtils.equals(left.getKey(), right.getKey());
        } else {
            if (left != right) {
                z = false;
            }
            return z;
        }
    }

    public static boolean isUpdate(TvRecommendation left, TvRecommendation right) {
        return TextUtils.equals(left.getKey(), right.getKey());
    }
}
