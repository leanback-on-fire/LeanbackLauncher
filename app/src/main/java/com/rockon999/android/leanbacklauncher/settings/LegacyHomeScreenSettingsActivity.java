package com.rockon999.android.leanbacklauncher.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.app.GuidedStepSupportFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;

import com.rockon999.android.leanbacklauncher.R;

public class LegacyHomeScreenSettingsActivity extends FragmentActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.settings_dialog_bg_protection, null));

        if (savedInstanceState == null) {
            GuidedStepSupportFragment.addAsRoot(this, new LegacyHomeScreenPreferenceFragment(), 16908290);
        }
    }
}
