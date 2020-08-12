package com.amazon.tv.leanbacklauncher.util;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
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

    // todo this is a mess
    public Drawable loadInBackground() {
        Cursor data;
        this.mTvSearchIcon = null;

// FIXME
//        try {
//            data = getContext().getContentResolver().query(SearchWidgetInfoContract.ICON_CONTENT_URI, null, null, null, null);
//
//            Resources resources = null;
//
//            if (data != null) {
//                if (data.moveToFirst()) {
//                    String iconResource = data.getString(0);
//                    if (!TextUtils.isEmpty(iconResource)) {
//                        try {
//                            resources = getContext().getPackageManager().getResourcesForApplication("com.google.android.katniss");
//                            this.mTvSearchIcon = resources.getDrawable(resources.getIdentifier(iconResource, "drawable", "com.google.android.katniss"), null);
//                        } catch (NameNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//            if (data != null) {
//                data.close();
//            }
//
//        } catch (Exception e) {
//            Log.e("TvSearchIconLoader", "Exception in loadInBackground()", e);
//        }
        return this.mTvSearchIcon;
    }
}