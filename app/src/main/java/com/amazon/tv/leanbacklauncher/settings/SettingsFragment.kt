package com.amazon.tv.leanbacklauncher.settings

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.leanback.preference.LeanbackPreferenceFragmentCompat
import androidx.leanback.preference.LeanbackSettingsFragmentCompat
import androidx.preference.*
import com.amazon.tv.firetv.leanbacklauncher.apps.RowPreferences
import com.amazon.tv.firetv.leanbacklauncher.util.FireTVUtils
import com.amazon.tv.firetv.leanbacklauncher.util.SharedPreferencesUtil
import com.amazon.tv.leanbacklauncher.BuildConfig
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.apps.AppsManager
import com.amazon.tv.leanbacklauncher.util.Util
import java.io.File
import java.util.*


class SettingsFragment : LeanbackSettingsFragmentCompat() {
    private val TAG = "SettingsFragment"

    override fun onPreferenceStartInitialScreen() {
        startPreferenceFragment(LauncherSettingsFragment())
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat?,
        pref: Preference
    ): Boolean {
        val args: Bundle = pref.extras
        val f: Fragment = childFragmentManager.fragmentFactory.instantiate(
            requireActivity().classLoader, pref.fragment
        )
        f.arguments = args
        f.setTargetFragment(caller, 0)
        if (f is PreferenceFragmentCompat
            || f is PreferenceDialogFragmentCompat
        ) {
            Log.d(TAG, "onPreferenceStartFragment: startPreferenceFragment($f)")
            startPreferenceFragment(f)
        } else {
            Log.d(TAG, "onPreferenceStartFragment: startImmersiveFragment($f)")
            startImmersiveFragment(f)
        }
        return true
    }

    override fun onPreferenceStartScreen(
        caller: PreferenceFragmentCompat?,
        pref: PreferenceScreen
    ): Boolean {
        val fragment: Fragment = LauncherSettingsFragment()
        val args = Bundle(1)
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, pref.key)
        fragment.arguments = args
        Log.d(TAG, "onPreferenceStartScreen: startPreferenceFragment($fragment)")
        startPreferenceFragment(fragment)
        return true
    }
}

/**
 * The fragment that is embedded in SettingsFragment
 */
class LauncherSettingsFragment : LeanbackPreferenceFragmentCompat() {
    private val TAG = "*****"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey)
        val ps = findPreference<PreferenceScreen>("root_prefs")

        findPreference<Preference>("rec_sources")?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d(TAG, "REMOVE $this from $ps")
                ps?.removePreference(this)
                preferenceScreen = ps
            }
        }

        findPreference<Preference>("version")?.apply {
            this.summary = BuildConfig.VERSION_NAME
        }

    }
}

class HomePreferencesFragment : LeanbackPreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.home_prefs, rootKey)
        findPreference<PreferenceScreen>("home_prefs")

        val sortingMode = AppsManager.getSavedSortingMode(context)
        findPreference<Preference>("apps_order")?.apply {
            this.summary =
                if (sortingMode == AppsManager.SortingMode.FIXED) getString(R.string.select_app_order_action_description_fixed)
                else getString(R.string.select_app_order_action_description_recency)
        }
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference?.key == "apps_order") {
            val mode = AppsManager.getSavedSortingMode(context)
            if (mode == AppsManager.SortingMode.FIXED) {
                AppsManager.saveSortingMode(context, AppsManager.SortingMode.RECENCY)
                preference.summary = getString(R.string.select_app_order_action_description_recency)
            } else {
                AppsManager.saveSortingMode(context, AppsManager.SortingMode.FIXED)
                preference.summary = getString(R.string.select_app_order_action_description_fixed)

            }
            return true
        }
        return super.onPreferenceTreeClick(preference)
    }
}

class HiddenPreferenceFragment : LeanbackPreferenceFragmentCompat() {
    private var mIdToPackageMap: HashMap<Long, String> = hashMapOf()
    private var screen: PreferenceScreen? = null

    private fun loadHiddenApps() {
        val prefUtil = SharedPreferencesUtil.instance(requireContext())
        val packages: List<String> = ArrayList(prefUtil!!.hiddenApps())
        mIdToPackageMap = HashMap()
        val prefs = ArrayList<Preference>()

        val showAllAppsPref = SwitchPreference(context)
        showAllAppsPref.key = KEY_ID_ALL_APPS.toString()
        showAllAppsPref.title = getString(R.string.show_all_apps)
        showAllAppsPref.isChecked = prefUtil.isAllAppsShown()
        screen?.addPreference(showAllAppsPref) // show all apps switch

        var appId: Long = 0
        val pm = activity!!.packageManager
        for (pkg in packages) {
            if (pkg.isNotEmpty()) {
                val hidden: Boolean
                try {
                    val packageInfo = pm.getPackageInfo(pkg, 0)
                    hidden = prefUtil.isHidden(pkg)
                    if (hidden) { // show only hidden apps
                        val icon =
                            pm.getApplicationIcon(packageInfo.applicationInfo) // ?: pm.getApplicationBanner(packageInfo.applicationInfo)
                        val appPreference = CheckBoxPreference(context)
                        appPreference.key = appId.toString()
                        appPreference.title = pm.getApplicationLabel(packageInfo.applicationInfo)
                        appPreference.icon = icon
                        appPreference.isChecked = hidden
                        prefs.add(appPreference)
                    }
                    mIdToPackageMap[appId] = packageInfo.packageName
                    appId++
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            }
        }

        val hiddenCategory = PreferenceCategory(context)
        hiddenCategory.key = "hidden_apps_category"
        hiddenCategory.title = getString(R.string.hidden_applications_desc)
        screen?.addPreference(hiddenCategory)
        if (prefs.isNotEmpty()) {
            for (pref in prefs) {
                hiddenCategory.addPreference(pref)
            }
        } else {
            val emptyPref = Preference(context)
            emptyPref.key = "empty"
            emptyPref.title = getString(R.string.hidden_applications_empty)
            emptyPref.isEnabled = false
            emptyPref.isSelectable = false
            hiddenCategory.addPreference(emptyPref)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = preferenceManager.context
        screen = preferenceManager.createPreferenceScreen(context)
        screen?.title = getString(R.string.hidden_applications_title)
        loadHiddenApps()
        preferenceScreen = screen
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        val context = requireContext()
        val prefUtil = SharedPreferencesUtil.instance(context)
        var appIdFromKey: Long = -1L
        if (preference!!.key.isDigitsOnly())
            appIdFromKey = preference.key.toLong()

        if (preference is SwitchPreference) { // show all apps switch
            if (appIdFromKey == KEY_ID_ALL_APPS) {
                prefUtil?.showAllApps(preference.isChecked)
                // refresh home broadcast
                Util.refreshHome(context)
            }
            return true
        } else if (preference is CheckBoxPreference) { // hidden apps list
            if (preference.isChecked) {
                prefUtil?.hide(mIdToPackageMap[appIdFromKey])
            } else {
                prefUtil?.unhide(mIdToPackageMap[appIdFromKey])
                screen?.removePreference(preference)
            }
            return true
        }
        return super.onPreferenceTreeClick(preference)
    }

    companion object {
        private const val KEY_ID_ALL_APPS = 1000L
    }
}

class RecommendationsPreferenceFragment : LeanbackPreferenceFragmentCompat(),
    RecommendationsPreferenceManager.LoadRecommendationPackagesCallback {
    private var mIdToPackageMap: HashMap<Long, String> = hashMapOf()
    private var mPreferenceManager: RecommendationsPreferenceManager? = null
    private var screen: PreferenceScreen? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPreferenceManager = RecommendationsPreferenceManager(requireContext())
    }

    override fun onResume() {
        super.onResume()
        mPreferenceManager?.loadRecommendationsPackages(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = preferenceManager.context
        screen = preferenceManager.createPreferenceScreen(context)
        screen?.title = getString(R.string.recommendation_blacklist_action_title)
        preferenceScreen = screen
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        var recIdFromKey: Long = -1L
        if (preference!!.key.isDigitsOnly())
            recIdFromKey = preference.key.toLong()
        if (preference is CheckBoxPreference)
            mPreferenceManager?.savePackageBlacklisted(
                mIdToPackageMap[recIdFromKey],
                !preference.isChecked
            )
        return true
    }

    override fun onRecommendationPackagesLoaded(info: List<RecommendationsPreferenceManager.PackageInfo>?) {
        mIdToPackageMap = HashMap()
        val recs = ArrayList<Preference>()
        var recId: Long = 0
        if (info != null) {
            for (packageInfo in info) {
                val banner: Drawable? = if (packageInfo.banner != null) {
                    packageInfo.banner
                } else {
                    buildBannerFromIcon(packageInfo.icon)
                }
                val recPreference = CheckBoxPreference(context)
                recPreference.key = recId.toString()
                recPreference.title = packageInfo.appTitle
                recPreference.icon = banner
                recPreference.isChecked = !packageInfo.blacklisted
                recs.add(recPreference)
                mIdToPackageMap[recId] = packageInfo.packageName!!
                recId++
            }
        }
        if (recs.isNotEmpty()) {
            for (rec in recs) {
                screen?.addPreference(rec)
            }
        } else {
            val emptyPref = Preference(context)
            emptyPref.key = "empty"
            emptyPref.title = getString(R.string.rec_soures_empty)
            emptyPref.isEnabled = false
            emptyPref.isSelectable = false
            screen?.addPreference(emptyPref)
        }
    }

    private fun buildBannerFromIcon(icon: Drawable?): Drawable {
        val resources = resources
        val bannerWidth = resources.getDimensionPixelSize(R.dimen.preference_item_banner_width)
        val bannerHeight = resources.getDimensionPixelSize(R.dimen.preference_item_banner_height)
        val iconSize = resources.getDimensionPixelSize(R.dimen.preference_item_icon_size)
        val bitmap = Bitmap.createBitmap(bannerWidth, bannerHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(
            ResourcesCompat.getColor(
                resources,
                R.color.preference_item_banner_background,
                null
            )
        )
        icon?.let {
            it.setBounds(
                (bannerWidth - iconSize) / 2,
                (bannerHeight - iconSize) / 2,
                (bannerWidth + iconSize) / 2,
                (bannerHeight + iconSize) / 2
            )
            it.draw(canvas)
        }
        return BitmapDrawable(resources, bitmap)
    }
}

class BannersPreferenceFragment :
    LeanbackPreferenceFragmentCompat()/*, SharedPreferences.OnSharedPreferenceChangeListener*/ {
    private val TAG = "*****"

//    override fun onResume() {
//        super.onResume()
//        // Set up a listener whenever a key changes
//        preferenceScreen.sharedPreferences
//            .registerOnSharedPreferenceChangeListener(this)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        // Unregister the listener whenever a key changes
//        preferenceScreen.sharedPreferences
//            .unregisterOnSharedPreferenceChangeListener(this)
//    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.banners_prefs, rootKey)

        val bs = findPreference(getString(R.string.pref_banner_size)) as EditTextPreference?
        bs!!.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
        }
//        findPreference<EditTextPreference>(getString(R.string.pref_banner_size))?.apply {
//            val size = RowPreferences.getBannersSize(context).toString()
//            this.summary = size
//    }

        val bc =
            findPreference(getString(R.string.pref_banner_corner_radius)) as EditTextPreference?
//        findPreference<EditTextPreference>("banner_corner_radius")?.apply {
//            val radius = RowPreferences.getCorners(context).toString()
//            this.summary = radius
//    }
        bc!!.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
        }

        val bf = findPreference(getString(R.string.pref_banner_frame_stroke)) as EditTextPreference?
//        findPreference<EditTextPreference>("banner_frame_stroke")?.apply {
//            val stroke = RowPreferences.getFrameWidth(context).toString()
//            this.summary = stroke
//        }
        bf!!.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
        }
        val fc =
            findPreference(getString(R.string.pref_banner_focus_frame_color)) as EditTextPreference?
        fc!!.setOnPreferenceChangeListener { preference, newValue ->
            Log.d(TAG, "$preference new value $newValue")
            val value = try {
                Color.parseColor(newValue.toString())
            } catch (nfe: IllegalArgumentException) {
                RowPreferences.getFrameColor(requireContext())
            }
            RowPreferences.setFrameColor(context, value)
            preference.summary = hexStringColor(value)
            // refresh home broadcast
            Util.refreshHome(requireContext())
            true
        }
        fc.apply {
            val color = hexStringColor(
                RowPreferences.getFrameColor(context)
            )
            this.summary = color
            setOnBindEditTextListener {
                it.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
            }
        }
    }

    private fun hexStringColor(color: Int): String {
        return String.format("#%08X", -0x1 and color)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        // refresh home broadcast
        Util.refreshHome(requireContext())
        return super.onPreferenceTreeClick(preference)
    }

//    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
//        val pref = findPreference<Preference>(key!!)
//        Log.d(TAG, "pref $key changed to $pref")
//    }
}

class AppRowsPreferenceFragment : LeanbackPreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.rows_prefs, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        val ctx = requireContext()
        if (preference?.key == getString(R.string.pref_enable_recommendations_row)) {
            val enabled = (preference as SwitchPreference).isChecked
            RowPreferences.setRecommendationsEnabled(ctx, enabled)
            if (enabled && FireTVUtils.isAmazonNotificationsEnabled(ctx)) {
                Toast.makeText(ctx, ctx.getString(R.string.recs_warning_sale), Toast.LENGTH_LONG)
                    .show()
            }
            // refresh home broadcast
            Util.refreshHome(requireContext())
            return true
        } else { // all others
            // refresh home broadcast
            Util.refreshHome(requireContext())
        }
        return super.onPreferenceTreeClick(preference)
    }

}

class WallpaperFragment : LeanbackPreferenceFragmentCompat() {

    override fun onResume() {
        super.onResume()
        // check permissions
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

        findPreference<Preference>("select_wallpaper")?.apply {
            this.summary = getWallpaperDesc(context)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.wallpaper_prefs, rootKey)

        findPreference<Preference>("select_wallpaper")?.apply {
            this.summary = getWallpaperDesc(context)
        }
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        return when (preference?.key) {
            "reset_wallpaper" -> {
                resetWallpaper()
                fragmentManager?.popBackStack()
                true
            }
            else -> super.onPreferenceTreeClick(preference)
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
        Util.refreshHome(context)
        return true
    }

    private fun getWallpaperDesc(context: Context): String {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val imagePath = pref.getString("wallpaper_image", "")
        return if (!imagePath.isNullOrEmpty()) {
            imagePath
        } else {
            val file = File(context.getExternalFilesDir(null), "background.jpg")
            if (file.canRead()) {
                file.toString()
            }
            getString(R.string.select_wallpaper_action_desc)
        }
    }
}

class FileListFragment : LeanbackPreferenceFragmentCompat() {
    private var rootPath: String? = null
    private var dirName: String? = null
    private var screen: PreferenceScreen? = null

    companion object {

        /* Action ID definition */
        private const val ACTION_BACK = 1
        private const val ACTION_DIR = 2
        private const val ACTION_SELECT = 3
    }

    override fun onResume() {
        super.onResume()
        val fm = fragmentManager
        if (rootPath.isNullOrEmpty() || fm?.backStackEntryCount == 3) // 3 on root dir
            rootPath = Environment.getExternalStorageDirectory().absolutePath
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = preferenceManager.context
        screen = preferenceManager.createPreferenceScreen(context)
        screen?.title = getString(R.string.select_wallpaper_action_title)
        preferenceScreen = screen

        if (rootPath.isNullOrEmpty())
            rootPath = Environment.getExternalStorageDirectory().absolutePath
        loadFileList()
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        val ctx = requireContext()
        val fm = fragmentManager

        when (preference?.key) {
            ACTION_BACK.toString() -> {
                dirName?.let { rootPath = rootPath?.removeSuffix(it) }
                fm?.popBackStack()
                return true
            }
            ACTION_DIR.toString() -> {
                rootPath = File(rootPath, preference.title.toString()).absolutePath
                dirName = preference.title.toString()
                val curFragContainerId = (view!!.parent as ViewGroup).id
                fm?.beginTransaction()?.replace(curFragContainerId, FileListFragment())
                    ?.addToBackStack(null)?.commit()
                return true
            }
            ACTION_SELECT.toString() -> {
                val name = preference.title
                val file = File(rootPath, name.toString())
                if (file.canRead())
                    setWallpaper(ctx, file.path.toString())
                else
                    Toast.makeText(ctx, ctx.getString(R.string.file_no_access), Toast.LENGTH_LONG)
                        .show()
                dirName?.let { rootPath = rootPath?.removeSuffix(it) }
                fm?.popBackStack()
                return true
            }
            else -> return super.onPreferenceTreeClick(preference)
        }
    }

    private fun loadFileList() {
        var dir: File? = null
        var folders: ArrayList<File> = ArrayList()
        var images: ArrayList<File> = ArrayList()

        rootPath?.let { dir = File(it) }
        dir?.let {
            folders = dirReader(it)
            images = imageReader(it)
        }
        val prefs = ArrayList<Preference>()

        // back
        val backPref = Preference(context)
        backPref.key = ACTION_BACK.toString()
        backPref.title = getString(R.string.goback)
        prefs.add(backPref)

        // directories
        if (folders.size > 0)
            folders.forEach {
                val dirPref = Preference(context)
                dirPref.key = ACTION_DIR.toString()
                dirPref.title = it.name
                dirPref.icon =
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_folder_24, null)
                prefs.add(dirPref)
            }

        // images
        if (images.size > 0)
            images.forEach {
                val imagePref = Preference(context)
                imagePref.key = ACTION_SELECT.toString()
                imagePref.title = it.name
                try {
                    val d = Drawable.createFromPath(it.toString())
                    imagePref.icon = buildBannerFromImage(d)
                } catch (e: java.lang.Exception) {
                }
                prefs.add(imagePref)
            }

        // apply
        if (prefs.isNotEmpty()) {
            for (pref in prefs) {
                screen?.addPreference(pref)
            }
        }
    }

    private fun imageReader(root: File): ArrayList<File> {
        val imageList: ArrayList<File> = ArrayList()
        val images = root.listFiles { file ->
            (!file.isDirectory &&
                    file.name.endsWith(".jpeg") ||
                    file.name.endsWith(".jpg") ||
                    file.name.endsWith(".png"))
        }
        if (!images.isNullOrEmpty()) {
            for (image in images) {
                imageList.add(image.absoluteFile)
            }
        }
        return imageList
    }

    private fun dirReader(root: File): ArrayList<File> {
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

    private fun buildBannerFromImage(image: Drawable?): Drawable {
        val resources = resources
        val bannerWidth = resources.getDimensionPixelSize(R.dimen.preference_item_banner_width)
        val bannerHeight = resources.getDimensionPixelSize(R.dimen.preference_item_banner_height)
        val bitmap = Bitmap.createBitmap(bannerWidth, bannerHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(
            ResourcesCompat.getColor(
                resources,
                R.color.preference_item_banner_background,
                null
            )
        )
        image?.let {
            it.setBounds(
                0,
                0,
                bannerWidth,
                bannerHeight
            )
            it.draw(canvas)
        }
        return BitmapDrawable(resources, bitmap)
    }

    private fun setWallpaper(context: Context?, image: String): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        if (image.isNotEmpty()) pref.edit().putString("wallpaper_image", image).apply()
        // refresh home broadcast
        Util.refreshHome(requireContext())
        return true
    }

}