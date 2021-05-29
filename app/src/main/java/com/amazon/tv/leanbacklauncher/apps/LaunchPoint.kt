package com.amazon.tv.leanbacklauncher.apps

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.palette.graphics.Palette
import com.amazon.tv.firetv.leanbacklauncher.apps.AppCategory
import com.amazon.tv.firetv.leanbacklauncher.util.AppCategorizer
import com.amazon.tv.firetv.leanbacklauncher.util.BannerUtil
import com.amazon.tv.firetv.leanbacklauncher.util.SettingsUtil.SettingsType
import com.amazon.tv.leanbacklauncher.R
import com.amazon.tv.leanbacklauncher.util.Util
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.util.*

class LaunchPoint {
    var title: String? = null
    var bannerDrawable: Drawable? = null
        private set
    var componentName: String? = null
        private set
    var contentDescription: String? = null
    private var mHasBanner = false
    var iconDrawable: Drawable? = null
    var installProgressPercent = 0
    var installStateStringResId = 0
        private set
    private var isInitialInstall = false
    var launchColor = 0
        private set
    var launchIntent: Intent? = null
        private set
    private var mListener: InstallingLaunchPointListener? = null
    var firstInstallTime: Long = 0
        private set
    var packageName: String? = null
        private set
    var priority = 0
    var settingsType = SettingsType.UNKNOWN.code
    private var mTranslucentTheme = false
    var isTranslucentTheme = false
        private set
    var appCategory: AppCategory? = null
        private set

    constructor()
    internal constructor(context: Context, appTitle: String?, packageName: String?) {
        clear(context)
        title = appTitle
        this.packageName = packageName
    }

    constructor(
        context: Context,
        appTitle: String?,
        iconUrl: String?,
        pkgName: String?,
        launchIntent: Intent?,
        listener: InstallingLaunchPointListener?
    ) {
        title = appTitle
        val resources = context.resources
        launchColor =
            ResourcesCompat.getColor(resources, R.color.app_launch_ripple_default_color, null)
        packageName = pkgName
        firstInstallTime = Util.getInstallTimeForPackage(context, packageName)
        if (launchIntent != null) {
            this.launchIntent =
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            if (this.launchIntent!!.component != null) {
                componentName = this.launchIntent!!.component!!.flattenToString()
            }
        }
        mListener = listener
        isInitialInstall = true
    }

    constructor(
        context: Context,
        appTitle: String?,
        iconUrl: String?,
        launchIntent: Intent?,
        launchColor: Int,
        listener: InstallingLaunchPointListener?
    ) {
        clear(context)
        title = appTitle
        this.launchColor = launchColor
        if (launchIntent != null) {
            this.launchIntent =
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            if (this.launchIntent!!.component != null) {
                componentName = this.launchIntent!!.component!!.flattenToString()
                packageName = this.launchIntent!!.component!!.packageName
            }
        }
        val resources = context.resources
        this.launchColor =
            ResourcesCompat.getColor(resources, R.color.app_launch_ripple_default_color, null)
        firstInstallTime = Util.getInstallTimeForPackage(context, packageName)
        mListener = listener
        isInitialInstall = true
        if (!TextUtils.isEmpty(iconUrl)) {
            val maxIconSize = resources.getDimensionPixelSize(R.dimen.banner_icon_size)
            Glide.with(context).asDrawable()
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .load(iconUrl)
                .into(object : CustomTarget<Drawable?>(maxIconSize, maxIconSize) {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable?>?
                    ) {
                        val launchPointListener = mListener
                        launchPointListener?.let {
                            iconDrawable = resource
                            it.onInstallingLaunchPointChanged(this@LaunchPoint)
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        }
    }

    // TODO
    constructor(
        context: Context,
        appTitle: String?,
        iconDrawable: Drawable?,
        launchIntent: Intent?,
        launchColor: Int
    ) {
        clear(context)
        title = appTitle
        this.iconDrawable = iconDrawable
        this.launchColor = launchColor
        if (launchIntent != null) {
            this.launchIntent =
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            if (this.launchIntent!!.component != null) {
                componentName = this.launchIntent!!.component!!.flattenToString()
                packageName = this.launchIntent!!.component!!.packageName
                firstInstallTime = Util.getInstallTimeForPackage(context, packageName)
            }
        }
    }

    constructor(ctx: Context, pm: PackageManager?, info: ResolveInfo) {
        set(ctx, pm, info)
        isTranslucentTheme = true
    }

    constructor(
        ctx: Context,
        pm: PackageManager?,
        info: ResolveInfo,
        useBanner: Boolean,
        settingsType: Int
    ) {
        set(ctx, pm, info, useBanner)
        this.settingsType = settingsType
        isTranslucentTheme = true
    }

    @JvmOverloads
    fun set(
        ctx: Context,
        pm: PackageManager?,
        info: ResolveInfo,
        useBanner: Boolean = true
    ): LaunchPoint {
        clear(ctx)
        title = info.loadLabel(pm).toString()
        launchIntent = getLaunchIntent(info)
        launchIntent?.component?.let {
            componentName = it.flattenToString()
            packageName = it.packageName
        }
        val res = ctx.resources
        val maxWidth = res.getDimensionPixelOffset(R.dimen.max_banner_image_width)
        val maxHeight = res.getDimensionPixelOffset(R.dimen.max_banner_image_height)
        val activityInfo = info.activityInfo
        activityInfo?.let { ai ->
            appCategory = AppCategorizer.getAppCategory(packageName, ai)
            if (useBanner) {
                bannerDrawable = ai.loadBanner(pm)
                if (bannerDrawable is BitmapDrawable) {
                    bannerDrawable = BitmapDrawable(
                        res,
                        Util.getSizeCappedBitmap(
                            (bannerDrawable as BitmapDrawable?)!!.bitmap,
                            maxWidth,
                            maxHeight
                        )
                    )
                }
            }
            val overrides = BannerUtil.BANNER_OVERRIDES
            for (str in overrides.keys) {
                if (packageName!!.lowercase(Locale.getDefault()).contains(str)) {
                    bannerDrawable = ContextCompat.getDrawable(ctx, overrides[str]!!)
                    if (bannerDrawable is BitmapDrawable) {
                        bannerDrawable = BitmapDrawable(
                            res,
                            Util.getSizeCappedBitmap(
                                (bannerDrawable as BitmapDrawable?)!!.bitmap,
                                maxWidth,
                                maxHeight
                            )
                        )
                    }
                    break
                }
            }
            if (bannerDrawable != null) {
                mHasBanner = true
            } else {
                if (useBanner) {
                    bannerDrawable = ai.loadLogo(pm)
                    if (bannerDrawable is BitmapDrawable) {
                        bannerDrawable = BitmapDrawable(
                            res,
                            Util.getSizeCappedBitmap(
                                (bannerDrawable as BitmapDrawable?)!!.bitmap,
                                maxWidth,
                                maxHeight
                            )
                        )
                    }
                }
                if (bannerDrawable != null) {
                    mHasBanner = true
                } else {
                    mHasBanner = false
                    iconDrawable = info.loadIcon(pm)
                }
            }
        }
        priority = info.priority
        mTranslucentTheme = isTranslucentTheme(ctx, info)
        launchColor = getColor(ctx, info)
        firstInstallTime = Util.getInstallTimeForPackage(ctx, packageName)
        return this
    }

    fun addLaunchIntentFlags(flags: Int) {
        launchIntent?.addFlags(flags)
    }

    private fun clear(context: Context) {
        cancelPendingOperations(context)
        installProgressPercent = -1
        installStateStringResId = 0
        componentName = null
        packageName = null
        bannerDrawable = null
        title = null
        contentDescription = null
        iconDrawable = null
        launchColor = 0
        launchIntent = null
        mHasBanner = false
        priority = -1
        settingsType = SettingsType.UNKNOWN.code
        isInitialInstall = false
        mListener = null
        firstInstallTime = -1
    }

    fun setInstallationState(launchPoint: LaunchPoint): LaunchPoint {
        installProgressPercent = launchPoint.installProgressPercent
        installStateStringResId = launchPoint.installStateStringResId
        return this
    }

    fun hasBanner(): Boolean {
        return mHasBanner
    }

    val isGame: Boolean
        get() = appCategory == AppCategory.GAME

    fun getInstallProgressString(context: Context): String {
        return if (installProgressPercent == -1) {
            ""
        } else context.getString(R.string.progress_percent, installProgressPercent)
    }

    fun getInstallStateString(context: Context): String {
        return context.getString(installStateStringResId)
    }

    fun setInstallStateStringResourceId(stateStringResourceId: Int) {
        installStateStringResId = stateStringResourceId
    }

    val isInstalling: Boolean
        get() = installStateStringResId != 0

    override fun toString(): String {
        return "$title [$packageName]"
    }

    fun dump(): String {
        return "$title [$packageName]\n" +
                "component:$componentName\n" +
                "contentDescription:$contentDescription\n" +
                "hasBanner:$mHasBanner\n" +
                "launchColor:$launchColor\n" +
                "launchIntent:$launchIntent\n" +
                "priority:$priority\n" +
                "settingsType:$settingsType\n" +
                "isTranslucentTheme:$isTranslucentTheme\n" +
                "isGame:$isGame\n" +
                "appCategory:$appCategory\n"
    }

    fun cancelPendingOperations(context: Context?) {}

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LaunchPoint) return false
        return if (componentName != null && componentName != other.componentName) {
            false
        } else packageName != null && packageName != other.packageName
    }

    override fun hashCode(): Int {
        var result = if (componentName != null) componentName.hashCode() else 0
        result = 31 * result + if (packageName != null) packageName.hashCode() else 0
        return result
    }

    companion object {
        private fun getLaunchIntent(info: ResolveInfo): Intent {
            val componentName =
                ComponentName(info.activityInfo.applicationInfo.packageName, info.activityInfo.name)
            val intent = Intent("android.intent.action.MAIN")
            intent.addCategory("android.intent.category.LAUNCHER")
            intent.component = componentName
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            return intent
        }

        private fun getColor(myContext: Context, info: ResolveInfo): Int {
            return try {
                val theme = myContext.createPackageContext(
                    info.activityInfo.applicationInfo.packageName,
                    0
                ).theme
                theme.applyStyle(info.activityInfo.themeResource, true)
                val a = theme.obtainStyledAttributes(intArrayOf(android.R.attr.colorPrimary))
                val color = a.getColor(
                    0,
                    ResourcesCompat.getColor(myContext.resources, R.color.banner_background, null)
                )
                a.recycle()
                color
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                ResourcesCompat.getColor(myContext.resources, R.color.banner_background, null)
            }
        }

        private fun getColor(context: Context, bitmap: Bitmap): Int {
            val p = Palette.from(bitmap).generate()
            var swatch = p.darkMutedSwatch
            if (swatch != null) return swatch.rgb else if (p.lightMutedSwatch.also {
                    swatch = it
                } != null) return swatch!!.rgb else if (p.vibrantSwatch.also {
                    swatch = it
                } != null) return swatch!!.rgb else if (p.mutedSwatch.also {
                    swatch = it
                } != null) return swatch!!.rgb else if (p.darkVibrantSwatch.also {
                    swatch = it
                } != null) return swatch!!.rgb else if (p.darkMutedSwatch.also {
                    swatch = it
                } != null) return swatch!!.rgb
            return ContextCompat.getColor(context, R.color.banner_background) // for API 22
        }

        fun isTranslucentTheme(myContext: Context, info: ResolveInfo): Boolean {
            return try {
                val theme = myContext.createPackageContext(
                    info.activityInfo.applicationInfo.packageName,
                    0
                ).theme
                theme.applyStyle(info.activityInfo.themeResource, true)
                val a = theme.obtainStyledAttributes(intArrayOf(android.R.attr.windowIsTranslucent))
                val windowIsTranslucent = a.getBoolean(0, false)
                a.recycle()
                windowIsTranslucent
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                false
            }
        }
    }
}