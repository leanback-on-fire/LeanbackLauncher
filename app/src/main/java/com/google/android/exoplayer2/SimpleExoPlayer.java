package com.google.android.exoplayer2;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.PlaybackParams;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataRenderer;
import com.google.android.exoplayer2.metadata.MetadataRenderer.Output;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextRenderer;
import com.google.android.exoplayer2.text.TextRenderer.Output;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

@TargetApi(16)
public class SimpleExoPlayer
  implements ExoPlayer
{
  public static final int EXTENSION_RENDERER_MODE_OFF = 0;
  public static final int EXTENSION_RENDERER_MODE_ON = 1;
  public static final int EXTENSION_RENDERER_MODE_PREFER = 2;
  protected static final int MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY = 50;
  private static final String TAG = "SimpleExoPlayer";
  private AudioRendererEventListener audioDebugListener;
  private DecoderCounters audioDecoderCounters;
  private Format audioFormat;
  private final int audioRendererCount;
  private int audioSessionId;
  private int audioStreamType;
  private float audioVolume;
  private final ComponentListener componentListener = new ComponentListener(null);
  private final Handler mainHandler = new Handler();
  private MetadataRenderer.Output metadataOutput;
  private boolean ownsSurface;
  private PlaybackParamsHolder playbackParamsHolder;
  private final ExoPlayer player;
  private final Renderer[] renderers;
  private Surface surface;
  private SurfaceHolder surfaceHolder;
  private TextRenderer.Output textOutput;
  private TextureView textureView;
  private VideoRendererEventListener videoDebugListener;
  private DecoderCounters videoDecoderCounters;
  private Format videoFormat;
  private VideoListener videoListener;
  private final int videoRendererCount;
  private int videoScalingMode;
  
  protected SimpleExoPlayer(Context paramContext, TrackSelector paramTrackSelector, LoadControl paramLoadControl, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, int paramInt, long paramLong)
  {
    ArrayList localArrayList = new ArrayList();
    buildRenderers(paramContext, this.mainHandler, paramDrmSessionManager, paramInt, paramLong, localArrayList);
    this.renderers = ((Renderer[])localArrayList.toArray(new Renderer[localArrayList.size()]));
    int i = 0;
    int j = 0;
    paramContext = this.renderers;
    int k = paramContext.length;
    paramInt = 0;
    if (paramInt < k)
    {
      switch (paramContext[paramInt].getTrackType())
      {
      }
      for (;;)
      {
        paramInt += 1;
        break;
        i += 1;
        continue;
        j += 1;
      }
    }
    this.videoRendererCount = i;
    this.audioRendererCount = j;
    this.audioVolume = 1.0F;
    this.audioSessionId = 0;
    this.audioStreamType = 3;
    this.videoScalingMode = 1;
    this.player = new ExoPlayerImpl(this.renderers, paramTrackSelector, paramLoadControl);
  }
  
  private void buildRenderers(Context paramContext, Handler paramHandler, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, int paramInt, long paramLong, ArrayList<Renderer> paramArrayList)
  {
    buildVideoRenderers(paramContext, paramHandler, paramDrmSessionManager, paramInt, this.componentListener, paramLong, paramArrayList);
    buildAudioRenderers(paramContext, paramHandler, paramDrmSessionManager, paramInt, this.componentListener, paramArrayList);
    buildTextRenderers(paramContext, paramHandler, paramInt, this.componentListener, paramArrayList);
    buildMetadataRenderers(paramContext, paramHandler, paramInt, this.componentListener, paramArrayList);
    buildMiscellaneousRenderers(paramContext, paramHandler, paramInt, paramArrayList);
  }
  
  private void removeSurfaceCallbacks()
  {
    if (this.textureView != null)
    {
      if (this.textureView.getSurfaceTextureListener() == this.componentListener) {
        break label60;
      }
      Log.w("SimpleExoPlayer", "SurfaceTextureListener already unset or replaced.");
    }
    for (;;)
    {
      this.textureView = null;
      if (this.surfaceHolder != null)
      {
        this.surfaceHolder.removeCallback(this.componentListener);
        this.surfaceHolder = null;
      }
      return;
      label60:
      this.textureView.setSurfaceTextureListener(null);
    }
  }
  
  private void setVideoSurfaceInternal(Surface paramSurface, boolean paramBoolean)
  {
    ExoPlayer.ExoPlayerMessage[] arrayOfExoPlayerMessage = new ExoPlayer.ExoPlayerMessage[this.videoRendererCount];
    Renderer[] arrayOfRenderer = this.renderers;
    int m = arrayOfRenderer.length;
    int j = 0;
    int i = 0;
    if (j < m)
    {
      Renderer localRenderer = arrayOfRenderer[j];
      if (localRenderer.getTrackType() != 2) {
        break label147;
      }
      int k = i + 1;
      arrayOfExoPlayerMessage[i] = new ExoPlayer.ExoPlayerMessage(localRenderer, 1, paramSurface);
      i = k;
    }
    label147:
    for (;;)
    {
      j += 1;
      break;
      if ((this.surface != null) && (this.surface != paramSurface))
      {
        if (this.ownsSurface) {
          this.surface.release();
        }
        this.player.blockingSendMessages(arrayOfExoPlayerMessage);
      }
      for (;;)
      {
        this.surface = paramSurface;
        this.ownsSurface = paramBoolean;
        return;
        this.player.sendMessages(arrayOfExoPlayerMessage);
      }
    }
  }
  
  public void addListener(ExoPlayer.EventListener paramEventListener)
  {
    this.player.addListener(paramEventListener);
  }
  
  public void blockingSendMessages(ExoPlayer.ExoPlayerMessage... paramVarArgs)
  {
    this.player.blockingSendMessages(paramVarArgs);
  }
  
  /* Error */
  protected void buildAudioRenderers(Context paramContext, Handler paramHandler, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, int paramInt, AudioRendererEventListener paramAudioRendererEventListener, ArrayList<Renderer> paramArrayList)
  {
    // Byte code:
    //   0: aload 6
    //   2: new 262	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer
    //   5: dup
    //   6: getstatic 268	com/google/android/exoplayer2/mediacodec/MediaCodecSelector:DEFAULT	Lcom/google/android/exoplayer2/mediacodec/MediaCodecSelector;
    //   9: aload_3
    //   10: iconst_1
    //   11: aload_2
    //   12: aload 5
    //   14: aload_1
    //   15: invokestatic 274	com/google/android/exoplayer2/audio/AudioCapabilities:getCapabilities	(Landroid/content/Context;)Lcom/google/android/exoplayer2/audio/AudioCapabilities;
    //   18: invokespecial 277	com/google/android/exoplayer2/audio/MediaCodecAudioRenderer:<init>	(Lcom/google/android/exoplayer2/mediacodec/MediaCodecSelector;Lcom/google/android/exoplayer2/drm/DrmSessionManager;ZLandroid/os/Handler;Lcom/google/android/exoplayer2/audio/AudioRendererEventListener;Lcom/google/android/exoplayer2/audio/AudioCapabilities;)V
    //   21: invokevirtual 281	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   24: pop
    //   25: iload 4
    //   27: ifne +4 -> 31
    //   30: return
    //   31: aload 6
    //   33: invokevirtual 103	java/util/ArrayList:size	()I
    //   36: istore 7
    //   38: iload 4
    //   40: iconst_2
    //   41: if_icmpne +289 -> 330
    //   44: iload 7
    //   46: iconst_1
    //   47: isub
    //   48: istore 4
    //   50: ldc_w 283
    //   53: invokestatic 289	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   56: iconst_2
    //   57: anewarray 285	java/lang/Class
    //   60: dup
    //   61: iconst_0
    //   62: ldc 84
    //   64: aastore
    //   65: dup
    //   66: iconst_1
    //   67: ldc_w 291
    //   70: aastore
    //   71: invokevirtual 295	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   74: iconst_2
    //   75: anewarray 4	java/lang/Object
    //   78: dup
    //   79: iconst_0
    //   80: aload_2
    //   81: aastore
    //   82: dup
    //   83: iconst_1
    //   84: aload_0
    //   85: getfield 92	com/google/android/exoplayer2/SimpleExoPlayer:componentListener	Lcom/google/android/exoplayer2/SimpleExoPlayer$ComponentListener;
    //   88: aastore
    //   89: invokevirtual 301	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   92: checkcast 105	com/google/android/exoplayer2/Renderer
    //   95: astore_1
    //   96: iload 4
    //   98: iconst_1
    //   99: iadd
    //   100: istore 7
    //   102: aload 6
    //   104: iload 4
    //   106: aload_1
    //   107: invokevirtual 304	java/util/ArrayList:add	(ILjava/lang/Object;)V
    //   110: ldc 36
    //   112: ldc_w 306
    //   115: invokestatic 309	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   118: pop
    //   119: iload 7
    //   121: istore 4
    //   123: ldc_w 311
    //   126: invokestatic 289	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   129: iconst_2
    //   130: anewarray 285	java/lang/Class
    //   133: dup
    //   134: iconst_0
    //   135: ldc 84
    //   137: aastore
    //   138: dup
    //   139: iconst_1
    //   140: ldc_w 291
    //   143: aastore
    //   144: invokevirtual 295	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   147: iconst_2
    //   148: anewarray 4	java/lang/Object
    //   151: dup
    //   152: iconst_0
    //   153: aload_2
    //   154: aastore
    //   155: dup
    //   156: iconst_1
    //   157: aload_0
    //   158: getfield 92	com/google/android/exoplayer2/SimpleExoPlayer:componentListener	Lcom/google/android/exoplayer2/SimpleExoPlayer$ComponentListener;
    //   161: aastore
    //   162: invokevirtual 301	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   165: checkcast 105	com/google/android/exoplayer2/Renderer
    //   168: astore_1
    //   169: iload 4
    //   171: iconst_1
    //   172: iadd
    //   173: istore 7
    //   175: aload 6
    //   177: iload 4
    //   179: aload_1
    //   180: invokevirtual 304	java/util/ArrayList:add	(ILjava/lang/Object;)V
    //   183: ldc 36
    //   185: ldc_w 313
    //   188: invokestatic 309	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   191: pop
    //   192: ldc_w 315
    //   195: invokestatic 289	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   198: iconst_2
    //   199: anewarray 285	java/lang/Class
    //   202: dup
    //   203: iconst_0
    //   204: ldc 84
    //   206: aastore
    //   207: dup
    //   208: iconst_1
    //   209: ldc_w 291
    //   212: aastore
    //   213: invokevirtual 295	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   216: iconst_2
    //   217: anewarray 4	java/lang/Object
    //   220: dup
    //   221: iconst_0
    //   222: aload_2
    //   223: aastore
    //   224: dup
    //   225: iconst_1
    //   226: aload_0
    //   227: getfield 92	com/google/android/exoplayer2/SimpleExoPlayer:componentListener	Lcom/google/android/exoplayer2/SimpleExoPlayer$ComponentListener;
    //   230: aastore
    //   231: invokevirtual 301	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   234: checkcast 105	com/google/android/exoplayer2/Renderer
    //   237: astore_1
    //   238: aload 6
    //   240: iload 7
    //   242: aload_1
    //   243: invokevirtual 304	java/util/ArrayList:add	(ILjava/lang/Object;)V
    //   246: ldc 36
    //   248: ldc_w 317
    //   251: invokestatic 309	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   254: pop
    //   255: return
    //   256: astore_1
    //   257: return
    //   258: astore_1
    //   259: goto -136 -> 123
    //   262: astore_1
    //   263: new 319	java/lang/RuntimeException
    //   266: dup
    //   267: aload_1
    //   268: invokespecial 322	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   271: athrow
    //   272: astore_1
    //   273: iload 4
    //   275: istore 7
    //   277: goto -85 -> 192
    //   280: astore_1
    //   281: new 319	java/lang/RuntimeException
    //   284: dup
    //   285: aload_1
    //   286: invokespecial 322	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   289: athrow
    //   290: astore_1
    //   291: new 319	java/lang/RuntimeException
    //   294: dup
    //   295: aload_1
    //   296: invokespecial 322	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   299: athrow
    //   300: astore_1
    //   301: goto -10 -> 291
    //   304: astore_1
    //   305: return
    //   306: astore_1
    //   307: goto -26 -> 281
    //   310: astore_1
    //   311: iload 7
    //   313: istore 4
    //   315: goto -42 -> 273
    //   318: astore_1
    //   319: goto -56 -> 263
    //   322: astore_1
    //   323: iload 7
    //   325: istore 4
    //   327: goto -68 -> 259
    //   330: iload 7
    //   332: istore 4
    //   334: goto -284 -> 50
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	337	0	this	SimpleExoPlayer
    //   0	337	1	paramContext	Context
    //   0	337	2	paramHandler	Handler
    //   0	337	3	paramDrmSessionManager	DrmSessionManager<FrameworkMediaCrypto>
    //   0	337	4	paramInt	int
    //   0	337	5	paramAudioRendererEventListener	AudioRendererEventListener
    //   0	337	6	paramArrayList	ArrayList<Renderer>
    //   36	295	7	i	int
    // Exception table:
    //   from	to	target	type
    //   238	255	256	java/lang/ClassNotFoundException
    //   50	96	258	java/lang/ClassNotFoundException
    //   50	96	262	java/lang/Exception
    //   123	169	272	java/lang/ClassNotFoundException
    //   123	169	280	java/lang/Exception
    //   192	238	290	java/lang/Exception
    //   238	255	300	java/lang/Exception
    //   192	238	304	java/lang/ClassNotFoundException
    //   175	192	306	java/lang/Exception
    //   175	192	310	java/lang/ClassNotFoundException
    //   102	119	318	java/lang/Exception
    //   102	119	322	java/lang/ClassNotFoundException
  }
  
  protected void buildMetadataRenderers(Context paramContext, Handler paramHandler, int paramInt, MetadataRenderer.Output paramOutput, ArrayList<Renderer> paramArrayList)
  {
    paramArrayList.add(new MetadataRenderer(paramOutput, paramHandler.getLooper()));
  }
  
  protected void buildMiscellaneousRenderers(Context paramContext, Handler paramHandler, int paramInt, ArrayList<Renderer> paramArrayList) {}
  
  protected void buildTextRenderers(Context paramContext, Handler paramHandler, int paramInt, TextRenderer.Output paramOutput, ArrayList<Renderer> paramArrayList)
  {
    paramArrayList.add(new TextRenderer(paramOutput, paramHandler.getLooper()));
  }
  
  /* Error */
  protected void buildVideoRenderers(Context paramContext, Handler paramHandler, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, int paramInt, VideoRendererEventListener paramVideoRendererEventListener, long paramLong, ArrayList<Renderer> paramArrayList)
  {
    // Byte code:
    //   0: aload 8
    //   2: new 342	com/google/android/exoplayer2/video/MediaCodecVideoRenderer
    //   5: dup
    //   6: aload_1
    //   7: getstatic 268	com/google/android/exoplayer2/mediacodec/MediaCodecSelector:DEFAULT	Lcom/google/android/exoplayer2/mediacodec/MediaCodecSelector;
    //   10: lload 6
    //   12: aload_3
    //   13: iconst_0
    //   14: aload_2
    //   15: aload 5
    //   17: bipush 50
    //   19: invokespecial 345	com/google/android/exoplayer2/video/MediaCodecVideoRenderer:<init>	(Landroid/content/Context;Lcom/google/android/exoplayer2/mediacodec/MediaCodecSelector;JLcom/google/android/exoplayer2/drm/DrmSessionManager;ZLandroid/os/Handler;Lcom/google/android/exoplayer2/video/VideoRendererEventListener;I)V
    //   22: invokevirtual 281	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   25: pop
    //   26: iload 4
    //   28: ifne +4 -> 32
    //   31: return
    //   32: aload 8
    //   34: invokevirtual 103	java/util/ArrayList:size	()I
    //   37: istore 9
    //   39: iload 4
    //   41: iconst_2
    //   42: if_icmpne +132 -> 174
    //   45: iload 9
    //   47: iconst_1
    //   48: isub
    //   49: istore 4
    //   51: ldc_w 347
    //   54: invokestatic 289	java/lang/Class:forName	(Ljava/lang/String;)Ljava/lang/Class;
    //   57: iconst_5
    //   58: anewarray 285	java/lang/Class
    //   61: dup
    //   62: iconst_0
    //   63: getstatic 353	java/lang/Boolean:TYPE	Ljava/lang/Class;
    //   66: aastore
    //   67: dup
    //   68: iconst_1
    //   69: getstatic 356	java/lang/Long:TYPE	Ljava/lang/Class;
    //   72: aastore
    //   73: dup
    //   74: iconst_2
    //   75: ldc 84
    //   77: aastore
    //   78: dup
    //   79: iconst_3
    //   80: ldc_w 358
    //   83: aastore
    //   84: dup
    //   85: iconst_4
    //   86: getstatic 361	java/lang/Integer:TYPE	Ljava/lang/Class;
    //   89: aastore
    //   90: invokevirtual 295	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   93: iconst_5
    //   94: anewarray 4	java/lang/Object
    //   97: dup
    //   98: iconst_0
    //   99: iconst_1
    //   100: invokestatic 365	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   103: aastore
    //   104: dup
    //   105: iconst_1
    //   106: lload 6
    //   108: invokestatic 368	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   111: aastore
    //   112: dup
    //   113: iconst_2
    //   114: aload_2
    //   115: aastore
    //   116: dup
    //   117: iconst_3
    //   118: aload_0
    //   119: getfield 92	com/google/android/exoplayer2/SimpleExoPlayer:componentListener	Lcom/google/android/exoplayer2/SimpleExoPlayer$ComponentListener;
    //   122: aastore
    //   123: dup
    //   124: iconst_4
    //   125: bipush 50
    //   127: invokestatic 371	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   130: aastore
    //   131: invokevirtual 301	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   134: checkcast 105	com/google/android/exoplayer2/Renderer
    //   137: astore_1
    //   138: aload 8
    //   140: iload 4
    //   142: aload_1
    //   143: invokevirtual 304	java/util/ArrayList:add	(ILjava/lang/Object;)V
    //   146: ldc 36
    //   148: ldc_w 373
    //   151: invokestatic 309	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   154: pop
    //   155: return
    //   156: astore_1
    //   157: return
    //   158: astore_1
    //   159: new 319	java/lang/RuntimeException
    //   162: dup
    //   163: aload_1
    //   164: invokespecial 322	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   167: athrow
    //   168: astore_1
    //   169: goto -10 -> 159
    //   172: astore_1
    //   173: return
    //   174: iload 9
    //   176: istore 4
    //   178: goto -127 -> 51
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	181	0	this	SimpleExoPlayer
    //   0	181	1	paramContext	Context
    //   0	181	2	paramHandler	Handler
    //   0	181	3	paramDrmSessionManager	DrmSessionManager<FrameworkMediaCrypto>
    //   0	181	4	paramInt	int
    //   0	181	5	paramVideoRendererEventListener	VideoRendererEventListener
    //   0	181	6	paramLong	long
    //   0	181	8	paramArrayList	ArrayList<Renderer>
    //   37	138	9	i	int
    // Exception table:
    //   from	to	target	type
    //   138	155	156	java/lang/ClassNotFoundException
    //   51	138	158	java/lang/Exception
    //   138	155	168	java/lang/Exception
    //   51	138	172	java/lang/ClassNotFoundException
  }
  
  public void clearVideoSurface()
  {
    setVideoSurface(null);
  }
  
  public DecoderCounters getAudioDecoderCounters()
  {
    return this.audioDecoderCounters;
  }
  
  public Format getAudioFormat()
  {
    return this.audioFormat;
  }
  
  public int getAudioSessionId()
  {
    return this.audioSessionId;
  }
  
  public int getAudioStreamType()
  {
    return this.audioStreamType;
  }
  
  public int getBufferedPercentage()
  {
    return this.player.getBufferedPercentage();
  }
  
  public long getBufferedPosition()
  {
    return this.player.getBufferedPosition();
  }
  
  public Object getCurrentManifest()
  {
    return this.player.getCurrentManifest();
  }
  
  public int getCurrentPeriodIndex()
  {
    return this.player.getCurrentPeriodIndex();
  }
  
  public long getCurrentPosition()
  {
    return this.player.getCurrentPosition();
  }
  
  public Timeline getCurrentTimeline()
  {
    return this.player.getCurrentTimeline();
  }
  
  public TrackGroupArray getCurrentTrackGroups()
  {
    return this.player.getCurrentTrackGroups();
  }
  
  public TrackSelectionArray getCurrentTrackSelections()
  {
    return this.player.getCurrentTrackSelections();
  }
  
  public int getCurrentWindowIndex()
  {
    return this.player.getCurrentWindowIndex();
  }
  
  public long getDuration()
  {
    return this.player.getDuration();
  }
  
  public boolean getPlayWhenReady()
  {
    return this.player.getPlayWhenReady();
  }
  
  @TargetApi(23)
  public PlaybackParams getPlaybackParams()
  {
    if (this.playbackParamsHolder == null) {
      return null;
    }
    return this.playbackParamsHolder.params;
  }
  
  public int getPlaybackState()
  {
    return this.player.getPlaybackState();
  }
  
  public int getRendererCount()
  {
    return this.player.getRendererCount();
  }
  
  public int getRendererType(int paramInt)
  {
    return this.player.getRendererType(paramInt);
  }
  
  public DecoderCounters getVideoDecoderCounters()
  {
    return this.videoDecoderCounters;
  }
  
  public Format getVideoFormat()
  {
    return this.videoFormat;
  }
  
  public int getVideoScalingMode()
  {
    return this.videoScalingMode;
  }
  
  public float getVolume()
  {
    return this.audioVolume;
  }
  
  public boolean isCurrentWindowDynamic()
  {
    return this.player.isCurrentWindowDynamic();
  }
  
  public boolean isCurrentWindowSeekable()
  {
    return this.player.isCurrentWindowSeekable();
  }
  
  public boolean isLoading()
  {
    return this.player.isLoading();
  }
  
  public void prepare(MediaSource paramMediaSource)
  {
    this.player.prepare(paramMediaSource);
  }
  
  public void prepare(MediaSource paramMediaSource, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.player.prepare(paramMediaSource, paramBoolean1, paramBoolean2);
  }
  
  public void release()
  {
    this.player.release();
    removeSurfaceCallbacks();
    if (this.surface != null)
    {
      if (this.ownsSurface) {
        this.surface.release();
      }
      this.surface = null;
    }
  }
  
  public void removeListener(ExoPlayer.EventListener paramEventListener)
  {
    this.player.removeListener(paramEventListener);
  }
  
  public void seekTo(int paramInt, long paramLong)
  {
    this.player.seekTo(paramInt, paramLong);
  }
  
  public void seekTo(long paramLong)
  {
    this.player.seekTo(paramLong);
  }
  
  public void seekToDefaultPosition()
  {
    this.player.seekToDefaultPosition();
  }
  
  public void seekToDefaultPosition(int paramInt)
  {
    this.player.seekToDefaultPosition(paramInt);
  }
  
  public void sendMessages(ExoPlayer.ExoPlayerMessage... paramVarArgs)
  {
    this.player.sendMessages(paramVarArgs);
  }
  
  public void setAudioDebugListener(AudioRendererEventListener paramAudioRendererEventListener)
  {
    this.audioDebugListener = paramAudioRendererEventListener;
  }
  
  public void setAudioStreamType(int paramInt)
  {
    this.audioStreamType = paramInt;
    ExoPlayer.ExoPlayerMessage[] arrayOfExoPlayerMessage = new ExoPlayer.ExoPlayerMessage[this.audioRendererCount];
    Renderer[] arrayOfRenderer = this.renderers;
    int m = arrayOfRenderer.length;
    int j = 0;
    int i = 0;
    if (j < m)
    {
      Renderer localRenderer = arrayOfRenderer[j];
      if (localRenderer.getTrackType() != 1) {
        break label97;
      }
      int k = i + 1;
      arrayOfExoPlayerMessage[i] = new ExoPlayer.ExoPlayerMessage(localRenderer, 4, Integer.valueOf(paramInt));
      i = k;
    }
    label97:
    for (;;)
    {
      j += 1;
      break;
      this.player.sendMessages(arrayOfExoPlayerMessage);
      return;
    }
  }
  
  public void setMetadataOutput(MetadataRenderer.Output paramOutput)
  {
    this.metadataOutput = paramOutput;
  }
  
  public void setPlayWhenReady(boolean paramBoolean)
  {
    this.player.setPlayWhenReady(paramBoolean);
  }
  
  @TargetApi(23)
  public void setPlaybackParams(PlaybackParams paramPlaybackParams)
  {
    ExoPlayer.ExoPlayerMessage[] arrayOfExoPlayerMessage;
    int j;
    if (paramPlaybackParams != null)
    {
      paramPlaybackParams.allowDefaults();
      this.playbackParamsHolder = new PlaybackParamsHolder(paramPlaybackParams);
      arrayOfExoPlayerMessage = new ExoPlayer.ExoPlayerMessage[this.audioRendererCount];
      Renderer[] arrayOfRenderer = this.renderers;
      int m = arrayOfRenderer.length;
      j = 0;
      int i = 0;
      label45:
      if (j >= m) {
        break label106;
      }
      Renderer localRenderer = arrayOfRenderer[j];
      if (localRenderer.getTrackType() != 1) {
        break label118;
      }
      int k = i + 1;
      arrayOfExoPlayerMessage[i] = new ExoPlayer.ExoPlayerMessage(localRenderer, 3, paramPlaybackParams);
      i = k;
    }
    label106:
    label118:
    for (;;)
    {
      j += 1;
      break label45;
      this.playbackParamsHolder = null;
      break;
      this.player.sendMessages(arrayOfExoPlayerMessage);
      return;
    }
  }
  
  public void setTextOutput(TextRenderer.Output paramOutput)
  {
    this.textOutput = paramOutput;
  }
  
  public void setVideoDebugListener(VideoRendererEventListener paramVideoRendererEventListener)
  {
    this.videoDebugListener = paramVideoRendererEventListener;
  }
  
  public void setVideoListener(VideoListener paramVideoListener)
  {
    this.videoListener = paramVideoListener;
  }
  
  public void setVideoScalingMode(int paramInt)
  {
    this.videoScalingMode = paramInt;
    ExoPlayer.ExoPlayerMessage[] arrayOfExoPlayerMessage = new ExoPlayer.ExoPlayerMessage[this.videoRendererCount];
    Renderer[] arrayOfRenderer = this.renderers;
    int m = arrayOfRenderer.length;
    int j = 0;
    int i = 0;
    if (j < m)
    {
      Renderer localRenderer = arrayOfRenderer[j];
      if (localRenderer.getTrackType() != 2) {
        break label97;
      }
      int k = i + 1;
      arrayOfExoPlayerMessage[i] = new ExoPlayer.ExoPlayerMessage(localRenderer, 5, Integer.valueOf(paramInt));
      i = k;
    }
    label97:
    for (;;)
    {
      j += 1;
      break;
      this.player.sendMessages(arrayOfExoPlayerMessage);
      return;
    }
  }
  
  public void setVideoSurface(Surface paramSurface)
  {
    removeSurfaceCallbacks();
    setVideoSurfaceInternal(paramSurface, false);
  }
  
  public void setVideoSurfaceHolder(SurfaceHolder paramSurfaceHolder)
  {
    removeSurfaceCallbacks();
    this.surfaceHolder = paramSurfaceHolder;
    if (paramSurfaceHolder == null)
    {
      setVideoSurfaceInternal(null, false);
      return;
    }
    setVideoSurfaceInternal(paramSurfaceHolder.getSurface(), false);
    paramSurfaceHolder.addCallback(this.componentListener);
  }
  
  public void setVideoSurfaceView(SurfaceView paramSurfaceView)
  {
    setVideoSurfaceHolder(paramSurfaceView.getHolder());
  }
  
  public void setVideoTextureView(TextureView paramTextureView)
  {
    Surface localSurface = null;
    removeSurfaceCallbacks();
    this.textureView = paramTextureView;
    if (paramTextureView == null)
    {
      setVideoSurfaceInternal(null, true);
      return;
    }
    if (paramTextureView.getSurfaceTextureListener() != null) {
      Log.w("SimpleExoPlayer", "Replacing existing SurfaceTextureListener.");
    }
    SurfaceTexture localSurfaceTexture = paramTextureView.getSurfaceTexture();
    if (localSurfaceTexture == null) {}
    for (;;)
    {
      setVideoSurfaceInternal(localSurface, true);
      paramTextureView.setSurfaceTextureListener(this.componentListener);
      return;
      localSurface = new Surface(localSurfaceTexture);
    }
  }
  
  public void setVolume(float paramFloat)
  {
    this.audioVolume = paramFloat;
    ExoPlayer.ExoPlayerMessage[] arrayOfExoPlayerMessage = new ExoPlayer.ExoPlayerMessage[this.audioRendererCount];
    Renderer[] arrayOfRenderer = this.renderers;
    int m = arrayOfRenderer.length;
    int j = 0;
    int i = 0;
    if (j < m)
    {
      Renderer localRenderer = arrayOfRenderer[j];
      if (localRenderer.getTrackType() != 1) {
        break label97;
      }
      int k = i + 1;
      arrayOfExoPlayerMessage[i] = new ExoPlayer.ExoPlayerMessage(localRenderer, 2, Float.valueOf(paramFloat));
      i = k;
    }
    label97:
    for (;;)
    {
      j += 1;
      break;
      this.player.sendMessages(arrayOfExoPlayerMessage);
      return;
    }
  }
  
  public void stop()
  {
    this.player.stop();
  }
  
  private final class ComponentListener
    implements VideoRendererEventListener, AudioRendererEventListener, TextRenderer.Output, MetadataRenderer.Output, SurfaceHolder.Callback, TextureView.SurfaceTextureListener
  {
    private ComponentListener() {}
    
    public void onAudioDecoderInitialized(String paramString, long paramLong1, long paramLong2)
    {
      if (SimpleExoPlayer.this.audioDebugListener != null) {
        SimpleExoPlayer.this.audioDebugListener.onAudioDecoderInitialized(paramString, paramLong1, paramLong2);
      }
    }
    
    public void onAudioDisabled(DecoderCounters paramDecoderCounters)
    {
      if (SimpleExoPlayer.this.audioDebugListener != null) {
        SimpleExoPlayer.this.audioDebugListener.onAudioDisabled(paramDecoderCounters);
      }
      SimpleExoPlayer.access$902(SimpleExoPlayer.this, null);
      SimpleExoPlayer.access$602(SimpleExoPlayer.this, null);
      SimpleExoPlayer.access$802(SimpleExoPlayer.this, 0);
    }
    
    public void onAudioEnabled(DecoderCounters paramDecoderCounters)
    {
      SimpleExoPlayer.access$602(SimpleExoPlayer.this, paramDecoderCounters);
      if (SimpleExoPlayer.this.audioDebugListener != null) {
        SimpleExoPlayer.this.audioDebugListener.onAudioEnabled(paramDecoderCounters);
      }
    }
    
    public void onAudioInputFormatChanged(Format paramFormat)
    {
      SimpleExoPlayer.access$902(SimpleExoPlayer.this, paramFormat);
      if (SimpleExoPlayer.this.audioDebugListener != null) {
        SimpleExoPlayer.this.audioDebugListener.onAudioInputFormatChanged(paramFormat);
      }
    }
    
    public void onAudioSessionId(int paramInt)
    {
      SimpleExoPlayer.access$802(SimpleExoPlayer.this, paramInt);
      if (SimpleExoPlayer.this.audioDebugListener != null) {
        SimpleExoPlayer.this.audioDebugListener.onAudioSessionId(paramInt);
      }
    }
    
    public void onAudioTrackUnderrun(int paramInt, long paramLong1, long paramLong2)
    {
      if (SimpleExoPlayer.this.audioDebugListener != null) {
        SimpleExoPlayer.this.audioDebugListener.onAudioTrackUnderrun(paramInt, paramLong1, paramLong2);
      }
    }
    
    public void onCues(List<Cue> paramList)
    {
      if (SimpleExoPlayer.this.textOutput != null) {
        SimpleExoPlayer.this.textOutput.onCues(paramList);
      }
    }
    
    public void onDroppedFrames(int paramInt, long paramLong)
    {
      if (SimpleExoPlayer.this.videoDebugListener != null) {
        SimpleExoPlayer.this.videoDebugListener.onDroppedFrames(paramInt, paramLong);
      }
    }
    
    public void onMetadata(Metadata paramMetadata)
    {
      if (SimpleExoPlayer.this.metadataOutput != null) {
        SimpleExoPlayer.this.metadataOutput.onMetadata(paramMetadata);
      }
    }
    
    public void onRenderedFirstFrame(Surface paramSurface)
    {
      if ((SimpleExoPlayer.this.videoListener != null) && (SimpleExoPlayer.this.surface == paramSurface)) {
        SimpleExoPlayer.this.videoListener.onRenderedFirstFrame();
      }
      if (SimpleExoPlayer.this.videoDebugListener != null) {
        SimpleExoPlayer.this.videoDebugListener.onRenderedFirstFrame(paramSurface);
      }
    }
    
    public void onSurfaceTextureAvailable(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2)
    {
      SimpleExoPlayer.this.setVideoSurfaceInternal(new Surface(paramSurfaceTexture), true);
    }
    
    public boolean onSurfaceTextureDestroyed(SurfaceTexture paramSurfaceTexture)
    {
      SimpleExoPlayer.this.setVideoSurfaceInternal(null, true);
      return true;
    }
    
    public void onSurfaceTextureSizeChanged(SurfaceTexture paramSurfaceTexture, int paramInt1, int paramInt2) {}
    
    public void onSurfaceTextureUpdated(SurfaceTexture paramSurfaceTexture) {}
    
    public void onVideoDecoderInitialized(String paramString, long paramLong1, long paramLong2)
    {
      if (SimpleExoPlayer.this.videoDebugListener != null) {
        SimpleExoPlayer.this.videoDebugListener.onVideoDecoderInitialized(paramString, paramLong1, paramLong2);
      }
    }
    
    public void onVideoDisabled(DecoderCounters paramDecoderCounters)
    {
      if (SimpleExoPlayer.this.videoDebugListener != null) {
        SimpleExoPlayer.this.videoDebugListener.onVideoDisabled(paramDecoderCounters);
      }
      SimpleExoPlayer.access$302(SimpleExoPlayer.this, null);
      SimpleExoPlayer.access$102(SimpleExoPlayer.this, null);
    }
    
    public void onVideoEnabled(DecoderCounters paramDecoderCounters)
    {
      SimpleExoPlayer.access$102(SimpleExoPlayer.this, paramDecoderCounters);
      if (SimpleExoPlayer.this.videoDebugListener != null) {
        SimpleExoPlayer.this.videoDebugListener.onVideoEnabled(paramDecoderCounters);
      }
    }
    
    public void onVideoInputFormatChanged(Format paramFormat)
    {
      SimpleExoPlayer.access$302(SimpleExoPlayer.this, paramFormat);
      if (SimpleExoPlayer.this.videoDebugListener != null) {
        SimpleExoPlayer.this.videoDebugListener.onVideoInputFormatChanged(paramFormat);
      }
    }
    
    public void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
    {
      if (SimpleExoPlayer.this.videoListener != null) {
        SimpleExoPlayer.this.videoListener.onVideoSizeChanged(paramInt1, paramInt2, paramInt3, paramFloat);
      }
      if (SimpleExoPlayer.this.videoDebugListener != null) {
        SimpleExoPlayer.this.videoDebugListener.onVideoSizeChanged(paramInt1, paramInt2, paramInt3, paramFloat);
      }
    }
    
    public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3) {}
    
    public void surfaceCreated(SurfaceHolder paramSurfaceHolder)
    {
      SimpleExoPlayer.this.setVideoSurfaceInternal(paramSurfaceHolder.getSurface(), false);
    }
    
    public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder)
    {
      SimpleExoPlayer.this.setVideoSurfaceInternal(null, false);
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface ExtensionRendererMode {}
  
  @TargetApi(23)
  private static final class PlaybackParamsHolder
  {
    public final PlaybackParams params;
    
    public PlaybackParamsHolder(PlaybackParams paramPlaybackParams)
    {
      this.params = paramPlaybackParams;
    }
  }
  
  public static abstract interface VideoListener
  {
    public abstract void onRenderedFirstFrame();
    
    public abstract void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/exoplayer2/SimpleExoPlayer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */