package android.support.v17.leanback.transition;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.support.annotation.RequiresApi;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.Transition.TransitionListener;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.HashMap;

@RequiresApi(19)
final class TransitionHelperKitkat
{
  static void addTarget(Object paramObject, View paramView)
  {
    ((Transition)paramObject).addTarget(paramView);
  }
  
  static void addTransition(Object paramObject1, Object paramObject2)
  {
    ((TransitionSet)paramObject1).addTransition((Transition)paramObject2);
  }
  
  static void addTransitionListener(Object paramObject, TransitionListener paramTransitionListener)
  {
    if (paramTransitionListener == null) {
      return;
    }
    paramObject = (Transition)paramObject;
    paramTransitionListener.mImpl = new Transition.TransitionListener()
    {
      public void onTransitionCancel(Transition paramAnonymousTransition)
      {
        this.val$listener.onTransitionCancel(paramAnonymousTransition);
      }
      
      public void onTransitionEnd(Transition paramAnonymousTransition)
      {
        this.val$listener.onTransitionEnd(paramAnonymousTransition);
      }
      
      public void onTransitionPause(Transition paramAnonymousTransition)
      {
        this.val$listener.onTransitionPause(paramAnonymousTransition);
      }
      
      public void onTransitionResume(Transition paramAnonymousTransition)
      {
        this.val$listener.onTransitionResume(paramAnonymousTransition);
      }
      
      public void onTransitionStart(Transition paramAnonymousTransition)
      {
        this.val$listener.onTransitionStart(paramAnonymousTransition);
      }
    };
    ((Transition)paramObject).addListener((Transition.TransitionListener)paramTransitionListener.mImpl);
  }
  
  static Object createAutoTransition()
  {
    return new AutoTransition();
  }
  
  static Object createChangeBounds(boolean paramBoolean)
  {
    CustomChangeBounds localCustomChangeBounds = new CustomChangeBounds();
    localCustomChangeBounds.setReparent(paramBoolean);
    return localCustomChangeBounds;
  }
  
  static Object createFadeTransition(int paramInt)
  {
    return new Fade(paramInt);
  }
  
  static Object createScale()
  {
    return new Scale();
  }
  
  static Object createScene(ViewGroup paramViewGroup, Runnable paramRunnable)
  {
    paramViewGroup = new Scene(paramViewGroup);
    paramViewGroup.setEnterAction(paramRunnable);
    return paramViewGroup;
  }
  
  static Object createSlide(int paramInt)
  {
    SlideKitkat localSlideKitkat = new SlideKitkat();
    localSlideKitkat.setSlideEdge(paramInt);
    return localSlideKitkat;
  }
  
  static Object createTransitionSet(boolean paramBoolean)
  {
    TransitionSet localTransitionSet = new TransitionSet();
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localTransitionSet.setOrdering(i);
      return localTransitionSet;
    }
  }
  
  static void exclude(Object paramObject, int paramInt, boolean paramBoolean)
  {
    ((Transition)paramObject).excludeTarget(paramInt, paramBoolean);
  }
  
  static void exclude(Object paramObject, View paramView, boolean paramBoolean)
  {
    ((Transition)paramObject).excludeTarget(paramView, paramBoolean);
  }
  
  static void excludeChildren(Object paramObject, int paramInt, boolean paramBoolean)
  {
    ((Transition)paramObject).excludeChildren(paramInt, paramBoolean);
  }
  
  static void excludeChildren(Object paramObject, View paramView, boolean paramBoolean)
  {
    ((Transition)paramObject).excludeChildren(paramView, paramBoolean);
  }
  
  static void include(Object paramObject, int paramInt)
  {
    ((Transition)paramObject).addTarget(paramInt);
  }
  
  static void include(Object paramObject, View paramView)
  {
    ((Transition)paramObject).addTarget(paramView);
  }
  
  static Object loadTransition(Context paramContext, int paramInt)
  {
    return TransitionInflater.from(paramContext).inflateTransition(paramInt);
  }
  
  static void removeTransitionListener(Object paramObject, TransitionListener paramTransitionListener)
  {
    if ((paramTransitionListener == null) || (paramTransitionListener.mImpl == null)) {
      return;
    }
    ((Transition)paramObject).removeListener((Transition.TransitionListener)paramTransitionListener.mImpl);
    paramTransitionListener.mImpl = null;
  }
  
  static void runTransition(Object paramObject1, Object paramObject2)
  {
    TransitionManager.go((Scene)paramObject1, (Transition)paramObject2);
  }
  
  static void setChangeBoundsDefaultStartDelay(Object paramObject, int paramInt)
  {
    ((CustomChangeBounds)paramObject).setDefaultStartDelay(paramInt);
  }
  
  static void setChangeBoundsStartDelay(Object paramObject, int paramInt1, int paramInt2)
  {
    ((CustomChangeBounds)paramObject).setStartDelay(paramInt1, paramInt2);
  }
  
  static void setChangeBoundsStartDelay(Object paramObject, View paramView, int paramInt)
  {
    ((CustomChangeBounds)paramObject).setStartDelay(paramView, paramInt);
  }
  
  static void setChangeBoundsStartDelay(Object paramObject, String paramString, int paramInt)
  {
    ((CustomChangeBounds)paramObject).setStartDelay(paramString, paramInt);
  }
  
  static void setDuration(Object paramObject, long paramLong)
  {
    ((Transition)paramObject).setDuration(paramLong);
  }
  
  static void setInterpolator(Object paramObject1, Object paramObject2)
  {
    ((Transition)paramObject1).setInterpolator((TimeInterpolator)paramObject2);
  }
  
  static void setStartDelay(Object paramObject, long paramLong)
  {
    ((Transition)paramObject).setStartDelay(paramLong);
  }
  
  static class CustomChangeBounds
    extends ChangeBounds
  {
    final HashMap<String, Integer> mClassStartDelays = new HashMap();
    int mDefaultStartDelay;
    final SparseIntArray mIdStartDelays = new SparseIntArray();
    final HashMap<View, Integer> mViewStartDelays = new HashMap();
    
    private int getDelay(View paramView)
    {
      Integer localInteger = (Integer)this.mViewStartDelays.get(paramView);
      int i;
      if (localInteger != null) {
        i = localInteger.intValue();
      }
      int j;
      do
      {
        return i;
        j = this.mIdStartDelays.get(paramView.getId(), -1);
        i = j;
      } while (j != -1);
      paramView = (Integer)this.mClassStartDelays.get(paramView.getClass().getName());
      if (paramView != null) {
        return paramView.intValue();
      }
      return this.mDefaultStartDelay;
    }
    
    public Animator createAnimator(ViewGroup paramViewGroup, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
    {
      paramViewGroup = super.createAnimator(paramViewGroup, paramTransitionValues1, paramTransitionValues2);
      if ((paramViewGroup != null) && (paramTransitionValues2 != null) && (paramTransitionValues2.view != null)) {
        paramViewGroup.setStartDelay(getDelay(paramTransitionValues2.view));
      }
      return paramViewGroup;
    }
    
    public void setDefaultStartDelay(int paramInt)
    {
      this.mDefaultStartDelay = paramInt;
    }
    
    public void setStartDelay(int paramInt1, int paramInt2)
    {
      this.mIdStartDelays.put(paramInt1, paramInt2);
    }
    
    public void setStartDelay(View paramView, int paramInt)
    {
      this.mViewStartDelays.put(paramView, Integer.valueOf(paramInt));
    }
    
    public void setStartDelay(String paramString, int paramInt)
    {
      this.mClassStartDelays.put(paramString, Integer.valueOf(paramInt));
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/transition/TransitionHelperKitkat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */