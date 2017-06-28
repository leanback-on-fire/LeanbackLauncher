package com.rockchips.android.leanbacklauncher;

import android.app.Application;

import com.rockchips.android.leanbacklauncher.data.ConstData;

public class LauncherApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ConstData.appContext = this;
    }
}
