package com.bumptech.glide.request.transition;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;

public class DrawableCrossFadeTransition
  implements Transition<Drawable>
{
  private final Transition<Drawable> defaultAnimation;
  private final int duration;
  private final boolean isCrossFadeEnabled;
  
  public DrawableCrossFadeTransition(Transition<Drawable> paramTransition, int paramInt, boolean paramBoolean)
  {
    this.defaultAnimation = paramTransition;
    this.duration = paramInt;
    this.isCrossFadeEnabled = paramBoolean;
  }
  
  public boolean transition(Drawable paramDrawable, Transition.ViewAdapter paramViewAdapter)
  {
    Drawable localDrawable = paramViewAdapter.getCurrentDrawable();
    if (localDrawable != null)
    {
      paramDrawable = new TransitionDrawable(new Drawable[] { localDrawable, paramDrawable });
      paramDrawable.setCrossFadeEnabled(this.isCrossFadeEnabled);
      paramDrawable.startTransition(this.duration);
      paramViewAdapter.setDrawable(paramDrawable);
      return true;
    }
    this.defaultAnimation.transition(paramDrawable, paramViewAdapter);
    return false;
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/request/transition/DrawableCrossFadeTransition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */