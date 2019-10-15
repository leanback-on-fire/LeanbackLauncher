package com.amazon.tv.leanbacklauncher.notifications;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

public class RecommendationImageLoaderFactory implements ModelLoaderFactory<RecommendationImageKey, Bitmap> {
    private final RecommendationImageLoader mLoader;

    private class RecommendationImageFetcher implements DataFetcher<Bitmap> {
        private final RecommendationImageLoader mLoader;
        private final RecommendationImageKey mRecommendationImageKey;

        public RecommendationImageFetcher(RecommendationImageLoader loader, RecommendationImageKey recommendationImageKey) {
            this.mLoader = loader;
            this.mRecommendationImageKey = recommendationImageKey;
        }

        public Class<Bitmap> getDataClass() {
            return Bitmap.class;
        }

        public DataSource getDataSource() {
            return DataSource.REMOTE;
        }

        public void loadData(Priority priority, DataCallback<? super Bitmap> callback) {
            callback.onDataReady(this.mLoader.getImageForRecommendation(this.mRecommendationImageKey.getKey()));
        }

        public void cleanup() {
        }

        public void cancel() {
        }
    }

    public RecommendationImageLoaderFactory(Context context) {
        this.mLoader = RecommendationImageLoader.getInstance(context);
    }

    public ModelLoader<RecommendationImageKey, Bitmap> build(MultiModelLoaderFactory multiFactory) {
        return new ModelLoader<RecommendationImageKey, Bitmap>() {
            public boolean handles(RecommendationImageKey recommendationImageKey) {
                return true;
            }

            public LoadData<Bitmap> buildLoadData(RecommendationImageKey recommendationImageKey, int width, int height, Options options) {
                return new LoadData(recommendationImageKey, new RecommendationImageFetcher(RecommendationImageLoaderFactory.this.mLoader, recommendationImageKey));
            }
        };
    }

    @Override
    public void teardown() {

    }
}
