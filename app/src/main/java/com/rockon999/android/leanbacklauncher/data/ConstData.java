package com.rockon999.android.leanbacklauncher.data;

import android.content.Context;
import android.graphics.Color;

import com.rockon999.android.leanbacklauncher.R;

import java.util.HashMap;
import java.util.Map;

import momo.cn.edu.fjnu.androidutils.data.CommonValues;

/**
 * Created by GaoFei on 2017/6/6.
 * Updated by rockon999 on 2018/2/15
 */

public class ConstData {
    public static Context appContext;
    public static final String DB_NAME = "onfire_tvlauncher.db";
    public static final String DB_DIRECTORY = CommonValues.application.getFilesDir().getPath();
    public static final String CACHE_IMG_DIR = CommonValues.application.getFilesDir() + "/imageCache/";
    public static final int DB_VERSION = 1;
    public static final int[] APP_ITEM_BACK_COLORS = {
            Color.parseColor("#008eae"), Color.parseColor("#00c2d0"), Color.parseColor("#febc59"),
            Color.parseColor("#aabeeb"), Color.parseColor("#59e5fe"),
            Color.parseColor("#ebaaaa"), Color.parseColor("#aacaeb"),
            Color.parseColor("#c1aaeb"), Color.parseColor("#ebaae9"), Color.parseColor("#6ae0b2"),
            Color.parseColor("#54c1d5"), Color.parseColor("#34a9cf"), Color.parseColor("#00d0bf"),
            Color.parseColor("#009dd0"),
            Color.parseColor("#fdad59"), Color.parseColor("#fe8459"), Color.parseColor("#fe599f"),
            Color.parseColor("#fe5959")};
    public static final String[] DEFAULT_FAVORITE_PACKAGES = {"com.google.android.youtube.tv"};
    public static final String[] DEFAULT_RECOMMEND_ACTIVITIES = {
            "com.google.android.youtube.tv/com.google.android.apps.youtube.tv.activity.TvGuideActivity"};
    public static final String SETTINGS_PACKAGE = "com.amazon.tv.settings";

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

    public interface AppType {
        int DEFAULT = 0;
        int SETTINGS = 2;
        int ALL = 3;
        int FAVORITE = 4;
        int VIDEO = 6;
        int MUSIC = 7;
        int GAME = 8;

    }

    public interface RowType {
        int SYSTEM_UI = 0;
        int ALL_APPS = 3;
        int SETTINGS = 5;
        int FAVORITE = 8;
        int VIDEO = 9;
        int MUSIC = 10;
        int INPUTS = 6;
    }

    public static final String[] VIDEO_FILTER = new String[]{
            "video",
            "netflix",
            "youtube",
            "twitter",
            "twittertv",
            "hbo",
            "disney",
            "starz",
            "foxnews",
            "fx",
            "fox",
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

    public interface SharedKey {
        String IS_FIRST_LOAD_FAVORITE_APP = "is_first_load_favorite_app";
    }
}
