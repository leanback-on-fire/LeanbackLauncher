package com.google.android.tvlauncher.util;

import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;

public class ScaleAndExpandFocusHandler
  extends ScaleFocusHandler
{
  private static final double EPSILON = 0.01D;
  private double mFocusedAspectRatio;
  private double mUnfocusedAspectRatio;
  
  public ScaleAndExpandFocusHandler(int paramInt1, float paramFloat1, float paramFloat2, int paramInt2)
  {
    super(paramInt1, paramFloat1, paramFloat2, paramInt2);
  }
  
  private void updateExpandedState(boolean paramBoolean)
  {
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)this.mView.getLayoutParams();
    if (paramBoolean) {}
    for (double d1 = this.mFocusedAspectRatio;; d1 = this.mUnfocusedAspectRatio)
    {
      double d2 = 0.0D;
      if (localMarginLayoutParams.height != 0) {
        d2 = localMarginLayoutParams.width / localMarginLayoutParams.height;
      }
      if (Math.abs(d1 - d2) > 0.01D)
      {
        localMarginLayoutParams.width = ((int)Math.round(localMarginLayoutParams.height * d1));
        this.mView.setLayoutParams(localMarginLayoutParams);
      }
      return;
    }
  }
  
  protected void animateFocusedState(boolean paramBoolean)
  {
    updateExpandedState(paramBoolean);
    super.animateFocusedState(paramBoolean);
  }
  
  public double getFocusedAspectRatio()
  {
    return this.mFocusedAspectRatio;
  }
  
  public double getUnfocusedAspectRatio()
  {
    return this.mUnfocusedAspectRatio;
  }
  
  public void resetFocusedState()
  {
    updateExpandedState(this.mView.isFocused());
    super.resetFocusedState();
  }
  
  public void setFocusedAspectRatio(double paramDouble)
  {
    this.mFocusedAspectRatio = paramDouble;
  }
  
  public void setUnfocusedAspectRatio(double paramDouble)
  {
    this.mUnfocusedAspectRatio = paramDouble;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/com/google/android/tvlauncher/util/ScaleAndExpandFocusHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */