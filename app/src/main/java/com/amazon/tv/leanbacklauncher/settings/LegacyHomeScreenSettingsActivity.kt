package com.amazon.tv.leanbacklauncher.settings

import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.GuidedStepSupportFragment
import com.amazon.tv.leanbacklauncher.R

class LegacyHomeScreenSettingsActivity : FragmentActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.settings_dialog_bg_protection, null))
        if (savedInstanceState == null) {
            GuidedStepSupportFragment.addAsRoot(this, LegacyHomeScreenPreferenceFragment(), 16908290)
        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
            // back to Launcher
            // Intent intent = new Intent(this, MainActivity.class);
            // startActivity(intent);
            // finish();
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}