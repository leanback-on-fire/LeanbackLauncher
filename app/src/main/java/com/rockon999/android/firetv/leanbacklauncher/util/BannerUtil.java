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
    }
}
