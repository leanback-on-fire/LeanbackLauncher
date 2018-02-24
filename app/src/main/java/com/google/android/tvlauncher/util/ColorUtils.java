package com.google.android.tvlauncher.util;

import android.graphics.Color;
import android.support.annotation.ColorInt;

public class ColorUtils {
    @ColorInt
    public static int darkenColor(@ColorInt int color, float factor) {
        float[] hsv = new float[3];
        Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), hsv);
        hsv[2] = hsv[2] * factor;
        return Color.HSVToColor(hsv);
    }
}
