package com.google.android.tvlauncher.util;

import android.content.AsyncTaskLoader;
import android.content.Context;

public abstract class LauncherAsyncTaskLoader<T> extends AsyncTaskLoader<T> {
    private T mResult;

    public LauncherAsyncTaskLoader(Context context) {
        super(context);
    }

    protected void onStartLoading() {
        if (this.mResult != null) {
            deliverResult(this.mResult);
        }
        if (takeContentChanged() || this.mResult == null) {
            forceLoad();
        }
    }

    protected void onStopLoading() {
        cancelLoad();
    }

    public void deliverResult(T data) {
        if (!isReset()) {
            this.mResult = data;
            if (isStarted()) {
                super.deliverResult(data);
            }
        }
    }

    protected void onReset() {
        super.onReset();
        onStopLoading();
        this.mResult = null;
    }
}
