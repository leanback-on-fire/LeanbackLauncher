package com.bumptech.glide.manager;

import android.content.Context;
import android.support.annotation.NonNull;

public class DefaultConnectivityMonitorFactory
  implements ConnectivityMonitorFactory
{
  @NonNull
  public ConnectivityMonitor build(@NonNull Context paramContext, @NonNull ConnectivityMonitor.ConnectivityListener paramConnectivityListener)
  {
    if (paramContext.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") == 0) {}
    for (int i = 1; i != 0; i = 0) {
      return new DefaultConnectivityMonitor(paramContext, paramConnectivityListener);
    }
    return new NullConnectivityMonitor();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/manager/DefaultConnectivityMonitorFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */