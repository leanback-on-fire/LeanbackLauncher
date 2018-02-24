package com.google.android.leanbacklauncher.notifications;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.google.android.leanbacklauncher.R;
import com.google.android.tvrecommendations.TvRecommendation;

public class CaptivePortalNotificationCardView extends RecommendationView {
    private Bitmap mNetworkIcon;
    private TvRecommendation mRecommendation;

    public CaptivePortalNotificationCardView(Context context) {
        super(context);
    }

    public void bindInfoAreaTitleAndContent() {
        this.mTitleView.setText(this.mRecommendation.getTitle());
        this.mContentView.setText(this.mRecommendation.getText());
    }

    public Bitmap getContentImage() {
        if (this.mNetworkIcon == null) {
            this.mNetworkIcon = generateArtwork();
        }
        return this.mNetworkIcon;
    }

    public int getDataWidth() {
        return this.mRecommendation != null ? this.mRecommendation.getWidth() : 0;
    }

    public int getDataHeight() {
        return this.mRecommendation != null ? this.mRecommendation.getHeight() : 0;
    }

    public boolean hasProgress() {
        return false;
    }

    public void setRecommendation(TvRecommendation rec, boolean updateImage) {
        this.mRecommendation = rec;
        this.mInfoAreaColor = getResources().getColor(R.color.notif_background_color);
        bindSourceName(getResources().getString(R.string.settings_network), this.mRecommendation.getPackageName());
        bindContentDescription(this.mRecommendation.getTitle(), this.mRecommendation.getSourceName(), this.mRecommendation.getText());
        setMainImage(new BitmapDrawable(getResources(), getContentImage()));
        onBind();
    }

    public String getWallpaperUri() {
        return null;
    }

    public String getSignature() {
        if (this.mSignature == null) {
            this.mSignature = this.mRecommendation.getTitle() + this.mRecommendation.getText() + this.mRecommendation.getSourceName();
        }
        return this.mSignature;
    }

    protected void bindBadge() {
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        boolean isLayoutRtl = true;
        super.onLayout(changed, left, top, right, bottom);
        int width = right - left;
        if (getLayoutDirection() != 1) {
            isLayoutRtl = false;
        }
        layoutMainImage(width);
        layoutSourceName(width, isLayoutRtl);
        layoutExpandedInfoArea(width, isLayoutRtl);
    }

    private Bitmap generateArtwork() {
        Drawable networkIcon = getResources().getDrawable(R.drawable.ic_settings_wifi_active_3, null);
        networkIcon.setTint(-1);
        int height = networkIcon.getIntrinsicHeight();
        int width = networkIcon.getIntrinsicWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        bmp.eraseColor(this.mRecommendation.getColor());
        Canvas canvas = new Canvas(bmp);
        networkIcon.setBounds(new Rect(0, 0, width, height));
        networkIcon.draw(canvas);
        return bmp;
    }
}
