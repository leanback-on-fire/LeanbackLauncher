package com.bumptech.glide.manager;

import android.content.Context;
import com.bumptech.glide.manager.ConnectivityMonitor.ConnectivityListener;

public interface ConnectivityMonitorFactory {
    ConnectivityMonitor build(Context context, ConnectivityListener connectivityListener);
}
