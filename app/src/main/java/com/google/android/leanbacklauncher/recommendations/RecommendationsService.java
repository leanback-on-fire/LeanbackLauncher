package com.google.android.leanbacklauncher.recommendations;

import com.google.android.leanbacklauncher.recommendations.GservicesRankerParameters.Factory;
import com.google.android.tvrecommendations.service.BaseRecommendationsService;

public class RecommendationsService extends BaseRecommendationsService {
    public RecommendationsService() {
        super(false, NotificationsService.class, new Factory());
    }
}
