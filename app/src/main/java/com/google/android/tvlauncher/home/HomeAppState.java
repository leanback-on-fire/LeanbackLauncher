package com.google.android.tvlauncher.home;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@interface HomeAppState {
    public static final int DEFAULT = 0;
    public static final int DEFAULT_ABOVE_SELECTED = 1;
    public static final int DEFAULT_SELECTED = 2;
    public static final int ZOOMED_OUT = 3;
}
