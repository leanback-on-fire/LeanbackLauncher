package com.google.android.leanbacklauncher.wallpaper;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.android.leanbacklauncher.R;
import com.google.android.leanbacklauncher.util.Partner;
import com.google.android.leanbacklauncher.util.Util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WallpaperInstaller {
    private static WallpaperInstaller sInstance;
    private Context mContext;
    private boolean mInstallationPending;
    private boolean mInstallingWallpaper;
    private boolean mWallpaperInstalled;

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
            new AsyncTask<Void, Void, Void>() {
                protected Void doInBackground(Void... params) {
                    WallpaperInstaller.this.installWallpaper();
                    return null;
                }
            }.execute(new Void[0]);
        }
    }

    public Bitmap getWallpaperBitmap() {
        Bitmap bitmap;
        Throwable th;
        File file = new File(this.mContext.getCacheDir(), "wp5");
        if (file.exists()) {
            bitmap = null;
            try {
                bitmap = BitmapFactory.decodeFile(file.getPath());
            } catch (Exception e) {
                Log.e("WallpaperInstaller", "Cannot load wallpaper from file " + file);
            }
            if (bitmap == null) {
                file.delete();
            }
            return bitmap;
        }
        Resources resources = this.mContext.getResources();
        Partner partner = Partner.get(this.mContext);
        Drawable systemBg = partner.getSystemBackground();
        if (systemBg == null) {
            systemBg = resources.getDrawable(R.drawable.bg_default, null);
        }
        int intrinsicWidth = systemBg.getIntrinsicWidth();
        int intrinsicHeight = systemBg.getIntrinsicHeight();
        int wallpaperWidth = Util.getDisplayMetrics(this.mContext).widthPixels;
        int wallpaperHeight = (wallpaperWidth * intrinsicHeight) / intrinsicWidth;
        bitmap = Bitmap.createBitmap(wallpaperWidth, (int) (((float) wallpaperWidth) / 1.333f), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(-16777216);
        systemBg.setBounds(0, 0, wallpaperWidth, wallpaperHeight);
        systemBg.draw(canvas);
        Bitmap maskBitmap = partner.getSystemBackgroundMask();
        if (maskBitmap == null) {
            maskBitmap = BitmapFactory.decodeResource(resources, R.drawable.bg_protection);
        }
        BitmapDrawable maskDrawable = new BitmapDrawable(resources, maskBitmap);
        maskDrawable.setTileModeX(TileMode.REPEAT);
        maskDrawable.setTileModeY(TileMode.CLAMP);
        maskDrawable.setBounds(0, 0, wallpaperWidth, wallpaperHeight);
        maskDrawable.draw(canvas);
        try {
            FileOutputStream out = new FileOutputStream(file);
            Throwable th2 = null;
            try {
                bitmap.compress(CompressFormat.JPEG, 90, out);
                if (out != null) {
                    if (null != null) {
                        try {
                            out.close();
                        } catch (Throwable th3) {
                            null.addSuppressed(th3);
                        }
                    } else {
                        out.close();
                    }
                }
                return bitmap;
            } catch (Throwable th22) {
                Throwable th4 = th22;
                th22 = th3;
                th3 = th4;
            }
            if (out != null) {
                if (th22 != null) {
                    try {
                        out.close();
                    } catch (Throwable th5) {
                        th22.addSuppressed(th5);
                    }
                } else {
                    out.close();
                }
            }
            throw th3;
            throw th3;
        } catch (IOException e2) {
            Log.e("WallpaperInstaller", "Cannot cache wallpaper in file " + file, e2);
        }
    }

    private void installWallpaper() {
        Throwable e;
        try {
            Log.i("WallpaperInstaller", "Installing wallpaper");
            this.mInstallingWallpaper = true;
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this.mContext);
            Bitmap bitmap = getWallpaperBitmap();
            wallpaperManager.suggestDesiredDimensions(bitmap.getWidth(), bitmap.getHeight());
            wallpaperManager.setBitmap(bitmap);
            return;
        } catch (IOException e2) {
            e = e2;
        } catch (OutOfMemoryError e3) {
            e = e3;
        }
        Log.e("WallpaperInstaller", "Cannot install wallpaper", e);
        this.mInstallingWallpaper = false;
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
