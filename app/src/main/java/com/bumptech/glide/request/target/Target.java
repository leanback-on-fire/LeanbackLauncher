package com.bumptech.glide.request.target;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import com.bumptech.glide.manager.LifecycleListener;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.transition.Transition;

public abstract interface Target<R>
  extends LifecycleListener
{
  public static final int SIZE_ORIGINAL = Integer.MIN_VALUE;
  
  @Nullable
  public abstract Request getRequest();
  
  public abstract void getSize(SizeReadyCallback paramSizeReadyCallback);
  
  public abstract void onLoadCleared(@Nullable Drawable paramDrawable);
  
  public abstract void onLoadFailed(@Nullable Drawable paramDrawable);
  
  public abstract void onLoadStarted(@Nullable Drawable paramDrawable);
  
  public abstract void onResourceReady(R paramR, Transition<? super R> paramTransition);
  
  public abstract void setRequest(@Nullable Request paramRequest);
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/request/target/Target.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */