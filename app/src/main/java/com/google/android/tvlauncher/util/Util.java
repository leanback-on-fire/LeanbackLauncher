package com.google.android.tvlauncher.util;

import android.content.Context;
import android.view.accessibility.AccessibilityManager;

public class Util {
    private static final String TAG = "TVLauncher.Util";

    private Util() {
    }

    public static boolean isRtl(Context context) {
        if (context.getResources().getConfiguration().getLayoutDirection() == 1) {
            return true;
        }
        return false;
    }

    public static boolean isAccessibilityEnabled(Context context) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService("accessibility");
        if (am == null || !am.isEnabled()) {
            return false;
        }
        if (am.isTouchExplorationEnabled() || !am.getEnabledAccessibilityServiceList(16).isEmpty()) {
            return true;
        }
        return false;
    }
}
