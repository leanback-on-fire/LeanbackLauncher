package com.google.android.tvlauncher.home;

import android.view.View;

public interface HomeRow {
    View getView();

    void setOnHomeRowRemovedListener(OnHomeRowRemovedListener onHomeRowRemovedListener);

    void setOnHomeRowSelectedListener(OnHomeRowSelectedListener onHomeRowSelectedListener);

    void setOnHomeStateChangeListener(OnHomeStateChangeListener onHomeStateChangeListener);
}
