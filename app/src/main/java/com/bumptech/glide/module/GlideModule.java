package com.bumptech.glide.module;

import android.content.Context;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;

public interface GlideModule {
    void applyOptions(Context context, GlideBuilder glideBuilder);

    void registerComponents(Context context, Registry registry);
}
