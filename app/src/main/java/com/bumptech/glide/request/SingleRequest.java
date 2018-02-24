package com.bumptech.glide.request;

import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.util.Pools.Pool;
import android.util.Log;
import com.bumptech.glide.GlideContext;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.Engine;
import com.bumptech.glide.load.engine.Engine.LoadStatus;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.TransitionFactory;
import com.bumptech.glide.util.LogTime;
import com.bumptech.glide.util.Util;
import com.bumptech.glide.util.pool.FactoryPools;
import com.bumptech.glide.util.pool.FactoryPools.Factory;
import com.bumptech.glide.util.pool.FactoryPools.Poolable;
import com.bumptech.glide.util.pool.StateVerifier;

public final class SingleRequest<R> implements Request, ResourceCallback, SizeReadyCallback, Poolable {
    private static final Pool<SingleRequest<?>> POOL = FactoryPools.simple(150, new Factory<SingleRequest<?>>() {
        public SingleRequest<?> create() {
            return new SingleRequest();
        }
    });
    private TransitionFactory<? super R> animationFactory;
    private Engine engine;
    private Drawable errorDrawable;
    private Drawable fallbackDrawable;
    private GlideContext glideContext;
    private int height;
    private LoadStatus loadStatus;
    private Object model;
    private int overrideHeight;
    private int overrideWidth;
    private Drawable placeholderDrawable;
    private Priority priority;
    private RequestCoordinator requestCoordinator;
    private RequestListener<R> requestListener;
    private BaseRequestOptions<?> requestOptions;
    private Resource<R> resource;
    private long startTime;
    private final StateVerifier stateVerifier;
    private Status status;
    private final String tag;
    private Target<R> target;
    private Class<R> transcodeClass;
    private int width;

    private enum Status {
        PENDING,
        RUNNING,
        WAITING_FOR_SIZE,
        COMPLETE,
        FAILED,
        CANCELLED,
        CLEARED,
        PAUSED
    }

    public static <R> SingleRequest<R> obtain(GlideContext glideContext, Object model, Class<R> transcodeClass, BaseRequestOptions<?> requestOptions, int overrideWidth, int overrideHeight, Priority priority, Target<R> target, RequestListener<R> requestListener, RequestCoordinator requestCoordinator, Engine engine, TransitionFactory<? super R> animationFactory) {
        SingleRequest<R> request = (SingleRequest) POOL.acquire();
        if (request == null) {
            request = new SingleRequest();
        }
        request.init(glideContext, model, transcodeClass, requestOptions, overrideWidth, overrideHeight, priority, target, requestListener, requestCoordinator, engine, animationFactory);
        return request;
    }

    private SingleRequest() {
        this.tag = String.valueOf(hashCode());
        this.stateVerifier = StateVerifier.newInstance();
    }

    private void init(GlideContext glideContext, Object model, Class<R> transcodeClass, BaseRequestOptions<?> requestOptions, int overrideWidth, int overrideHeight, Priority priority, Target<R> target, RequestListener<R> requestListener, RequestCoordinator requestCoordinator, Engine engine, TransitionFactory<? super R> animationFactory) {
        this.glideContext = glideContext;
        this.model = model;
        this.transcodeClass = transcodeClass;
        this.requestOptions = requestOptions;
        this.overrideWidth = overrideWidth;
        this.overrideHeight = overrideHeight;
        this.priority = priority;
        this.target = target;
        this.requestListener = requestListener;
        this.requestCoordinator = requestCoordinator;
        this.engine = engine;
        this.animationFactory = animationFactory;
        this.status = Status.PENDING;
    }

    public StateVerifier getVerifier() {
        return this.stateVerifier;
    }

    public void recycle() {
        this.glideContext = null;
        this.model = null;
        this.transcodeClass = null;
        this.requestOptions = null;
        this.overrideWidth = -1;
        this.overrideHeight = -1;
        this.target = null;
        this.requestListener = null;
        this.requestCoordinator = null;
        this.animationFactory = null;
        this.loadStatus = null;
        this.errorDrawable = null;
        this.placeholderDrawable = null;
        this.fallbackDrawable = null;
        this.width = -1;
        this.height = -1;
        POOL.release(this);
    }

    public void begin() {
        this.stateVerifier.throwIfRecycled();
        this.startTime = LogTime.getLogTime();
        if (this.model == null) {
            if (Util.isValidDimensions(this.overrideWidth, this.overrideHeight)) {
                this.width = this.overrideWidth;
                this.height = this.overrideHeight;
            }
            onLoadFailed(new GlideException("Received null model"), getFallbackDrawable() == null ? 5 : 3);
            return;
        }
        this.status = Status.WAITING_FOR_SIZE;
        if (Util.isValidDimensions(this.overrideWidth, this.overrideHeight)) {
            onSizeReady(this.overrideWidth, this.overrideHeight);
        } else {
            this.target.getSize(this);
        }
        if ((this.status == Status.RUNNING || this.status == Status.WAITING_FOR_SIZE) && canNotifyStatusChanged()) {
            this.target.onLoadStarted(getPlaceholderDrawable());
        }
        if (Log.isLoggable("Request", 2)) {
            logV("finished run method in " + LogTime.getElapsedMillis(this.startTime));
        }
    }

    void cancel() {
        this.stateVerifier.throwIfRecycled();
        this.status = Status.CANCELLED;
        if (this.loadStatus != null) {
            this.loadStatus.cancel();
            this.loadStatus = null;
        }
    }

    public void clear() {
        Util.assertMainThread();
        if (this.status != Status.CLEARED) {
            cancel();
            if (this.resource != null) {
                releaseResource(this.resource);
            }
            if (canNotifyStatusChanged()) {
                this.target.onLoadCleared(getPlaceholderDrawable());
            }
            this.status = Status.CLEARED;
        }
    }

    public boolean isPaused() {
        return this.status == Status.PAUSED;
    }

    public void pause() {
        clear();
        this.status = Status.PAUSED;
    }

    private void releaseResource(Resource resource) {
        this.engine.release(resource);
        this.resource = null;
    }

    public boolean isRunning() {
        return this.status == Status.RUNNING || this.status == Status.WAITING_FOR_SIZE;
    }

    public boolean isComplete() {
        return this.status == Status.COMPLETE;
    }

    public boolean isResourceSet() {
        return isComplete();
    }

    public boolean isCancelled() {
        return this.status == Status.CANCELLED || this.status == Status.CLEARED;
    }

    private Drawable getErrorDrawable() {
        if (this.errorDrawable == null) {
            this.errorDrawable = this.requestOptions.getErrorPlaceholder();
            if (this.errorDrawable == null && this.requestOptions.getErrorId() > 0) {
                this.errorDrawable = loadDrawable(this.requestOptions.getErrorId());
            }
        }
        return this.errorDrawable;
    }

    private Drawable getPlaceholderDrawable() {
        if (this.placeholderDrawable == null) {
            this.placeholderDrawable = this.requestOptions.getPlaceholderDrawable();
            if (this.placeholderDrawable == null && this.requestOptions.getPlaceholderId() > 0) {
                this.placeholderDrawable = loadDrawable(this.requestOptions.getPlaceholderId());
            }
        }
        return this.placeholderDrawable;
    }

    private Drawable getFallbackDrawable() {
        if (this.fallbackDrawable == null) {
            this.fallbackDrawable = this.requestOptions.getFallbackDrawable();
            if (this.fallbackDrawable == null && this.requestOptions.getFallbackId() > 0) {
                this.fallbackDrawable = loadDrawable(this.requestOptions.getFallbackId());
            }
        }
        return this.fallbackDrawable;
    }

    private Drawable loadDrawable(int resourceId) {
        return ResourcesCompat.getDrawable(this.glideContext.getResources(), resourceId, this.requestOptions.getTheme());
    }

    private void setErrorPlaceholder() {
        if (canNotifyStatusChanged()) {
            Drawable error = this.model == null ? getFallbackDrawable() : getErrorDrawable();
            if (error == null) {
                error = getPlaceholderDrawable();
            }
            this.target.onLoadFailed(error);
        }
    }

    public void onSizeReady(int width, int height) {
        this.stateVerifier.throwIfRecycled();
        if (Log.isLoggable("Request", 2)) {
            logV("Got onSizeReady in " + LogTime.getElapsedMillis(this.startTime));
        }
        if (this.status == Status.WAITING_FOR_SIZE) {
            this.status = Status.RUNNING;
            float sizeMultiplier = this.requestOptions.getSizeMultiplier();
            this.width = maybeApplySizeMultiplier(width, sizeMultiplier);
            this.height = maybeApplySizeMultiplier(height, sizeMultiplier);
            if (Log.isLoggable("Request", 2)) {
                logV("finished setup for calling load in " + LogTime.getElapsedMillis(this.startTime));
            }
            this.loadStatus = this.engine.load(this.glideContext, this.model, this.requestOptions.getSignature(), this.width, this.height, this.requestOptions.getResourceClass(), this.transcodeClass, this.priority, this.requestOptions.getDiskCacheStrategy(), this.requestOptions.getTransformations(), this.requestOptions.isTransformationRequired(), this.requestOptions.getOptions(), this.requestOptions.isMemoryCacheable(), this.requestOptions.getUseUnlimitedSourceGeneratorsPool(), this);
            if (Log.isLoggable("Request", 2)) {
                logV("finished onSizeReady in " + LogTime.getElapsedMillis(this.startTime));
            }
        }
    }

    private static int maybeApplySizeMultiplier(int size, float sizeMultiplier) {
        return size == Integer.MIN_VALUE ? size : Math.round(((float) size) * sizeMultiplier);
    }

    private boolean canSetResource() {
        return this.requestCoordinator == null || this.requestCoordinator.canSetImage(this);
    }

    private boolean canNotifyStatusChanged() {
        return this.requestCoordinator == null || this.requestCoordinator.canNotifyStatusChanged(this);
    }

    private boolean isFirstReadyResource() {
        return this.requestCoordinator == null || !this.requestCoordinator.isAnyResourceSet();
    }

    private void notifyLoadSuccess() {
        if (this.requestCoordinator != null) {
            this.requestCoordinator.onRequestSuccess(this);
        }
    }

    public void onResourceReady(Resource<?> resource, DataSource dataSource) {
        this.stateVerifier.throwIfRecycled();
        this.loadStatus = null;
        if (resource == null) {
            String valueOf = String.valueOf(this.transcodeClass);
            onLoadFailed(new GlideException(new StringBuilder(String.valueOf(valueOf).length() + 82).append("Expected to receive a Resource<R> with an object of ").append(valueOf).append(" inside, but instead got null.").toString()));
            return;
        }
        Object received = resource.get();
        if (received == null || !this.transcodeClass.isAssignableFrom(received.getClass())) {
            releaseResource(resource);
            String valueOf2 = String.valueOf(this.transcodeClass);
            String valueOf3 = String.valueOf(received != null ? received.getClass() : "");
            String valueOf4 = String.valueOf(received);
            String valueOf5 = String.valueOf(resource);
            valueOf = received != null ? "" : " To indicate failure return a null Resource object, rather than a Resource object containing null data.";
            onLoadFailed(new GlideException(new StringBuilder(((((String.valueOf(valueOf2).length() + 71) + String.valueOf(valueOf3).length()) + String.valueOf(valueOf4).length()) + String.valueOf(valueOf5).length()) + String.valueOf(valueOf).length()).append("Expected to receive an object of ").append(valueOf2).append(" but instead got ").append(valueOf3).append("{").append(valueOf4).append("} inside Resource{").append(valueOf5).append("}.").append(valueOf).toString()));
        } else if (canSetResource()) {
            onResourceReady(resource, received, dataSource);
        } else {
            releaseResource(resource);
            this.status = Status.COMPLETE;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onResourceReady(com.bumptech.glide.load.engine.Resource<R> r16, R r17, com.bumptech.glide.load.DataSource r18) {
        /*
        r15 = this;
        r7 = r15.isFirstReadyResource();
        r2 = com.bumptech.glide.request.SingleRequest.Status.COMPLETE;
        r15.status = r2;
        r0 = r16;
        r15.resource = r0;
        r2 = r15.glideContext;
        r2 = r2.getLogLevel();
        r3 = 3;
        if (r2 > r3) goto L_0x00a1;
    L_0x0015:
        r2 = "Glide";
        r3 = r17.getClass();
        r3 = r3.getSimpleName();
        r3 = java.lang.String.valueOf(r3);
        r4 = java.lang.String.valueOf(r18);
        r5 = r15.model;
        r5 = java.lang.String.valueOf(r5);
        r6 = r15.width;
        r9 = r15.height;
        r10 = r15.startTime;
        r10 = com.bumptech.glide.util.LogTime.getElapsedMillis(r10);
        r12 = new java.lang.StringBuilder;
        r13 = java.lang.String.valueOf(r3);
        r13 = r13.length();
        r13 = r13 + 95;
        r14 = java.lang.String.valueOf(r4);
        r14 = r14.length();
        r13 = r13 + r14;
        r14 = java.lang.String.valueOf(r5);
        r14 = r14.length();
        r13 = r13 + r14;
        r12.<init>(r13);
        r13 = "Finished loading ";
        r12 = r12.append(r13);
        r3 = r12.append(r3);
        r12 = " from ";
        r3 = r3.append(r12);
        r3 = r3.append(r4);
        r4 = " for ";
        r3 = r3.append(r4);
        r3 = r3.append(r5);
        r4 = " with size [";
        r3 = r3.append(r4);
        r3 = r3.append(r6);
        r4 = "x";
        r3 = r3.append(r4);
        r3 = r3.append(r9);
        r4 = "] in ";
        r3 = r3.append(r4);
        r3 = r3.append(r10);
        r4 = " ms";
        r3 = r3.append(r4);
        r3 = r3.toString();
        android.util.Log.d(r2, r3);
    L_0x00a1:
        r2 = r15.requestListener;
        if (r2 == 0) goto L_0x00b5;
    L_0x00a5:
        r2 = r15.requestListener;
        r4 = r15.model;
        r5 = r15.target;
        r3 = r17;
        r6 = r18;
        r2 = r2.onResourceReady(r3, r4, r5, r6, r7);
        if (r2 != 0) goto L_0x00c4;
    L_0x00b5:
        r2 = r15.animationFactory;
        r0 = r18;
        r8 = r2.build(r0, r7);
        r2 = r15.target;
        r0 = r17;
        r2.onResourceReady(r0, r8);
    L_0x00c4:
        r15.notifyLoadSuccess();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.request.SingleRequest.onResourceReady(com.bumptech.glide.load.engine.Resource, java.lang.Object, com.bumptech.glide.load.DataSource):void");
    }

    public void onLoadFailed(GlideException e) {
        onLoadFailed(e, 5);
    }

    private void onLoadFailed(GlideException e, int maxLogLevel) {
        this.stateVerifier.throwIfRecycled();
        int logLevel = this.glideContext.getLogLevel();
        if (logLevel <= maxLogLevel) {
            String valueOf = String.valueOf(this.model);
            int i = this.width;
            Log.w("Glide", new StringBuilder(String.valueOf(valueOf).length() + 52).append("Load failed for ").append(valueOf).append(" with size [").append(i).append("x").append(this.height).append("]").toString(), e);
            if (logLevel <= 4) {
                e.logRootCauses("Glide");
            }
        }
        this.loadStatus = null;
        this.status = Status.FAILED;
        if (this.requestListener == null || !this.requestListener.onLoadFailed(e, this.model, this.target, isFirstReadyResource())) {
            setErrorPlaceholder();
        }
    }

    private void logV(String message) {
        String str = this.tag;
        Log.v("Request", new StringBuilder((String.valueOf(message).length() + 7) + String.valueOf(str).length()).append(message).append(" this: ").append(str).toString());
    }
}
