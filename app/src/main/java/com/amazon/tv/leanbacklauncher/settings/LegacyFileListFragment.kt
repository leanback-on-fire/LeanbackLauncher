package com.amazon.tv.leanbacklauncher.settings

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist.Guidance
import androidx.leanback.widget.GuidedAction
import androidx.preference.PreferenceManager
import com.amazon.tv.leanbacklauncher.BuildConfig
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.util.Util.refreshHome
import java.io.File

class LegacyFileListFragment : GuidedStepSupportFragment() {

    companion object {
        private val TAG by lazy {
            if (BuildConfig.DEBUG) ("[*]" + LegacyFileListFragment::class.java.simpleName).take(
                21
            ) else LegacyFileListFragment::class.java.simpleName
        }
        private var rootPath: String? = null
        private var dirName: String? = null

        /* Action ID definition */
        private const val ACTION_SELECT = 1
        private const val ACTION_BACK = 2
        private const val ACTION_DIR = 3
    }

    override fun onCreateGuidance(savedInstanceState: Bundle?): Guidance {
        return Guidance(
            getString(R.string.select_wallpaper_action_title),  // title
            getWallpaperDesc(requireContext()),  // description
            getString(R.string.settings_dialog_title),  // breadcrumb (parent)
            ResourcesCompat.getDrawable(resources, R.drawable.ic_settings_home, null) // icon
        )
    }

    override fun onResume() {
        super.onResume()
        val fm = fragmentManager
        if (rootPath.isNullOrEmpty() || fm?.backStackEntryCount == 3) // 3 on root dir
            rootPath = Environment.getExternalStorageDirectory().absolutePath
        val ctx = requireContext()
        if (BuildConfig.DEBUG) Log.d(TAG, "onResume() rootPath: $rootPath")

        var dir: File? = null
        var subdirs: ArrayList<File> = ArrayList()
        var dimages: ArrayList<File> = ArrayList()
        rootPath?.let { dir = File(it) }
        dir?.let {
            subdirs = dirReader(it)
            dimages = imageReader(it)
        }
        val actions = ArrayList<GuidedAction>()
        // back
        actions.add(
            GuidedAction.Builder(ctx)
                .id(ACTION_BACK.toLong())
                .title(R.string.goback)
                .description(null)
                .build()
        )
        // directories
        if (subdirs.size > 0)
            subdirs.forEach {
                actions.add(
                    GuidedAction.Builder(ctx)
                        .id(ACTION_DIR.toLong())
                        .title(it.name)
                        .description(null)
                        .build()
                )
            }
        // images
        if (dimages.size > 0)
            dimages.forEach {
                actions.add(
                    GuidedAction.Builder(ctx)
                        .id(ACTION_SELECT.toLong())
                        .title(it.name)
                        .description(null)
                        .build()
                )
            }

        setActions(actions)
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        val ctx = requireContext()
        val fm = fragmentManager
        when (action.id.toInt()) {
            ACTION_DIR -> {
                rootPath = File(rootPath, action.title.toString()).absolutePath
                dirName = action.title.toString()
                if (fm != null) {
                    add(fm, LegacyFileListFragment())
                }
            }
            ACTION_SELECT -> {
                val name = action.title
                val file = File(rootPath, name.toString())
                if (file.canRead())
                    setWallpaper(ctx, file.path.toString())
                else
                    Toast.makeText(ctx, ctx.getString(R.string.file_no_access), Toast.LENGTH_LONG)
                        .show()
                dirName?.let { rootPath = rootPath?.removeSuffix(it) }
                fm?.popBackStack()
            }
            ACTION_BACK -> {
                dirName?.let { rootPath = rootPath?.removeSuffix(it) }
                fm?.popBackStack()
            }
        }
        //if (BuildConfig.DEBUG) Log.d(TAG, "onGuidedActionClicked() rootPath: $rootPath, backstack: ${fm?.backStackEntryCount}")
    }

    fun imageReader(root: File): ArrayList<File> {
        val imageList: ArrayList<File> = ArrayList()
        val images = root.listFiles { file ->
            (!file.isDirectory && file.name.endsWith(".jpeg") || file.name.endsWith(".jpg") || file.name.endsWith(
                ".png"
            ))
        }
        if (!images.isNullOrEmpty()) {
            for (image in images) {
                // File absolute path
                if (BuildConfig.DEBUG) Log.d(TAG, "image path ${image.absolutePath}")
                // File Name
                if (BuildConfig.DEBUG) Log.d(TAG, "image name ${image.name}")
                imageList.add(image.absoluteFile)
            }
            if (BuildConfig.DEBUG) Log.w(TAG, "fileList size ${imageList.size}")
        }
        return imageList
    }

    fun dirReader(root: File): ArrayList<File> {
        val dirList: ArrayList<File> = ArrayList()
        val dirs = root.listFiles { file ->
            file.isDirectory && !file.name.startsWith(".")
        }
        if (!dirs.isNullOrEmpty())
            for (dir in dirs) {
                dirList.add(dir.absoluteFile)
            }
        return dirList
    }

    private fun setWallpaper(context: Context?, image: String): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        if (image.isNotEmpty()) pref.edit().putString("wallpaper_image", image).apply()
        // refresh home broadcast
        val activity = requireActivity()
        refreshHome(activity)
        return true
    }

    private fun getWallpaperDesc(context: Context): String {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val imagePath = pref.getString("wallpaper_image", "")
        return if (imagePath!!.isNotBlank()) {
            imagePath
        } else {
            val file = File(context.getExternalFilesDir(null), "background.jpg")
            if (file.canRead()) {
                file.toString()
            }
            getString(R.string.wallpaper_choose)
        }
    }
}