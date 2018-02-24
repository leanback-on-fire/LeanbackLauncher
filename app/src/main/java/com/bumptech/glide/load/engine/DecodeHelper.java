package com.bumptech.glide.load.engine;

import com.bumptech.glide.GlideContext;
import com.bumptech.glide.Priority;
import com.bumptech.glide.Registry.NoModelLoaderAvailableException;
import com.bumptech.glide.Registry.NoSourceEncoderAvailableException;
import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.load.resource.UnitTransformation;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class DecodeHelper<Transcode> {
    private final List<Key> cacheKeys = new ArrayList();
    private DiskCacheProvider diskCacheProvider;
    private DiskCacheStrategy diskCacheStrategy;
    private GlideContext glideContext;
    private int height;
    private boolean isCacheKeysSet;
    private boolean isLoadDataSet;
    private boolean isTransformationRequired;
    private final List<LoadData<?>> loadData = new ArrayList();
    private Object model;
    private Options options;
    private Priority priority;
    private Class<?> resourceClass;
    private Key signature;
    private Class<Transcode> transcodeClass;
    private Map<Class<?>, Transformation<?>> transformations;
    private int width;

    DecodeHelper() {
    }

    <R> DecodeHelper<R> init(GlideContext glideContext, Object model, Key signature, int width, int height, DiskCacheStrategy diskCacheStrategy, Class<?> resourceClass, Class<R> transcodeClass, Priority priority, Options options, Map<Class<?>, Transformation<?>> transformations, boolean isTransformationRequired, DiskCacheProvider diskCacheProvider) {
        this.glideContext = glideContext;
        this.model = model;
        this.signature = signature;
        this.width = width;
        this.height = height;
        this.diskCacheStrategy = diskCacheStrategy;
        this.resourceClass = resourceClass;
        this.diskCacheProvider = diskCacheProvider;
        this.transcodeClass = transcodeClass;
        this.priority = priority;
        this.options = options;
        this.transformations = transformations;
        this.isTransformationRequired = isTransformationRequired;
        return this;
    }

    void clear() {
        this.glideContext = null;
        this.model = null;
        this.signature = null;
        this.resourceClass = null;
        this.transcodeClass = null;
        this.options = null;
        this.priority = null;
        this.transformations = null;
        this.diskCacheStrategy = null;
        this.loadData.clear();
        this.isLoadDataSet = false;
        this.cacheKeys.clear();
        this.isCacheKeysSet = false;
    }

    DiskCache getDiskCache() {
        return this.diskCacheProvider.getDiskCache();
    }

    DiskCacheStrategy getDiskCacheStrategy() {
        return this.diskCacheStrategy;
    }

    Priority getPriority() {
        return this.priority;
    }

    Options getOptions() {
        return this.options;
    }

    Key getSignature() {
        return this.signature;
    }

    int getWidth() {
        return this.width;
    }

    int getHeight() {
        return this.height;
    }

    List<Class<?>> getRegisteredResourceClasses() {
        return this.glideContext.getRegistry().getRegisteredResourceClasses(this.model.getClass(), this.resourceClass, this.transcodeClass);
    }

    boolean hasLoadPath(Class<?> dataClass) {
        return getLoadPath(dataClass) != null;
    }

    <Data> LoadPath<Data, ?, Transcode> getLoadPath(Class<Data> dataClass) {
        return this.glideContext.getRegistry().getLoadPath(dataClass, this.resourceClass, this.transcodeClass);
    }

    <Z> Transformation<Z> getTransformation(Class<Z> resourceClass) {
        Transformation<Z> result = (Transformation) this.transformations.get(resourceClass);
        if (result != null) {
            return result;
        }
        if (!this.transformations.isEmpty() || !this.isTransformationRequired) {
            return UnitTransformation.get();
        }
        String valueOf = String.valueOf(resourceClass);
        throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 115).append("Missing transformation for ").append(valueOf).append(". If you wish to ignore unknown resource types, use the optional transformation methods.").toString());
    }

    boolean isResourceEncoderAvailable(Resource<?> resource) {
        return this.glideContext.getRegistry().isResourceEncoderAvailable(resource);
    }

    <Z> ResourceEncoder<Z> getResultEncoder(Resource<Z> resource) {
        return this.glideContext.getRegistry().getResultEncoder(resource);
    }

    List<ModelLoader<File, ?>> getModelLoaders(File file) throws NoModelLoaderAvailableException {
        return this.glideContext.getRegistry().getModelLoaders(file);
    }

    boolean isSourceKey(Key key) {
        List<LoadData<?>> loadData = getLoadData();
        int size = loadData.size();
        for (int i = 0; i < size; i++) {
            if (((LoadData) loadData.get(i)).sourceKey.equals(key)) {
                return true;
            }
        }
        return false;
    }

    List<LoadData<?>> getLoadData() {
        if (!this.isLoadDataSet) {
            this.isLoadDataSet = true;
            this.loadData.clear();
            List<ModelLoader<Object, ?>> modelLoaders = this.glideContext.getRegistry().getModelLoaders(this.model);
            int size = modelLoaders.size();
            for (int i = 0; i < size; i++) {
                LoadData<?> current = ((ModelLoader) modelLoaders.get(i)).buildLoadData(this.model, this.width, this.height, this.options);
                if (current != null) {
                    this.loadData.add(current);
                }
            }
        }
        return this.loadData;
    }

    List<Key> getCacheKeys() {
        if (!this.isCacheKeysSet) {
            this.isCacheKeysSet = true;
            this.cacheKeys.clear();
            List<LoadData<?>> loadData = getLoadData();
            int size = loadData.size();
            for (int i = 0; i < size; i++) {
                LoadData<?> data = (LoadData) loadData.get(i);
                this.cacheKeys.add(data.sourceKey);
                this.cacheKeys.addAll(data.alternateKeys);
            }
        }
        return this.cacheKeys;
    }

    <X> Encoder<X> getSourceEncoder(X data) throws NoSourceEncoderAvailableException {
        return this.glideContext.getRegistry().getSourceEncoder(data);
    }
}
