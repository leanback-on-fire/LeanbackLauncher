package com.rockchips.android.leanbacklauncher.settings;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;

import com.rockchips.android.leanbacklauncher.R;

public class HomeScreenPreferenceFragment extends LeanbackPreferenceFragment implements BlacklistManager.LoadRecommendationPackagesCallback {
    private BlacklistManager mBlacklistManager;
    private Preference mRecommendationsPref;

    public static Fragment newInstance() {
        return new HomeScreenPreferenceFragment();
    }

    public void onCreatePreferences(Bundle bundle, String s) {
        Context preferenceContext = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(preferenceContext);
        screen.setTitle(R.string.settings_dialog_title);
        this.mRecommendationsPref = new Preference(preferenceContext);
        this.mRecommendationsPref.setTitle(R.string.recommendation_blacklist_action_title);
        this.mRecommendationsPref.setFragment(RecommendationsPreferenceFragment.class.getName());
        screen.addPreference(this.mRecommendationsPref);
        Preference appsAndGames = new Preference(preferenceContext);
        appsAndGames.setTitle(R.string.home_screen_order_action_title);
        appsAndGames.setFragment(AppsAndGamesPreferenceFragment.class.getName());
        screen.addPreference(appsAndGames);
        setPreferenceScreen(screen);
    }

    public void onStart() {
        super.onStart();
        this.mBlacklistManager = new BlacklistManager(getActivity());
        this.mBlacklistManager.loadRecommendationPackages(this);
    }

    public void onRecommendationPackagesLoaded(String[] recommendationPackaged, String[] blacklistedPackages) {
        this.mRecommendationsPref.setSummary(getResources().getQuantityString(R.plurals.recommendation_blacklist_action_description, blacklistedPackages.length, new Object[]{Integer.valueOf(blacklistedPackages.length)}));
    }
}
