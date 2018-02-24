package com.bumptech.glide.load.model;

import android.support.v4.util.Pools.Pool;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelLoaderRegistry {
    private final ModelLoaderCache cache;
    private final MultiModelLoaderFactory multiModelLoaderFactory;

    private static class ModelLoaderCache {
        private final Map<Class<?>, Entry<?>> cachedModelLoaders;

        private static class Entry<Model> {
            private final List<ModelLoader<Model, ?>> loaders;

            public Entry(List<ModelLoader<Model, ?>> loaders) {
                this.loaders = loaders;
            }
        }

        private ModelLoaderCache() {
            this.cachedModelLoaders = new HashMap();
        }

        public void clear() {
            this.cachedModelLoaders.clear();
        }

        public <Model> void put(Class<Model> modelClass, List<ModelLoader<Model, ?>> loaders) {
            if (((Entry) this.cachedModelLoaders.put(modelClass, new Entry(loaders))) != null) {
                String valueOf = String.valueOf(modelClass);
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 34).append("Already cached loaders for model: ").append(valueOf).toString());
            }
        }

        public <Model> List<ModelLoader<Model, ?>> get(Class<Model> modelClass) {
            Entry<Model> entry = (Entry) this.cachedModelLoaders.get(modelClass);
            return entry == null ? null : entry.loaders;
        }
    }

    public ModelLoaderRegistry(Pool<List<Exception>> exceptionListPool) {
        this(new MultiModelLoaderFactory(exceptionListPool));
    }

    ModelLoaderRegistry(MultiModelLoaderFactory multiModelLoaderFactory) {
        this.cache = new ModelLoaderCache();
        this.multiModelLoaderFactory = multiModelLoaderFactory;
    }

    public synchronized <Model, Data> void append(Class<Model> modelClass, Class<Data> dataClass, ModelLoaderFactory<Model, Data> factory) {
        this.multiModelLoaderFactory.append(modelClass, dataClass, factory);
        this.cache.clear();
    }

    public synchronized <A> List<ModelLoader<A, ?>> getModelLoaders(A model) {
        List<ModelLoader<A, ?>> filteredLoaders;
        List<ModelLoader<A, ?>> modelLoaders = getModelLoadersForClass(getClass(model));
        int size = modelLoaders.size();
        filteredLoaders = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            ModelLoader<A, ?> loader = (ModelLoader) modelLoaders.get(i);
            if (loader.handles(model)) {
                filteredLoaders.add(loader);
            }
        }
        return filteredLoaders;
    }

    public synchronized List<Class<?>> getDataClasses(Class<?> modelClass) {
        return this.multiModelLoaderFactory.getDataClasses(modelClass);
    }

    private <A> List<ModelLoader<A, ?>> getModelLoadersForClass(Class<A> modelClass) {
        List<ModelLoader<A, ?>> loaders = this.cache.get(modelClass);
        if (loaders != null) {
            return loaders;
        }
        loaders = Collections.unmodifiableList(this.multiModelLoaderFactory.build((Class) modelClass));
        this.cache.put(modelClass, loaders);
        return loaders;
    }

    private static <A> Class<A> getClass(A model) {
        return model.getClass();
    }
}
