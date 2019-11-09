package com.amazon.tv.firetv.leanbacklauncher.util;


import android.content.pm.ActivityInfo;

import com.amazon.tv.firetv.leanbacklauncher.apps.AppCategory;

public class AppCategorizer {

    public static AppCategory getAppCategory(String pkgName, ActivityInfo actInfo) {
        if (actInfo != null) {
            if ((actInfo.applicationInfo.flags & 33554432) != 0 || (actInfo.applicationInfo.metaData != null && actInfo.applicationInfo.metaData.getBoolean("isGame", false))) {
                return AppCategory.GAME;
            }
        }

        for (String s : MUSIC_FILTER) {
            if (pkgName.contains(s)) {
                return AppCategory.MUSIC;
            }
        }

        for (String s : VIDEO_FILTER) {
            if (pkgName.contains(s)) {
                return AppCategory.VIDEO;
            }
        }

        return AppCategory.OTHER;
    }

    public static final String[] VIDEO_FILTER = new String[]{
            "video",
            "netflix",
            "youtube",
            "twittertv",
            "hbo",
            "disney",
            "starz",
            "foxnews",
            "fxnow",
            "foxnow",
            "foxnews",
            "twitch",
            "mtv",
            "showtime",
            "dramafever",
            "cbs",
            "hulu",
            "cn.tv",
            "tvplayer",
            "niklabs.pp",
            "lazycatsoftware.lmd",
            "kinotrend",
            "megogo.tv",
            "khd.app",
            "serials"
    };

    public static final String[] MUSIC_FILTER = new String[]{
            "music",
            "spotify",
            "pandora",
            "deezer",
            "tunein",
            "somafm",
            "ituner",
            "fmplay",
            "tidal",
            "radio"
    };
}
