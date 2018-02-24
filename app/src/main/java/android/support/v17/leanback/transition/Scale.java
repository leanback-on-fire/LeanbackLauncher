package android.support.v17.leanback.transition;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.support.annotation.RequiresApi;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;
import java.util.Map;

@RequiresApi(19)
class Scale
  extends Transition
{
  private static final String PROPNAME_SCALE = "android:leanback:scale";
  
  private void captureValues(TransitionValues paramTransitionValues)
  {
    View localView = paramTransitionValues.view;
    paramTransitionValues.values.put("android:leanback:scale", Float.valueOf(localView.getScaleX()));
  }
  
  public void captureEndValues(TransitionValues paramTransitionValues)
  {
    captureValues(paramTransitionValues);
  }
  
  public void captureStartValues(TransitionValues paramTransitionValues)
  {
    captureValues(paramTransitionValues);
  }
  
  public Animator createAnimator(final ViewGroup paramViewGroup, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    if ((paramTransitionValues1 == null) || (paramTransitionValues2 == null)) {
      return null;
    }
    float f1 = ((Float)paramTransitionValues1.values.get("android:leanback:scale")).floatValue();
    float f2 = ((Float)paramTransitionValues2.values.get("android:leanback:scale")).floatValue();
    paramViewGroup = paramTransitionValues1.view;
    paramViewGroup.setScaleX(f1);
    paramViewGroup.setScaleY(f1);
    paramTransitionValues1 = ValueAnimator.ofFloat(new float[] { f1, f2 });
    paramTransitionValues1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        float f = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        paramViewGroup.setScaleX(f);
        paramViewGroup.setScaleY(f);
      }
    });
    return paramTransitionValues1;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/transition/Scale.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */