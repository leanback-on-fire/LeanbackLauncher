package com.rockon999.android.leanbacklauncher.apps;

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
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;

import com.rockon999.android.leanbacklauncher.R;
import com.rockon999.android.leanbacklauncher.recline.util.BitmapWorkerOptions;
import com.rockon999.android.leanbacklauncher.recline.util.DrawableDownloader;
import com.rockon999.android.leanbacklauncher.util.BitmapUtils;
import com.rockon999.android.leanbacklauncher.util.ConstData;
import com.rockon999.android.leanbacklauncher.util.SettingsUtil;
import com.rockon999.android.leanbacklauncher.util.Util;

import java.io.File;
import java.util.Date;
import java.util.Map;


public class LaunchPoint {
    protected String mAppTitle;
    protected Drawable mBannerDrawable;
    private final DrawableDownloader.BitmapCallback mBitmapCallback;
    protected String mComponentName;
    protected String mContentDescription;
    protected boolean mHasBanner;
    protected Drawable mIconDrawable;
    protected int mInstallProgressPercent;
    protected int mInstallStateStringResourceId;
    protected boolean mIsInitialInstall;
    protected int mLaunchColor;
    protected Intent mLaunchIntent;
    protected InstallingLaunchPointListener mListener;
    protected long mPackageInstallTime;
    protected String mPackageName;
    protected int mPriority;
    protected int mSettingsType = SettingsUtil.Type.UNKNOWN.getCode();
    protected boolean mTranslucentTheme;

    private boolean mIsSettingsItem;
    private AppCategory mAppCategory;
    private boolean hasGameFlag;

    public AppCategory getAppCategory() {
        return mAppCategory;
    }


    class C01861 extends DrawableDownloader.BitmapCallback {
        C01861() {
        }

        public void onCompleted(Drawable drawable) {
            if (drawable != null) {
                LaunchPoint.this.mIconDrawable = drawable;
                LaunchPoint.this.mListener.onInstallingLaunchPointChanged(LaunchPoint.this);
            }
        }
    }

    public LaunchPoint() {
        this.mBitmapCallback = new C01861();
    }

    public LaunchPoint(Context context, String appTitle, Drawable iconDrawable, Intent launchIntent, int launchColor) {
        this.mBitmapCallback = new C01861();
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

    public LaunchPoint(Context context, String appTitle, String iconUrl, String pkgName, Intent launchIntent, InstallingLaunchPointListener listener) {
        this(context, appTitle, null, launchIntent, context.getResources().getColor(R.color.app_launch_ripple_default_color));
        this.mPackageName = pkgName;
        this.mListener = listener;
        this.mIsInitialInstall = true;
        this.mPackageInstallTime = new Date().getTime();
        if (!TextUtils.isEmpty(iconUrl)) {
            int maxIconSize = context.getResources().getDimensionPixelSize(R.dimen.banner_icon_size);
            DrawableDownloader.getInstance(context).getBitmap(new BitmapWorkerOptions.Builder(context).resource(Uri.parse(iconUrl)).width(maxIconSize).height(maxIconSize).cacheFlag(1).build(), this.mBitmapCallback);
        }
    }

    public LaunchPoint(Context ctx, PackageManager pm, ResolveInfo info) {
        this.mBitmapCallback = new C01861();
        set(ctx, pm, info);
    }

    public LaunchPoint(Context ctx, PackageManager pm, ResolveInfo info, boolean useBanner, int settingsType) {
        this.mBitmapCallback = new C01861();
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
        String componentName = null;
        String bannerPath = null;
        String iconPath = null;

        Map<String, Integer> overrides = ConstData.BANNER_OVERRIDES;
        String key = null;

        for (String str : overrides.keySet()) {
            if (this.mPackageName.toLowerCase().contains(str)) {
                key = str;
                break;
            }
        } // todo optimize

        if (key != null) {
            this.mBannerDrawable = ContextCompat.getDrawable(ctx, overrides.get(key));
            if (this.mBannerDrawable instanceof BitmapDrawable) {
                this.mBannerDrawable = new BitmapDrawable(res, Util.getSizeCappedBitmap(((BitmapDrawable) this.mBannerDrawable).getBitmap(), maxWidth, maxHeight));
            }
        } else {
            if (mComponentName != null) {
                componentName = mComponentName.replace(".", "_").replace("/", "__");

                bannerPath = ctx.getFilesDir() + "/" + ConstData.CACHE_IMG_DIR + "/" + componentName + "_banner.png";
                iconPath = ctx.getFilesDir() + "/" + ConstData.CACHE_IMG_DIR + "/" + componentName + "_icon.png";
            }

            if (actInfo != null) {
                if (useBanner) {
                    Bitmap cacheBitmap = null;
                    if (bannerPath != null && new File(bannerPath).exists()) {
                        File file = new File(bannerPath);
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        cacheBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);

                        this.mBannerDrawable = new BitmapDrawable(res, cacheBitmap);
                    }
                    if (cacheBitmap == null || !new File(bannerPath).exists()) {
                        this.mBannerDrawable = actInfo.loadBanner(pm);
                        if (this.mBannerDrawable instanceof BitmapDrawable) {
                            this.mBannerDrawable = new BitmapDrawable(res, Util.getSizeCappedBitmap(((BitmapDrawable) this.mBannerDrawable).getBitmap(), maxWidth, maxHeight));
                        }
                        if (mBannerDrawable != null && mBannerDrawable instanceof BitmapDrawable)
                            BitmapUtils.saveBitmapToImage(((BitmapDrawable) mBannerDrawable).getBitmap(), bannerPath, Bitmap.CompressFormat.PNG, 80);
                    }

                }
            }
        }


        // todo move out of LaunchPoint

        resetAppCategory(actInfo);

        if (this.mBannerDrawable != null) {
            this.mHasBanner = true;
        } else {
            if (useBanner) {
                Bitmap cacheBitmap = null;
                if (bannerPath != null && new File(bannerPath).exists()) {
                    cacheBitmap = BitmapUtils.getBitmapFromFile(bannerPath);
                    this.mBannerDrawable = new BitmapDrawable(res, cacheBitmap);
                }
                if (cacheBitmap == null || !new File(bannerPath).exists()) {
                    if (actInfo != null) {
                        this.mBannerDrawable = actInfo.loadLogo(pm);
                    }
                    if (this.mBannerDrawable instanceof BitmapDrawable) {
                        this.mBannerDrawable = new BitmapDrawable(res, Util.getSizeCappedBitmap(((BitmapDrawable) this.mBannerDrawable).getBitmap(), maxWidth, maxHeight));
                    }
                    if (mBannerDrawable instanceof BitmapDrawable) {
                        BitmapUtils.saveBitmapToImage(((BitmapDrawable) mBannerDrawable).getBitmap(), bannerPath, Bitmap.CompressFormat.PNG, 80);
                    }
                }
            }
            this.mHasBanner = this.mBannerDrawable != null;
        }

        if (key == null)

        {
            Bitmap cacheBitmap = null;

            if (iconPath != null) {
                File parent = new File(iconPath).getParentFile();

                if (parent != null) {
                    parent.mkdirs(); // todo
                }

                if (new File(iconPath).exists()) {
                    cacheBitmap = BitmapUtils.getBitmapFromFile(iconPath);
                    this.mIconDrawable = new BitmapDrawable(res, cacheBitmap);
                }

                if (cacheBitmap == null || !new File(iconPath).exists()) {
                    this.mIconDrawable = info.loadIcon(pm);
                    if (this.mIconDrawable instanceof BitmapDrawable) {
                        // todo sizecap
                        this.mIconDrawable = new BitmapDrawable(res, ((BitmapDrawable) this.mIconDrawable).getBitmap());
                        BitmapUtils.saveBitmapToImage(((BitmapDrawable) mIconDrawable).getBitmap(), iconPath, Bitmap.CompressFormat.PNG, 80);
                    }
                }
            }
        }


        this.mPriority = info.priority;
        this.mTranslucentTheme =

                isTranslucentTheme(ctx, info);

        if (!mHasBanner && mIconDrawable != null && mIconDrawable instanceof BitmapDrawable)

        {
            if (mComponentName != null)
                mLaunchColor = ConstData.APP_ITEM_BACK_COLORS[Math.abs(mComponentName.hashCode() % ConstData.APP_ITEM_BACK_COLORS.length)];
            else
                mLaunchColor = ctx.getResources().getColor(R.color.banner_background);
        } else

        {
            this.mLaunchColor = getColor(ctx, info);
        }
        this.mPackageInstallTime = Util.getInstallTimeForPackage(ctx, this.mPackageName);
        return this;
    }

    public void resetAppCategory() {
        this.mAppCategory = null;

        if (hasGameFlag) {
            this.mAppCategory = AppCategory.GAME;
        }

        String pkg = this.getPackageName().toLowerCase();

        for (String s : ConstData.VIDEO_FILTER) {
            if (pkg.contains(s)) {
                this.mAppCategory = AppCategory.VIDEO;
                return;
            }
        }

        for (String s : ConstData.MUSIC_FILTER) {
            if (pkg.contains(s)) {
                this.mAppCategory = AppCategory.MUSIC;
                return;
            }
        }

        this.mAppCategory = AppCategory.OTHER;
    }

    public void resetAppCategory(ActivityInfo actInfo) {
        if (this.mAppCategory == null) {
            if (actInfo != null) {
                if ((actInfo.applicationInfo.flags & 33554432) != 0 || (actInfo.applicationInfo.metaData != null && actInfo.applicationInfo.metaData.getBoolean("isGame", false))) {
                    this.mAppCategory = AppCategory.GAME;

                    hasGameFlag = true;
                } else {
                    hasGameFlag = false;
                }
            }
        }

        String pkg = this.getPackageName().toLowerCase();

        if (this.mAppCategory == null) {
            for (String s : ConstData.VIDEO_FILTER) {
                if (pkg.contains(s)) {
                    this.mAppCategory = AppCategory.VIDEO;
                    break;
                }
            }
        }

        if (this.mAppCategory == null) {
            for (String s : ConstData.MUSIC_FILTER) {
                if (pkg.contains(s)) {
                    this.mAppCategory = AppCategory.MUSIC;
                    break;
                }
            }
        }

        if (this.mAppCategory == null) {
            this.mAppCategory = AppCategory.OTHER; // todo
        }
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
        this.mSettingsType = SettingsUtil.Type.UNKNOWN.getCode();
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

    private static boolean isTranslucentTheme(Context myContext, ResolveInfo info) {
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
        DrawableDownloader.getInstance(context).cancelDownload(this.mBitmapCallback);
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
