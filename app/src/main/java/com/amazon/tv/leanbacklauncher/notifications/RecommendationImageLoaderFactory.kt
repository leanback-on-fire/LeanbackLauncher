package com.amazon.tv.leanbacklauncher.notifications

import android.content.Context
import android.graphics.Bitmap
import com.amazon.tv.leanbacklauncher.notifications.RecommendationImageLoader.Companion.getInstance
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoader.LoadData
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory

class RecommendationImageLoaderFactory(context: Context?) : ModelLoaderFactory<RecommendationImageKey, Bitmap> {
    private val mLoader: RecommendationImageLoader?

    private inner class RecommendationImageFetcher(private val mLoader: RecommendationImageLoader?, private val mRecommendationImageKey: RecommendationImageKey) : DataFetcher<Bitmap> {
        override fun getDataClass(): Class<Bitmap> {
            return Bitmap::class.java
        }

        override fun getDataSource(): DataSource {
            return DataSource.REMOTE
        }

        override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in Bitmap>) {
            callback.onDataReady(this.mLoader!!.getImageForRecommendation(mRecommendationImageKey.key))
        }

        override fun cleanup() {}
        override fun cancel() {}
    }

    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<RecommendationImageKey, Bitmap?> {
        return object : ModelLoader<RecommendationImageKey, Bitmap?> {
            override fun handles(recommendationImageKey: RecommendationImageKey): Boolean {
                return true
            }

            override fun buildLoadData(recommendationImageKey: RecommendationImageKey, width: Int, height: Int, options: Options): LoadData<Bitmap?>? {
                return LoadData(recommendationImageKey, RecommendationImageFetcher(mLoader, recommendationImageKey))
            }
        }
    }

    override fun teardown() {}

    init {
        mLoader = getInstance(context!!)
    }
}