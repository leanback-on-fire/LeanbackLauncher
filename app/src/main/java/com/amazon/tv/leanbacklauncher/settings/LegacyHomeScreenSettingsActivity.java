package com.amazon.tv.leanbacklauncher.settings;

import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.leanback.app.GuidedStepSupportFragment;

import com.amazon.tv.leanbacklauncher.R;

public class LegacyHomeScreenSettingsActivity extends FragmentActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.settings_dialog_bg_protection, null));

        if (savedInstanceState == null) {
            GuidedStepSupportFragment.addAsRoot(this, new LegacyHomeScreenPreferenceFragment(), 16908290);
        }
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            // back to Launcher
            // Intent intent = new Intent(this, MainActivity.class);
            // startActivity(intent);
            // finish();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}
