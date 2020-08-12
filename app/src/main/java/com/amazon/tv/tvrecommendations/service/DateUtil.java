package com.amazon.tv.tvrecommendations.service;

import android.content.Context;
import android.preference.PreferenceManager;

import java.util.Calendar;
import java.util.Date;

class DateUtil {
    public static int getDay(Date date) {
        if (date == null) {
            return -1;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return (cal.get(1) * 1000) + cal.get(6);
    }

    public static Date getDate(int day) {
        if (day == -1) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.set(1, day / 1000);
        cal.set(6, day % 1000);
        return cal.getTime();
    }

    public static boolean initialRankingApplied(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("recommendations_oob_ranking_marker", false);
    }

    public static void setInitialRankingAppliedFlag(Context ctx, boolean applied) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putBoolean("recommendations_oob_ranking_marker", applied).apply();
    }
}
