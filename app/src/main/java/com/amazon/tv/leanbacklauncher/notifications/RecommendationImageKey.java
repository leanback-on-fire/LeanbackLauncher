package com.amazon.tv.leanbacklauncher.notifications;

import com.amazon.tv.tvrecommendations.TvRecommendation;
import com.bumptech.glide.load.Key;

import java.security.MessageDigest;

public class RecommendationImageKey implements Key {
    private final String mKey;
    private final String mSignature;

    public RecommendationImageKey(TvRecommendation rec) {
        this.mKey = rec.getKey();
        this.mSignature = rec.getKey() + rec.getTitle() + rec.getText();
    }

    public String getKey() {
        return this.mKey;
    }

    public int hashCode() {
        return this.mSignature.hashCode();
    }

    public boolean equals(Object obj) {
        return (obj instanceof RecommendationImageKey) && ((RecommendationImageKey) obj).mSignature.equals(this.mSignature);
    }

    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(this.mSignature.getBytes(CHARSET));
    }
}
