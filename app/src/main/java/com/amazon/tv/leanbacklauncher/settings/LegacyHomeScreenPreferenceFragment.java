package com.amazon.tv.leanbacklauncher.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.app.GuidedStepSupportFragment;
import android.support.v17.leanback.widget.GuidanceStylist.Guidance;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v17.leanback.widget.GuidedAction.Builder;
import android.support.v4.content.res.ResourcesCompat;

import com.amazon.tv.leanbacklauncher.R;

import java.util.ArrayList;

public class LegacyHomeScreenPreferenceFragment extends GuidedStepSupportFragment implements RecommendationsPreferenceManager.LoadBlacklistCountCallback {

    @NonNull
    public Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new Guidance(getString(R.string.settings_dialog_title), null, "", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_settings_home, null));
    }

    public void onResume() {
        super.onResume();

        new RecommendationsPreferenceManager(getActivity()).loadBlacklistCount(this);
    }

    public void onBlacklistCountLoaded(int blacklistCount) {
        if (isAdded()) {
            ArrayList<GuidedAction> actions = new ArrayList<>();

            String description = null;
            if (blacklistCount != -1) {
                description = getResources().getQuantityString(R.plurals.recommendation_blacklist_action_description, blacklistCount, new Object[]{Integer.valueOf(blacklistCount)});
            }
            actions.add(new Builder(getActivity()).id(1).title((int) R.string.recommendation_blacklist_action_title).description((CharSequence) description).build());
            actions.add(new Builder(getActivity()).id(2).title((int) R.string.home_screen_order_action_title).build());
            actions.add(new Builder(getActivity()).id(3).title(R.string.hidden_applications_title).build());
            actions.add(new Builder(getActivity()).id(4).title(R.string.wallpaper_title).description(R.string.select_wallpaper_action_desc).build());
            actions.add(new Builder(getActivity()).id(5).title(R.string.banners_prefs_title).description(R.string.banners_prefs_desc).build());
            setActions(actions);
        }
    }

    public void onGuidedActionClicked(GuidedAction action) {
        switch ((int) action.getId()) {
            case 1:
                GuidedStepSupportFragment.add(getFragmentManager(), new LegacyRecommendationsPreferenceFragment());
                return;
            case 2:
                GuidedStepSupportFragment.add(getFragmentManager(), new LegacyAppsAndGamesPreferenceFragment());
                return;
            case 3:
                GuidedStepSupportFragment.add(getFragmentManager(), new LegacyHiddenPreferenceFragment());
                return;
            case 4:
                GuidedStepSupportFragment.add(getFragmentManager(), new LegacyWallpaperFragment());
                return;
            case 5:
                GuidedStepSupportFragment.add(getFragmentManager(), new LegacyBannersFragment());
                return;
            default:
                return;
        }
    }
}
