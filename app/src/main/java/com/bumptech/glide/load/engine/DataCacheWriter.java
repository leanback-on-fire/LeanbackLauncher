package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.cache.DiskCache.Writer;
import java.io.File;

class DataCacheWriter<DataType> implements Writer {
    private final DataType data;
    private final Encoder<DataType> encoder;
    private final Options options;

    DataCacheWriter(Encoder<DataType> encoder, DataType data, Options options) {
        this.encoder = encoder;
        this.data = data;
        this.options = options;
    }

    public boolean write(File file) {
        return this.encoder.encode(this.data, file, this.options);
    }
}
