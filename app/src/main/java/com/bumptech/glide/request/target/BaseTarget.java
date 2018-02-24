package com.bumptech.glide.request.target;

import android.graphics.drawable.Drawable;
import com.bumptech.glide.request.Request;

public abstract class BaseTarget<Z> implements Target<Z> {
    private Request request;

    public void setRequest(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return this.request;
    }

    public void onLoadCleared(Drawable placeholder) {
    }

    public void onLoadStarted(Drawable placeholder) {
    }

    public void onLoadFailed(Drawable errorDrawable) {
    }

    public void onStart() {
    }

    public void onStop() {
    }

    public void onDestroy() {
    }
}
