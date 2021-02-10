package com.amazon.tv.leanbacklauncher.notifications

import com.amazon.tv.tvrecommendations.TvRecommendation
import com.bumptech.glide.load.Key
import java.security.MessageDigest

class RecommendationImageKey(rec: TvRecommendation) : Key {
    val key: String
    private val mSignature: String
    override fun hashCode(): Int {
        return mSignature.hashCode()
    }

    override fun equals(obj: Any?): Boolean {
        return obj is RecommendationImageKey && obj.mSignature == mSignature
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(mSignature.toByteArray(Key.CHARSET))
    }

    init {
        key = rec.key
        mSignature = rec.key + rec.title + rec.text
    }
}