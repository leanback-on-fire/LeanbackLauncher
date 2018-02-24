package android.support.v17.leanback.app;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v17.leanback.R.animator;
import android.support.v17.leanback.R.attr;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.layout;
import android.support.v17.leanback.widget.PagingIndicator;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Property;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public abstract class OnboardingSupportFragment
  extends Fragment
{
  private static final boolean DEBUG = false;
  private static final long DESCRIPTION_START_DELAY_MS = 33L;
  private static final long HEADER_ANIMATION_DURATION_MS = 417L;
  private static final long HEADER_APPEAR_DELAY_MS = 500L;
  private static final TimeInterpolator HEADER_APPEAR_INTERPOLATOR = new DecelerateInterpolator();
  private static final TimeInterpolator HEADER_DISAPPEAR_INTERPOLATOR = new AccelerateInterpolator();
  private static final String KEY_CURRENT_PAGE_INDEX = "leanback.onboarding.current_page_index";
  private static final String KEY_ENTER_ANIMATION_FINISHED = "leanback.onboarding.enter_animation_finished";
  private static final String KEY_LOGO_ANIMATION_FINISHED = "leanback.onboarding.logo_animation_finished";
  private static final long LOGO_SPLASH_PAUSE_DURATION_MS = 1333L;
  private static final int SLIDE_DISTANCE = 60;
  private static final String TAG = "OnboardingSupportFragment";
  private static int sSlideDistance;
  private AnimatorSet mAnimator;
  @ColorInt
  private int mArrowBackgroundColor = 0;
  private boolean mArrowBackgroundColorSet;
  @ColorInt
  private int mArrowColor = 0;
  private boolean mArrowColorSet;
  int mCurrentPageIndex;
  TextView mDescriptionView;
  @ColorInt
  private int mDescriptionViewTextColor = 0;
  private boolean mDescriptionViewTextColorSet;
  @ColorInt
  private int mDotBackgroundColor = 0;
  private boolean mDotBackgroundColorSet;
  boolean mEnterAnimationFinished;
  private int mIconResourceId;
  boolean mIsLtr;
  boolean mLogoAnimationFinished;
  private int mLogoResourceId;
  private ImageView mLogoView;
  private ImageView mMainIconView;
  private final View.OnClickListener mOnClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (!OnboardingSupportFragment.this.mLogoAnimationFinished) {
        return;
      }
      if (OnboardingSupportFragment.this.mCurrentPageIndex == OnboardingSupportFragment.this.getPageCount() - 1)
      {
        OnboardingSupportFragment.this.onFinishFragment();
        return;
      }
      OnboardingSupportFragment.this.moveToNextPage();
    }
  };
  private final View.OnKeyListener mOnKeyListener = new View.OnKeyListener()
  {
    public boolean onKey(View paramAnonymousView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
    {
      if (!OnboardingSupportFragment.this.mLogoAnimationFinished) {
        return paramAnonymousInt != 4;
      }
      if (paramAnonymousKeyEvent.getAction() == 0) {
        return false;
      }
      switch (paramAnonymousInt)
      {
      default: 
        return false;
      case 4: 
        if (OnboardingSupportFragment.this.mCurrentPageIndex == 0) {
          return false;
        }
        OnboardingSupportFragment.this.moveToPreviousPage();
        return true;
      case 21: 
        if (OnboardingSupportFragment.this.mIsLtr)
        {
          OnboardingSupportFragment.this.moveToPreviousPage();
          return true;
        }
        OnboardingSupportFragment.this.moveToNextPage();
        return true;
      }
      if (OnboardingSupportFragment.this.mIsLtr)
      {
        OnboardingSupportFragment.this.moveToNextPage();
        return true;
      }
      OnboardingSupportFragment.this.moveToPreviousPage();
      return true;
    }
  };
  PagingIndicator mPageIndicator;
  View mStartButton;
  private CharSequence mStartButtonText;
  private boolean mStartButtonTextSet;
  private ContextThemeWrapper mThemeWrapper;
  TextView mTitleView;
  @ColorInt
  private int mTitleViewTextColor = 0;
  private boolean mTitleViewTextColorSet;
  
  private Animator createAnimator(View paramView, boolean paramBoolean, int paramInt, long paramLong)
  {
    int i;
    if (getView().getLayoutDirection() == 0)
    {
      i = 1;
      if (((i == 0) || (paramInt != 8388613)) && ((i != 0) || (paramInt != 8388611)) && (paramInt != 5)) {
        break label194;
      }
      paramInt = 1;
      label42:
      if (!paramBoolean) {
        break label209;
      }
      localObjectAnimator = ObjectAnimator.ofFloat(paramView, View.ALPHA, new float[] { 0.0F, 1.0F });
      localObject = View.TRANSLATION_X;
      if (paramInt == 0) {
        break label199;
      }
    }
    label194:
    label199:
    for (float f = sSlideDistance;; f = -sSlideDistance)
    {
      localObject = ObjectAnimator.ofFloat(paramView, (Property)localObject, new float[] { f, 0.0F });
      localObjectAnimator.setInterpolator(HEADER_APPEAR_INTERPOLATOR);
      ((Animator)localObject).setInterpolator(HEADER_APPEAR_INTERPOLATOR);
      localObjectAnimator.setDuration(417L);
      localObjectAnimator.setTarget(paramView);
      ((Animator)localObject).setDuration(417L);
      ((Animator)localObject).setTarget(paramView);
      paramView = new AnimatorSet();
      paramView.playTogether(new Animator[] { localObjectAnimator, localObject });
      if (paramLong > 0L) {
        paramView.setStartDelay(paramLong);
      }
      return paramView;
      i = 0;
      break;
      paramInt = 0;
      break label42;
    }
    label209:
    ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(paramView, View.ALPHA, new float[] { 1.0F, 0.0F });
    Object localObject = View.TRANSLATION_X;
    if (paramInt != 0) {}
    for (f = sSlideDistance;; f = -sSlideDistance)
    {
      localObject = ObjectAnimator.ofFloat(paramView, (Property)localObject, new float[] { 0.0F, f });
      localObjectAnimator.setInterpolator(HEADER_DISAPPEAR_INTERPOLATOR);
      ((Animator)localObject).setInterpolator(HEADER_DISAPPEAR_INTERPOLATOR);
      break;
    }
  }
  
  private LayoutInflater getThemeInflater(LayoutInflater paramLayoutInflater)
  {
    if (this.mThemeWrapper == null) {
      return paramLayoutInflater;
    }
    return paramLayoutInflater.cloneInContext(this.mThemeWrapper);
  }
  
  private void onPageChangedInternal(int paramInt)
  {
    if (this.mAnimator != null) {
      this.mAnimator.end();
    }
    this.mPageIndicator.onPageSelected(this.mCurrentPageIndex, true);
    ArrayList localArrayList = new ArrayList();
    Object localObject;
    Animator localAnimator;
    if (paramInt < getCurrentPageIndex())
    {
      localArrayList.add(createAnimator(this.mTitleView, false, 8388611, 0L));
      localObject = createAnimator(this.mDescriptionView, false, 8388611, 33L);
      localArrayList.add(localObject);
      localArrayList.add(createAnimator(this.mTitleView, true, 8388613, 500L));
      localArrayList.add(createAnimator(this.mDescriptionView, true, 8388613, 533L));
      ((Animator)localObject).addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          OnboardingSupportFragment.this.mTitleView.setText(OnboardingSupportFragment.this.getPageTitle(this.val$currentPageIndex));
          OnboardingSupportFragment.this.mDescriptionView.setText(OnboardingSupportFragment.this.getPageDescription(this.val$currentPageIndex));
        }
      });
      localObject = getContext();
      if (getCurrentPageIndex() != getPageCount() - 1) {
        break label355;
      }
      this.mStartButton.setVisibility(0);
      localAnimator = AnimatorInflater.loadAnimator((Context)localObject, R.animator.lb_onboarding_page_indicator_fade_out);
      localAnimator.setTarget(this.mPageIndicator);
      localAnimator.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          OnboardingSupportFragment.this.mPageIndicator.setVisibility(8);
        }
      });
      localArrayList.add(localAnimator);
      localObject = AnimatorInflater.loadAnimator((Context)localObject, R.animator.lb_onboarding_start_button_fade_in);
      ((Animator)localObject).setTarget(this.mStartButton);
      localArrayList.add(localObject);
    }
    for (;;)
    {
      this.mAnimator = new AnimatorSet();
      this.mAnimator.playTogether(localArrayList);
      this.mAnimator.start();
      onPageChanged(this.mCurrentPageIndex, paramInt);
      return;
      localArrayList.add(createAnimator(this.mTitleView, false, 8388613, 0L));
      localObject = createAnimator(this.mDescriptionView, false, 8388613, 33L);
      localArrayList.add(localObject);
      localArrayList.add(createAnimator(this.mTitleView, true, 8388611, 500L));
      localArrayList.add(createAnimator(this.mDescriptionView, true, 8388611, 533L));
      break;
      label355:
      if (paramInt == getPageCount() - 1)
      {
        this.mPageIndicator.setVisibility(0);
        localAnimator = AnimatorInflater.loadAnimator((Context)localObject, R.animator.lb_onboarding_page_indicator_fade_in);
        localAnimator.setTarget(this.mPageIndicator);
        localArrayList.add(localAnimator);
        localObject = AnimatorInflater.loadAnimator((Context)localObject, R.animator.lb_onboarding_start_button_fade_out);
        ((Animator)localObject).setTarget(this.mStartButton);
        ((Animator)localObject).addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            OnboardingSupportFragment.this.mStartButton.setVisibility(8);
          }
        });
        localArrayList.add(localObject);
      }
    }
  }
  
  private void resolveTheme()
  {
    Context localContext = getContext();
    int i = onProvideTheme();
    if (i == -1)
    {
      i = R.attr.onboardingTheme;
      TypedValue localTypedValue = new TypedValue();
      if (localContext.getTheme().resolveAttribute(i, localTypedValue, true)) {
        this.mThemeWrapper = new ContextThemeWrapper(localContext, localTypedValue.resourceId);
      }
      return;
    }
    this.mThemeWrapper = new ContextThemeWrapper(localContext, i);
  }
  
  @ColorInt
  public final int getArrowBackgroundColor()
  {
    return this.mArrowBackgroundColor;
  }
  
  @ColorInt
  public final int getArrowColor()
  {
    return this.mArrowColor;
  }
  
  protected final int getCurrentPageIndex()
  {
    return this.mCurrentPageIndex;
  }
  
  @ColorInt
  public final int getDescriptionViewTextColor()
  {
    return this.mDescriptionViewTextColor;
  }
  
  @ColorInt
  public final int getDotBackgroundColor()
  {
    return this.mDotBackgroundColor;
  }
  
  public final int getIconResourceId()
  {
    return this.mIconResourceId;
  }
  
  public final int getLogoResourceId()
  {
    return this.mLogoResourceId;
  }
  
  protected abstract int getPageCount();
  
  protected abstract CharSequence getPageDescription(int paramInt);
  
  protected abstract CharSequence getPageTitle(int paramInt);
  
  public final CharSequence getStartButtonText()
  {
    return this.mStartButtonText;
  }
  
  @ColorInt
  public final int getTitleViewTextColor()
  {
    return this.mTitleViewTextColor;
  }
  
  void hideLogoView()
  {
    this.mLogoView.setVisibility(8);
    if (this.mIconResourceId != 0)
    {
      this.mMainIconView.setImageResource(this.mIconResourceId);
      this.mMainIconView.setVisibility(0);
    }
    View localView1 = getView();
    Object localObject = getThemeInflater(LayoutInflater.from(getContext()));
    ViewGroup localViewGroup = (ViewGroup)localView1.findViewById(R.id.background_container);
    View localView2 = onCreateBackgroundView((LayoutInflater)localObject, localViewGroup);
    if (localView2 != null)
    {
      localViewGroup.setVisibility(0);
      localViewGroup.addView(localView2);
    }
    localViewGroup = (ViewGroup)localView1.findViewById(R.id.content_container);
    localView2 = onCreateContentView((LayoutInflater)localObject, localViewGroup);
    if (localView2 != null)
    {
      localViewGroup.setVisibility(0);
      localViewGroup.addView(localView2);
    }
    localViewGroup = (ViewGroup)localView1.findViewById(R.id.foreground_container);
    localObject = onCreateForegroundView((LayoutInflater)localObject, localViewGroup);
    if (localObject != null)
    {
      localViewGroup.setVisibility(0);
      localViewGroup.addView((View)localObject);
    }
    localView1.findViewById(R.id.page_container).setVisibility(0);
    localView1.findViewById(R.id.content_container).setVisibility(0);
    if (getPageCount() > 1)
    {
      this.mPageIndicator.setPageCount(getPageCount());
      this.mPageIndicator.onPageSelected(this.mCurrentPageIndex, false);
    }
    if (this.mCurrentPageIndex == getPageCount() - 1) {
      this.mStartButton.setVisibility(0);
    }
    for (;;)
    {
      this.mTitleView.setText(getPageTitle(this.mCurrentPageIndex));
      this.mDescriptionView.setText(getPageDescription(this.mCurrentPageIndex));
      return;
      this.mPageIndicator.setVisibility(0);
    }
  }
  
  protected final boolean isLogoAnimationFinished()
  {
    return this.mLogoAnimationFinished;
  }
  
  protected void moveToNextPage()
  {
    if (!this.mLogoAnimationFinished) {}
    while (this.mCurrentPageIndex >= getPageCount() - 1) {
      return;
    }
    this.mCurrentPageIndex += 1;
    onPageChangedInternal(this.mCurrentPageIndex - 1);
  }
  
  protected void moveToPreviousPage()
  {
    if (!this.mLogoAnimationFinished) {}
    while (this.mCurrentPageIndex <= 0) {
      return;
    }
    this.mCurrentPageIndex -= 1;
    onPageChangedInternal(this.mCurrentPageIndex + 1);
  }
  
  @Nullable
  protected abstract View onCreateBackgroundView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup);
  
  @Nullable
  protected abstract View onCreateContentView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup);
  
  protected Animator onCreateDescriptionAnimator()
  {
    return AnimatorInflater.loadAnimator(getContext(), R.animator.lb_onboarding_description_enter);
  }
  
  @Nullable
  protected Animator onCreateEnterAnimation()
  {
    return null;
  }
  
  @Nullable
  protected abstract View onCreateForegroundView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup);
  
  @Nullable
  protected Animator onCreateLogoAnimation()
  {
    return null;
  }
  
  protected Animator onCreateTitleAnimator()
  {
    return AnimatorInflater.loadAnimator(getContext(), R.animator.lb_onboarding_title_enter);
  }
  
  @Nullable
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    boolean bool = false;
    resolveTheme();
    paramLayoutInflater = (ViewGroup)getThemeInflater(paramLayoutInflater).inflate(R.layout.lb_onboarding_fragment, paramViewGroup, false);
    if (getResources().getConfiguration().getLayoutDirection() == 0) {
      bool = true;
    }
    this.mIsLtr = bool;
    this.mPageIndicator = ((PagingIndicator)paramLayoutInflater.findViewById(R.id.page_indicator));
    this.mPageIndicator.setOnClickListener(this.mOnClickListener);
    this.mPageIndicator.setOnKeyListener(this.mOnKeyListener);
    this.mStartButton = paramLayoutInflater.findViewById(R.id.button_start);
    this.mStartButton.setOnClickListener(this.mOnClickListener);
    this.mStartButton.setOnKeyListener(this.mOnKeyListener);
    this.mMainIconView = ((ImageView)paramLayoutInflater.findViewById(R.id.main_icon));
    this.mLogoView = ((ImageView)paramLayoutInflater.findViewById(R.id.logo));
    this.mTitleView = ((TextView)paramLayoutInflater.findViewById(R.id.title));
    this.mDescriptionView = ((TextView)paramLayoutInflater.findViewById(R.id.description));
    if (this.mTitleViewTextColorSet) {
      this.mTitleView.setTextColor(this.mTitleViewTextColor);
    }
    if (this.mDescriptionViewTextColorSet) {
      this.mDescriptionView.setTextColor(this.mDescriptionViewTextColor);
    }
    if (this.mDotBackgroundColorSet) {
      this.mPageIndicator.setDotBackgroundColor(this.mDotBackgroundColor);
    }
    if (this.mArrowColorSet) {
      this.mPageIndicator.setArrowColor(this.mArrowColor);
    }
    if (this.mArrowBackgroundColorSet) {
      this.mPageIndicator.setDotBackgroundColor(this.mArrowBackgroundColor);
    }
    if (this.mStartButtonTextSet) {
      ((Button)this.mStartButton).setText(this.mStartButtonText);
    }
    paramViewGroup = getContext();
    if (sSlideDistance == 0) {
      sSlideDistance = (int)(60.0F * paramViewGroup.getResources().getDisplayMetrics().scaledDensity);
    }
    paramLayoutInflater.requestFocus();
    return paramLayoutInflater;
  }
  
  protected void onFinishFragment() {}
  
  protected void onLogoAnimationFinished()
  {
    startEnterAnimation(false);
  }
  
  protected void onPageChanged(int paramInt1, int paramInt2) {}
  
  public int onProvideTheme()
  {
    return -1;
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putInt("leanback.onboarding.current_page_index", this.mCurrentPageIndex);
    paramBundle.putBoolean("leanback.onboarding.logo_animation_finished", this.mLogoAnimationFinished);
    paramBundle.putBoolean("leanback.onboarding.enter_animation_finished", this.mEnterAnimationFinished);
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    if (paramBundle == null)
    {
      this.mCurrentPageIndex = 0;
      this.mLogoAnimationFinished = false;
      this.mEnterAnimationFinished = false;
      this.mPageIndicator.onPageSelected(0, false);
      paramView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          OnboardingSupportFragment.this.getView().getViewTreeObserver().removeOnPreDrawListener(this);
          if (!OnboardingSupportFragment.this.startLogoAnimation())
          {
            OnboardingSupportFragment.this.mLogoAnimationFinished = true;
            OnboardingSupportFragment.this.onLogoAnimationFinished();
          }
          return true;
        }
      });
    }
    do
    {
      return;
      this.mCurrentPageIndex = paramBundle.getInt("leanback.onboarding.current_page_index");
      this.mLogoAnimationFinished = paramBundle.getBoolean("leanback.onboarding.logo_animation_finished");
      this.mEnterAnimationFinished = paramBundle.getBoolean("leanback.onboarding.enter_animation_finished");
      if (this.mLogoAnimationFinished) {
        break;
      }
    } while (startLogoAnimation());
    this.mLogoAnimationFinished = true;
    onLogoAnimationFinished();
    return;
    onLogoAnimationFinished();
  }
  
  public void setArrowBackgroundColor(@ColorInt int paramInt)
  {
    this.mArrowBackgroundColor = paramInt;
    this.mArrowBackgroundColorSet = true;
    if (this.mPageIndicator != null) {
      this.mPageIndicator.setArrowBackgroundColor(paramInt);
    }
  }
  
  public void setArrowColor(@ColorInt int paramInt)
  {
    this.mArrowColor = paramInt;
    this.mArrowColorSet = true;
    if (this.mPageIndicator != null) {
      this.mPageIndicator.setArrowColor(paramInt);
    }
  }
  
  public void setDescriptionViewTextColor(@ColorInt int paramInt)
  {
    this.mDescriptionViewTextColor = paramInt;
    this.mDescriptionViewTextColorSet = true;
    if (this.mDescriptionView != null) {
      this.mDescriptionView.setTextColor(paramInt);
    }
  }
  
  public void setDotBackgroundColor(@ColorInt int paramInt)
  {
    this.mDotBackgroundColor = paramInt;
    this.mDotBackgroundColorSet = true;
    if (this.mPageIndicator != null) {
      this.mPageIndicator.setDotBackgroundColor(paramInt);
    }
  }
  
  public final void setIconResouceId(int paramInt)
  {
    this.mIconResourceId = paramInt;
    if (this.mMainIconView != null)
    {
      this.mMainIconView.setImageResource(paramInt);
      this.mMainIconView.setVisibility(0);
    }
  }
  
  public final void setLogoResourceId(int paramInt)
  {
    this.mLogoResourceId = paramInt;
  }
  
  public void setStartButtonText(CharSequence paramCharSequence)
  {
    this.mStartButtonText = paramCharSequence;
    this.mStartButtonTextSet = true;
    if (this.mStartButton != null) {
      ((Button)this.mStartButton).setText(this.mStartButtonText);
    }
  }
  
  public void setTitleViewTextColor(@ColorInt int paramInt)
  {
    this.mTitleViewTextColor = paramInt;
    this.mTitleViewTextColorSet = true;
    if (this.mTitleView != null) {
      this.mTitleView.setTextColor(paramInt);
    }
  }
  
  protected final void startEnterAnimation(boolean paramBoolean)
  {
    hideLogoView();
    if ((this.mEnterAnimationFinished) && (!paramBoolean)) {
      return;
    }
    ArrayList localArrayList = new ArrayList();
    Animator localAnimator = AnimatorInflater.loadAnimator(getContext(), R.animator.lb_onboarding_page_indicator_enter);
    if (getPageCount() <= 1) {}
    for (Object localObject = this.mStartButton;; localObject = this.mPageIndicator)
    {
      localAnimator.setTarget(localObject);
      localArrayList.add(localAnimator);
      localObject = onCreateTitleAnimator();
      if (localObject != null)
      {
        ((Animator)localObject).setTarget(this.mTitleView);
        localArrayList.add(localObject);
      }
      localObject = onCreateDescriptionAnimator();
      if (localObject != null)
      {
        ((Animator)localObject).setTarget(this.mDescriptionView);
        localArrayList.add(localObject);
      }
      localObject = onCreateEnterAnimation();
      if (localObject != null) {
        localArrayList.add(localObject);
      }
      if (localArrayList.isEmpty()) {
        break;
      }
      this.mAnimator = new AnimatorSet();
      this.mAnimator.playTogether(localArrayList);
      this.mAnimator.start();
      this.mAnimator.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          OnboardingSupportFragment.this.mEnterAnimationFinished = true;
        }
      });
      getView().requestFocus();
      return;
    }
  }
  
  boolean startLogoAnimation()
  {
    final Context localContext = getContext();
    Object localObject;
    if (this.mLogoResourceId != 0)
    {
      this.mLogoView.setVisibility(0);
      this.mLogoView.setImageResource(this.mLogoResourceId);
      Animator localAnimator1 = AnimatorInflater.loadAnimator(localContext, R.animator.lb_onboarding_logo_enter);
      Animator localAnimator2 = AnimatorInflater.loadAnimator(localContext, R.animator.lb_onboarding_logo_exit);
      localAnimator2.setStartDelay(1333L);
      localObject = new AnimatorSet();
      ((AnimatorSet)localObject).playSequentially(new Animator[] { localAnimator1, localAnimator2 });
      ((AnimatorSet)localObject).setTarget(this.mLogoView);
    }
    while (localObject != null)
    {
      ((Animator)localObject).addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (localContext != null)
          {
            OnboardingSupportFragment.this.mLogoAnimationFinished = true;
            OnboardingSupportFragment.this.onLogoAnimationFinished();
          }
        }
      });
      ((Animator)localObject).start();
      return true;
      localObject = onCreateLogoAnimation();
    }
    return false;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/app/OnboardingSupportFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */