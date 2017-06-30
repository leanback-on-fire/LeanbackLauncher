package com.rockchips.android.leanbacklauncher.recommendations;

import android.util.Log;

import com.rockchips.android.leanbacklauncher.tvrecommendations.service.BaseNotificationsService;

public class NotificationsService extends BaseNotificationsService {
    private static final String TAG = "NotificationsService";
    public NotificationsService() {
        super(false);
        Log.i(TAG, "NotificationsService constructor");
    }
}
