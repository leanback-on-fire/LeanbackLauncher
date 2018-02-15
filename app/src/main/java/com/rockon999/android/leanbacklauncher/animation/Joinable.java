package com.rockon999.android.leanbacklauncher.animation;

import android.view.View;

public interface Joinable {
    void exclude(View view);

    void include(View view);
}
