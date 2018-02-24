package com.bumptech.glide.manager;

class ApplicationLifecycle implements Lifecycle {
    ApplicationLifecycle() {
    }

    public void addListener(LifecycleListener listener) {
        listener.onStart();
    }

    public void removeListener(LifecycleListener listener) {
    }
}
