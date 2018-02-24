package com.bumptech.glide;

import com.bumptech.glide.request.transition.NoTransition;
import com.bumptech.glide.request.transition.TransitionFactory;
import com.bumptech.glide.request.transition.ViewAnimationFactory;
import com.bumptech.glide.request.transition.ViewPropertyAnimationFactory;
import com.bumptech.glide.request.transition.ViewPropertyTransition.Animator;
import com.bumptech.glide.util.Preconditions;

public abstract class TransitionOptions<CHILD extends TransitionOptions<CHILD, TranscodeType>, TranscodeType>
  implements Cloneable
{
  private TransitionFactory<? super TranscodeType> transitionFactory = NoTransition.getFactory();
  
  private CHILD self()
  {
    return this;
  }
  
  protected final CHILD clone()
  {
    try
    {
      TransitionOptions localTransitionOptions = (TransitionOptions)super.clone();
      return localTransitionOptions;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new RuntimeException(localCloneNotSupportedException);
    }
  }
  
  public final CHILD dontTransition()
  {
    return transition(NoTransition.getFactory());
  }
  
  final TransitionFactory<? super TranscodeType> getTransitionFactory()
  {
    return this.transitionFactory;
  }
  
  public final CHILD transition(int paramInt)
  {
    return transition(new ViewAnimationFactory(paramInt));
  }
  
  public final CHILD transition(TransitionFactory<? super TranscodeType> paramTransitionFactory)
  {
    this.transitionFactory = ((TransitionFactory)Preconditions.checkNotNull(paramTransitionFactory));
    return self();
  }
  
  public final CHILD transition(ViewPropertyTransition.Animator paramAnimator)
  {
    return transition(new ViewPropertyAnimationFactory(paramAnimator));
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/TransitionOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */