package com.bumptech.glide.load.engine;

import android.util.Log;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.engine.DataFetcherGenerator.FetcherReadyCallback;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.util.LogTime;
import java.util.Collections;
import java.util.List;

class SourceGenerator implements DataCallback<Object>, DataFetcherGenerator, FetcherReadyCallback {
    private final FetcherReadyCallback cb;
    private Object dataToCache;
    private final DecodeHelper<?> helper;
    private volatile LoadData<?> loadData;
    private int loadDataListIndex;
    private DataCacheKey originalKey;
    private DataCacheGenerator sourceCacheGenerator;

    public SourceGenerator(DecodeHelper<?> helper, FetcherReadyCallback cb) {
        this.helper = helper;
        this.cb = cb;
    }

    public boolean startNext() {
        if (this.dataToCache != null) {
            Object data = this.dataToCache;
            this.dataToCache = null;
            cacheData(data);
        }
        if (this.sourceCacheGenerator != null && this.sourceCacheGenerator.startNext()) {
            return true;
        }
        this.sourceCacheGenerator = null;
        this.loadData = null;
        boolean started = false;
        while (!started && hasNextModelLoader()) {
            List loadData = this.helper.getLoadData();
            int i = this.loadDataListIndex;
            this.loadDataListIndex = i + 1;
            this.loadData = (LoadData) loadData.get(i);
            if (this.loadData != null && (this.helper.getDiskCacheStrategy().isDataCacheable(this.loadData.fetcher.getDataSource()) || this.helper.hasLoadPath(this.loadData.fetcher.getDataClass()))) {
                started = true;
                this.loadData.fetcher.loadData(this.helper.getPriority(), this);
            }
        }
        return started;
    }

    private boolean hasNextModelLoader() {
        return this.loadDataListIndex < this.helper.getLoadData().size();
    }

    private void cacheData(Object dataToCache) {
        long startTime = LogTime.getLogTime();
        try {
            Encoder<Object> encoder = this.helper.getSourceEncoder(dataToCache);
            DataCacheWriter<Object> writer = new DataCacheWriter(encoder, dataToCache, this.helper.getOptions());
            this.originalKey = new DataCacheKey(this.loadData.sourceKey, this.helper.getSignature());
            this.helper.getDiskCache().put(this.originalKey, writer);
            if (Log.isLoggable("SourceGenerator", 2)) {
                String valueOf = String.valueOf(this.originalKey);
                String valueOf2 = String.valueOf(dataToCache);
                String valueOf3 = String.valueOf(encoder);
                Log.v("SourceGenerator", new StringBuilder(((String.valueOf(valueOf).length() + 95) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length()).append("Finished encoding source to cache, key: ").append(valueOf).append(", data: ").append(valueOf2).append(", encoder: ").append(valueOf3).append(", duration: ").append(LogTime.getElapsedMillis(startTime)).toString());
            }
            this.loadData.fetcher.cleanup();
            this.sourceCacheGenerator = new DataCacheGenerator(Collections.singletonList(this.loadData.sourceKey), this.helper, this);
        } catch (Throwable th) {
            this.loadData.fetcher.cleanup();
        }
    }

    public void cancel() {
        LoadData<?> local = this.loadData;
        if (local != null) {
            local.fetcher.cancel();
        }
    }

    public void onDataReady(Object data) {
        DiskCacheStrategy diskCacheStrategy = this.helper.getDiskCacheStrategy();
        if (data == null || !diskCacheStrategy.isDataCacheable(this.loadData.fetcher.getDataSource())) {
            this.cb.onDataFetcherReady(this.loadData.sourceKey, data, this.loadData.fetcher, this.loadData.fetcher.getDataSource(), this.originalKey);
            return;
        }
        this.dataToCache = data;
        this.cb.reschedule();
    }

    public void onLoadFailed(Exception e) {
        this.cb.onDataFetcherFailed(this.originalKey, e, this.loadData.fetcher, this.loadData.fetcher.getDataSource());
    }

    public void reschedule() {
        throw new UnsupportedOperationException();
    }

    public void onDataFetcherReady(Key sourceKey, Object data, DataFetcher<?> fetcher, DataSource dataSource, Key attemptedKey) {
        this.cb.onDataFetcherReady(sourceKey, data, fetcher, this.loadData.fetcher.getDataSource(), sourceKey);
    }

    public void onDataFetcherFailed(Key sourceKey, Exception e, DataFetcher<?> fetcher, DataSource dataSource) {
        this.cb.onDataFetcherFailed(sourceKey, e, fetcher, this.loadData.fetcher.getDataSource());
    }
}
