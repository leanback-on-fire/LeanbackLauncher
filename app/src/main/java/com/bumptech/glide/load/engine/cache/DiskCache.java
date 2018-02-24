package com.bumptech.glide.load.engine.cache;

import com.bumptech.glide.load.Key;
import java.io.File;

public interface DiskCache {

    public interface Writer {
        boolean write(File file);
    }

    public interface Factory {
        DiskCache build();
    }

    File get(Key key);

    void put(Key key, Writer writer);
}
