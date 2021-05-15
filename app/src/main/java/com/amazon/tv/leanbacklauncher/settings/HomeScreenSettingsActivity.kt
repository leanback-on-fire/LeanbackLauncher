package com.amazon.tv.leanbacklauncher.settings

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.amazon.tv.leanbacklauncher.R

class HomeScreenSettingsActivity: FragmentActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
    }
}