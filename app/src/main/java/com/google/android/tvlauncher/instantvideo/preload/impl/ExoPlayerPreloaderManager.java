package com.google.android.tvlauncher.instantvideo.preload.impl;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection.Factory;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.tvlauncher.instantvideo.media.MediaPlayer;
import com.google.android.tvlauncher.instantvideo.media.impl.ExoPlayerImpl;
import com.google.android.tvlauncher.instantvideo.preload.Preloader;
import com.google.android.tvlauncher.instantvideo.preload.Preloader.OnPreloadFinishedListener;
import com.google.android.tvlauncher.instantvideo.preload.PreloaderManager;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class ExoPlayerPreloaderManager
  extends PreloaderManager
{
  private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
  private static final byte[] BUFFER = new byte['Ϩ'];
  private static final boolean DEBUG = false;
  private static final int MSG_START_PRELOAD = 100;
  private static final int PLAYER_VIEW_POOL_SIZE = 2;
  private static final long PRELOAD_TIMEOUT_MS = 10000L;
  private static final String TAG = "ExoPlayerPreloader";
  private CacheDataSourceFactory mCacheDataSourceFactory;
  private final long mCacheSizePerVideo;
  private Context mContext;
  private final Handler mHandler = new Handler(Looper.getMainLooper());
  private HandlerThread mHandlerThread;
  private final Deque<SimpleExoPlayerView> mPlayerViewPool = new LinkedList();
  private Set<Uri> mPreloadedVideo = new HashSet();
  
  public ExoPlayerPreloaderManager(Context paramContext, long paramLong1, long paramLong2)
  {
    this.mContext = paramContext.getApplicationContext();
    this.mCacheDataSourceFactory = new CacheDataSourceFactory(new SimpleCache(this.mContext.getCacheDir(), new LeastRecentlyUsedCacheEvictor(paramLong1 * 1024L * 1024L)), new DefaultDataSourceFactory(paramContext, BANDWIDTH_METER, new DefaultHttpDataSourceFactory("ExoPlayerPreloaderManager", BANDWIDTH_METER)), 0);
    this.mHandlerThread = new HandlerThread("ExoPlayerPreloaderManager");
    this.mHandlerThread.start();
    this.mCacheSizePerVideo = paramLong2;
  }
  
  public void bringPreloadedVideoToTopPriority(Uri paramUri) {}
  
  public int canPlayVideo(Uri paramUri)
  {
    if (paramUri == null) {
      return 0;
    }
    if (paramUri.toString().endsWith(".mp4")) {
      return 100;
    }
    return 10;
  }
  
  public void clearPreloadedData(Uri paramUri)
  {
    this.mPreloadedVideo.remove(paramUri);
  }
  
  public Preloader createPreloader(final Uri paramUri)
  {
    new Preloader()
    {
      ExoPlayerPreloaderManager.PreloadHandler mPreloadHandler;
      
      public void startPreload(Preloader.OnPreloadFinishedListener paramAnonymousOnPreloadFinishedListener)
      {
        if (this.mPreloadHandler != null) {
          return;
        }
        this.mPreloadHandler = new ExoPlayerPreloaderManager.PreloadHandler(ExoPlayerPreloaderManager.this, ExoPlayerPreloaderManager.this.mHandlerThread.getLooper(), paramUri, paramAnonymousOnPreloadFinishedListener);
        this.mPreloadHandler.sendEmptyMessage(100);
      }
      
      public void stopPreload()
      {
        if (this.mPreloadHandler != null)
        {
          ExoPlayerPreloaderManager.PreloadHandler.access$102(this.mPreloadHandler, true);
          this.mPreloadHandler = null;
        }
      }
    };
  }
  
  public MediaPlayer getOrCreatePlayer(Uri paramUri)
  {
    Object localObject2 = (SimpleExoPlayerView)this.mPlayerViewPool.pollFirst();
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      localObject1 = new SimpleExoPlayerView(this.mContext);
      ((SurfaceView)((SimpleExoPlayerView)localObject1).getVideoSurfaceView()).getHolder().setFormat(-2);
      ((SimpleExoPlayerView)localObject1).setUseController(false);
      ((SimpleExoPlayerView)localObject1).setResizeMode(3);
    }
    localObject2 = new DefaultTrackSelector(new AdaptiveVideoTrackSelection.Factory(BANDWIDTH_METER));
    localObject1 = new ExoPlayerImpl(this.mContext, ExoPlayerFactory.newSimpleInstance(this.mContext, (TrackSelector)localObject2, new DefaultLoadControl()), (SimpleExoPlayerView)localObject1, this.mHandler, this.mCacheDataSourceFactory);
    ((ExoPlayerImpl)localObject1).setVideoUri(paramUri);
    return (MediaPlayer)localObject1;
  }
  
  public boolean isPreloaded(Uri paramUri)
  {
    return this.mPreloadedVideo.contains(paramUri);
  }
  
  public void recycleMediaPlayer(MediaPlayer paramMediaPlayer)
  {
    paramMediaPlayer = (ExoPlayerImpl)paramMediaPlayer;
    paramMediaPlayer.getExoPlayerIntance().release();
    if (this.mPlayerViewPool.size() < 2) {
      this.mPlayerViewPool.addFirst((SimpleExoPlayerView)paramMediaPlayer.getPlayerView());
    }
  }
  
  private class PreloadHandler
    extends Handler
  {
    private Preloader.OnPreloadFinishedListener mOnPreloadFinishedListener;
    private volatile boolean mStopped;
    private Uri mVideoUri;
    
    PreloadHandler(Looper paramLooper, Uri paramUri, Preloader.OnPreloadFinishedListener paramOnPreloadFinishedListener)
    {
      super();
      this.mVideoUri = paramUri;
      this.mOnPreloadFinishedListener = paramOnPreloadFinishedListener;
    }
    
    private void notifyPreloadFinished(int paramInt)
    {
      if (this.mOnPreloadFinishedListener != null) {
        ExoPlayerPreloaderManager.this.mHandler.post(new Runnable()
        {
          public void run()
          {
            ExoPlayerPreloaderManager.PreloadHandler.this.mOnPreloadFinishedListener.onPreloadFinishedListener();
          }
        });
      }
    }
    
    /* Error */
    public void handleMessage(android.os.Message paramMessage)
    {
      // Byte code:
      //   0: aload_1
      //   1: getfield 58	android/os/Message:what	I
      //   4: bipush 100
      //   6: if_icmpeq +4 -> 10
      //   9: return
      //   10: aload_0
      //   11: getfield 21	com/google/android/tvlauncher/instantvideo/preload/impl/ExoPlayerPreloaderManager$PreloadHandler:this$0	Lcom/google/android/tvlauncher/instantvideo/preload/impl/ExoPlayerPreloaderManager;
      //   14: invokestatic 62	com/google/android/tvlauncher/instantvideo/preload/impl/ExoPlayerPreloaderManager:access$200	(Lcom/google/android/tvlauncher/instantvideo/preload/impl/ExoPlayerPreloaderManager;)Lcom/google/android/exoplayer2/upstream/cache/CacheDataSourceFactory;
      //   17: invokevirtual 68	com/google/android/exoplayer2/upstream/cache/CacheDataSourceFactory:createDataSource	()Lcom/google/android/exoplayer2/upstream/DataSource;
      //   20: astore_1
      //   21: aload_1
      //   22: new 70	com/google/android/exoplayer2/upstream/DataSpec
      //   25: dup
      //   26: aload_0
      //   27: getfield 26	com/google/android/tvlauncher/instantvideo/preload/impl/ExoPlayerPreloaderManager$PreloadHandler:mVideoUri	Landroid/net/Uri;
      //   30: invokespecial 73	com/google/android/exoplayer2/upstream/DataSpec:<init>	(Landroid/net/Uri;)V
      //   33: invokeinterface 79 2 0
      //   38: pop2
      //   39: lconst_0
      //   40: lstore 7
      //   42: iconst_0
      //   43: istore_3
      //   44: iconst_0
      //   45: istore_2
      //   46: iload_3
      //   47: i2l
      //   48: aload_0
      //   49: getfield 21	com/google/android/tvlauncher/instantvideo/preload/impl/ExoPlayerPreloaderManager$PreloadHandler:this$0	Lcom/google/android/tvlauncher/instantvideo/preload/impl/ExoPlayerPreloaderManager;
      //   52: invokestatic 83	com/google/android/tvlauncher/instantvideo/preload/impl/ExoPlayerPreloaderManager:access$300	(Lcom/google/android/tvlauncher/instantvideo/preload/impl/ExoPlayerPreloaderManager;)J
      //   55: lcmp
      //   56: ifge +31 -> 87
      //   59: aload_0
      //   60: getfield 33	com/google/android/tvlauncher/instantvideo/preload/impl/ExoPlayerPreloaderManager$PreloadHandler:mStopped	Z
      //   63: ifne +24 -> 87
      //   66: aload_1
      //   67: invokestatic 87	com/google/android/tvlauncher/instantvideo/preload/impl/ExoPlayerPreloaderManager:access$400	()[B
      //   70: iconst_0
      //   71: invokestatic 87	com/google/android/tvlauncher/instantvideo/preload/impl/ExoPlayerPreloaderManager:access$400	()[B
      //   74: arraylength
      //   75: invokeinterface 91 4 0
      //   80: istore 4
      //   82: iload 4
      //   84: ifge +60 -> 144
      //   87: aload_1
      //   88: invokeinterface 95 1 0
      //   93: aload_0
      //   94: iload_3
      //   95: invokespecial 97	com/google/android/tvlauncher/instantvideo/preload/impl/ExoPlayerPreloaderManager$PreloadHandler:notifyPreloadFinished	(I)V
      //   98: return
      //   99: astore_1
      //   100: aload_0
      //   101: iconst_0
      //   102: invokespecial 97	com/google/android/tvlauncher/instantvideo/preload/impl/ExoPlayerPreloaderManager$PreloadHandler:notifyPreloadFinished	(I)V
      //   105: return
      //   106: astore 9
      //   108: ldc 99
      //   110: new 101	java/lang/StringBuilder
      //   113: dup
      //   114: invokespecial 103	java/lang/StringBuilder:<init>	()V
      //   117: ldc 105
      //   119: invokevirtual 109	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   122: aload_0
      //   123: getfield 26	com/google/android/tvlauncher/instantvideo/preload/impl/ExoPlayerPreloaderManager$PreloadHandler:mVideoUri	Landroid/net/Uri;
      //   126: invokevirtual 112	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   129: ldc 114
      //   131: invokevirtual 109	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   134: invokevirtual 118	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   137: invokestatic 124	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   140: pop
      //   141: goto -54 -> 87
      //   144: iload 4
      //   146: ifne +84 -> 230
      //   149: lload 7
      //   151: lconst_0
      //   152: lcmp
      //   153: ifne +24 -> 177
      //   156: invokestatic 130	java/lang/System:currentTimeMillis	()J
      //   159: lstore 5
      //   161: iload_3
      //   162: iload 4
      //   164: iadd
      //   165: istore_3
      //   166: iload_2
      //   167: iconst_1
      //   168: iadd
      //   169: istore_2
      //   170: lload 5
      //   172: lstore 7
      //   174: goto -128 -> 46
      //   177: lload 7
      //   179: lstore 5
      //   181: invokestatic 130	java/lang/System:currentTimeMillis	()J
      //   184: lload 7
      //   186: lsub
      //   187: ldc2_w 131
      //   190: lcmp
      //   191: ifle -30 -> 161
      //   194: ldc 99
      //   196: new 101	java/lang/StringBuilder
      //   199: dup
      //   200: invokespecial 103	java/lang/StringBuilder:<init>	()V
      //   203: ldc -122
      //   205: invokevirtual 109	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   208: aload_0
      //   209: getfield 26	com/google/android/tvlauncher/instantvideo/preload/impl/ExoPlayerPreloaderManager$PreloadHandler:mVideoUri	Landroid/net/Uri;
      //   212: invokevirtual 112	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   215: ldc 114
      //   217: invokevirtual 109	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   220: invokevirtual 118	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   223: invokestatic 124	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   226: pop
      //   227: goto -140 -> 87
      //   230: lconst_0
      //   231: lstore 5
      //   233: goto -72 -> 161
      //   236: astore_1
      //   237: goto -144 -> 93
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	240	0	this	PreloadHandler
      //   0	240	1	paramMessage	android.os.Message
      //   45	125	2	i	int
      //   43	123	3	j	int
      //   80	85	4	k	int
      //   159	73	5	l1	long
      //   40	145	7	l2	long
      //   106	1	9	localIOException	java.io.IOException
      // Exception table:
      //   from	to	target	type
      //   21	39	99	java/io/IOException
      //   66	82	106	java/io/IOException
      //   87	93	236	java/io/IOException
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/instantvideo/preload/impl/ExoPlayerPreloaderManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */