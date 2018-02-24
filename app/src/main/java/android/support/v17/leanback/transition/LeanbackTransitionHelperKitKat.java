package android.support.v17.leanback.transition;

import android.content.Context;
import android.support.annotation.RequiresApi;
import android.support.v17.leanback.R.animator;
import android.support.v17.leanback.R.id;
import android.view.animation.AnimationUtils;

@RequiresApi(19)
class LeanbackTransitionHelperKitKat
{
  public static Object loadTitleInTransition(Context paramContext)
  {
    SlideKitkat localSlideKitkat = new SlideKitkat();
    localSlideKitkat.setSlideEdge(48);
    localSlideKitkat.setInterpolator(AnimationUtils.loadInterpolator(paramContext, 17432582));
    localSlideKitkat.addTarget(R.id.browse_title_group);
    return localSlideKitkat;
  }
  
  public static Object loadTitleOutTransition(Context paramContext)
  {
    SlideKitkat localSlideKitkat = new SlideKitkat();
    localSlideKitkat.setSlideEdge(48);
    localSlideKitkat.setInterpolator(AnimationUtils.loadInterpolator(paramContext, R.animator.lb_decelerator_4));
    localSlideKitkat.addTarget(R.id.browse_title_group);
    return localSlideKitkat;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/transition/LeanbackTransitionHelperKitKat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */