package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.Key;

interface EngineJobListener {
    void onEngineJobCancelled(EngineJob engineJob, Key key);

    void onEngineJobComplete(Key key, EngineResource<?> engineResource);
}
