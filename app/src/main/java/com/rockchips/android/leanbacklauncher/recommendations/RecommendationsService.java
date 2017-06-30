package com.rockchips.android.leanbacklauncher.recommendations;

import android.util.Log;

import com.rockchips.android.leanbacklauncher.tvrecommendations.service.BaseRecommendationsService;

public class RecommendationsService extends BaseRecommendationsService {
    private static final String TAG = "RecommendationsService";
    public RecommendationsService() {
        super(false, NotificationsService.class);
        Log.i(TAG, "RecommendationsService  constructor");
    }
}
