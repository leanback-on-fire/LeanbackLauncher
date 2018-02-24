package com.bumptech.glide.request;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableTransformation;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawableTransformation;
import com.bumptech.glide.signature.EmptySignature;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseRequestOptions<CHILD extends BaseRequestOptions<CHILD>> implements Cloneable {
    private DiskCacheStrategy diskCacheStrategy = DiskCacheStrategy.AUTOMATIC;
    private int errorId;
    private Drawable errorPlaceholder;
    private Drawable fallbackDrawable;
    private int fallbackId;
    private int fields;
    private boolean isAutoCloneEnabled;
    private boolean isCacheable = true;
    private boolean isLocked;
    private boolean isTransformationAllowed = true;
    private boolean isTransformationRequired;
    private Options options = new Options();
    private int overrideHeight = -1;
    private int overrideWidth = -1;
    private Drawable placeholderDrawable;
    private int placeholderId;
    private Priority priority = Priority.NORMAL;
    private Class<?> resourceClass = Object.class;
    private Key signature = EmptySignature.obtain();
    private float sizeMultiplier = 1.0f;
    private Theme theme;
    private Map<Class<?>, Transformation<?>> transformations = new HashMap();
    private boolean useUnlimitedSourceGeneratorsPool;

    public final CHILD sizeMultiplier(float sizeMultiplier) {
        if (this.isAutoCloneEnabled) {
            return clone().sizeMultiplier(sizeMultiplier);
        }
        if (sizeMultiplier < 0.0f || sizeMultiplier > 1.0f) {
            throw new IllegalArgumentException("sizeMultiplier must be between 0 and 1");
        }
        this.sizeMultiplier = sizeMultiplier;
        this.fields |= 2;
        return selfOrThrowIfLocked();
    }

    public final CHILD diskCacheStrategy(DiskCacheStrategy strategy) {
        if (this.isAutoCloneEnabled) {
            return clone().diskCacheStrategy(strategy);
        }
        this.diskCacheStrategy = (DiskCacheStrategy) Preconditions.checkNotNull(strategy);
        this.fields |= 4;
        return selfOrThrowIfLocked();
    }

    public final CHILD priority(Priority priority) {
        if (this.isAutoCloneEnabled) {
            return clone().priority(priority);
        }
        this.priority = (Priority) Preconditions.checkNotNull(priority);
        this.fields |= 8;
        return selfOrThrowIfLocked();
    }

    public final CHILD skipMemoryCache(boolean skip) {
        boolean z = true;
        if (this.isAutoCloneEnabled) {
            return clone().skipMemoryCache(true);
        }
        if (skip) {
            z = false;
        }
        this.isCacheable = z;
        this.fields |= 256;
        return selfOrThrowIfLocked();
    }

    public final CHILD override(int width, int height) {
        if (this.isAutoCloneEnabled) {
            return clone().override(width, height);
        }
        this.overrideWidth = width;
        this.overrideHeight = height;
        this.fields |= 512;
        return selfOrThrowIfLocked();
    }

    public final CHILD signature(Key signature) {
        if (this.isAutoCloneEnabled) {
            return clone().signature(signature);
        }
        this.signature = (Key) Preconditions.checkNotNull(signature);
        this.fields |= 1024;
        return selfOrThrowIfLocked();
    }

    public final CHILD clone() {
        try {
            BaseRequestOptions<CHILD> result = (BaseRequestOptions) super.clone();
            result.options = new Options();
            result.options.putAll(this.options);
            result.transformations = new HashMap();
            result.transformations.putAll(this.transformations);
            result.isLocked = false;
            result.isAutoCloneEnabled = false;
            return result;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public final CHILD decode(Class<?> resourceClass) {
        if (this.isAutoCloneEnabled) {
            return clone().decode(resourceClass);
        }
        this.resourceClass = (Class) Preconditions.checkNotNull(resourceClass);
        this.fields |= 4096;
        return selfOrThrowIfLocked();
    }

    public CHILD transform(Context context, Transformation<Bitmap> transformation) {
        if (this.isAutoCloneEnabled) {
            return clone().transform(context, transformation);
        }
        optionalTransform(context, (Transformation) transformation);
        this.isTransformationRequired = true;
        this.fields |= 131072;
        return selfOrThrowIfLocked();
    }

    public CHILD optionalTransform(Context context, Transformation<Bitmap> transformation) {
        if (this.isAutoCloneEnabled) {
            return clone().optionalTransform(context, (Transformation) transformation);
        }
        optionalTransform(Bitmap.class, (Transformation) transformation);
        optionalTransform(BitmapDrawable.class, new BitmapDrawableTransformation(context, transformation));
        optionalTransform(GifDrawable.class, new GifDrawableTransformation(context, (Transformation) transformation));
        return selfOrThrowIfLocked();
    }

    public final <T> CHILD optionalTransform(Class<T> resourceClass, Transformation<T> transformation) {
        if (this.isAutoCloneEnabled) {
            return clone().optionalTransform((Class) resourceClass, (Transformation) transformation);
        }
        Preconditions.checkNotNull(resourceClass);
        Preconditions.checkNotNull(transformation);
        this.transformations.put(resourceClass, transformation);
        this.fields |= 2048;
        this.isTransformationAllowed = true;
        this.fields |= 65536;
        return selfOrThrowIfLocked();
    }

    public final CHILD apply(BaseRequestOptions<?> other) {
        if (this.isAutoCloneEnabled) {
            return clone().apply(other);
        }
        if (isSet(other.fields, 2)) {
            this.sizeMultiplier = other.sizeMultiplier;
        }
        if (isSet(other.fields, 262144)) {
            this.useUnlimitedSourceGeneratorsPool = other.useUnlimitedSourceGeneratorsPool;
        }
        if (isSet(other.fields, 4)) {
            this.diskCacheStrategy = other.diskCacheStrategy;
        }
        if (isSet(other.fields, 8)) {
            this.priority = other.priority;
        }
        if (isSet(other.fields, 16)) {
            this.errorPlaceholder = other.errorPlaceholder;
        }
        if (isSet(other.fields, 32)) {
            this.errorId = other.errorId;
        }
        if (isSet(other.fields, 64)) {
            this.placeholderDrawable = other.placeholderDrawable;
        }
        if (isSet(other.fields, 128)) {
            this.placeholderId = other.placeholderId;
        }
        if (isSet(other.fields, 256)) {
            this.isCacheable = other.isCacheable;
        }
        if (isSet(other.fields, 512)) {
            this.overrideWidth = other.overrideWidth;
            this.overrideHeight = other.overrideHeight;
        }
        if (isSet(other.fields, 1024)) {
            this.signature = other.signature;
        }
        if (isSet(other.fields, 4096)) {
            this.resourceClass = other.resourceClass;
        }
        if (isSet(other.fields, 8192)) {
            this.fallbackDrawable = other.fallbackDrawable;
        }
        if (isSet(other.fields, 16384)) {
            this.fallbackId = other.fallbackId;
        }
        if (isSet(other.fields, 32768)) {
            this.theme = other.theme;
        }
        if (isSet(other.fields, 65536)) {
            this.isTransformationAllowed = other.isTransformationAllowed;
        }
        if (isSet(other.fields, 131072)) {
            this.isTransformationRequired = other.isTransformationRequired;
        }
        if (isSet(other.fields, 2048)) {
            this.transformations.putAll(other.transformations);
        }
        if (!this.isTransformationAllowed) {
            this.transformations.clear();
            this.fields &= -2049;
            this.isTransformationRequired = false;
            this.fields &= -131073;
        }
        this.fields |= other.fields;
        this.options.putAll(other.options);
        return selfOrThrowIfLocked();
    }

    public final CHILD lock() {
        this.isLocked = true;
        return this;
    }

    public final CHILD autoLock() {
        if (!this.isLocked || this.isAutoCloneEnabled) {
            this.isAutoCloneEnabled = true;
            return lock();
        }
        throw new IllegalStateException("You cannot auto lock an already locked options object, try clone() first");
    }

    private CHILD selfOrThrowIfLocked() {
        if (!this.isLocked) {
            return this;
        }
        throw new IllegalStateException("You cannot modify locked RequestOptions, consider clone()");
    }

    public final Map<Class<?>, Transformation<?>> getTransformations() {
        return this.transformations;
    }

    public final boolean isTransformationRequired() {
        return this.isTransformationRequired;
    }

    public final Options getOptions() {
        return this.options;
    }

    public final Class<?> getResourceClass() {
        return this.resourceClass;
    }

    public final DiskCacheStrategy getDiskCacheStrategy() {
        return this.diskCacheStrategy;
    }

    public final Drawable getErrorPlaceholder() {
        return this.errorPlaceholder;
    }

    public final int getErrorId() {
        return this.errorId;
    }

    public final int getPlaceholderId() {
        return this.placeholderId;
    }

    public final Drawable getPlaceholderDrawable() {
        return this.placeholderDrawable;
    }

    public final int getFallbackId() {
        return this.fallbackId;
    }

    public final Drawable getFallbackDrawable() {
        return this.fallbackDrawable;
    }

    public final Theme getTheme() {
        return this.theme;
    }

    public final boolean isMemoryCacheable() {
        return this.isCacheable;
    }

    public final Key getSignature() {
        return this.signature;
    }

    public final boolean isPrioritySet() {
        return isSet(8);
    }

    public final Priority getPriority() {
        return this.priority;
    }

    public final int getOverrideWidth() {
        return this.overrideWidth;
    }

    public final boolean isValidOverride() {
        return Util.isValidDimensions(this.overrideWidth, this.overrideHeight);
    }

    public final int getOverrideHeight() {
        return this.overrideHeight;
    }

    public final float getSizeMultiplier() {
        return this.sizeMultiplier;
    }

    private boolean isSet(int flag) {
        return isSet(this.fields, flag);
    }

    private static boolean isSet(int fields, int flag) {
        return (fields & flag) != 0;
    }

    public final boolean getUseUnlimitedSourceGeneratorsPool() {
        return this.useUnlimitedSourceGeneratorsPool;
    }
}
