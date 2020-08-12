package com.amazon.tv.leanbacklauncher.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.leanback.app.GuidedStepSupportFragment;
import androidx.leanback.widget.GuidanceStylist.Guidance;
import androidx.leanback.widget.GuidedAction;
import androidx.leanback.widget.GuidedAction.Builder;

import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.leanbacklauncher.apps.AppsManager;
import com.amazon.tv.leanbacklauncher.apps.AppsManager.SortingMode;

import java.util.ArrayList;

public class LegacyAppsAndGamesPreferenceFragment extends GuidedStepSupportFragment {
    @NonNull
    public Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new Guidance(getString(R.string.home_screen_order_action_title), getString(R.string.home_screen_order_content_description), getString(R.string.settings_dialog_title), ResourcesCompat.getDrawable(getResources(), R.drawable.ic_settings_home, null));
    }

    public void onResume() {
        super.onResume();
        ArrayList<GuidedAction> actions = new ArrayList<>();
        SortingMode sortingMode = AppsManager.getSavedSortingMode(getActivity());
        actions.add(new Builder(getActivity()).id(1).title(R.string.edit_row_title).description(R.string.select_app_customize_rows_title).build());
        actions.add(new Builder(getActivity()).id(2).title(R.string.home_screen_order_content_title).description(sortingMode == SortingMode.FIXED ? R.string.select_app_order_action_description_fixed : R.string.select_app_order_action_description_recency).build());
        // BROKEN
        //if (sortingMode == SortingMode.FIXED) {
        //    actions.add(new Builder(getActivity()).id(2).title((int) R.string.customize_app_order_action_title).build());
        //    actions.add(new Builder(getActivity()).id(3).title((int) R.string.customize_game_order_action_title).build());
        //}
        actions.add(new Builder(getActivity()).id(3).title(R.string.banners_prefs_title).description(R.string.banners_prefs_desc).build());
        actions.add(new Builder(getActivity()).id(4).title(R.string.wallpaper_title).description(R.string.select_wallpaper_action_desc).build());

        setActions(actions);
    }

    public void onGuidedActionClicked(GuidedAction action) {
        Intent startMain;
        switch ((int) action.getId()) {
            case 1:
                GuidedStepSupportFragment.add(getFragmentManager(), new LegacyAppRowPreferenceFragment());
                return;
            case 2:
                GuidedStepSupportFragment.add(getFragmentManager(), new LegacyAppOrderPreferenceFragment());
                return;
            case 3:
                GuidedStepSupportFragment.add(getFragmentManager(), new LegacyBannersFragment());
                return;
            case 4:
                GuidedStepSupportFragment.add(getFragmentManager(), new LegacyWallpaperFragment());
                return;
            default:
                return;
        }
    }
}
