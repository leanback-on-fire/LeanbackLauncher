package com.google.android.tvlauncher.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.SearchOrbView.Colors;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;
import com.google.android.tvlauncher.appsview.AppsManager.SearchPackageChangeListener;
import com.google.android.tvlauncher.util.Partner;
import java.util.Random;

public class SearchOrbView
  extends FrameLayout
  implements AppsManager.SearchPackageChangeListener
{
  private static final String EXTRA_SEARCH_TYPE = "search_type";
  private static final int FOCUSED_KEYBOARD_TEXT = -3;
  private static final int FOCUSED_MIC_TEXT = -2;
  private static final int INIT_TEXT = -1;
  private static final String KATNISS_PACKAGE = "com.google.android.katniss";
  private static final int SEARCH_TYPE_KEYBOARD = 2;
  private static final int SEARCH_TYPE_VOICE = 1;
  private static final String TAG = "SearchOrbView";
  private static final int TEXT_ANIM_FADE = 2;
  private static final int TEXT_ANIM_HORIZONTAL = 1;
  private static final int TEXT_ANIM_VERTICAL = 0;
  private int mClickDeviceId = -1;
  private int mColorBright;
  private int mColorDim;
  private Context mContext;
  private int mCurrentIndex = 0;
  private boolean mEatDpadCenterKeyDown;
  private final int mFocusedColor;
  private final String mFocusedKeyboardText;
  private final String mFocusedMicText;
  private final String mFocusedText;
  private final ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener()
  {
    public void onGlobalLayout()
    {
      SearchOrbView.this.setKeyboardOrbProgress(SearchOrbView.this.mKeyboardOrbProgress);
    }
  };
  private Handler mHandler = new Handler();
  private final int mIdleTextFlipDelay;
  private final boolean mIsHintFlippingAllowed;
  private FrameLayout mKeyboardContainer;
  private Drawable mKeyboardFocusedIcon;
  private final int mKeyboardOrbAnimationDuration;
  private float mKeyboardOrbProgress = 0.0F;
  private android.support.v17.leanback.widget.SearchOrbView mKeyboardOrbView;
  private Drawable mKeyboardUnfocusedIcon;
  private final int mLaunchFadeDuration;
  private android.support.v17.leanback.widget.SearchOrbView mMicOrbView;
  private ObjectAnimator mOrbAnimation;
  private final String mSearchHintText;
  private final Intent mSearchIntent = getSearchIntent();
  private final int mSearchOrbsSpacing;
  private Runnable mSwitchRunnable;
  private TextSwitcher mSwitcher;
  private final String[] mTextToShow;
  private final int mUnfocusedColor;
  private boolean mWahlbergUx;
  
  public SearchOrbView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext;
    this.mContext.getTheme();
    paramAttributeSet = paramContext.getResources();
    this.mTextToShow = paramAttributeSet.getStringArray(2131361794);
    this.mIdleTextFlipDelay = paramAttributeSet.getInteger(2131689524);
    this.mLaunchFadeDuration = paramAttributeSet.getInteger(2131689525);
    this.mSearchHintText = fixItalics(paramContext.getString(2131493070));
    this.mFocusedText = fixItalics(paramContext.getString(2131492992));
    this.mFocusedMicText = fixItalics(paramContext.getString(2131492994));
    this.mFocusedKeyboardText = paramContext.getString(2131492993);
    this.mFocusedColor = ContextCompat.getColor(this.mContext, 2131820729);
    this.mUnfocusedColor = ContextCompat.getColor(this.mContext, 2131820730);
    boolean bool1 = bool2;
    if (paramAttributeSet.getBoolean(2131755013))
    {
      bool1 = bool2;
      if (isKatnissPackagePresent()) {
        bool1 = true;
      }
    }
    this.mIsHintFlippingAllowed = bool1;
    this.mWahlbergUx = useWahlbergUx();
    this.mSearchOrbsSpacing = paramAttributeSet.getDimensionPixelSize(2131559015);
    this.mKeyboardOrbAnimationDuration = paramAttributeSet.getInteger(2131689517);
  }
  
  private void animateOut()
  {
    setVisible(false);
    reset();
  }
  
  private void animateVisibility(View paramView, boolean paramBoolean)
  {
    paramView.clearAnimation();
    paramView = paramView.animate();
    if (paramBoolean) {}
    for (float f = 1.0F;; f = 0.0F)
    {
      paramView = paramView.alpha(f).setDuration(this.mLaunchFadeDuration);
      if ((this.mWahlbergUx) && (this.mKeyboardOrbView != null) && (!paramBoolean)) {
        paramView.setListener(new Animator.AnimatorListener()
        {
          public void onAnimationCancel(Animator paramAnonymousAnimator) {}
          
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            if ((SearchOrbView.this.mKeyboardOrbView != null) && (SearchOrbView.this.mKeyboardOrbView.hasFocus())) {
              SearchOrbView.this.mMicOrbView.requestFocus();
            }
          }
          
          public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
          
          public void onAnimationStart(Animator paramAnonymousAnimator) {}
        });
      }
      paramView.start();
      return;
    }
  }
  
  private void configSwitcher(boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    Object localObject;
    int i;
    if (paramBoolean1)
    {
      localObject = this.mSwitcher.getNextView();
      if ((localObject instanceof TextView))
      {
        localObject = (TextView)localObject;
        if (!paramBoolean2) {
          break label104;
        }
        i = this.mFocusedColor;
        label38:
        ((TextView)localObject).setTextColor(i);
        ((TextView)localObject).setTypeface(null, 2);
      }
      if (paramInt != 1) {
        break label113;
      }
      paramInt = 2131034125;
      i = 2131034126;
    }
    for (;;)
    {
      this.mSwitcher.setInAnimation(this.mContext, paramInt);
      this.mSwitcher.setOutAnimation(this.mContext, i);
      return;
      localObject = this.mSwitcher.getCurrentView();
      break;
      label104:
      i = this.mUnfocusedColor;
      break label38;
      label113:
      if (paramInt == 0)
      {
        paramInt = 2131034124;
        i = 2131034127;
      }
      else
      {
        paramInt = 2131034122;
        i = 2131034123;
      }
    }
  }
  
  private boolean focusIsOnSearchView()
  {
    return (this.mMicOrbView.hasFocus()) || ((this.mKeyboardOrbView != null) && (this.mKeyboardOrbView.hasFocus()));
  }
  
  public static int getColor(Resources paramResources, int paramInt, @Nullable Resources.Theme paramTheme)
  {
    return paramResources.getColor(paramInt, paramTheme);
  }
  
  private String getHintText(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mWahlbergUx)
    {
      if (paramBoolean1)
      {
        if (paramBoolean2) {
          return this.mFocusedKeyboardText;
        }
        return this.mFocusedMicText;
      }
      return this.mSearchHintText;
    }
    if (paramBoolean1) {
      return this.mFocusedText;
    }
    return this.mSearchHintText;
  }
  
  public static Intent getSearchIntent()
  {
    return new Intent("android.intent.action.ASSIST").addFlags(270532608);
  }
  
  private void initTextSwitcher(final Context paramContext)
  {
    this.mSwitcher = ((TextSwitcher)findViewById(2131951834));
    this.mSwitcher.setAnimateFirstView(false);
    this.mSwitcher.setFactory(new ViewSwitcher.ViewFactory()
    {
      LayoutInflater inflater = (LayoutInflater)paramContext.getSystemService("layout_inflater");
      
      public View makeView()
      {
        return this.inflater.inflate(2130968732, SearchOrbView.this, false);
      }
    });
    this.mSwitchRunnable = new Runnable()
    {
      public void run()
      {
        int i = SearchOrbView.this.mCurrentIndex;
        SearchOrbView.access$902(SearchOrbView.this, new Random().nextInt(SearchOrbView.this.mTextToShow.length));
        if (i == SearchOrbView.this.mCurrentIndex) {
          SearchOrbView.access$902(SearchOrbView.this, (SearchOrbView.this.mCurrentIndex + 1) % SearchOrbView.this.mTextToShow.length);
        }
        SearchOrbView.this.configSwitcher(true, false, 0);
        SearchOrbView.this.mSwitcher.setText(SearchOrbView.this.fixItalics(SearchOrbView.this.mTextToShow[SearchOrbView.this.mCurrentIndex]));
        SearchOrbView.this.mHandler.postDelayed(this, SearchOrbView.this.mIdleTextFlipDelay);
      }
    };
    reset();
  }
  
  private void initializeSearchOrbs()
  {
    if ((this.mOrbAnimation != null) && ((this.mOrbAnimation.isRunning()) || (this.mOrbAnimation.isStarted()))) {
      this.mOrbAnimation.cancel();
    }
    this.mOrbAnimation = null;
    setKeyboardOrbProgress(0.0F);
    if (this.mWahlbergUx)
    {
      this.mKeyboardOrbView = ((android.support.v17.leanback.widget.SearchOrbView)findViewById(2131951833));
      this.mKeyboardContainer = ((FrameLayout)findViewById(2131951831));
      this.mKeyboardFocusedIcon = ContextCompat.getDrawable(this.mContext, 2130837664);
      this.mKeyboardUnfocusedIcon = ContextCompat.getDrawable(this.mContext, 2130837665);
      this.mColorBright = ContextCompat.getColor(this.mContext, 2131820725);
      this.mColorDim = ContextCompat.getColor(this.mContext, 2131820728);
      this.mKeyboardOrbView.setOrbIcon(this.mKeyboardUnfocusedIcon);
      this.mKeyboardOrbView.setOrbColor(this.mColorDim);
      this.mKeyboardOrbView.enableOrbColorAnimation(false);
      this.mKeyboardOrbView.setOnFocusChangeListener(new View.OnFocusChangeListener()
      {
        public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
        {
          SearchOrbView.this.setSearchState(false);
          android.support.v17.leanback.widget.SearchOrbView localSearchOrbView = (android.support.v17.leanback.widget.SearchOrbView)paramAnonymousView;
          if (paramAnonymousBoolean)
          {
            paramAnonymousView = SearchOrbView.this.mKeyboardFocusedIcon;
            localSearchOrbView.setOrbIcon(paramAnonymousView);
            if (!paramAnonymousBoolean) {
              break label62;
            }
          }
          label62:
          for (int i = SearchOrbView.this.mColorBright;; i = SearchOrbView.this.mColorDim)
          {
            localSearchOrbView.setOrbColor(i);
            return;
            paramAnonymousView = SearchOrbView.this.mKeyboardUnfocusedIcon;
            break;
          }
        }
      });
      this.mOrbAnimation = ObjectAnimator.ofFloat(this, "keyboardOrbProgress", new float[] { 0.0F });
      this.mOrbAnimation.setDuration(this.mKeyboardOrbAnimationDuration);
    }
    for (;;)
    {
      Drawable localDrawable = Partner.get(this.mContext).getCustomSearchIcon();
      if (localDrawable == null) {
        break;
      }
      this.mMicOrbView.setOrbIcon(localDrawable);
      return;
      this.mKeyboardFocusedIcon = null;
      this.mKeyboardUnfocusedIcon = null;
      this.mKeyboardOrbView = null;
      this.mKeyboardContainer = null;
    }
    if (this.mWahlbergUx)
    {
      this.mMicOrbView.setOrbColor(this.mColorDim);
      this.mMicOrbView.setOrbIcon(ContextCompat.getDrawable(this.mContext, 2130837666));
      this.mMicOrbView.enableOrbColorAnimation(false);
      return;
    }
    this.mMicOrbView.setOrbColors(new SearchOrbView.Colors(ContextCompat.getColor(this.mContext, 2131820727), ContextCompat.getColor(this.mContext, 2131820726)));
    this.mMicOrbView.setOrbIcon(ContextCompat.getDrawable(this.mContext, 2130837670));
    this.mMicOrbView.enableOrbColorAnimation(true);
  }
  
  private static boolean isConfirmKey(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    }
    return true;
  }
  
  private boolean isKatnissPackagePresent()
  {
    boolean bool = false;
    try
    {
      PackageInfo localPackageInfo = this.mContext.getPackageManager().getPackageInfo("com.google.android.katniss", 0);
      if (localPackageInfo != null) {
        bool = true;
      }
      return bool;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        Object localObject = null;
      }
    }
  }
  
  private static void playErrorSound(Context paramContext)
  {
    ((AudioManager)paramContext.getSystemService("audio")).playSoundEffect(9);
  }
  
  private void setKeyboardOrbProgress(float paramFloat)
  {
    int k = 0;
    int j = 1;
    boolean bool;
    if (paramFloat == 1.0D)
    {
      bool = true;
      if (paramFloat <= 0.0F) {
        break label163;
      }
      i = 1;
      label24:
      this.mKeyboardOrbProgress = paramFloat;
      if (this.mKeyboardOrbView != null)
      {
        this.mKeyboardOrbView.setFocusable(bool);
        this.mKeyboardOrbView.setClipToOutline(true);
        this.mKeyboardOrbView.setAlpha(paramFloat);
        FrameLayout localFrameLayout = this.mKeyboardContainer;
        if (i == 0) {
          break label168;
        }
        i = k;
        label74:
        localFrameLayout.setVisibility(i);
        this.mKeyboardContainer.setScaleX(paramFloat);
        this.mKeyboardContainer.setScaleY(paramFloat);
        k = this.mKeyboardOrbView.getMeasuredWidth();
        if (getLayoutDirection() != 1) {
          break label173;
        }
      }
    }
    label163:
    label168:
    label173:
    for (int i = j;; i = -1)
    {
      float f = i * (this.mSearchOrbsSpacing + k) * (1.0F - paramFloat);
      this.mKeyboardOrbView.setTranslationX(f / paramFloat);
      if (this.mSwitcher != null) {
        this.mSwitcher.setTranslationX(f);
      }
      return;
      bool = false;
      break;
      i = 0;
      break label24;
      i = 4;
      break label74;
    }
  }
  
  private void setSearchState(boolean paramBoolean)
  {
    this.mHandler.removeCallbacks(this.mSwitchRunnable);
    boolean bool2 = focusIsOnSearchView();
    int j = this.mCurrentIndex;
    boolean bool1;
    if ((this.mWahlbergUx) && (bool2) && (!this.mMicOrbView.hasFocus()))
    {
      bool1 = true;
      if (!bool2) {
        break label146;
      }
      if (bool1) {
        break label140;
      }
      i = -2;
      label60:
      this.mCurrentIndex = i;
      if (j != this.mCurrentIndex)
      {
        if ((j == -1) || (this.mCurrentIndex == -1)) {
          break label151;
        }
        i = 1;
        label88:
        if (paramBoolean) {
          break label156;
        }
        paramBoolean = true;
        label94:
        if (i == 0) {
          break label161;
        }
      }
    }
    label140:
    label146:
    label151:
    label156:
    label161:
    for (int i = 2;; i = 1)
    {
      configSwitcher(paramBoolean, bool2, i);
      if (this.mWahlbergUx) {
        break label166;
      }
      this.mSwitcher.setText(fixItalics(getHintText(bool2, false)));
      return;
      bool1 = false;
      break;
      i = -3;
      break label60;
      i = -1;
      break label60;
      i = 0;
      break label88;
      paramBoolean = false;
      break label94;
    }
    label166:
    this.mSwitcher.setText(fixItalics(getHintText(bool2, bool1)));
  }
  
  private void setVisible(boolean paramBoolean)
  {
    animateVisibility(this.mSwitcher, paramBoolean);
  }
  
  private static boolean startActivitySafely(Context paramContext, Intent paramIntent)
  {
    try
    {
      paramContext.startActivity(paramIntent);
      return true;
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      Log.e("SearchOrbView", "Exception launching intent " + paramIntent, localActivityNotFoundException);
      Toast.makeText(paramContext, paramContext.getString(2131492896), 0).show();
    }
    return false;
  }
  
  private static boolean startSearchActivitySafely(Context paramContext, Intent paramIntent, int paramInt, boolean paramBoolean)
  {
    paramIntent.putExtra("android.intent.extra.ASSIST_INPUT_DEVICE_ID", paramInt);
    if (paramBoolean) {}
    for (paramInt = 2;; paramInt = 1)
    {
      paramIntent.putExtra("search_type", paramInt);
      return startActivitySafely(paramContext, paramIntent);
    }
  }
  
  private static boolean startSearchActivitySafely(Context paramContext, Intent paramIntent, boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 2;; i = 1)
    {
      paramIntent.putExtra("search_type", i);
      return startActivitySafely(paramContext, paramIntent);
    }
  }
  
  private boolean useWahlbergUx()
  {
    try
    {
      Resources localResources = this.mContext.getPackageManager().getResourcesForApplication("com.google.android.katniss");
      int i = 0;
      if (localResources != null) {
        i = localResources.getIdentifier("katniss_uses_new_google_logo", "bool", "com.google.android.katniss");
      }
      if (i != 0)
      {
        boolean bool = localResources.getBoolean(i);
        return bool;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
    return false;
  }
  
  public void animateIn()
  {
    setVisible(true);
  }
  
  public void animateKeyboardOrb(boolean paramBoolean)
  {
    if ((this.mKeyboardOrbView == null) || (this.mOrbAnimation == null)) {}
    for (;;)
    {
      return;
      if (this.mOrbAnimation.isStarted()) {
        this.mOrbAnimation.cancel();
      }
      if (paramBoolean) {}
      for (float f = 1.0F; this.mKeyboardOrbProgress != f; f = 0.0F)
      {
        this.mOrbAnimation.setFloatValues(new float[] { this.mKeyboardOrbProgress, f });
        this.mOrbAnimation.start();
        return;
      }
    }
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    int i = paramKeyEvent.getAction();
    if (isConfirmKey(paramKeyEvent.getKeyCode()))
    {
      if (paramKeyEvent.isLongPress())
      {
        this.mEatDpadCenterKeyDown = true;
        playErrorSound(getContext());
        return true;
      }
      if (i == 1)
      {
        if (this.mEatDpadCenterKeyDown)
        {
          this.mEatDpadCenterKeyDown = false;
          return true;
        }
        this.mClickDeviceId = paramKeyEvent.getDeviceId();
      }
    }
    return super.dispatchKeyEvent(paramKeyEvent);
  }
  
  public String fixItalics(String paramString)
  {
    if (paramString != null)
    {
      paramString = new StringBuilder(paramString);
      if (getLayoutDirection() == 1) {
        paramString.insert(0, " ");
      }
      for (;;)
      {
        return paramString.toString();
        paramString.append(" ");
      }
    }
    return null;
  }
  
  public String getSearchPackageName()
  {
    return "com.google.android.katniss";
  }
  
  public void onAttachedToWindow()
  {
    final boolean bool = true;
    super.onAttachedToWindow();
    setVisible(true);
    Object localObject = (AccessibilityManager)this.mContext.getSystemService("accessibility");
    if ((((AccessibilityManager)localObject).isEnabled()) && (((AccessibilityManager)localObject).isTouchExplorationEnabled())) {}
    for (;;)
    {
      localObject = new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if ((SearchOrbView.this.mKeyboardOrbView != null) && (SearchOrbView.this.mKeyboardOrbView.hasFocus()))
          {
            bool = true;
            if (!bool) {
              break label68;
            }
          }
          label68:
          for (boolean bool = SearchOrbView.startSearchActivitySafely(SearchOrbView.this.mContext, SearchOrbView.this.mSearchIntent, bool);; bool = SearchOrbView.startSearchActivitySafely(SearchOrbView.this.mContext, SearchOrbView.this.mSearchIntent, SearchOrbView.this.mClickDeviceId, bool))
          {
            if (bool) {
              SearchOrbView.this.reset();
            }
            return;
            bool = false;
            break;
          }
        }
      };
      this.mMicOrbView.setOnClickListener((View.OnClickListener)localObject);
      if (this.mKeyboardOrbView != null) {
        this.mKeyboardOrbView.setOnClickListener((View.OnClickListener)localObject);
      }
      getViewTreeObserver().addOnGlobalLayoutListener(this.mGlobalLayoutListener);
      return;
      bool = false;
    }
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    getViewTreeObserver().removeOnGlobalLayoutListener(this.mGlobalLayoutListener);
    reset();
  }
  
  public void onFinishInflate()
  {
    super.onFinishInflate();
    this.mMicOrbView = ((android.support.v17.leanback.widget.SearchOrbView)findViewById(2131951832));
    this.mMicOrbView.setOnFocusChangeListener(new View.OnFocusChangeListener()
    {
      public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
      {
        SearchOrbView.this.setSearchState(false);
        if (SearchOrbView.this.mWahlbergUx)
        {
          paramAnonymousView = SearchOrbView.this.mMicOrbView;
          if (!paramAnonymousBoolean) {
            break label44;
          }
        }
        label44:
        for (int i = SearchOrbView.this.mColorBright;; i = SearchOrbView.this.mColorDim)
        {
          paramAnonymousView.setOrbColor(i);
          return;
        }
      }
    });
    initializeSearchOrbs();
    initTextSwitcher(getContext());
  }
  
  public void onSearchPackageChanged()
  {
    if (useWahlbergUx() != this.mWahlbergUx)
    {
      this.mWahlbergUx = useWahlbergUx();
      initializeSearchOrbs();
      setSearchState(false);
    }
  }
  
  protected void onVisibilityChanged(View paramView, int paramInt)
  {
    boolean bool = true;
    if (paramView == this)
    {
      if (paramInt != 0) {
        break label53;
      }
      paramInt = 1;
      if (paramInt != 0) {
        break label58;
      }
      reset();
      label21:
      paramView = this.mMicOrbView;
      if ((paramInt == 0) || (!this.mMicOrbView.hasFocus()) || (this.mWahlbergUx)) {
        break label86;
      }
    }
    for (;;)
    {
      paramView.enableOrbColorAnimation(bool);
      return;
      label53:
      paramInt = 0;
      break;
      label58:
      if ((this.mKeyboardOrbView == null) || (!this.mKeyboardOrbView.hasFocus())) {
        break label21;
      }
      this.mMicOrbView.requestFocus();
      break label21;
      label86:
      bool = false;
    }
  }
  
  public void reset()
  {
    this.mHandler.removeCallbacks(this.mSwitchRunnable);
    this.mSwitcher.reset();
    this.mCurrentIndex = 0;
    setSearchState(true);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/view/SearchOrbView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */