package com.bumptech.glide.load.resource.drawable;

import android.graphics.drawable.Drawable;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory.Builder;

public final class DrawableTransitionOptions
  extends TransitionOptions<DrawableTransitionOptions, Drawable>
{
  public static DrawableTransitionOptions withCrossFade()
  {
    return new DrawableTransitionOptions().crossFade();
  }
  
  public static DrawableTransitionOptions withCrossFade(int paramInt)
  {
    return new DrawableTransitionOptions().crossFade(paramInt);
  }
  
  public static DrawableTransitionOptions withCrossFade(int paramInt1, int paramInt2)
  {
    return new DrawableTransitionOptions().crossFade(paramInt1, paramInt2);
  }
  
  public static DrawableTransitionOptions withCrossFade(DrawableCrossFadeFactory.Builder paramBuilder)
  {
    return new DrawableTransitionOptions().crossFade(paramBuilder);
  }
  
  public static DrawableTransitionOptions withCrossFade(DrawableCrossFadeFactory paramDrawableCrossFadeFactory)
  {
    return new DrawableTransitionOptions().crossFade(paramDrawableCrossFadeFactory);
  }
  
  public DrawableTransitionOptions crossFade()
  {
    return crossFade(new DrawableCrossFadeFactory.Builder());
  }
  
  public DrawableTransitionOptions crossFade(int paramInt)
  {
    return crossFade(new DrawableCrossFadeFactory.Builder(paramInt));
  }
  
  public DrawableTransitionOptions crossFade(int paramInt1, int paramInt2)
  {
    return crossFade(new DrawableCrossFadeFactory.Builder(paramInt2).setDefaultAnimationId(paramInt1));
  }
  
  public DrawableTransitionOptions crossFade(DrawableCrossFadeFactory.Builder paramBuilder)
  {
    return crossFade(paramBuilder.build());
  }
  
  public DrawableTransitionOptions crossFade(DrawableCrossFadeFactory paramDrawableCrossFadeFactory)
  {
    return (DrawableTransitionOptions)transition(paramDrawableCrossFadeFactory);
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/load/resource/drawable/DrawableTransitionOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */