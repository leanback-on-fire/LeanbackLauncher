package com.amazon.tv.leanbacklauncher.notifications;

import android.text.TextUtils;

import com.amazon.tv.tvrecommendations.TvRecommendation;

class NotificationUtils {
    public static boolean equals(TvRecommendation left, TvRecommendation right) {
        if (left == null || right == null) {
            return left == right;
        } else return TextUtils.equals(left.getKey(), right.getKey());
    }
}
