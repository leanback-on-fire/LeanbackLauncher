package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.SoundPool;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.v17.leanback.R.color;
import android.support.v17.leanback.R.dimen;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.integer;
import android.support.v17.leanback.R.layout;
import android.support.v17.leanback.R.raw;
import android.support.v17.leanback.R.string;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SearchBar
  extends RelativeLayout
{
  static final boolean DEBUG = false;
  static final int DEFAULT_PRIORITY = 1;
  static final float DEFAULT_RATE = 1.0F;
  static final int DO_NOT_LOOP = 0;
  static final float FULL_LEFT_VOLUME = 1.0F;
  static final float FULL_RIGHT_VOLUME = 1.0F;
  static final String TAG = SearchBar.class.getSimpleName();
  private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener()
  {
    public void onAudioFocusChange(int paramAnonymousInt)
    {
      SearchBar.this.stopRecognition();
    }
  };
  private AudioManager mAudioManager;
  boolean mAutoStartRecognition = false;
  private int mBackgroundAlpha;
  private int mBackgroundSpeechAlpha;
  private Drawable mBadgeDrawable;
  private ImageView mBadgeView;
  private Drawable mBarBackground;
  private int mBarHeight;
  private final Context mContext;
  final Handler mHandler = new Handler();
  private String mHint;
  private final InputMethodManager mInputMethodManager;
  private boolean mListening;
  private SearchBarPermissionListener mPermissionListener;
  boolean mRecognizing = false;
  SearchBarListener mSearchBarListener;
  String mSearchQuery;
  SearchEditText mSearchTextEditor;
  SparseIntArray mSoundMap = new SparseIntArray();
  SoundPool mSoundPool;
  SpeechOrbView mSpeechOrbView;
  private SpeechRecognitionCallback mSpeechRecognitionCallback;
  private SpeechRecognizer mSpeechRecognizer;
  private final int mTextColor;
  private final int mTextColorSpeechMode;
  private final int mTextHintColor;
  private final int mTextHintColorSpeechMode;
  private String mTitle;
  
  public SearchBar(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SearchBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public SearchBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.mContext = paramContext;
    paramAttributeSet = getResources();
    LayoutInflater.from(getContext()).inflate(R.layout.lb_search_bar, this, true);
    this.mBarHeight = getResources().getDimensionPixelSize(R.dimen.lb_search_bar_height);
    RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(-1, this.mBarHeight);
    localLayoutParams.addRule(10, -1);
    setLayoutParams(localLayoutParams);
    setBackgroundColor(0);
    setClipChildren(false);
    this.mSearchQuery = "";
    this.mInputMethodManager = ((InputMethodManager)paramContext.getSystemService("input_method"));
    this.mTextColorSpeechMode = paramAttributeSet.getColor(R.color.lb_search_bar_text_speech_mode);
    this.mTextColor = paramAttributeSet.getColor(R.color.lb_search_bar_text);
    this.mBackgroundSpeechAlpha = paramAttributeSet.getInteger(R.integer.lb_search_bar_speech_mode_background_alpha);
    this.mBackgroundAlpha = paramAttributeSet.getInteger(R.integer.lb_search_bar_text_mode_background_alpha);
    this.mTextHintColorSpeechMode = paramAttributeSet.getColor(R.color.lb_search_bar_hint_speech_mode);
    this.mTextHintColor = paramAttributeSet.getColor(R.color.lb_search_bar_hint);
    this.mAudioManager = ((AudioManager)paramContext.getSystemService("audio"));
  }
  
  private boolean isVoiceMode()
  {
    return this.mSpeechOrbView.isFocused();
  }
  
  private void loadSounds(Context paramContext)
  {
    int i = 0;
    int[] arrayOfInt = new int[4];
    arrayOfInt[0] = R.raw.lb_voice_failure;
    arrayOfInt[1] = R.raw.lb_voice_open;
    arrayOfInt[2] = R.raw.lb_voice_no_input;
    arrayOfInt[3] = R.raw.lb_voice_success;
    int j = arrayOfInt.length;
    while (i < j)
    {
      int k = arrayOfInt[i];
      this.mSoundMap.put(k, this.mSoundPool.load(paramContext, k, 1));
      i += 1;
    }
  }
  
  private void play(final int paramInt)
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        int i = SearchBar.this.mSoundMap.get(paramInt);
        SearchBar.this.mSoundPool.play(i, 1.0F, 1.0F, 1, 0, 1.0F);
      }
    });
  }
  
  private void playSearchNoInput()
  {
    play(R.raw.lb_voice_no_input);
  }
  
  private void updateHint()
  {
    String str = getResources().getString(R.string.lb_search_bar_hint);
    if (!TextUtils.isEmpty(this.mTitle)) {
      if (isVoiceMode()) {
        str = getResources().getString(R.string.lb_search_bar_hint_with_title_speech, new Object[] { this.mTitle });
      }
    }
    for (;;)
    {
      this.mHint = str;
      if (this.mSearchTextEditor != null) {
        this.mSearchTextEditor.setHint(this.mHint);
      }
      return;
      str = getResources().getString(R.string.lb_search_bar_hint_with_title, new Object[] { this.mTitle });
      continue;
      if (isVoiceMode()) {
        str = getResources().getString(R.string.lb_search_bar_hint_speech);
      }
    }
  }
  
  public void displayCompletions(List<String> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    if (paramList != null)
    {
      paramList = paramList.iterator();
      while (paramList.hasNext())
      {
        String str = (String)paramList.next();
        localArrayList.add(new CompletionInfo(localArrayList.size(), localArrayList.size(), str));
      }
    }
    displayCompletions((CompletionInfo[])localArrayList.toArray(new CompletionInfo[localArrayList.size()]));
  }
  
  public void displayCompletions(CompletionInfo[] paramArrayOfCompletionInfo)
  {
    this.mInputMethodManager.displayCompletions(this.mSearchTextEditor, paramArrayOfCompletionInfo);
  }
  
  public Drawable getBadgeDrawable()
  {
    return this.mBadgeDrawable;
  }
  
  public CharSequence getHint()
  {
    return this.mHint;
  }
  
  public String getTitle()
  {
    return this.mTitle;
  }
  
  void hideNativeKeyboard()
  {
    this.mInputMethodManager.hideSoftInputFromWindow(this.mSearchTextEditor.getWindowToken(), 0);
  }
  
  public boolean isRecognizing()
  {
    return this.mRecognizing;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.mSoundPool = new SoundPool(2, 1, 0);
    loadSounds(this.mContext);
  }
  
  protected void onDetachedFromWindow()
  {
    stopRecognition();
    this.mSoundPool.release();
    super.onDetachedFromWindow();
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mBarBackground = ((RelativeLayout)findViewById(R.id.lb_search_bar_items)).getBackground();
    this.mSearchTextEditor = ((SearchEditText)findViewById(R.id.lb_search_text_editor));
    this.mBadgeView = ((ImageView)findViewById(R.id.lb_search_bar_badge));
    if (this.mBadgeDrawable != null) {
      this.mBadgeView.setImageDrawable(this.mBadgeDrawable);
    }
    this.mSearchTextEditor.setOnFocusChangeListener(new View.OnFocusChangeListener()
    {
      public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
      {
        if (paramAnonymousBoolean) {
          SearchBar.this.showNativeKeyboard();
        }
        SearchBar.this.updateUi(paramAnonymousBoolean);
      }
    });
    final Runnable local3 = new Runnable()
    {
      public void run()
      {
        SearchBar.this.setSearchQueryInternal(SearchBar.this.mSearchTextEditor.getText().toString());
      }
    };
    this.mSearchTextEditor.addTextChangedListener(new TextWatcher()
    {
      public void afterTextChanged(Editable paramAnonymousEditable) {}
      
      public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      
      public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
      {
        if (SearchBar.this.mRecognizing) {
          return;
        }
        SearchBar.this.mHandler.removeCallbacks(local3);
        SearchBar.this.mHandler.post(local3);
      }
    });
    this.mSearchTextEditor.setOnKeyboardDismissListener(new SearchEditText.OnKeyboardDismissListener()
    {
      public void onKeyboardDismiss()
      {
        if (SearchBar.this.mSearchBarListener != null) {
          SearchBar.this.mSearchBarListener.onKeyboardDismiss(SearchBar.this.mSearchQuery);
        }
      }
    });
    this.mSearchTextEditor.setOnEditorActionListener(new TextView.OnEditorActionListener()
    {
      public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
      {
        if (((3 == paramAnonymousInt) || (paramAnonymousInt == 0)) && (SearchBar.this.mSearchBarListener != null))
        {
          SearchBar.this.hideNativeKeyboard();
          SearchBar.this.mHandler.postDelayed(new Runnable()
          {
            public void run()
            {
              SearchBar.this.submitQuery();
            }
          }, 500L);
          return true;
        }
        if ((1 == paramAnonymousInt) && (SearchBar.this.mSearchBarListener != null))
        {
          SearchBar.this.hideNativeKeyboard();
          SearchBar.this.mHandler.postDelayed(new Runnable()
          {
            public void run()
            {
              SearchBar.this.mSearchBarListener.onKeyboardDismiss(SearchBar.this.mSearchQuery);
            }
          }, 500L);
          return true;
        }
        if (2 == paramAnonymousInt)
        {
          SearchBar.this.hideNativeKeyboard();
          SearchBar.this.mHandler.postDelayed(new Runnable()
          {
            public void run()
            {
              SearchBar.this.mAutoStartRecognition = true;
              SearchBar.this.mSpeechOrbView.requestFocus();
            }
          }, 500L);
          return true;
        }
        return false;
      }
    });
    this.mSearchTextEditor.setPrivateImeOptions("EscapeNorth=1;VoiceDismiss=1;");
    this.mSpeechOrbView = ((SpeechOrbView)findViewById(R.id.lb_search_bar_speech_orb));
    this.mSpeechOrbView.setOnOrbClickedListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        SearchBar.this.toggleRecognition();
      }
    });
    this.mSpeechOrbView.setOnFocusChangeListener(new View.OnFocusChangeListener()
    {
      public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
      {
        if (paramAnonymousBoolean)
        {
          SearchBar.this.hideNativeKeyboard();
          if (SearchBar.this.mAutoStartRecognition)
          {
            SearchBar.this.startRecognition();
            SearchBar.this.mAutoStartRecognition = false;
          }
        }
        for (;;)
        {
          SearchBar.this.updateUi(paramAnonymousBoolean);
          return;
          SearchBar.this.stopRecognition();
        }
      }
    });
    updateUi(hasFocus());
    updateHint();
  }
  
  void playSearchFailure()
  {
    play(R.raw.lb_voice_failure);
  }
  
  void playSearchOpen()
  {
    play(R.raw.lb_voice_open);
  }
  
  void playSearchSuccess()
  {
    play(R.raw.lb_voice_success);
  }
  
  public void setBadgeDrawable(Drawable paramDrawable)
  {
    this.mBadgeDrawable = paramDrawable;
    if (this.mBadgeView != null)
    {
      this.mBadgeView.setImageDrawable(paramDrawable);
      if (paramDrawable != null) {
        this.mBadgeView.setVisibility(0);
      }
    }
    else
    {
      return;
    }
    this.mBadgeView.setVisibility(8);
  }
  
  public void setNextFocusDownId(int paramInt)
  {
    this.mSpeechOrbView.setNextFocusDownId(paramInt);
    this.mSearchTextEditor.setNextFocusDownId(paramInt);
  }
  
  public void setPermissionListener(SearchBarPermissionListener paramSearchBarPermissionListener)
  {
    this.mPermissionListener = paramSearchBarPermissionListener;
  }
  
  public void setSearchAffordanceColors(SearchOrbView.Colors paramColors)
  {
    if (this.mSpeechOrbView != null) {
      this.mSpeechOrbView.setNotListeningOrbColors(paramColors);
    }
  }
  
  public void setSearchAffordanceColorsInListening(SearchOrbView.Colors paramColors)
  {
    if (this.mSpeechOrbView != null) {
      this.mSpeechOrbView.setListeningOrbColors(paramColors);
    }
  }
  
  public void setSearchBarListener(SearchBarListener paramSearchBarListener)
  {
    this.mSearchBarListener = paramSearchBarListener;
  }
  
  public void setSearchQuery(String paramString)
  {
    stopRecognition();
    this.mSearchTextEditor.setText(paramString);
    setSearchQueryInternal(paramString);
  }
  
  void setSearchQueryInternal(String paramString)
  {
    if (TextUtils.equals(this.mSearchQuery, paramString)) {}
    do
    {
      return;
      this.mSearchQuery = paramString;
    } while (this.mSearchBarListener == null);
    this.mSearchBarListener.onSearchQueryChange(this.mSearchQuery);
  }
  
  public void setSpeechRecognitionCallback(SpeechRecognitionCallback paramSpeechRecognitionCallback)
  {
    this.mSpeechRecognitionCallback = paramSpeechRecognitionCallback;
    if ((this.mSpeechRecognitionCallback != null) && (this.mSpeechRecognizer != null)) {
      throw new IllegalStateException("Can't have speech recognizer and request");
    }
  }
  
  public void setSpeechRecognizer(SpeechRecognizer paramSpeechRecognizer)
  {
    stopRecognition();
    if (this.mSpeechRecognizer != null)
    {
      this.mSpeechRecognizer.setRecognitionListener(null);
      if (this.mListening)
      {
        this.mSpeechRecognizer.cancel();
        this.mListening = false;
      }
    }
    this.mSpeechRecognizer = paramSpeechRecognizer;
    if ((this.mSpeechRecognitionCallback != null) && (this.mSpeechRecognizer != null)) {
      throw new IllegalStateException("Can't have speech recognizer and request");
    }
  }
  
  public void setTitle(String paramString)
  {
    this.mTitle = paramString;
    updateHint();
  }
  
  void showNativeKeyboard()
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        SearchBar.this.mSearchTextEditor.requestFocusFromTouch();
        SearchBar.this.mSearchTextEditor.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, SearchBar.this.mSearchTextEditor.getWidth(), SearchBar.this.mSearchTextEditor.getHeight(), 0));
        SearchBar.this.mSearchTextEditor.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, SearchBar.this.mSearchTextEditor.getWidth(), SearchBar.this.mSearchTextEditor.getHeight(), 0));
      }
    });
  }
  
  public void startRecognition()
  {
    if (this.mRecognizing) {}
    do
    {
      return;
      if (!hasFocus()) {
        requestFocus();
      }
      if (this.mSpeechRecognitionCallback != null)
      {
        this.mSearchTextEditor.setText("");
        this.mSearchTextEditor.setHint("");
        this.mSpeechRecognitionCallback.recognizeSpeech();
        this.mRecognizing = true;
        return;
      }
    } while (this.mSpeechRecognizer == null);
    if (getContext().checkCallingOrSelfPermission("android.permission.RECORD_AUDIO") != 0)
    {
      if ((Build.VERSION.SDK_INT >= 23) && (this.mPermissionListener != null))
      {
        this.mPermissionListener.requestAudioPermission();
        return;
      }
      throw new IllegalStateException("android.permission.RECORD_AUDIO required for search");
    }
    this.mRecognizing = true;
    if (this.mAudioManager.requestAudioFocus(this.mAudioFocusChangeListener, 3, 3) != 1) {
      Log.w(TAG, "Could not get audio focus");
    }
    this.mSearchTextEditor.setText("");
    Intent localIntent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
    localIntent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
    localIntent.putExtra("android.speech.extra.PARTIAL_RESULTS", true);
    this.mSpeechRecognizer.setRecognitionListener(new RecognitionListener()
    {
      public void onBeginningOfSpeech() {}
      
      public void onBufferReceived(byte[] paramAnonymousArrayOfByte) {}
      
      public void onEndOfSpeech() {}
      
      public void onError(int paramAnonymousInt)
      {
        switch (paramAnonymousInt)
        {
        default: 
          Log.d(SearchBar.TAG, "recognizer other error");
        }
        for (;;)
        {
          SearchBar.this.stopRecognition();
          SearchBar.this.playSearchFailure();
          return;
          Log.w(SearchBar.TAG, "recognizer network timeout");
          continue;
          Log.w(SearchBar.TAG, "recognizer network error");
          continue;
          Log.w(SearchBar.TAG, "recognizer audio error");
          continue;
          Log.w(SearchBar.TAG, "recognizer server error");
          continue;
          Log.w(SearchBar.TAG, "recognizer client error");
          continue;
          Log.w(SearchBar.TAG, "recognizer speech timeout");
          continue;
          Log.w(SearchBar.TAG, "recognizer no match");
          continue;
          Log.w(SearchBar.TAG, "recognizer busy");
          continue;
          Log.w(SearchBar.TAG, "recognizer insufficient permissions");
        }
      }
      
      public void onEvent(int paramAnonymousInt, Bundle paramAnonymousBundle) {}
      
      public void onPartialResults(Bundle paramAnonymousBundle)
      {
        paramAnonymousBundle = paramAnonymousBundle.getStringArrayList("results_recognition");
        if ((paramAnonymousBundle == null) || (paramAnonymousBundle.size() == 0)) {
          return;
        }
        String str = (String)paramAnonymousBundle.get(0);
        if (paramAnonymousBundle.size() > 1) {}
        for (paramAnonymousBundle = (String)paramAnonymousBundle.get(1);; paramAnonymousBundle = null)
        {
          SearchBar.this.mSearchTextEditor.updateRecognizedText(str, paramAnonymousBundle);
          return;
        }
      }
      
      public void onReadyForSpeech(Bundle paramAnonymousBundle)
      {
        SearchBar.this.mSpeechOrbView.showListening();
        SearchBar.this.playSearchOpen();
      }
      
      public void onResults(Bundle paramAnonymousBundle)
      {
        paramAnonymousBundle = paramAnonymousBundle.getStringArrayList("results_recognition");
        if (paramAnonymousBundle != null)
        {
          SearchBar.this.mSearchQuery = ((String)paramAnonymousBundle.get(0));
          SearchBar.this.mSearchTextEditor.setText(SearchBar.this.mSearchQuery);
          SearchBar.this.submitQuery();
        }
        SearchBar.this.stopRecognition();
        SearchBar.this.playSearchSuccess();
      }
      
      public void onRmsChanged(float paramAnonymousFloat)
      {
        if (paramAnonymousFloat < 0.0F) {}
        for (int i = 0;; i = (int)(10.0F * paramAnonymousFloat))
        {
          SearchBar.this.mSpeechOrbView.setSoundLevel(i);
          return;
        }
      }
    });
    this.mListening = true;
    this.mSpeechRecognizer.startListening(localIntent);
  }
  
  public void stopRecognition()
  {
    if (!this.mRecognizing) {}
    do
    {
      return;
      this.mSearchTextEditor.setText(this.mSearchQuery);
      this.mSearchTextEditor.setHint(this.mHint);
      this.mRecognizing = false;
    } while ((this.mSpeechRecognitionCallback != null) || (this.mSpeechRecognizer == null));
    this.mSpeechOrbView.showNotListening();
    if (this.mListening)
    {
      this.mSpeechRecognizer.cancel();
      this.mListening = false;
      this.mAudioManager.abandonAudioFocus(this.mAudioFocusChangeListener);
    }
    this.mSpeechRecognizer.setRecognitionListener(null);
  }
  
  void submitQuery()
  {
    if ((!TextUtils.isEmpty(this.mSearchQuery)) && (this.mSearchBarListener != null)) {
      this.mSearchBarListener.onSearchQuerySubmit(this.mSearchQuery);
    }
  }
  
  void toggleRecognition()
  {
    if (this.mRecognizing)
    {
      stopRecognition();
      return;
    }
    startRecognition();
  }
  
  void updateUi(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mBarBackground.setAlpha(this.mBackgroundSpeechAlpha);
      if (isVoiceMode())
      {
        this.mSearchTextEditor.setTextColor(this.mTextHintColorSpeechMode);
        this.mSearchTextEditor.setHintTextColor(this.mTextHintColorSpeechMode);
      }
    }
    for (;;)
    {
      updateHint();
      return;
      this.mSearchTextEditor.setTextColor(this.mTextColorSpeechMode);
      this.mSearchTextEditor.setHintTextColor(this.mTextHintColorSpeechMode);
      continue;
      this.mBarBackground.setAlpha(this.mBackgroundAlpha);
      this.mSearchTextEditor.setTextColor(this.mTextColor);
      this.mSearchTextEditor.setHintTextColor(this.mTextHintColor);
    }
  }
  
  public static abstract interface SearchBarListener
  {
    public abstract void onKeyboardDismiss(String paramString);
    
    public abstract void onSearchQueryChange(String paramString);
    
    public abstract void onSearchQuerySubmit(String paramString);
  }
  
  public static abstract interface SearchBarPermissionListener
  {
    public abstract void requestAudioPermission();
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/SearchBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */