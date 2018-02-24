package com.bumptech.glide.manager;

public interface Lifecycle {
    void addListener(LifecycleListener lifecycleListener);

    void removeListener(LifecycleListener lifecycleListener);
}
