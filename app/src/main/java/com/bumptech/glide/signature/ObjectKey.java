package com.bumptech.glide.signature;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.util.Preconditions;
import java.security.MessageDigest;

public final class ObjectKey implements Key {
    private final Object object;

    public ObjectKey(Object object) {
        this.object = Preconditions.checkNotNull(object);
    }

    public String toString() {
        String valueOf = String.valueOf(this.object);
        return new StringBuilder(String.valueOf(valueOf).length() + 18).append("ObjectKey{object=").append(valueOf).append("}").toString();
    }

    public boolean equals(Object o) {
        if (!(o instanceof ObjectKey)) {
            return false;
        }
        return this.object.equals(((ObjectKey) o).object);
    }

    public int hashCode() {
        return this.object.hashCode();
    }

    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(this.object.toString().getBytes(CHARSET));
    }
}
