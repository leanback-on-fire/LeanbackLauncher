package com.amazon.tv.leanbacklauncher.settings

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import androidx.preference.PreferenceManager
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.util.Util.refreshHome
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
        actions.add(
            GuidedAction.Builder(
                requireContext()
            )
                .id(ACTION_CONTINUE.toLong())
                .title(R.string.select_wallpaper_action_title)
                .description(R.string.select_wallpaper_action_desc)
                .build()
        )
        actions.add(
            GuidedAction.Builder(
                requireContext()
            )
                .id(ACTION_RESET.toLong())
                .title(R.string.reset_wallpaper_action_title)
                .description(R.string.reset_wallpaper_action_desc)
                .build()
        )
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                500
            )
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id.toInt()) {
            ACTION_CONTINUE -> add(fragmentManager, LegacyFileListFragment())
            ACTION_RESET -> {
                resetWallpaper()
                fragmentManager!!.popBackStack()
            }
            else -> return
        }
    }

    fun resetWallpaper(): Boolean {
        val context = requireContext()
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        pref.edit().remove("wallpaper_image").apply()
        val file = File(context.getExternalFilesDir(null), "background.jpg")
        if (file.exists()) {
            try {
                file.delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        // refresh home broadcast
        val activity = requireActivity()
        refreshHome(activity)
        return true
    }

    private fun getWallpaperDesc(context: Context): String? {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val image = pref.getString("wallpaper_image", "")
        return if (image!!.isNotBlank()) {
            image
        } else {
            val file = File(context.getExternalFilesDir(null), "background.jpg")
            if (file.canRead()) {
                file.path
            } else null
        }
    }

    companion object {
        private const val ACTION_CONTINUE = 0
        private const val ACTION_RESET = 1
    }
}