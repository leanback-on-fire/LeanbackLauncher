package com.bumptech.glide.load;

import android.support.v4.util.ArrayMap;
import java.security.MessageDigest;
import java.util.Map.Entry;

public final class Options implements Key {
    private final ArrayMap<Option<?>, Object> values = new ArrayMap();

    public void putAll(Options other) {
        this.values.putAll(other.values);
    }

    public <T> T get(Option<T> option) {
        return this.values.containsKey(option) ? this.values.get(option) : option.getDefaultValue();
    }

    public boolean equals(Object o) {
        if (!(o instanceof Options)) {
            return false;
        }
        return this.values.equals(((Options) o).values);
    }

    public int hashCode() {
        return this.values.hashCode();
    }

    public void updateDiskCacheKey(MessageDigest messageDigest) {
        for (Entry<Option<?>, Object> entry : this.values.entrySet()) {
            updateDiskCacheKey((Option) entry.getKey(), entry.getValue(), messageDigest);
        }
    }

    public String toString() {
        String valueOf = String.valueOf(this.values);
        return new StringBuilder(String.valueOf(valueOf).length() + 16).append("Options{values=").append(valueOf).append("}").toString();
    }

    private static <T> void updateDiskCacheKey(Option<T> option, Object value, MessageDigest md) {
        option.update(value, md);
    }
}
