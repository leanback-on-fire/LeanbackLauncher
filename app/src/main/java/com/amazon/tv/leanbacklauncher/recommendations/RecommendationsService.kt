package com.amazon.tv.leanbacklauncher.recommendations

import com.amazon.tv.tvrecommendations.service.BaseRecommendationsService

class RecommendationsService : BaseRecommendationsService(
    false,
    NotificationsServiceV4::class.java,
    GservicesRankerParameters.Factory()
)