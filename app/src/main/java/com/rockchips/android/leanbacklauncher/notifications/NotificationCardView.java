package com.rockchips.android.leanbacklauncher.notifications;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Keep;
import android.support.v17.leanback.widget.BaseCardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.rockchips.android.leanbacklauncher.DimmableItem;
import com.rockchips.android.leanbacklauncher.R;
import com.rockchips.android.leanbacklauncher.animation.ParticipatesInLaunchAnimation;
import com.rockchips.android.leanbacklauncher.animation.ParticipatesInScrollAnimation;
import com.rockchips.android.leanbacklauncher.animation.ViewDimmer;
import com.rockchips.android.leanbacklauncher.animation.ViewDimmer.DimState;
import com.rockchips.android.leanbacklauncher.animation.ViewFocusAnimator;
import com.rockchips.android.leanbacklauncher.util.Util;
import com.rockchips.android.leanbacklauncher.tvrecommendations.TvRecommendation;

public class NotificationCardView extends BaseCardView implements DimmableItem, ParticipatesInLaunchAnimation, ParticipatesInScrollAnimation {
    private boolean mAnimationsEnabled;
    private boolean mAutoDismiss;
    private ImageView mBadgeImage;
    private float mBadgeImageAlpha;
    private ImageView mBadgeImageSelected;
    private final int mBadgeSize;
    private int mCardWidth;
    private PendingIntent mClickedIntent;
    private int mColor;
    private TextView mContentView;
    protected ViewDimmer mDimmer;
    private final int mFocusAnimDuration;
    private ViewFocusAnimator mFocusAnimator;
    private final int mImageHeight;
    private final int mImageMaxWidth;
    private final int mImageMinWidth;
    private ImageView mImageView;
    private View mInfoArea;
    private ColorDrawable mInfoBackground;
    private ObjectAnimator mMetaAnim;
    private float mMetaDataOpenFraction;
    private int mMetaUnselectedHeight;
    private View mMetadataArea;
    private ProgressBar mProgBar;
    private String mRecGroup;
    private PrescaledLayout mSelectedMetadataContainer;
    private TextView mSourceNameView;
    private TextView mTitleView;
    private String mWallpaperUri;

    /* renamed from: NotificationCardView.1 */
    class C01901 extends AnimatorListenerAdapter {
        C01901() {
        }

        public void onAnimationStart(Animator animation) {
            NotificationCardView.this.setHasTransientState(true);
        }

        public void onAnimationEnd(Animator animation) {
            NotificationCardView.this.setHasTransientState(false);
        }
    }

    /* renamed from: NotificationCardView.2 */
    class C01912 implements Runnable {
        C01912() {
        }

        public void run() {
            NotificationCardView.this.requestLayout();
        }
    }

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
        this.mBadgeSize = res.getDimensionPixelOffset(R.dimen.notif_card_extra_badge_size);
        this.mFocusAnimDuration = res.getInteger(R.integer.notif_card_metadata_animation_duration);
        this.mMetaUnselectedHeight = res.getDimensionPixelOffset(R.dimen.notif_card_info_height);
        TypedValue out = new TypedValue();
        res.getValue(R.raw.badge_icon_alpha, out, true);
        this.mBadgeImageAlpha = out.getFloat();
        this.mFocusAnimator = new ViewFocusAnimator(this);
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
        this.mColor = getResources().getColor(R.color.notif_background_color);
        this.mInfoBackground = new ColorDrawable(this.mColor);
        this.mInfoArea.setBackground(this.mInfoBackground);
        this.mDimmer = new ViewDimmer(this);
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
            this.mInfoBackground.setColor(getResources().getColor(R.color.notif_background_color));
        }
    }

    public void setMainImage(Drawable image) {
        this.mImageView.setImageDrawable(image);
    }

    public void setTitleText(CharSequence text) {
        this.mTitleView.setText(text);
        this.mTitleView.requestLayout();
    }

    public void setContentText(CharSequence text) {
        this.mContentView.setText(text);
        this.mContentView.requestLayout();
    }

    public void setSourceName(CharSequence text) {
        this.mSourceNameView.setText(text);
    }

    public void setBadgeImage(Drawable image) {
        if (image != null) {
            image.mutate();
            this.mBadgeImageSelected.setImageDrawable(image);
            this.mBadgeImageSelected.setVisibility(0);
            this.mBadgeImage.setImageDrawable(image);
            this.mBadgeImage.setVisibility(0);
            return;
        }
        this.mBadgeImageSelected.setVisibility(8);
        this.mBadgeImage.setVisibility(8);
    }

    private Drawable getResizedBitmapDrawable(Drawable image, int width, int height) {
        if (!(image instanceof BitmapDrawable)) {
            return image;
        }
        return new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(((BitmapDrawable) image).getBitmap(), width, height, false));
    }

    public void setWallpaperUri(String uri) {
        if (Util.isContentUri(uri)) {
            this.mWallpaperUri = uri;
            return;
        }
        Log.w("NotificationCardView", "Invalid Content URI provided for recommendation background: " + uri);
        this.mWallpaperUri = null;
    }

    public String getWallpaperUri() {
        return this.mWallpaperUri;
    }

    public void setProgressShown(boolean shown) {
        if (this.mProgBar != null) {
            this.mProgBar.setVisibility(shown ? 0 : 8);
        }
    }

    public void setProgress(int max, int progress) {
        if (this.mProgBar != null) {
            this.mProgBar.setMax(max);
            this.mProgBar.setProgress(progress);
        }
    }

    private static void setViewWidth(View v, int width) {
        LayoutParams p = (BaseCardView.LayoutParams)v.getLayoutParams();
        if (p.width != width) {
            p.width = width;
            v.setLayoutParams(p);
        }
    }

    private static void setViewHeight(View v, int height) {
        LayoutParams p = (BaseCardView.LayoutParams)v.getLayoutParams();
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

    public boolean isAutoDismiss() {
        return this.mAutoDismiss;
    }

    public void setRecommendationGroup(String tag) {
        this.mRecGroup = tag;
    }

    public String getRecommendationGroup() {
        return this.mRecGroup;
    }

    public int getColor() {
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

    public void setNotificationContent(TvRecommendation rec, boolean updateImage) {
        CharSequence title = rec.getTitle();
        CharSequence text = rec.getText();
        CharSequence sourceName = rec.getSourceName();
        if (updateImage) {
            setNotificationImage(rec);
        }
        setTitleText(title);
        setContentText(text);
        PackageManager pm = getContext().getPackageManager();
        if (TextUtils.isEmpty(sourceName)) {
            try {
                sourceName = pm.getApplicationLabel(pm.getApplicationInfo(rec.getPackageName(), 0));
            } catch (NameNotFoundException e) {
                sourceName = null;
            }
        }
        setSourceName(sourceName);
        setWallpaperUri(rec.getBackgroundImageUri());
        int color = rec.getColor();
        if (color != 0) {
            this.mColor = color;
            this.mInfoBackground.setColor(this.mColor);
        } else {
            this.mColor = getResources().getColor(R.color.notif_background_color);
            this.mInfoBackground.setColor(this.mColor);
        }
        setContentDescription(title, sourceName, text);
        setClickedIntent(rec.getContentIntent());
        setRecommendationGroup(rec.getGroup());
        setAutoDismiss(rec.isAutoDismiss());
        boolean progVisible = false;
        int max = 0;
        int prog = 0;
        if (rec.hasProgress()) {
            max = rec.getProgressMax();
            prog = rec.getProgress();
            if (max > 0) {
                progVisible = true;
            }
        }
        if (progVisible) {
            this.mProgBar.setVisibility(0);
            this.mProgBar.setMax(max);
            this.mProgBar.setProgress(prog);
            return;
        }
        this.mProgBar.setVisibility(8);
    }

    private void setContentDescription(CharSequence title, CharSequence label, CharSequence contentText) {
        String description;
        if (title != null) {
            if (label == null || title.toString().equalsIgnoreCase(label.toString())) {
                if (contentText == null || contentText.toString().equalsIgnoreCase(title.toString())) {
                    description = title.toString();
                } else {
                    description = String.format(getResources().getString(R.string.notification_card_view_description_title_content), new Object[]{title, contentText});
                }
            } else if (contentText == null || contentText.toString().equalsIgnoreCase(label.toString())) {
                description = String.format(getResources().getString(R.string.notification_card_view_description_title_label), new Object[]{title, label});
            } else {
                description = String.format(getResources().getString(R.string.notification_card_view_description_title_label_content), new Object[]{title, label, contentText});
            }
        } else if (label != null) {
            if (contentText == null || contentText.toString().equalsIgnoreCase(label.toString())) {
                description = label.toString();
            } else {
                description = String.format(getResources().getString(R.string.notification_card_view_description_label_content), new Object[]{label, contentText});
            }
        } else if (contentText != null) {
            description = contentText.toString();
        } else {
            description = getResources().getString(R.string.notification_card_view_description_default);
        }
        setContentDescription(description);
    }

    private void setNotificationImage(TvRecommendation rec) {
        try {
            Resources res = getContext().getPackageManager().getResourcesForApplication(rec.getPackageName());
            Drawable drawable = null;
            int width = -1;
            int height = -1;
            if (rec.getContentImage() != null) {
                drawable = new BitmapDrawable(res, rec.getContentImage());
                width = drawable.getIntrinsicWidth();
                height = drawable.getIntrinsicHeight();
            }
            if (rec.getWidth() > 0) {
                width = rec.getWidth();
            }
            if (rec.getHeight() > 0) {
                height = rec.getHeight();
            }
            this.mImageView.setScaleType(ScaleType.CENTER_CROP);
            setDimensions(width, height);
            setMainImage(drawable);
            setBadgeImage(getResizedBitmapDrawable(res.getDrawable(rec.getBadgeIcon(), null), this.mBadgeSize, this.mBadgeSize));
        } catch (NameNotFoundException e) {
        } catch (NotFoundException e2) {
        }
    }

    protected void setDimensions(int imgWidth, int imgHeight) {
        if (imgWidth <= 0 || imgHeight <= 0) {
            this.mCardWidth = this.mImageMinWidth;
        } else {
            this.mCardWidth = (int) (((float) imgWidth) / (((float) imgHeight) / ((float) this.mImageHeight)));
            if (this.mCardWidth > this.mImageMaxWidth) {
                this.mCardWidth = this.mImageMaxWidth;
            }
            if (this.mCardWidth < this.mImageMinWidth) {
                this.mCardWidth = this.mImageMinWidth;
            }
        }
        setViewWidth(this.mImageView, this.mCardWidth);
        setViewHeight(this.mImageView, this.mImageHeight);
        if (((float) this.mCardWidth) / ((float) this.mImageHeight) > 1.5f) {
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

    private void setMetaDataExpanded(boolean expanded) {
        if (this.mMetaAnim != null) {
            this.mMetaAnim.cancel();
            this.mMetaAnim = null;
        }
        if (this.mAnimationsEnabled && getVisibility() == 0 && hasWindowFocus() && isAttachedToWindow()) {
            this.mSelectedMetadataContainer.setVisibility(expanded ? 0 : 8);
            String str = "metadataOpenFraction";
            float[] fArr = new float[2];
            fArr[0] = this.mMetaDataOpenFraction;
            fArr[1] = expanded ? 1.0f : 0.0f;
            this.mMetaAnim = ObjectAnimator.ofFloat(this, str, fArr);
            this.mMetaAnim.setDuration((long) this.mFocusAnimDuration);
            this.mMetaAnim.addListener(new C01901());
            this.mMetaAnim.start();
            return;
        }
        setMetaDataExpandedImmediate(expanded);
    }

    private void setMetaDataExpandedImmediate(boolean expanded) {
        if (expanded) {
            this.mMetaDataOpenFraction = 1.0f;
            this.mSelectedMetadataContainer.setVisibility(0);
            this.mSourceNameView.setAlpha(0.0f);
            this.mBadgeImage.setAlpha(0.0f);
            this.mInfoArea.getBackground().setAlpha(255);
            this.mTitleView.setAlpha(1.0f);
            this.mContentView.setAlpha(1.0f);
            this.mBadgeImageSelected.setAlpha(1.0f);
            setViewHeight(this.mMetadataArea, -2);
            return;
        }
        this.mMetaDataOpenFraction = 0.0f;
        this.mSelectedMetadataContainer.setVisibility(8);
        this.mSourceNameView.setAlpha(1.0f);
        this.mBadgeImage.setAlpha(this.mBadgeImageAlpha);
        setViewHeight(this.mMetadataArea, this.mMetaUnselectedHeight);
    }

    @Keep
    public void setMetadataOpenFraction(float fract) {
        if (fract == 0.0f) {
            setMetaDataExpandedImmediate(false);
        } else if (((double) fract) > 0.99d) {
            setMetaDataExpandedImmediate(true);
        } else {
            this.mMetaDataOpenFraction = fract;
            this.mSourceNameView.setAlpha(1.0f - fract);
            this.mBadgeImage.setAlpha((1.0f - fract) * this.mBadgeImageAlpha);
            boolean selectedMetadataVisible = fract > 0.0f;
            this.mSelectedMetadataContainer.setVisibility(selectedMetadataVisible ? 0 : 8);
            if (selectedMetadataVisible) {
                setViewHeight(this.mMetadataArea, this.mMetaUnselectedHeight + Math.round(((float) Math.max(0, this.mSelectedMetadataContainer.getContentHeight() - this.mMetaUnselectedHeight)) * fract));
                this.mInfoArea.getBackground().setAlpha((int) ((255.0f * fract) + 0.5f));
                this.mTitleView.setAlpha(fract);
                this.mContentView.setAlpha(fract);
                this.mBadgeImageSelected.setAlpha(fract);
            } else {
                setViewHeight(this.mMetadataArea, this.mMetaUnselectedHeight);
            }
            postOnAnimation(new C01912());
        }
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
