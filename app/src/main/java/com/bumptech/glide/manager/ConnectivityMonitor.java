package com.bumptech.glide.manager;

public abstract interface ConnectivityMonitor
  extends LifecycleListener
{
  public static abstract interface ConnectivityListener
  {
    public abstract void onConnectivityChanged(boolean paramBoolean);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/manager/ConnectivityMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */