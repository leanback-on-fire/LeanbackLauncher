package android.support.v17.leanback.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.AnimatorSet.Builder;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v17.leanback.R.styleable;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.Transition.EpicenterCallback;
import android.transition.Transition.TransitionListener;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import java.util.Map;

@RequiresApi(21)
@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class FadeAndShortSlide
  extends Visibility
{
  private static final String PROPNAME_SCREEN_POSITION = "android:fadeAndShortSlideTransition:screenPosition";
  static final CalculateSlide sCalculateBottom = new CalculateSlide()
  {
    public float getGoneY(FadeAndShortSlide paramAnonymousFadeAndShortSlide, ViewGroup paramAnonymousViewGroup, View paramAnonymousView, int[] paramAnonymousArrayOfInt)
    {
      return paramAnonymousView.getTranslationY() + paramAnonymousFadeAndShortSlide.getVerticalDistance(paramAnonymousViewGroup);
    }
  };
  static final CalculateSlide sCalculateEnd;
  static final CalculateSlide sCalculateStart;
  static final CalculateSlide sCalculateStartEnd;
  static final CalculateSlide sCalculateTop = new CalculateSlide()
  {
    public float getGoneY(FadeAndShortSlide paramAnonymousFadeAndShortSlide, ViewGroup paramAnonymousViewGroup, View paramAnonymousView, int[] paramAnonymousArrayOfInt)
    {
      return paramAnonymousView.getTranslationY() - paramAnonymousFadeAndShortSlide.getVerticalDistance(paramAnonymousViewGroup);
    }
  };
  private static final TimeInterpolator sDecelerate = new DecelerateInterpolator();
  private float mDistance = -1.0F;
  private Visibility mFade = new Fade();
  private CalculateSlide mSlideCalculator;
  final CalculateSlide sCalculateTopBottom = new CalculateSlide()
  {
    public float getGoneY(FadeAndShortSlide paramAnonymousFadeAndShortSlide, ViewGroup paramAnonymousViewGroup, View paramAnonymousView, int[] paramAnonymousArrayOfInt)
    {
      int j = paramAnonymousArrayOfInt[1];
      int k = paramAnonymousView.getHeight() / 2;
      paramAnonymousViewGroup.getLocationOnScreen(paramAnonymousArrayOfInt);
      Rect localRect = FadeAndShortSlide.this.getEpicenter();
      if (localRect == null) {}
      for (int i = paramAnonymousArrayOfInt[1] + paramAnonymousViewGroup.getHeight() / 2; j + k < i; i = localRect.centerY()) {
        return paramAnonymousView.getTranslationY() - paramAnonymousFadeAndShortSlide.getVerticalDistance(paramAnonymousViewGroup);
      }
      return paramAnonymousView.getTranslationY() + paramAnonymousFadeAndShortSlide.getVerticalDistance(paramAnonymousViewGroup);
    }
  };
  
  static
  {
    sCalculateStart = new CalculateSlide()
    {
      public float getGoneX(FadeAndShortSlide paramAnonymousFadeAndShortSlide, ViewGroup paramAnonymousViewGroup, View paramAnonymousView, int[] paramAnonymousArrayOfInt)
      {
        int i = 1;
        if (paramAnonymousViewGroup.getLayoutDirection() == 1) {}
        while (i != 0)
        {
          return paramAnonymousView.getTranslationX() + paramAnonymousFadeAndShortSlide.getHorizontalDistance(paramAnonymousViewGroup);
          i = 0;
        }
        return paramAnonymousView.getTranslationX() - paramAnonymousFadeAndShortSlide.getHorizontalDistance(paramAnonymousViewGroup);
      }
    };
    sCalculateEnd = new CalculateSlide()
    {
      public float getGoneX(FadeAndShortSlide paramAnonymousFadeAndShortSlide, ViewGroup paramAnonymousViewGroup, View paramAnonymousView, int[] paramAnonymousArrayOfInt)
      {
        int i = 1;
        if (paramAnonymousViewGroup.getLayoutDirection() == 1) {}
        while (i != 0)
        {
          return paramAnonymousView.getTranslationX() - paramAnonymousFadeAndShortSlide.getHorizontalDistance(paramAnonymousViewGroup);
          i = 0;
        }
        return paramAnonymousView.getTranslationX() + paramAnonymousFadeAndShortSlide.getHorizontalDistance(paramAnonymousViewGroup);
      }
    };
    sCalculateStartEnd = new CalculateSlide()
    {
      public float getGoneX(FadeAndShortSlide paramAnonymousFadeAndShortSlide, ViewGroup paramAnonymousViewGroup, View paramAnonymousView, int[] paramAnonymousArrayOfInt)
      {
        int j = paramAnonymousArrayOfInt[0];
        int k = paramAnonymousView.getWidth() / 2;
        paramAnonymousViewGroup.getLocationOnScreen(paramAnonymousArrayOfInt);
        Rect localRect = paramAnonymousFadeAndShortSlide.getEpicenter();
        if (localRect == null) {}
        for (int i = paramAnonymousArrayOfInt[0] + paramAnonymousViewGroup.getWidth() / 2; j + k < i; i = localRect.centerX()) {
          return paramAnonymousView.getTranslationX() - paramAnonymousFadeAndShortSlide.getHorizontalDistance(paramAnonymousViewGroup);
        }
        return paramAnonymousView.getTranslationX() + paramAnonymousFadeAndShortSlide.getHorizontalDistance(paramAnonymousViewGroup);
      }
    };
  }
  
  public FadeAndShortSlide()
  {
    this(8388611);
  }
  
  public FadeAndShortSlide(int paramInt)
  {
    setSlideEdge(paramInt);
  }
  
  public FadeAndShortSlide(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.lbSlide);
    setSlideEdge(paramContext.getInt(R.styleable.lbSlide_lb_slideEdge, 8388611));
    paramContext.recycle();
  }
  
  private void captureValues(TransitionValues paramTransitionValues)
  {
    View localView = paramTransitionValues.view;
    int[] arrayOfInt = new int[2];
    localView.getLocationOnScreen(arrayOfInt);
    paramTransitionValues.values.put("android:fadeAndShortSlideTransition:screenPosition", arrayOfInt);
  }
  
  public Transition addListener(Transition.TransitionListener paramTransitionListener)
  {
    this.mFade.addListener(paramTransitionListener);
    return super.addListener(paramTransitionListener);
  }
  
  public void captureEndValues(TransitionValues paramTransitionValues)
  {
    this.mFade.captureEndValues(paramTransitionValues);
    super.captureEndValues(paramTransitionValues);
    captureValues(paramTransitionValues);
  }
  
  public void captureStartValues(TransitionValues paramTransitionValues)
  {
    this.mFade.captureStartValues(paramTransitionValues);
    super.captureStartValues(paramTransitionValues);
    captureValues(paramTransitionValues);
  }
  
  public Transition clone()
  {
    FadeAndShortSlide localFadeAndShortSlide = (FadeAndShortSlide)super.clone();
    localFadeAndShortSlide.mFade = ((Visibility)this.mFade.clone());
    return localFadeAndShortSlide;
  }
  
  public float getDistance()
  {
    return this.mDistance;
  }
  
  float getHorizontalDistance(ViewGroup paramViewGroup)
  {
    if (this.mDistance >= 0.0F) {
      return this.mDistance;
    }
    return paramViewGroup.getWidth() / 4;
  }
  
  float getVerticalDistance(ViewGroup paramViewGroup)
  {
    if (this.mDistance >= 0.0F) {
      return this.mDistance;
    }
    return paramViewGroup.getHeight() / 4;
  }
  
  public Animator onAppear(ViewGroup paramViewGroup, View paramView, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    if (paramTransitionValues2 == null) {
      paramViewGroup = null;
    }
    Object localObject;
    do
    {
      return paramViewGroup;
      if (paramViewGroup == paramView) {
        return null;
      }
      localObject = (int[])paramTransitionValues2.values.get("android:fadeAndShortSlideTransition:screenPosition");
      int i = localObject[0];
      int j = localObject[1];
      float f1 = paramView.getTranslationX();
      float f2 = this.mSlideCalculator.getGoneX(this, paramViewGroup, paramView, (int[])localObject);
      float f3 = paramView.getTranslationY();
      localObject = TranslationAnimationCreator.createAnimation(paramView, paramTransitionValues2, i, j, f2, this.mSlideCalculator.getGoneY(this, paramViewGroup, paramView, (int[])localObject), f1, f3, sDecelerate, this);
      paramView = this.mFade.onAppear(paramViewGroup, paramView, paramTransitionValues1, paramTransitionValues2);
      paramViewGroup = paramView;
    } while (localObject == null);
    if (paramView == null) {
      return (Animator)localObject;
    }
    paramViewGroup = new AnimatorSet();
    paramViewGroup.play((Animator)localObject).with(paramView);
    return paramViewGroup;
  }
  
  public Animator onDisappear(ViewGroup paramViewGroup, View paramView, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    if (paramTransitionValues1 == null) {
      paramViewGroup = null;
    }
    Object localObject;
    do
    {
      return paramViewGroup;
      if (paramViewGroup == paramView) {
        return null;
      }
      localObject = (int[])paramTransitionValues1.values.get("android:fadeAndShortSlideTransition:screenPosition");
      int i = localObject[0];
      int j = localObject[1];
      float f1 = paramView.getTranslationX();
      float f2 = this.mSlideCalculator.getGoneX(this, paramViewGroup, paramView, (int[])localObject);
      localObject = TranslationAnimationCreator.createAnimation(paramView, paramTransitionValues1, i, j, f1, paramView.getTranslationY(), f2, this.mSlideCalculator.getGoneY(this, paramViewGroup, paramView, (int[])localObject), sDecelerate, this);
      paramView = this.mFade.onDisappear(paramViewGroup, paramView, paramTransitionValues1, paramTransitionValues2);
      paramViewGroup = paramView;
    } while (localObject == null);
    if (paramView == null) {
      return (Animator)localObject;
    }
    paramViewGroup = new AnimatorSet();
    paramViewGroup.play((Animator)localObject).with(paramView);
    return paramViewGroup;
  }
  
  public Transition removeListener(Transition.TransitionListener paramTransitionListener)
  {
    this.mFade.removeListener(paramTransitionListener);
    return super.removeListener(paramTransitionListener);
  }
  
  public void setDistance(float paramFloat)
  {
    this.mDistance = paramFloat;
  }
  
  public void setEpicenterCallback(Transition.EpicenterCallback paramEpicenterCallback)
  {
    this.mFade.setEpicenterCallback(paramEpicenterCallback);
    super.setEpicenterCallback(paramEpicenterCallback);
  }
  
  public void setSlideEdge(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("Invalid slide direction");
    case 8388611: 
      this.mSlideCalculator = sCalculateStart;
      return;
    case 8388613: 
      this.mSlideCalculator = sCalculateEnd;
      return;
    case 8388615: 
      this.mSlideCalculator = sCalculateStartEnd;
      return;
    case 48: 
      this.mSlideCalculator = sCalculateTop;
      return;
    case 80: 
      this.mSlideCalculator = sCalculateBottom;
      return;
    }
    this.mSlideCalculator = this.sCalculateTopBottom;
  }
  
  private static abstract class CalculateSlide
  {
    float getGoneX(FadeAndShortSlide paramFadeAndShortSlide, ViewGroup paramViewGroup, View paramView, int[] paramArrayOfInt)
    {
      return paramView.getTranslationX();
    }
    
    float getGoneY(FadeAndShortSlide paramFadeAndShortSlide, ViewGroup paramViewGroup, View paramView, int[] paramArrayOfInt)
    {
      return paramView.getTranslationY();
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/transition/FadeAndShortSlide.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */