package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.engine.DataFetcherGenerator.FetcherReadyCallback;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import java.io.File;
import java.util.List;

class ResourceCacheGenerator implements DataCallback<Object>, DataFetcherGenerator {
    private File cacheFile;
    private final FetcherReadyCallback cb;
    private ResourceCacheKey currentKey;
    private final DecodeHelper<?> helper;
    private volatile LoadData<?> loadData;
    private int modelLoaderIndex;
    private List<ModelLoader<File, ?>> modelLoaders;
    private int resourceClassIndex = -1;
    private int sourceIdIndex = 0;
    private Key sourceKey;

    public ResourceCacheGenerator(DecodeHelper<?> helper, FetcherReadyCallback cb) {
        this.helper = helper;
        this.cb = cb;
    }

    public boolean startNext() {
        boolean z = false;
        List<Key> sourceIds = this.helper.getCacheKeys();
        if (!sourceIds.isEmpty()) {
            List<Class<?>> resourceClasses = this.helper.getRegisteredResourceClasses();
            while (true) {
                if (this.modelLoaders != null && hasNextModelLoader()) {
                    break;
                }
                this.resourceClassIndex++;
                if (this.resourceClassIndex >= resourceClasses.size()) {
                    this.sourceIdIndex++;
                    if (this.sourceIdIndex >= sourceIds.size()) {
                        break;
                    }
                    this.resourceClassIndex = 0;
                }
                Key sourceId = (Key) sourceIds.get(this.sourceIdIndex);
                Class<?> resourceClass = (Class) resourceClasses.get(this.resourceClassIndex);
                this.currentKey = new ResourceCacheKey(sourceId, this.helper.getSignature(), this.helper.getWidth(), this.helper.getHeight(), this.helper.getTransformation(resourceClass), resourceClass, this.helper.getOptions());
                this.cacheFile = this.helper.getDiskCache().get(this.currentKey);
                if (this.cacheFile != null) {
                    this.sourceKey = sourceId;
                    this.modelLoaders = this.helper.getModelLoaders(this.cacheFile);
                    this.modelLoaderIndex = 0;
                }
            }
            this.loadData = null;
            z = false;
            while (!z && hasNextModelLoader()) {
                List list = this.modelLoaders;
                int i = this.modelLoaderIndex;
                this.modelLoaderIndex = i + 1;
                this.loadData = ((ModelLoader) list.get(i)).buildLoadData(this.cacheFile, this.helper.getWidth(), this.helper.getHeight(), this.helper.getOptions());
                if (this.loadData != null && this.helper.hasLoadPath(this.loadData.fetcher.getDataClass())) {
                    z = true;
                    this.loadData.fetcher.loadData(this.helper.getPriority(), this);
                }
            }
        }
        return z;
    }

    private boolean hasNextModelLoader() {
        return this.modelLoaderIndex < this.modelLoaders.size();
    }

    public void cancel() {
        LoadData<?> local = this.loadData;
        if (local != null) {
            local.fetcher.cancel();
        }
    }

    public void onDataReady(Object data) {
        this.cb.onDataFetcherReady(this.sourceKey, data, this.loadData.fetcher, DataSource.RESOURCE_DISK_CACHE, this.currentKey);
    }

    public void onLoadFailed(Exception e) {
        this.cb.onDataFetcherFailed(this.currentKey, e, this.loadData.fetcher, DataSource.RESOURCE_DISK_CACHE);
    }
}
