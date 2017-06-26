package com.google.android.leanbacklauncher.recommendations;

import android.content.Context;
import android.content.Intent;
import com.google.android.leanbacklauncher.tvrecommendations.RecommendationsClient;

public abstract class SwitchingRecommendationsClient extends RecommendationsClient {
    private Context mContext;

    public SwitchingRecommendationsClient(Context context) {
        super(context);
        this.mContext = context;
    }

    protected Intent getServiceIntent() {
        Intent serviceIntent = super.getServiceIntent();
        if (serviceIntent == null) {
            return new Intent(this.mContext, RecommendationsService.class);
        }
        return serviceIntent;
    }
}
