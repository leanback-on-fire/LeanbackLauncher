package com.amazon.tv.firetv.leanbacklauncher.util

import com.amazon.tv.leanbacklauncher.R
import java.util.*

object BannerUtil {
    val BANNER_OVERRIDES: MutableMap<String, Int> = HashMap()

    init {
        BANNER_OVERRIDES["com.amazon.tv.launcher"] = R.drawable.banner_amazon_launcher
        BANNER_OVERRIDES["com.amazon.ssm"] = R.drawable.banner_amazon_ssm
        BANNER_OVERRIDES["amazon.venezia"] = R.drawable.banner_amazon_apps
        BANNER_OVERRIDES["bueller.music"] = R.drawable.banner_amazon_music
        BANNER_OVERRIDES["bueller.photos"] = R.drawable.banner_amazon_photo
        BANNER_OVERRIDES["com.amazon.avod"] = R.drawable.banner_amazon_prime
        BANNER_OVERRIDES["com.amazon.hedwig"] = R.drawable.banner_hedwig
        BANNER_OVERRIDES["com.amazon.cloud9"] = R.drawable.banner_silk
        BANNER_OVERRIDES["com.amazon.imdb.tv.android.app"] = R.drawable.banner_amazon_imdbtv
        BANNER_OVERRIDES["com.niklabs.pp"] = R.drawable.banner_pp
        BANNER_OVERRIDES["de.radio.android"] = R.drawable.banner_radio_net
        BANNER_OVERRIDES["hulu"] = R.drawable.banner_hulu
        BANNER_OVERRIDES["org.mozilla.tv.firefox"] = R.drawable.banner_firefox
        BANNER_OVERRIDES["ru.cn.tv"] = R.drawable.banner_peerstv
        BANNER_OVERRIDES["showtime"] = R.drawable.banner_showtime
        BANNER_OVERRIDES["tunein.player"] = R.drawable.banner_tunein
        BANNER_OVERRIDES["ru.fmplay"] = R.drawable.banner_fmplay
        BANNER_OVERRIDES["tv2x2"] = R.drawable.banner_2x2
        BANNER_OVERRIDES["com.jozein.xedge"] = R.drawable.banner_xedge
        BANNER_OVERRIDES["com.topjohnwu.magisk"] = R.drawable.banner_magisk
    }
}
