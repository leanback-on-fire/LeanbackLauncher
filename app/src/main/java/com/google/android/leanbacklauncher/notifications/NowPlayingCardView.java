package com.google.android.leanbacklauncher.notifications;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.leanbacklauncher.R;
import com.google.android.leanbacklauncher.widget.PlayingIndicatorView;

public class NowPlayingCardView extends RecommendationView {
    private boolean mAnimationStarted;
    private PendingIntent mClickedIntent;
    private AnimatorSet mFadeInAnimation;
    private AnimatorSet mFadeOutAnimation;
    private final OnGlobalLayoutListener mGlobalLayoutListener;
    private Handler mHandler;
    private Drawable mLinearGradient;
    private NowPlayingCardData mMediaData;
    private ImageView mPauseImage;
    private long mPlayerPosMs;
    private int mPlayerState;
    private PlayingIndicatorView mPlayingIndicator;
    private boolean mShowProgress;
    private long mTimeUpdateMs;
    private final int mUpdateInterval;
    private boolean mViewVisible;

    private void setUpFadeAnimators() {
        Context context = getContext();
        this.mPlayingIndicator.setPlaying(true);
        this.mFadeInAnimation = new AnimatorSet();
        this.mFadeOutAnimation = new AnimatorSet();
        Animator threeBarFadeInAnimator = AnimatorInflater.loadAnimator(context, R.animator.now_playing_bars_fade_in);
        Animator threeBarFadeOutAnimator = AnimatorInflater.loadAnimator(context, R.animator.now_playing_bars_fade_out);
        Animator pauseFadeInAnim = AnimatorInflater.loadAnimator(context, R.animator.now_playing_pause_fade_in);
        Animator pauseFadeOutAnim = AnimatorInflater.loadAnimator(context, R.animator.now_playing_pause_fade_out);
        pauseFadeInAnim.setTarget(this.mPauseImage);
        pauseFadeInAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                NowPlayingCardView.this.mPauseImage.setVisibility(0);
            }
        });
        pauseFadeOutAnim.setTarget(this.mPauseImage);
        pauseFadeOutAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                NowPlayingCardView.this.mPauseImage.setVisibility(0);
            }

            public void onAnimationEnd(Animator animation) {
                NowPlayingCardView.this.mPauseImage.setVisibility(8);
            }
        });
        threeBarFadeInAnimator.setTarget(this.mPlayingIndicator);
        threeBarFadeInAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                NowPlayingCardView.this.mPlayingIndicator.startAnimationIfVisible();
                NowPlayingCardView.this.mPlayingIndicator.setVisibility(0);
            }

            public void onAnimationEnd(Animator animation) {
                NowPlayingCardView.this.mPlayingIndicator.startAnimationIfVisible();
            }
        });
        threeBarFadeOutAnimator.setTarget(this.mPlayingIndicator);
        threeBarFadeOutAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                NowPlayingCardView.this.mPlayingIndicator.setVisibility(0);
            }

            public void onAnimationEnd(Animator animation) {
                NowPlayingCardView.this.mPlayingIndicator.stopAnimation();
                NowPlayingCardView.this.mPlayingIndicator.setVisibility(8);
            }
        });
        this.mFadeInAnimation.playSequentially(new Animator[]{pauseFadeOutAnim, threeBarFadeInAnimator});
        this.mFadeOutAnimation.playSequentially(new Animator[]{threeBarFadeOutAnimator, pauseFadeInAnim});
    }

    public void stopIconAnimation() {
        this.mPlayingIndicator.stopAnimation();
    }

    private void startAnimation() {
        if (!this.mAnimationStarted) {
            this.mFadeInAnimation.start();
            this.mAnimationStarted = true;
        }
    }

    private void stopAnimation() {
        if (this.mAnimationStarted) {
            this.mFadeOutAnimation.start();
            this.mAnimationStarted = false;
        }
    }

    private void adjustAnimationState() {
        boolean viewVisible = isViewVisibleToUser();
        if (this.mViewVisible != viewVisible) {
            this.mViewVisible = viewVisible;
            if (!this.mAnimationStarted) {
                return;
            }
            if (!this.mViewVisible) {
                this.mPlayingIndicator.stopAnimation();
            } else if (!this.mFadeInAnimation.isRunning()) {
                this.mPlayingIndicator.startAnimationIfVisible();
            }
        }
    }

    private boolean isViewVisibleToUser() {
        ViewParent viewParent = this;
        while (viewParent instanceof View) {
            View view = (View) viewParent;
            if (view.getAlpha() <= 0.0f || view.getVisibility() != 0) {
                return false;
            }
            viewParent = view.getParent();
        }
        return true;
    }

    public NowPlayingCardView(Context context) {
        super(context);
        this.mGlobalLayoutListener = new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                NowPlayingCardView.this.adjustAnimationState();
            }
        };
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        NowPlayingCardView.this.updatePlayProgress();
                        NowPlayingCardView.this.mHandler.sendEmptyMessageDelayed(1, (long) NowPlayingCardView.this.mUpdateInterval);
                        return;
                    default:
                        return;
                }
            }
        };
        this.mUpdateInterval = getResources().getInteger(R.integer.now_playing_card_update_interval_ms);
        this.mPlayingIndicator = new PlayingIndicatorView(context, null);
        this.mPlayingIndicator.setVisibility(8);
        this.mPauseImage = new ImageView(context);
        this.mPauseImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_now_playing_paused));
        this.mPauseImage.setLayoutParams(new LayoutParams(-2, -2));
        this.mDimmer.addDimTarget(this.mPauseImage);
        this.mDimmer.addDimTarget(this.mPlayingIndicator);
        addView(this.mPlayingIndicator);
        addView(this.mPauseImage);
        this.mLinearGradient = getResources().getDrawable(R.drawable.bottom_top_gradient, null);
        setUpFadeAnimators();
    }

    public NowPlayingCardView(Context context, AttributeSet attrs) {
        this(context);
    }

    public void bindInfoAreaTitleAndContent() {
        CharSequence charSequence;
        TextView textView = this.mTitleView;
        if (this.mMediaData != null) {
            charSequence = this.mMediaData.title;
        } else {
            charSequence = getContext().getString(R.string.unknown_title);
        }
        textView.setText(charSequence);
        this.mContentView.setText(getPlayStateString());
    }

    public Bitmap getContentImage() {
        return this.mMediaData != null ? this.mMediaData.artwork : null;
    }

    public int getDataWidth() {
        return this.mMediaData != null ? this.mMediaData.artwork.getWidth() : 0;
    }

    public int getDataHeight() {
        return this.mMediaData != null ? this.mMediaData.artwork.getHeight() : 0;
    }

    public boolean hasProgress() {
        return this.mShowProgress && this.mMediaData != null;
    }

    public String getSignature() {
        if (this.mSignature == null && this.mMediaData != null) {
            this.mSignature = this.mMediaData.title + getPlayStateString() + this.mMediaData.playerPackage;
        }
        return this.mSignature;
    }

    public String getWallpaperUri() {
        return null;
    }

    protected void bindBadge() {
        try {
            if (this.mMediaData.playerPackage != null) {
                bindBadge(this.mPackageManager.getApplicationIcon(this.mMediaData.playerPackage));
            }
        } catch (NameNotFoundException e) {
            bindBadge(null);
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getRootView().getViewTreeObserver().addOnGlobalLayoutListener(this.mGlobalLayoutListener);
        if (this.mPlayerState == 3) {
            this.mHandler.removeMessages(1);
            updatePlayProgress();
            this.mHandler.sendEmptyMessageDelayed(1, (long) this.mUpdateInterval);
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(this.mGlobalLayoutListener);
        stopSelfUpdate();
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
        layoutGradient();
        layoutPlayIndicator();
        layoutProgressBar(width);
        layoutSourceName(width, isLayoutRtl);
        layoutBadgeIcon(width, height, isLayoutRtl);
        layoutExpandedInfoArea(width, isLayoutRtl);
    }

    private void setProgressShown(boolean showProgress) {
        this.mShowProgress = showProgress;
    }

    private CharSequence getPlayStateString() {
        if (this.mPlayerState == 2) {
            return getResources().getString(R.string.now_playing_card_paused);
        }
        if (this.mPlayerState == 1) {
            return getResources().getString(R.string.now_playing_card_stopped);
        }
        return getResources().getString(R.string.now_playing_card_playing);
    }

    public void setPlayerState(int state, long currentPosMs, long stateChangeTimeMs) {
        boolean unchangedState;
        if (this.mPlayerState == state) {
            unchangedState = true;
        } else {
            unchangedState = false;
        }
        this.mPlayerState = state;
        this.mPlayerPosMs = currentPosMs;
        this.mTimeUpdateMs = stateChangeTimeMs;
        this.mHandler.removeMessages(1);
        updatePlayProgress();
        if (!unchangedState) {
            if (this.mPlayerState == 7) {
                stopAnimation();
                setProgressShown(false);
                return;
            }
            if (this.mPlayerState == 3) {
                startAnimation();
            } else {
                stopAnimation();
            }
            bindSourceName(getPlayStateString(), this.mMediaData != null ? this.mMediaData.playerPackage : null);
            this.mExpandedInfoAreaBound = false;
            bindExpandedInfoArea();
            requestLayout();
        }
        if (this.mPlayerState == 3 && isAttachedToWindow()) {
            this.mHandler.sendEmptyMessageDelayed(1, (long) this.mUpdateInterval);
        }
    }

    public void setNowPlayingContent(NowPlayingCardData mediaData) {
        Drawable image;
        this.mMediaData = mediaData;
        int color = this.mMediaData.launchColor;
        if (color == 0) {
            color = this.mInfoAreaDefaultColor;
        }
        this.mInfoAreaColor = color;
        bindProgressBar((int) this.mMediaData.duration, (int) this.mPlayerPosMs);
        bindSourceName(getPlayStateString(), this.mMediaData.playerPackage);
        bindBadge();
        CharSequence name = null;
        try {
            PackageManager packageManager = getContext().getPackageManager();
            name = packageManager.getApplicationInfo(this.mMediaData.playerPackage, 0).loadLabel(packageManager);
        } catch (NameNotFoundException e) {
            Log.e("NowPlayingCardView", "Session package not found", e);
        }
        bindContentDescription(this.mMediaData.title, name, getPlayStateString());
        if (mediaData.artwork != null) {
            image = new BitmapDrawable(getContext().getResources(), mediaData.artwork);
        } else {
            image = ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.ic_now_playing_default, null);
        }
        if (!(image == null || image.getOpacity() == -1)) {
            Bitmap opaqueBitmap = Bitmap.createBitmap(image.getIntrinsicWidth(), image.getIntrinsicHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(opaqueBitmap);
            canvas.drawColor(ContextCompat.getColor(getContext(), R.color.notif_background_color));
            image.setBounds(0, 0, opaqueBitmap.getWidth(), opaqueBitmap.getHeight());
            image.draw(canvas);
            image = new BitmapDrawable(getResources(), opaqueBitmap);
        }
        setMainImage(image);
        setClickedIntent(this.mMediaData.clickIntent);
        onBind();
    }

    private void setClickedIntent(PendingIntent clickIntent) {
        this.mClickedIntent = clickIntent;
    }

    public PendingIntent getClickedIntent() {
        return this.mClickedIntent;
    }

    public void stopSelfUpdate() {
        this.mHandler.removeCallbacksAndMessages(null);
    }

    private void updatePlayProgress() {
        if (this.mMediaData == null || this.mPlayerPosMs < 0 || this.mMediaData.duration <= 0 || this.mPlayerPosMs > this.mMediaData.duration) {
            setProgressShown(false);
            return;
        }
        long current = this.mPlayerPosMs;
        if (this.mPlayerState == 3) {
            current += SystemClock.elapsedRealtime() - this.mTimeUpdateMs;
        }
        setProgressShown(true);
        bindProgressBar((int) this.mMediaData.duration, (int) current);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mPlayingIndicator.measure(widthMeasureSpec, heightMeasureSpec);
        this.mPauseImage.measure(0, 0);
    }

    private void layoutPlayIndicator() {
        if (this.mPlayingIndicator != null && this.mPauseImage != null) {
            int endPadding = (int) getResources().getDimension(R.dimen.leanback_card_now_playing_padding_end);
            int bottomPadding = (int) getResources().getDimension(R.dimen.leanback_card_now_playing_padding_bottom);
            int left = getMeasuredWidth() - endPadding;
            int right = getMeasuredWidth() - endPadding;
            int top = this.mImageHeight - bottomPadding;
            int bottom = this.mImageHeight - bottomPadding;
            this.mPauseImage.layout(left - this.mPauseImage.getMeasuredWidth(), top - this.mPauseImage.getMeasuredHeight(), right, bottom);
            this.mPlayingIndicator.layout(left - this.mPlayingIndicator.getMeasuredWidth(), top - this.mPlayingIndicator.getMeasuredHeight(), right, bottom);
        }
    }

    private void layoutGradient() {
        this.mLinearGradient.setBounds(this.mImage.getBounds());
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mLinearGradient != null) {
            this.mLinearGradient.draw(canvas);
        }
    }
}
