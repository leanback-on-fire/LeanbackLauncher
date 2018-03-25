package com.rockon999.android.leanbacklauncher.settings;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist.Guidance;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v17.leanback.widget.GuidedAction.Builder;
import android.support.v4.content.res.ResourcesCompat;

import com.rockon999.android.leanbacklauncher.R;
import com.rockon999.android.leanbacklauncher.apps.AppsManager;
import com.rockon999.android.leanbacklauncher.apps.AppsManager.SortingMode;

import java.util.ArrayList;

public class LegacyAppsAndGamesPreferenceFragment extends GuidedStepFragment {
    public Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new Guidance(getString(R.string.home_screen_order_content_title), getString(R.string.home_screen_order_content_description), getString(R.string.settings_dialog_title), ResourcesCompat.getDrawable(getResources(), R.drawable.ic_settings_home, null));
    }

    public void onResume() {
        super.onResume();
        ArrayList<GuidedAction> actions = new ArrayList<>();
        SortingMode sortingMode = AppsManager.getSavedSortingMode(getActivity());
        actions.add(new Builder(getActivity()).id(1).title((int) R.string.home_screen_order_content_title).description(sortingMode == SortingMode.FIXED ? R.string.select_app_order_action_description_fixed : R.string.select_app_order_action_description_recency).build());
        if (sortingMode == SortingMode.FIXED) {
            actions.add(new Builder(getActivity()).id(2).title((int) R.string.customize_app_order_action_title).build());
            actions.add(new Builder(getActivity()).id(3).title((int) R.string.customize_game_order_action_title).build());
        }
        setActions(actions);
    }

    public void onGuidedActionClicked(GuidedAction action) {
        Intent startMain;
        switch ((int) action.getId()) {
            case 1:
                GuidedStepFragment.add(getFragmentManager(), new LegacyAppOrderPreferenceFragment());
                return;
            case 2:
                startMain = new Intent("android.intent.action.MAIN");
                // startMain.addCategory("android.intent.category.HOME");
                startMain.setComponent(ComponentName.unflattenFromString("com.rockon999.android.leanbacklauncher/.MainActivity"));
                startMain.putExtra("extra_start_customize_apps", true);
                startActivity(startMain);
                return;
            case 3:
                startMain = new Intent("android.intent.action.MAIN");
                // startMain.addCategory("android.intent.category.HOME");
                startMain.setComponent(ComponentName.unflattenFromString("com.rockon999.android.leanbacklauncher/.MainActivity"));
                startMain.putExtra("extra_start_customize_games", true);
                startActivity(startMain);
                return;
            default:
                return;
        }
    }
}
