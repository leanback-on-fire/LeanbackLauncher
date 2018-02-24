package com.google.android.gsf;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WebAddAccountView
{
  private static final String TAG = "WebAddAccountView";
  private View mBackButton;
  private View mBottomBar;
  private Callback mCallback;
  private View mCancelButton;
  private String mDomainName;
  private boolean mIsLoading;
  private ProgressBar mProgressBar;
  private View mProgressView;
  private String mStartUrl;
  private TextView mTitleTextView;
  private View mTitleView;
  private WebView mWebView;
  
  public WebAddAccountView(WebView paramWebView, View paramView1, ProgressBar paramProgressBar, View paramView2, View paramView3, View paramView4, TextView paramTextView, View paramView5, Callback paramCallback)
  {
    this.mWebView = paramWebView;
    this.mProgressView = paramView1;
    this.mProgressBar = paramProgressBar;
    this.mProgressBar.setMax(100);
    this.mCallback = paramCallback;
    this.mBackButton = paramView2;
    this.mCancelButton = paramView3;
    this.mTitleView = paramView4;
    this.mTitleTextView = paramTextView;
    this.mBottomBar = paramView5;
    setupOptionsAndCallbacks();
  }
  
  private void doLogin()
  {
    this.mTitleView.setVisibility(8);
    this.mWebView.setVisibility(8);
    this.mBackButton.setVisibility(8);
    this.mCancelButton.setVisibility(0);
    this.mProgressBar.setProgress(0);
    this.mProgressView.setVisibility(0);
    this.mWebView.loadUrl(this.mStartUrl);
    this.mIsLoading = true;
  }
  
  private void setupOptionsAndCallbacks()
  {
    this.mWebView.setWebViewClient(new MyWebViewClient(null));
    this.mWebView.setWebChromeClient(new MyChromeClient(null));
    WebSettings localWebSettings = this.mWebView.getSettings();
    localWebSettings.setJavaScriptEnabled(true);
    localWebSettings.setSupportMultipleWindows(false);
    localWebSettings.setSaveFormData(false);
    localWebSettings.setSavePassword(false);
    localWebSettings.setAllowFileAccess(false);
    localWebSettings.setDatabaseEnabled(false);
    localWebSettings.setJavaScriptCanOpenWindowsAutomatically(false);
    localWebSettings.setLoadsImagesAutomatically(true);
    localWebSettings.setLightTouchEnabled(false);
    localWebSettings.setNeedInitialFocus(false);
    localWebSettings.setUseWideViewPort(true);
    this.mWebView.setMapTrackballToArrowKeys(false);
    this.mWebView.setFocusable(true);
    this.mWebView.setFocusableInTouchMode(true);
  }
  
  public void login(String paramString1, String paramString2)
  {
    this.mStartUrl = paramString1;
    this.mDomainName = paramString2;
    doLogin();
  }
  
  public void stop()
  {
    if (this.mIsLoading)
    {
      this.mWebView.stopLoading();
      this.mIsLoading = false;
    }
  }
  
  public static abstract interface Callback
  {
    public abstract void onWebLoginCompleted(String paramString);
    
    public abstract void onWebLoginError(WebAddAccountView.Error paramError, int paramInt, String paramString);
  }
  
  public static enum Error
  {
    HttpError,  TooManyRedirects;
    
    private Error() {}
  }
  
  private class MyChromeClient
    extends WebChromeClient
  {
    private MyChromeClient() {}
    
    public boolean onCreateWindow(WebView paramWebView, boolean paramBoolean1, boolean paramBoolean2, Message paramMessage)
    {
      Log.v("WebAddAccountView", "onCreateWindow");
      paramMessage.obj = WebAddAccountView.this.mWebView;
      paramMessage.sendToTarget();
      return true;
    }
    
    public void onProgressChanged(WebView paramWebView, int paramInt)
    {
      WebAddAccountView.this.mProgressBar.setProgress(paramInt);
    }
  }
  
  private class MyWebViewClient
    extends WebViewClient
  {
    private boolean mOAuthDone = false;
    private String mOAuthUrl;
    
    private MyWebViewClient() {}
    
    private boolean foundOauthURL(WebView paramWebView, String paramString)
    {
      if (Uri.parse(paramString).getScheme().compareTo("oauth") == 0)
      {
        Log.i("WebAddAccountView", "We will handle oauth:gls URL " + paramString);
        this.mOAuthDone = true;
        this.mOAuthUrl = paramString;
        return maybeFinish(paramWebView);
      }
      return false;
    }
    
    private void hideWebUI()
    {
      CookieManager.getInstance().removeAllCookie();
      WebAddAccountView.this.mWebView.clearView();
      WebAddAccountView.this.mWebView.setVisibility(8);
      WebAddAccountView.this.mTitleView.setVisibility(8);
      WebAddAccountView.this.mBottomBar.setVisibility(0);
    }
    
    private boolean maybeFinish(WebView paramWebView)
    {
      if (!this.mOAuthDone) {
        return false;
      }
      paramWebView.stopLoading();
      WebAddAccountView.access$402(WebAddAccountView.this, false);
      hideWebUI();
      WebAddAccountView.this.mCallback.onWebLoginCompleted(this.mOAuthUrl);
      return true;
    }
    
    public void onPageFinished(WebView paramWebView, String paramString)
    {
      if (!WebAddAccountView.this.mIsLoading)
      {
        Log.i("WebAddAccountView", "Web view ingoring loaded url " + paramString);
        return;
      }
      if (!maybeFinish(paramWebView))
      {
        Log.v("WebAddAccountView", "Not finished at " + paramString);
        super.onPageFinished(paramWebView, paramString);
        WebAddAccountView.this.mProgressView.setVisibility(8);
        WebAddAccountView.this.mTitleView.setVisibility(0);
        paramString = Uri.parse(paramString);
        paramWebView = "";
        if ("https".equalsIgnoreCase(paramString.getScheme())) {
          paramWebView = "" + "https://";
        }
        String str = WebAddAccountView.this.mWebView.getTitle();
        if (TextUtils.isEmpty(str)) {}
        for (paramWebView = paramWebView + paramString.getAuthority();; paramWebView = paramWebView + paramString.getAuthority() + " : " + str)
        {
          WebAddAccountView.this.mTitleTextView.setText(paramWebView);
          WebAddAccountView.this.mBottomBar.setVisibility(8);
          WebAddAccountView.this.mCancelButton.setVisibility(8);
          WebAddAccountView.this.mWebView.setVisibility(0);
          WebAddAccountView.this.mWebView.requestFocus();
          return;
        }
      }
      Log.v("WebAddAccountView", "Finished at " + paramString);
    }
    
    public void onPageStarted(WebView paramWebView, String paramString, Bitmap paramBitmap)
    {
      StringBuilder localStringBuilder = new StringBuilder().append("onPageStarted ").append(paramString);
      String str;
      if (WebAddAccountView.this.mIsLoading)
      {
        str = " - loading";
        Log.i("WebAddAccountView", str);
        if (!foundOauthURL(paramWebView, paramString)) {
          break label65;
        }
      }
      label65:
      do
      {
        return;
        str = " - not loading";
        break;
        if (!WebAddAccountView.this.mIsLoading)
        {
          WebAddAccountView.this.mWebView.stopLoading();
          return;
        }
      } while (maybeFinish(paramWebView));
      super.onPageStarted(paramWebView, paramString, paramBitmap);
    }
    
    public void onReceivedError(WebView paramWebView, int paramInt, String paramString1, String paramString2)
    {
      Log.w("WebAddAccountView", "onReceivedError " + paramString1);
      WebAddAccountView.access$402(WebAddAccountView.this, false);
      hideWebUI();
      WebAddAccountView.this.mCallback.onWebLoginError(WebAddAccountView.Error.HttpError, paramInt, paramString1);
      super.onReceivedError(paramWebView, paramInt, paramString1, paramString2);
    }
    
    public void onTooManyRedirects(WebView paramWebView, Message paramMessage1, Message paramMessage2)
    {
      Log.e("WebAddAccountView", "onTooManyRedirects");
      hideWebUI();
      super.onTooManyRedirects(paramWebView, paramMessage1, paramMessage2);
      WebAddAccountView.this.mCallback.onWebLoginError(WebAddAccountView.Error.TooManyRedirects, 0, "");
    }
    
    public boolean shouldOverrideUrlLoading(WebView paramWebView, String paramString)
    {
      Log.i("WebAddAccountView", "Web view is loading " + paramString);
      return foundOauthURL(paramWebView, paramString);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/gsf/WebAddAccountView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */