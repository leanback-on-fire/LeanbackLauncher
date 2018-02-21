package com.rockon999.android.leanbacklauncher.settings;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.rockon999.android.leanbacklauncher.R;
import com.rockon999.android.leanbacklauncher.apps.AppsPreferences;
import com.rockon999.android.leanbacklauncher.apps.AppsPreferences.SortingMode;
import com.rockon999.android.leanbacklauncher.recline.app.DialogFragment;
import com.rockon999.android.leanbacklauncher.recline.app.DialogFragment.Action;
import com.rockon999.android.leanbacklauncher.recline.app.DialogFragment.Action.Listener;
import com.rockon999.android.leanbacklauncher.recline.app.DialogFragment.Builder;

import java.util.ArrayList;

public class FullScreenSettingsActivity extends Activity { // implements Listener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_fragment);
    }
    /*private boolean mAppOrderChanged;
    private String mClickedActionTitle;
    private boolean mSaved;

    public FullScreenSettingsActivity() {
        this.mSaved = true;
        this.mAppOrderChanged = true;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.settings_dialog_bg_protection));

        DialogFragment.add(getFragmentManager(), buildFragment(getString(R.string.settings_dialog_title), R.drawable.ic_settings_home, new Builder().actions(getBaseActions())));
    }

    @Override
    public void onBackPressed() {

    }

    public void onActionClicked(Action action) {
        String key = action.getKey();
        switch (key) {
            case "homeScreenOrder":
                showHomeScreenOrderDialog();
                break;
            case "appOrder":
                showSelectAppOrderDialog();
                break;
            case "recency":
                checkAndSetSortMode(action, SortingMode.RECENCY);
                break;
            case "fixed":
                checkAndSetSortMode(action, SortingMode.FIXED);
                break;
            default:
                break;
        }
    }

    private void checkAndSetSortMode(Action action, SortingMode sortMode) {
        if (action.isChecked()) {
            this.mAppOrderChanged = false;
            return;
        }
        this.mAppOrderChanged = true;
        action.setChecked(true);
        AppsPreferences.saveSortingMode(getApplicationContext(), sortMode);
        DialogFragment fragment = DialogFragment.getCurrentDialogFragment(getFragmentManager());
        if (fragment != null) {
            for (Action otherAction : fragment.getActions()) {
                if (otherAction != action) {
                    otherAction.setChecked(false);
                }
            }
            fragment.setActions(fragment.getActions());
        }
    }

    private void showHomeScreenOrderDialog() {
        DialogFragment.add(getFragmentManager(), buildFragment(getString(R.string.home_screen_order_content_title), R.drawable.ic_settings_home, new Builder().description(getString(R.string.home_screen_order_content_description)).actions(getHomeScreenOrderActions())));
    }


    private ArrayList<Action> getHomeScreenOrderActions() {
        String appOrderActionDescription;
        ArrayList<Action> actions = new ArrayList<>();
        SortingMode sortingMode = AppsPreferences.getSavedSortingMode(getApplicationContext());
        if (sortingMode == SortingMode.FIXED) {
            appOrderActionDescription = getString(R.string.select_app_order_action_description_fixed);
        } else {
            appOrderActionDescription = getString(R.string.select_app_order_action_description_recency);
        }
        actions.add(new Action.Builder().title(getString(R.string.select_app_order_action_title)).description(appOrderActionDescription).key("appOrder").build());
        //if (sortingMode == SortingMode.FIXED) {
        //    actions.add(new Action.Builder().title(getString(R.string.customize_app_order_action_title)).key("customizeApps").build());
        //    actions.add(new Action.Builder().title(getString(R.string.customize_game_order_action_title)).key("customizeGames").build());
        //}
        return actions;
    }

    private void showSelectAppOrderDialog() {
        boolean z;
        int i = 0;
        String title = getString(R.string.select_app_order_content_title);
        ArrayList<Action> actions = new ArrayList<>();
        SortingMode sortingMode = AppsPreferences.getSavedSortingMode(getApplicationContext());
        Action.Builder description = new Action.Builder().title(getString(R.string.recency_order_action_title)).description(getString(R.string.recency_order_action_description));
        z = sortingMode == SortingMode.RECENCY;
        actions.add(description.checked(z).key("recency").build());
        description = new Action.Builder().title(getString(R.string.fixed_order_action_title)).description(getString(R.string.fixed_order_action_description));
        z = sortingMode == SortingMode.FIXED;
        actions.add(description.checked(z).key("fixed").build());
        FragmentManager fragmentManager = getFragmentManager();
        Builder actions2 = new Builder().actions(actions);
        if (sortingMode != SortingMode.RECENCY) {
            i = 1;
        }
        DialogFragment.add(fragmentManager, buildFragment(title, R.drawable.ic_settings_home, actions2.selectedIndex(i)));
    }

    private DialogFragment buildFragment(String title, int iconResourceId, Builder builder) {
        if (!TextUtils.isEmpty(this.mClickedActionTitle)) {
            builder.breadcrumb(this.mClickedActionTitle);
        }
        if (!TextUtils.isEmpty(title)) {
            builder.title(title);
            this.mClickedActionTitle = title;
        }
        if (iconResourceId != 0) {
            builder.iconResourceId(iconResourceId);
            builder.iconBackgroundColor(getResources().getColor(R.color.settings_icon_background));
        }
        return builder.build();
    }

    private ArrayList<Action> getBaseActions() {
        //ArrayList<Action> actions = new ArrayList<>(1);
        // actions.add(new Action.Builder().title(getString(R.string.recommendation_blacklist_action_title)).description(getResources().getQuantityString(R.plurals.recommendation_blacklist_action_description, this.mBlacklistedPackageNames.size(), this.mBlacklistedPackageNames.size())).key("recs").build());
        //actions.add(new Action.Builder().title(getString(R.string.home_screen_order_action_title)).key("homeScreenOrder").build());
        return getHomeScreenOrderActions();
    }*/
}


