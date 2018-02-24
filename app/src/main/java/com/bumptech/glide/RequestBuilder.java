package com.bumptech.glide;

import android.widget.ImageView.ScaleType;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestCoordinator;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.SingleRequest;
import com.bumptech.glide.request.ThumbnailRequestCoordinator;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;

public class RequestBuilder<TranscodeType> implements Cloneable {
    private static final TransitionOptions<?, ?> DEFAULT_ANIMATION_OPTIONS = new GenericTransitionOptions();
    private static final BaseRequestOptions<?> DOWNLOAD_ONLY_OPTIONS = ((RequestOptions) ((RequestOptions) new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA)).priority(Priority.LOW)).skipMemoryCache(true);
    private final GlideContext context;
    private final BaseRequestOptions<?> defaultRequestOptions;
    private boolean isModelSet;
    private boolean isThumbnailBuilt;
    private Object model;
    private RequestListener<TranscodeType> requestListener;
    private final RequestManager requestManager;
    private BaseRequestOptions<?> requestOptions;
    private Float thumbSizeMultiplier;
    private RequestBuilder<TranscodeType> thumbnailBuilder;
    private final Class<TranscodeType> transcodeClass;
    private TransitionOptions<?, ? super TranscodeType> transitionOptions = DEFAULT_ANIMATION_OPTIONS;

    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$android$widget$ImageView$ScaleType = new int[ScaleType.values().length];

        static {
            $SwitchMap$com$bumptech$glide$Priority = new int[Priority.values().length];
            try {
                $SwitchMap$com$bumptech$glide$Priority[Priority.LOW.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$bumptech$glide$Priority[Priority.NORMAL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$bumptech$glide$Priority[Priority.HIGH.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$bumptech$glide$Priority[Priority.IMMEDIATE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.CENTER_CROP.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.CENTER_INSIDE.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.FIT_CENTER.ordinal()] = 3;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.FIT_START.ordinal()] = 4;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.FIT_END.ordinal()] = 5;
            } catch (NoSuchFieldError e9) {
            }
        }
    }

    RequestBuilder(GlideContext context, RequestManager requestManager, Class<TranscodeType> transcodeClass) {
        this.requestManager = requestManager;
        this.context = (GlideContext) Preconditions.checkNotNull(context);
        this.transcodeClass = transcodeClass;
        this.defaultRequestOptions = requestManager.getDefaultRequestOptions();
        this.requestOptions = this.defaultRequestOptions;
    }

    public RequestBuilder<TranscodeType> apply(BaseRequestOptions<?> requestOptions) {
        Preconditions.checkNotNull(requestOptions);
        this.requestOptions = (this.defaultRequestOptions == this.requestOptions ? this.requestOptions.clone() : this.requestOptions).apply(requestOptions);
        return this;
    }

    public RequestBuilder<TranscodeType> transition(TransitionOptions<?, ? super TranscodeType> transitionOptions) {
        this.transitionOptions = (TransitionOptions) Preconditions.checkNotNull(transitionOptions);
        return this;
    }

    public RequestBuilder<TranscodeType> load(Object model) {
        return loadGeneric(model);
    }

    private RequestBuilder<TranscodeType> loadGeneric(Object model) {
        this.model = model;
        this.isModelSet = true;
        return this;
    }

    public RequestBuilder<TranscodeType> load(String string) {
        return loadGeneric(string);
    }

    public RequestBuilder<TranscodeType> clone() {
        try {
            RequestBuilder<TranscodeType> result = (RequestBuilder) super.clone();
            result.requestOptions = result.requestOptions.clone();
            result.transitionOptions = result.transitionOptions.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public <Y extends Target<TranscodeType>> Y into(Y target) {
        Util.assertMainThread();
        Preconditions.checkNotNull(target);
        if (this.isModelSet) {
            if (target.getRequest() != null) {
                this.requestManager.clear(target);
            }
            this.requestOptions.lock();
            Request request = buildRequest(target);
            target.setRequest(request);
            this.requestManager.track(target, request);
            return target;
        }
        throw new IllegalArgumentException("You must call #load() before calling #into()");
    }

    private Priority getThumbnailPriority(Priority current) {
        switch (current) {
            case LOW:
                return Priority.NORMAL;
            case NORMAL:
                return Priority.HIGH;
            case HIGH:
            case IMMEDIATE:
                return Priority.IMMEDIATE;
            default:
                String valueOf = String.valueOf(this.requestOptions.getPriority());
                throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 18).append("unknown priority: ").append(valueOf).toString());
        }
    }

    private Request buildRequest(Target<TranscodeType> target) {
        return buildRequestRecursive(target, null, this.transitionOptions, this.requestOptions.getPriority(), this.requestOptions.getOverrideWidth(), this.requestOptions.getOverrideHeight());
    }

    private Request buildRequestRecursive(Target<TranscodeType> target, ThumbnailRequestCoordinator parentCoordinator, TransitionOptions<?, ? super TranscodeType> transitionOptions, Priority priority, int overrideWidth, int overrideHeight) {
        ThumbnailRequestCoordinator coordinator;
        if (this.thumbnailBuilder != null) {
            if (this.isThumbnailBuilt) {
                throw new IllegalStateException("You cannot use a request as both the main request and a thumbnail, consider using clone() on the request(s) passed to thumbnail()");
            }
            Priority thumbPriority;
            TransitionOptions<?, ? super TranscodeType> thumbTransitionOptions = this.thumbnailBuilder.transitionOptions;
            if (DEFAULT_ANIMATION_OPTIONS.equals(thumbTransitionOptions)) {
                thumbTransitionOptions = transitionOptions;
            }
            if (this.thumbnailBuilder.requestOptions.isPrioritySet()) {
                thumbPriority = this.thumbnailBuilder.requestOptions.getPriority();
            } else {
                thumbPriority = getThumbnailPriority(priority);
            }
            int thumbOverrideWidth = this.thumbnailBuilder.requestOptions.getOverrideWidth();
            int thumbOverrideHeight = this.thumbnailBuilder.requestOptions.getOverrideHeight();
            if (Util.isValidDimensions(overrideWidth, overrideHeight) && !this.thumbnailBuilder.requestOptions.isValidOverride()) {
                thumbOverrideWidth = this.requestOptions.getOverrideWidth();
                thumbOverrideHeight = this.requestOptions.getOverrideHeight();
            }
            coordinator = new ThumbnailRequestCoordinator(parentCoordinator);
            Request fullRequest = obtainRequest(target, this.requestOptions, coordinator, transitionOptions, priority, overrideWidth, overrideHeight);
            this.isThumbnailBuilt = true;
            Request thumbRequest = this.thumbnailBuilder.buildRequestRecursive(target, coordinator, thumbTransitionOptions, thumbPriority, thumbOverrideWidth, thumbOverrideHeight);
            this.isThumbnailBuilt = false;
            coordinator.setRequests(fullRequest, thumbRequest);
            return coordinator;
        } else if (this.thumbSizeMultiplier != null) {
            coordinator = new ThumbnailRequestCoordinator(parentCoordinator);
            coordinator.setRequests(obtainRequest(target, this.requestOptions, coordinator, transitionOptions, priority, overrideWidth, overrideHeight), obtainRequest(target, this.requestOptions.clone().sizeMultiplier(this.thumbSizeMultiplier.floatValue()), coordinator, transitionOptions, getThumbnailPriority(priority), overrideWidth, overrideHeight));
            return coordinator;
        } else {
            return obtainRequest(target, this.requestOptions, parentCoordinator, transitionOptions, priority, overrideWidth, overrideHeight);
        }
    }

    private Request obtainRequest(Target<TranscodeType> target, BaseRequestOptions<?> requestOptions, RequestCoordinator requestCoordinator, TransitionOptions<?, ? super TranscodeType> transitionOptions, Priority priority, int overrideWidth, int overrideHeight) {
        requestOptions.lock();
        return SingleRequest.obtain(this.context, this.model, this.transcodeClass, requestOptions, overrideWidth, overrideHeight, priority, target, this.requestListener, requestCoordinator, this.context.getEngine(), transitionOptions.getTransitionFactory());
    }
}
