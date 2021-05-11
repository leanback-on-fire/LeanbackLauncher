package com.amazon.tv.firetv.leanbacklauncher.apps

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.GuidedStepSupportFragment
import com.amazon.tv.firetv.leanbacklauncher.apps.AppInfoFragment.Companion.newInstance

class AppInfoActivity : FragmentActivity() {
    private var icon: Drawable? = null
    private var title: String? = null
    private var pkg: String? = null
    private var version: String? = null
    private var desc: String? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            pkg = intent.extras?.getString("pkg")
            pkg?.let { id ->
                try {
                    val info = packageManager.getApplicationInfo(id, 0)
                    title = packageManager.getApplicationLabel(info).toString()
                    version = packageManager.getPackageInfo(id, 0).versionName
                    desc = if (version.isNullOrEmpty()) id else "$id ($version)"
                    icon = packageManager.getApplicationBanner(info)
                    if (icon == null) icon = packageManager.getApplicationLogo(info)
                    if (icon == null) icon = packageManager.getApplicationIcon(info)
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            }
            window.setBackgroundDrawable(ColorDrawable(Color.parseColor("#21272A")))
            val fragment = newInstance(title, pkg, icon)
            GuidedStepSupportFragment.addAsRoot(this, fragment, android.R.id.content)
        }
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    // finish on user leave
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        finishAffinity()
    }
}