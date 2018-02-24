package android.support.v17.leanback.app;

import android.animation.PropertyValuesHolder;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.R.dimen;
import android.support.v17.leanback.graphics.FitWidthBitmapDrawable;
import android.support.v17.leanback.media.PlaybackGlue;
import android.support.v17.leanback.media.PlaybackGlueHost;
import android.support.v17.leanback.widget.DetailsParallaxDrawable;
import android.support.v17.leanback.widget.ParallaxTarget.PropertyValuesHolderTarget;

public class DetailsFragmentBackgroundController
{
  boolean mCanUseHost = false;
  Bitmap mCoverBitmap;
  final DetailsFragment mFragment;
  boolean mInitialControlVisible = false;
  private Fragment mLastVideoFragmentForGlueHost;
  DetailsParallaxDrawable mParallaxDrawable;
  int mParallaxDrawableMaxOffset;
  PlaybackGlue mPlaybackGlue;
  int mSolidColor;
  DetailsBackgroundVideoHelper mVideoHelper;
  
  public DetailsFragmentBackgroundController(DetailsFragment paramDetailsFragment)
  {
    if (paramDetailsFragment.mDetailsBackgroundController != null) {
      throw new IllegalStateException("Each DetailsFragment is allowed to initialize DetailsFragmentBackgroundController once");
    }
    paramDetailsFragment.mDetailsBackgroundController = this;
    this.mFragment = paramDetailsFragment;
  }
  
  public boolean canNavigateToVideoFragment()
  {
    return this.mPlaybackGlue != null;
  }
  
  PlaybackGlueHost createGlueHost()
  {
    PlaybackGlueHost localPlaybackGlueHost = onCreateGlueHost();
    if (this.mInitialControlVisible)
    {
      localPlaybackGlueHost.showControlsOverlay(false);
      return localPlaybackGlueHost;
    }
    localPlaybackGlueHost.hideControlsOverlay(false);
    return localPlaybackGlueHost;
  }
  
  boolean disableVideoParallax()
  {
    if (this.mVideoHelper != null)
    {
      this.mVideoHelper.stopParallax();
      return this.mVideoHelper.isVideoVisible();
    }
    return false;
  }
  
  public void enableParallax()
  {
    int j = this.mParallaxDrawableMaxOffset;
    int i = j;
    if (j == 0) {
      i = FragmentUtil.getContext(this.mFragment).getResources().getDimensionPixelSize(R.dimen.lb_details_cover_drawable_parallax_movement);
    }
    FitWidthBitmapDrawable localFitWidthBitmapDrawable = new FitWidthBitmapDrawable();
    enableParallax(localFitWidthBitmapDrawable, new ColorDrawable(), new ParallaxTarget.PropertyValuesHolderTarget(localFitWidthBitmapDrawable, PropertyValuesHolder.ofInt(FitWidthBitmapDrawable.PROPERTY_VERTICAL_OFFSET, new int[] { 0, -i })));
  }
  
  public void enableParallax(@NonNull Drawable paramDrawable1, @NonNull Drawable paramDrawable2, @Nullable ParallaxTarget.PropertyValuesHolderTarget paramPropertyValuesHolderTarget)
  {
    if (this.mParallaxDrawable != null) {
      return;
    }
    if ((this.mCoverBitmap != null) && ((paramDrawable1 instanceof FitWidthBitmapDrawable))) {
      ((FitWidthBitmapDrawable)paramDrawable1).setBitmap(this.mCoverBitmap);
    }
    if ((this.mSolidColor != 0) && ((paramDrawable2 instanceof ColorDrawable))) {
      ((ColorDrawable)paramDrawable2).setColor(this.mSolidColor);
    }
    if (this.mPlaybackGlue != null) {
      throw new IllegalStateException("enableParallaxDrawable must be called before enableVideoPlayback");
    }
    this.mParallaxDrawable = new DetailsParallaxDrawable(FragmentUtil.getContext(this.mFragment), this.mFragment.getParallax(), paramDrawable1, paramDrawable2, paramPropertyValuesHolderTarget);
    this.mFragment.setBackgroundDrawable(this.mParallaxDrawable);
    this.mVideoHelper = new DetailsBackgroundVideoHelper(null, this.mFragment.getParallax(), this.mParallaxDrawable.getCoverDrawable());
  }
  
  public final Fragment findOrCreateVideoFragment()
  {
    return this.mFragment.findOrCreateVideoFragment();
  }
  
  public final Drawable getBottomDrawable()
  {
    if (this.mParallaxDrawable == null) {
      return null;
    }
    return this.mParallaxDrawable.getBottomDrawable();
  }
  
  public final Bitmap getCoverBitmap()
  {
    return this.mCoverBitmap;
  }
  
  public final Drawable getCoverDrawable()
  {
    if (this.mParallaxDrawable == null) {
      return null;
    }
    return this.mParallaxDrawable.getCoverDrawable();
  }
  
  public final int getParallaxDrawableMaxOffset()
  {
    return this.mParallaxDrawableMaxOffset;
  }
  
  public final PlaybackGlue getPlaybackGlue()
  {
    return this.mPlaybackGlue;
  }
  
  @ColorInt
  public final int getSolidColor()
  {
    return this.mSolidColor;
  }
  
  public PlaybackGlueHost onCreateGlueHost()
  {
    return new VideoFragmentGlueHost((VideoFragment)findOrCreateVideoFragment());
  }
  
  public Fragment onCreateVideoFragment()
  {
    return new VideoFragment();
  }
  
  void onStart()
  {
    if (!this.mCanUseHost)
    {
      this.mCanUseHost = true;
      if (this.mPlaybackGlue != null)
      {
        this.mPlaybackGlue.setHost(createGlueHost());
        this.mLastVideoFragmentForGlueHost = findOrCreateVideoFragment();
      }
    }
    if ((this.mPlaybackGlue != null) && (this.mPlaybackGlue.isPrepared())) {
      this.mPlaybackGlue.play();
    }
  }
  
  void onStop()
  {
    if (this.mPlaybackGlue != null) {
      this.mPlaybackGlue.pause();
    }
  }
  
  public final void setCoverBitmap(Bitmap paramBitmap)
  {
    this.mCoverBitmap = paramBitmap;
    paramBitmap = getCoverDrawable();
    if ((paramBitmap instanceof FitWidthBitmapDrawable)) {
      ((FitWidthBitmapDrawable)paramBitmap).setBitmap(this.mCoverBitmap);
    }
  }
  
  public final void setParallaxDrawableMaxOffset(int paramInt)
  {
    if (this.mParallaxDrawable != null) {
      throw new IllegalStateException("enableParallax already called");
    }
    this.mParallaxDrawableMaxOffset = paramInt;
  }
  
  public final void setSolidColor(@ColorInt int paramInt)
  {
    this.mSolidColor = paramInt;
    Drawable localDrawable = getBottomDrawable();
    if ((localDrawable instanceof ColorDrawable)) {
      ((ColorDrawable)localDrawable).setColor(paramInt);
    }
  }
  
  public void setupVideoPlayback(@NonNull PlaybackGlue paramPlaybackGlue)
  {
    if (this.mPlaybackGlue == paramPlaybackGlue) {}
    PlaybackGlueHost localPlaybackGlueHost;
    do
    {
      return;
      localPlaybackGlueHost = null;
      if (this.mPlaybackGlue != null)
      {
        localPlaybackGlueHost = this.mPlaybackGlue.getHost();
        this.mPlaybackGlue.setHost(null);
      }
      this.mPlaybackGlue = paramPlaybackGlue;
      this.mVideoHelper.setPlaybackGlue(this.mPlaybackGlue);
    } while ((!this.mCanUseHost) || (this.mPlaybackGlue == null));
    if ((localPlaybackGlueHost == null) || (this.mLastVideoFragmentForGlueHost != findOrCreateVideoFragment()))
    {
      this.mPlaybackGlue.setHost(createGlueHost());
      this.mLastVideoFragmentForGlueHost = findOrCreateVideoFragment();
      return;
    }
    this.mPlaybackGlue.setHost(localPlaybackGlueHost);
  }
  
  public final void switchToRows()
  {
    this.mFragment.switchToRows();
  }
  
  public final void switchToVideo()
  {
    this.mFragment.switchToVideo();
  }
  
  void switchToVideoBeforeCreate()
  {
    this.mVideoHelper.crossFadeBackgroundToVideo(true, true);
    this.mInitialControlVisible = true;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/DetailsFragmentBackgroundController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */