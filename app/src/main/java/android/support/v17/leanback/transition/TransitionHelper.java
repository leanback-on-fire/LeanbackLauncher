package android.support.v17.leanback.transition;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.annotation.RestrictTo;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import java.util.ArrayList;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public final class TransitionHelper
{
  public static final int FADE_IN = 1;
  public static final int FADE_OUT = 2;
  public static final int SLIDE_BOTTOM = 80;
  public static final int SLIDE_LEFT = 3;
  public static final int SLIDE_RIGHT = 5;
  public static final int SLIDE_TOP = 48;
  private static TransitionHelperVersionImpl sImpl = new TransitionHelperStubImpl();
  
  static
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      sImpl = new TransitionHelperApi21Impl();
      return;
    }
    if (systemSupportsTransitions())
    {
      sImpl = new TransitionHelperKitkatImpl();
      return;
    }
  }
  
  public static void addSharedElement(android.app.FragmentTransaction paramFragmentTransaction, View paramView, String paramString)
  {
    sImpl.addSharedElement(paramFragmentTransaction, paramView, paramString);
  }
  
  public static void addSharedElement(android.support.v4.app.FragmentTransaction paramFragmentTransaction, View paramView, String paramString)
  {
    paramFragmentTransaction.addSharedElement(paramView, paramString);
  }
  
  public static void addTarget(Object paramObject, View paramView)
  {
    sImpl.addTarget(paramObject, paramView);
  }
  
  public static void addTransition(Object paramObject1, Object paramObject2)
  {
    sImpl.addTransition(paramObject1, paramObject2);
  }
  
  public static void addTransitionListener(Object paramObject, TransitionListener paramTransitionListener)
  {
    sImpl.addTransitionListener(paramObject, paramTransitionListener);
  }
  
  public static void beginDelayedTransition(ViewGroup paramViewGroup, Object paramObject)
  {
    sImpl.beginDelayedTransition(paramViewGroup, paramObject);
  }
  
  public static Object createAutoTransition()
  {
    return sImpl.createAutoTransition();
  }
  
  public static Object createChangeBounds(boolean paramBoolean)
  {
    return sImpl.createChangeBounds(paramBoolean);
  }
  
  public static Object createChangeTransform()
  {
    return sImpl.createChangeTransform();
  }
  
  public static Object createDefaultInterpolator(Context paramContext)
  {
    return sImpl.createDefaultInterpolator(paramContext);
  }
  
  public static Object createFadeAndShortSlide(int paramInt)
  {
    return sImpl.createFadeAndShortSlide(paramInt);
  }
  
  public static Object createFadeAndShortSlide(int paramInt, float paramFloat)
  {
    return sImpl.createFadeAndShortSlide(paramInt, paramFloat);
  }
  
  public static Object createFadeTransition(int paramInt)
  {
    return sImpl.createFadeTransition(paramInt);
  }
  
  public static Object createScale()
  {
    return sImpl.createScale();
  }
  
  public static Object createScene(ViewGroup paramViewGroup, Runnable paramRunnable)
  {
    return sImpl.createScene(paramViewGroup, paramRunnable);
  }
  
  public static Object createSlide(int paramInt)
  {
    return sImpl.createSlide(paramInt);
  }
  
  public static Object createTransitionSet(boolean paramBoolean)
  {
    return sImpl.createTransitionSet(paramBoolean);
  }
  
  public static void exclude(Object paramObject, int paramInt, boolean paramBoolean)
  {
    sImpl.exclude(paramObject, paramInt, paramBoolean);
  }
  
  public static void exclude(Object paramObject, View paramView, boolean paramBoolean)
  {
    sImpl.exclude(paramObject, paramView, paramBoolean);
  }
  
  public static void excludeChildren(Object paramObject, int paramInt, boolean paramBoolean)
  {
    sImpl.excludeChildren(paramObject, paramInt, paramBoolean);
  }
  
  public static void excludeChildren(Object paramObject, View paramView, boolean paramBoolean)
  {
    sImpl.excludeChildren(paramObject, paramView, paramBoolean);
  }
  
  public static Object getEnterTransition(Window paramWindow)
  {
    return sImpl.getEnterTransition(paramWindow);
  }
  
  public static Object getExitTransition(Window paramWindow)
  {
    return sImpl.getExitTransition(paramWindow);
  }
  
  @Deprecated
  public static TransitionHelper getInstance()
  {
    return new TransitionHelper();
  }
  
  public static Object getReenterTransition(Window paramWindow)
  {
    return sImpl.getReenterTransition(paramWindow);
  }
  
  public static Object getReturnTransition(Window paramWindow)
  {
    return sImpl.getReturnTransition(paramWindow);
  }
  
  public static Object getSharedElementEnterTransition(Window paramWindow)
  {
    return sImpl.getSharedElementEnterTransition(paramWindow);
  }
  
  public static Object getSharedElementExitTransition(Window paramWindow)
  {
    return sImpl.getSharedElementExitTransition(paramWindow);
  }
  
  public static Object getSharedElementReenterTransition(Window paramWindow)
  {
    return sImpl.getSharedElementReenterTransition(paramWindow);
  }
  
  public static Object getSharedElementReturnTransition(Window paramWindow)
  {
    return sImpl.getSharedElementReturnTransition(paramWindow);
  }
  
  public static void include(Object paramObject, int paramInt)
  {
    sImpl.include(paramObject, paramInt);
  }
  
  public static void include(Object paramObject, View paramView)
  {
    sImpl.include(paramObject, paramView);
  }
  
  public static Object loadTransition(Context paramContext, int paramInt)
  {
    return sImpl.loadTransition(paramContext, paramInt);
  }
  
  public static void removeTransitionListener(Object paramObject, TransitionListener paramTransitionListener)
  {
    sImpl.removeTransitionListener(paramObject, paramTransitionListener);
  }
  
  public static void runTransition(Object paramObject1, Object paramObject2)
  {
    sImpl.runTransition(paramObject1, paramObject2);
  }
  
  public static void setChangeBoundsDefaultStartDelay(Object paramObject, int paramInt)
  {
    sImpl.setChangeBoundsDefaultStartDelay(paramObject, paramInt);
  }
  
  public static void setChangeBoundsStartDelay(Object paramObject, int paramInt1, int paramInt2)
  {
    sImpl.setChangeBoundsStartDelay(paramObject, paramInt1, paramInt2);
  }
  
  public static void setChangeBoundsStartDelay(Object paramObject, View paramView, int paramInt)
  {
    sImpl.setChangeBoundsStartDelay(paramObject, paramView, paramInt);
  }
  
  public static void setChangeBoundsStartDelay(Object paramObject, String paramString, int paramInt)
  {
    sImpl.setChangeBoundsStartDelay(paramObject, paramString, paramInt);
  }
  
  public static void setDuration(Object paramObject, long paramLong)
  {
    sImpl.setDuration(paramObject, paramLong);
  }
  
  public static void setEnterTransition(android.app.Fragment paramFragment, Object paramObject)
  {
    sImpl.setEnterTransition(paramFragment, paramObject);
  }
  
  public static void setEnterTransition(android.support.v4.app.Fragment paramFragment, Object paramObject)
  {
    paramFragment.setEnterTransition(paramObject);
  }
  
  public static void setEnterTransition(Window paramWindow, Object paramObject)
  {
    sImpl.setEnterTransition(paramWindow, paramObject);
  }
  
  public static void setEpicenterCallback(Object paramObject, TransitionEpicenterCallback paramTransitionEpicenterCallback)
  {
    sImpl.setEpicenterCallback(paramObject, paramTransitionEpicenterCallback);
  }
  
  public static void setExitTransition(android.app.Fragment paramFragment, Object paramObject)
  {
    sImpl.setExitTransition(paramFragment, paramObject);
  }
  
  public static void setExitTransition(android.support.v4.app.Fragment paramFragment, Object paramObject)
  {
    paramFragment.setExitTransition(paramObject);
  }
  
  public static void setInterpolator(Object paramObject1, Object paramObject2)
  {
    sImpl.setInterpolator(paramObject1, paramObject2);
  }
  
  public static void setReturnTransition(Window paramWindow, Object paramObject)
  {
    sImpl.setReturnTransition(paramWindow, paramObject);
  }
  
  public static void setSharedElementEnterTransition(android.app.Fragment paramFragment, Object paramObject)
  {
    sImpl.setSharedElementEnterTransition(paramFragment, paramObject);
  }
  
  public static void setSharedElementEnterTransition(android.support.v4.app.Fragment paramFragment, Object paramObject)
  {
    paramFragment.setSharedElementEnterTransition(paramObject);
  }
  
  public static void setSharedElementEnterTransition(Window paramWindow, Object paramObject)
  {
    sImpl.setSharedElementEnterTransition(paramWindow, paramObject);
  }
  
  public static void setSharedElementReturnTransition(Window paramWindow, Object paramObject)
  {
    sImpl.setSharedElementReturnTransition(paramWindow, paramObject);
  }
  
  public static void setStartDelay(Object paramObject, long paramLong)
  {
    sImpl.setStartDelay(paramObject, paramLong);
  }
  
  public static void setTransitionGroup(ViewGroup paramViewGroup, boolean paramBoolean)
  {
    sImpl.setTransitionGroup(paramViewGroup, paramBoolean);
  }
  
  @Deprecated
  public static void setTransitionListener(Object paramObject, TransitionListener paramTransitionListener)
  {
    sImpl.addTransitionListener(paramObject, paramTransitionListener);
  }
  
  public static boolean systemSupportsEntranceTransitions()
  {
    return Build.VERSION.SDK_INT >= 21;
  }
  
  public static boolean systemSupportsTransitions()
  {
    return Build.VERSION.SDK_INT >= 19;
  }
  
  static final class TransitionHelperApi21Impl
    extends TransitionHelper.TransitionHelperKitkatImpl
  {
    public void addSharedElement(android.app.FragmentTransaction paramFragmentTransaction, View paramView, String paramString)
    {
      TransitionHelperApi21.addSharedElement(paramFragmentTransaction, paramView, paramString);
    }
    
    public void beginDelayedTransition(ViewGroup paramViewGroup, Object paramObject)
    {
      TransitionHelperApi21.beginDelayedTransition(paramViewGroup, paramObject);
    }
    
    public Object createChangeTransform()
    {
      return TransitionHelperApi21.createChangeTransform();
    }
    
    public Object createDefaultInterpolator(Context paramContext)
    {
      return TransitionHelperApi21.createDefaultInterpolator(paramContext);
    }
    
    public Object createFadeAndShortSlide(int paramInt)
    {
      return TransitionHelperApi21.createFadeAndShortSlide(paramInt);
    }
    
    public Object createFadeAndShortSlide(int paramInt, float paramFloat)
    {
      return TransitionHelperApi21.createFadeAndShortSlide(paramInt, paramFloat);
    }
    
    public Object createScale()
    {
      return TransitionHelperApi21.createScale();
    }
    
    public Object getEnterTransition(Window paramWindow)
    {
      return TransitionHelperApi21.getEnterTransition(paramWindow);
    }
    
    public Object getExitTransition(Window paramWindow)
    {
      return TransitionHelperApi21.getExitTransition(paramWindow);
    }
    
    public Object getReenterTransition(Window paramWindow)
    {
      return TransitionHelperApi21.getReenterTransition(paramWindow);
    }
    
    public Object getReturnTransition(Window paramWindow)
    {
      return TransitionHelperApi21.getReturnTransition(paramWindow);
    }
    
    public Object getSharedElementEnterTransition(Window paramWindow)
    {
      return TransitionHelperApi21.getSharedElementEnterTransition(paramWindow);
    }
    
    public Object getSharedElementExitTransition(Window paramWindow)
    {
      return TransitionHelperApi21.getSharedElementExitTransition(paramWindow);
    }
    
    public Object getSharedElementReenterTransition(Window paramWindow)
    {
      return TransitionHelperApi21.getSharedElementReenterTransition(paramWindow);
    }
    
    public Object getSharedElementReturnTransition(Window paramWindow)
    {
      return TransitionHelperApi21.getSharedElementReturnTransition(paramWindow);
    }
    
    public void setEnterTransition(android.app.Fragment paramFragment, Object paramObject)
    {
      TransitionHelperApi21.setEnterTransition(paramFragment, paramObject);
    }
    
    public void setEnterTransition(Window paramWindow, Object paramObject)
    {
      TransitionHelperApi21.setEnterTransition(paramWindow, paramObject);
    }
    
    public void setEpicenterCallback(Object paramObject, TransitionEpicenterCallback paramTransitionEpicenterCallback)
    {
      TransitionHelperApi21.setEpicenterCallback(paramObject, paramTransitionEpicenterCallback);
    }
    
    public void setExitTransition(android.app.Fragment paramFragment, Object paramObject)
    {
      TransitionHelperApi21.setExitTransition(paramFragment, paramObject);
    }
    
    public void setReturnTransition(Window paramWindow, Object paramObject)
    {
      TransitionHelperApi21.setReturnTransition(paramWindow, paramObject);
    }
    
    public void setSharedElementEnterTransition(android.app.Fragment paramFragment, Object paramObject)
    {
      TransitionHelperApi21.setSharedElementEnterTransition(paramFragment, paramObject);
    }
    
    public void setSharedElementEnterTransition(Window paramWindow, Object paramObject)
    {
      TransitionHelperApi21.setSharedElementEnterTransition(paramWindow, paramObject);
    }
    
    public void setSharedElementReturnTransition(Window paramWindow, Object paramObject)
    {
      TransitionHelperApi21.setSharedElementReturnTransition(paramWindow, paramObject);
    }
    
    public void setTransitionGroup(ViewGroup paramViewGroup, boolean paramBoolean)
    {
      TransitionHelperApi21.setTransitionGroup(paramViewGroup, paramBoolean);
    }
  }
  
  static class TransitionHelperKitkatImpl
    extends TransitionHelper.TransitionHelperStubImpl
  {
    public void addTarget(Object paramObject, View paramView)
    {
      TransitionHelperKitkat.addTarget(paramObject, paramView);
    }
    
    public void addTransition(Object paramObject1, Object paramObject2)
    {
      TransitionHelperKitkat.addTransition(paramObject1, paramObject2);
    }
    
    public void addTransitionListener(Object paramObject, TransitionListener paramTransitionListener)
    {
      TransitionHelperKitkat.addTransitionListener(paramObject, paramTransitionListener);
    }
    
    public Object createAutoTransition()
    {
      return TransitionHelperKitkat.createAutoTransition();
    }
    
    public Object createChangeBounds(boolean paramBoolean)
    {
      return TransitionHelperKitkat.createChangeBounds(paramBoolean);
    }
    
    public Object createDefaultInterpolator(Context paramContext)
    {
      return null;
    }
    
    public Object createFadeTransition(int paramInt)
    {
      return TransitionHelperKitkat.createFadeTransition(paramInt);
    }
    
    public Object createScale()
    {
      return TransitionHelperKitkat.createScale();
    }
    
    public Object createScene(ViewGroup paramViewGroup, Runnable paramRunnable)
    {
      return TransitionHelperKitkat.createScene(paramViewGroup, paramRunnable);
    }
    
    public Object createSlide(int paramInt)
    {
      return TransitionHelperKitkat.createSlide(paramInt);
    }
    
    public Object createTransitionSet(boolean paramBoolean)
    {
      return TransitionHelperKitkat.createTransitionSet(paramBoolean);
    }
    
    public void exclude(Object paramObject, int paramInt, boolean paramBoolean)
    {
      TransitionHelperKitkat.exclude(paramObject, paramInt, paramBoolean);
    }
    
    public void exclude(Object paramObject, View paramView, boolean paramBoolean)
    {
      TransitionHelperKitkat.exclude(paramObject, paramView, paramBoolean);
    }
    
    public void excludeChildren(Object paramObject, int paramInt, boolean paramBoolean)
    {
      TransitionHelperKitkat.excludeChildren(paramObject, paramInt, paramBoolean);
    }
    
    public void excludeChildren(Object paramObject, View paramView, boolean paramBoolean)
    {
      TransitionHelperKitkat.excludeChildren(paramObject, paramView, paramBoolean);
    }
    
    public void include(Object paramObject, int paramInt)
    {
      TransitionHelperKitkat.include(paramObject, paramInt);
    }
    
    public void include(Object paramObject, View paramView)
    {
      TransitionHelperKitkat.include(paramObject, paramView);
    }
    
    public Object loadTransition(Context paramContext, int paramInt)
    {
      return TransitionHelperKitkat.loadTransition(paramContext, paramInt);
    }
    
    public void removeTransitionListener(Object paramObject, TransitionListener paramTransitionListener)
    {
      TransitionHelperKitkat.removeTransitionListener(paramObject, paramTransitionListener);
    }
    
    public void runTransition(Object paramObject1, Object paramObject2)
    {
      TransitionHelperKitkat.runTransition(paramObject1, paramObject2);
    }
    
    public void setChangeBoundsDefaultStartDelay(Object paramObject, int paramInt)
    {
      TransitionHelperKitkat.setChangeBoundsDefaultStartDelay(paramObject, paramInt);
    }
    
    public void setChangeBoundsStartDelay(Object paramObject, int paramInt1, int paramInt2)
    {
      TransitionHelperKitkat.setChangeBoundsStartDelay(paramObject, paramInt1, paramInt2);
    }
    
    public void setChangeBoundsStartDelay(Object paramObject, View paramView, int paramInt)
    {
      TransitionHelperKitkat.setChangeBoundsStartDelay(paramObject, paramView, paramInt);
    }
    
    public void setChangeBoundsStartDelay(Object paramObject, String paramString, int paramInt)
    {
      TransitionHelperKitkat.setChangeBoundsStartDelay(paramObject, paramString, paramInt);
    }
    
    public void setDuration(Object paramObject, long paramLong)
    {
      TransitionHelperKitkat.setDuration(paramObject, paramLong);
    }
    
    public void setInterpolator(Object paramObject1, Object paramObject2)
    {
      TransitionHelperKitkat.setInterpolator(paramObject1, paramObject2);
    }
    
    public void setStartDelay(Object paramObject, long paramLong)
    {
      TransitionHelperKitkat.setStartDelay(paramObject, paramLong);
    }
  }
  
  static class TransitionHelperStubImpl
    implements TransitionHelper.TransitionHelperVersionImpl
  {
    public void addSharedElement(android.app.FragmentTransaction paramFragmentTransaction, View paramView, String paramString) {}
    
    public void addTarget(Object paramObject, View paramView) {}
    
    public void addTransition(Object paramObject1, Object paramObject2) {}
    
    public void addTransitionListener(Object paramObject, TransitionListener paramTransitionListener)
    {
      paramObject = (TransitionStub)paramObject;
      if (((TransitionStub)paramObject).mTransitionListeners == null) {
        ((TransitionStub)paramObject).mTransitionListeners = new ArrayList();
      }
      ((TransitionStub)paramObject).mTransitionListeners.add(paramTransitionListener);
    }
    
    public void beginDelayedTransition(ViewGroup paramViewGroup, Object paramObject) {}
    
    public Object createAutoTransition()
    {
      return new TransitionStub();
    }
    
    public Object createChangeBounds(boolean paramBoolean)
    {
      return new TransitionStub();
    }
    
    public Object createChangeTransform()
    {
      return new TransitionStub();
    }
    
    public Object createDefaultInterpolator(Context paramContext)
    {
      return null;
    }
    
    public Object createFadeAndShortSlide(int paramInt)
    {
      return new TransitionStub();
    }
    
    public Object createFadeAndShortSlide(int paramInt, float paramFloat)
    {
      return new TransitionStub();
    }
    
    public Object createFadeTransition(int paramInt)
    {
      return new TransitionStub();
    }
    
    public Object createScale()
    {
      return new TransitionStub();
    }
    
    public Object createScene(ViewGroup paramViewGroup, Runnable paramRunnable)
    {
      return paramRunnable;
    }
    
    public Object createSlide(int paramInt)
    {
      return new TransitionStub();
    }
    
    public Object createTransitionSet(boolean paramBoolean)
    {
      return new TransitionStub();
    }
    
    public void exclude(Object paramObject, int paramInt, boolean paramBoolean) {}
    
    public void exclude(Object paramObject, View paramView, boolean paramBoolean) {}
    
    public void excludeChildren(Object paramObject, int paramInt, boolean paramBoolean) {}
    
    public void excludeChildren(Object paramObject, View paramView, boolean paramBoolean) {}
    
    public Object getEnterTransition(Window paramWindow)
    {
      return null;
    }
    
    public Object getExitTransition(Window paramWindow)
    {
      return null;
    }
    
    public Object getReenterTransition(Window paramWindow)
    {
      return null;
    }
    
    public Object getReturnTransition(Window paramWindow)
    {
      return null;
    }
    
    public Object getSharedElementEnterTransition(Window paramWindow)
    {
      return null;
    }
    
    public Object getSharedElementExitTransition(Window paramWindow)
    {
      return null;
    }
    
    public Object getSharedElementReenterTransition(Window paramWindow)
    {
      return null;
    }
    
    public Object getSharedElementReturnTransition(Window paramWindow)
    {
      return null;
    }
    
    public void include(Object paramObject, int paramInt) {}
    
    public void include(Object paramObject, View paramView) {}
    
    public Object loadTransition(Context paramContext, int paramInt)
    {
      return new TransitionStub();
    }
    
    public void removeTransitionListener(Object paramObject, TransitionListener paramTransitionListener)
    {
      paramObject = (TransitionStub)paramObject;
      if (((TransitionStub)paramObject).mTransitionListeners != null) {
        ((TransitionStub)paramObject).mTransitionListeners.remove(paramTransitionListener);
      }
    }
    
    public void runTransition(Object paramObject1, Object paramObject2)
    {
      TransitionStub localTransitionStub = (TransitionStub)paramObject2;
      int i;
      int j;
      if ((localTransitionStub != null) && (localTransitionStub.mTransitionListeners != null))
      {
        i = 0;
        j = localTransitionStub.mTransitionListeners.size();
        while (i < j)
        {
          ((TransitionListener)localTransitionStub.mTransitionListeners.get(i)).onTransitionStart(paramObject2);
          i += 1;
        }
      }
      paramObject1 = (Runnable)paramObject1;
      if (paramObject1 != null) {
        ((Runnable)paramObject1).run();
      }
      if ((localTransitionStub != null) && (localTransitionStub.mTransitionListeners != null))
      {
        i = 0;
        j = localTransitionStub.mTransitionListeners.size();
        while (i < j)
        {
          ((TransitionListener)localTransitionStub.mTransitionListeners.get(i)).onTransitionEnd(paramObject2);
          i += 1;
        }
      }
    }
    
    public void setChangeBoundsDefaultStartDelay(Object paramObject, int paramInt) {}
    
    public void setChangeBoundsStartDelay(Object paramObject, int paramInt1, int paramInt2) {}
    
    public void setChangeBoundsStartDelay(Object paramObject, View paramView, int paramInt) {}
    
    public void setChangeBoundsStartDelay(Object paramObject, String paramString, int paramInt) {}
    
    public void setDuration(Object paramObject, long paramLong) {}
    
    public void setEnterTransition(android.app.Fragment paramFragment, Object paramObject) {}
    
    public void setEnterTransition(Window paramWindow, Object paramObject) {}
    
    public void setEpicenterCallback(Object paramObject, TransitionEpicenterCallback paramTransitionEpicenterCallback) {}
    
    public void setExitTransition(android.app.Fragment paramFragment, Object paramObject) {}
    
    public void setInterpolator(Object paramObject1, Object paramObject2) {}
    
    public void setReturnTransition(Window paramWindow, Object paramObject) {}
    
    public void setSharedElementEnterTransition(android.app.Fragment paramFragment, Object paramObject) {}
    
    public void setSharedElementEnterTransition(Window paramWindow, Object paramObject) {}
    
    public void setSharedElementReturnTransition(Window paramWindow, Object paramObject) {}
    
    public void setStartDelay(Object paramObject, long paramLong) {}
    
    public void setTransitionGroup(ViewGroup paramViewGroup, boolean paramBoolean) {}
    
    private static class TransitionStub
    {
      ArrayList<TransitionListener> mTransitionListeners;
    }
  }
  
  static abstract interface TransitionHelperVersionImpl
  {
    public abstract void addSharedElement(android.app.FragmentTransaction paramFragmentTransaction, View paramView, String paramString);
    
    public abstract void addTarget(Object paramObject, View paramView);
    
    public abstract void addTransition(Object paramObject1, Object paramObject2);
    
    public abstract void addTransitionListener(Object paramObject, TransitionListener paramTransitionListener);
    
    public abstract void beginDelayedTransition(ViewGroup paramViewGroup, Object paramObject);
    
    public abstract Object createAutoTransition();
    
    public abstract Object createChangeBounds(boolean paramBoolean);
    
    public abstract Object createChangeTransform();
    
    public abstract Object createDefaultInterpolator(Context paramContext);
    
    public abstract Object createFadeAndShortSlide(int paramInt);
    
    public abstract Object createFadeAndShortSlide(int paramInt, float paramFloat);
    
    public abstract Object createFadeTransition(int paramInt);
    
    public abstract Object createScale();
    
    public abstract Object createScene(ViewGroup paramViewGroup, Runnable paramRunnable);
    
    public abstract Object createSlide(int paramInt);
    
    public abstract Object createTransitionSet(boolean paramBoolean);
    
    public abstract void exclude(Object paramObject, int paramInt, boolean paramBoolean);
    
    public abstract void exclude(Object paramObject, View paramView, boolean paramBoolean);
    
    public abstract void excludeChildren(Object paramObject, int paramInt, boolean paramBoolean);
    
    public abstract void excludeChildren(Object paramObject, View paramView, boolean paramBoolean);
    
    public abstract Object getEnterTransition(Window paramWindow);
    
    public abstract Object getExitTransition(Window paramWindow);
    
    public abstract Object getReenterTransition(Window paramWindow);
    
    public abstract Object getReturnTransition(Window paramWindow);
    
    public abstract Object getSharedElementEnterTransition(Window paramWindow);
    
    public abstract Object getSharedElementExitTransition(Window paramWindow);
    
    public abstract Object getSharedElementReenterTransition(Window paramWindow);
    
    public abstract Object getSharedElementReturnTransition(Window paramWindow);
    
    public abstract void include(Object paramObject, int paramInt);
    
    public abstract void include(Object paramObject, View paramView);
    
    public abstract Object loadTransition(Context paramContext, int paramInt);
    
    public abstract void removeTransitionListener(Object paramObject, TransitionListener paramTransitionListener);
    
    public abstract void runTransition(Object paramObject1, Object paramObject2);
    
    public abstract void setChangeBoundsDefaultStartDelay(Object paramObject, int paramInt);
    
    public abstract void setChangeBoundsStartDelay(Object paramObject, int paramInt1, int paramInt2);
    
    public abstract void setChangeBoundsStartDelay(Object paramObject, View paramView, int paramInt);
    
    public abstract void setChangeBoundsStartDelay(Object paramObject, String paramString, int paramInt);
    
    public abstract void setDuration(Object paramObject, long paramLong);
    
    public abstract void setEnterTransition(android.app.Fragment paramFragment, Object paramObject);
    
    public abstract void setEnterTransition(Window paramWindow, Object paramObject);
    
    public abstract void setEpicenterCallback(Object paramObject, TransitionEpicenterCallback paramTransitionEpicenterCallback);
    
    public abstract void setExitTransition(android.app.Fragment paramFragment, Object paramObject);
    
    public abstract void setInterpolator(Object paramObject1, Object paramObject2);
    
    public abstract void setReturnTransition(Window paramWindow, Object paramObject);
    
    public abstract void setSharedElementEnterTransition(android.app.Fragment paramFragment, Object paramObject);
    
    public abstract void setSharedElementEnterTransition(Window paramWindow, Object paramObject);
    
    public abstract void setSharedElementReturnTransition(Window paramWindow, Object paramObject);
    
    public abstract void setStartDelay(Object paramObject, long paramLong);
    
    public abstract void setTransitionGroup(ViewGroup paramViewGroup, boolean paramBoolean);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/transition/TransitionHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */