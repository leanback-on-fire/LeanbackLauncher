package com.rockon999.android.leanbacklauncher.util;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

public class TvSearchSuggestionsLoader extends AsyncTaskLoader<String[]> {
    private ContentObserver mContentObserver = null;
    private String[] mSearchSuggestions = null;

    public TvSearchSuggestionsLoader(Context context) {
        super(context);
    }

    protected void onStartLoading() {
        if (this.mSearchSuggestions != null) {
            deliverResult(this.mSearchSuggestions);
        }
        if (this.mContentObserver == null) {
            this.mContentObserver = new ContentObserver(new Handler()) {
                public void onChange(boolean selfChange) {
                    TvSearchSuggestionsLoader.this.onContentChanged();
                }

                public void onChange(boolean selfChange, Uri uri) {
                    onChange(selfChange);
                }
            };
            try {
                getContext().getContentResolver().registerContentObserver(SearchWidgetInfoContract.SUGGESTIONS_CONTENT_URI, true, this.mContentObserver);
            } catch (SecurityException e) {
                Log.e("TvSearchSuggestionsLdr", "Exception in onStartLoading() on registering content observer", e);
                this.mContentObserver = null;
            }
        }
        if (takeContentChanged() || this.mSearchSuggestions == null) {
            forceLoad();
        }
    }

    protected void onStopLoading() {
        cancelLoad();
    }

    protected void onReset() {
        onStopLoading();
        this.mSearchSuggestions = null;
        if (this.mContentObserver != null) {
            getContext().getContentResolver().unregisterContentObserver(this.mContentObserver);
            this.mContentObserver = null;
        }
    }

    public String[] loadInBackground() {
        Cursor data;
        this.mSearchSuggestions = null;
// FIXME
//        try {
//            data = getContext().getContentResolver().query(SearchWidgetInfoContract.SUGGESTIONS_CONTENT_URI, null, null, null, null);
//
//            if (data != null) {
//                try {
//                    if (data.moveToFirst()) {
//                        int size = data.getCount();
//                        this.mSearchSuggestions = new String[size];
//                        for (int i = 0; i < size; i++) {
//                            this.mSearchSuggestions[i] = data.getString(0);
//                            data.moveToNext();
//                        }
//                    }
//                } catch (Throwable ignored) {
//                }
//            }
//
//            if (data != null) {
//                data.close();
//            }
//        } catch (Exception e) {
//            Log.e("TvSearchSuggestionsLdr", "Exception in loadInBackground()", e);
//        }

        return this.mSearchSuggestions;
    }
}
