package android.support.v17.leanback.app;

import android.os.Bundle;
import android.support.v17.leanback.R.layout;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class VideoFragment
  extends PlaybackFragment
{
  static final int SURFACE_CREATED = 1;
  static final int SURFACE_NOT_CREATED = 0;
  SurfaceHolder.Callback mMediaPlaybackCallback;
  int mState = 0;
  SurfaceView mVideoSurface;
  
  public SurfaceView getSurfaceView()
  {
    return this.mVideoSurface;
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = (ViewGroup)super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
    this.mVideoSurface = ((SurfaceView)LayoutInflater.from(FragmentUtil.getContext(this)).inflate(R.layout.lb_video_surface, paramLayoutInflater, false));
    paramLayoutInflater.addView(this.mVideoSurface, 0);
    this.mVideoSurface.getHolder().addCallback(new SurfaceHolder.Callback()
    {
      public void surfaceChanged(SurfaceHolder paramAnonymousSurfaceHolder, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
      {
        if (VideoFragment.this.mMediaPlaybackCallback != null) {
          VideoFragment.this.mMediaPlaybackCallback.surfaceChanged(paramAnonymousSurfaceHolder, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3);
        }
      }
      
      public void surfaceCreated(SurfaceHolder paramAnonymousSurfaceHolder)
      {
        if (VideoFragment.this.mMediaPlaybackCallback != null) {
          VideoFragment.this.mMediaPlaybackCallback.surfaceCreated(paramAnonymousSurfaceHolder);
        }
        VideoFragment.this.mState = 1;
      }
      
      public void surfaceDestroyed(SurfaceHolder paramAnonymousSurfaceHolder)
      {
        if (VideoFragment.this.mMediaPlaybackCallback != null) {
          VideoFragment.this.mMediaPlaybackCallback.surfaceDestroyed(paramAnonymousSurfaceHolder);
        }
        VideoFragment.this.mState = 0;
      }
    });
    setBackgroundType(2);
    return paramLayoutInflater;
  }
  
  public void onDestroyView()
  {
    this.mVideoSurface = null;
    this.mState = 0;
    super.onDestroyView();
  }
  
  protected void onVideoSizeChanged(int paramInt1, int paramInt2)
  {
    int i = getView().getWidth();
    int j = getView().getHeight();
    ViewGroup.LayoutParams localLayoutParams = this.mVideoSurface.getLayoutParams();
    if (i * paramInt2 > paramInt1 * j)
    {
      localLayoutParams.height = j;
      localLayoutParams.width = (j * paramInt1 / paramInt2);
    }
    for (;;)
    {
      this.mVideoSurface.setLayoutParams(localLayoutParams);
      return;
      localLayoutParams.width = i;
      localLayoutParams.height = (i * paramInt2 / paramInt1);
    }
  }
  
  public void setSurfaceHolderCallback(SurfaceHolder.Callback paramCallback)
  {
    this.mMediaPlaybackCallback = paramCallback;
    if ((paramCallback != null) && (this.mState == 1)) {
      this.mMediaPlaybackCallback.surfaceCreated(this.mVideoSurface.getHolder());
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/VideoFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */