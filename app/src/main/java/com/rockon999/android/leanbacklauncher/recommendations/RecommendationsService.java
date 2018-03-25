package com.rockon999.android.leanbacklauncher.recommendations;

import com.rockon999.android.leanbacklauncher.recommendations.GservicesRankerParameters.Factory;
import com.rockon999.android.tvrecommendations.service.BaseRecommendationsService;

public class RecommendationsService extends BaseRecommendationsService {
    public RecommendationsService() {
        super(false, NotificationsServiceV4.class, new Factory());
    }
}
