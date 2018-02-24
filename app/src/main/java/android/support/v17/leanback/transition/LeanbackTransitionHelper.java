package android.support.v17.leanback.transition;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.annotation.RestrictTo;
import android.support.v17.leanback.R.transition;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class LeanbackTransitionHelper
{
  static LeanbackTransitionHelperVersion sImpl = new LeanbackTransitionHelperDefault();
  
  static
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      sImpl = new LeanbackTransitionHelperDefault();
      return;
    }
    if (Build.VERSION.SDK_INT >= 19)
    {
      sImpl = new LeanbackTransitionHelperKitKatImpl();
      return;
    }
  }
  
  public static Object loadTitleInTransition(Context paramContext)
  {
    return sImpl.loadTitleInTransition(paramContext);
  }
  
  public static Object loadTitleOutTransition(Context paramContext)
  {
    return sImpl.loadTitleOutTransition(paramContext);
  }
  
  static class LeanbackTransitionHelperDefault
    implements LeanbackTransitionHelper.LeanbackTransitionHelperVersion
  {
    public Object loadTitleInTransition(Context paramContext)
    {
      return TransitionHelper.loadTransition(paramContext, R.transition.lb_title_in);
    }
    
    public Object loadTitleOutTransition(Context paramContext)
    {
      return TransitionHelper.loadTransition(paramContext, R.transition.lb_title_out);
    }
  }
  
  static class LeanbackTransitionHelperKitKatImpl
    implements LeanbackTransitionHelper.LeanbackTransitionHelperVersion
  {
    public Object loadTitleInTransition(Context paramContext)
    {
      return LeanbackTransitionHelperKitKat.loadTitleInTransition(paramContext);
    }
    
    public Object loadTitleOutTransition(Context paramContext)
    {
      return LeanbackTransitionHelperKitKat.loadTitleOutTransition(paramContext);
    }
  }
  
  static abstract interface LeanbackTransitionHelperVersion
  {
    public abstract Object loadTitleInTransition(Context paramContext);
    
    public abstract Object loadTitleOutTransition(Context paramContext);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/transition/LeanbackTransitionHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */