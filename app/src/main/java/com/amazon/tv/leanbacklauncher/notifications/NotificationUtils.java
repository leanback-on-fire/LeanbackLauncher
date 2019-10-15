package com.amazon.tv.leanbacklauncher.notifications;

import android.text.TextUtils;
import com.amazon.tv.tvrecommendations.TvRecommendation;

class NotificationUtils {
    public static boolean equals(TvRecommendation left, TvRecommendation right) {
        if (left == null || right == null) {
            if (left == right) {
                return true;
            }
            return false;
        } else if (TextUtils.equals(left.getKey(), right.getKey())) {
            return true;
        } else {
            return false;
        }
    }
}
