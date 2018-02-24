package com.google.android.tvlauncher.home;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.SystemClock;
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
import com.bumptech.glide.Registry;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.google.android.tvlauncher.analytics.EventLogger;
import com.google.android.tvlauncher.analytics.LogEvent;
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

class ProgramController
  implements View.OnClickListener, ContextMenu.OnItemClickListener, View.OnLongClickListener
{
  private static final boolean DEBUG = false;
  private static final double EPS = 0.001D;
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
  private View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener()
  {
    public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
    {
      if ((ProgramController.this.mProgramMenu != null) && (ProgramController.this.mProgramMenu.isShowing())) {
        ProgramController.this.mProgramMenu.forceDismiss();
      }
      if (!ProgramController.this.mIsWatchNextProgram)
      {
        if (ProgramController.this.mPreviewVideoUri == null) {
          break label141;
        }
        if ((!paramAnonymousBoolean) || (ActiveMediaSessionManager.getInstance(paramAnonymousView.getContext()).hasActiveMediaSession()) || (!ProgramController.this.allowPreviewVideoPlaying())) {
          break label130;
        }
        ProgramController.this.startPreviewVideoDelayed();
        ProgramController.this.updatePreviewImageTreatment(paramAnonymousBoolean);
      }
      label130:
      label141:
      while (ProgramController.this.mThumbnailUri == null) {
        for (;;)
        {
          if ((ProgramController.this.mOnProgramViewFocusedListener != null) && (paramAnonymousBoolean)) {
            ProgramController.this.mOnProgramViewFocusedListener.onProgramViewFocused();
          }
          ProgramController.this.updateBadges(paramAnonymousBoolean);
          return;
          ProgramController.this.stopPreviewVideo(true);
        }
      }
      paramAnonymousView = ProgramController.this.mThumbnail;
      if (paramAnonymousBoolean) {}
      for (int i = 0;; i = 8)
      {
        paramAnonymousView.setVisibility(i);
        break;
      }
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
  private Animator.AnimatorListener mPreviewImageFadeInAnimatorListener;
  private ValueAnimator mPreviewImageFadeOutAnimator;
  private Animator.AnimatorListener mPreviewImageFadeOutAnimatorListener;
  private ValueAnimator.AnimatorUpdateListener mPreviewImageFadeUpdateListener;
  private boolean mPreviewImageNeedsTreatment;
  private Palette mPreviewImagePalette;
  private ImageViewTarget<PaletteBitmapContainer> mPreviewImagePaletteGlideTarget;
  private float mPreviewImageVisibilityValue = 1.0F;
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
  private Runnable mStartPreviewVideoRunnable = new Runnable()
  {
    public void run()
    {
      if ((!ProgramController.this.mIsWatchNextProgram) && (ProgramController.this.mPreviewVideoUri != null) && (ProgramController.this.mView.isFocused()))
      {
        ProgramController.this.mPreviewVideo.setVisibility(0);
        ProgramController.access$702(ProgramController.this, ProgramController.this.mPreviewVideoPref.getBoolean("enable_preview_audio_key", true));
        if (!ProgramController.this.mAllowAudioPlaying) {
          ProgramController.this.mPreviewVideo.setVolume(0.0F);
        }
        ProgramController.this.mPreviewVideo.start(ProgramController.this.mVideoCallback);
      }
    }
  };
  private long mStartedPreviewVideoMillis;
  private final ImageView mThumbnail;
  private String mThumbnailUri;
  private Double mUnfocusedAspectRatio;
  private InstantVideoView.VideoCallback mVideoCallback = new InstantVideoView.VideoCallback()
  {
    public void onVideoEnded(InstantVideoView paramAnonymousInstantVideoView)
    {
      ProgramController.this.stopPreviewVideo(true);
    }
    
    public void onVideoError(InstantVideoView paramAnonymousInstantVideoView)
    {
      Log.e("ProgramController", "onVideoError: uri=[" + paramAnonymousInstantVideoView.getVideoUri() + "]");
      ProgramController.this.stopPreviewVideo(true);
    }
    
    public void onVideoStarted(InstantVideoView paramAnonymousInstantVideoView)
    {
      ProgramController.access$002(ProgramController.this, SystemClock.elapsedRealtime());
      ProgramController.this.fadePreviewImageOut();
      ProgramController.this.mPreviewVideoDelayOverlay.setVisibility(8);
    }
  };
  private final View mView;
  private int mWatchedPreviewVideoSeconds;
  
  ProgramController(View paramView, EventLogger paramEventLogger)
  {
    this.mView = paramView;
    paramView.setOutlineProvider(new ViewOutlineProvider()
    {
      public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
      {
        paramAnonymousOutline.setRoundRect(0, 0, paramAnonymousView.getWidth(), paramAnonymousView.getHeight(), paramAnonymousView.getResources().getDimensionPixelSize(2131558510));
      }
    });
    paramView.setClipToOutline(true);
    paramView.setOnClickListener(this);
    paramView.setOnLongClickListener(this);
    this.mPreviewVideoPref = this.mView.getContext().getSharedPreferences("com.google.android.tvlauncher.data.TvDataManager.PREVIEW_VIDEO_PREF_FILE_NAME", 0);
    this.mEventLogger = paramEventLogger;
    this.mProgramSettings = ProgramUtil.getProgramSettings(paramView.getContext());
    if (!sPreviewImageTranscoderRegistered)
    {
      Glide.get(this.mView.getContext()).getRegistry().register(Bitmap.class, ProgramPreviewImageData.class, new ProgramPreviewImageTranscoder(this.mView.getContext()));
      sPreviewImageTranscoderRegistered = true;
    }
    this.mPreviewImage = ((ImageView)paramView.findViewById(2131951786));
    this.mPreviewImageContainer = paramView.findViewById(2131952043);
    this.mPreviewImageBackground = ((ImageView)paramView.findViewById(2131952044));
    this.mPreviewVideo = ((InstantVideoView)paramView.findViewById(2131952042));
    this.mPreviewVideo.setImageViewEnabled(false);
    this.mThumbnail = ((ImageView)paramView.findViewById(2131952041));
    this.mLogoBadgesContainer = paramView.findViewById(2131952045);
    this.mLogo = ((ImageView)paramView.findViewById(2131952047));
    this.mLogoDimmer = paramView.findViewById(2131952046);
    this.mBadgesContainer = paramView.findViewById(2131952048);
    this.mLiveBadge = ((TextView)paramView.findViewById(2131952049));
    this.mLiveIcon = ((ImageView)paramView.findViewById(2131952050));
    this.mDurationBadge = ((TextView)paramView.findViewById(2131952051));
    this.mBadgesContainer.setOutlineProvider(new ViewOutlineProvider()
    {
      public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
      {
        paramAnonymousOutline.setRoundRect(0, 0, paramAnonymousView.getWidth(), paramAnonymousView.getHeight(), paramAnonymousView.getResources().getDimensionPixelSize(2131558966));
      }
    });
    this.mBadgesContainer.setClipToOutline(true);
    this.mPreviewVideoDelayOverlay = paramView.findViewById(2131952053);
    this.mPlaybackProgress = ((ProgressBar)paramView.findViewById(2131952054));
    this.mPlaybackProgressDimmer = paramView.findViewById(2131952052);
    this.mFocusHandler = new ScaleAndExpandFocusHandler(this.mProgramSettings.focusedAnimationDuration, this.mProgramSettings.focusedScale, this.mProgramSettings.focusedElevation, 1);
    this.mFocusHandler.setView(paramView);
    this.mFocusHandler.setOnFocusChangeListener(this.mOnFocusChangeListener);
    this.mProgramDefaultBackgroundDrawable = new ColorDrawable(paramView.getContext().getColor(2131820710));
    this.mPreviewImageExpandedVerticalMargin = this.mView.getResources().getDimensionPixelOffset(2131558996);
    int i = this.mProgramSettings.selectedHeight;
    int j = (int)(this.mProgramSettings.selectedHeight * 1.7777777777777777D);
    this.mImageRequestOptions = ((RequestOptions)((RequestOptions)new RequestOptions().override(j + 20, i)).centerInside(this.mView.getContext()));
    this.mProgramMenuAddToWatchNextText = this.mView.getContext().getString(2131493060);
    this.mProgramMenuRemoveText = this.mView.getContext().getString(2131493064);
    this.mProgramMenuAddToWatchNextNotAvailableText = this.mView.getContext().getString(2131493059);
    this.mProgramMenuAlreadyInWatchNextText = this.mView.getContext().getString(2131493061);
    this.mProgramMenuRemoveNotAvailableText = this.mView.getContext().getString(2131493063);
  }
  
  private boolean allowPreviewVideoPlaying()
  {
    return this.mView.getContext().getSharedPreferences("com.google.android.tvlauncher.data.TvDataManager.PREVIEW_VIDEO_PREF_FILE_NAME", 0).getBoolean("show_preview_video_key", true);
  }
  
  private void fadePreviewImageIn()
  {
    if ((this.mPreviewImageFadeInAnimator != null) && (this.mPreviewImageFadeInAnimator.isRunning())) {
      return;
    }
    if ((this.mPreviewImageFadeOutAnimator != null) && (this.mPreviewImageFadeOutAnimator.isRunning()))
    {
      this.mPreviewImageFadeOutAnimator.cancel();
      this.mPreviewImageFadeOutAnimator = null;
    }
    if (this.mPreviewImageFadeInAnimatorListener == null) {
      this.mPreviewImageFadeInAnimatorListener = new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          ProgramController.this.finishStoppingPreviewVideo();
        }
        
        public void onAnimationStart(Animator paramAnonymousAnimator)
        {
          ProgramController.this.mPreviewImageContainer.setVisibility(0);
        }
      };
    }
    this.mPreviewImageFadeInAnimator = ValueAnimator.ofFloat(new float[] { this.mPreviewImageVisibilityValue, 1.0F });
    this.mPreviewImageFadeInAnimator.addUpdateListener(getPreviewImageFadeUpdateListener());
    this.mPreviewImageFadeInAnimator.addListener(this.mPreviewImageFadeInAnimatorListener);
    this.mPreviewImageFadeInAnimator.setDuration(300L);
    this.mPreviewImageFadeInAnimator.start();
  }
  
  private void fadePreviewImageOut()
  {
    if ((this.mPreviewImageFadeOutAnimator != null) && (this.mPreviewImageFadeOutAnimator.isRunning())) {
      return;
    }
    if ((this.mPreviewImageFadeInAnimator != null) && (this.mPreviewImageFadeInAnimator.isRunning()))
    {
      this.mPreviewImageFadeInAnimator.cancel();
      this.mPreviewImageFadeInAnimator = null;
    }
    if (this.mPreviewImageFadeOutAnimatorListener == null) {
      this.mPreviewImageFadeOutAnimatorListener = new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          ProgramController.this.mPreviewImageContainer.setVisibility(8);
        }
        
        public void onAnimationStart(Animator paramAnonymousAnimator)
        {
          ProgramController.this.mPreviewVideo.setVisibility(0);
        }
      };
    }
    this.mPreviewImageFadeOutAnimator = ValueAnimator.ofFloat(new float[] { this.mPreviewImageVisibilityValue, 0.0F });
    this.mPreviewImageFadeOutAnimator.addUpdateListener(getPreviewImageFadeUpdateListener());
    this.mPreviewImageFadeOutAnimator.addListener(this.mPreviewImageFadeOutAnimatorListener);
    this.mPreviewImageFadeOutAnimator.setDuration(300L);
    this.mPreviewImageFadeOutAnimator.start();
  }
  
  private void finishStoppingPreviewVideo()
  {
    this.mPreviewVideo.setVisibility(8);
    this.mPreviewVideo.stop();
  }
  
  private String formatDurationInHoursMinutesAndSeconds(long paramLong)
  {
    return DateUtils.formatElapsedTime(paramLong / 1000L);
  }
  
  private ValueAnimator.AnimatorUpdateListener getPreviewImageFadeUpdateListener()
  {
    if (this.mPreviewImageFadeUpdateListener == null) {
      this.mPreviewImageFadeUpdateListener = new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          ProgramController.this.setPreviewImageVisibilityValue(((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue());
        }
      };
    }
    return this.mPreviewImageFadeUpdateListener;
  }
  
  private boolean isNullOrTransparent(Drawable paramDrawable)
  {
    return (paramDrawable == null) || (((paramDrawable instanceof BitmapDrawable)) && (((BitmapDrawable)paramDrawable).getBitmap().hasAlpha()));
  }
  
  private void loadLogoImage()
  {
    this.mLogo.setImageDrawable(null);
    Glide.with(this.mLogo.getContext()).load(this.mLogoUri).into(this.mLogo);
  }
  
  private void loadPreviewImage(String paramString)
  {
    this.mPreviewImageBackground.setImageDrawable(this.mProgramDefaultBackgroundDrawable);
    this.mPreviewImagePalette = null;
    this.mBlurredPreviewImageDrawable = null;
    if (this.mPreviewImageNeedsTreatment)
    {
      loadPreviewImageWithBlur(paramString);
      return;
    }
    loadPreviewImageWithoutBlur(paramString);
  }
  
  private void loadPreviewImageWithBlur(String paramString)
  {
    paramString = Glide.with(this.mPreviewImage.getContext()).as(ProgramPreviewImageData.class).load(paramString).apply(this.mImageRequestOptions);
    if (this.mPreviewImageBlurGlideTarget == null) {
      this.mPreviewImageBlurGlideTarget = new ImageViewTarget(this.mPreviewImage)
      {
        protected void setResource(ProgramPreviewImageData paramAnonymousProgramPreviewImageData)
        {
          if (paramAnonymousProgramPreviewImageData != null)
          {
            ((ImageView)this.view).setImageBitmap(paramAnonymousProgramPreviewImageData.getBitmap());
            ProgramController.access$1802(ProgramController.this, paramAnonymousProgramPreviewImageData.getPalette());
            ProgramController.access$1902(ProgramController.this, new BitmapDrawable(ProgramController.this.mView.getResources(), paramAnonymousProgramPreviewImageData.getBlurredBitmap()));
          }
          for (;;)
          {
            ProgramController.this.updatePreviewImageTreatment(ProgramController.this.mView.isFocused());
            return;
            ((ImageView)this.view).setImageDrawable(null);
          }
        }
      };
    }
    paramString.into(this.mPreviewImageBlurGlideTarget);
  }
  
  private void loadPreviewImageWithoutBlur(String paramString)
  {
    paramString = Glide.with(this.mPreviewImage.getContext()).as(PaletteBitmapContainer.class).load(paramString).apply(this.mImageRequestOptions);
    if (this.mPreviewImagePaletteGlideTarget == null) {
      this.mPreviewImagePaletteGlideTarget = new ImageViewTarget(this.mPreviewImage)
      {
        protected void setResource(PaletteBitmapContainer paramAnonymousPaletteBitmapContainer)
        {
          if (paramAnonymousPaletteBitmapContainer != null)
          {
            ((ImageView)this.view).setImageBitmap(paramAnonymousPaletteBitmapContainer.getBitmap());
            ProgramController.access$1802(ProgramController.this, paramAnonymousPaletteBitmapContainer.getPalette());
          }
          for (;;)
          {
            ProgramController.this.setPreviewImageDefaultBackground();
            return;
            ((ImageView)this.view).setImageDrawable(null);
          }
        }
      };
    }
    paramString.into(this.mPreviewImagePaletteGlideTarget);
  }
  
  private void loadThumbnailImage()
  {
    this.mThumbnail.setImageDrawable(null);
    Glide.with(this.mThumbnail.getContext()).load(this.mThumbnailUri).apply(this.mImageRequestOptions).into(this.mThumbnail);
  }
  
  private void logStopVideo()
  {
    if (this.mStartedPreviewVideoMillis == 0L) {
      return;
    }
    this.mWatchedPreviewVideoSeconds = ((int)((SystemClock.elapsedRealtime() - this.mStartedPreviewVideoMillis) / 1000L));
    if (this.mWatchedPreviewVideoSeconds > 0) {
      this.mEventLogger.log(new UserActionEvent("watch_preview").putParameter("watched_preview_video_seconds", this.mWatchedPreviewVideoSeconds));
    }
    this.mStartedPreviewVideoMillis = 0L;
  }
  
  private boolean needDurationBadge()
  {
    long l = this.mProgramDuration / 3600000L;
    if ((this.mProgramDuration < 1000L) || (l > 99L)) {}
    while (this.mProgramType != 4) {
      return false;
    }
    return true;
  }
  
  private boolean needLiveBadge()
  {
    return this.mProgramIsLive;
  }
  
  private boolean needLogo()
  {
    if (this.mLogoUri == null) {
      return false;
    }
    switch (this.mProgramType)
    {
    case 4: 
    default: 
      return false;
    }
    return true;
  }
  
  private void onPrimaryAction()
  {
    if (this.mActionUri != null)
    {
      UserActionEvent localUserActionEvent = new UserActionEvent("start_program");
      localUserActionEvent.putParameter("program_type", LogUtils.programTypeToString(this.mProgramType));
      if ((!this.mIsWatchNextProgram) && (this.mPreviewVideoUri != null))
      {
        stopPreviewVideo(true);
        localUserActionEvent.putParameter("has_preview_video", true).putParameter("watched_preview_video_seconds", this.mWatchedPreviewVideoSeconds);
      }
      this.mEventLogger.log(localUserActionEvent);
      IntentLauncher.launchMediaIntentFromUri(this.mView.getContext(), this.mActionUri);
    }
  }
  
  private void setPreviewImageDefaultBackground()
  {
    if (isNullOrTransparent(this.mPreviewImage.getDrawable()))
    {
      this.mPreviewImageBackground.setVisibility(0);
      this.mPreviewImageBackground.setImageDrawable(this.mProgramDefaultBackgroundDrawable);
      return;
    }
    this.mPreviewImageBackground.setVisibility(8);
    this.mPreviewImageBackground.setImageDrawable(null);
  }
  
  private void setPreviewImageSpecialBackground()
  {
    this.mPreviewImageBackground.setVisibility(0);
    if ((this.mBlurredPreviewImageDrawable == null) || (isNullOrTransparent(this.mPreviewImage.getDrawable())))
    {
      this.mPreviewImageBackground.setImageDrawable(this.mProgramDefaultBackgroundDrawable);
      return;
    }
    this.mPreviewImageBackground.setImageDrawable(this.mBlurredPreviewImageDrawable);
  }
  
  private void setPreviewImageVisibilityValue(float paramFloat)
  {
    this.mPreviewImageVisibilityValue = paramFloat;
    this.mPreviewImageContainer.setAlpha(this.mPreviewImageVisibilityValue);
    this.mLogoBadgesContainer.setAlpha(this.mPreviewImageVisibilityValue);
    if (this.mAllowAudioPlaying) {
      this.mPreviewVideo.setVolume(1.0F - this.mPreviewImageVisibilityValue);
    }
  }
  
  private void startPreviewVideoDelayed()
  {
    this.mView.removeCallbacks(this.mStartPreviewVideoRunnable);
    this.mView.postDelayed(this.mStartPreviewVideoRunnable, 3000L);
    this.mPreviewVideoDelayOverlay.setVisibility(0);
  }
  
  private void updateBadges(boolean paramBoolean)
  {
    int i = 1;
    int j;
    if (this.mProgramState == 1)
    {
      j = 0;
      if ((this.mIsLegacy) || (i == 0) || (!needLogo())) {
        break label203;
      }
      loadLogoImage();
      if (Util.isRtl(this.mView.getContext())) {
        this.mLogo.setScaleType(ImageView.ScaleType.FIT_END);
      }
      this.mLogo.setVisibility(0);
      this.mLogoDimmer.setVisibility(0);
      label73:
      if ((this.mIsLegacy) || (i == 0) || (!needLiveBadge())) {
        break label254;
      }
      if (((!paramBoolean) || (Double.compare(this.mFocusedAspectRatio.doubleValue(), 0.6666666666666666D) <= 0)) && ((paramBoolean) || (Double.compare(this.mUnfocusedAspectRatio.doubleValue(), 0.6666666666666666D) <= 0))) {
        break label232;
      }
      this.mLiveBadge.setVisibility(0);
      this.mLiveIcon.setVisibility(8);
    }
    for (;;)
    {
      if ((this.mIsLegacy) || (i == 0) || (j != 0) || (!needDurationBadge())) {
        break label275;
      }
      String str = formatDurationInHoursMinutesAndSeconds(this.mProgramDuration);
      this.mDurationBadge.setText(str);
      this.mDurationBadge.setVisibility(0);
      return;
      i = 0;
      break;
      label203:
      this.mLogo.setImageDrawable(null);
      this.mLogo.setVisibility(8);
      this.mLogoDimmer.setVisibility(8);
      break label73;
      label232:
      j = 1;
      this.mLiveBadge.setVisibility(8);
      this.mLiveIcon.setVisibility(0);
      continue;
      label254:
      this.mLiveBadge.setVisibility(8);
      this.mLiveIcon.setVisibility(8);
    }
    label275:
    this.mDurationBadge.setVisibility(8);
  }
  
  private void updatePreviewImageTreatment(boolean paramBoolean)
  {
    if ((paramBoolean) && (this.mPreviewImageNeedsTreatment))
    {
      this.mPreviewImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
      localMarginLayoutParams = (ViewGroup.MarginLayoutParams)this.mPreviewImage.getLayoutParams();
      localMarginLayoutParams.setMargins(0, this.mPreviewImageExpandedVerticalMargin, 0, this.mPreviewImageExpandedVerticalMargin);
      this.mPreviewImage.setLayoutParams(localMarginLayoutParams);
      setPreviewImageSpecialBackground();
      return;
    }
    this.mPreviewImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)this.mPreviewImage.getLayoutParams();
    localMarginLayoutParams.setMargins(0, 0, 0, 0);
    this.mPreviewImage.setLayoutParams(localMarginLayoutParams);
    setPreviewImageDefaultBackground();
  }
  
  private void updateSize()
  {
    ProgramUtil.updateSize(this.mView, this.mProgramState, this.mUnfocusedAspectRatio.doubleValue(), this.mProgramSettings);
  }
  
  void bind(Program paramProgram, String paramString, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if ((this.mProgramMenu != null) && (this.mProgramMenu.isShowing())) {
      this.mProgramMenu.forceDismiss();
    }
    this.mChannelPackageName = paramString;
    this.mProgramState = paramInt;
    this.mCanAddToWatchNext = paramBoolean1;
    this.mCanRemoveProgram = paramBoolean2;
    this.mIsLegacy = paramBoolean3;
    this.mProgramId = paramProgram.getId();
    this.mContentId = paramProgram.getContentId();
    this.mPreviewVideoUri = paramProgram.getPreviewVideoUri();
    if (this.mPreviewVideo.getVideoUri() != null)
    {
      paramString = this.mPreviewVideo.getVideoUri().toString();
      if ((paramString != null) && (!paramString.equals(this.mPreviewVideoUri))) {
        stopPreviewVideo(false);
      }
      if (this.mPreviewVideoUri == null) {
        break label535;
      }
      this.mPreviewVideo.setVideoUri(Uri.parse(this.mPreviewVideoUri));
      label144:
      this.mWatchedPreviewVideoSeconds = 0;
      this.mThumbnail.setVisibility(8);
      this.mActionUri = paramProgram.getActionUri();
      this.mProgramType = paramProgram.getType();
      this.mProgramDuration = paramProgram.getDuration();
      this.mProgramIsLive = paramProgram.isLive();
      this.mUnfocusedAspectRatio = Double.valueOf(ProgramUtil.getAspectRatio(paramProgram.getPreviewImageAspectRatio()));
      updateSize();
      this.mThumbnailUri = null;
      this.mFocusedAspectRatio = null;
      if (!this.mIsWatchNextProgram)
      {
        if (this.mPreviewVideoUri == null) {
          break label546;
        }
        this.mFocusedAspectRatio = Double.valueOf(1.7777777777777777D);
      }
      label252:
      if (this.mFocusedAspectRatio == null) {
        this.mFocusedAspectRatio = this.mUnfocusedAspectRatio;
      }
      this.mFocusHandler.setFocusedAspectRatio(this.mFocusedAspectRatio.doubleValue());
      this.mFocusHandler.setUnfocusedAspectRatio(this.mUnfocusedAspectRatio.doubleValue());
      this.mFocusHandler.resetFocusedState();
      if ((this.mPreviewVideoUri == null) || (Math.abs(this.mUnfocusedAspectRatio.doubleValue() - this.mFocusedAspectRatio.doubleValue()) <= 0.001D)) {
        break label582;
      }
      paramBoolean1 = true;
      label337:
      this.mPreviewImageNeedsTreatment = paramBoolean1;
      this.mPreviewImage.setContentDescription(paramProgram.getTitle());
      updatePreviewImageTreatment(this.mView.isFocused());
      loadPreviewImage(paramProgram.getPreviewImageUri());
      if (this.mThumbnailUri == null) {
        break label588;
      }
      loadThumbnailImage();
      if (this.mView.isFocused()) {
        this.mThumbnail.setVisibility(0);
      }
      label406:
      this.mLogoUri = paramProgram.getLogoUri();
      updateBadges(this.mView.isFocused());
      if ((!this.mIsWatchNextProgram) || (paramProgram.getDuration() <= 0L) || (paramProgram.getPlaybackPosition() <= 0L) || (paramProgram.getWatchNextType() != 0)) {
        break label604;
      }
      this.mPlaybackProgress.setMin(0);
      this.mPlaybackProgress.setMax((int)paramProgram.getDuration());
      this.mPlaybackProgress.setProgress((int)paramProgram.getPlaybackPosition());
      this.mPlaybackProgress.setVisibility(0);
      paramProgram = this.mPlaybackProgressDimmer;
      if (!needLogo()) {
        break label599;
      }
    }
    label535:
    label546:
    label582:
    label588:
    label599:
    for (paramInt = 8;; paramInt = 0)
    {
      paramProgram.setVisibility(paramInt);
      return;
      paramString = null;
      break;
      this.mPreviewVideo.setVideoUri(null);
      break label144;
      this.mThumbnailUri = paramProgram.getThumbnailUri();
      if (this.mThumbnailUri == null) {
        break label252;
      }
      this.mFocusedAspectRatio = Double.valueOf(ProgramUtil.getAspectRatio(paramProgram.getThumbnailAspectRatio()));
      break label252;
      paramBoolean1 = false;
      break label337;
      this.mThumbnail.setImageDrawable(null);
      break label406;
    }
    label604:
    this.mPlaybackProgress.setVisibility(8);
    this.mPlaybackProgressDimmer.setVisibility(8);
  }
  
  void bindState(int paramInt)
  {
    this.mProgramState = paramInt;
    updateBadges(this.mView.isFocused());
    updateSize();
  }
  
  Palette getPreviewImagePalette()
  {
    return this.mPreviewImagePalette;
  }
  
  boolean isViewFocused()
  {
    return this.mView.isFocused();
  }
  
  public void onClick(View paramView)
  {
    if (Util.isAccessibilityEnabled(paramView.getContext()))
    {
      onLongClick(paramView);
      return;
    }
    onPrimaryAction();
  }
  
  public void onItemClick(ContextMenuItem paramContextMenuItem)
  {
    switch (paramContextMenuItem.getId())
    {
    default: 
      return;
    case 1: 
      onPrimaryAction();
      return;
    case 2: 
      TvDataManager.addProgramToWatchlist(this.mView.getContext(), this.mProgramId, this.mChannelPackageName);
      TvDataManager.getInstance(this.mView.getContext()).addProgramToWatchNextCache(this.mContentId, this.mChannelPackageName);
      paramContextMenuItem = new UserActionEvent("add_program_to_watch_next");
      if (this.mChannelPackageName != null) {
        paramContextMenuItem.putParameter("package_name", this.mChannelPackageName);
      }
      this.mEventLogger.log(paramContextMenuItem);
      return;
    case 3: 
      TvDataManager.removePreviewProgram(this.mView.getContext(), this.mProgramId, this.mChannelPackageName);
      paramContextMenuItem = new UserActionEvent("remove_program_from_channel").putParameter("program_type", this.mProgramType);
      if (this.mChannelPackageName != null) {
        paramContextMenuItem.putParameter("package_name", this.mChannelPackageName);
      }
      this.mEventLogger.log(paramContextMenuItem);
      return;
    }
    TvDataManager.removeProgramFromWatchlist(this.mView.getContext(), this.mProgramId, this.mEventLogger);
    TvDataManager.getInstance(this.mView.getContext()).removeProgramFromWatchNextCache(this.mContentId, this.mChannelPackageName);
  }
  
  public boolean onLongClick(View paramView)
  {
    this.mProgramMenu = new ContextMenu((Activity)paramView.getContext(), paramView, paramView.getResources().getDimensionPixelSize(2131558510));
    Object localObject;
    if (Util.isAccessibilityEnabled(paramView.getContext()))
    {
      localObject = new ContextMenuItem(1, paramView.getContext().getString(2131492901), null);
      this.mProgramMenu.addItem((ContextMenuItem)localObject);
    }
    ContextMenuItem localContextMenuItem;
    boolean bool1;
    if (!this.mIsWatchNextProgram)
    {
      boolean bool2 = TvDataManager.getInstance(paramView.getContext()).isInWatchNext(this.mContentId, this.mChannelPackageName);
      localContextMenuItem = new ContextMenuItem(2, null, paramView.getContext().getDrawable(2130837640));
      this.mProgramMenu.addItem(localContextMenuItem);
      if ((this.mCanAddToWatchNext) && (!bool2))
      {
        bool1 = true;
        localContextMenuItem.setEnabled(bool1);
        if (!bool2) {
          break label236;
        }
        localContextMenuItem.setTitle(this.mProgramMenuAlreadyInWatchNextText);
        localObject = new ContextMenuItem(3, null, paramView.getContext().getDrawable(2130837647));
        this.mProgramMenu.addItem((ContextMenuItem)localObject);
        ((ContextMenuItem)localObject).setEnabled(this.mCanRemoveProgram);
        if (!this.mCanRemoveProgram) {
          break label268;
        }
        paramView = this.mProgramMenuRemoveText;
        label208:
        ((ContextMenuItem)localObject).setTitle(paramView);
      }
    }
    for (;;)
    {
      this.mProgramMenu.setOnMenuItemClickListener(this);
      this.mProgramMenu.show();
      return true;
      bool1 = false;
      break;
      label236:
      if (this.mCanAddToWatchNext) {}
      for (localObject = this.mProgramMenuAddToWatchNextText;; localObject = this.mProgramMenuAddToWatchNextNotAvailableText)
      {
        localContextMenuItem.setTitle((String)localObject);
        break;
      }
      label268:
      paramView = this.mProgramMenuRemoveNotAvailableText;
      break label208;
      this.mProgramMenu.addItem(new ContextMenuItem(4, paramView.getContext().getString(2131493062), paramView.getContext().getDrawable(2130837647)));
    }
  }
  
  void setIsWatchNextProgram(boolean paramBoolean)
  {
    this.mIsWatchNextProgram = paramBoolean;
  }
  
  void setOnProgramViewFocusedListener(OnProgramViewFocusedListener paramOnProgramViewFocusedListener)
  {
    this.mOnProgramViewFocusedListener = paramOnProgramViewFocusedListener;
  }
  
  void stopPreviewVideo(boolean paramBoolean)
  {
    logStopVideo();
    this.mView.removeCallbacks(this.mStartPreviewVideoRunnable);
    this.mPreviewVideoDelayOverlay.setVisibility(8);
    if (this.mPreviewImageVisibilityValue == 1.0F)
    {
      finishStoppingPreviewVideo();
      return;
    }
    if (paramBoolean)
    {
      fadePreviewImageIn();
      return;
    }
    this.mPreviewImageContainer.setVisibility(0);
    setPreviewImageVisibilityValue(1.0F);
    finishStoppingPreviewVideo();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/home/ProgramController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */