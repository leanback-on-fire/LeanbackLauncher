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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class YoutubePlayerImpl implements MediaPlayer {
    private static final byte[] BUFFER = new byte[1024];
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
    private VideoCallback mVideoCallback;
    private String mVideoId;
    private final WebView mWebView;
    private int mWebViewState;
    private Uri mYoutubeUri;

    public YoutubePlayerImpl(Context context) {
        this.mContext = context;
        if (sYoutubeHtmlTemplate == null) {
            sYoutubeHtmlTemplate = readYoutubeHtmlTemplate();
        }
        if (sYoutubeHtmlTemplate == null) {
            throw new IllegalStateException("Failed to read youtube html template");
        }
        this.mWebViewState = 0;
        this.mWebView = new WebView(this.mContext);
        WebSettings settings = this.mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setAllowFileAccess(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setCacheMode(1);
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        this.mWebView.setVerticalScrollBarEnabled(false);
        this.mWebView.setHorizontalScrollBarEnabled(false);
        this.mWebView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    public int getPlaybackState() {
        return this.mState;
    }

    public void setVideoUri(Uri uri) {
        if (isYoutubeUri(uri)) {
            this.mYoutubeUri = uri;
            this.mVideoId = this.mYoutubeUri.getQueryParameter("v");
            this.mDisplayWidth = DEFAULT_DISPLAY_WIDTH;
            this.mDisplayHeight = DEFAULT_DISPLAY_HEIGHT;
            return;
        }
        throw new IllegalArgumentException("Malformed youtube uri:" + uri);
    }

    public Uri getVideoUri() {
        return this.mYoutubeUri;
    }

    public void prepare() {
        this.mState = 2;
        this.mDisplaySizeSet = true;
        this.mFirstFrameDrawn = false;
        if (this.mWebViewState == 0) {
            this.mWebView.addJavascriptInterface(this, JAVA_SCRIPT_BRIDGE_TAG);
            this.mWebView.loadDataWithBaseURL("", sYoutubeHtmlTemplate.replace(TAG_VIDEO_ID, this.mVideoId).replace(TAG_DISPLAY_WIDTH, Integer.toString(this.mDisplayWidth)).replace(TAG_DISPLAY_HEIGHT, Integer.toString(this.mDisplayHeight)), "text/html", "UTF-8", null);
            this.mWebView.setInitialScale(100);
            this.mWebViewState = 1;
            this.mWebView.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView view, String url) {
                    int previousState = YoutubePlayerImpl.this.mWebViewState;
                    YoutubePlayerImpl.this.mWebViewState = 3;
                    super.onPageFinished(view, url);
                    YoutubePlayerImpl.this.mWebView.zoomBy(1.0f);
                    if (previousState == 1) {
                        YoutubePlayerImpl.this.setPlayWhenReady(YoutubePlayerImpl.this.mPlayWhenReady);
                    }
                }
            });
            return;
        }
        this.mWebView.setInitialScale(100);
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        this.mPlayWhenReady = playWhenReady;
        if (this.mWebViewState == 3) {
            if (playWhenReady) {
                callJavaScript("player.playVideo();");
            } else {
                callJavaScript("player.pauseVideo();");
            }
        }
    }

    public void stop() {
        if (this.mWebViewState == 1) {
            this.mWebViewState = 2;
        }
        if (this.mWebViewState == 3) {
            callJavaScript("player.seekTo(0, false);player.pauseVideo();");
            this.mState = 1;
        }
    }

    public void seekTo(int positionMs) {
    }

    public void setDisplaySize(int width, int height) {
        this.mDisplayWidth = width;
        this.mDisplayHeight = height;
        if (this.mState == 3) {
            this.mWebView.evaluateJavascript("player.setSize(" + width + "," + height + ");", null);
            this.mDisplaySizeSet = true;
            return;
        }
        this.mDisplaySizeSet = false;
    }

    public int getCurrentPosition() {
        return 0;
    }

    public void setVolume(float volume) {
        if (volume < 0.0f) {
            volume = 0.0f;
        } else if (volume > 1.0f) {
            volume = 1.0f;
        }
        callJavaScript("player.setVolume(" + ((int) ((volume / 1.0f) * 100.0f)) + ");");
    }

    public View getPlayerView() {
        return this.mWebView;
    }

    public void setVideoCallback(VideoCallback callback) {
        this.mVideoCallback = callback;
    }

    @WorkerThread
    @JavascriptInterface
    public void onJsPlayerChangeState(final int playerState) {
        this.mHandler.post(new Runnable() {
            public void run() {
                if (!YoutubePlayerImpl.this.mDisplaySizeSet) {
                    if (!(YoutubePlayerImpl.this.mDisplayWidth == 0 || YoutubePlayerImpl.this.mDisplayHeight == 0)) {
                        YoutubePlayerImpl.this.callJavaScript("player.setSize(" + YoutubePlayerImpl.this.mDisplayWidth + "," + YoutubePlayerImpl.this.mDisplayHeight + ");");
                    }
                    YoutubePlayerImpl.this.mDisplaySizeSet = true;
                }
                if (playerState == 1 || playerState == 2) {
                    if (!YoutubePlayerImpl.this.mFirstFrameDrawn) {
                        if (YoutubePlayerImpl.this.mVideoCallback != null) {
                            YoutubePlayerImpl.this.mVideoCallback.onVideoAvailable();
                        }
                        YoutubePlayerImpl.this.mFirstFrameDrawn = true;
                    }
                    YoutubePlayerImpl.this.mState = 3;
                } else if (playerState == 0) {
                    YoutubePlayerImpl.this.mState = 4;
                    if (YoutubePlayerImpl.this.mVideoCallback != null) {
                        YoutubePlayerImpl.this.mVideoCallback.onVideoEnded();
                    }
                }
            }
        });
    }

    @WorkerThread
    @JavascriptInterface
    public void onJsPlayerError(final int errorCode) {
        this.mHandler.post(new Runnable() {
            public void run() {
                YoutubePlayerImpl.this.stop();
                if (YoutubePlayerImpl.this.mVideoCallback != null) {
                    YoutubePlayerImpl.this.mVideoCallback.onVideoError();
                }
            }
        });
    }

    private void callJavaScript(String command) {
        this.mWebView.evaluateJavascript(command, null);
    }

    private String readYoutubeHtmlTemplate() {
        try {
            InputStream inputStream = this.mContext.getAssets().open("youtube_template.html");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                for (int readSize = inputStream.read(BUFFER); readSize >= 0; readSize = inputStream.read(BUFFER)) {
                    byteArrayOutputStream.write(BUFFER, 0, readSize);
                }
                inputStream.close();
                return byteArrayOutputStream.toString();
            } catch (IOException e) {
                return null;
            }
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static boolean isYoutubeUri(Uri uri) {
        if (uri == null) {
            return false;
        }
        List<String> segments = uri.getPathSegments();
        String authority = uri.getAuthority();
        boolean z = authority != null && authority.equals("www.youtube.com") && segments.size() == 1 && ((String) segments.get(0)).equals("watch");
        return z;
    }

    public void release() {
        if (this.mWebViewState != 0) {
            this.mWebView.removeJavascriptInterface(JAVA_SCRIPT_BRIDGE_TAG);
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
}
