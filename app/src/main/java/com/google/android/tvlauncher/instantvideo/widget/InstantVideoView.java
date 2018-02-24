package com.google.android.tvlauncher.instantvideo.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import com.google.android.tvlauncher.instantvideo.media.MediaPlayer;
import com.google.android.tvlauncher.instantvideo.media.impl.ExoPlayerImpl;
import com.google.android.tvlauncher.instantvideo.preload.InstantVideoPreloadManager;

public class InstantVideoView extends FrameLayout {
    private static final boolean DEBUG = false;
    private static final long FADE_OUT_DURATION_MS = 2000;
    private static final String TAG = "InstantVideoView";
    private static final int VIDEO_IDLE = 0;
    private static final int VIDEO_PREPARING = 1;
    private static final int VIDEO_SHOWN = 2;
    private ImageView mImageView;
    private ViewPropertyAnimator mImageViewFadeOut;
    private MediaPlayer mPlayer;
    private Runnable mStopVideoRunnable;
    private boolean mVideoStarted;
    private Uri mVideoUri;
    private View mVideoView;
    private float mVolume;

    public static abstract class VideoCallback {
        public void onVideoError(InstantVideoView view) {
        }

        public void onVideoEnded(InstantVideoView view) {
        }

        public void onVideoStarted(InstantVideoView view) {
        }
    }

    public InstantVideoView(Context context) {
        this(context, null, 0);
    }

    public InstantVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InstantVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mVolume = 1.0f;
        this.mStopVideoRunnable = new Runnable() {
            public void run() {
                InstantVideoView.this.stopVideoInternal();
            }
        };
        setImageViewEnabled(true);
        setDisplayedChild(0);
    }

    public void setVideoUri(Uri uri) {
        this.mVideoUri = uri;
    }

    public Uri getVideoUri() {
        return this.mVideoUri;
    }

    public void setImageDrawable(Drawable drawable) {
        this.mImageView.setImageDrawable(drawable);
    }

    public ImageView getImageView() {
        return this.mImageView;
    }

    public void setVolume(float volume) {
        this.mVolume = volume;
        if (this.mPlayer != null && this.mVideoStarted) {
            this.mPlayer.setVolume(volume);
        }
    }

    public void setImageViewEnabled(boolean enable) {
        if (enable && this.mImageView == null) {
            this.mImageView = new ImageView(getContext());
            addView(this.mImageView, new LayoutParams(-1, -1, 17));
        } else if (!enable && this.mImageView != null) {
            if (this.mImageViewFadeOut != null) {
                this.mImageViewFadeOut.cancel();
                this.mImageViewFadeOut = null;
            }
            removeView(this.mImageView);
            this.mImageView = null;
        }
    }

    public void start(final VideoCallback callback) {
        if (!this.mVideoStarted) {
            if (this.mVideoView != null) {
                stopVideoInternal();
            }
            this.mVideoStarted = true;
            this.mPlayer = InstantVideoPreloadManager.getInstance(getContext()).getOrCreatePlayer(this.mVideoUri);
            if (this.mPlayer == null) {
                if (callback != null) {
                    callback.onVideoError(this);
                }
                this.mVideoStarted = false;
                return;
            }
            this.mVideoView = this.mPlayer.getPlayerView();
            addView(this.mVideoView, new LayoutParams(-1, -1, 17));
            if (this.mImageView != null) {
                bringChildToFront(this.mImageView);
            }
            if (!(getWidth() == 0 || getHeight() == 0)) {
                this.mPlayer.setDisplaySize(getWidth(), getHeight());
            }
            if (!(this.mPlayer instanceof ExoPlayerImpl)) {
                setDisplayedChild(1);
            }
            this.mPlayer.prepare();
            this.mPlayer.setPlayWhenReady(true);
            this.mPlayer.setVolume(this.mVolume);
            this.mPlayer.setVideoCallback(new com.google.android.tvlauncher.instantvideo.media.MediaPlayer.VideoCallback() {
                public void onVideoAvailable() {
                    if (InstantVideoView.this.mVideoStarted) {
                        InstantVideoView.this.setDisplayedChild(2);
                        if (callback != null) {
                            callback.onVideoStarted(InstantVideoView.this);
                        }
                    }
                }

                public void onVideoError() {
                    if (InstantVideoView.this.mVideoStarted && callback != null) {
                        callback.onVideoError(InstantVideoView.this);
                    }
                }

                public void onVideoEnded() {
                    if (InstantVideoView.this.mVideoStarted && callback != null) {
                        callback.onVideoEnded(InstantVideoView.this);
                    }
                }
            });
        }
    }

    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        if (width != 0 && height != 0 && this.mPlayer != null) {
            this.mPlayer.setDisplaySize(width, height);
        }
    }

    private void setDisplayedChild(int videoState) {
        if (videoState == 0) {
            if (this.mImageViewFadeOut != null) {
                this.mImageViewFadeOut.cancel();
                this.mImageViewFadeOut = null;
            }
            if (this.mImageView != null) {
                this.mImageView.setVisibility(0);
                this.mImageView.setAlpha(1.0f);
            }
            if (this.mVideoView != null) {
                this.mVideoView.setVisibility(8);
            }
        } else if (videoState != 1) {
            if (this.mImageView != null) {
                this.mImageViewFadeOut = this.mImageView.animate();
                this.mImageViewFadeOut.alpha(0.0f).setDuration(FADE_OUT_DURATION_MS).setInterpolator(new DecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        InstantVideoView.this.mImageViewFadeOut = null;
                        InstantVideoView.this.mImageView.setVisibility(8);
                    }
                }).start();
            }
            if (this.mVideoView != null) {
                this.mVideoView.setVisibility(0);
                this.mVideoView.setAlpha(1.0f);
            }
        } else if (this.mVideoView != null) {
            this.mVideoView.setVisibility(0);
            this.mVideoView.setAlpha(0.0f);
        }
    }

    public void stop() {
        if (this.mVideoStarted) {
            this.mVideoStarted = false;
            this.mPlayer.setVideoCallback(null);
            setDisplayedChild(0);
            post(this.mStopVideoRunnable);
        }
    }

    public void seekTo(int positionMs) {
        this.mPlayer.seekTo(positionMs);
    }

    public int getCurrentPosition() {
        return this.mPlayer.getCurrentPosition();
    }

    public boolean isPlaying() {
        return this.mVideoStarted;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    private void stopVideoInternal() {
        removeCallbacks(this.mStopVideoRunnable);
        if (this.mVideoView != null) {
            this.mPlayer.setVideoCallback(null);
            this.mPlayer.stop();
            InstantVideoPreloadManager.getInstance(getContext()).recyclePlayer(this.mPlayer, this.mVideoUri);
            this.mPlayer = null;
            removeView(this.mVideoView);
            this.mVideoView = null;
        }
    }
}
