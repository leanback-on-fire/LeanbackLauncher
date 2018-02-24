package com.google.android.leanbacklauncher.notifications;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Keep;
import android.support.v17.leanback.widget.BaseCardView;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.leanbacklauncher.DimmableItem;
import com.google.android.leanbacklauncher.R;
import com.google.android.leanbacklauncher.animation.ParticipatesInLaunchAnimation;
import com.google.android.leanbacklauncher.animation.ParticipatesInScrollAnimation;
import com.google.android.leanbacklauncher.animation.ViewDimmer;
import com.google.android.leanbacklauncher.animation.ViewDimmer.DimState;
import com.google.android.leanbacklauncher.animation.ViewFocusAnimator;
import com.google.android.leanbacklauncher.util.Util;
import com.google.android.tvrecommendations.TvRecommendation;

public class NotificationCardView extends BaseCardView implements DimmableItem, ParticipatesInLaunchAnimation, ParticipatesInScrollAnimation {
    private boolean mAnimationsEnabled;
    private boolean mAutoDismiss;
    private ImageView mBadgeImage;
    private float mBadgeImageAlpha;
    private ImageView mBadgeImageSelected;
    private PendingIntent mClickedIntent;
    private Rect mClipBounds;
    private int mColor;
    private TextView mContentView;
    protected ViewDimmer mDimmer;
    private final int mFocusAnimDuration;
    protected ViewFocusAnimator mFocusAnimator;
    private final int mImageHeight;
    private final int mImageMaxWidth;
    private final int mImageMinWidth;
    private ImageView mImageView;
    private View mInfoArea;
    private ColorDrawable mInfoBackground;
    private ObjectAnimator mMetaAnim;
    private int mMetaUnselectedHeight;
    private View mMetadataArea;
    private ProgressBar mProgBar;
    private String mRecGroup;
    private PrescaledLayout mSelectedMetadataContainer;
    private String mSignature;
    private TextView mSourceNameView;
    private TextView mTitleView;
    private String mWallpaperUri;

    public NotificationCardView(Context context) {
        this(context, null);
    }

    public NotificationCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotificationCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mAnimationsEnabled = true;
        Resources res = getResources();
        this.mImageMinWidth = res.getDimensionPixelOffset(R.dimen.notif_card_img_min_width);
        this.mImageMaxWidth = res.getDimensionPixelOffset(R.dimen.notif_card_img_max_width);
        this.mImageHeight = res.getDimensionPixelOffset(R.dimen.notif_card_img_height);
        this.mFocusAnimDuration = res.getInteger(R.integer.notif_card_metadata_animation_duration);
        this.mMetaUnselectedHeight = res.getDimensionPixelOffset(R.dimen.notif_card_info_height);
        this.mBadgeImageAlpha = res.getFraction(R.fraction.badge_icon_alpha, 1, 1);
        this.mDimmer = new ViewDimmer(this);
        this.mFocusAnimator = createNewFocusAnimator();
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mImageView = (ImageView) findViewById(R.id.art_work);
        this.mMetadataArea = findViewById(R.id.metadata);
        this.mSelectedMetadataContainer = (PrescaledLayout) findViewById(R.id.selected_metadata_container);
        this.mInfoArea = findViewById(R.id.info_field);
        this.mTitleView = (TextView) findViewById(R.id.title_text);
        this.mContentView = (TextView) findViewById(R.id.content_text);
        this.mSourceNameView = (TextView) findViewById(R.id.source_name);
        this.mBadgeImage = (ImageView) findViewById(R.id.badge);
        this.mBadgeImageSelected = (ImageView) findViewById(R.id.badge_selected);
        this.mProgBar = (ProgressBar) findViewById(R.id.progress_bar);
        Drawable cardBkg = getBackground();
        this.mColor = ResourcesCompat.getColor(getResources(), R.color.notif_background_color, null);
        this.mInfoBackground = new ColorDrawable(this.mColor);
        this.mInfoArea.setBackground(this.mInfoBackground);
        this.mDimmer.addDimTarget(this.mImageView);
        this.mDimmer.addDimTarget(this.mTitleView);
        this.mDimmer.addDimTarget(this.mContentView);
        this.mDimmer.addDimTarget(this.mSourceNameView);
        this.mDimmer.addDimTarget(cardBkg);
        this.mDimmer.addDesatDimTarget(this.mBadgeImage);
        this.mDimmer.addDesatDimTarget(this.mBadgeImageSelected);
        this.mDimmer.addDimTarget(this.mInfoBackground);
        this.mDimmer.addDimTarget(this.mProgBar.getProgressDrawable());
        this.mDimmer.setDimLevelImmediate();
        setClipToOutline(true);
    }

    public void setColor(int color) {
        this.mColor = color;
        if (this.mColor != 0) {
            this.mInfoBackground.setColor(this.mColor);
        } else {
            this.mInfoBackground.setColor(ResourcesCompat.getColor(getResources(), R.color.notif_background_color, null));
        }
    }

    public void setMainImage(Drawable image) {
        this.mImageView.setImageDrawable(image);
    }

    public void setTitleText(CharSequence text) {
        this.mTitleView.setText(text);
    }

    public void setContentText(CharSequence text) {
        this.mContentView.setText(text);
    }

    public void setSourceName(CharSequence text) {
        this.mSourceNameView.setText(text);
    }

    public void setBadgeImage(Drawable image) {
        if (image != null) {
            image = image.mutate();
            this.mBadgeImage.setImageDrawable(getDeselectedBadgeIcon(image));
            this.mBadgeImage.setVisibility(0);
            this.mBadgeImageSelected.setImageDrawable(image);
            this.mBadgeImageSelected.setVisibility(0);
            return;
        }
        this.mBadgeImageSelected.setVisibility(8);
        this.mBadgeImage.setVisibility(8);
    }

    private Drawable getDeselectedBadgeIcon(Drawable image) {
        int iconWidth = image.getIntrinsicWidth();
        int iconHeight = image.getIntrinsicHeight();
        image.setBounds(0, 0, iconWidth, iconHeight);
        image.setFilterBitmap(true);
        image.setAlpha((int) (255.0f * this.mBadgeImageAlpha));
        Bitmap bitmap = Bitmap.createBitmap(iconWidth, iconHeight, Config.ARGB_8888);
        image.draw(new Canvas(bitmap));
        return new BitmapDrawable(getResources(), bitmap);
    }

    protected ViewFocusAnimator createNewFocusAnimator() {
        return new ViewFocusAnimator(this);
    }

    public void setWallpaperUri(String uri) {
        if (Util.isContentUri(uri)) {
            this.mWallpaperUri = uri;
            return;
        }
        this.mWallpaperUri = null;
        if (uri != null) {
            Log.w("NotificationCardView", "Invalid Content URI provided for recommendation background: " + uri);
        }
    }

    public String getWallpaperUri() {
        return this.mWallpaperUri;
    }

    protected void setSignature(String signature) {
        this.mSignature = signature;
    }

    public String getSignature() {
        return this.mSignature;
    }

    public void setProgressShown(boolean shown) {
        if (this.mProgBar != null) {
            this.mProgBar.setVisibility(shown ? 0 : 4);
        }
    }

    private static void setViewWidth(View v, int width) {
        LayoutParams p = v.getLayoutParams();
        if (p.width != width) {
            p.width = width;
            v.setLayoutParams(p);
        }
    }

    private static void setViewHeight(View v, int height) {
        LayoutParams p = v.getLayoutParams();
        if (p.height != height) {
            p.height = height;
            v.setLayoutParams(p);
        }
    }

    public void setClickedIntent(PendingIntent intent) {
        this.mClickedIntent = intent;
    }

    public PendingIntent getClickedIntent() {
        return this.mClickedIntent;
    }

    public void setAutoDismiss(boolean auto) {
        this.mAutoDismiss = auto;
    }

    public void setRecommendationGroup(String tag) {
        this.mRecGroup = tag;
    }

    public String getRecommendationGroup() {
        return this.mRecGroup;
    }

    public int getLaunchAnimationColor() {
        return this.mColor;
    }

    public void setDimState(DimState dimState, boolean immediate) {
        this.mDimmer.setDimState(dimState, immediate);
    }

    public void setSelected(boolean selected) {
        if (selected != isSelected()) {
            super.setSelected(selected);
            setMetaDataExpanded(selected);
        }
    }

    private void setNotificationImage(TvRecommendation rec) {
        try {
            Resources res = getContext().getPackageManager().getResourcesForApplication(rec.getPackageName());
            Drawable image = null;
            int width = -1;
            int height = -1;
            if (rec.getContentImage() != null) {
                image = new BitmapDrawable(res, rec.getContentImage());
                width = image.getIntrinsicWidth();
                height = image.getIntrinsicHeight();
            }
            if (rec.getWidth() > 0) {
                width = rec.getWidth();
            }
            if (rec.getHeight() > 0) {
                height = rec.getHeight();
            }
            this.mImageView.setScaleType(ScaleType.CENTER_CROP);
            setDimensions(width, height);
            setMainImage(image);
            setBadgeImage(res.getDrawable(rec.getBadgeIcon(), null));
        } catch (NameNotFoundException e) {
        }
    }

    protected void setDimensions(int imgWidth, int imgHeight) {
        int cardWidth;
        if (imgWidth <= 0 || imgHeight <= 0) {
            cardWidth = this.mImageMinWidth;
        } else {
            cardWidth = (int) (((float) imgWidth) / (((float) imgHeight) / ((float) this.mImageHeight)));
            if (cardWidth > this.mImageMaxWidth) {
                cardWidth = this.mImageMaxWidth;
            }
            if (cardWidth < this.mImageMinWidth) {
                cardWidth = this.mImageMinWidth;
            }
        }
        setViewWidth(this.mImageView, cardWidth);
        setViewHeight(this.mImageView, this.mImageHeight);
        if (((float) cardWidth) / ((float) this.mImageHeight) > 1.5f) {
            this.mTitleView.setMaxLines(1);
            this.mContentView.setMaxLines(1);
            return;
        }
        this.mTitleView.setMaxLines(2);
        this.mContentView.setMaxLines(2);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        resetCardState();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        resetCardState();
    }

    public void resetCardState() {
        boolean focus = hasFocus();
        super.setSelected(focus);
        if (this.mMetaAnim != null) {
            this.mMetaAnim.cancel();
            this.mMetaAnim = null;
        }
        clearAnimation();
        this.mDimmer.setDimLevelImmediate();
        this.mFocusAnimator.setFocusImmediate(focus);
        setMetaDataExpandedImmediate(focus);
        setAnimationsEnabled(true);
    }

    private void setMetaDataExpanded(final boolean expanded) {
        float f = 1.0f;
        if (this.mMetaAnim != null) {
            this.mMetaAnim.cancel();
            this.mMetaAnim = null;
        }
        if (this.mAnimationsEnabled && getVisibility() == 0 && hasWindowFocus() && isAttachedToWindow()) {
            float f2;
            this.mSelectedMetadataContainer.setVisibility(0);
            setMetadataOpenFraction(expanded ? 0.0f : 1.0f);
            String str = "metadataOpenFraction";
            float[] fArr = new float[2];
            if (expanded) {
                f2 = 0.0f;
            } else {
                f2 = 1.0f;
            }
            fArr[0] = f2;
            if (!expanded) {
                f = 0.0f;
            }
            fArr[1] = f;
            this.mMetaAnim = ObjectAnimator.ofFloat(this, str, fArr);
            this.mMetaAnim.setDuration((long) this.mFocusAnimDuration);
            this.mMetaAnim.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animation) {
                    NotificationCardView.this.setHasTransientState(true);
                }

                public void onAnimationEnd(Animator animation) {
                    NotificationCardView.this.setHasTransientState(false);
                    NotificationCardView.this.setMetaDataExpandedImmediate(expanded);
                }
            });
            this.mMetaAnim.start();
            return;
        }
        setMetaDataExpandedImmediate(expanded);
    }

    private void setMetaDataExpandedImmediate(boolean expanded) {
        setMetadataAlphaAndPositionFraction(expanded ? 1.0f : 0.0f);
        setClipBounds(null);
        this.mSelectedMetadataContainer.setVisibility(expanded ? 0 : 8);
    }

    @Keep
    public void setMetadataOpenFraction(float fract) {
        setMetadataAlphaAndPositionFraction(fract);
        int height = this.mMetaUnselectedHeight + Math.round(((float) Math.max(0, this.mSelectedMetadataContainer.getContentHeight() - this.mMetaUnselectedHeight)) * fract);
        if (this.mClipBounds == null) {
            this.mClipBounds = new Rect();
        }
        this.mClipBounds.set(0, 0, getWidth(), (getHeight() - this.mMetadataArea.getHeight()) + height);
        setClipBounds(this.mClipBounds);
    }

    private void setMetadataAlphaAndPositionFraction(float fract) {
        int badgeTop = getRelativeTop(this.mBadgeImage, this.mMetadataArea);
        int badgeSelectedTop = getRelativeTop(this.mBadgeImageSelected, this.mMetadataArea);
        this.mSourceNameView.setAlpha(1.0f - fract);
        this.mBadgeImage.setAlpha(1.0f - fract);
        this.mBadgeImage.setTranslationY(((float) (badgeSelectedTop - badgeTop)) * fract);
        this.mInfoArea.getBackground().setAlpha((int) ((255.0f * fract) + 0.5f));
        this.mTitleView.setAlpha(fract);
        this.mContentView.setAlpha(fract);
        this.mBadgeImageSelected.setAlpha(fract);
        this.mBadgeImageSelected.setTranslationY(((float) ((-badgeSelectedTop) + badgeTop)) * (1.0f - fract));
    }

    private int getRelativeTop(View view, View ancestor) {
        if (view == ancestor) {
            return 0;
        }
        return getRelativeTop((View) view.getParent(), ancestor) + view.getTop();
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        setSelected(gainFocus);
    }

    public void setAnimationsEnabled(boolean enabled) {
        this.mAnimationsEnabled = enabled;
        if (!(enabled || this.mMetaAnim == null || !this.mMetaAnim.isStarted())) {
            this.mMetaAnim.end();
        }
        this.mFocusAnimator.setEnabled(enabled);
        this.mDimmer.setAnimationEnabled(enabled);
    }
}
