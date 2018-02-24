package com.bumptech.glide.request.transition;

import android.view.View;

public class ViewPropertyTransition<R>
  implements Transition<R>
{
  private final Animator animator;
  
  public ViewPropertyTransition(Animator paramAnimator)
  {
    this.animator = paramAnimator;
  }
  
  public boolean transition(R paramR, Transition.ViewAdapter paramViewAdapter)
  {
    if (paramViewAdapter.getView() != null) {
      this.animator.animate(paramViewAdapter.getView());
    }
    return false;
  }
  
  public static abstract interface Animator
  {
    public abstract void animate(View paramView);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/request/transition/ViewPropertyTransition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */