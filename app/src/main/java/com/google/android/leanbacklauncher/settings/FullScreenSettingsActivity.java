package com.google.android.leanbacklauncher.settings;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.leanbacklauncher.R;
import com.google.android.leanbacklauncher.apps.SortingModeManager;
import com.google.android.leanbacklauncher.apps.SortingModeManager.SortingMode;
import com.google.android.leanbacklauncher.settings.BlacklistManager.LoadRecommendationPackagesCallback;
import com.google.android.leanbacklauncher.recline.app.DialogFragment;
import com.google.android.leanbacklauncher.recline.app.DialogFragment.Action;
import com.google.android.leanbacklauncher.recline.app.DialogFragment.Action.Listener;
import com.google.android.leanbacklauncher.recline.app.DialogFragment.Builder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class FullScreenSettingsActivity extends Activity implements Listener, LoadRecommendationPackagesCallback {
    private boolean mAppOrderChanged;
    private BlacklistManager mBlacklistManager;
    private HashSet<String> mBlacklistedPackageNames;
    private String mClickedActionTitle;
    private ArrayList<String> mPackageNames;
    private boolean mSaved;

    private class ActionComparator implements Comparator<Action> {
        private ActionComparator() {
        }

        public int compare(Action lhs, Action rhs) {
            if (lhs == null) {
                return -1;
            }
            if (rhs == null) {
                return 1;
            }
            return compare(lhs.getTitle(), rhs.getTitle());
        }

        private int compare(String lhs, String rhs) {
            if (lhs == null) {
                return -1;
            }
            if (rhs == null) {
                return 1;
            }
            return lhs.compareTo(rhs);
        }
    }

    public FullScreenSettingsActivity() {
        this.mPackageNames = null;
        this.mBlacklistedPackageNames = null;
        this.mSaved = true;
        this.mAppOrderChanged = true;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.settings_dialog_bg_protection));
        this.mBlacklistManager = new BlacklistManager(this);
        this.mBlacklistManager.loadRecommendationPackages(this);
    }

    public void onPause() {
        saveBlacklistIfNecessary();
        super.onPause();
    }

    public void onStop() {
        super.onStop();
        finish();
    }

    public void onBackPressed() {
        super.onBackPressed();
        if (saveBlacklistIfNecessary() || this.mAppOrderChanged) {
            DialogFragment frag = DialogFragment.getCurrentDialogFragment(getFragmentManager());
            if (frag != null) {
                ArrayList<Action> actions = frag.getActions();
                int size = actions.size();
                int a = 0;
                while (a < size) {
                    if (TextUtils.equals(((Action) actions.get(a)).getKey(), "recs")) {
                        frag.setActions(getBaseActions());
                        return;
                    } else if (TextUtils.equals(((Action) actions.get(a)).getKey(), "appOrder")) {
                        frag.setActions(getHomeScreenOrderActions());
                        return;
                    } else {
                        a++;
                    }
                }
            }
        }
    }

    public void onActionClicked(Action action) {
        String key = action.getKey();
        if (key.equals("recs")) {
            showBlacklistDialog();
        } else if (key.equals("homeScreenOrder")) {
            showHomeScreenOrderDialog();
        } else if (key.equals("appOrder")) {
            showSelectAppOrderDialog();
        } else if (key.equals("customizeApps")) {
            launchEditMode("extra_start_customize_apps");
        } else if (key.equals("customizeGames")) {
            launchEditMode("extra_start_customize_games");
        } else if (key.equals("recency")) {
            checkAndSetSortMode(action, SortingMode.RECENCY);
        } else if (key.equals("fixed")) {
            checkAndSetSortMode(action, SortingMode.FIXED);
        } else {
            action.setChecked(!action.isChecked());
            this.mSaved = false;
            if (action.isChecked()) {
                this.mBlacklistedPackageNames.remove(key);
            } else {
                this.mBlacklistedPackageNames.add(key);
            }
            DialogFragment fragment = DialogFragment.getCurrentDialogFragment(getFragmentManager());
            if (fragment != null) {
                fragment.setActions(fragment.getActions());
            }
        }
    }

    private void launchEditMode(String editModeExtraKey) {
        Intent startMain = new Intent("android.intent.action.MAIN");
        startMain.addCategory("android.intent.category.HOME");
        startMain.putExtra(editModeExtraKey, true);
        startActivity(startMain);
    }

    private void checkAndSetSortMode(Action action, SortingMode sortMode) {
        if (action.isChecked()) {
            this.mAppOrderChanged = false;
            return;
        }
        this.mAppOrderChanged = true;
        action.setChecked(true);
        SortingModeManager.saveSortingMode(getApplicationContext(), sortMode);
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
        ArrayList<Action> actions = new ArrayList(this.mPackageNames.size());
        SortingMode sortingMode = SortingModeManager.getSavedSortingMode(getApplicationContext());
        if (sortingMode == SortingMode.FIXED) {
            appOrderActionDescription = getString(R.string.select_app_order_action_description_fixed);
        } else {
            appOrderActionDescription = getString(R.string.select_app_order_action_description_recency);
        }
        actions.add(new Action.Builder().title(getString(R.string.select_app_order_action_title)).description(appOrderActionDescription).key("appOrder").build());
        if (sortingMode == SortingMode.FIXED) {
            actions.add(new Action.Builder().title(getString(R.string.customize_app_order_action_title)).key("customizeApps").build());
            actions.add(new Action.Builder().title(getString(R.string.customize_game_order_action_title)).key("customizeGames").build());
        }
        return actions;
    }

    private void showSelectAppOrderDialog() {
        boolean z;
        int i = 0;
        String title = getString(R.string.select_app_order_content_title);
        ArrayList<Action> actions = new ArrayList(this.mPackageNames.size());
        SortingMode sortingMode = SortingModeManager.getSavedSortingMode(getApplicationContext());
        Action.Builder description = new Action.Builder().title(getString(R.string.recency_order_action_title)).description(getString(R.string.recency_order_action_description));
        if (sortingMode == SortingMode.RECENCY) {
            z = true;
        } else {
            z = false;
        }
        actions.add(description.checked(z).key("recency").build());
        description = new Action.Builder().title(getString(R.string.fixed_order_action_title)).description(getString(R.string.fixed_order_action_description));
        if (sortingMode == SortingMode.FIXED) {
            z = true;
        } else {
            z = false;
        }
        actions.add(description.checked(z).key("fixed").build());
        FragmentManager fragmentManager = getFragmentManager();
        Builder actions2 = new Builder().actions(actions);
        if (sortingMode != SortingMode.RECENCY) {
            i = 1;
        }
        DialogFragment.add(fragmentManager, buildFragment(title, R.drawable.ic_settings_home, actions2.selectedIndex(i)));
    }

    private void showBlacklistDialog() {
        PackageManager pkgMan = getPackageManager();
        String title = getString(R.string.recommendation_blacklist_content_title);
        String description = getString(R.string.recommendation_blacklist_content_description);
        ArrayList<Action> actions = new ArrayList(this.mPackageNames.size());
        for (String packageName : this.mPackageNames) {
            CharSequence appTitle;
            int resId = 0;
            try {
                ApplicationInfo info = pkgMan.getApplicationInfo(packageName, 0);
                appTitle = pkgMan.getApplicationLabel(info);
                Intent intent = new Intent();
                intent.addCategory("android.intent.category.LEANBACK_LAUNCHER");
                intent.setAction("android.intent.action.MAIN");
                intent.setPackage(packageName);
                ResolveInfo resolveInfo = pkgMan.resolveActivity(intent, 0);
                if (!(resolveInfo == null || resolveInfo.activityInfo == null)) {
                    resId = resolveInfo.activityInfo.banner;
                }
                if (resId == 0) {
                    resId = info.banner;
                }
                if (resId == 0) {
                    resId = info.icon != 0 ? info.icon : 17301651;
                }
            } catch (NameNotFoundException e) {
                appTitle = null;
                resId = 0;
            }
            if (!TextUtils.isEmpty(appTitle)) {
                boolean z;
                Action.Builder key = new Action.Builder().title(appTitle.toString()).resourcePackageName(packageName).drawableResource(resId).key(packageName);
                if (this.mBlacklistedPackageNames.contains(packageName)) {
                    z = false;
                } else {
                    z = true;
                }
                actions.add(key.checked(z).build());
            }
        }
        Collections.sort(actions, new ActionComparator());
        DialogFragment.add(getFragmentManager(), buildFragment(title, R.drawable.ic_settings_home, new Builder().description(description).actions(actions)));
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

    public void onRecommendationPackagesLoaded(String[] recommendationPackaged, String[] blacklistedPackages) {
        this.mPackageNames = new ArrayList(Arrays.asList(recommendationPackaged));
        this.mBlacklistedPackageNames = new HashSet(Arrays.asList(blacklistedPackages));
        DialogFragment.add(getFragmentManager(), buildFragment(getString(R.string.settings_dialog_title), R.drawable.ic_settings_home, new Builder().actions(getBaseActions())));
    }

    private boolean saveBlacklistIfNecessary() {
        if (this.mSaved || this.mBlacklistedPackageNames == null) {
            return false;
        }
        this.mSaved = true;
        this.mBlacklistManager.saveEntityBlacklist(new ArrayList(this.mBlacklistedPackageNames));
        return true;
    }

    private ArrayList<Action> getBaseActions() {
        ArrayList<Action> actions = new ArrayList(1);
        actions.add(new Action.Builder().title(getString(R.string.recommendation_blacklist_action_title)).description(getResources().getQuantityString(R.plurals.recommendation_blacklist_action_description, this.mBlacklistedPackageNames.size(), new Object[]{Integer.valueOf(this.mBlacklistedPackageNames.size())})).key("recs").build());
        actions.add(new Action.Builder().title(getString(R.string.home_screen_order_action_title)).key("homeScreenOrder").build());
        return actions;
    }
}
