package android.support.v17.leanback.widget;

import android.support.annotation.RestrictTo;
import android.view.View;

abstract interface FocusHighlightHandler
{
  public abstract void onInitializeView(View paramView);
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public abstract void onItemFocused(View paramView, boolean paramBoolean);
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/widget/FocusHighlightHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */