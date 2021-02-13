package com.amazon.tv.leanbacklauncher.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import com.amazon.tv.leanbacklauncher.MainActivity
import com.amazon.tv.leanbacklauncher.R
import java.io.File

class LegacyWallpaperFragment : GuidedStepSupportFragment() {

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(
                getString(R.string.wallpaper_title),  // title
                getWallpaperDesc(requireContext()),  // description
                getString(R.string.settings_dialog_title),  // breadcrumb (parent)
                ResourcesCompat.getDrawable(resources, R.drawable.ic_settings_home, null) // icon
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.add(GuidedAction.Builder(
                requireContext())
                .id(ACTION_CONTINUE.toLong())
                .title(R.string.select_wallpaper_action_title)
                .description(R.string.select_wallpaper_action_desc)
                .build()
        )
        actions.add(GuidedAction.Builder(
                requireContext())
                .id(ACTION_RESET.toLong())
                .title(R.string.reset_wallpaper_action_title)
                .description(R.string.reset_wallpaper_action_desc)
                .build()
        )
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id.toInt()) {
            ACTION_CONTINUE -> add(fragmentManager, LegacyFileListFragment())
            ACTION_RESET -> {
                resetWallpaper()
                fragmentManager!!.popBackStack()
            }
            else -> {
            }
        }
    }

    fun resetWallpaper(): Boolean {
        val context = requireContext()
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        pref.edit().remove("wallpaper_image").apply()
        val file = File(context.filesDir, "background.jpg")
        if (file.exists()) {
            try {
                file.delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        // refresh home
        val activity = requireActivity()
        val Broadcast = Intent(MainActivity::class.java.name) // ACTION
        Broadcast.putExtra("RefreshHome", true)
        activity.sendBroadcast(Broadcast)
        return true
    }

    private fun getWallpaperDesc(context: Context): String? {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val image = pref.getString("wallpaper_image", "")
        return if (image!!.isNotBlank()) {
            image
        } else {
            val file = File(context.filesDir, "background.jpg")
            if (file.canRead()) {
                file.toString()
            } else null
        }
    }

    companion object {
        /* Action ID definition */
        private const val ACTION_CONTINUE = 0
        private const val ACTION_RESET = 1
    }
}