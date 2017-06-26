package com.google.android.leanbacklauncher.recommendations;

import com.google.android.leanbacklauncher.tvrecommendations.service.BaseRecommendationsService;

public class RecommendationsService extends BaseRecommendationsService {
    public RecommendationsService() {
        super(false, NotificationsService.class);
    }
}
