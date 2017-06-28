package com.rockchips.android.leanbacklauncher.recommendations;

import com.rockchips.android.leanbacklauncher.tvrecommendations.service.BaseRecommendationsService;

public class RecommendationsService extends BaseRecommendationsService {
    public RecommendationsService() {
        super(false, NotificationsService.class);
    }
}
