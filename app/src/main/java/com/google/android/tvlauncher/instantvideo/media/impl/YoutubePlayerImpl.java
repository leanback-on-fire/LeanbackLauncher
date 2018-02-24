package com.google.android.tvlauncher.instantvideo.media.impl;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.WorkerThread;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.android.tvlauncher.instantvideo.media.MediaPlayer;
import com.google.android.tvlauncher.instantvideo.media.MediaPlayer.VideoCallback;
import java.util.List;

public class YoutubePlayerImpl
  implements MediaPlayer
{
  private static final byte[] BUFFER = new byte['Ѐ'];
  private static final boolean DEBUG = false;
  private static final int DEFAULT_DISPLAY_HEIGHT = 524;
  private static final int DEFAULT_DISPLAY_WIDTH = 932;
  private static final String JAVA_SCRIPT_BRIDGE_TAG = "Android";
  private static final String TAG = "YoutubePlayerImpl";
  private static final String TAG_DISPLAY_HEIGHT = "<display_height>";
  private static final String TAG_DISPLAY_WIDTH = "<display_width>";
  private static final String TAG_VIDEO_ID = "<video_id>";
  private static final int WEBVIEW_STATE_LOADED = 3;
  private static final int WEBVIEW_STATE_LOADING = 1;
  private static final int WEBVIEW_STATE_LOADING_BACKGROUND = 2;
  private static final int WEBVIEW_STATE_NONE = 0;
  private static final int YOUTUBE_STATE_BUFFERING = 3;
  private static final int YOUTUBE_STATE_ENDED = 0;
  private static final int YOUTUBE_STATE_PAUSED = 2;
  private static final int YOUTUBE_STATE_PLAYING = 1;
  private static final int YOUTUBE_STATE_UNSTARTED = -1;
  private static String sYoutubeHtmlTemplate;
  private Context mContext;
  private int mDisplayHeight;
  private boolean mDisplaySizeSet;
  private int mDisplayWidth;
  private boolean mFirstFrameDrawn;
  private Handler mHandler = new Handler();
  private boolean mPlayWhenReady;
  private int mState = 1;
  private MediaPlayer.VideoCallback mVideoCallback;
  private String mVideoId;
  private final WebView mWebView;
  private int mWebViewState;
  private Uri mYoutubeUri;
  
  public YoutubePlayerImpl(Context paramContext)
  {
    this.mContext = paramContext;
    if (sYoutubeHtmlTemplate == null) {
      sYoutubeHtmlTemplate = readYoutubeHtmlTemplate();
    }
    if (sYoutubeHtmlTemplate == null) {
      throw new IllegalStateException("Failed to read youtube html template");
    }
    this.mWebViewState = 0;
    this.mWebView = new WebView(this.mContext);
    paramContext = this.mWebView.getSettings();
    paramContext.setJavaScriptEnabled(true);
    paramContext.setJavaScriptCanOpenWindowsAutomatically(true);
    paramContext.setLoadWithOverviewMode(true);
    paramContext.setUseWideViewPort(true);
    paramContext.setAllowFileAccess(true);
    paramContext.setAllowUniversalAccessFromFileURLs(true);
    paramContext.setMediaPlaybackRequiresUserGesture(false);
    paramContext.setCacheMode(1);
    paramContext.setAppCacheEnabled(true);
    paramContext.setDomStorageEnabled(true);
    paramContext.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
    this.mWebView.setVerticalScrollBarEnabled(false);
    this.mWebView.setHorizontalScrollBarEnabled(false);
    this.mWebView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
  }
  
  private void callJavaScript(String paramString)
  {
    this.mWebView.evaluateJavascript(paramString, null);
  }
  
  public static boolean isYoutubeUri(Uri paramUri)
  {
    if (paramUri == null) {
      return false;
    }
    List localList = paramUri.getPathSegments();
    paramUri = paramUri.getAuthority();
    if ((paramUri != null) && (paramUri.equals("www.youtube.com")) && (localList.size() == 1) && (((String)localList.get(0)).equals("watch"))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  /* Error */
  private String readYoutubeHtmlTemplate()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 90	com/google/android/tvlauncher/instantvideo/media/impl/YoutubePlayerImpl:mContext	Landroid/content/Context;
    //   4: invokevirtual 250	android/content/Context:getAssets	()Landroid/content/res/AssetManager;
    //   7: ldc -4
    //   9: invokevirtual 258	android/content/res/AssetManager:open	(Ljava/lang/String;)Ljava/io/InputStream;
    //   12: astore_2
    //   13: new 260	java/io/ByteArrayOutputStream
    //   16: dup
    //   17: invokespecial 261	java/io/ByteArrayOutputStream:<init>	()V
    //   20: astore_3
    //   21: aload_2
    //   22: getstatic 76	com/google/android/tvlauncher/instantvideo/media/impl/YoutubePlayerImpl:BUFFER	[B
    //   25: invokevirtual 267	java/io/InputStream:read	([B)I
    //   28: istore_1
    //   29: iload_1
    //   30: iflt +30 -> 60
    //   33: aload_3
    //   34: getstatic 76	com/google/android/tvlauncher/instantvideo/media/impl/YoutubePlayerImpl:BUFFER	[B
    //   37: iconst_0
    //   38: iload_1
    //   39: invokevirtual 271	java/io/ByteArrayOutputStream:write	([BII)V
    //   42: aload_2
    //   43: getstatic 76	com/google/android/tvlauncher/instantvideo/media/impl/YoutubePlayerImpl:BUFFER	[B
    //   46: invokevirtual 267	java/io/InputStream:read	([B)I
    //   49: istore_1
    //   50: goto -21 -> 29
    //   53: astore_2
    //   54: aload_2
    //   55: invokevirtual 274	java/io/IOException:printStackTrace	()V
    //   58: aconst_null
    //   59: areturn
    //   60: aload_2
    //   61: invokevirtual 277	java/io/InputStream:close	()V
    //   64: aload_3
    //   65: invokevirtual 280	java/io/ByteArrayOutputStream:toString	()Ljava/lang/String;
    //   68: areturn
    //   69: astore_2
    //   70: aconst_null
    //   71: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	72	0	this	YoutubePlayerImpl
    //   28	22	1	i	int
    //   12	31	2	localInputStream	java.io.InputStream
    //   53	8	2	localIOException1	java.io.IOException
    //   69	1	2	localIOException2	java.io.IOException
    //   20	45	3	localByteArrayOutputStream	java.io.ByteArrayOutputStream
    // Exception table:
    //   from	to	target	type
    //   0	13	53	java/io/IOException
    //   21	29	69	java/io/IOException
    //   33	50	69	java/io/IOException
    //   60	64	69	java/io/IOException
  }
  
  public int getCurrentPosition()
  {
    return 0;
  }
  
  public int getPlaybackState()
  {
    return this.mState;
  }
  
  public View getPlayerView()
  {
    return this.mWebView;
  }
  
  public Uri getVideoUri()
  {
    return this.mYoutubeUri;
  }
  
  @JavascriptInterface
  @WorkerThread
  public void onJsPlayerChangeState(final int paramInt)
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        if (!YoutubePlayerImpl.this.mDisplaySizeSet)
        {
          if ((YoutubePlayerImpl.this.mDisplayWidth != 0) && (YoutubePlayerImpl.this.mDisplayHeight != 0)) {
            YoutubePlayerImpl.this.callJavaScript("player.setSize(" + YoutubePlayerImpl.this.mDisplayWidth + "," + YoutubePlayerImpl.this.mDisplayHeight + ");");
          }
          YoutubePlayerImpl.access$302(YoutubePlayerImpl.this, true);
        }
        if ((paramInt == 1) || (paramInt == 2)) {
          if (!YoutubePlayerImpl.this.mFirstFrameDrawn)
          {
            if (YoutubePlayerImpl.this.mVideoCallback != null) {
              YoutubePlayerImpl.this.mVideoCallback.onVideoAvailable();
            }
            YoutubePlayerImpl.access$702(YoutubePlayerImpl.this, true);
          }
        }
        do
        {
          YoutubePlayerImpl.access$902(YoutubePlayerImpl.this, 3);
          do
          {
            return;
          } while (paramInt != 0);
          YoutubePlayerImpl.access$902(YoutubePlayerImpl.this, 4);
        } while (YoutubePlayerImpl.this.mVideoCallback == null);
        YoutubePlayerImpl.this.mVideoCallback.onVideoEnded();
      }
    });
  }
  
  @JavascriptInterface
  @WorkerThread
  public void onJsPlayerError(final int paramInt)
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        YoutubePlayerImpl.this.stop();
        if (YoutubePlayerImpl.this.mVideoCallback != null) {
          YoutubePlayerImpl.this.mVideoCallback.onVideoError();
        }
      }
    });
  }
  
  public void prepare()
  {
    this.mState = 2;
    this.mDisplaySizeSet = true;
    this.mFirstFrameDrawn = false;
    if (this.mWebViewState == 0)
    {
      this.mWebView.addJavascriptInterface(this, "Android");
      String str = sYoutubeHtmlTemplate.replace("<video_id>", this.mVideoId).replace("<display_width>", Integer.toString(this.mDisplayWidth)).replace("<display_height>", Integer.toString(this.mDisplayHeight));
      this.mWebView.loadDataWithBaseURL("", str, "text/html", "UTF-8", null);
      this.mWebView.setInitialScale(100);
      this.mWebViewState = 1;
      this.mWebView.setWebViewClient(new WebViewClient()
      {
        public void onPageFinished(WebView paramAnonymousWebView, String paramAnonymousString)
        {
          int i = YoutubePlayerImpl.this.mWebViewState;
          YoutubePlayerImpl.access$002(YoutubePlayerImpl.this, 3);
          super.onPageFinished(paramAnonymousWebView, paramAnonymousString);
          YoutubePlayerImpl.this.mWebView.zoomBy(1.0F);
          if (i == 1) {
            YoutubePlayerImpl.this.setPlayWhenReady(YoutubePlayerImpl.this.mPlayWhenReady);
          }
        }
      });
      return;
    }
    this.mWebView.setInitialScale(100);
  }
  
  public void release()
  {
    if (this.mWebViewState != 0)
    {
      this.mWebView.removeJavascriptInterface("Android");
      this.mWebView.loadUrl("about:blank");
    }
    this.mYoutubeUri = null;
    this.mVideoId = null;
    this.mDisplayWidth = 0;
    this.mDisplayHeight = 0;
    this.mDisplaySizeSet = false;
    this.mFirstFrameDrawn = false;
    this.mWebViewState = 0;
    this.mVideoCallback = null;
    this.mState = 1;
  }
  
  public void seekTo(int paramInt) {}
  
  public void setDisplaySize(int paramInt1, int paramInt2)
  {
    this.mDisplayWidth = paramInt1;
    this.mDisplayHeight = paramInt2;
    if (this.mState == 3)
    {
      this.mWebView.evaluateJavascript("player.setSize(" + paramInt1 + "," + paramInt2 + ");", null);
      this.mDisplaySizeSet = true;
      return;
    }
    this.mDisplaySizeSet = false;
  }
  
  public void setPlayWhenReady(boolean paramBoolean)
  {
    this.mPlayWhenReady = paramBoolean;
    if (this.mWebViewState != 3) {
      return;
    }
    if (paramBoolean)
    {
      callJavaScript("player.playVideo();");
      return;
    }
    callJavaScript("player.pauseVideo();");
  }
  
  public void setVideoCallback(MediaPlayer.VideoCallback paramVideoCallback)
  {
    this.mVideoCallback = paramVideoCallback;
  }
  
  public void setVideoUri(Uri paramUri)
  {
    if (!isYoutubeUri(paramUri)) {
      throw new IllegalArgumentException("Malformed youtube uri:" + paramUri);
    }
    this.mYoutubeUri = paramUri;
    this.mVideoId = this.mYoutubeUri.getQueryParameter("v");
    this.mDisplayWidth = 932;
    this.mDisplayHeight = 524;
  }
  
  public void setVolume(float paramFloat)
  {
    float f;
    if (paramFloat < 0.0F) {
      f = 0.0F;
    }
    for (;;)
    {
      int i = (int)(f / 1.0F * 100.0F);
      callJavaScript("player.setVolume(" + i + ");");
      return;
      f = paramFloat;
      if (paramFloat > 1.0F) {
        f = 1.0F;
      }
    }
  }
  
  public void stop()
  {
    if (this.mWebViewState == 1) {
      this.mWebViewState = 2;
    }
    if (this.mWebViewState != 3) {
      return;
    }
    callJavaScript("player.seekTo(0, false);player.pauseVideo();");
    this.mState = 1;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/instantvideo/media/impl/YoutubePlayerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */