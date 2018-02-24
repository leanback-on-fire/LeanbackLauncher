package com.google.android.tvlauncher.appsview;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.home.util.ProgramUtil;
import java.util.Objects;

public class LaunchItem implements Comparable<LaunchItem> {
    static final int PROGRESS_UNKNOWN = -1;
    static final int STATE_UNKNOWN = 0;
    private Drawable mBanner;
    private String mComponentName;
    private Context mContext;
    private Drawable mIcon;
    private int mInstallProgressPercent;
    private int mInstallStateStringResourceId;
    private Intent mIntent;
    private boolean mIsGame;
    private boolean mIsInitialInstall;
    private CharSequence mLabel;
    private String mPkgName;

    private class BitmapResizeTask extends AsyncTask<Void, Void, Bitmap> {
        private Bitmap mImageToResize;
        private boolean mIsBanner;
        private int mMaxHeight;
        private int mMaxWidth;

        BitmapResizeTask(Bitmap imageToResize, int maxWidth, int maxHeight, boolean isBanner) {
            this.mImageToResize = imageToResize;
            this.mMaxWidth = maxWidth;
            this.mMaxHeight = maxHeight;
            this.mIsBanner = isBanner;
        }

        protected Bitmap doInBackground(Void... params) {
            return LaunchItem.getSizeCappedBitmap(this.mImageToResize, this.mMaxWidth, this.mMaxHeight);
        }

        protected void onPostExecute(Bitmap bitmap) {
            if (this.mIsBanner) {
                LaunchItem.this.mBanner = new BitmapDrawable(LaunchItem.this.mContext.getResources(), bitmap);
            } else {
                LaunchItem.this.mIcon = new BitmapDrawable(LaunchItem.this.mContext.getResources(), bitmap);
            }
        }
    }

    LaunchItem(Context context, ResolveInfo info) {
        set(context, info);
        this.mIsInitialInstall = true;
    }

    LaunchItem(Context context, CharSequence appLabel, String pkgName, Intent intent, Drawable banner) {
        init(context, appLabel, pkgName);
        this.mIntent = intent;
        this.mBanner = banner;
    }

    @VisibleForTesting
    LaunchItem(Context context, CharSequence appLabel, String componentName, String pkgName) {
        init(context, appLabel, pkgName);
        this.mComponentName = componentName;
    }

    public LaunchItem(Context context, CharSequence appLabel, String iconUri, String pkgName, Intent launchIntent, boolean isGame, final InstallingLaunchItemListener listener) {
        init(context, appLabel, pkgName);
        if (launchIntent != null) {
            this.mIntent = launchIntent.addFlags(270532608);
            if (this.mIntent.getComponent() != null) {
                this.mComponentName = this.mIntent.getComponent().flattenToString();
            }
        }
        this.mIsGame = isGame;
        this.mIsInitialInstall = true;
        if (!TextUtils.isEmpty(iconUri)) {
            int maxIconSize = context.getResources().getDimensionPixelSize(R.dimen.banner_icon_size);
            Glide.with(context).asDrawable().apply(RequestOptions.skipMemoryCacheOf(true)).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)).load(iconUri).into(new SimpleTarget<Drawable>(maxIconSize, maxIconSize) {
                public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                    if (drawable != null && listener != null) {
                        LaunchItem.this.mIcon = drawable;
                        listener.onInstallingLaunchItemChanged(LaunchItem.this);
                    }
                }
            });
        }
        this.mBanner = null;
    }

    private void init(Context context, CharSequence appLabel, String pkgName) {
        this.mContext = context;
        this.mLabel = appLabel;
        this.mPkgName = pkgName;
    }

    public LaunchItem set(Context context, ResolveInfo info) {
        boolean z;
        clear();
        this.mContext = context;
        this.mIntent = Intent.makeMainActivity(new ComponentName(info.activityInfo.packageName, info.activityInfo.name));
        this.mIntent.addFlags(268435456);
        PackageManager packageManager = context.getPackageManager();
        this.mIcon = info.loadIcon(packageManager);
        this.mLabel = info.loadLabel(packageManager);
        this.mPkgName = this.mIntent.getComponent().getPackageName();
        this.mComponentName = this.mIntent.getComponent().flattenToString();
        if ((info.activityInfo.applicationInfo.flags & 33554432) != 0) {
            z = true;
        } else {
            z = false;
        }
        this.mIsGame = z;
        Resources res = context.getResources();
        this.mBanner = info.activityInfo.loadBanner(packageManager);
        if (this.mBanner instanceof BitmapDrawable) {
            int maxWidth = Math.round(((float) res.getDimensionPixelOffset(R.dimen.banner_width)) * res.getFraction(R.fraction.home_app_banner_focused_scale, 1, 1));
            int maxHeight = Math.round(((float) res.getDimensionPixelOffset(R.dimen.banner_height)) * res.getFraction(R.fraction.home_app_banner_focused_scale, 1, 1));
            if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                new BitmapResizeTask(((BitmapDrawable) this.mBanner).getBitmap(), maxWidth, maxHeight, true).execute(new Void[0]);
            } else {
                this.mBanner = new BitmapDrawable(res, getSizeCappedBitmap(((BitmapDrawable) this.mBanner).getBitmap(), maxWidth, maxHeight));
            }
        }
        return this;
    }

    private static Bitmap getSizeCappedBitmap(Bitmap image, int maxWidth, int maxHeight) {
        if (image == null) {
            return null;
        }
        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();
        if ((imgWidth <= maxWidth && imgHeight <= maxHeight) || imgWidth <= 0 || imgHeight <= 0) {
            return image;
        }
        float scale = Math.min(1.0f, ((float) maxHeight) / ((float) imgHeight));
        if (((double) scale) >= ProgramUtil.ASPECT_RATIO_1_1 && imgWidth <= maxWidth) {
            return image;
        }
        float deltaW = ((float) Math.max(Math.round(((float) imgWidth) * scale) - maxWidth, 0)) / scale;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap newImage = Bitmap.createBitmap(image, (int) (deltaW / 2.0f), 0, (int) (((float) imgWidth) - deltaW), imgHeight, matrix, true);
        if (newImage != null) {
            return newImage;
        }
        return image;
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public Drawable getBanner() {
        return this.mBanner;
    }

    public Drawable getIcon() {
        return this.mIcon;
    }

    public CharSequence getLabel() {
        return this.mLabel;
    }

    public String getPackageName() {
        return this.mPkgName;
    }

    public String getComponentName() {
        return this.mComponentName;
    }

    public Drawable getItemDrawable() {
        if (isInstalling()) {
            if (hasImage(this.mIcon)) {
                return this.mIcon;
            }
            return this.mContext.getResources().getDrawable(R.drawable.ic_file_download_black_24dp, null);
        } else if (hasImage(this.mBanner)) {
            return this.mBanner;
        } else {
            return this.mIcon;
        }
    }

    private boolean hasImage(Drawable drawable) {
        boolean hasImage;
        if (drawable != null) {
            hasImage = true;
        } else {
            hasImage = false;
        }
        if (!(drawable instanceof BitmapDrawable)) {
            return hasImage;
        }
        if (((BitmapDrawable) drawable).getBitmap() != null) {
            return true;
        }
        return false;
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

    public boolean isInitialInstall() {
        return this.mIsInitialInstall;
    }

    public boolean isGame() {
        return this.mIsGame;
    }

    public boolean areContentsTheSame(LaunchItem another) {
        return Objects.equals(another.getBanner(), getBanner()) && Objects.equals(another.getIcon(), getIcon()) && Objects.equals(another.getLabel(), getLabel());
    }

    public int compareTo(@NonNull LaunchItem another) {
        CharSequence o1Label = getLabel();
        CharSequence o2Label = another.getLabel();
        if (o1Label == null) {
            o1Label = getComponentName();
        }
        if (o2Label == null) {
            o2Label = another.getComponentName();
        }
        if (o1Label == null && o2Label == null) {
            return 0;
        }
        if (o1Label == null) {
            return 1;
        }
        if (o2Label == null) {
            return -1;
        }
        return o1Label.toString().compareToIgnoreCase(o2Label.toString());
    }

    public int hashCode() {
        return this.mPkgName.hashCode();
    }

    public String toString() {
        return this.mLabel + " -- " + getComponentName();
    }

    public String toDebugString() {
        return "Label: " + this.mLabel + " Intent: " + this.mIntent + " Banner: " + this.mBanner + " Icon: " + this.mIcon;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LaunchItem)) {
            return false;
        }
        boolean z;
        String pkgName = ((LaunchItem) other).getPackageName();
        String compName = ((LaunchItem) other).getComponentName();
        if (TextUtils.equals(this.mPkgName, pkgName) && TextUtils.equals(this.mComponentName, compName)) {
            z = true;
        } else {
            z = false;
        }
        return z;
    }

    public boolean hasSamePackageName(ResolveInfo info) {
        String pkgName = null;
        String compName = null;
        Intent intent = getLaunchIntent(info);
        if (intent.getComponent() != null) {
            pkgName = intent.getComponent().getPackageName();
            compName = intent.getComponent().flattenToString();
        }
        return TextUtils.equals(this.mPkgName, pkgName) && TextUtils.equals(this.mComponentName, compName);
    }

    public LaunchItem setInstallationState(LaunchItem item) {
        this.mInstallProgressPercent = item.getInstallProgressPercent();
        this.mInstallStateStringResourceId = item.getInstallStateStringResId();
        return this;
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

    private static Intent getLaunchIntent(ResolveInfo info) {
        ComponentName componentName = new ComponentName(info.activityInfo.applicationInfo.packageName, info.activityInfo.name);
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setComponent(componentName);
        intent.addFlags(270532608);
        return intent;
    }

    private void clear() {
        this.mInstallProgressPercent = -1;
        this.mInstallStateStringResourceId = 0;
        this.mIntent = null;
        this.mBanner = null;
        this.mIcon = null;
        this.mLabel = null;
        this.mPkgName = null;
        this.mComponentName = null;
        this.mIsInitialInstall = false;
        this.mIsGame = false;
    }

    public static boolean isSystemApp(Context context, String packageName) {
        try {
            if ((context.getPackageManager().getApplicationInfo(packageName, 0).flags & 1) != 0) {
                return true;
            }
            return false;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
}
