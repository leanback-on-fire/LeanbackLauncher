package com.google.android.tvlauncher.home;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.google.android.tvlauncher.R;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEvent;
import com.google.android.tvlauncher.analytics.LogEvents;
import com.google.android.tvlauncher.analytics.LogUtils;
import com.google.android.tvlauncher.analytics.UserActionEvent;
import com.google.android.tvlauncher.data.TvDataManager;
import com.google.android.tvlauncher.home.util.ProgramPreviewImageData;
import com.google.android.tvlauncher.home.util.ProgramPreviewImageTranscoder;
import com.google.android.tvlauncher.home.util.ProgramSettings;
import com.google.android.tvlauncher.home.util.ProgramUtil;
import com.google.android.tvlauncher.instantvideo.widget.InstantVideoView;
import com.google.android.tvlauncher.instantvideo.widget.InstantVideoView.VideoCallback;
import com.google.android.tvlauncher.model.Program;
import com.google.android.tvlauncher.util.ContextMenu;
import com.google.android.tvlauncher.util.ContextMenu.OnItemClickListener;
import com.google.android.tvlauncher.util.ContextMenuItem;
import com.google.android.tvlauncher.util.IntentLauncher;
import com.google.android.tvlauncher.util.ScaleAndExpandFocusHandler;
import com.google.android.tvlauncher.util.Util;
import com.google.android.tvlauncher.util.palette.PaletteBitmapContainer;

class ProgramController implements OnClickListener, OnItemClickListener, OnLongClickListener {
    private static final boolean DEBUG = false;
    private static final double EPS = 0.001d;
    private static final int MENU_ADD_TO_WATCH_NEXT = 2;
    private static final int MENU_PRIMARY_ACTION = 1;
    private static final int MENU_REMOVE_FROM_WATCH_NEXT = 4;
    private static final int MENU_REMOVE_PREVIEW_PROGRAM = 3;
    private static final int PREVIEW_IMAGE_FADE_DURATION_MILLIS = 300;
    private static final int PREVIEW_VIDEO_START_DELAY_MILLIS = 3000;
    private static final int SCALING_ERROR_MARGIN = 20;
    private static final String TAG = "ProgramController";
    private static boolean sPreviewImageTranscoderRegistered = false;
    private String mActionUri;
    private boolean mAllowAudioPlaying;
    private final View mBadgesContainer;
    private BitmapDrawable mBlurredPreviewImageDrawable;
    private boolean mCanAddToWatchNext;
    private boolean mCanRemoveProgram;
    private String mChannelPackageName;
    private String mContentId;
    private final TextView mDurationBadge;
    private final EventLogger mEventLogger;
    private final ScaleAndExpandFocusHandler mFocusHandler;
    private Double mFocusedAspectRatio;
    private final RequestOptions mImageRequestOptions;
    private boolean mIsLegacy;
    private boolean mIsWatchNextProgram = false;
    private final TextView mLiveBadge;
    private final ImageView mLiveIcon;
    private final ImageView mLogo;
    private final View mLogoBadgesContainer;
    private final View mLogoDimmer;
    private String mLogoUri;
    private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            if (ProgramController.this.mProgramMenu != null && ProgramController.this.mProgramMenu.isShowing()) {
                ProgramController.this.mProgramMenu.forceDismiss();
            }
            if (!ProgramController.this.mIsWatchNextProgram) {
                if (ProgramController.this.mPreviewVideoUri != null) {
                    if (hasFocus /*!ActiveMediaSessionManager.getInstance(v.getContext()).hasActiveMediaSession() */ &&
                            ProgramController.this.allowPreviewVideoPlaying()) {
                        ProgramController.this.startPreviewVideoDelayed();
                    } else {
                        ProgramController.this.stopPreviewVideo(true);
                    }
                    ProgramController.this.updatePreviewImageTreatment(hasFocus);
                } else if (ProgramController.this.mThumbnailUri != null) {
                    ProgramController.this.mThumbnail.setVisibility(hasFocus ? 0 : 8);
                }
            }
            if (ProgramController.this.mOnProgramViewFocusedListener != null && hasFocus) {
                ProgramController.this.mOnProgramViewFocusedListener.onProgramViewFocused();
            }
            ProgramController.this.updateBadges(hasFocus);
        }
    };
    private OnProgramViewFocusedListener mOnProgramViewFocusedListener;
    private final ProgressBar mPlaybackProgress;
    private final View mPlaybackProgressDimmer;
    private final ImageView mPreviewImage;
    private final ImageView mPreviewImageBackground;
    private ImageViewTarget<ProgramPreviewImageData> mPreviewImageBlurGlideTarget;
    private final View mPreviewImageContainer;
    private final int mPreviewImageExpandedVerticalMargin;
    private ValueAnimator mPreviewImageFadeInAnimator;
    private AnimatorListener mPreviewImageFadeInAnimatorListener;
    private ValueAnimator mPreviewImageFadeOutAnimator;
    private AnimatorListener mPreviewImageFadeOutAnimatorListener;
    private AnimatorUpdateListener mPreviewImageFadeUpdateListener;
    private boolean mPreviewImageNeedsTreatment;
    private Palette mPreviewImagePalette;
    private ImageViewTarget<PaletteBitmapContainer> mPreviewImagePaletteGlideTarget;
    private float mPreviewImageVisibilityValue = 1.0f;
    private final InstantVideoView mPreviewVideo;
    private final View mPreviewVideoDelayOverlay;
    private SharedPreferences mPreviewVideoPref;
    private String mPreviewVideoUri;
    private final ColorDrawable mProgramDefaultBackgroundDrawable;
    private long mProgramDuration;
    private long mProgramId;
    private boolean mProgramIsLive;
    private ContextMenu mProgramMenu;
    private final String mProgramMenuAddToWatchNextNotAvailableText;
    private final String mProgramMenuAddToWatchNextText;
    private final String mProgramMenuAlreadyInWatchNextText;
    private final String mProgramMenuRemoveNotAvailableText;
    private final String mProgramMenuRemoveText;
    private final ProgramSettings mProgramSettings;
    private int mProgramState;
    private int mProgramType;
    private Runnable mStartPreviewVideoRunnable = new Runnable() {
        public void run() {
            if (!ProgramController.this.mIsWatchNextProgram && ProgramController.this.mPreviewVideoUri != null && ProgramController.this.mView.isFocused()) {
                ProgramController.this.mPreviewVideo.setVisibility(0);
                ProgramController.this.mAllowAudioPlaying = ProgramController.this.mPreviewVideoPref.getBoolean(TvDataManager.ENABLE_PREVIEW_AUDIO_KEY, true);
                if (!ProgramController.this.mAllowAudioPlaying) {
                    ProgramController.this.mPreviewVideo.setVolume(0.0f);
                }
                ProgramController.this.mPreviewVideo.start(ProgramController.this.mVideoCallback);
            }
        }
    };
    private long mStartedPreviewVideoMillis;
    private final ImageView mThumbnail;
    private String mThumbnailUri;
    private Double mUnfocusedAspectRatio;
    private VideoCallback mVideoCallback = new VideoCallback() {
        public void onVideoStarted(InstantVideoView view) {
            ProgramController.this.mStartedPreviewVideoMillis = SystemClock.elapsedRealtime();
            ProgramController.this.fadePreviewImageOut();
            ProgramController.this.mPreviewVideoDelayOverlay.setVisibility(8);
        }

        public void onVideoEnded(InstantVideoView view) {
            ProgramController.this.stopPreviewVideo(true);
        }

        public void onVideoError(InstantVideoView view) {
            Log.e(ProgramController.TAG, "onVideoError: uri=[" + view.getVideoUri() + "]");
            ProgramController.this.stopPreviewVideo(true);
        }
    };
    private final View mView;
    private int mWatchedPreviewVideoSeconds;

    private boolean allowPreviewVideoPlaying() {
        return this.mView.getContext().getSharedPreferences(TvDataManager.PREVIEW_VIDEO_PREF_FILE_NAME, 0).getBoolean(TvDataManager.SHOW_PREVIEW_VIDEO_KEY, true);
    }

    ProgramController(View v, EventLogger eventLogger) {
        this.mView = v;
        v.setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), (float) view.getResources().getDimensionPixelSize(R.dimen.card_rounded_corner_radius));
            }
        });
        v.setClipToOutline(true);
        v.setOnClickListener(this);
        v.setOnLongClickListener(this);
        this.mPreviewVideoPref = this.mView.getContext().getSharedPreferences(TvDataManager.PREVIEW_VIDEO_PREF_FILE_NAME, 0);
        this.mEventLogger = eventLogger;
        this.mProgramSettings = ProgramUtil.getProgramSettings(v.getContext());
        if (!sPreviewImageTranscoderRegistered) {
            Glide.get(this.mView.getContext()).getRegistry().register(Bitmap.class, ProgramPreviewImageData.class, new ProgramPreviewImageTranscoder(this.mView.getContext()));
            sPreviewImageTranscoderRegistered = true;
        }
        this.mPreviewImage = (ImageView) v.findViewById(R.id.preview_image);
        this.mPreviewImageContainer = v.findViewById(R.id.preview_image_container);
        this.mPreviewImageBackground = (ImageView) v.findViewById(R.id.preview_image_background);
        this.mPreviewVideo = (InstantVideoView) v.findViewById(R.id.preview_video_view);
        this.mPreviewVideo.setImageViewEnabled(false);
        this.mThumbnail = (ImageView) v.findViewById(R.id.thumbnail);
        this.mLogoBadgesContainer = v.findViewById(R.id.program_logo_badges_container);
        this.mLogo = (ImageView) v.findViewById(R.id.program_card_logo);
        this.mLogoDimmer = v.findViewById(R.id.program_logo_dimmer);
        this.mBadgesContainer = v.findViewById(R.id.program_badges_container);
        this.mLiveBadge = (TextView) v.findViewById(R.id.program_live_badge);
        this.mLiveIcon = (ImageView) v.findViewById(R.id.program_live_icon);
        this.mDurationBadge = (TextView) v.findViewById(R.id.program_duration_badge);
        this.mBadgesContainer.setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), (float) view.getResources().getDimensionPixelSize(R.dimen.program_badge_background_corner_radius));
            }
        });
        this.mBadgesContainer.setClipToOutline(true);
        this.mPreviewVideoDelayOverlay = v.findViewById(R.id.preview_video_delay_overlay);
        this.mPlaybackProgress = (ProgressBar) v.findViewById(R.id.watch_next_playback_progress);
        this.mPlaybackProgressDimmer = v.findViewById(R.id.watch_next_program_playback_progress_dimmer);
        this.mFocusHandler = new ScaleAndExpandFocusHandler(this.mProgramSettings.focusedAnimationDuration, this.mProgramSettings.focusedScale, this.mProgramSettings.focusedElevation, 1);
        this.mFocusHandler.setView(v);
        this.mFocusHandler.setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.mProgramDefaultBackgroundDrawable = new ColorDrawable(ContextCompat.getColor(v.getContext(), R.color.program_default_background));
        this.mPreviewImageExpandedVerticalMargin = this.mView.getResources().getDimensionPixelOffset(R.dimen.program_preview_image_expanded_vertical_margin);
        int maxWidth = ((int) (((double) this.mProgramSettings.selectedHeight) * ProgramUtil.ASPECT_RATIO_16_9)) + 20;
        this.mImageRequestOptions = (RequestOptions) ((RequestOptions) new RequestOptions().override(maxWidth, this.mProgramSettings.selectedHeight)).centerInside();
        this.mProgramMenuAddToWatchNextText = this.mView.getContext().getString(R.string.program_menu_add_to_watch_next_text);
        this.mProgramMenuRemoveText = this.mView.getContext().getString(R.string.program_menu_remove_text);
        this.mProgramMenuAddToWatchNextNotAvailableText = this.mView.getContext().getString(R.string.program_menu_add_to_watch_next_not_available_text);
        this.mProgramMenuAlreadyInWatchNextText = this.mView.getContext().getString(R.string.program_menu_already_in_watch_next_text);
        this.mProgramMenuRemoveNotAvailableText = this.mView.getContext().getString(R.string.program_menu_remove_no_available_text);
    }

    private void startPreviewVideoDelayed() {
        this.mView.removeCallbacks(this.mStartPreviewVideoRunnable);
        this.mView.postDelayed(this.mStartPreviewVideoRunnable, 3000);
        this.mPreviewVideoDelayOverlay.setVisibility(0);
    }

    private void finishStoppingPreviewVideo() {
        this.mPreviewVideo.setVisibility(8);
        this.mPreviewVideo.stop();
    }

    void stopPreviewVideo(boolean animated) {
        logStopVideo();
        this.mView.removeCallbacks(this.mStartPreviewVideoRunnable);
        this.mPreviewVideoDelayOverlay.setVisibility(8);
        if (this.mPreviewImageVisibilityValue == 1.0f) {
            finishStoppingPreviewVideo();
        } else if (animated) {
            fadePreviewImageIn();
        } else {
            this.mPreviewImageContainer.setVisibility(0);
            setPreviewImageVisibilityValue(1.0f);
            finishStoppingPreviewVideo();
        }
    }

    private void logStopVideo() {
        if (this.mStartedPreviewVideoMillis != 0) {
            this.mWatchedPreviewVideoSeconds = (int) ((SystemClock.elapsedRealtime() - this.mStartedPreviewVideoMillis) / 1000);
            if (this.mWatchedPreviewVideoSeconds > 0) {
                this.mEventLogger.log(new UserActionEvent(LogEvents.WATCH_PREVIEW).putParameter(LogEvents.PARAMETER_WATCHED_PREVIEW_VIDEO_SECONDS, this.mWatchedPreviewVideoSeconds));
            }
            this.mStartedPreviewVideoMillis = 0;
        }
    }

    void setOnProgramViewFocusedListener(OnProgramViewFocusedListener listener) {
        this.mOnProgramViewFocusedListener = listener;
    }

    void setIsWatchNextProgram(boolean isWatchNextProgram) {
        this.mIsWatchNextProgram = isWatchNextProgram;
    }

    void bind(Program program, String packageName, int programState, boolean canAddToWatchNext, boolean canRemoveProgram, boolean isLegacy) {
        if (this.mProgramMenu != null && this.mProgramMenu.isShowing()) {
            this.mProgramMenu.forceDismiss();
        }
        this.mChannelPackageName = packageName;
        this.mProgramState = programState;
        this.mCanAddToWatchNext = canAddToWatchNext;
        this.mCanRemoveProgram = canRemoveProgram;
        this.mIsLegacy = isLegacy;
        this.mProgramId = program.getId();
        this.mContentId = program.getContentId();
        this.mPreviewVideoUri = program.getPreviewVideoUri();
        String currentPreviewVideoUri = this.mPreviewVideo.getVideoUri() != null ? this.mPreviewVideo.getVideoUri().toString() : null;
        if (!(currentPreviewVideoUri == null || currentPreviewVideoUri.equals(this.mPreviewVideoUri))) {
            stopPreviewVideo(false);
        }
        if (this.mPreviewVideoUri != null) {
            this.mPreviewVideo.setVideoUri(Uri.parse(this.mPreviewVideoUri));
        } else {
            this.mPreviewVideo.setVideoUri(null);
        }
        this.mWatchedPreviewVideoSeconds = 0;
        this.mThumbnail.setVisibility(8);
        this.mActionUri = program.getActionUri();
        this.mProgramType = program.getType();
        this.mProgramDuration = program.getDuration();
        this.mProgramIsLive = program.isLive();
        this.mUnfocusedAspectRatio = Double.valueOf(ProgramUtil.getAspectRatio(program.getPreviewImageAspectRatio()));
        updateSize();
        this.mThumbnailUri = null;
        this.mFocusedAspectRatio = null;
        if (!this.mIsWatchNextProgram) {
            if (this.mPreviewVideoUri != null) {
                this.mFocusedAspectRatio = Double.valueOf(ProgramUtil.ASPECT_RATIO_16_9);
            } else {
                this.mThumbnailUri = program.getThumbnailUri();
                if (this.mThumbnailUri != null) {
                    this.mFocusedAspectRatio = Double.valueOf(ProgramUtil.getAspectRatio(program.getThumbnailAspectRatio()));
                }
            }
        }
        if (this.mFocusedAspectRatio == null) {
            this.mFocusedAspectRatio = this.mUnfocusedAspectRatio;
        }
        this.mFocusHandler.setFocusedAspectRatio(this.mFocusedAspectRatio.doubleValue());
        this.mFocusHandler.setUnfocusedAspectRatio(this.mUnfocusedAspectRatio.doubleValue());
        this.mFocusHandler.resetFocusedState();
        boolean z = this.mPreviewVideoUri != null && Math.abs(this.mUnfocusedAspectRatio.doubleValue() - this.mFocusedAspectRatio.doubleValue()) > EPS;
        this.mPreviewImageNeedsTreatment = z;
        this.mPreviewImage.setContentDescription(program.getTitle());
        updatePreviewImageTreatment(this.mView.isFocused());
        loadPreviewImage(program.getPreviewImageUri());
        if (this.mThumbnailUri != null) {
            loadThumbnailImage();
            if (this.mView.isFocused()) {
                this.mThumbnail.setVisibility(0);
            }
        } else {
            this.mThumbnail.setImageDrawable(null);
        }
        this.mLogoUri = program.getLogoUri();
        updateBadges(this.mView.isFocused());
        if (!this.mIsWatchNextProgram || program.getDuration() <= 0 || program.getPlaybackPosition() <= 0 || program.getWatchNextType() != 0) {
            this.mPlaybackProgress.setVisibility(8);
            this.mPlaybackProgressDimmer.setVisibility(8);
            return;
        }
        // this.mPlaybackProgress.setMin(0);
        this.mPlaybackProgress.setMax((int) program.getDuration());
        this.mPlaybackProgress.setProgress((int) program.getPlaybackPosition());
        this.mPlaybackProgress.setVisibility(0);
        this.mPlaybackProgressDimmer.setVisibility(needLogo() ? 8 : 0);
    }

    private String formatDurationInHoursMinutesAndSeconds(long milliseconds) {
        return DateUtils.formatElapsedTime(milliseconds / 1000);
    }

    private boolean needLogo() {
        if (this.mLogoUri == null) {
            return false;
        }
        switch (this.mProgramType) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 5:
            case 6:
                return true;
            default:
                return false;
        }
    }

    private boolean needLiveBadge() {
        return this.mProgramIsLive;
    }

    private boolean needDurationBadge() {
        long hours = this.mProgramDuration / 3600000;
        if (this.mProgramDuration < 1000 || hours > 99 || this.mProgramType != 4) {
            return false;
        }
        return true;
    }

    private void updateBadges(boolean hasFocus) {
        boolean isSelectedRow = true;
        if (this.mProgramState != 1) {
            isSelectedRow = false;
        }
        boolean hasLiveIcon = false;
        if (!this.mIsLegacy && isSelectedRow && needLogo()) {
            loadLogoImage();
            if (Util.isRtl(this.mView.getContext())) {
                this.mLogo.setScaleType(ScaleType.FIT_END);
            }
            this.mLogo.setVisibility(0);
            this.mLogoDimmer.setVisibility(0);
        } else {
            this.mLogo.setImageDrawable(null);
            this.mLogo.setVisibility(8);
            this.mLogoDimmer.setVisibility(8);
        }
        if (this.mIsLegacy || !isSelectedRow || !needLiveBadge()) {
            this.mLiveBadge.setVisibility(8);
            this.mLiveIcon.setVisibility(8);
        } else if ((!hasFocus || Double.compare(this.mFocusedAspectRatio.doubleValue(), ProgramUtil.ASPECT_RATIO_2_3) <= 0) && (hasFocus || Double.compare(this.mUnfocusedAspectRatio.doubleValue(), ProgramUtil.ASPECT_RATIO_2_3) <= 0)) {
            hasLiveIcon = true;
            this.mLiveBadge.setVisibility(8);
            this.mLiveIcon.setVisibility(0);
        } else {
            this.mLiveBadge.setVisibility(0);
            this.mLiveIcon.setVisibility(8);
        }
        if (this.mIsLegacy || !isSelectedRow || hasLiveIcon || !needDurationBadge()) {
            this.mDurationBadge.setVisibility(8);
            return;
        }
        this.mDurationBadge.setText(formatDurationInHoursMinutesAndSeconds(this.mProgramDuration));
        this.mDurationBadge.setVisibility(0);
    }

    void bindState(int programState) {
        this.mProgramState = programState;
        updateBadges(this.mView.isFocused());
        updateSize();
    }

    private void updatePreviewImageTreatment(boolean focused) {
        MarginLayoutParams lp = (MarginLayoutParams) this.mPreviewImage.getLayoutParams();

        if (focused && this.mPreviewImageNeedsTreatment) {
            this.mPreviewImage.setScaleType(ScaleType.FIT_CENTER);
            lp.setMargins(0, this.mPreviewImageExpandedVerticalMargin, 0, this.mPreviewImageExpandedVerticalMargin);
            this.mPreviewImage.setLayoutParams(lp);
            setPreviewImageSpecialBackground();
            return;
        }
        this.mPreviewImage.setScaleType(ScaleType.CENTER_CROP);
        lp = (MarginLayoutParams) this.mPreviewImage.getLayoutParams();
        lp.setMargins(0, 0, 0, 0);
        this.mPreviewImage.setLayoutParams(lp);
        setPreviewImageDefaultBackground();
    }

    private void setPreviewImageSpecialBackground() {
        this.mPreviewImageBackground.setVisibility(0);
        if (this.mBlurredPreviewImageDrawable == null || isNullOrTransparent(this.mPreviewImage.getDrawable())) {
            this.mPreviewImageBackground.setImageDrawable(this.mProgramDefaultBackgroundDrawable);
        } else {
            this.mPreviewImageBackground.setImageDrawable(this.mBlurredPreviewImageDrawable);
        }
    }

    private void setPreviewImageDefaultBackground() {
        if (isNullOrTransparent(this.mPreviewImage.getDrawable())) {
            this.mPreviewImageBackground.setVisibility(0);
            this.mPreviewImageBackground.setImageDrawable(this.mProgramDefaultBackgroundDrawable);
            return;
        }
        this.mPreviewImageBackground.setVisibility(8);
        this.mPreviewImageBackground.setImageDrawable(null);
    }

    private boolean isNullOrTransparent(Drawable drawable) {
        return drawable == null || ((drawable instanceof BitmapDrawable) && ((BitmapDrawable) drawable).getBitmap().hasAlpha());
    }

    private void loadPreviewImage(String previewImageUri) {
        this.mPreviewImageBackground.setImageDrawable(this.mProgramDefaultBackgroundDrawable);
        this.mPreviewImagePalette = null;
        this.mBlurredPreviewImageDrawable = null;
        if (this.mPreviewImageNeedsTreatment) {
            loadPreviewImageWithBlur(previewImageUri);
        } else {
            loadPreviewImageWithoutBlur(previewImageUri);
        }
    }

    private void loadPreviewImageWithBlur(String previewImageUri) {
        RequestBuilder<ProgramPreviewImageData> builder = Glide.with(this.mPreviewImage.getContext()).as(ProgramPreviewImageData.class).load(previewImageUri).apply(this.mImageRequestOptions);
        if (this.mPreviewImageBlurGlideTarget == null) {
            this.mPreviewImageBlurGlideTarget = new ImageViewTarget<ProgramPreviewImageData>(this.mPreviewImage) {
                protected void setResource(ProgramPreviewImageData resource) {
                    if (resource != null) {
                        ((ImageView) this.view).setImageBitmap(resource.getBitmap());
                        ProgramController.this.mPreviewImagePalette = resource.getPalette();
                        ProgramController.this.mBlurredPreviewImageDrawable = new BitmapDrawable(ProgramController.this.mView.getResources(), resource.getBlurredBitmap());
                    } else {
                        ((ImageView) this.view).setImageDrawable(null);
                    }
                    ProgramController.this.updatePreviewImageTreatment(ProgramController.this.mView.isFocused());
                }
            };
        }
        builder.into(this.mPreviewImageBlurGlideTarget);
    }

    private void loadPreviewImageWithoutBlur(String previewImageUri) {
        RequestBuilder<PaletteBitmapContainer> builder = Glide.with(this.mPreviewImage.getContext()).as(PaletteBitmapContainer.class).load(previewImageUri).apply(this.mImageRequestOptions);
        if (this.mPreviewImagePaletteGlideTarget == null) {
            this.mPreviewImagePaletteGlideTarget = new ImageViewTarget<PaletteBitmapContainer>(this.mPreviewImage) {
                protected void setResource(PaletteBitmapContainer resource) {
                    if (resource != null) {
                        ((ImageView) this.view).setImageBitmap(resource.getBitmap());
                        ProgramController.this.mPreviewImagePalette = resource.getPalette();
                    } else {
                        ((ImageView) this.view).setImageDrawable(null);
                    }
                    ProgramController.this.setPreviewImageDefaultBackground();
                }
            };
        }
        builder.into(this.mPreviewImagePaletteGlideTarget);
    }

    private void loadThumbnailImage() {
        this.mThumbnail.setImageDrawable(null);
        Glide.with(this.mThumbnail.getContext()).load(this.mThumbnailUri).apply(this.mImageRequestOptions).into(this.mThumbnail);
    }

    private void loadLogoImage() {
        this.mLogo.setImageDrawable(null);
        Glide.with(this.mLogo.getContext()).load(this.mLogoUri).into(this.mLogo);
    }

    private void updateSize() {
        ProgramUtil.updateSize(this.mView, this.mProgramState, this.mUnfocusedAspectRatio.doubleValue(), this.mProgramSettings);
    }

    private void fadePreviewImageOut() {
        if (this.mPreviewImageFadeOutAnimator == null || !this.mPreviewImageFadeOutAnimator.isRunning()) {
            if (this.mPreviewImageFadeInAnimator != null && this.mPreviewImageFadeInAnimator.isRunning()) {
                this.mPreviewImageFadeInAnimator.cancel();
                this.mPreviewImageFadeInAnimator = null;
            }
            if (this.mPreviewImageFadeOutAnimatorListener == null) {
                this.mPreviewImageFadeOutAnimatorListener = new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animation) {
                        ProgramController.this.mPreviewVideo.setVisibility(0);
                    }

                    public void onAnimationEnd(Animator animation) {
                        ProgramController.this.mPreviewImageContainer.setVisibility(8);
                    }
                };
            }
            this.mPreviewImageFadeOutAnimator = ValueAnimator.ofFloat(new float[]{this.mPreviewImageVisibilityValue, 0.0f});
            this.mPreviewImageFadeOutAnimator.addUpdateListener(getPreviewImageFadeUpdateListener());
            this.mPreviewImageFadeOutAnimator.addListener(this.mPreviewImageFadeOutAnimatorListener);
            this.mPreviewImageFadeOutAnimator.setDuration(300);
            this.mPreviewImageFadeOutAnimator.start();
        }
    }

    private void fadePreviewImageIn() {
        if (this.mPreviewImageFadeInAnimator == null || !this.mPreviewImageFadeInAnimator.isRunning()) {
            if (this.mPreviewImageFadeOutAnimator != null && this.mPreviewImageFadeOutAnimator.isRunning()) {
                this.mPreviewImageFadeOutAnimator.cancel();
                this.mPreviewImageFadeOutAnimator = null;
            }
            if (this.mPreviewImageFadeInAnimatorListener == null) {
                this.mPreviewImageFadeInAnimatorListener = new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animation) {
                        ProgramController.this.mPreviewImageContainer.setVisibility(0);
                    }

                    public void onAnimationEnd(Animator animation) {
                        ProgramController.this.finishStoppingPreviewVideo();
                    }
                };
            }
            this.mPreviewImageFadeInAnimator = ValueAnimator.ofFloat(new float[]{this.mPreviewImageVisibilityValue, 1.0f});
            this.mPreviewImageFadeInAnimator.addUpdateListener(getPreviewImageFadeUpdateListener());
            this.mPreviewImageFadeInAnimator.addListener(this.mPreviewImageFadeInAnimatorListener);
            this.mPreviewImageFadeInAnimator.setDuration(300);
            this.mPreviewImageFadeInAnimator.start();
        }
    }

    private AnimatorUpdateListener getPreviewImageFadeUpdateListener() {
        if (this.mPreviewImageFadeUpdateListener == null) {
            this.mPreviewImageFadeUpdateListener = new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    ProgramController.this.setPreviewImageVisibilityValue(((Float) animation.getAnimatedValue()).floatValue());
                }
            };
        }
        return this.mPreviewImageFadeUpdateListener;
    }

    private void setPreviewImageVisibilityValue(float visibilityValue) {
        this.mPreviewImageVisibilityValue = visibilityValue;
        this.mPreviewImageContainer.setAlpha(this.mPreviewImageVisibilityValue);
        this.mLogoBadgesContainer.setAlpha(this.mPreviewImageVisibilityValue);
        if (this.mAllowAudioPlaying) {
            this.mPreviewVideo.setVolume(1.0f - this.mPreviewImageVisibilityValue);
        }
    }

    boolean isViewFocused() {
        return this.mView.isFocused();
    }

    Palette getPreviewImagePalette() {
        return this.mPreviewImagePalette;
    }

    public void onClick(View v) {
        if (Util.isAccessibilityEnabled(v.getContext())) {
            onLongClick(v);
        } else {
            onPrimaryAction();
        }
    }

    private void onPrimaryAction() {
        if (this.mActionUri != null) {
            UserActionEvent event = new UserActionEvent(LogEvents.START_PROGRAM);
            event.putParameter(LogEvents.PARAMETER_PROGRAM_TYPE, LogUtils.programTypeToString(this.mProgramType));
            if (!(this.mIsWatchNextProgram || this.mPreviewVideoUri == null)) {
                stopPreviewVideo(true);
                event.putParameter(LogEvents.PARAMETER_HAS_PREVIEW_VIDEO, true).putParameter(LogEvents.PARAMETER_WATCHED_PREVIEW_VIDEO_SECONDS, this.mWatchedPreviewVideoSeconds);
            }
            this.mEventLogger.log(event);
            IntentLauncher.launchMediaIntentFromUri(this.mView.getContext(), this.mActionUri);
        }
    }

    public boolean onLongClick(View v) {
        this.mProgramMenu = new ContextMenu((Activity) v.getContext(), v, v.getResources().getDimensionPixelSize(R.dimen.card_rounded_corner_radius));
        if (Util.isAccessibilityEnabled(v.getContext())) {
            this.mProgramMenu.addItem(new ContextMenuItem(1, v.getContext().getString(R.string.banner_sidebar_primary_action_text), null));
        }
        if (this.mIsWatchNextProgram) {
            this.mProgramMenu.addItem(new ContextMenuItem(4, v.getContext().getString(R.string.program_menu_remove_for_watch_next_text), v.getContext().getDrawable(R.drawable.ic_context_menu_uninstall_black)));
        } else {
            boolean isInWatchNext = TvDataManager.getInstance(v.getContext()).isInWatchNext(this.mContentId, this.mChannelPackageName);
            ContextMenuItem addToWatchNextItem = new ContextMenuItem(2, null, v.getContext().getDrawable(R.drawable.ic_context_menu_add_to_watch_next_black));
            this.mProgramMenu.addItem(addToWatchNextItem);
            boolean z = this.mCanAddToWatchNext && !isInWatchNext;
            addToWatchNextItem.setEnabled(z);
            if (isInWatchNext) {
                addToWatchNextItem.setTitle(this.mProgramMenuAlreadyInWatchNextText);
            } else {
                addToWatchNextItem.setTitle(this.mCanAddToWatchNext ? this.mProgramMenuAddToWatchNextText : this.mProgramMenuAddToWatchNextNotAvailableText);
            }
            ContextMenuItem removeProgramItem = new ContextMenuItem(3, null, v.getContext().getDrawable(R.drawable.ic_context_menu_uninstall_black));
            this.mProgramMenu.addItem(removeProgramItem);
            removeProgramItem.setEnabled(this.mCanRemoveProgram);
            removeProgramItem.setTitle(this.mCanRemoveProgram ? this.mProgramMenuRemoveText : this.mProgramMenuRemoveNotAvailableText);
        }
        this.mProgramMenu.setOnMenuItemClickListener(this);
        this.mProgramMenu.show();
        return true;
    }

    public void onItemClick(ContextMenuItem item) {
        LogEvent userEvent;
        switch (item.getId()) {
            case 1:
                onPrimaryAction();
                return;
            case 2:
                TvDataManager.addProgramToWatchlist(this.mView.getContext(), this.mProgramId, this.mChannelPackageName);
                TvDataManager.getInstance(this.mView.getContext()).addProgramToWatchNextCache(this.mContentId, this.mChannelPackageName);
                userEvent = new UserActionEvent(LogEvents.ADD_PROGRAM_TO_WATCH_NEXT);
                if (this.mChannelPackageName != null) {
                    userEvent.putParameter("package_name", this.mChannelPackageName);
                }
                this.mEventLogger.log(userEvent);
                return;
            case 3:
                TvDataManager.removePreviewProgram(this.mView.getContext(), this.mProgramId, this.mChannelPackageName);
                userEvent = new UserActionEvent(LogEvents.REMOVE_PROGRAM_FROM_CHANNEL).putParameter(LogEvents.PARAMETER_PROGRAM_TYPE, this.mProgramType);
                if (this.mChannelPackageName != null) {
                    userEvent.putParameter("package_name", this.mChannelPackageName);
                }
                this.mEventLogger.log(userEvent);
                return;
            case 4:
                TvDataManager.removeProgramFromWatchlist(this.mView.getContext(), this.mProgramId, this.mEventLogger);
                TvDataManager.getInstance(this.mView.getContext()).removeProgramFromWatchNextCache(this.mContentId, this.mChannelPackageName);
                return;
            default:
                return;
        }
    }
}
