package com.amazon.tv.firetv.leanbacklauncher.util

import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.os.Build
import com.amazon.tv.firetv.leanbacklauncher.apps.AppCategory

object AppCategorizer {

    val VIDEO_FILTER = arrayOf(
            "abema",
            "air.com.vudu",
            "android.rbtv",
            "appletv",
            "bbc.iplayer",
            "beeline.ott",
            "bloomberg",
            "cbs",
            "cn.tv",
            "com.canal.android",
            "com.google.android.tv",
            "com.lgi.orionandroid.tv",
            "com.nhl",
            "com.tvp.vod.tv",
            "com.vgtrk",
            "courville.nova",
            "ctcmediagroup.ctc",
            "dailymotion",
            "disney",
            "divantv",
            "dramafever",
            "dtv.tvx",
            "facebook",
            "firebat",
            "foxnews",
            "foxnow",
            "fujitv",
            "fxnow",
            "gyao",
            "haystack",
            "hbo",
            "hedwig",
            "hopwatch",
            "hulu",
            "imdb.tv",
            "iptvremote",
            "iqiyi",
            "khd.app",
            "kinopub",
            "kinotrend",
            "kodi",
            "lazycatsoft",
            "megafon.tv",
            "megogo",
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
            "pl.redefine.ipla",
            "pl.tvn.player.tv",
            "planeta.tv",
            "raintv",
            "redbull.rbtv",
            "ru.beeline.tv",
            "ru.cn.tv",
            "ru.ctv",
            "ru.ivi",
            "ru.kinopoisk",
            "ru.ntv",
            "ru.rutube",
            "ru.start",
            "ru.tv1",
            "ru.yourok",
            "schabi",
            "serials",
            "showtime",
            "simon.kaelae",
            "skyking",
            "smarttubetv",
            "smotreshka",
            "spb.tv",
            "spbtv",
            "starz",
            "ted",
            "tnt_premier",
            "tricoloronline",
            "tubitv",
            "tv.a24h",
            "tv.kartina",
            "tv.mts",
            "tv.ntvplus",
            "tv.okko",
            "tv.pluto",
            "tv.vintera",
            "tver",
            "tviztv.tviz",
            "tvplayer",
            "twitch",
            "twittertv",
            "txcom.vplayer",
            "ua.youtv",
            "ufc.android",
            "unext",
            "video",
            "viki",
            "viu.tv",
            "viutv",
            "wifire",
            "youtube",
            "yuriev.ndr"
    )
    val MUSIC_FILTER = arrayOf(
            "audials",
            "audioplayer",
            "deezer",
            "fmplay",
            "ituner",
            "music",
            "pandora",
            "radio",
            "somafm",
            "spotify",
            "tidal",
            "tunein",
            "zaycev"
    )
    val GAMES_FILTER = arrayOf(
            "android.play.games",
            "signaltalk",
            "tetris"
    )

    fun getAppCategory(pkgName: String?, actInfo: ActivityInfo?): AppCategory {
        pkgName?.let { pn ->
            for (s in VIDEO_FILTER) {
                if (pn.contains(s)) {
                    return AppCategory.VIDEO
                }
            }
            for (s in MUSIC_FILTER) {
                if (pn.contains(s)) {
                    return AppCategory.MUSIC
                }
            }
            for (s in GAMES_FILTER) {
                if (pn.contains(s)) {
                    return AppCategory.GAME
                }
            }
        }
        actInfo?.let { ai ->
            if (ai.applicationInfo.flags and ApplicationInfo.FLAG_IS_GAME != 0 ||
                    ai.applicationInfo.metaData != null && ai.applicationInfo.metaData.getBoolean("isGame", false)) {
                return AppCategory.GAME
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (ai.applicationInfo.category == ApplicationInfo.CATEGORY_GAME) {
                    return AppCategory.GAME
                } else if (ai.applicationInfo.category == ApplicationInfo.CATEGORY_VIDEO) {
                    return AppCategory.VIDEO
                } else if (ai.applicationInfo.category == ApplicationInfo.CATEGORY_AUDIO) {
                    return AppCategory.MUSIC
                }
            }
        }
        return AppCategory.OTHER
    }
}