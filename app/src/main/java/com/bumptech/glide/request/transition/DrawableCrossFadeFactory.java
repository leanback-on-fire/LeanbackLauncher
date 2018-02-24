package com.bumptech.glide.request.transition;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import com.bumptech.glide.load.DataSource;

public class DrawableCrossFadeFactory
  implements TransitionFactory<Drawable>
{
  private final int duration;
  private DrawableCrossFadeTransition firstResourceTransition;
  private final boolean isCrossFadeEnabled;
  private DrawableCrossFadeTransition secondResourceTransition;
  private final ViewAnimationFactory<Drawable> viewAnimationFactory;
  
  protected DrawableCrossFadeFactory(ViewAnimationFactory<Drawable> paramViewAnimationFactory, int paramInt, boolean paramBoolean)
  {
    this.viewAnimationFactory = paramViewAnimationFactory;
    this.duration = paramInt;
    this.isCrossFadeEnabled = paramBoolean;
  }
  
  private DrawableCrossFadeTransition buildTransition(DataSource paramDataSource, boolean paramBoolean)
  {
    return new DrawableCrossFadeTransition(this.viewAnimationFactory.build(paramDataSource, paramBoolean), this.duration, this.isCrossFadeEnabled);
  }
  
  private Transition<Drawable> getFirstResourceTransition(DataSource paramDataSource)
  {
    if (this.firstResourceTransition == null) {
      this.firstResourceTransition = buildTransition(paramDataSource, true);
    }
    return this.firstResourceTransition;
  }
  
  private Transition<Drawable> getSecondResourceTransition(DataSource paramDataSource)
  {
    if (this.secondResourceTransition == null) {
      this.secondResourceTransition = buildTransition(paramDataSource, false);
    }
    return this.secondResourceTransition;
  }
  
  public Transition<Drawable> build(DataSource paramDataSource, boolean paramBoolean)
  {
    if (paramDataSource == DataSource.MEMORY_CACHE) {
      return NoTransition.get();
    }
    if (paramBoolean) {
      return getFirstResourceTransition(paramDataSource);
    }
    return getSecondResourceTransition(paramDataSource);
  }
  
  public static class Builder
  {
    private static final int DEFAULT_DURATION_MS = 300;
    private int durationMillis;
    private ViewAnimationFactory<Drawable> factory;
    private boolean isCrossFadeEnabled;
    
    public Builder()
    {
      this(300);
    }
    
    public Builder(int paramInt)
    {
      this.durationMillis = paramInt;
      this.factory = new ViewAnimationFactory(new DrawableCrossFadeFactory.DefaultViewTransitionAnimationFactory(paramInt));
    }
    
    public DrawableCrossFadeFactory build()
    {
      return new DrawableCrossFadeFactory(this.factory, this.durationMillis, this.isCrossFadeEnabled);
    }
    
    public Builder setCrossFadeEnabled(boolean paramBoolean)
    {
      this.isCrossFadeEnabled = paramBoolean;
      return this;
    }
    
    public Builder setDefaultAnimation(Animation paramAnimation)
    {
      return setDefaultAnimationFactory(new ViewAnimationFactory(paramAnimation));
    }
    
    public Builder setDefaultAnimationFactory(ViewAnimationFactory<Drawable> paramViewAnimationFactory)
    {
      this.factory = paramViewAnimationFactory;
      return this;
    }
    
    public Builder setDefaultAnimationId(int paramInt)
    {
      return setDefaultAnimationFactory(new ViewAnimationFactory(paramInt));
    }
  }
  
  private static final class DefaultViewTransitionAnimationFactory
    implements ViewTransition.ViewTransitionAnimationFactory
  {
    private final int durationMillis;
    
    DefaultViewTransitionAnimationFactory(int paramInt)
    {
      this.durationMillis = paramInt;
    }
    
    public Animation build(Context paramContext)
    {
      paramContext = new AlphaAnimation(0.0F, 1.0F);
      paramContext.setDuration(this.durationMillis);
      return paramContext;
    }
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/request/transition/DrawableCrossFadeFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */