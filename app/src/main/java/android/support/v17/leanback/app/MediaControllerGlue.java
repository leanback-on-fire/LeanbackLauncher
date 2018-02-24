package android.support.v17.leanback.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaControllerCompat.Callback;
import android.support.v4.media.session.MediaControllerCompat.TransportControls;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

@Deprecated
public abstract class MediaControllerGlue
  extends PlaybackControlGlue
{
  static final boolean DEBUG = false;
  static final String TAG = "MediaControllerGlue";
  private final MediaControllerCompat.Callback mCallback = new MediaControllerCompat.Callback()
  {
    public void onMetadataChanged(MediaMetadataCompat paramAnonymousMediaMetadataCompat)
    {
      MediaControllerGlue.this.onMetadataChanged();
    }
    
    public void onPlaybackStateChanged(PlaybackStateCompat paramAnonymousPlaybackStateCompat)
    {
      MediaControllerGlue.this.onStateChanged();
    }
    
    public void onSessionDestroyed()
    {
      MediaControllerGlue.this.mMediaController = null;
    }
    
    public void onSessionEvent(String paramAnonymousString, Bundle paramAnonymousBundle) {}
  };
  MediaControllerCompat mMediaController;
  
  public MediaControllerGlue(Context paramContext, PlaybackOverlayFragment paramPlaybackOverlayFragment, int[] paramArrayOfInt)
  {
    super(paramContext, paramPlaybackOverlayFragment, paramArrayOfInt);
  }
  
  public MediaControllerGlue(Context paramContext, PlaybackOverlayFragment paramPlaybackOverlayFragment, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    super(paramContext, paramPlaybackOverlayFragment, paramArrayOfInt1, paramArrayOfInt2);
  }
  
  public void attachToMediaController(MediaControllerCompat paramMediaControllerCompat)
  {
    if (paramMediaControllerCompat != this.mMediaController)
    {
      detach();
      this.mMediaController = paramMediaControllerCompat;
      if (this.mMediaController != null) {
        this.mMediaController.registerCallback(this.mCallback);
      }
      onMetadataChanged();
      onStateChanged();
    }
  }
  
  public void detach()
  {
    if (this.mMediaController != null) {
      this.mMediaController.unregisterCallback(this.mCallback);
    }
    this.mMediaController = null;
  }
  
  public int getCurrentPosition()
  {
    return (int)this.mMediaController.getPlaybackState().getPosition();
  }
  
  public int getCurrentSpeedId()
  {
    int i = 1;
    int j = (int)this.mMediaController.getPlaybackState().getPlaybackSpeed();
    if (j == 0) {
      i = 0;
    }
    while (j == 1) {
      return i;
    }
    if (j > 0)
    {
      arrayOfInt = getFastForwardSpeeds();
      i = 0;
      while (i < arrayOfInt.length)
      {
        if (j == arrayOfInt[i]) {
          return i + 10;
        }
        i += 1;
      }
    }
    int[] arrayOfInt = getRewindSpeeds();
    i = 0;
    while (i < arrayOfInt.length)
    {
      if (-j == arrayOfInt[i]) {
        return -10 - i;
      }
      i += 1;
    }
    Log.w("MediaControllerGlue", "Couldn't find index for speed " + j);
    return -1;
  }
  
  public Drawable getMediaArt()
  {
    Bitmap localBitmap = this.mMediaController.getMetadata().getDescription().getIconBitmap();
    if (localBitmap == null) {
      return null;
    }
    return new BitmapDrawable(getContext().getResources(), localBitmap);
  }
  
  public final MediaControllerCompat getMediaController()
  {
    return this.mMediaController;
  }
  
  public int getMediaDuration()
  {
    return (int)this.mMediaController.getMetadata().getLong("android.media.metadata.DURATION");
  }
  
  public CharSequence getMediaSubtitle()
  {
    return this.mMediaController.getMetadata().getDescription().getSubtitle();
  }
  
  public CharSequence getMediaTitle()
  {
    return this.mMediaController.getMetadata().getDescription().getTitle();
  }
  
  public long getSupportedActions()
  {
    long l2 = 0L;
    long l3 = this.mMediaController.getPlaybackState().getActions();
    if ((0x200 & l3) != 0L) {
      l2 = 0L | 0x40;
    }
    long l1 = l2;
    if ((l3 & 0x20) != 0L) {
      l1 = l2 | 0x100;
    }
    l2 = l1;
    if ((l3 & 0x10) != 0L) {
      l2 = l1 | 0x10;
    }
    l1 = l2;
    if ((l3 & 0x40) != 0L) {
      l1 = l2 | 0x80;
    }
    l2 = l1;
    if ((0x8 & l3) != 0L) {
      l2 = l1 | 0x20;
    }
    return l2;
  }
  
  public boolean hasValidMedia()
  {
    return (this.mMediaController != null) && (this.mMediaController.getMetadata() != null);
  }
  
  public boolean isMediaPlaying()
  {
    return this.mMediaController.getPlaybackState().getState() == 3;
  }
  
  protected void pausePlayback()
  {
    this.mMediaController.getTransportControls().pause();
  }
  
  protected void skipToNext()
  {
    this.mMediaController.getTransportControls().skipToNext();
  }
  
  protected void skipToPrevious()
  {
    this.mMediaController.getTransportControls().skipToPrevious();
  }
  
  protected void startPlayback(int paramInt)
  {
    if (paramInt == 1)
    {
      this.mMediaController.getTransportControls().play();
      return;
    }
    if (paramInt > 0)
    {
      this.mMediaController.getTransportControls().fastForward();
      return;
    }
    this.mMediaController.getTransportControls().rewind();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/MediaControllerGlue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */