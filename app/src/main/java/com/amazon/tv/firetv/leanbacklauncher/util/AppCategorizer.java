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
            "air.com.vudu",
            "android.rbtv",
            "appletv",
            "beeline.ott",
            "bloomberg",
            "cbs",
            "cn.tv",
            "com.nhl",
            "com.vgtrk",
            "ctcmediagroup.ctc",
            "disney",
            "divantv",
            "dramafever",
            "firebat",
            "foxnews",
            "foxnow",
            "fxnow",
            "haystack",
            "hbo",
            "hedwig",
            "hulu",
            "iptvremote",
            "khd.app",
            "kinopub",
            "kinotrend",
            "lazycatsoft",
            "megogo.tv",
            "moviesanywhere",
            "mts.mtstv",
            "mtv",
            "mubi",
            "netflix",
            "niklabs.pp",
            "ontv",
            "ott_play",
            "ottnavigator",
            "ottplay",
            "planeta.tv",
            "raintv",
            "redbull.rbtv",
            "ru.beeline.tv",
            "ru.cn.tv",
            "ru.ctv",
            "ru.ivi",
            "ru.ntv",
            "ru.rutube",
            "ru.start",
            "ru.tv1",
            "serials",
            "showtime",
            "simon.kaelae",
            "smotreshka",
            "spb.tv",
            "spbtv",
            "starz",
            "tnt_premier",
            "tricoloronline",
            "tubitv",
            "tv.kartina",
            "tv.mts",
            "tv.ntvplus",
            "tv.okko",
            "tv.pluto",
            "tv.vintera",
            "tviztv.tviz",
            "tvplayer",
            "twitch",
            "twittertv",
            "ua.youtv",
            "ufc.android",
            "video",
            "viu.tv",
            "viutv",
            "youtube"
    };

    public static final String[] MUSIC_FILTER = new String[]{
            "deezer",
            "fmplay",
            "iplayer",
            "ituner",
            "music",
            "pandora",
            "radio",
            "somafm",
            "spotify",
            "tidal",
            "tunein",
            "zaycev"
    };
}
