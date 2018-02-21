package com.rockon999.android.leanbacklauncher.util;

import android.graphics.Color;

import com.rockon999.android.leanbacklauncher.R;

import java.util.HashMap;
import java.util.Map;

public class ConstData {
    public static final String CACHE_IMG_DIR = "imgCache";

    public static final int[] APP_ITEM_BACK_COLORS = {
            Color.parseColor("#008eae"), Color.parseColor("#00c2d0"), Color.parseColor("#febc59"),
            Color.parseColor("#aabeeb"), Color.parseColor("#59e5fe"),
            Color.parseColor("#ebaaaa"), Color.parseColor("#aacaeb"),
            Color.parseColor("#c1aaeb"), Color.parseColor("#ebaae9"), Color.parseColor("#6ae0b2"),
            Color.parseColor("#54c1d5"), Color.parseColor("#34a9cf"), Color.parseColor("#00d0bf"),
            Color.parseColor("#009dd0"),
            Color.parseColor("#fdad59"), Color.parseColor("#fe8459"), Color.parseColor("#fe599f"),
            Color.parseColor("#fe5959")};

    public static final int APP_ROW_COUNT = 10;
    public static final int APP_COL_COUNT = 10;

    public static final int WIFI_MAX_STRENGTH = 5;

    public static final Map<String, Integer> BANNER_OVERRIDES = new HashMap<>();

    static {
        BANNER_OVERRIDES.put("hulu", R.drawable.hulu);
        BANNER_OVERRIDES.put("bueller.music", R.drawable.amazon_music);
    }

    public interface NetworkState {
        int NO = 0;
        int WIFI = 1;
        int ETHERNET = 2;
    }

    public interface RowType {
        int SYSTEM_UI = 0;
        int ALL_APPS = 3;
        int SETTINGS = 5;
        int FAVORITE = 8;
        int VIDEO = 9;
        int MUSIC = 10;
        int INPUTS = 6;
        int GAMES = 15;
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
            "hulu"
    };

    public static final String[] MUSIC_FILTER = new String[]{
            "music",
            "spotify",
            "pandora"
    };
}
