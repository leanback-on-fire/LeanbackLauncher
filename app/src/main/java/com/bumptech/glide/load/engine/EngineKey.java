package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.util.Preconditions;
import java.security.MessageDigest;
import java.util.Map;

class EngineKey implements Key {
    private int hashCode;
    private final int height;
    private final Object model;
    private final Options options;
    private final Class<?> resourceClass;
    private final Key signature;
    private final Class<?> transcodeClass;
    private final Map<Class<?>, Transformation<?>> transformations;
    private final int width;

    public EngineKey(Object model, Key signature, int width, int height, Map<Class<?>, Transformation<?>> transformations, Class<?> resourceClass, Class<?> transcodeClass, Options options) {
        this.model = Preconditions.checkNotNull(model);
        this.signature = (Key) Preconditions.checkNotNull(signature, "Signature must not be null");
        this.width = width;
        this.height = height;
        this.transformations = (Map) Preconditions.checkNotNull(transformations);
        this.resourceClass = (Class) Preconditions.checkNotNull(resourceClass, "Resource class must not be null");
        this.transcodeClass = (Class) Preconditions.checkNotNull(transcodeClass, "Transcode class must not be null");
        this.options = (Options) Preconditions.checkNotNull(options);
    }

    public boolean equals(Object o) {
        if (!(o instanceof EngineKey)) {
            return false;
        }
        EngineKey other = (EngineKey) o;
        if (this.model.equals(other.model) && this.signature.equals(other.signature) && this.height == other.height && this.width == other.width && this.transformations.equals(other.transformations) && this.resourceClass.equals(other.resourceClass) && this.transcodeClass.equals(other.transcodeClass) && this.options.equals(other.options)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = this.model.hashCode();
            this.hashCode = (this.hashCode * 31) + this.signature.hashCode();
            this.hashCode = (this.hashCode * 31) + this.width;
            this.hashCode = (this.hashCode * 31) + this.height;
            this.hashCode = (this.hashCode * 31) + this.transformations.hashCode();
            this.hashCode = (this.hashCode * 31) + this.resourceClass.hashCode();
            this.hashCode = (this.hashCode * 31) + this.transcodeClass.hashCode();
            this.hashCode = (this.hashCode * 31) + this.options.hashCode();
        }
        return this.hashCode;
    }

    public String toString() {
        String valueOf = String.valueOf(this.model);
        int i = this.width;
        int i2 = this.height;
        String valueOf2 = String.valueOf(this.resourceClass);
        String valueOf3 = String.valueOf(this.transcodeClass);
        String valueOf4 = String.valueOf(this.signature);
        int i3 = this.hashCode;
        String valueOf5 = String.valueOf(this.transformations);
        String valueOf6 = String.valueOf(this.options);
        return new StringBuilder((((((String.valueOf(valueOf).length() + 151) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length()) + String.valueOf(valueOf4).length()) + String.valueOf(valueOf5).length()) + String.valueOf(valueOf6).length()).append("EngineKey{model=").append(valueOf).append(", width=").append(i).append(", height=").append(i2).append(", resourceClass=").append(valueOf2).append(", transcodeClass=").append(valueOf3).append(", signature=").append(valueOf4).append(", hashCode=").append(i3).append(", transformations=").append(valueOf5).append(", options=").append(valueOf6).append("}").toString();
    }

    public void updateDiskCacheKey(MessageDigest messageDigest) {
        throw new UnsupportedOperationException();
    }
}
