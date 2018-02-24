package com.bumptech.glide.load.model;

import android.support.v4.util.Pools.Pool;
import com.bumptech.glide.Registry.NoModelLoaderAvailableException;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.util.Preconditions;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MultiModelLoaderFactory {
    private static final Factory DEFAULT_FACTORY = new Factory();
    private static final ModelLoader<Object, Object> EMPTY_MODEL_LOADER = new EmptyModelLoader();
    private final Set<Entry<?, ?>> alreadyUsedEntries;
    private final List<Entry<?, ?>> entries;
    private final Pool<List<Exception>> exceptionListPool;
    private final Factory factory;

    private static class EmptyModelLoader implements ModelLoader<Object, Object> {
        private EmptyModelLoader() {
        }

        public LoadData<Object> buildLoadData(Object o, int width, int height, Options options) {
            return null;
        }

        public boolean handles(Object o) {
            return false;
        }
    }

    private static class Entry<Model, Data> {
        private final Class<Data> dataClass;
        private final ModelLoaderFactory<Model, Data> factory;
        private final Class<Model> modelClass;

        public Entry(Class<Model> modelClass, Class<Data> dataClass, ModelLoaderFactory<Model, Data> factory) {
            this.modelClass = modelClass;
            this.dataClass = dataClass;
            this.factory = factory;
        }

        public boolean handles(Class<?> modelClass, Class<?> dataClass) {
            return handles(modelClass) && this.dataClass.isAssignableFrom(dataClass);
        }

        public boolean handles(Class<?> modelClass) {
            return this.modelClass.isAssignableFrom(modelClass);
        }
    }

    static class Factory {
        Factory() {
        }

        public <Model, Data> MultiModelLoader<Model, Data> build(List<ModelLoader<Model, Data>> modelLoaders, Pool<List<Exception>> exceptionListPool) {
            return new MultiModelLoader(modelLoaders, exceptionListPool);
        }
    }

    public MultiModelLoaderFactory(Pool<List<Exception>> exceptionListPool) {
        this(exceptionListPool, DEFAULT_FACTORY);
    }

    MultiModelLoaderFactory(Pool<List<Exception>> exceptionListPool, Factory factory) {
        this.entries = new ArrayList();
        this.alreadyUsedEntries = new HashSet();
        this.exceptionListPool = exceptionListPool;
        this.factory = factory;
    }

    synchronized <Model, Data> void append(Class<Model> modelClass, Class<Data> dataClass, ModelLoaderFactory<Model, Data> factory) {
        add(modelClass, dataClass, factory, true);
    }

    private <Model, Data> void add(Class<Model> modelClass, Class<Data> dataClass, ModelLoaderFactory<Model, Data> factory, boolean append) {
        this.entries.add(append ? this.entries.size() : 0, new Entry(modelClass, dataClass, factory));
    }

    synchronized <Model> List<ModelLoader<Model, ?>> build(Class<Model> modelClass) {
        List<ModelLoader<Model, ?>> loaders;
        try {
            loaders = new ArrayList();
            for (Entry entry : this.entries) {
                if (!this.alreadyUsedEntries.contains(entry) && entry.handles(modelClass)) {
                    this.alreadyUsedEntries.add(entry);
                    loaders.add(build(entry));
                    this.alreadyUsedEntries.remove(entry);
                }
            }
        } catch (Throwable th) {
            this.alreadyUsedEntries.clear();
        }
        return loaders;
    }

    synchronized List<Class<?>> getDataClasses(Class<?> modelClass) {
        List<Class<?>> result;
        result = new ArrayList();
        for (Entry<?, ?> entry : this.entries) {
            if (!result.contains(entry.dataClass) && entry.handles(modelClass)) {
                result.add(entry.dataClass);
            }
        }
        return result;
    }

    public synchronized <Model, Data> ModelLoader<Model, Data> build(Class<Model> modelClass, Class<Data> dataClass) {
        ModelLoader<Model, Data> build;
        try {
            List<ModelLoader<Model, Data>> loaders = new ArrayList();
            boolean ignoredAnyEntries = false;
            for (Entry entry : this.entries) {
                if (this.alreadyUsedEntries.contains(entry)) {
                    ignoredAnyEntries = true;
                } else if (entry.handles(modelClass, dataClass)) {
                    this.alreadyUsedEntries.add(entry);
                    loaders.add(build(entry));
                    this.alreadyUsedEntries.remove(entry);
                }
            }
            if (loaders.size() > 1) {
                build = this.factory.build(loaders, this.exceptionListPool);
            } else if (loaders.size() == 1) {
                build = (ModelLoader) loaders.get(0);
            } else if (ignoredAnyEntries) {
                build = emptyModelLoader();
            } else {
                throw new NoModelLoaderAvailableException(modelClass, dataClass);
            }
        } catch (Throwable th) {
            this.alreadyUsedEntries.clear();
        }
        return build;
    }

    private <Model, Data> ModelLoader<Model, Data> build(Entry<?, ?> entry) {
        return (ModelLoader) Preconditions.checkNotNull(entry.factory.build(this));
    }

    private static <Model, Data> ModelLoader<Model, Data> emptyModelLoader() {
        return EMPTY_MODEL_LOADER;
    }
}
