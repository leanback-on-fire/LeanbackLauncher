package com.amazon.tv.leanbacklauncher

import android.content.Context
import android.graphics.Bitmap
import com.amazon.tv.leanbacklauncher.notifications.RecImageKey
import com.amazon.tv.leanbacklauncher.notifications.RecImageLoaderFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class LauncherGlideModule : AppGlideModule() {
    companion object {
        private const val DEBUG = false
        private const val DISK_CACHE_SIZE_BYTES = 52428800 // 50 MB
        private const val MEMORY_CACHE_SIZE_BYTES = 8388608 // 8 MB
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setMemoryCache(LruResourceCache(MEMORY_CACHE_SIZE_BYTES.toLong()))
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, DISK_CACHE_SIZE_BYTES.toLong()))
        builder.setBitmapPool(BitmapPoolAdapter())
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.append(RecImageKey::class.java, Bitmap::class.java, RecImageLoaderFactory(context))
        registry.append(
            Bitmap::class.java,
            Bitmap::class.java,
            object : ResourceDecoder<Bitmap?, Bitmap?> {
                private var mBitmapPool: BitmapPool? = null
                override fun decode(
                    source: Bitmap,
                    width: Int,
                    height: Int,
                    options: Options
                ): Resource<Bitmap?> {
                    if (mBitmapPool == null) {
                        mBitmapPool = Glide.get(context).bitmapPool
                    }
                    return BitmapResource(source, mBitmapPool!!)
                }

                override fun handles(source: Bitmap, options: Options): Boolean {
                    return true
                }
            })
    }
}