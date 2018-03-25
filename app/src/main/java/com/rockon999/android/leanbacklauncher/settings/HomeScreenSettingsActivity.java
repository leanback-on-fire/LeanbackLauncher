package com.rockon999.android.leanbacklauncher.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v17.preference.LeanbackSettingsFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;

import com.rockon999.android.leanbacklauncher.MainActivity;
import com.rockon999.android.leanbacklauncher.R;

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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @SuppressLint("ResourceType")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getWindow().getDecorView().setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_default));

            getFragmentManager().beginTransaction().add(SettingsFragment.newInstance(), "Settings").addToBackStack(null).commit();
        }
    }
}
