package com.amazon.tv.leanbacklauncher.notifications;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.amazon.tv.leanbacklauncher.DimmableItem;
import com.amazon.tv.leanbacklauncher.R;
import com.amazon.tv.leanbacklauncher.RoundedRectOutlineProvider;
import com.amazon.tv.leanbacklauncher.animation.ParticipatesInLaunchAnimation;
import com.amazon.tv.leanbacklauncher.animation.ParticipatesInScrollAnimation;
import com.amazon.tv.leanbacklauncher.animation.ViewDimmer;
import com.amazon.tv.leanbacklauncher.animation.ViewFocusAnimator;
import com.amazon.tv.leanbacklauncher.capabilities.LauncherConfiguration;
import com.amazon.tv.leanbacklauncher.trace.AppTrace;
import com.amazon.tv.leanbacklauncher.trace.AppTrace.TraceTag;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

public abstract class RecommendationView extends ViewGroup implements Target<Bitmap>, DimmableItem, ParticipatesInLaunchAnimation, ParticipatesInScrollAnimation, ViewFocusAnimator.OnFocusLevelChangeListener {
    private static RoundedRectOutlineProvider sOutline;
    private static Paint sPaint = new Paint();
    private static RectF sRect = new RectF();
    private final Drawable mBackground;
    private int mBackgroundColor;
    private Drawable mBadgeIcon;
    private RectF mBadgeIconCollapsedBounds = new RectF();
    private RectF mBadgeIconExpandedBounds = new RectF();
    private RectF mBadgeIconIntrinsicBounds = new RectF();
    private Matrix mBadgeIconMatrix = new Matrix();
    private final int mBadgeMarginBottom;
    private final int mBadgeSize;
    private Rect mClipBounds;
    protected final TextView mContentView;
    protected final ViewDimmer mDimmer;
    protected boolean mExpandedInfoAreaBound;
    private final ViewFocusAnimator mFocusAnimator;
    private float mFocusLevel;
    private boolean mFocusLevelAnimating;
    private final int mGapBetweenSourceNameAndBadge;
    private Request mGlideRequest;
    protected Drawable mImage;
    protected final int mImageHeight;
    private final int mImageMaxWidth;
    private final int mImageMinWidth;
    private final int mInfoAreaCollapsedHeight;
    protected int mInfoAreaColor;
    protected final int mInfoAreaDefaultColor;
    private int mInfoAreaExpandedHeight;
    private final int mInfoAreaPaddingBottom;
    private final int mInfoAreaPaddingEnd;
    private final int mInfoAreaPaddingStart;
    private final int mInfoAreaPaddingTop;
    private final int mInfoAreaTop;
    private ColorDrawable mInfoBackground;
    protected PackageManager mPackageManager;
    private ProgressBar mProgressBar;
    private final int mProgressBarHeight;
    private Drawable mProgressDrawable;
    private final float mScaleFactor;
    protected String mSignature;
    private final TextView mSourceNameView;
    protected final TextView mTitleView;
    private TraceTag mTraceTag;
    private final Typeface mTypeface;

    protected abstract void bindBadge();

    protected abstract void bindInfoAreaTitleAndContent();

    protected abstract Bitmap getContentImage();

    protected abstract int getDataHeight();

    protected abstract int getDataWidth();

    protected abstract String getSignature();

    protected abstract String getWallpaperUri();

    protected abstract boolean hasProgress();

    public RecommendationView(Context context) {
        super(context);
        setWillNotDraw(false);
        setId(R.id.card);
        setFocusable(true);
        LauncherConfiguration launcherConfiguration = LauncherConfiguration.getInstance();
        if (launcherConfiguration != null && launcherConfiguration.isRoundCornersEnabled()) {
            if (sOutline == null) {
                sOutline = new RoundedRectOutlineProvider((float) getResources().getDimensionPixelOffset(R.dimen.notif_card_corner_radius));
            }
            setOutlineProvider(sOutline);
            setClipToOutline(true);
        }
        this.mDimmer = new ViewDimmer(this);
        this.mInfoBackground = new ColorDrawable();
        this.mDimmer.addDimTarget(this.mInfoBackground);
        this.mPackageManager = getContext().getPackageManager();
        /*int[] attr = new int[]{
                R.attr.background,
                R.attr.maxWidth,
                R.attr.cardFont,
                R.attr.minWidth,
                R.attr.imageHeight,
                R.attr.progressBarHeight,
                R.attr.infoAreaPaddingStart, //
                R.attr.infoAreaPaddingTop,
                R.attr.infoAreaPaddingEnd,
                R.attr.infoAreaPaddingBottom,
                R.attr.infoAreaCollapsedHeight,
                R.attr.infoAreaDefaultColor,
                R.attr.sourceNameTextSize,
                R.attr.sourceNameTextColor,
                R.attr.gapBetweenSourceNameAndBadge,
                R.attr.badgeSize,
                R.attr.badgeMarginBottom,
                R.attr.titleTextSize,
                R.attr.titleTextColor,
                R.attr.contentTextSize,
                R.attr.contentTextColor,
        };
        Arrays.sort(attr);*/
        //  TypedArray a = context.obtainStyledAttributes(R.style.LeanbackRecommendationCard, R.styleable.LeanbackRecommendationCard);

/*

   <item name="background">@drawable/rec_card_background</item>
        <item name="maxWidth">@dimen/notif_card_img_max_width</item>
        <item name="cardFont">@string/font</item>
        <item name="minWidth">@dimen/notif_card_img_min_width</item>
        <item name="imageHeight">@dimen/notif_card_img_height</item>
        <item name="progressBarHeight">@dimen/progress_bar_height</item>
        <item name="infoAreaPaddingStart">@dimen/notif_card_info_start_margin</item>
        <item name="infoAreaPaddingTop">@dimen/notif_card_info_margin_top</item>
        <item name="infoAreaPaddingEnd">@dimen/notif_card_info_badge_end_margin</item>
        <item name="infoAreaPaddingBottom">@dimen/notif_card_info_margin_bottom</item>
        <item name="infoAreaCollapsedHeight">@dimen/notif_card_info_height</item>
        <item name="infoAreaDefaultColor">@color/notif_background_color</item>
        <item name="sourceNameTextSize">@dimen/notif_card_source_text_size</item>
        <item name="sourceNameTextColor">@color/notif_source_text_color</item>
        <item name="gapBetweenSourceNameAndBadge">@dimen/notif_card_info_badge_start_margin</item>
        <item name="badgeSize">@dimen/notif_card_extra_badge_size</item>
        <item name="badgeMarginBottom">@dimen/notif_card_info_badge_bottom_margin</item>
        <item name="titleTextSize">@dimen/notif_card_title_text_size</item>
        <item name="titleTextColor">@color/notif_title_text_color</item>
        <item name="contentTextSize">@dimen/</item>
        <item name="contentTextColor">@color/notif_content_text_color</item>

 */
        Resources res = context.getResources();

        String font = context.getString(R.string.font);//= a.getString(2);
        this.mBackground = context.getDrawable(R.drawable.rec_card_background);//a.getDrawable(0);
        this.mImageMinWidth = res.getDimensionPixelSize(R.dimen.notif_card_img_min_width);//a.getDimensionPixelSize(3, 0);
        this.mImageMaxWidth = res.getDimensionPixelSize(R.dimen.notif_card_img_max_width);//a.getDimensionPixelSize(1, 0);
        this.mImageHeight = res.getDimensionPixelSize(R.dimen.notif_card_img_height);//a.getDimensionPixelSize(4, 0);
        this.mProgressBarHeight = res.getDimensionPixelSize(R.dimen.progress_bar_height);//a.getDimensionPixelSize(5, 0);
        this.mInfoAreaPaddingStart = res.getDimensionPixelOffset(R.dimen.notif_card_info_start_margin);//a.getDimensionPixelOffset(6, 0);
        this.mInfoAreaPaddingTop = res.getDimensionPixelOffset(R.dimen.notif_card_info_margin_top);// a.getDimensionPixelOffset(7, 0);
        this.mInfoAreaPaddingEnd = res.getDimensionPixelOffset(R.dimen.notif_card_info_badge_end_margin);////a.getDimensionPixelOffset(8, 0);
        this.mInfoAreaPaddingBottom = res.getDimensionPixelOffset(R.dimen.notif_card_info_margin_bottom);//a.getDimensionPixelOffset(9, 0);
        this.mInfoAreaCollapsedHeight = res.getDimensionPixelSize(R.dimen.notif_card_info_height); //a.getDimensionPixelSize(10, 0);
        this.mInfoAreaDefaultColor = ContextCompat.getColor(context, R.color.notif_background_color);//a.getColor(11, 0);
        float sourceNameTextSize = res.getDimension(R.dimen.notif_card_source_text_size);//a.getDimension(12, 0.0f);
        int sourceNameTextColor = ContextCompat.getColor(context, R.color.notif_source_text_color);//a.getColor(13, 0);
        this.mGapBetweenSourceNameAndBadge = res.getDimensionPixelOffset(R.dimen.notif_card_info_badge_start_margin);//a.getDimensionPixelOffset(14, 0);
        this.mBadgeSize = res.getDimensionPixelSize(R.dimen.notif_card_extra_badge_size);//a.getDimensionPixelSize(15, 0);
        this.mBadgeMarginBottom = res.getDimensionPixelOffset(R.dimen.notif_card_info_badge_bottom_margin);//a.getDimensionPixelOffset(16, 0);
        float titleTextSize = res.getDimension(R.dimen.notif_card_title_text_size);//a.getDimension(17, 0.0f);
        int titleTextColor = ContextCompat.getColor(context, R.color.notif_title_text_color);//a.getColor(18, 0);
        float contentTextSize = res.getDimension(R.dimen.notif_card_content_text_size); //a.getDimension(19, 0.0f);
        int contentTextColor = ContextCompat.getColor(context, R.color.notif_content_text_color);//a.getColor(20, 0);
        //a.recycle();
        this.mTypeface = Typeface.create(font, 0);
        this.mBackgroundColor = ContextCompat.getColor(context, R.color.notif_background_color);
        this.mSourceNameView = createTextView(sourceNameTextSize, sourceNameTextColor);
        this.mSourceNameView.setGravity(16);
        this.mSourceNameView.setLines(1);
        addView(this.mSourceNameView);
        this.mDimmer.addDimTarget(this.mSourceNameView);
        this.mTitleView = createTextView(titleTextSize, titleTextColor);
        addView(this.mTitleView);
        this.mDimmer.addDimTarget(this.mTitleView);
        this.mContentView = createTextView(contentTextSize, contentTextColor);
        addView(this.mContentView);
        this.mDimmer.addDimTarget(this.mContentView);
        this.mFocusAnimator = new ViewFocusAnimator(this);
        this.mFocusAnimator.setOnFocusProgressListener(this);
        this.mScaleFactor = this.mFocusAnimator.getFocusedScaleFactor();
        this.mInfoAreaTop = this.mImageHeight;
    }

    protected void onBind() {
        this.mSignature = null;
        this.mExpandedInfoAreaBound = false;
        this.mFocusAnimator.setFocusImmediate(hasFocus());
        this.mDimmer.setDimLevelImmediate();
        requestLayout();
    }

    protected void bindProgressBar(int progressMax, int progress) {
        if (hasProgress()) {
            if (this.mProgressBar == null) {
                Context context = getContext();
                this.mProgressDrawable = context.getDrawable(R.drawable.card_progress_drawable);
                this.mProgressBar = new ProgressBar(context, null, 0, 16973855);
                this.mProgressBar.setProgressDrawable(this.mProgressDrawable);
                this.mProgressBar.setLayoutDirection(0);
                addView(this.mProgressBar);
            }
            this.mProgressBar.setVisibility(0);
            this.mProgressBar.setMax(progressMax);
            this.mProgressBar.setProgress(progress);
            this.mDimmer.addDimTarget(this.mProgressDrawable);
        } else if (this.mProgressBar != null) {
            this.mProgressBar.setVisibility(8);
            this.mDimmer.removeDimTarget(this.mProgressDrawable);
        }
    }

    protected void bindContentDescription(CharSequence title, CharSequence label, CharSequence contentText) {
        String description;
        if (title != null) {
            if (label == null || title.toString().equalsIgnoreCase(label.toString())) {
                if (contentText == null || contentText.toString().equalsIgnoreCase(title.toString())) {
                    description = title.toString();
                } else {
                    description = String.format(getResources().getString(R.string.notification_card_view_description_title_content), title, contentText);
                }
            } else if (contentText == null || contentText.toString().equalsIgnoreCase(label.toString())) {
                description = String.format(getResources().getString(R.string.notification_card_view_description_title_label), title, label);
            } else {
                description = String.format(getResources().getString(R.string.notification_card_view_description_title_label_content), title, label, contentText);
            }
        } else if (label != null) {
            if (contentText == null || contentText.toString().equalsIgnoreCase(label.toString())) {
                description = label.toString();
            } else {
                description = String.format(getResources().getString(R.string.notification_card_view_description_label_content), label, contentText);
            }
        } else if (contentText != null) {
            description = contentText.toString();
        } else {
            description = getResources().getString(R.string.notification_card_view_description_default);
        }
        setContentDescription(description);
    }

    protected void bindSourceName(CharSequence sourceName, String packageName) {
        if (TextUtils.isEmpty(sourceName)) {
            try {
                sourceName = this.mPackageManager.getApplicationLabel(this.mPackageManager.getApplicationInfo(packageName, 0));
            } catch (NameNotFoundException e) {
                sourceName = null;
            }
        }
        this.mSourceNameView.setText(sourceName);
    }

    protected void bindExpandedInfoArea() {
        if (!this.mExpandedInfoAreaBound) {
            if (((float) getWidth()) / ((float) this.mImageHeight) > 1.5f) {
                this.mTitleView.setMaxLines(1);
                this.mContentView.setMaxLines(1);
            } else {
                this.mTitleView.setMaxLines(2);
                this.mContentView.setMaxLines(2);
            }
            bindInfoAreaTitleAndContent();
            this.mExpandedInfoAreaBound = true;
            this.mInfoAreaExpandedHeight = 0;
        }
    }

    protected void bindBadge(Drawable badgeIcon) {
        if (this.mBadgeIcon != null) {
            this.mDimmer.removeDimTarget(this.mBadgeIcon);
            this.mDimmer.removeDesatDimTarget(this.mBadgeIcon);
        }
        this.mBadgeIcon = badgeIcon;
        if (this.mBadgeIcon != null) {
            this.mBadgeIcon = this.mBadgeIcon.mutate();
            this.mBadgeIcon.setBounds(0, 0, this.mBadgeIcon.getIntrinsicWidth(), this.mBadgeIcon.getIntrinsicHeight());
            this.mBadgeIconIntrinsicBounds.set(0.0f, 0.0f, (float) this.mBadgeIcon.getIntrinsicWidth(), (float) this.mBadgeIcon.getIntrinsicHeight());
            this.mBadgeIcon.setCallback(this);
            this.mDimmer.addDimTarget(this.mBadgeIcon);
            this.mDimmer.addDesatDimTarget(this.mBadgeIcon);
        }
    }

    private TextView createTextView(float textSize, int textColor) {
        TextView textView = new TextView(getContext());
        textView.setTypeface(this.mTypeface);
        textView.setTextSize(0, textSize);
        textView.setTextColor(textColor);
        textView.setEllipsize(TruncateAt.END);
        textView.setTextDirection(5);
        return textView;
    }

    public void onFocusLevelSettled(boolean focused) {
        if (focused) {
            bindExpandedInfoArea();
            this.mSourceNameView.setVisibility(8);
            this.mTitleView.setVisibility(0);
            this.mTitleView.setAlpha(1.0f);
            this.mContentView.setVisibility(0);
            this.mContentView.setAlpha(1.0f);
        } else {
            this.mSourceNameView.setVisibility(0);
            this.mSourceNameView.setAlpha(1.0f);
            this.mTitleView.setVisibility(8);
            this.mContentView.setVisibility(8);
        }
        setClipBounds(null);
        requestLayout();
        this.mFocusLevelAnimating = false;
    }

    public void onFocusLevelChange(float level) {
        if (this.mFocusLevel != level) {
            if (this.mFocusLevel == 0.0f) {
                bindExpandedInfoArea();
                requestLayout();
            }
            this.mSourceNameView.setVisibility(0);
            this.mTitleView.setVisibility(0);
            this.mContentView.setVisibility(0);
            if (this.mInfoAreaExpandedHeight == 0) {
                measureExpandedInfoArea(calculateCardWidth());
            }
            this.mSourceNameView.setAlpha(1.0f - level);
            this.mTitleView.setAlpha(level);
            this.mContentView.setAlpha(level);
            if (this.mClipBounds == null) {
                this.mClipBounds = new Rect();
            }
            this.mClipBounds.set(0, 0, getWidth(), (this.mImageHeight + this.mInfoAreaCollapsedHeight) + Math.round(((float) (this.mInfoAreaExpandedHeight - this.mInfoAreaCollapsedHeight)) * level));
            setClipBounds(this.mClipBounds);
            this.mFocusLevelAnimating = true;
            this.mFocusLevel = level;
            interpolateBadgeIconLayout();
            invalidate();
        }
    }

    public void setMainImage(Drawable drawable) {
        if (this.mImage != null) {
            this.mDimmer.removeDimTarget(this.mImage);
        }
        if (drawable != null) {
            this.mImage = drawable.mutate();
            layoutMainImage(getWidth());
            this.mImage.setCallback(this);
            this.mDimmer.addDimTarget(this.mImage);
        } else {
            this.mImage = null;
        }
        invalidate();
    }

    public void setUseBackground(boolean useBackground) {
        if (useBackground) {
            setBackground(this.mBackground);
            this.mDimmer.addDimTarget(this.mBackground);
            return;
        }
        this.mDimmer.removeDimTarget(this.mBackground);
        setBackground(null);
    }

    public int getLaunchAnimationColor() {
        return this.mInfoAreaColor;
    }

    public void setDimState(ViewDimmer.DimState dimState, boolean immediate) {
        this.mDimmer.setDimState(dimState, immediate);
    }

    public void setAnimationsEnabled(boolean enabled) {
        this.mFocusAnimator.setEnabled(enabled);
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        setSelected(gainFocus);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int cardWidth = calculateCardWidth();
        if (this.mProgressBar != null && this.mProgressBar.getVisibility() == 0) {
            this.mProgressBar.measure(MeasureSpec.makeMeasureSpec(cardWidth, 1073741824), MeasureSpec.makeMeasureSpec(this.mProgressBarHeight, 1073741824));
        }
        int startBound = this.mInfoAreaPaddingStart;
        int endBound = cardWidth - this.mInfoAreaPaddingEnd;
        if (this.mBadgeIcon != null) {
            endBound -= this.mBadgeSize + this.mGapBetweenSourceNameAndBadge;
        }
        this.mSourceNameView.measure(MeasureSpec.makeMeasureSpec(endBound - startBound, 1073741824), MeasureSpec.makeMeasureSpec(this.mInfoAreaCollapsedHeight, 1073741824));
        if (this.mExpandedInfoAreaBound && this.mInfoAreaExpandedHeight == 0) {
            measureExpandedInfoArea(cardWidth);
        }
        setMeasuredDimension(cardWidth, this.mFocusLevel == 0.0f ? this.mImageHeight + this.mInfoAreaCollapsedHeight : this.mImageHeight + this.mInfoAreaExpandedHeight);
    }

    private int calculateCardWidth() {
        int cardWidth = getDataWidth();
        int imageHeight = getDataHeight();
        if (this.mImage != null) {
            if (cardWidth <= 0) {
                cardWidth = this.mImage.getIntrinsicWidth();
            }
            if (imageHeight <= 0) {
                imageHeight = this.mImage.getIntrinsicHeight();
            }
        }
        if (imageHeight > 0) {
            cardWidth = (this.mImageHeight * cardWidth) / imageHeight;
        } else {
            cardWidth = this.mImageMinWidth;
        }
        if (cardWidth > this.mImageMaxWidth) {
            return this.mImageMaxWidth;
        }
        if (cardWidth < this.mImageMinWidth) {
            return this.mImageMinWidth;
        }
        return cardWidth;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.mInfoBackground.setBounds(0, this.mInfoAreaTop, right - left, bottom - top);
    }

    protected void measureExpandedInfoArea(int width) {
        this.mTitleView.measure(MeasureSpec.makeMeasureSpec((int) (((float) ((width - this.mInfoAreaPaddingEnd) - this.mInfoAreaPaddingStart)) * this.mScaleFactor), 1073741824), MeasureSpec.makeMeasureSpec(0, 0));
        this.mContentView.measure(MeasureSpec.makeMeasureSpec((int) (((float) ((((width - this.mInfoAreaPaddingEnd) - this.mInfoAreaPaddingStart) - this.mBadgeSize) - this.mGapBetweenSourceNameAndBadge)) * this.mScaleFactor), 1073741824), MeasureSpec.makeMeasureSpec(0, 0));
        this.mInfoAreaExpandedHeight = (int) (((((float) this.mInfoAreaPaddingTop) + (((float) this.mTitleView.getMeasuredHeight()) / this.mScaleFactor)) + (((float) this.mContentView.getMeasuredHeight()) / this.mScaleFactor)) + ((float) this.mInfoAreaPaddingBottom));
    }

    protected void layoutMainImage(int width) {
        if (this.mImage != null) {
            float scaledImageWidth = (((float) this.mImage.getIntrinsicWidth()) * ((float) this.mImageHeight)) / ((float) this.mImage.getIntrinsicHeight());
            this.mImage.setBounds((int) ((((float) width) - scaledImageWidth) * 0.5f), 0, (int) Math.ceil((((float) width) + scaledImageWidth) * 0.5f), this.mImageHeight);
        }
    }

    protected void layoutProgressBar(int width) {
        if (this.mProgressBar != null) {
            this.mProgressBar.layout(0, this.mImageHeight - this.mProgressBarHeight, width, this.mImageHeight);
        }
    }

    protected void layoutSourceName(int width, boolean isLayoutRtl) {
        if (this.mSourceNameView.getVisibility() == 0) {
            layoutChild(this.mSourceNameView, this.mInfoAreaPaddingStart, this.mInfoAreaTop, width, isLayoutRtl);
        }
    }

    protected void layoutBadgeIcon(int width, int height, boolean isLayoutRtl) {
        if (this.mBadgeIcon != null) {
            float scaledBadgeSize = ((float) this.mBadgeSize) / this.mScaleFactor;
            float badgeBottom = ((float) height) - (((float) this.mBadgeMarginBottom) / this.mScaleFactor);
            if (isLayoutRtl) {
                this.mBadgeIconCollapsedBounds.set((float) this.mInfoAreaPaddingEnd, (float) this.mInfoAreaTop, (float) (this.mInfoAreaPaddingEnd + this.mBadgeSize), (float) (this.mInfoAreaTop + this.mInfoAreaCollapsedHeight));
                float badgeLeft = ((float) this.mInfoAreaPaddingEnd) / this.mScaleFactor;
                this.mBadgeIconExpandedBounds.set(badgeLeft, badgeBottom - scaledBadgeSize, badgeLeft + scaledBadgeSize, badgeBottom);
            } else {
                this.mBadgeIconCollapsedBounds.set((float) ((width - this.mInfoAreaPaddingEnd) - this.mBadgeSize), (float) this.mInfoAreaTop, (float) (width - this.mInfoAreaPaddingEnd), (float) (this.mInfoAreaTop + this.mInfoAreaCollapsedHeight));
                float badgeRight = ((float) width) - (((float) this.mInfoAreaPaddingEnd) / this.mScaleFactor);
                this.mBadgeIconExpandedBounds.set(badgeRight - scaledBadgeSize, badgeBottom - scaledBadgeSize, badgeRight, badgeBottom);
            }
            interpolateBadgeIconLayout();
        }
    }

    protected void layoutExpandedInfoArea(int width, boolean isLayoutRtl) {
        if (this.mTitleView != null && this.mTitleView.getVisibility() == 0) {
            int start = (int) (((float) this.mInfoAreaPaddingStart) * this.mScaleFactor);
            int titleTop = (int) (((float) this.mInfoAreaTop) + (((float) this.mInfoAreaPaddingTop) * this.mScaleFactor));
            int scaledWidth = (int) (((float) width) * this.mScaleFactor);
            layoutChild(this.mTitleView, start, titleTop, scaledWidth, isLayoutRtl);
            scaleExpandedInfoAreaView(this.mTitleView);
            layoutChild(this.mContentView, start, titleTop + this.mTitleView.getMeasuredHeight(), scaledWidth, isLayoutRtl);
            scaleExpandedInfoAreaView(this.mContentView);
        }
    }

    protected void layoutChild(TextView view, int start, int top, int parentWidth, boolean isLayoutRtl) {
        if (isLayoutRtl) {
            view.layout((parentWidth - start) - view.getMeasuredWidth(), top, parentWidth - start, view.getMeasuredHeight() + top);
        } else {
            view.layout(start, top, view.getMeasuredWidth() + start, view.getMeasuredHeight() + top);
        }
    }

    private void scaleExpandedInfoAreaView(TextView view) {
        view.setPivotX((float) (-view.getLeft()));
        view.setPivotY((float) (-(view.getTop() - this.mInfoAreaTop)));
        view.setScaleX(1.0f / this.mScaleFactor);
        view.setScaleY(1.0f / this.mScaleFactor);
    }

    protected void interpolateBadgeIconLayout() {
        if (this.mBadgeIcon != null) {
            RectF rectC = this.mBadgeIconCollapsedBounds;
            RectF rectE = this.mBadgeIconExpandedBounds;
            sRect.set(rectC.left + ((rectE.left - rectC.left) * this.mFocusLevel), rectC.top + ((rectE.top - rectC.top) * this.mFocusLevel), rectC.right + ((rectE.right - rectC.right) * this.mFocusLevel), rectC.bottom + ((rectE.bottom - rectC.bottom) * this.mFocusLevel));
            this.mBadgeIconMatrix.setRectToRect(this.mBadgeIconIntrinsicBounds, sRect, ScaleToFit.CENTER);
        }
    }

    public void onDraw(Canvas canvas) {
        if (this.mImage != null) {
            this.mImage.draw(canvas);
        } else {
            sPaint.setColor(this.mInfoAreaColor);
            canvas.drawRect(0.0f, 0.0f, (float) getWidth(), (float) this.mImageHeight, sPaint);
        }
        this.mInfoBackground.setColor(ColorUtils.blendARGB(this.mBackgroundColor, this.mInfoAreaColor, this.mFocusLevel));
        this.mInfoBackground.draw(canvas);
        if (this.mBadgeIcon != null) {
            canvas.save();
            canvas.concat(this.mBadgeIconMatrix);
            this.mBadgeIcon.draw(canvas);
            canvas.restore();
        }
    }

    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == this.mImage || who == this.mBadgeIcon || who == this.mInfoBackground;
    }

    public void onStartImageFetch() {
        this.mTraceTag = AppTrace.beginAsyncSection("RecImageFetch");
    }

    public void onLoadStarted(Drawable placeholder) {
        Bitmap contentImage = getContentImage();
        if (contentImage != null) {
            setMainImage(new BitmapDrawable(getResources(), contentImage));
        } else {
            setMainImage(null);
        }
    }

    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
        setMainImage(new BitmapDrawable(getResources(), bitmap));
        onLoadComplete();
    }

    public void onLoadFailed(Drawable errorDrawable) {
        onLoadComplete();
    }

    public void onLoadCleared(Drawable placeholder) {
        setMainImage(null);
        onLoadComplete();
    }

    protected void onLoadComplete() {
        if (this.mTraceTag != null) {
            AppTrace.endAsyncSection(this.mTraceTag);
            this.mTraceTag = null;
        }
    }

    public void getSize(SizeReadyCallback cb) {
        cb.onSizeReady(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public void setRequest(Request request) {
        this.mGlideRequest = request;
    }

    public Request getRequest() {
        return this.mGlideRequest;
    }

    public void onStart() {
    }

    public void onStop() {
    }

    public void onDestroy() {
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mGlideRequest != null && this.mGlideRequest.isRunning()) {
            this.mGlideRequest.pause();
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mGlideRequest != null && this.mGlideRequest.isPaused()) {
            this.mGlideRequest.begin();
        }
    }

    public String toString() {
        Object obj;
        StringBuilder append = new StringBuilder().append("S: ").append(this.mSourceNameView == null ? "No View" : this.mSourceNameView.getText()).append(" T: ");
        if (this.mTitleView == null) {
            obj = "No View";
        } else {
            obj = this.mTitleView.getText();
        }
        append = append.append(obj).append(" C: ");
        if (this.mContentView == null) {
            obj = "No View";
        } else {
            obj = this.mContentView.getText();
        }
        String titles = append.append(obj).toString();
        append = new StringBuilder().append("FL: ").append(this.mFocusLevel).append(" FP: ");
        if (this.mFocusAnimator == null) {
            obj = "No Animator";
        } else {
            obj = Float.valueOf(this.mFocusAnimator.getFocusProgress());
        }
        return super.toString() + " -- " + titles + " " + append.append(obj).append(" IA: ").append(this.mInfoAreaExpandedHeight).append(" FLA: ").append(this.mFocusLevelAnimating).toString();
    }
}
