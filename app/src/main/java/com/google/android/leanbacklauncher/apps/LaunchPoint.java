package com.google.android.leanbacklauncher.apps;

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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.leanbacklauncher.R;
import com.google.android.leanbacklauncher.util.Util;

public class LaunchPoint {
    private String mAppTitle;
    private Drawable mBannerDrawable;
    private String mComponentName;
    private String mContentDescription;
    private boolean mHasBanner;
    private Drawable mIconDrawable;
    private int mInstallProgressPercent;
    private int mInstallStateStringResourceId;
    private boolean mIsGame;
    private boolean mIsInitialInstall;
    private int mLaunchColor;
    private Intent mLaunchIntent;
    private InstallingLaunchPointListener mListener;
    private long mPackageInstallTime;
    private String mPackageName;
    private int mPriority;
    private int mSettingsType;
    private boolean mTranslucentTheme;

    LaunchPoint(String appTitle, String packageName) {
        clear();
        this.mAppTitle = appTitle;
        this.mPackageName = packageName;
    }

    public LaunchPoint(Context context, String appTitle, String iconUrl, String pkgName, Intent launchIntent, boolean isGame, InstallingLaunchPointListener listener) {
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
        this.mIsGame = isGame;
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

    public LaunchPoint(Context ctx, PackageManager pm, ResolveInfo info) {
        set(ctx, pm, info);
    }

    public LaunchPoint(Context ctx, PackageManager pm, ResolveInfo info, boolean useBanner, int settingsType) {
        set(ctx, pm, info, useBanner);
        this.mSettingsType = settingsType;
    }

    public LaunchPoint set(Context ctx, PackageManager pm, ResolveInfo info) {
        return set(ctx, pm, info, true);
    }

    public LaunchPoint set(Context ctx, PackageManager pm, ResolveInfo info, boolean useBanner) {
        clear();
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
            if (useBanner) {
                this.mBannerDrawable = actInfo.loadBanner(pm);
                if (this.mBannerDrawable instanceof BitmapDrawable) {
                    this.mBannerDrawable = new BitmapDrawable(res, Util.getSizeCappedBitmap(this.mBannerDrawable.getBitmap(), maxWidth, maxHeight));
                }
            }
            boolean z = (actInfo.applicationInfo.flags & 33554432) != 0 || (actInfo.applicationInfo.metaData != null && actInfo.applicationInfo.metaData.getBoolean("isGame", false));
            this.mIsGame = z;
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

    private void clear() {
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
        this.mSettingsType = -1;
        this.mIsGame = false;
        this.mIsInitialInstall = false;
        this.mListener = null;
        this.mPackageInstallTime = -1;
    }

    public LaunchPoint setInstallationState(LaunchPoint launchPoint) {
        this.mInstallProgressPercent = launchPoint.getInstallProgressPercent();
        this.mInstallStateStringResourceId = launchPoint.getInstallStateStringResId();
        return this;
    }

    public boolean equals(Object other) {
        return this == other || ((other instanceof LaunchPoint) && TextUtils.equals(this.mPackageName, ((LaunchPoint) other).getPackageName()) && TextUtils.equals(this.mComponentName, ((LaunchPoint) other).getComponentName()));
    }

    public boolean matches(ResolveInfo other) {
        String pkgName = null;
        String compName = null;
        if (getLaunchIntent(other).getComponent() != null) {
            pkgName = this.mLaunchIntent.getComponent().getPackageName();
            compName = this.mLaunchIntent.getComponent().flattenToString();
        }
        return TextUtils.equals(this.mPackageName, pkgName) && TextUtils.equals(this.mComponentName, compName);
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
            TypedArray a = theme.obtainStyledAttributes(new int[]{16843827});
            int color = a.getColor(0, 0);
            a.recycle();
            return color;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return ContextCompat.getColor(myContext, R.color.app_launch_ripple_default_color);
        }
    }

    public static boolean isTranslucentTheme(Context myContext, ResolveInfo info) {
        try {
            Theme theme = myContext.createPackageContext(info.activityInfo.applicationInfo.packageName, 0).getTheme();
            theme.applyStyle(info.activityInfo.getThemeResource(), true);
            TypedArray a = theme.obtainStyledAttributes(new int[]{16842840});
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
        return this.mIsGame;
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
        return this.mTranslucentTheme;
    }

    public int getPriority() {
        return this.mPriority;
    }

    public int getSettingsType() {
        return this.mSettingsType;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getInstallProgressString(Context context) {
        if (this.mInstallProgressPercent == -1) {
            return "";
        }
        return context.getString(R.string.progress_percent, new Object[]{Integer.valueOf(this.mInstallProgressPercent)});
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
}
