package com.rockon999.android.leanbacklauncher.settings;

import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;

import com.rockon999.android.leanbacklauncher.R;

/**
 * Created by Evan Welsh on 3/24/18.
 */

public class AdvancedPreferenceFragment extends LeanbackPreferenceFragment {
    private final static String PREFERENCE_RESOURCE_ID = "preferenceResource";
    private final static String PREFERENCE_ROOT = "root";
    private PreferenceFragment mPreferenceFragment;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(getPreferenceManager().getContext());
        screen.setTitle("Advanced");
        screen.setSummary("Summary");
        setPreferenceScreen(screen);

        mPreferenceFragment = buildPreferenceFragment(R.xml.settings, null);

    }

    @Override
    public Preference findPreference(CharSequence charSequence) {
        return mPreferenceFragment.findPreference(charSequence);
    }

    private PreferenceFragment buildPreferenceFragment(int preferenceResId, String root) {
        PreferenceFragment fragment = new PrefFragment();
        Bundle args = new Bundle();
        args.putInt(PREFERENCE_RESOURCE_ID, preferenceResId);
        args.putString(PREFERENCE_ROOT, root);
        fragment.setArguments(args);
        return fragment;
    }

    public static class PrefFragment extends LeanbackPreferenceFragment {

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            String root = getArguments().getString(PREFERENCE_ROOT, null);
            int prefResId = getArguments().getInt(PREFERENCE_RESOURCE_ID);
            if (root == null) {
                addPreferencesFromResource(prefResId);
            } else {
                setPreferencesFromResource(prefResId, root);
            }
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            return super.onPreferenceTreeClick(preference);
        }
    }
}
