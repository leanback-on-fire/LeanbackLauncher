package com.amazon.tv.leanbacklauncher.apps;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.leanbacklauncher.util.Util;
import com.amazon.tv.firetv.leanbacklauncher.util.AppCategorizer;
import com.amazon.tv.firetv.leanbacklauncher.apps.AppCategory;
import com.amazon.tv.firetv.leanbacklauncher.util.BannerUtil;
import com.amazon.tv.firetv.leanbacklauncher.util.SettingsUtil;

import java.util.Map;

public class LaunchPoint {
    private String mAppTitle;
    private Drawable mBannerDrawable;
    private String mComponentName;
    private String mContentDescription;
    private boolean mHasBanner;
    private Drawable mIconDrawable;
    private int mInstallProgressPercent;
    private int mInstallStateStringResourceId;
    private boolean mIsInitialInstall;
    private int mLaunchColor;
    private Intent mLaunchIntent;
    private InstallingLaunchPointListener mListener;
    private long mPackageInstallTime;
    private String mPackageName;
    private int mPriority;
    private int mSettingsType = SettingsUtil.SettingsType.UNKNOWN.getCode();
    private boolean mTranslucentTheme;

    private boolean mIsSettingsItem;
    private AppCategory mAppCategory;
    private boolean hasGameFlag;

    public AppCategory getAppCategory() {
        return mAppCategory;
    }

    public LaunchPoint() {

    }

    LaunchPoint(Context context, String appTitle, String packageName) {
        clear(context);
        this.mAppTitle = appTitle;
        this.mPackageName = packageName;
    }

    public LaunchPoint(Context context, String appTitle, String iconUrl, String pkgName, Intent launchIntent, InstallingLaunchPointListener listener) {
        this.mAppTitle = appTitle;
        Resources resources = context.getResources();
        this.mLaunchColor = ResourcesCompat.getColor(resources, R.color.app_launch_ripple_default_color, null);
        this.mPackageName = pkgName;
        this.mPackageInstallTime = Util.getInstallTimeForPackage(context, this.mPackageName);
        if (launchIntent != null) {
            this.mLaunchIntent = launchIntent.addFlags(270532608);
            if (this.mLaunchIntent.getComponent() != null) {
                this.mComponentName = this.mLaunchIntent.getComponent().flattenToString();
            }
        }
        this.mListener = listener;
        this.mIsInitialInstall = true;
    }

    public LaunchPoint(Context context, String appTitle, String iconUrl, Intent launchIntent, int launchColor, InstallingLaunchPointListener listener) {
        clear(context);
        this.mAppTitle = appTitle;

        this.mLaunchColor = launchColor;
        if (launchIntent != null) {
            this.mLaunchIntent = launchIntent.addFlags(270532608);
            if (this.mLaunchIntent.getComponent() != null) {
                this.mComponentName = this.mLaunchIntent.getComponent().flattenToString();
                this.mPackageName = this.mLaunchIntent.getComponent().getPackageName();
            }
        }

        Resources resources = context.getResources();
        this.mLaunchColor = ResourcesCompat.getColor(resources, R.color.app_launch_ripple_default_color, null);
        this.mPackageInstallTime = Util.getInstallTimeForPackage(context, this.mPackageName);

        this.mListener = listener;
        this.mIsInitialInstall = true;

        if (!TextUtils.isEmpty(iconUrl)) {
            int maxIconSize = resources.getDimensionPixelSize(R.dimen.banner_icon_size);
            Glide.with(context).asDrawable().apply(RequestOptions.skipMemoryCacheOf(true)).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).load(iconUrl).into(new SimpleTarget<Drawable>(maxIconSize, maxIconSize) {
                public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                    InstallingLaunchPointListener launchPointListener = LaunchPoint.this.mListener;
                    if (drawable != null && launchPointListener != null) {
                        LaunchPoint.this.mIconDrawable = drawable;
                        launchPointListener.onInstallingLaunchPointChanged(LaunchPoint.this);
                    }
                }
            });
        }
    }

    // todo
    public LaunchPoint(Context context, String appTitle, Drawable iconDrawable, Intent launchIntent, int launchColor) {
        clear(context);
        this.mAppTitle = appTitle;
        this.mIconDrawable = iconDrawable;
        this.mLaunchColor = launchColor;
        if (launchIntent != null) {
            this.mLaunchIntent = launchIntent.addFlags(270532608);
            if (this.mLaunchIntent.getComponent() != null) {
                this.mComponentName = this.mLaunchIntent.getComponent().flattenToString();
                this.mPackageName = this.mLaunchIntent.getComponent().getPackageName();
                this.mPackageInstallTime = Util.getInstallTimeForPackage(context, this.mPackageName);
            }
        }
    }


    public LaunchPoint(Context ctx, PackageManager pm, ResolveInfo info) {
        set(ctx, pm, info);
        this.mIsSettingsItem = true;
    }

    public LaunchPoint(Context ctx, PackageManager pm, ResolveInfo info, boolean useBanner, int settingsType) {
        set(ctx, pm, info, useBanner);
        this.mSettingsType = settingsType;
        this.mIsSettingsItem = true;
    }

    public LaunchPoint set(Context ctx, PackageManager pm, ResolveInfo info) {
        return set(ctx, pm, info, true);
    }

    public LaunchPoint set(Context ctx, PackageManager pm, ResolveInfo info, boolean useBanner) {
        clear(ctx);
        this.mAppTitle = info.loadLabel(pm).toString();
        this.mLaunchIntent = getLaunchIntent(info);
        if (this.mLaunchIntent.getComponent() != null) {
            this.mComponentName = this.mLaunchIntent.getComponent().flattenToString();
            this.mPackageName = this.mLaunchIntent.getComponent().getPackageName();
        }

        Resources res = ctx.getResources();
        int maxWidth = res.getDimensionPixelOffset(R.dimen.max_banner_image_width);
        int maxHeight = res.getDimensionPixelOffset(R.dimen.max_banner_image_height);

        ActivityInfo actInfo = info.activityInfo;
        if (actInfo != null) {
            mAppCategory = AppCategorizer.getAppCategory(this.mPackageName, actInfo);

            if (useBanner) {
                this.mBannerDrawable = actInfo.loadBanner(pm);
                if (this.mBannerDrawable instanceof BitmapDrawable) {
                    this.mBannerDrawable = new BitmapDrawable(res, Util.getSizeCappedBitmap(((BitmapDrawable) this.mBannerDrawable).getBitmap(), maxWidth, maxHeight));
                }
            }

            Map<String, Integer> overrides = BannerUtil.BANNER_OVERRIDES;

            for (String str : overrides.keySet()) {
                if (this.mPackageName.toLowerCase().contains(str)) {
                    this.mBannerDrawable = ContextCompat.getDrawable(ctx, overrides.get(str));
                    if (this.mBannerDrawable instanceof BitmapDrawable) {
                        this.mBannerDrawable = new BitmapDrawable(res, Util.getSizeCappedBitmap(((BitmapDrawable) this.mBannerDrawable).getBitmap(), maxWidth, maxHeight));
                    }
                    break;
                }
            }

            if (this.mBannerDrawable != null) {
                this.mHasBanner = true;
            } else {
                if (useBanner) {
                    this.mBannerDrawable = actInfo.loadLogo(pm);
                    if (this.mBannerDrawable instanceof BitmapDrawable) {
                        this.mBannerDrawable = new BitmapDrawable(res, Util.getSizeCappedBitmap(((BitmapDrawable) this.mBannerDrawable).getBitmap(), maxWidth, maxHeight));
                    }
                }
                if (this.mBannerDrawable != null) {
                    this.mHasBanner = true;
                } else {
                    this.mHasBanner = false;
                    this.mIconDrawable = info.loadIcon(pm);
                }
            }
        }
        this.mPriority = info.priority;
        this.mTranslucentTheme = isTranslucentTheme(ctx, info);
        this.mLaunchColor = getColor(ctx, info);
        this.mPackageInstallTime = Util.getInstallTimeForPackage(ctx, this.mPackageName);
        return this;
    }

    public void addLaunchIntentFlags(int flags) {
        if (this.mLaunchIntent != null) {
            this.mLaunchIntent.addFlags(flags);
        }
    }

    private void clear(Context context) {
        cancelPendingOperations(context);
        this.mInstallProgressPercent = -1;
        this.mInstallStateStringResourceId = 0;
        this.mComponentName = null;
        this.mPackageName = null;
        this.mBannerDrawable = null;
        this.mAppTitle = null;
        this.mContentDescription = null;
        this.mIconDrawable = null;
        this.mLaunchColor = 0;
        this.mLaunchIntent = null;
        this.mHasBanner = false;
        this.mPriority = -1;
        this.mSettingsType = SettingsUtil.SettingsType.UNKNOWN.getCode();
        this.mIsInitialInstall = false;
        this.mListener = null;
        this.mPackageInstallTime = -1;
    }

    public LaunchPoint setInstallationState(LaunchPoint launchPoint) {
        this.mInstallProgressPercent = launchPoint.getInstallProgressPercent();
        this.mInstallStateStringResourceId = launchPoint.getInstallStateStringResId();
        return this;
    }

    private static Intent getLaunchIntent(ResolveInfo info) {
        ComponentName componentName = new ComponentName(info.activityInfo.applicationInfo.packageName, info.activityInfo.name);
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setComponent(componentName);
        intent.addFlags(270532608);
        return intent;
    }

    private static int getColor(Context myContext, ResolveInfo info) {
        try {
            Theme theme = myContext.createPackageContext(info.activityInfo.applicationInfo.packageName, 0).getTheme();
            theme.applyStyle(info.activityInfo.getThemeResource(), true);
            TypedArray a = theme.obtainStyledAttributes(new int[]{android.R.attr.colorPrimary});
            int color = a.getColor(0, myContext.getResources().getColor(R.color.banner_background));
            a.recycle();
            return color;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return myContext.getResources().getColor(R.color.banner_background);
        }
    }

    private static int getColor(Context context, Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        Palette.Swatch swatch = p.getDarkMutedSwatch();

        if (swatch != null)
            return swatch.getRgb();
        else if ((swatch = p.getLightMutedSwatch()) != null)
            return swatch.getRgb();
        else if ((swatch = p.getVibrantSwatch()) != null)
            return swatch.getRgb();
        else if ((swatch = p.getMutedSwatch()) != null)
            return swatch.getRgb();
        else if ((swatch = p.getDarkVibrantSwatch()) != null)
            return swatch.getRgb();
        else if ((swatch = p.getDarkMutedSwatch()) != null)
            return swatch.getRgb();

        return ContextCompat.getColor(context, R.color.banner_background); // for API 22
    }

    public static boolean isTranslucentTheme(Context myContext, ResolveInfo info) {
        try {
            Theme theme = myContext.createPackageContext(info.activityInfo.applicationInfo.packageName, 0).getTheme();
            theme.applyStyle(info.activityInfo.getThemeResource(), true);
            TypedArray a = theme.obtainStyledAttributes(new int[]{android.R.attr.windowIsTranslucent});
            boolean windowIsTranslucent = a.getBoolean(0, false);
            a.recycle();
            return windowIsTranslucent;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getComponentName() {
        return this.mComponentName;
    }

    public String getTitle() {
        return this.mAppTitle;
    }

    public void setContentDescription(String desc) {
        this.mContentDescription = desc;
    }

    public String getContentDescription() {
        return this.mContentDescription;
    }

    public boolean hasBanner() {
        return this.mHasBanner;
    }

    public Intent getLaunchIntent() {
        return this.mLaunchIntent;
    }

    public Drawable getBannerDrawable() {
        return this.mBannerDrawable;
    }

    public Drawable getIconDrawable() {
        return this.mIconDrawable;
    }

    public boolean isGame() {
        return this.mAppCategory == AppCategory.GAME;
    }

    public void setIconDrawable(Drawable drawable) {
        this.mIconDrawable = drawable;
    }

    public void setTitle(String title) {
        this.mAppTitle = title;
    }

    public int getLaunchColor() {
        return this.mLaunchColor;
    }

    public boolean isTranslucentTheme() {
        return this.mIsSettingsItem;
    }

    public int getPriority() {
        return this.mPriority;
    }

    public void setPriority(int priority) {
        this.mPriority = priority;
    }

    public int getSettingsType() {
        return this.mSettingsType;
    }

    public void setSettingsType(int mSettingsType) {
        this.mSettingsType = mSettingsType;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getInstallProgressString(Context context) {
        if (this.mInstallProgressPercent == -1) {
            return "";
        }
        return context.getString(R.string.progress_percent, this.mInstallProgressPercent);
    }

    public int getInstallProgressPercent() {
        return this.mInstallProgressPercent;
    }

    public String getInstallStateString(Context context) {
        return context.getString(this.mInstallStateStringResourceId);
    }

    public int getInstallStateStringResId() {
        return this.mInstallStateStringResourceId;
    }

    public void setInstallProgressPercent(int progressPercent) {
        this.mInstallProgressPercent = progressPercent;
    }

    public void setInstallStateStringResourceId(int stateStringResourceId) {
        this.mInstallStateStringResourceId = stateStringResourceId;
    }

    public boolean isInstalling() {
        return this.mInstallStateStringResourceId != 0;
    }

    public boolean isInitialInstall() {
        return this.mIsInitialInstall;
    }

    public long getFirstInstallTime() {
        return this.mPackageInstallTime;
    }

    public String toString() {
        return this.mAppTitle + " [" + this.mPackageName + "]";
    }

    public void cancelPendingOperations(Context context) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LaunchPoint)) return false;

        LaunchPoint that = (LaunchPoint) o;

        if (mComponentName != null && !mComponentName.equals(that.mComponentName)) {
            return false;
        }

        return (mPackageName != null && !mPackageName.equals(that.mPackageName));
    }

    @Override
    public int hashCode() {
        int result = mComponentName != null ? mComponentName.hashCode() : 0;
        result = 31 * result + (mPackageName != null ? mPackageName.hashCode() : 0);
        return result;
    }
}
