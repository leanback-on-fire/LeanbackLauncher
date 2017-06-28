package com.rockchips.android.leanbacklauncher.notifications;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rockchips.android.leanbacklauncher.R;
import com.rockchips.android.leanbacklauncher.tvrecommendations.TvRecommendation;

public class NowPlayingCardView extends NotificationCardView {
    private boolean mAnimationStarted;
    private AnimatorSet mFadeInAnimation;
    private AnimatorSet mFadeOutAnimation;
    private final OnGlobalLayoutListener mGlobalLayoutListener;
    private Handler mHandler;
    private ImageView mImage1;
    private ImageView mImage2;
    private ImageView mImage3;
    private NowPlayingCardData mMediaData;
    private ImageView mPauseImage;
    private long mPlayerPosMs;
    private int mPlayerState;
    private AnimatorSet mThreeBarAnimator;
    private long mTimeUpdateMs;
    private final int mUpdateInterval;
    private boolean mViewVisible;

    /* renamed from: NowPlayingCardView.1 */
    class C01971 implements OnGlobalLayoutListener {
        C01971() {
        }

        public void onGlobalLayout() {
            NowPlayingCardView.this.adjustAnimationState();
        }
    }

    /* renamed from: NowPlayingCardView.2 */
    class C01982 extends Handler {
        C01982() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                    NowPlayingCardView.this.updatePlayProgress();
                    NowPlayingCardView.this.mHandler.sendEmptyMessageDelayed(1, (long) NowPlayingCardView.this.mUpdateInterval);
                default:
            }
        }
    }

    /* renamed from: NowPlayingCardView.3 */
    class C01993 extends AnimatorListenerAdapter {
        C01993() {
        }

        public void onAnimationStart(Animator animation) {
            NowPlayingCardView.this.mPauseImage.setVisibility(0);
        }
    }

    /* renamed from: NowPlayingCardView.4 */
    class C02004 extends AnimatorListenerAdapter {
        C02004() {
        }

        public void onAnimationStart(Animator animation) {
            NowPlayingCardView.this.mPauseImage.setVisibility(0);
        }

        public void onAnimationEnd(Animator animation) {
            NowPlayingCardView.this.mPauseImage.setVisibility(8);
        }
    }

    /* renamed from: NowPlayingCardView.5 */
    class C02015 extends AnimatorListenerAdapter {
        C02015() {
        }

        public void onAnimationStart(Animator animation) {
            if (NowPlayingCardView.this.mViewVisible) {
                NowPlayingCardView.this.mThreeBarAnimator.start();
            }
            NowPlayingCardView.this.setBarImageVisibility(0);
        }

        public void onAnimationEnd(Animator animation) {
            if (NowPlayingCardView.this.mViewVisible && !NowPlayingCardView.this.mThreeBarAnimator.isStarted()) {
                NowPlayingCardView.this.mThreeBarAnimator.start();
            }
        }
    }

    /* renamed from: NowPlayingCardView.6 */
    class C02026 extends AnimatorListenerAdapter {
        C02026() {
        }

        public void onAnimationStart(Animator animation) {
            NowPlayingCardView.this.setBarImageVisibility(0);
        }

        public void onAnimationEnd(Animator animation) {
            NowPlayingCardView.this.setBarImageVisibility(8);
            NowPlayingCardView.this.mThreeBarAnimator.end();
        }
    }

    static class DropListener implements AnimatorListener {
        private View mView;

        public void onAnimationStart(Animator animation) {
        }

        public void onAnimationEnd(Animator animation) {
            setDropScale(this.mView);
            animation.removeListener(this);
        }

        public void onAnimationCancel(Animator animation) {
        }

        public void onAnimationRepeat(Animator animation) {
        }

        static void setDropScale(View view) {
            view.setScaleY(0.083333336f);
        }
    }

    protected Drawable getBarDrawable(Resources resources) {
        return resources.getDrawable(R.drawable.leanback_card_now_playing_bar);
    }

    private void setUpThreeBarAnimators() {
        Context context = getContext();
        this.mThreeBarAnimator = new AnimatorSet();
        int pivotY = getBarDrawable(context.getResources()).getIntrinsicHeight();
        this.mImage1 = (ImageView) findViewById(R.id.bar1);
        this.mImage1.setPivotY((float) pivotY);
        DropListener.setDropScale(this.mImage1);
        this.mImage2 = (ImageView) findViewById(R.id.bar2);
        this.mImage2.setPivotY((float) pivotY);
        DropListener.setDropScale(this.mImage2);
        this.mImage3 = (ImageView) findViewById(R.id.bar3);
        this.mImage3.setPivotY((float) pivotY);
        DropListener.setDropScale(this.mImage3);
        ObjectAnimator bar1Animator = ObjectAnimator.ofFloat(this.mImage1, "scaleY", new float[]{0.41666666f, 0.25f, 0.41666666f, 0.5833333f, 0.75f, 0.8333333f, 0.9166667f, 1.0f, 0.9166667f, 1.0f, 0.8333333f, 0.6666667f, 0.5f, 0.33333334f, 0.16666667f, 0.33333334f, 0.5f, 0.5833333f, 0.75f, 0.9166667f, 0.75f, 0.5833333f, 0.41666666f, 0.25f, 0.41666666f, 0.6666667f, 0.41666666f, 0.25f, 0.33333334f, 0.41666666f});
        bar1Animator.setRepeatCount(-1);
        bar1Animator.setDuration(2320);
        bar1Animator.setInterpolator(new LinearInterpolator());
        ObjectAnimator bar2Animator = ObjectAnimator.ofFloat(this.mImage2, "scaleY", new float[]{1.0f, 0.9166667f, 0.8333333f, 0.9166667f, 1.0f, 0.9166667f, 0.75f, 0.5833333f, 0.75f, 0.9166667f, 1.0f, 0.8333333f, 0.6666667f, 0.8333333f, 1.0f, 0.9166667f, 0.75f, 0.41666666f, 0.25f, 0.41666666f, 0.6666667f, 0.8333333f, 1.0f, 0.8333333f, 0.75f, 0.6666667f, 1.0f});
        bar2Animator.setRepeatCount(-1);
        bar2Animator.setDuration(2080);
        bar2Animator.setInterpolator(new LinearInterpolator());
        ObjectAnimator bar3Animator = ObjectAnimator.ofFloat(this.mImage3, "scaleY", new float[]{0.6666667f, 0.75f, 0.8333333f, 1.0f, 0.9166667f, 0.75f, 0.5833333f, 0.41666666f, 0.5833333f, 0.6666667f, 0.75f, 1.0f, 0.9166667f, 1.0f, 0.75f, 0.5833333f, 0.75f, 0.9166667f, 1.0f, 0.8333333f, 0.6666667f, 0.75f, 0.5833333f, 0.41666666f, 0.25f, 0.6666667f});
        bar3Animator.setRepeatCount(-1);
        bar3Animator.setDuration(2000);
        bar3Animator.setInterpolator(new LinearInterpolator());
        this.mThreeBarAnimator.playTogether(new Animator[]{bar1Animator, bar2Animator, bar3Animator});
        findViewById(R.id.scrim).setVisibility(0);
    }

    private void setUpFadeAnimators() {
        Context context = getContext();
        LinearLayout threeBarLayout = (LinearLayout) findViewById(R.id.three_bars);
        this.mFadeInAnimation = new AnimatorSet();
        this.mFadeOutAnimation = new AnimatorSet();
        Animator threeBarFadeInAnimator = AnimatorInflater.loadAnimator(context, R.anim.now_playing_bars_fade_in);
        Animator threeBarFadeOutAnimator = AnimatorInflater.loadAnimator(context, R.anim.now_playing_bars_fade_out);
        Animator pauseFadeInAnim = AnimatorInflater.loadAnimator(context, R.anim.now_playing_pause_fade_in);
        Animator pauseFadeOutAnim = AnimatorInflater.loadAnimator(context, R.anim.now_playing_pause_fade_out);
        this.mPauseImage = (ImageView) findViewById(R.id.pause_icon);
        this.mPauseImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_now_playing_paused));
        pauseFadeInAnim.setTarget(this.mPauseImage);
        pauseFadeInAnim.addListener(new C01993());
        pauseFadeOutAnim.setTarget(this.mPauseImage);
        pauseFadeOutAnim.addListener(new C02004());
        threeBarFadeInAnimator.setTarget(threeBarLayout);
        threeBarFadeInAnimator.addListener(new C02015());
        threeBarFadeOutAnimator.setTarget(threeBarLayout);
        threeBarFadeOutAnimator.addListener(new C02026());
        this.mFadeInAnimation.playSequentially(new Animator[]{pauseFadeOutAnim, threeBarFadeInAnimator});
        this.mFadeOutAnimation.playSequentially(new Animator[]{threeBarFadeOutAnimator, pauseFadeInAnim});
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
                this.mThreeBarAnimator.end();
            } else if (!this.mFadeInAnimation.isRunning()) {
                this.mThreeBarAnimator.start();
            }
        }
    }

    private boolean isViewVisibleToUser() {
        View current = this;
        while (current instanceof View) {
            View view = current;
            if (view.getAlpha() <= 0.0f || view.getVisibility() != 0) {
                return false;
            }
            current = (View) view.getParent();
        }
        return true;
    }

    private void setBarImageVisibility(int visibility) {
        this.mImage1.setVisibility(visibility);
        this.mImage2.setVisibility(visibility);
        this.mImage3.setVisibility(visibility);
    }

    public NowPlayingCardView(Context context) {
        this(context, null);
    }

    public NowPlayingCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NowPlayingCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mGlobalLayoutListener = new C01971();
        this.mHandler = new C01982();
        this.mUpdateInterval = getResources().getInteger(R.integer.now_playing_card_update_interval_ms);
    }

    public void setNotificationContent(TvRecommendation rec, boolean updateImage) {
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

    public void setPlayerState(int state, long currentPosMs, long stateChangeTimeMs) {
        this.mPlayerState = state;
        this.mPlayerPosMs = currentPosMs;
        this.mTimeUpdateMs = stateChangeTimeMs;
        this.mHandler.removeMessages(1);
        if (this.mPlayerState == 9) {
            stopAnimation();
            setProgressShown(false);
            return;
        }
        if (this.mPlayerState == 1) {
            stopAnimation();
            setSourceName(getResources().getString(R.string.now_playing_card_stopped));
            setContentText(getResources().getString(R.string.now_playing_card_stopped));
        } else if (this.mPlayerState == 2) {
            stopAnimation();
            setSourceName(getResources().getString(R.string.now_playing_card_paused));
            setContentText(getResources().getString(R.string.now_playing_card_paused));
        } else {
            startAnimation();
            setSourceName(getResources().getString(R.string.now_playing_card_playing));
            setContentText(getResources().getString(R.string.now_playing_card_playing));
        }
        updatePlayProgress();
        if (this.mPlayerState == 3 && isAttachedToWindow()) {
            this.mHandler.sendEmptyMessageDelayed(1, (long) this.mUpdateInterval);
        }
    }

    public void setNowPlayingContent(@NonNull NowPlayingCardData mediaData) {
        Drawable image;
        this.mMediaData = mediaData;
        if (mediaData.artwork != null) {
            image = new BitmapDrawable(getContext().getResources(), mediaData.artwork);
        } else {
            image = getContext().getResources().getDrawable(R.drawable.ic_now_playing_default);
        }
        if (image != null) {
            setDimensions(image.getIntrinsicWidth(), image.getIntrinsicHeight());
        }
        setMainImage(image);
        setWallpaperUri(null);
        setTitleText(mediaData.title);
        if (this.mPlayerState == 1) {
            setSourceName(getResources().getString(R.string.now_playing_card_stopped));
            setContentText(getResources().getString(R.string.now_playing_card_stopped));
        } else if (this.mPlayerState == 2) {
            setSourceName(getResources().getString(R.string.now_playing_card_paused));
            setContentText(getResources().getString(R.string.now_playing_card_paused));
        } else {
            setSourceName(getResources().getString(R.string.now_playing_card_playing));
            setContentText(getResources().getString(R.string.now_playing_card_playing));
        }
        setColor(mediaData.launchColor);
        Drawable badgeIcon = null;
        if (mediaData.playerPackage != null) {
            try {
                badgeIcon = getContext().getPackageManager().getApplicationIcon(mediaData.playerPackage);
            } catch (NameNotFoundException e) {
                Log.e("NowPlayingCardView", "Couldn't get remote control client package icon", e);
            }
        }
        setClickedIntent(this.mMediaData.clickIntent);
        setBadgeImage(badgeIcon);
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
        float progress = Math.min(1.0f, ((float) current) / ((float) this.mMediaData.duration));
        setProgressShown(true);
        setProgress(100, (int) (100.0f * progress));
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        setUpThreeBarAnimators();
        setUpFadeAnimators();
        this.mDimmer.addDimTarget((ImageView) findViewById(R.id.bar1));
        this.mDimmer.addDimTarget((ImageView) findViewById(R.id.bar2));
        this.mDimmer.addDimTarget((ImageView) findViewById(R.id.bar3));
        this.mDimmer.addDimTarget((ImageView) findViewById(R.id.pause_icon));
    }
}
