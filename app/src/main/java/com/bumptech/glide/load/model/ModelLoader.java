package com.bumptech.glide.load.model;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.util.Preconditions;
import java.util.Collections;
import java.util.List;

public interface ModelLoader<Model, Data> {

    public static class LoadData<Data> {
        public final List<Key> alternateKeys;
        public final DataFetcher<Data> fetcher;
        public final Key sourceKey;

        public LoadData(Key sourceKey, DataFetcher<Data> fetcher) {
            this(sourceKey, Collections.emptyList(), fetcher);
        }

        public LoadData(Key sourceKey, List<Key> alternateKeys, DataFetcher<Data> fetcher) {
            this.sourceKey = (Key) Preconditions.checkNotNull(sourceKey);
            this.alternateKeys = (List) Preconditions.checkNotNull(alternateKeys);
            this.fetcher = (DataFetcher) Preconditions.checkNotNull(fetcher);
        }
    }

    LoadData<Data> buildLoadData(Model model, int i, int i2, Options options);

    boolean handles(Model model);
}
