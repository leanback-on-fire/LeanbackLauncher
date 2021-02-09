package com.amazon.tv.leanbacklauncher;

import com.amazon.tv.leanbacklauncher.animation.ViewDimmer;

public interface DimmableItem {
    void setDimState(ViewDimmer.DimState dimState, boolean z);
}
