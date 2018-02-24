package android.support.v17.leanback.widget;

import android.animation.Animator;
import android.support.annotation.NonNull;
import java.util.List;

public abstract interface FragmentAnimationProvider
{
  public abstract void onImeAppearing(@NonNull List<Animator> paramList);
  
  public abstract void onImeDisappearing(@NonNull List<Animator> paramList);
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/FragmentAnimationProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */