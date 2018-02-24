package com.google.android.tvlauncher.settings;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v14.preference.PreferenceFragment;
import android.support.v17.preference.LeanbackSettingsFragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.Transition.TransitionListener;
import android.transition.TransitionManager;
import android.view.ViewGroup;

import com.google.android.tvlauncher.analytics.LoggingActivity;

public class FavoriteAppsActivity extends LoggingActivity {
    private static final String TAG_FAVORITES_FRAGMENT = "tag_favorites_fragment";

    public static class FavoriteAppsFragment extends LeanbackSettingsFragment {
        public static FavoriteAppsFragment newInstance() {
            return new FavoriteAppsFragment();
        }

        public void onPreferenceStartInitialScreen() {
            startPreferenceFragment(FavoriteAppsSelectionFragment.newInstance());
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

    public FavoriteAppsActivity() {
        super("FavoriteApps");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(android.R.id.content, FavoriteAppsFragment.newInstance(), TAG_FAVORITES_FRAGMENT).commit();
            TransitionManager.go(new Scene((ViewGroup) findViewById(android.R.id.content)), new Slide(GravityCompat.END));
        }
    }

    public void finish() {
        final Fragment fragment = getFragmentManager().findFragmentByTag(TAG_FAVORITES_FRAGMENT);
        if (fragment == null || !fragment.isResumed()) {
            super.finish();
            return;
        }
        Scene scene = new Scene((ViewGroup) findViewById(android.R.id.content));
        scene.setEnterAction(new Runnable() {
            public void run() {
                FavoriteAppsActivity.this.getFragmentManager().beginTransaction().remove(fragment).commit();//todo Now();
            }
        });
        Slide slide = new Slide(GravityCompat.END);
        slide.addListener(new TransitionListener() {
            public void onTransitionStart(Transition transition) {
                FavoriteAppsActivity.this.getWindow().setDimAmount(0.0f);
            }

            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                FavoriteAppsActivity.super.finish();
            }

            public void onTransitionCancel(Transition transition) {
            }

            public void onTransitionPause(Transition transition) {
            }

            public void onTransitionResume(Transition transition) {
            }
        });
        TransitionManager.go(scene, slide);
    }
}
