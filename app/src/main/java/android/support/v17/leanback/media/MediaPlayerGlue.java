package android.support.v17.leanback.media;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Handler;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRow.FastForwardAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.RepeatAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.RewindAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.ThumbsDownAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.ThumbsUpAction;
import android.support.v17.leanback.widget.Presenter.ViewHolder;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter.ViewHolder;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import java.util.Iterator;
import java.util.List;

public class MediaPlayerGlue
  extends PlaybackControlGlue
  implements OnItemViewSelectedListener
{
  public static final int FAST_FORWARD_REWIND_REPEAT_DELAY = 200;
  public static final int FAST_FORWARD_REWIND_STEP = 10000;
  public static final int NO_REPEAT = 0;
  public static final int REPEAT_ALL = 2;
  public static final int REPEAT_ONE = 1;
  private static final String TAG = "MediaPlayerGlue";
  private String mArtist;
  private Drawable mCover;
  private Handler mHandler = new Handler();
  private boolean mInitialized = false;
  private long mLastKeyDownEvent = 0L;
  private String mMediaSourcePath = null;
  private Uri mMediaSourceUri = null;
  private MediaPlayer.OnCompletionListener mOnCompletionListener;
  MediaPlayer mPlayer = new MediaPlayer();
  private final PlaybackControlsRow.RepeatAction mRepeatAction = new PlaybackControlsRow.RepeatAction(getContext());
  private Runnable mRunnable;
  private Action mSelectedAction;
  protected final PlaybackControlsRow.ThumbsDownAction mThumbsDownAction = new PlaybackControlsRow.ThumbsDownAction(getContext());
  protected final PlaybackControlsRow.ThumbsUpAction mThumbsUpAction = new PlaybackControlsRow.ThumbsUpAction(getContext());
  private String mTitle;
  
  public MediaPlayerGlue(Context paramContext)
  {
    this(paramContext, new int[] { 1 }, new int[] { 1 });
  }
  
  public MediaPlayerGlue(Context paramContext, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    super(paramContext, paramArrayOfInt1, paramArrayOfInt2);
    this.mThumbsDownAction.setIndex(1);
    this.mThumbsUpAction.setIndex(1);
  }
  
  /* Error */
  private void prepareMediaForPlaying()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 123	android/support/v17/leanback/media/MediaPlayerGlue:reset	()V
    //   4: aload_0
    //   5: getfield 86	android/support/v17/leanback/media/MediaPlayerGlue:mMediaSourceUri	Landroid/net/Uri;
    //   8: ifnull +86 -> 94
    //   11: aload_0
    //   12: getfield 75	android/support/v17/leanback/media/MediaPlayerGlue:mPlayer	Landroid/media/MediaPlayer;
    //   15: aload_0
    //   16: invokevirtual 94	android/support/v17/leanback/media/MediaPlayerGlue:getContext	()Landroid/content/Context;
    //   19: aload_0
    //   20: getfield 86	android/support/v17/leanback/media/MediaPlayerGlue:mMediaSourceUri	Landroid/net/Uri;
    //   23: invokevirtual 127	android/media/MediaPlayer:setDataSource	(Landroid/content/Context;Landroid/net/Uri;)V
    //   26: aload_0
    //   27: getfield 75	android/support/v17/leanback/media/MediaPlayerGlue:mPlayer	Landroid/media/MediaPlayer;
    //   30: iconst_3
    //   31: invokevirtual 130	android/media/MediaPlayer:setAudioStreamType	(I)V
    //   34: aload_0
    //   35: getfield 75	android/support/v17/leanback/media/MediaPlayerGlue:mPlayer	Landroid/media/MediaPlayer;
    //   38: new 14	android/support/v17/leanback/media/MediaPlayerGlue$4
    //   41: dup
    //   42: aload_0
    //   43: invokespecial 133	android/support/v17/leanback/media/MediaPlayerGlue$4:<init>	(Landroid/support/v17/leanback/media/MediaPlayerGlue;)V
    //   46: invokevirtual 137	android/media/MediaPlayer:setOnPreparedListener	(Landroid/media/MediaPlayer$OnPreparedListener;)V
    //   49: aload_0
    //   50: getfield 139	android/support/v17/leanback/media/MediaPlayerGlue:mOnCompletionListener	Landroid/media/MediaPlayer$OnCompletionListener;
    //   53: ifnull +14 -> 67
    //   56: aload_0
    //   57: getfield 75	android/support/v17/leanback/media/MediaPlayerGlue:mPlayer	Landroid/media/MediaPlayer;
    //   60: aload_0
    //   61: getfield 139	android/support/v17/leanback/media/MediaPlayerGlue:mOnCompletionListener	Landroid/media/MediaPlayer$OnCompletionListener;
    //   64: invokevirtual 143	android/media/MediaPlayer:setOnCompletionListener	(Landroid/media/MediaPlayer$OnCompletionListener;)V
    //   67: aload_0
    //   68: getfield 75	android/support/v17/leanback/media/MediaPlayerGlue:mPlayer	Landroid/media/MediaPlayer;
    //   71: new 16	android/support/v17/leanback/media/MediaPlayerGlue$5
    //   74: dup
    //   75: aload_0
    //   76: invokespecial 144	android/support/v17/leanback/media/MediaPlayerGlue$5:<init>	(Landroid/support/v17/leanback/media/MediaPlayerGlue;)V
    //   79: invokevirtual 148	android/media/MediaPlayer:setOnBufferingUpdateListener	(Landroid/media/MediaPlayer$OnBufferingUpdateListener;)V
    //   82: aload_0
    //   83: getfield 75	android/support/v17/leanback/media/MediaPlayerGlue:mPlayer	Landroid/media/MediaPlayer;
    //   86: invokevirtual 151	android/media/MediaPlayer:prepareAsync	()V
    //   89: aload_0
    //   90: invokevirtual 154	android/support/v17/leanback/media/MediaPlayerGlue:onStateChanged	()V
    //   93: return
    //   94: aload_0
    //   95: getfield 88	android/support/v17/leanback/media/MediaPlayerGlue:mMediaSourcePath	Ljava/lang/String;
    //   98: ifnull -5 -> 93
    //   101: aload_0
    //   102: getfield 75	android/support/v17/leanback/media/MediaPlayerGlue:mPlayer	Landroid/media/MediaPlayer;
    //   105: aload_0
    //   106: getfield 88	android/support/v17/leanback/media/MediaPlayerGlue:mMediaSourcePath	Ljava/lang/String;
    //   109: invokevirtual 157	android/media/MediaPlayer:setDataSource	(Ljava/lang/String;)V
    //   112: goto -86 -> 26
    //   115: astore_1
    //   116: aload_1
    //   117: invokevirtual 160	java/io/IOException:printStackTrace	()V
    //   120: new 162	java/lang/RuntimeException
    //   123: dup
    //   124: aload_1
    //   125: invokespecial 165	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   128: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	129	0	this	MediaPlayerGlue
    //   115	10	1	localIOException	java.io.IOException
    // Exception table:
    //   from	to	target	type
    //   4	26	115	java/io/IOException
    //   94	112	115	java/io/IOException
  }
  
  void changeToUnitialized()
  {
    if (this.mInitialized)
    {
      this.mInitialized = false;
      Object localObject = getPlayerCallbacks();
      if (localObject != null)
      {
        localObject = ((List)localObject).iterator();
        while (((Iterator)localObject).hasNext()) {
          ((PlaybackGlue.PlayerCallback)((Iterator)localObject).next()).onPreparedStateChanged(this);
        }
      }
    }
  }
  
  public void enableProgressUpdating(boolean paramBoolean)
  {
    if (this.mRunnable != null) {
      this.mHandler.removeCallbacks(this.mRunnable);
    }
    if (!paramBoolean) {
      return;
    }
    if (this.mRunnable == null) {
      this.mRunnable = new Runnable()
      {
        public void run()
        {
          MediaPlayerGlue.this.updateProgress();
          MediaPlayerGlue.this.mHandler.postDelayed(this, MediaPlayerGlue.this.getUpdatePeriod());
        }
      };
    }
    this.mHandler.postDelayed(this.mRunnable, getUpdatePeriod());
  }
  
  public int getCurrentPosition()
  {
    if (this.mInitialized) {
      return this.mPlayer.getCurrentPosition();
    }
    return 0;
  }
  
  public int getCurrentSpeedId()
  {
    if (isMediaPlaying()) {
      return 1;
    }
    return 0;
  }
  
  public Drawable getMediaArt()
  {
    return this.mCover;
  }
  
  public int getMediaDuration()
  {
    if (this.mInitialized) {
      return this.mPlayer.getDuration();
    }
    return 0;
  }
  
  public CharSequence getMediaSubtitle()
  {
    if (this.mArtist != null) {
      return this.mArtist;
    }
    return "N/a";
  }
  
  public CharSequence getMediaTitle()
  {
    if (this.mTitle != null) {
      return this.mTitle;
    }
    return "N/a";
  }
  
  public long getSupportedActions()
  {
    return 224L;
  }
  
  public boolean hasValidMedia()
  {
    return (this.mTitle != null) && ((this.mMediaSourcePath != null) || (this.mMediaSourceUri != null));
  }
  
  public boolean isMediaPlaying()
  {
    return (this.mInitialized) && (this.mPlayer.isPlaying());
  }
  
  public boolean isPlaying()
  {
    return isMediaPlaying();
  }
  
  public boolean isPrepared()
  {
    return this.mInitialized;
  }
  
  public boolean isReadyForPlayback()
  {
    return this.mInitialized;
  }
  
  public void onActionClicked(Action paramAction)
  {
    super.onActionClicked(paramAction);
    if ((paramAction instanceof PlaybackControlsRow.RepeatAction)) {
      ((PlaybackControlsRow.RepeatAction)paramAction).nextIndex();
    }
    for (;;)
    {
      onMetadataChanged();
      return;
      if (paramAction == this.mThumbsUpAction)
      {
        if (this.mThumbsUpAction.getIndex() == 0)
        {
          this.mThumbsUpAction.setIndex(1);
        }
        else
        {
          this.mThumbsUpAction.setIndex(0);
          this.mThumbsDownAction.setIndex(1);
        }
      }
      else if (paramAction == this.mThumbsDownAction) {
        if (this.mThumbsDownAction.getIndex() == 0)
        {
          this.mThumbsDownAction.setIndex(1);
        }
        else
        {
          this.mThumbsDownAction.setIndex(0);
          this.mThumbsUpAction.setIndex(1);
        }
      }
    }
  }
  
  protected void onAttachedToHost(PlaybackGlueHost paramPlaybackGlueHost)
  {
    super.onAttachedToHost(paramPlaybackGlueHost);
    if ((paramPlaybackGlueHost instanceof SurfaceHolderGlueHost)) {
      ((SurfaceHolderGlueHost)paramPlaybackGlueHost).setSurfaceHolderCallback(new VideoPlayerSurfaceHolderCallback());
    }
  }
  
  protected void onCreateSecondaryActions(ArrayObjectAdapter paramArrayObjectAdapter)
  {
    paramArrayObjectAdapter.add(this.mRepeatAction);
    paramArrayObjectAdapter.add(this.mThumbsDownAction);
    paramArrayObjectAdapter.add(this.mThumbsUpAction);
  }
  
  protected void onDetachedFromHost()
  {
    if ((getHost() instanceof SurfaceHolderGlueHost)) {
      ((SurfaceHolderGlueHost)getHost()).setSurfaceHolderCallback(null);
    }
    reset();
    release();
    super.onDetachedFromHost();
  }
  
  public void onItemSelected(Presenter.ViewHolder paramViewHolder, Object paramObject, RowPresenter.ViewHolder paramViewHolder1, Row paramRow)
  {
    if ((paramObject instanceof Action))
    {
      this.mSelectedAction = ((Action)paramObject);
      return;
    }
    this.mSelectedAction = null;
  }
  
  public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
  {
    if (((this.mSelectedAction instanceof PlaybackControlsRow.RewindAction)) || ((this.mSelectedAction instanceof PlaybackControlsRow.FastForwardAction)))
    {
      i = 1;
      if ((i == 0) || (!this.mInitialized)) {
        break label176;
      }
      i = 1;
      label38:
      if ((i == 0) || (paramKeyEvent.getKeyCode() != 23)) {
        break label182;
      }
      i = 1;
      label55:
      if ((i == 0) || (paramKeyEvent.getAction() != 0)) {
        break label188;
      }
      i = 1;
      label70:
      if ((i == 0) || (System.currentTimeMillis() - this.mLastKeyDownEvent <= 200L)) {
        break label194;
      }
    }
    label176:
    label182:
    label188:
    label194:
    for (int i = 1;; i = 0)
    {
      if (i == 0) {
        break label200;
      }
      this.mLastKeyDownEvent = System.currentTimeMillis();
      i = getCurrentPosition() + 10000;
      if ((this.mSelectedAction instanceof PlaybackControlsRow.RewindAction)) {
        i = getCurrentPosition() - 10000;
      }
      paramInt = i;
      if (i < 0) {
        paramInt = 0;
      }
      i = paramInt;
      if (paramInt > getMediaDuration()) {
        i = getMediaDuration();
      }
      seekTo(i);
      return true;
      i = 0;
      break;
      i = 0;
      break label38;
      i = 0;
      break label55;
      i = 0;
      break label70;
    }
    label200:
    return super.onKey(paramView, paramInt, paramKeyEvent);
  }
  
  public void pause()
  {
    if (isMediaPlaying())
    {
      this.mPlayer.pause();
      onStateChanged();
    }
  }
  
  public void play(int paramInt)
  {
    if ((!this.mInitialized) || (this.mPlayer.isPlaying())) {
      return;
    }
    this.mPlayer.start();
    onMetadataChanged();
    onStateChanged();
    updateProgress();
  }
  
  public void release()
  {
    changeToUnitialized();
    this.mPlayer.release();
  }
  
  public void reset()
  {
    changeToUnitialized();
    this.mPlayer.reset();
  }
  
  protected void seekTo(int paramInt)
  {
    if (!this.mInitialized) {
      return;
    }
    this.mPlayer.seekTo(paramInt);
  }
  
  public void setArtist(String paramString)
  {
    this.mArtist = paramString;
  }
  
  public void setCover(Drawable paramDrawable)
  {
    this.mCover = paramDrawable;
  }
  
  public void setDisplay(SurfaceHolder paramSurfaceHolder)
  {
    this.mPlayer.setDisplay(paramSurfaceHolder);
  }
  
  public boolean setMediaSource(Uri paramUri)
  {
    if (this.mMediaSourceUri != null)
    {
      if (!this.mMediaSourceUri.equals(paramUri)) {}
    }
    else {
      while (paramUri == null) {
        return false;
      }
    }
    this.mMediaSourceUri = paramUri;
    this.mMediaSourcePath = null;
    prepareMediaForPlaying();
    return true;
  }
  
  public boolean setMediaSource(String paramString)
  {
    if (this.mMediaSourcePath != null)
    {
      if (!this.mMediaSourcePath.equals(paramString)) {}
    }
    else {
      while (paramString == null) {
        return false;
      }
    }
    this.mMediaSourceUri = null;
    this.mMediaSourcePath = paramString;
    prepareMediaForPlaying();
    return true;
  }
  
  public void setMode(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return;
    case 0: 
      this.mOnCompletionListener = null;
      return;
    case 1: 
      this.mOnCompletionListener = new MediaPlayer.OnCompletionListener()
      {
        public boolean mFirstRepeat;
        
        public void onCompletion(MediaPlayer paramAnonymousMediaPlayer)
        {
          if (!this.mFirstRepeat)
          {
            this.mFirstRepeat = true;
            paramAnonymousMediaPlayer.setOnCompletionListener(null);
          }
          MediaPlayerGlue.this.play();
        }
      };
      return;
    }
    this.mOnCompletionListener = new MediaPlayer.OnCompletionListener()
    {
      public void onCompletion(MediaPlayer paramAnonymousMediaPlayer)
      {
        MediaPlayerGlue.this.play();
      }
    };
  }
  
  public void setTitle(String paramString)
  {
    this.mTitle = paramString;
  }
  
  public void setVideoUrl(String paramString)
  {
    setMediaSource(paramString);
    onMetadataChanged();
  }
  
  class VideoPlayerSurfaceHolderCallback
    implements SurfaceHolder.Callback
  {
    VideoPlayerSurfaceHolderCallback() {}
    
    public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3) {}
    
    public void surfaceCreated(SurfaceHolder paramSurfaceHolder)
    {
      MediaPlayerGlue.this.setDisplay(paramSurfaceHolder);
    }
    
    public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder)
    {
      MediaPlayerGlue.this.setDisplay(null);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/media/MediaPlayerGlue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */