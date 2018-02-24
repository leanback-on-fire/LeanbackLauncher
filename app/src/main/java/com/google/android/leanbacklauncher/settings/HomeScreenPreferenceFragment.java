package com.google.android.leanbacklauncher.settings;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.google.android.leanbacklauncher.R;
import com.google.android.leanbacklauncher.settings.RecommendationsPreferenceManager.LoadBlacklistCountCallback;

public class HomeScreenPreferenceFragment extends LeanbackPreferenceFragment implements LoadBlacklistCountCallback {
    private Preference mRecommendationsPref;

    public static Fragment newInstance() {
        return new HomeScreenPreferenceFragment();
    }

    public void onCreatePreferences(Bundle bundle, String s) {
        Context preferenceContext = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(preferenceContext);
        screen.setTitle((int) R.string.settings_dialog_title);
        this.mRecommendationsPref = new Preference(preferenceContext);
        this.mRecommendationsPref.setTitle((int) R.string.recommendation_blacklist_action_title);
        this.mRecommendationsPref.setFragment(RecommendationsPreferenceFragment.class.getName());
        screen.addPreference(this.mRecommendationsPref);
        Preference appsAndGames = new Preference(preferenceContext);
        appsAndGames.setTitle((int) R.string.home_screen_order_action_title);
        appsAndGames.setFragment(AppsAndGamesPreferenceFragment.class.getName());
        screen.addPreference(appsAndGames);
        setPreferenceScreen(screen);
    }

    public void onResume() {
        super.onResume();
        new RecommendationsPreferenceManager(getActivity()).loadBlacklistCount(this);
    }

    public void onBlacklistCountLoaded(int blacklistCount) {
        if (isAdded()) {
            this.mRecommendationsPref.setSummary(getResources().getQuantityString(R.plurals.recommendation_blacklist_action_description, blacklistCount, new Object[]{Integer.valueOf(blacklistCount)}));
        }
    }
}
