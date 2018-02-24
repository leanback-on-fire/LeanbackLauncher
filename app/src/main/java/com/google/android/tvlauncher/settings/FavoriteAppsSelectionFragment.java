package com.google.android.tvlauncher.settings;

import android.os.Bundle;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceScreen;
import android.util.Pair;

import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.appsview.AppsManager;
import com.google.android.tvlauncher.appsview.AppsManager.AppsViewChangeListener;
import com.google.android.tvlauncher.appsview.LaunchItem;

import java.util.ArrayList;
import java.util.Iterator;

public class FavoriteAppsSelectionFragment extends LeanbackPreferenceFragment implements AppsViewChangeListener, OnPreferenceClickListener {
    private AppsManager mAppsManager;
    private boolean mStarted;

    public static FavoriteAppsSelectionFragment newInstance() {
        return new FavoriteAppsSelectionFragment();
    }

    public void onStart() {
        super.onStart();
        if (!this.mStarted) {
            this.mAppsManager.registerAppsViewChangeListener(this);
            this.mStarted = true;
        }
    }

    public void onStop() {
        super.onStop();
        if (this.mStarted) {
            this.mAppsManager.unregisterAppsViewChangeListener(this);
            this.mStarted = false;
        }
    }

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(getPreferenceManager().getContext());
        screen.setTitle((int) R.string.select_favorite_apps_title);
        setPreferenceScreen(screen);
        screen.setOrderingAsAdded(false);
        this.mAppsManager = AppsManager.getInstance(getActivity().getApplicationContext()); //getContext()
        this.mAppsManager.refreshLaunchItems();
    }

    public void onLaunchItemsLoaded() {
        PreferenceScreen screen = getPreferenceScreen();
        screen.removeAll();
        for (LaunchItem item : this.mAppsManager.getAllNonFavoriteLaunchItems()) {
            addPreference(screen, item, new Preference(getPreferenceManager().getContext()));
        }
        setPreferenceScreen(screen);
    }

    public void onLaunchItemsAddedOrUpdated(ArrayList<LaunchItem> addedOrUpdatedItems) {
        PreferenceScreen screen = getPreferenceScreen();
        Iterator it = addedOrUpdatedItems.iterator();
        while (it.hasNext()) {
            LaunchItem item = (LaunchItem) it.next();
            if (!this.mAppsManager.isFavorite(item)) {
                Preference preference = getPreferenceManager().findPreference(item.getPackageName());
                if (preference == null) {
                    preference = new Preference(getPreferenceManager().getContext());
                }
                addPreference(screen, item, preference);
            }
        }
        setPreferenceScreen(screen);
    }

    public void onLaunchItemsRemoved(ArrayList<LaunchItem> removedItems) {
        PreferenceScreen screen = getPreferenceScreen();
        Iterator it = removedItems.iterator();
        while (it.hasNext()) {
            Preference preference = getPreferenceManager().findPreference(((LaunchItem) it.next()).getPackageName());
            if (preference != null) {
                screen.removePreference(preference);
            }
        }
        setPreferenceScreen(screen);
    }

    public void onEditModeItemOrderChange(ArrayList<LaunchItem> arrayList, boolean isGameItems, Pair<Integer, Integer> pair) {
    }

    public boolean onPreferenceClick(Preference preference) {
        this.mAppsManager.addToFavorites(preference.getKey());
        getActivity().finish();
        return false;
    }

    private void addPreference(PreferenceScreen screen, LaunchItem item, Preference preference) {
        preference.setLayoutResource(R.layout.favorite_app_preference);
        preference.setKey(item.getPackageName());
        preference.setIcon(item.getItemDrawable());
        preference.setTitle(item.getLabel());
        preference.setOnPreferenceClickListener(this);
        screen.addPreference(preference);
    }
}
