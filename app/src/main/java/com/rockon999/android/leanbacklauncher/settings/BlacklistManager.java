package com.rockon999.android.leanbacklauncher.settings;

import android.content.Context;

import java.util.List;

public class BlacklistManager {
    private Context mContext;


    public interface LoadRecommendationPackagesCallback {
        void onRecommendationPackagesLoaded(String[] strArr, String[] strArr2);
    }

    public BlacklistManager(Context context) {
        this.mContext = context;
    }

    public void saveEntityBlacklist(List<String> keys) {
    }

    public void loadRecommendationPackages(LoadRecommendationPackagesCallback callback) {
    }
}
