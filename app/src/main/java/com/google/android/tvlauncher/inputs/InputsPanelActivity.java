package com.google.android.tvlauncher.inputs;

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

import com.google.android.tvlauncher.analytics.LogEvents;
import com.google.android.tvlauncher.analytics.LoggingActivity;
import com.google.android.tvlauncher.analytics.UserActionEvent;

public class InputsPanelActivity extends LoggingActivity {
    private static final String TAG_INPUTS_FRAGMENT = "inputs_fragment";

    public static class AppInputsFragment extends LeanbackSettingsFragment {
        public static AppInputsFragment newInstance() {
            return new AppInputsFragment();
        }

        public void onPreferenceStartInitialScreen() {
            startPreferenceFragment(InputsPanelFragment.newInstance());
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

    public InputsPanelActivity() {
        super("InputsPanel");
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            //16908290
            getFragmentManager().beginTransaction().add(android.R.id.content, AppInputsFragment.newInstance(), TAG_INPUTS_FRAGMENT).commit();
            TransitionManager.go(new Scene((ViewGroup) findViewById(16908290)), new Slide(GravityCompat.END));
        }
        getEventLogger().log(new UserActionEvent(LogEvents.OPEN_INPUTS_VIEW));
    }

    public void finish() {
        final Fragment fragment = getFragmentManager().findFragmentByTag(TAG_INPUTS_FRAGMENT);
        if (fragment == null || !fragment.isResumed()) {
            super.finish();
            return;
        }
        Scene scene = new Scene((ViewGroup) findViewById(16908290));
        scene.setEnterAction(new Runnable() {
            public void run() {
                InputsPanelActivity.this.getFragmentManager().beginTransaction().remove(fragment).commit();// .commitNow();
            }
        });
        Slide slide = new Slide(GravityCompat.END);
        slide.addListener(new TransitionListener() {
            public void onTransitionStart(Transition transition) {
                InputsPanelActivity.this.getWindow().setDimAmount(0.0f);
            }

            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                InputsPanelActivity.super.finish();
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
