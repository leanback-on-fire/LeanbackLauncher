package com.rockon999.android.firetv.leanbacklauncher.util;

import com.rockon999.android.leanbacklauncher.R;

import java.util.HashMap;
import java.util.Map;


public class BannerUtil {
    public static final Map<String, Integer> BANNER_OVERRIDES = new HashMap<>();

    static {
        BANNER_OVERRIDES.put("hulu", R.drawable.banner_hulu);
        BANNER_OVERRIDES.put("bueller.music", R.drawable.banner_amazon_music);
        BANNER_OVERRIDES.put("showtime", R.drawable.banner_showtime);
        BANNER_OVERRIDES.put("com.amazon.avod", R.drawable.banner_amazon_prime);
        BANNER_OVERRIDES.put("tunein.player", R.drawable.banner_tunein);
        BANNER_OVERRIDES.put("org.mozilla.tv.firefox", R.drawable.banner_firefox);
        BANNER_OVERRIDES.put("com.google.android.youtube.tv", R.drawable.banner_youtube);
        BANNER_OVERRIDES.put("ru.yourok.torrserve", R.drawable.banner_torrserve);
        BANNER_OVERRIDES.put("com.niklabs.pp", R.drawable.banner_pp);
        BANNER_OVERRIDES.put("ru.cn.tv", R.drawable.banner_peerstv);
    }
}
