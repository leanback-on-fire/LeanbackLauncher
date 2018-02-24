package com.bumptech.glide.request.transition;

import com.bumptech.glide.load.DataSource;

public class NoTransition<R>
  implements Transition<R>
{
  private static final NoTransition<?> NO_ANIMATION = new NoTransition();
  private static final TransitionFactory<?> NO_ANIMATION_FACTORY = new NoAnimationFactory();
  
  public static <R> Transition<R> get()
  {
    return NO_ANIMATION;
  }
  
  public static <R> TransitionFactory<R> getFactory()
  {
    return NO_ANIMATION_FACTORY;
  }
  
  public boolean transition(Object paramObject, Transition.ViewAdapter paramViewAdapter)
  {
    return false;
  }
  
  public static class NoAnimationFactory<R>
    implements TransitionFactory<R>
  {
    public Transition<R> build(DataSource paramDataSource, boolean paramBoolean)
    {
      return NoTransition.NO_ANIMATION;
    }
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/request/transition/NoTransition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */