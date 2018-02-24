package com.bumptech.glide.manager;

public interface LifecycleListener {
    void onDestroy();

    void onStart();

    void onStop();
}
