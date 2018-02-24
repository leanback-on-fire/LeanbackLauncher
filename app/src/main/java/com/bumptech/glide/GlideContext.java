package com.bumptech.glide;

import android.annotation.TargetApi;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTargetFactory;

@TargetApi(14)
public class GlideContext extends ContextWrapper implements ComponentCallbacks2 {
    private final ComponentCallbacks2 componentCallbacks;
    private final RequestOptions defaultRequestOptions;
    private final Engine engine;
    private final ImageViewTargetFactory imageViewTargetFactory;
    private final int logLevel;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Registry registry;

    public GlideContext(Context context, Registry registry, ImageViewTargetFactory imageViewTargetFactory, RequestOptions defaultRequestOptions, Engine engine, ComponentCallbacks2 componentCallbacks, int logLevel) {
        super(context.getApplicationContext());
        this.registry = registry;
        this.imageViewTargetFactory = imageViewTargetFactory;
        this.defaultRequestOptions = defaultRequestOptions;
        this.engine = engine;
        this.componentCallbacks = componentCallbacks;
        this.logLevel = logLevel;
    }

    public RequestOptions getDefaultRequestOptions() {
        return this.defaultRequestOptions;
    }

    public Engine getEngine() {
        return this.engine;
    }

    public Registry getRegistry() {
        return this.registry;
    }

    public int getLogLevel() {
        return this.logLevel;
    }

    public void onTrimMemory(int level) {
        this.componentCallbacks.onTrimMemory(level);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        this.componentCallbacks.onConfigurationChanged(newConfig);
    }

    public void onLowMemory() {
        this.componentCallbacks.onLowMemory();
    }
}
