package com.amazon.tv.leanbacklauncher.notifications;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.amazon.tv.leanbacklauncher.PackageResourceCache;
import com.amazon.tv.tvrecommendations.TvRecommendation;
import com.bumptech.glide.request.target.SizeReadyCallback;

public class RecommendationCardView extends RecommendationView {
    private TvRecommendation mRecommendation;
    private PackageResourceCache mResourceCache;

    public RecommendationCardView(Context context) {
        this(context, PackageResourceCache.getInstance(context));
    }

    RecommendationCardView(Context context, PackageResourceCache resourceCache) {
        super(context);
        this.mResourceCache = resourceCache;
    }

    public void bindInfoAreaTitleAndContent() {
        this.mTitleView.setText(this.mRecommendation.getTitle());
        this.mContentView.setText(this.mRecommendation.getText());
    }

    public Bitmap getContentImage() {
        return this.mRecommendation != null ? this.mRecommendation.getContentImage() : null;
    }

    public int getDataWidth() {
        return this.mRecommendation != null ? this.mRecommendation.getWidth() : 0;
    }

    public int getDataHeight() {
        return this.mRecommendation != null ? this.mRecommendation.getHeight() : 0;
    }

    public boolean hasProgress() {
        return this.mRecommendation != null && this.mRecommendation.hasProgress();
    }

    public int getProgress() {
        return this.mRecommendation != null ? this.mRecommendation.getProgress() : 0;
    }

    public void setRecommendation(TvRecommendation rec, boolean updateImage) {
        this.mRecommendation = rec;
        int color = rec.getColor();
        if (color == 0) {
            color = this.mInfoAreaDefaultColor;
        }
        this.mInfoAreaColor = color;


        bindProgressBar(this.mRecommendation.getProgressMax(), this.mRecommendation.getProgress());
        bindSourceName(this.mRecommendation.getSourceName(), this.mRecommendation.getPackageName());
        bindBadge();
        bindContentDescription(this.mRecommendation.getTitle(), this.mRecommendation.getSourceName(), this.mRecommendation.getText());
        onBind();
    }


    public String getWallpaperUri() {
        return this.mRecommendation.getBackgroundImageUri();
    }

    public String getSignature() {
        if (this.mSignature == null) {
            this.mSignature = "" + this.mRecommendation.getTitle() + this.mRecommendation.getText() + this.mRecommendation.getSourceName();
        }
        return this.mSignature;
    }

    protected void bindBadge() {
        try {
            // todo
            bindBadge(this.mResourceCache.getDrawable(this.mRecommendation.getPackageName(), this.mRecommendation.getBadgeIcon()));
            return;
        } catch (NameNotFoundException e) {
        } catch (NotFoundException e2) {
        }
        bindBadge(null);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        boolean isLayoutRtl = true;
        super.onLayout(changed, left, top, right, bottom);
        int width = right - left;
        int height = bottom - top;
        if (getLayoutDirection() != 1) {
            isLayoutRtl = false;
        }
        layoutMainImage(width);
        layoutProgressBar(width);
        layoutSourceName(width, isLayoutRtl);
        layoutBadgeIcon(width, height, isLayoutRtl);
        layoutExpandedInfoArea(width, isLayoutRtl);
    }

    @Override
    public void removeCallback(@NonNull SizeReadyCallback cb) {

    }
}
