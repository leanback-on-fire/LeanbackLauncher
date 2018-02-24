package android.support.v4.view;

import android.support.annotation.Nullable;

public abstract interface NestedScrollingChild
{
  public abstract boolean dispatchNestedFling(float paramFloat1, float paramFloat2, boolean paramBoolean);
  
  public abstract boolean dispatchNestedPreFling(float paramFloat1, float paramFloat2);
  
  public abstract boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, @Nullable int[] paramArrayOfInt1, @Nullable int[] paramArrayOfInt2);
  
  public abstract boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, @Nullable int[] paramArrayOfInt);
  
  public abstract boolean hasNestedScrollingParent();
  
  public abstract boolean isNestedScrollingEnabled();
  
  public abstract void setNestedScrollingEnabled(boolean paramBoolean);
  
  public abstract boolean startNestedScroll(int paramInt);
  
  public abstract void stopNestedScroll();
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v4/view/NestedScrollingChild.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */