package com.amazon.tv.leanbacklauncher.recommendations;

import com.amazon.tv.leanbacklauncher.recommendations.GservicesRankerParameters.Factory;
import com.amazon.tv.tvrecommendations.service.BaseRecommendationsService;

public class RecommendationsService extends BaseRecommendationsService {
    public RecommendationsService() {
        super(false, NotificationsServiceV4.class, new Factory());
    }
}
