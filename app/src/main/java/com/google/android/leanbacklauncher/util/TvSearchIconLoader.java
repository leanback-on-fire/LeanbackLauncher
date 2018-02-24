package com.google.android.leanbacklauncher.util;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

public class TvSearchIconLoader extends AsyncTaskLoader<Drawable> {
    private ContentObserver mContentObserver = null;
    private Drawable mTvSearchIcon = null;

    public TvSearchIconLoader(Context context) {
        super(context);
    }

    protected void onStartLoading() {
        if (this.mTvSearchIcon != null) {
            deliverResult(this.mTvSearchIcon);
        }
        if (this.mContentObserver == null) {
            this.mContentObserver = new ContentObserver(new Handler()) {
                public void onChange(boolean selfChange) {
                    TvSearchIconLoader.this.onContentChanged();
                }

                public void onChange(boolean selfChange, Uri uri) {
                    onChange(selfChange);
                }
            };
            try {
                getContext().getContentResolver().registerContentObserver(SearchWidgetInfoContract.ICON_CONTENT_URI, true, this.mContentObserver);
            } catch (SecurityException e) {
                Log.e("TvSearchIconLdr", "Exception in onStartLoading() on registering content observer", e);
                this.mContentObserver = null;
            }
        }
        if (takeContentChanged() || this.mTvSearchIcon == null) {
            forceLoad();
        }
    }

    protected void onReset() {
        onStopLoading();
        this.mTvSearchIcon = null;
        if (this.mContentObserver != null) {
            getContext().getContentResolver().unregisterContentObserver(this.mContentObserver);
            this.mContentObserver = null;
        }
    }

    protected void onStopLoading() {
        cancelLoad();
    }

    public Drawable loadInBackground() {
        Cursor data;
        Throwable th;
        Throwable th2;
        this.mTvSearchIcon = null;
        try {
            data = getContext().getContentResolver().query(SearchWidgetInfoContract.ICON_CONTENT_URI, null, null, null, null);
            th = null;
            if (data != null) {
                try {
                    if (data.moveToFirst()) {
                        String iconResource = data.getString(0);
                        if (!TextUtils.isEmpty(iconResource)) {
                            try {
                                Resources resources = getContext().getPackageManager().getResourcesForApplication("com.google.android.katniss");
                                this.mTvSearchIcon = resources.getDrawable(resources.getIdentifier(iconResource, "drawable", "com.google.android.katniss"), null);
                            } catch (NameNotFoundException e) {
                            }
                        }
                    }
                } catch (Throwable th22) {
                    Throwable th3 = th22;
                    th22 = th;
                    th = th3;
                }
            }
            if (data != null) {
                if (null != null) {
                    try {
                        data.close();
                    } catch (Throwable th222) {
                        th.addSuppressed(th222);
                    }
                } else {
                    data.close();
                }
            }
        } catch (Exception e2) {
            Log.e("TvSearchIconLdr", "Exception in loadInBackground()", e2);
        }
        return this.mTvSearchIcon;
        if (data != null) {
            if (th222 != null) {
                try {
                    data.close();
                } catch (Throwable th4) {
                    th222.addSuppressed(th4);
                }
            } else {
                data.close();
            }
        }
        throw th;
        throw th;
    }
}
