package com.amazon.tv.leanbacklauncher.settings

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import com.amazon.tv.leanbacklauncher.MainActivity
import com.amazon.tv.leanbacklauncher.R
import java.io.File

class LegacyFileListFragment : GuidedStepSupportFragment() {

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        val activity: Activity? = activity
        val desc = getWallpaper(activity)
        return Guidance(
                getString(R.string.select_wallpaper_action_title),  // title
                desc,  // description
                getString(R.string.settings_dialog_title),  // breadcrumb (parent)
                ResourcesCompat.getDrawable(resources, R.drawable.ic_settings_home, null) // icon
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        val activity: Activity? = activity
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "READ_EXTERNAL_STORAGE permission is granted")
        } else {
            Log.v(TAG, "READ_EXTERNAL_STORAGE permission not granted")
            makeRequest()
        }
        actions.add(GuidedAction.Builder(
                getActivity())
                .id(ACTION_SELECT.toLong())
                .title(String.format(getString(R.string.select), Environment.getExternalStorageDirectory()))
                .description(null)
                .build()
        )
        actions.add(GuidedAction.Builder(
                getActivity())
                .id(ACTION_BACK.toLong())
                .title(R.string.goback)
                .description(null)
                .build()
        )
    }

    protected fun makeRequest() {
        val activity: Activity? = activity
        ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                500)
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        val activity: Activity? = activity
        when (action.id.toInt()) {
            ACTION_SELECT -> {
                val file = File(Environment.getExternalStorageDirectory(), "background.jpg")
                if (file.canRead()) setWallpaper(activity, Environment.getExternalStorageDirectory().toString() + "/background.jpg") else Toast.makeText(activity, activity!!.getString(R.string.file_no_access), Toast.LENGTH_LONG).show()
                fragmentManager!!.popBackStack()
            }
            ACTION_BACK -> fragmentManager!!.popBackStack()
            else -> {
            }
        }
    }

    fun setWallpaper(context: Context?, image: String): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        if (!image.isEmpty()) pref.edit().putString("wallpaper_image", image).apply()
        // refresh home
        val activity: Activity? = activity
        val Broadcast = Intent(MainActivity::class.java.name) // ACTION
        Broadcast.putExtra("RefreshHome", true)
        activity!!.sendBroadcast(Broadcast)
        return true
    }

    private fun getWallpaper(context: Context?): String? {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val image = pref.getString("wallpaper_image", "")
        return if (!image!!.isEmpty()) {
            image
        } else {
            val file = File(context!!.filesDir, "background.jpg")
            if (file.canRead()) {
                file.toString()
            } else null
        }
    }

    companion object {
        private const val TAG = "LegacyFileListFragment"

        /* Action ID definition */
        private const val ACTION_SELECT = 1
        private const val ACTION_BACK = 2
    }
}