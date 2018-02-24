package android.support.v17.leanback.animation;

import android.animation.TimeInterpolator;
import android.support.annotation.RestrictTo;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class LogDecelerateInterpolator
  implements TimeInterpolator
{
  int mBase;
  int mDrift;
  final float mLogScale;
  
  public LogDecelerateInterpolator(int paramInt1, int paramInt2)
  {
    this.mBase = paramInt1;
    this.mDrift = paramInt2;
    this.mLogScale = (1.0F / computeLog(1.0F, this.mBase, this.mDrift));
  }
  
  static float computeLog(float paramFloat, int paramInt1, int paramInt2)
  {
    return (float)-Math.pow(paramInt1, -paramFloat) + 1.0F + paramInt2 * paramFloat;
  }
  
  public float getInterpolation(float paramFloat)
  {
    return computeLog(paramFloat, this.mBase, this.mDrift) * this.mLogScale;
  }
}


/* Location:              /home/evan/Downloads/fugu-opr2.170623.027-factory-d4be396e/fugu-opr2.170623.027/image-fugu-opr2.170623.027/TVLauncher/TVLauncher/TVLauncher-dex2jar.jar!/android/support/v17/leanback/animation/LogDecelerateInterpolator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */