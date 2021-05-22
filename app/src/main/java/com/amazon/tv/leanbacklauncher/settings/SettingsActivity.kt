package com.amazon.tv.leanbacklauncher.settings

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.amazon.tv.leanbacklauncher.R

class SettingsActivity : FragmentActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
    }

    // https://developer.android.com/guide/topics/ui/settings/organize-your-settings
    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        // Instantiate the new Fragment
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader,
            pref.fragment
        )
        fragment.arguments = args
        fragment.setTargetFragment(caller, 0)
        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.settingsFragment, fragment)
            .addToBackStack(null)
            .commit()
        return true
    }
}