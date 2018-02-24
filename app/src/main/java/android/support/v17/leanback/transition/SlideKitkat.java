package android.support.v17.leanback.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.RequiresApi;
import android.support.v17.leanback.R.id;
import android.support.v17.leanback.R.styleable;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

@RequiresApi(19)
class SlideKitkat
  extends Visibility
{
  private static final String TAG = "SlideKitkat";
  private static final TimeInterpolator sAccelerate;
  private static final CalculateSlide sCalculateBottom;
  private static final CalculateSlide sCalculateEnd = new CalculateSlideHorizontal()
  {
    public float getGone(View paramAnonymousView)
    {
      if (paramAnonymousView.getLayoutDirection() == 1) {
        return paramAnonymousView.getTranslationX() - paramAnonymousView.getWidth();
      }
      return paramAnonymousView.getTranslationX() + paramAnonymousView.getWidth();
    }
  };
  private static final CalculateSlide sCalculateLeft;
  private static final CalculateSlide sCalculateRight;
  private static final CalculateSlide sCalculateStart;
  private static final CalculateSlide sCalculateTop;
  private static final TimeInterpolator sDecelerate = new DecelerateInterpolator();
  private CalculateSlide mSlideCalculator;
  private int mSlideEdge;
  
  static
  {
    sAccelerate = new AccelerateInterpolator();
    sCalculateLeft = new CalculateSlideHorizontal()
    {
      public float getGone(View paramAnonymousView)
      {
        return paramAnonymousView.getTranslationX() - paramAnonymousView.getWidth();
      }
    };
    sCalculateTop = new CalculateSlideVertical()
    {
      public float getGone(View paramAnonymousView)
      {
        return paramAnonymousView.getTranslationY() - paramAnonymousView.getHeight();
      }
    };
    sCalculateRight = new CalculateSlideHorizontal()
    {
      public float getGone(View paramAnonymousView)
      {
        return paramAnonymousView.getTranslationX() + paramAnonymousView.getWidth();
      }
    };
    sCalculateBottom = new CalculateSlideVertical()
    {
      public float getGone(View paramAnonymousView)
      {
        return paramAnonymousView.getTranslationY() + paramAnonymousView.getHeight();
      }
    };
    sCalculateStart = new CalculateSlideHorizontal()
    {
      public float getGone(View paramAnonymousView)
      {
        if (paramAnonymousView.getLayoutDirection() == 1) {
          return paramAnonymousView.getTranslationX() + paramAnonymousView.getWidth();
        }
        return paramAnonymousView.getTranslationX() - paramAnonymousView.getWidth();
      }
    };
  }
  
  public SlideKitkat()
  {
    setSlideEdge(80);
  }
  
  public SlideKitkat(Context paramContext, AttributeSet paramAttributeSet)
  {
    paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.lbSlide);
    setSlideEdge(paramAttributeSet.getInt(R.styleable.lbSlide_lb_slideEdge, 80));
    long l = paramAttributeSet.getInt(R.styleable.lbSlide_android_duration, -1);
    if (l >= 0L) {
      setDuration(l);
    }
    l = paramAttributeSet.getInt(R.styleable.lbSlide_android_startDelay, -1);
    if (l > 0L) {
      setStartDelay(l);
    }
    int i = paramAttributeSet.getResourceId(R.styleable.lbSlide_android_interpolator, 0);
    if (i > 0) {
      setInterpolator(AnimationUtils.loadInterpolator(paramContext, i));
    }
    paramAttributeSet.recycle();
  }
  
  private Animator createAnimation(View paramView, Property<View, Float> paramProperty, float paramFloat1, float paramFloat2, float paramFloat3, TimeInterpolator paramTimeInterpolator, int paramInt)
  {
    Object localObject = (float[])paramView.getTag(R.id.lb_slide_transition_value);
    if (localObject != null) {
      if (View.TRANSLATION_Y != paramProperty) {
        break label97;
      }
    }
    label97:
    for (paramFloat1 = localObject[1];; paramFloat1 = localObject[0])
    {
      paramView.setTag(R.id.lb_slide_transition_value, null);
      localObject = ObjectAnimator.ofFloat(paramView, paramProperty, new float[] { paramFloat1, paramFloat2 });
      paramView = new SlideAnimatorListener(paramView, paramProperty, paramFloat3, paramFloat2, paramInt);
      ((ObjectAnimator)localObject).addListener(paramView);
      ((ObjectAnimator)localObject).addPauseListener(paramView);
      ((ObjectAnimator)localObject).setInterpolator(paramTimeInterpolator);
      return (Animator)localObject;
    }
  }
  
  public int getSlideEdge()
  {
    return this.mSlideEdge;
  }
  
  public Animator onAppear(ViewGroup paramViewGroup, TransitionValues paramTransitionValues1, int paramInt1, TransitionValues paramTransitionValues2, int paramInt2)
  {
    if (paramTransitionValues2 != null) {}
    for (paramViewGroup = paramTransitionValues2.view; paramViewGroup == null; paramViewGroup = null) {
      return null;
    }
    float f1 = this.mSlideCalculator.getHere(paramViewGroup);
    float f2 = this.mSlideCalculator.getGone(paramViewGroup);
    return createAnimation(paramViewGroup, this.mSlideCalculator.getProperty(), f2, f1, f1, sDecelerate, 0);
  }
  
  public Animator onDisappear(ViewGroup paramViewGroup, TransitionValues paramTransitionValues1, int paramInt1, TransitionValues paramTransitionValues2, int paramInt2)
  {
    if (paramTransitionValues1 != null) {}
    for (paramViewGroup = paramTransitionValues1.view; paramViewGroup == null; paramViewGroup = null) {
      return null;
    }
    float f1 = this.mSlideCalculator.getHere(paramViewGroup);
    float f2 = this.mSlideCalculator.getGone(paramViewGroup);
    return createAnimation(paramViewGroup, this.mSlideCalculator.getProperty(), f1, f2, f1, sAccelerate, 4);
  }
  
  public void setSlideEdge(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Invalid slide direction");
    case 3: 
      this.mSlideCalculator = sCalculateLeft;
    }
    for (;;)
    {
      this.mSlideEdge = paramInt;
      return;
      this.mSlideCalculator = sCalculateTop;
      continue;
      this.mSlideCalculator = sCalculateRight;
      continue;
      this.mSlideCalculator = sCalculateBottom;
      continue;
      this.mSlideCalculator = sCalculateStart;
      continue;
      this.mSlideCalculator = sCalculateEnd;
    }
  }
  
  private static abstract interface CalculateSlide
  {
    public abstract float getGone(View paramView);
    
    public abstract float getHere(View paramView);
    
    public abstract Property<View, Float> getProperty();
  }
  
  private static abstract class CalculateSlideHorizontal
    implements SlideKitkat.CalculateSlide
  {
    public float getHere(View paramView)
    {
      return paramView.getTranslationX();
    }
    
    public Property<View, Float> getProperty()
    {
      return View.TRANSLATION_X;
    }
  }
  
  private static abstract class CalculateSlideVertical
    implements SlideKitkat.CalculateSlide
  {
    public float getHere(View paramView)
    {
      return paramView.getTranslationY();
    }
    
    public Property<View, Float> getProperty()
    {
      return View.TRANSLATION_Y;
    }
  }
  
  private static class SlideAnimatorListener
    extends AnimatorListenerAdapter
  {
    private boolean mCanceled = false;
    private final float mEndValue;
    private final int mFinalVisibility;
    private float mPausedValue;
    private final Property<View, Float> mProp;
    private final float mTerminalValue;
    private final View mView;
    
    public SlideAnimatorListener(View paramView, Property<View, Float> paramProperty, float paramFloat1, float paramFloat2, int paramInt)
    {
      this.mProp = paramProperty;
      this.mView = paramView;
      this.mTerminalValue = paramFloat1;
      this.mEndValue = paramFloat2;
      this.mFinalVisibility = paramInt;
      paramView.setVisibility(0);
    }
    
    public void onAnimationCancel(Animator paramAnimator)
    {
      float f1 = this.mView.getTranslationX();
      float f2 = this.mView.getTranslationY();
      this.mView.setTag(R.id.lb_slide_transition_value, new float[] { f1, f2 });
      this.mProp.set(this.mView, Float.valueOf(this.mTerminalValue));
      this.mCanceled = true;
    }
    
    public void onAnimationEnd(Animator paramAnimator)
    {
      if (!this.mCanceled) {
        this.mProp.set(this.mView, Float.valueOf(this.mTerminalValue));
      }
      this.mView.setVisibility(this.mFinalVisibility);
    }
    
    public void onAnimationPause(Animator paramAnimator)
    {
      this.mPausedValue = ((Float)this.mProp.get(this.mView)).floatValue();
      this.mProp.set(this.mView, Float.valueOf(this.mEndValue));
      this.mView.setVisibility(this.mFinalVisibility);
    }
    
    public void onAnimationResume(Animator paramAnimator)
    {
      this.mProp.set(this.mView, Float.valueOf(this.mPausedValue));
      this.mView.setVisibility(0);
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/transition/SlideKitkat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */