package com.bumptech.glide;

public final class GenericTransitionOptions<TranscodeType>
  extends TransitionOptions<GenericTransitionOptions<TranscodeType>, TranscodeType>
{
  public static <TranscodeType> GenericTransitionOptions<TranscodeType> withNoTransition()
  {
    return (GenericTransitionOptions)new GenericTransitionOptions().dontTransition();
  }
}


/* Location:              ~/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/bumptech/glide/GenericTransitionOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */