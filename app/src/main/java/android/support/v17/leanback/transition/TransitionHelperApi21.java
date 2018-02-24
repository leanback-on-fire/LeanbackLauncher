package android.support.v17.leanback.transition;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.RequiresApi;
import android.transition.ChangeTransform;
import android.transition.Transition;
import android.transition.Transition.EpicenterCallback;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;

@RequiresApi(21)
final class TransitionHelperApi21
{
  public static void addSharedElement(FragmentTransaction paramFragmentTransaction, View paramView, String paramString)
  {
    paramFragmentTransaction.addSharedElement(paramView, paramString);
  }
  
  public static void beginDelayedTransition(ViewGroup paramViewGroup, Object paramObject)
  {
    TransitionManager.beginDelayedTransition(paramViewGroup, (Transition)paramObject);
  }
  
  public static Object createChangeTransform()
  {
    return new ChangeTransform();
  }
  
  public static Object createDefaultInterpolator(Context paramContext)
  {
    return AnimationUtils.loadInterpolator(paramContext, 17563663);
  }
  
  public static Object createFadeAndShortSlide(int paramInt)
  {
    return new FadeAndShortSlide(paramInt);
  }
  
  public static Object createFadeAndShortSlide(int paramInt, float paramFloat)
  {
    FadeAndShortSlide localFadeAndShortSlide = new FadeAndShortSlide(paramInt);
    localFadeAndShortSlide.setDistance(paramFloat);
    return localFadeAndShortSlide;
  }
  
  public static Object createScale()
  {
    return new ChangeTransform();
  }
  
  public static Object getEnterTransition(Window paramWindow)
  {
    return paramWindow.getEnterTransition();
  }
  
  public static Object getExitTransition(Window paramWindow)
  {
    return paramWindow.getExitTransition();
  }
  
  public static Object getReenterTransition(Window paramWindow)
  {
    return paramWindow.getReenterTransition();
  }
  
  public static Object getReturnTransition(Window paramWindow)
  {
    return paramWindow.getReturnTransition();
  }
  
  public static Object getSharedElementEnterTransition(Window paramWindow)
  {
    return paramWindow.getSharedElementEnterTransition();
  }
  
  public static Object getSharedElementExitTransition(Window paramWindow)
  {
    return paramWindow.getSharedElementExitTransition();
  }
  
  public static Object getSharedElementReenterTransition(Window paramWindow)
  {
    return paramWindow.getSharedElementReenterTransition();
  }
  
  public static Object getSharedElementReturnTransition(Window paramWindow)
  {
    return paramWindow.getSharedElementReturnTransition();
  }
  
  public static void setEnterTransition(Fragment paramFragment, Object paramObject)
  {
    paramFragment.setEnterTransition((Transition)paramObject);
  }
  
  public static void setEnterTransition(Window paramWindow, Object paramObject)
  {
    paramWindow.setEnterTransition((Transition)paramObject);
  }
  
  public static void setEpicenterCallback(Object paramObject, TransitionEpicenterCallback paramTransitionEpicenterCallback)
  {
    paramObject = (Transition)paramObject;
    if (paramTransitionEpicenterCallback == null)
    {
      ((Transition)paramObject).setEpicenterCallback(null);
      return;
    }
    ((Transition)paramObject).setEpicenterCallback(new Transition.EpicenterCallback()
    {
      public Rect onGetEpicenter(Transition paramAnonymousTransition)
      {
        return this.val$callback.onGetEpicenter(paramAnonymousTransition);
      }
    });
  }
  
  public static void setExitTransition(Fragment paramFragment, Object paramObject)
  {
    paramFragment.setExitTransition((Transition)paramObject);
  }
  
  public static void setReturnTransition(Window paramWindow, Object paramObject)
  {
    paramWindow.setReturnTransition((Transition)paramObject);
  }
  
  public static void setSharedElementEnterTransition(Fragment paramFragment, Object paramObject)
  {
    paramFragment.setSharedElementEnterTransition((Transition)paramObject);
  }
  
  public static void setSharedElementEnterTransition(Window paramWindow, Object paramObject)
  {
    paramWindow.setSharedElementEnterTransition((Transition)paramObject);
  }
  
  public static void setSharedElementReturnTransition(Window paramWindow, Object paramObject)
  {
    paramWindow.setSharedElementReturnTransition((Transition)paramObject);
  }
  
  public static void setTransitionGroup(ViewGroup paramViewGroup, boolean paramBoolean)
  {
    paramViewGroup.setTransitionGroup(paramBoolean);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/transition/TransitionHelperApi21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */