package com.google.android.tvlauncher.appsview;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.ViewGroup;

import com.google.android.tvlauncher.analytics.LoggingActivity;

public class AppsViewActivity extends LoggingActivity {
    private static final String TAG = "AppsViewActivity";
    static final String TAG_APPS_VIEW_FRAGMENT = "apps_view_fragment";
    static final String TAG_EDIT_MODE_FRAGMENT = "edit_mode_fragment";

    public AppsViewActivity() {
        super("Apps");
    }

    private void checkIntentForEditMode() {
        Intent localIntent = getIntent();
        AppsViewFragment localAppsViewFragment = (AppsViewFragment) getFragmentManager().findFragmentByTag("apps_view_fragment");
        if ((localIntent != null) && (localIntent.getExtras() != null) && (localAppsViewFragment != null)) {
            if (localIntent.getExtras().getBoolean("extra_start_customize_apps", false)) {
                if (localIntent.getExtras().getBoolean("extra_start_customize_games")) {
                    localAppsViewFragment.startEditMode(1);
                } else {
                    localAppsViewFragment.startEditMode(0);
                }
            }
        }
    }

    private void onShowAppsView() {
        AppsViewFragment localAppsViewFragment = new AppsViewFragment();
        getFragmentManager().beginTransaction().add(16908290, localAppsViewFragment, "apps_view_fragment").commit();
    }

    public static void startAppsViewActivity(@Nullable Integer paramInteger, Context paramContext) {
        Intent localIntent = new Intent("android.intent.action.ALL_APPS");
        if ((paramInteger != null) && (paramInteger == 0)) {
            localIntent.putExtra("extra_start_customize_apps", true);
        }
        for (; ; ) {
            try {
                paramContext.startActivity(localIntent);
                return;
            } catch (ActivityNotFoundException ex) {
                Log.e("AppsViewActivity", "AppsViewActivity not found :  " + ex);
            }
            if ((paramInteger != null) && (paramInteger == 1)) {
                localIntent.putExtra("extra_start_customize_games", true);
            }
        }
    }

    public void finish() {
        final Fragment fragment = getFragmentManager().findFragmentByTag("apps_view_fragment");
        if ((fragment != null) && (((Fragment) fragment).isResumed())) {
            Scene localScene = new Scene((ViewGroup) findViewById(16908290));
            localScene.setEnterAction(new Runnable() {
                public void run() {
                    AppsViewActivity.this.getFragmentManager().beginTransaction().remove(fragment).commitNow();
                }
            });
            Slide slide = new Slide(8388613);
            slide.addListener(new Transition.TransitionListener() {
                public void onTransitionCancel(Transition paramAnonymousTransition) {
                }

                public void onTransitionEnd(Transition paramAnonymousTransition) {
                    paramAnonymousTransition.removeListener(this);
                    AppsViewActivity.this.finish();
                }

                public void onTransitionPause(Transition paramAnonymousTransition) {
                }

                public void onTransitionResume(Transition paramAnonymousTransition) {
                }

                public void onTransitionStart(Transition paramAnonymousTransition) {
                    AppsViewActivity.this.getWindow().setDimAmount(0.0F);
                }
            });
            TransitionManager.go(localScene, slide);
            return;
        }
        super.finish();
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        if (paramBundle == null) {
            onShowAppsView();
            TransitionManager.go(new Scene((ViewGroup) findViewById(16908290)), new Slide(8388613));
        }
    }

    protected void onNewIntent(Intent paramIntent) {
        super.onNewIntent(paramIntent);
        setIntent(paramIntent);
    }

    protected void onResume() {
        super.onResume();
        if ((getFragmentManager().findFragmentByTag("edit_mode_fragment") != null) && (getFragmentManager().getBackStackEntryCount() != 0)) {
            getFragmentManager().popBackStack();
        }
        sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
        checkIntentForEditMode();
    }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/appsview/AppsViewActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */