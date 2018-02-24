package com.google.android.leanbacklauncher.capabilities;

public abstract class LauncherConfiguration {
    private static LauncherConfiguration sInstance;

    public abstract boolean isCardElevationEnabled();

    public abstract boolean isLegacyRecommendationLayoutEnabled();

    public abstract boolean isRichRecommendationViewEnabled();

    public abstract boolean isRoundCornersEnabled();

    public static LauncherConfiguration getInstance() {
        return sInstance;
    }

    public static void setInstance(LauncherConfiguration capabilities) {
        sInstance = capabilities;
    }
}
