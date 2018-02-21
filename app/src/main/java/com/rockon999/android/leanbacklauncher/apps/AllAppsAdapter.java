package com.rockon999.android.leanbacklauncher.apps;

import android.content.Context;

public class AllAppsAdapter extends AppsAdapter {

    public AllAppsAdapter(Context context, LaunchPointListGenerator launchPointListGenerator, AppsRanker appsRanker, AppCategory... included) {
        super(context, launchPointListGenerator, appsRanker, false, included);

    }

    public AllAppsAdapter(Context context, LaunchPointListGenerator launchPointListGenerator, AppsRanker appsRanker) {
        super(context, launchPointListGenerator, appsRanker, false);
    }
}
