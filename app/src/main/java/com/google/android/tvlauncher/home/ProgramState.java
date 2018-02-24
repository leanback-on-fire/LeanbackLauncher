package com.google.android.tvlauncher.home;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface ProgramState {
    public static final int DEFAULT = 0;
    public static final int DEFAULT_SELECTED = 1;
    public static final int ZOOMED_OUT = 2;
}
