package com.google.android.leanbacklauncher.settings;

import android.os.Bundle;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist.Guidance;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v17.leanback.widget.GuidedAction.Builder;
import android.support.v4.content.res.ResourcesCompat;
import com.google.android.leanbacklauncher.R;
import com.google.android.leanbacklauncher.settings.RecommendationsPreferenceManager.LoadBlacklistCountCallback;
import java.util.ArrayList;

public class LegacyHomeScreenPreferenceFragment extends GuidedStepFragment implements LoadBlacklistCountCallback {
    private int mBlacklistedPackageCount = -1;

    public Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new Guidance(getString(R.string.settings_dialog_title), null, "", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_settings_home, null));
    }

    public void onResume() {
        super.onResume();
        new RecommendationsPreferenceManager(getActivity()).loadBlacklistCount(this);
    }

    public void onBlacklistCountLoaded(int blacklistCount) {
        if (isAdded()) {
            this.mBlacklistedPackageCount = blacklistCount;
            ArrayList<GuidedAction> actions = new ArrayList();
            String description = null;
            if (this.mBlacklistedPackageCount != -1) {
                description = getResources().getQuantityString(R.plurals.recommendation_blacklist_action_description, this.mBlacklistedPackageCount, new Object[]{Integer.valueOf(this.mBlacklistedPackageCount)});
            }
            actions.add(((Builder) ((Builder) ((Builder) new Builder(getActivity()).id(1)).title((int) R.string.recommendation_blacklist_action_title)).description((CharSequence) description)).build());
            actions.add(((Builder) ((Builder) new Builder(getActivity()).id(2)).title((int) R.string.home_screen_order_action_title)).build());
            setActions(actions);
        }
    }

    public void onGuidedActionClicked(GuidedAction action) {
        switch ((int) action.getId()) {
            case 1:
                GuidedStepFragment.add(getFragmentManager(), new LegacyRecommendationsPreferenceFragment());
                return;
            case 2:
                GuidedStepFragment.add(getFragmentManager(), new LegacyAppsAndGamesPreferenceFragment());
                return;
            default:
                return;
        }
    }
}
