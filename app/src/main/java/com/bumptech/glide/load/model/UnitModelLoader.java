package com.bumptech.glide.load.model;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.signature.ObjectKey;

public class UnitModelLoader<Model> implements ModelLoader<Model, Model> {

    public static class Factory<Model> implements ModelLoaderFactory<Model, Model> {
        public ModelLoader<Model, Model> build(MultiModelLoaderFactory multiFactory) {
            return new UnitModelLoader();
        }
    }

    private static class UnitFetcher<Model> implements DataFetcher<Model> {
        private final Model resource;

        public UnitFetcher(Model resource) {
            this.resource = resource;
        }

        public void loadData(Priority priority, DataCallback<? super Model> callback) {
            callback.onDataReady(this.resource);
        }

        public void cleanup() {
        }

        public void cancel() {
        }

        public Class<Model> getDataClass() {
            return this.resource.getClass();
        }

        public DataSource getDataSource() {
            return DataSource.LOCAL;
        }
    }

    public LoadData<Model> buildLoadData(Model model, int width, int height, Options options) {
        return new LoadData(new ObjectKey(model), new UnitFetcher(model));
    }

    public boolean handles(Model model) {
        return true;
    }
}
