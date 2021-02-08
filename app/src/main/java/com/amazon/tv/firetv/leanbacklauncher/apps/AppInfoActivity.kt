package com.amazon.tv.firetv.leanbacklauncher.apps

import android.R
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.leanback.app.GuidedStepFragment
import com.amazon.tv.firetv.leanbacklauncher.apps.AppInfoFragment.Companion.newInstance

//import android.util.Log;
class AppInfoActivity : Activity() {
    private var icon: Drawable? = null
    private var title: String? = null
    private var pkg: String? = null
    private var version: String? = null
    private var desc: String? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            pkg = intent.extras!!.getString("pkg")
            try {
                val info = packageManager.getApplicationInfo(pkg!!, 0)
                title = packageManager.getApplicationLabel(info).toString() // todo flag/tostr
                version = packageManager.getPackageInfo(pkg!!, 0).versionName
                if (version!!.isEmpty()) desc = pkg else desc = "$pkg ($version)"
                icon = packageManager.getApplicationBanner(info)
                if (icon == null) icon = packageManager.getApplicationLogo(info)
                if (icon == null) icon = packageManager.getApplicationIcon(info)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            window.setBackgroundDrawable(ColorDrawable(Color.parseColor("#21272A")))
            val fragment = newInstance(title, pkg, icon)
            GuidedStepFragment.addAsRoot(this, fragment, R.id.content)
        }
    }

    // close on Home press
    override fun onUserLeaveHint() {
        //Log.d("onUserLeaveHint", "Home button pressed");
        super.onUserLeaveHint()
        finish()
    }
}