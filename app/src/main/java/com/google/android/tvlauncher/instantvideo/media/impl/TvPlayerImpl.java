package com.google.android.tvlauncher.instantvideo.media.impl;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.media.tv.TvView;
import android.media.tv.TvView.TimeShiftPositionCallback;
import android.media.tv.TvView.TvInputCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import com.google.android.tvlauncher.instantvideo.media.MediaPlayer;
import com.google.android.tvlauncher.instantvideo.media.MediaPlayer.VideoCallback;
import java.util.List;

public class TvPlayerImpl
  implements MediaPlayer
{
  private static final boolean DEBUG = false;
  private static final String PARAM_INPUT = "input";
  private static final String PATH_PREVIEW_PROGRAM = "preview_program";
  private static final String PATH_RECORDED_PROGRAM = "recorded_program";
  private static final String TAG = "TvPlayerImpl";
  private static final int TYPE_LIVE_CONTENT = 1;
  private static final int TYPE_PREVIEW_PROGRAM = 3;
  private static final int TYPE_RECORDED_PROGRAM = 2;
  private final Context mContext;
  private long mCurrentPosition = 0L;
  private boolean mStarted = false;
  private int mState = 1;
  private String mTvInputId;
  private final TvView mTvView;
  private MediaPlayer.VideoCallback mVideoCallback;
  private int mVideoType;
  private Uri mVideoUri;
  private float mVolume = 1.0F;
  private boolean mVolumeUpdated = false;
  
  public TvPlayerImpl(Context paramContext, TvView paramTvView)
  {
    this.mContext = paramContext;
    this.mTvView = paramTvView;
  }
  
  public static boolean isPreviewProgramUri(Uri paramUri)
  {
    return (isTvUri(paramUri)) && (isTwoSegmentUriStartingWith(paramUri, "preview_program"));
  }
  
  public static boolean isRecordedProgramUri(Uri paramUri)
  {
    return (isTvUri(paramUri)) && (isTwoSegmentUriStartingWith(paramUri, "recorded_program"));
  }
  
  private static boolean isTvUri(Uri paramUri)
  {
    return (paramUri != null) && ("content".equals(paramUri.getScheme())) && ("android.media.tv".equals(paramUri.getAuthority()));
  }
  
  private static boolean isTwoSegmentUriStartingWith(Uri paramUri, String paramString)
  {
    paramUri = paramUri.getPathSegments();
    if (paramUri == null) {}
    while ((paramUri.size() != 2) || (!paramString.equals(paramUri.get(0)))) {
      return false;
    }
    return true;
  }
  
  private void prepareVideo()
  {
    if (this.mVideoType == 1) {
      this.mTvView.tune(this.mTvInputId, this.mVideoUri);
    }
    do
    {
      return;
      if (this.mVideoType == 2)
      {
        this.mTvView.timeShiftPlay(this.mTvInputId, this.mVideoUri);
        return;
      }
    } while (this.mVideoType != 3);
    this.mTvView.tune(this.mTvInputId, this.mVideoUri);
  }
  
  public int getCurrentPosition()
  {
    return (int)this.mCurrentPosition;
  }
  
  public int getPlaybackState()
  {
    return this.mState;
  }
  
  public View getPlayerView()
  {
    return this.mTvView;
  }
  
  public Uri getVideoUri()
  {
    return this.mVideoUri;
  }
  
  public void prepare()
  {
    this.mStarted = true;
    this.mVolumeUpdated = false;
    this.mState = 2;
    this.mTvView.setCallback(new TvView.TvInputCallback()
    {
      public void onConnectionFailed(String paramAnonymousString)
      {
        TvPlayerImpl.this.stop();
        if (TvPlayerImpl.this.mVideoCallback != null) {
          TvPlayerImpl.this.mVideoCallback.onVideoError();
        }
      }
      
      public void onDisconnected(String paramAnonymousString)
      {
        TvPlayerImpl.this.stop();
        if (TvPlayerImpl.this.mVideoCallback != null) {
          TvPlayerImpl.this.mVideoCallback.onVideoError();
        }
      }
      
      public void onVideoAvailable(String paramAnonymousString)
      {
        if (TvPlayerImpl.this.mVideoCallback != null)
        {
          TvPlayerImpl.access$102(TvPlayerImpl.this, 3);
          TvPlayerImpl.this.mVideoCallback.onVideoAvailable();
        }
      }
      
      public void onVideoUnavailable(String paramAnonymousString, int paramAnonymousInt)
      {
        if (paramAnonymousInt == 0)
        {
          TvPlayerImpl.this.stop();
          if (TvPlayerImpl.this.mVideoCallback != null) {
            TvPlayerImpl.this.mVideoCallback.onVideoError();
          }
        }
      }
    });
    if (this.mVideoType == 2) {
      this.mTvView.setTimeShiftPositionCallback(new TvView.TimeShiftPositionCallback()
      {
        public void onTimeShiftCurrentPositionChanged(String paramAnonymousString, long paramAnonymousLong)
        {
          super.onTimeShiftCurrentPositionChanged(paramAnonymousString, paramAnonymousLong);
          TvPlayerImpl.access$202(TvPlayerImpl.this, paramAnonymousLong);
        }
      });
    }
    if (this.mTvInputId != null)
    {
      prepareVideo();
      this.mTvView.setStreamVolume(0.0F);
    }
    do
    {
      return;
      if (this.mVideoType != 3)
      {
        new AsyncTask()
        {
          private String[] getProjection()
          {
            if (TvPlayerImpl.this.mVideoType == 1) {
              return new String[] { "input_id" };
            }
            return new String[] { "input_id" };
          }
          
          protected String doInBackground(Void... paramAnonymousVarArgs)
          {
            Cursor localCursor = TvPlayerImpl.this.mContext.getContentResolver().query(TvPlayerImpl.this.mVideoUri, getProjection(), null, null, null);
            Object localObject = null;
            paramAnonymousVarArgs = (Void[])localObject;
            if (localCursor != null)
            {
              paramAnonymousVarArgs = (Void[])localObject;
              if (localCursor.moveToNext()) {
                paramAnonymousVarArgs = localCursor.getString(0);
              }
            }
            if (localCursor != null) {
              localCursor.close();
            }
            return paramAnonymousVarArgs;
          }
          
          protected void onPostExecute(String paramAnonymousString)
          {
            super.onPostExecute(paramAnonymousString);
            if ((paramAnonymousString != null) && (TvPlayerImpl.this.mStarted))
            {
              TvPlayerImpl.access$602(TvPlayerImpl.this, paramAnonymousString);
              TvPlayerImpl.this.prepareVideo();
              if (!TvPlayerImpl.this.mVolumeUpdated) {
                TvPlayerImpl.this.mTvView.setStreamVolume(0.0F);
              }
            }
          }
        }.execute(new Void[0]);
        return;
      }
      Log.e("TvPlayerImpl", "TV input id must be given via URI query parameter");
      stop();
    } while (this.mVideoCallback == null);
    this.mVideoCallback.onVideoError();
  }
  
  public void seekTo(int paramInt)
  {
    if (this.mVideoType == 2) {
      this.mTvView.timeShiftSeekTo(paramInt);
    }
  }
  
  public void setDisplaySize(int paramInt1, int paramInt2) {}
  
  public void setPlayWhenReady(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mVolumeUpdated = true;
      this.mTvView.setStreamVolume(this.mVolume);
    }
  }
  
  public void setVideoCallback(MediaPlayer.VideoCallback paramVideoCallback)
  {
    this.mVideoCallback = paramVideoCallback;
  }
  
  public void setVideoUri(Uri paramUri)
  {
    this.mVideoUri = paramUri;
    this.mTvInputId = this.mVideoUri.getQueryParameter("input");
    if (TvContract.isChannelUri(paramUri)) {
      this.mVideoType = 1;
    }
    do
    {
      return;
      if (isRecordedProgramUri(paramUri))
      {
        this.mVideoType = 2;
        return;
      }
    } while (!isPreviewProgramUri(paramUri));
    this.mVideoType = 3;
  }
  
  public void setVolume(float paramFloat)
  {
    this.mVolume = paramFloat;
    if (this.mVolumeUpdated) {
      this.mTvView.setStreamVolume(paramFloat);
    }
  }
  
  public void stop()
  {
    this.mStarted = false;
    this.mState = 1;
    this.mTvView.reset();
    this.mTvView.setCallback(null);
    this.mTvView.setTimeShiftPositionCallback(null);
    this.mCurrentPosition = 0L;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/instantvideo/media/impl/TvPlayerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */