package com.rockon999.android.leanbacklauncher.wallpaper;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.rockon999.android.leanbacklauncher.R;
import com.rockon999.android.leanbacklauncher.util.Util;

import java.io.IOException;

public class WallpaperInstaller {
    private static WallpaperInstaller sInstance; // todo remove memory leak
    private Context mContext;
    private boolean mInstallationPending;
    private boolean mInstallingWallpaper;
    private boolean mWallpaperInstalled;

    /* renamed from: WallpaperInstaller.1 */
    class C02071 extends AsyncTask<Void, Void, Void> {
        C02071() {
        }

        protected Void doInBackground(Void... params) {
            WallpaperInstaller.this.installWallpaper();
            return null;
        }
    }

    public static final class WallpaperChangedReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            WallpaperInstaller.getInstance(context).onWallpaperChanged();
        }
    }

    public static WallpaperInstaller getInstance(Context context) {
        if (sInstance == null) {
            synchronized (WallpaperInstaller.class) {
                if (sInstance == null) {
                    sInstance = new WallpaperInstaller(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private WallpaperInstaller(Context context) {
        boolean z = false;
        this.mContext = context;
        if (PreferenceManager.getDefaultSharedPreferences(this.mContext).getInt("wallpaper_version", 0) == 5) {
            z = true;
        }
        this.mWallpaperInstalled = z;
    }

    public void installWallpaperIfNeeded() {
        if (!this.mWallpaperInstalled && !this.mInstallationPending) {
            this.mInstallationPending = true;
            new C02071().execute();
        }
    }

    public Bitmap getWallpaperBitmap() {
        Resources resources = this.mContext.getResources();
        Drawable systemBg = resources.getDrawable(R.drawable.bg_default, null);
        int intrinsicWidth = systemBg.getIntrinsicWidth();
        int intrinsicHeight = systemBg.getIntrinsicHeight();
        int wallpaperWidth = Util.getDisplayMetrics(this.mContext).widthPixels;
        int wallpaperHeight = (wallpaperWidth * intrinsicHeight) / intrinsicWidth;
        Bitmap bitmap = Bitmap.createBitmap(wallpaperWidth, wallpaperHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(-16777216);
        systemBg.setBounds(0, 0, wallpaperWidth, wallpaperHeight);
        systemBg.draw(canvas);


        Bitmap maskBitmap = BitmapFactory.decodeResource(resources, R.drawable.bg_protection);

        BitmapDrawable maskDrawable = new BitmapDrawable(resources, maskBitmap);
        maskDrawable.setTileModeX(TileMode.REPEAT);
        maskDrawable.setTileModeY(TileMode.CLAMP);
        maskDrawable.setBounds(0, 0, wallpaperWidth, wallpaperHeight);
        maskDrawable.draw(canvas);
        return bitmap;
    }

    private void installWallpaper() {
        try {
            Log.i("WallpaperInstaller", "Installing wallpaper");
            this.mInstallingWallpaper = true;
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this.mContext);
            Bitmap bitmap = getWallpaperBitmap();
            wallpaperManager.suggestDesiredDimensions(bitmap.getWidth(), bitmap.getHeight());
            wallpaperManager.setBitmap(bitmap);
        } catch (IOException e) {
            Log.e("WallpaperInstaller", "Cannot install wallpaper", e);
            this.mInstallingWallpaper = false;
        }
    }

    private void onWallpaperChanged() {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        if (this.mInstallingWallpaper) {
            edit.putInt("wallpaper_version", 5);
            this.mInstallingWallpaper = false;
        } else {
            edit.remove("wallpaper_version");
        }
        edit.apply();
    }
}
