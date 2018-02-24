package com.google.android.leanbacklauncher.settings;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v17.preference.LeanbackSettingsFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;

public class HomeScreenSettingsActivity extends Activity {

    public static class SettingsFragment extends LeanbackSettingsFragment {
        public static SettingsFragment newInstance() {
            return new SettingsFragment();
        }

        public void onPreferenceStartInitialScreen() {
            startPreferenceFragment(HomeScreenPreferenceFragment.newInstance());
        }

        public boolean onPreferenceStartFragment(PreferenceFragment caller, Preference pref) {
            Fragment f = Fragment.instantiate(getActivity(), pref.getFragment(), pref.getExtras());
            f.setTargetFragment(caller, 0);
            startPreferenceFragment(f);
            return true;
        }

        public boolean onPreferenceStartScreen(PreferenceFragment preferenceFragment, PreferenceScreen preferenceScreen) {
            return false;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(16908290, SettingsFragment.newInstance()).commit();
        }
    }
}
