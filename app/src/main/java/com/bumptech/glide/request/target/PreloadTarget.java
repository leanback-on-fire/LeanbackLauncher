package com.bumptech.glide.request.target;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.transition.Transition;

public final class PreloadTarget<Z>
  extends SimpleTarget<Z>
{
  private final RequestManager requestManager;
  
  private PreloadTarget(RequestManager paramRequestManager, int paramInt1, int paramInt2)
  {
    super(paramInt1, paramInt2);
    this.requestManager = paramRequestManager;
  }
  
  public static <Z> PreloadTarget<Z> obtain(RequestManager paramRequestManager, int paramInt1, int paramInt2)
  {
    return new PreloadTarget(paramRequestManager, paramInt1, paramInt2);
  }
  
  public void onResourceReady(Z paramZ, Transition<? super Z> paramTransition)
  {
    this.requestManager.clear(this);
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/request/target/PreloadTarget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */