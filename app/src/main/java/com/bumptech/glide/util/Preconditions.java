package com.bumptech.glide.util;

import android.text.TextUtils;
import java.util.Collection;

public final class Preconditions {
    public static void checkArgument(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T> T checkNotNull(T arg) {
        return checkNotNull(arg, "Argument must not be null");
    }

    public static <T> T checkNotNull(T arg, String message) {
        if (arg != null) {
            return arg;
        }
        throw new NullPointerException(message);
    }

    public static String checkNotEmpty(String string) {
        if (!TextUtils.isEmpty(string)) {
            return string;
        }
        throw new IllegalArgumentException("Must not be null or empty");
    }

    public static <T extends Collection<Y>, Y> T checkNotEmpty(T collection) {
        if (!collection.isEmpty()) {
            return collection;
        }
        throw new IllegalArgumentException("Must not be empty.");
    }
}
