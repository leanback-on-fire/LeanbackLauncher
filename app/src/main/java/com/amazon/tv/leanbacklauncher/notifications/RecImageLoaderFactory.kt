package com.amazon.tv.leanbacklauncher.notifications

import android.content.Context
import android.graphics.Bitmap
import com.amazon.tv.leanbacklauncher.notifications.RecImageLoader.Companion.getInstance
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoader.LoadData
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory

class RecImageLoaderFactory(context: Context?) : ModelLoaderFactory<RecImageKey, Bitmap> {
    private val mLoader: RecImageLoader?

    init {
        mLoader = getInstance(context!!)
    }

    private inner class RecommendationImageFetcher(private val mLoader: RecImageLoader?, private val mRecImageKey: RecImageKey) : DataFetcher<Bitmap> {
        override fun getDataClass(): Class<Bitmap> {
            return Bitmap::class.java
        }

        override fun getDataSource(): DataSource {
            return DataSource.REMOTE
        }

        override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in Bitmap>) {
            callback.onDataReady(this.mLoader!!.getImageForRecommendation(mRecImageKey.key))
        }

        override fun cleanup() {}
        override fun cancel() {}
    }

    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<RecImageKey, Bitmap?> {
        return object : ModelLoader<RecImageKey, Bitmap?> {
            override fun handles(recImageKey: RecImageKey): Boolean {
                return true
            }

            override fun buildLoadData(recImageKey: RecImageKey, width: Int, height: Int, options: Options): LoadData<Bitmap?> {
                return LoadData(recImageKey, RecommendationImageFetcher(mLoader, recImageKey))
            }
        }
    }

    override fun teardown() {}

}