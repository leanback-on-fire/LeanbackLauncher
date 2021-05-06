package com.amazon.tv.leanbacklauncher.notifications

import com.amazon.tv.tvrecommendations.TvRecommendation
import com.bumptech.glide.load.Key
import java.security.MessageDigest

class RecommendationImageKey(rec: TvRecommendation) : Key {
    val key: String = rec.key
    private val mSignature: String
    override fun hashCode(): Int {
        return mSignature.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is RecommendationImageKey && other.mSignature == mSignature
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(mSignature.toByteArray(Key.CHARSET))
    }

    init {
        mSignature = rec.key + rec.title + rec.text
    }
}