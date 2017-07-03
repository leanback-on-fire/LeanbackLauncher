package com.rockchips.android.leanbacklauncher.data;

import android.content.Context;
import android.graphics.Color;

import momo.cn.edu.fjnu.androidutils.data.CommonValues;

/**
 * Created by GaoFei on 2017/6/6.
 * 常量数据
 */

public class ConstData {
    public static  Context appContext;
    public static final String DB_NAME = "rk_tvlauncher.db";
    public static final String DB_DIRECTORY = CommonValues.application.getFilesDir().getPath();
    public static final int DB_VERSION = 1;
    public static final int[] APP_ITEM_BACK_COLORS = { Color.parseColor("#ebdaaa"),
            Color.parseColor("#008eae"), Color.parseColor("#00c2d0"), Color.parseColor("#febc59"),
            Color.parseColor("#bae3ce"), Color.parseColor("#aabeeb"), Color.parseColor("#59e5fe"),
            Color.parseColor("#ebaaaa"), Color.parseColor("#aae4eb"), Color.parseColor("#aacaeb"),
            Color.parseColor("#c1aaeb"), Color.parseColor("#ebaae9"), Color.parseColor("#6ae0b2"),
            Color.parseColor("#54c1d5"), Color.parseColor("#34a9cf"), Color.parseColor("#00d0bf"),
            Color.parseColor("#009dd0"), Color.parseColor("#e3dcba"), Color.parseColor("#c4e3ba"),
            Color.parseColor("#fdad59"), Color.parseColor("#fe8459"), Color.parseColor("#fe599f"),
            Color.parseColor("#fe5959"), Color.parseColor("#edecea"), Color.parseColor("#afd6ed")};
    public static final String[] DEFAULT_RECOMMEND_PACKAGES = {"com.rockchips.mediacenter", "org.xbmc.rkmc"};
    public static final String SETTINGS_PACKAGE = "com.android.tv.settings";
    public static final String SETTINGS_ACTIVITY = "com.android.tv.settings.MainSettings";
    /**
     * APP栏显示个数
     */
    public static final int APP_ROW_COUNT = 10;
    public static final int APP_COL_COUNT = 10;
    /**
     * WIFI最大信号强度
     */
    public static final int WIFI_MAX_STRENGTH = 5;
    public interface NetWorkState{
        int NO = 0;
        int WIFI = 1;
        int ETHERNET = 2;
    }


    public interface  AppType{
        int MY_RECOMMAND = 0;
        int VIDEO = 1;
        int MUSIC = 2;
        int ALL = 3;
    }
}
