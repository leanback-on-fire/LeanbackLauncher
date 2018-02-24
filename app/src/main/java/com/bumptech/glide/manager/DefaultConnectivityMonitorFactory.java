package com.bumptech.glide.manager;

import android.content.Context;
import com.bumptech.glide.manager.ConnectivityMonitor.ConnectivityListener;

public class DefaultConnectivityMonitorFactory implements ConnectivityMonitorFactory {
    public ConnectivityMonitor build(Context context, ConnectivityListener listener) {
        if (context.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") == 0) {
            return new DefaultConnectivityMonitor(context, listener);
        }
        return new NullConnectivityMonitor();
    }
}
